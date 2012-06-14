package jasmine.gp.util;


import jasmine.gp.Individual;
import jasmine.gp.multiclass.PCM;
import jasmine.gp.nodes.ADFNode;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeUtils;

import java.util.Vector;

/**
 * Converts a GP individual, that is a tree of executable nodes into
 * Java code that can be written out as a class. As well as having obvious
 * speed benefits over running a virtualised programm as a tree, it allows
 * individuals to be easily used in applications outside a Genetic Programming
 * context.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class JavaWriter {

    public static boolean PRINT_PCM = true;

    public static final int RETURN_VALUE = 1;
    public static final int STATEMENT = 2;
    public static final int EXPRESSION = 3;

    private static int nameCounter;

    private static int pcmCounter = 0;

    /**
     * Prints the individual out as a java method.
     */
    public static String toJava(Individual ind) {
        return toJava(ind, "public " + returnTypeToJava(ind.getReturnType()) + " eval()");
    }

    /**
     * Converts the individual into a java method. You may optionally set the method name
     */
    public static String toJava(Individual ind, String methodSignature) {

        // assign variable names to each node
        nameCounter = 0;


        StringBuffer buffer = new StringBuffer(1024);

        // if the individual has a pcm, print that too
        for (int t = 0; t < ind.getTreeCount(); t++) {
            PCM pcm = ind.getPCM(t);
            if (pcm != null) {
                pcm.name = "pcm" + pcmCounter + "_";
                pcmCounter++;
                buffer.append("PCM " + pcm.name + t + " = " + pcm.toJava() + ";\n");
            }
        }

        // find any ADFs in the individual
        for (int t = 0; t < ind.getTreeCount(); t++) {
            name(ind.getTree(t));
            Vector<ADFNode> adfNodes = TreeUtils.getADFNodes(ind.getTree(t));
            for (int i = 0; i < adfNodes.size(); i++) {
                ADFNode adfNode = adfNodes.elementAt(i);
                buffer.append("protected ");
                buffer.append(returnTypeToJava(adfNode.getReturnTypes()[0]));
                buffer.append(" method");
                buffer.append(adfNode.getID());
                buffer.append("()");
                buffer.append(" {\n");
                buffer.append(toJava(adfNode.getTree(), RETURN_VALUE, t, null));
                buffer.append("\n}\n\n");
            }

            // print java code
            buffer.append(methodSignature);
            buffer.append(" {\n");
            buffer.append(toJava(ind.getTree(t), RETURN_VALUE, t, ind.getPCM(t)));
            buffer.append("\n}\n\n");

        }

        return buffer.toString();

    }

    /**
     * Prints out an ADF Node as a method
     *
     * @return
     */
    public static String toJava(ADFNode adfNode, String comment) {
        // find any ADFs in the individual
        nameCounter = 0;
        name(adfNode.getTree());
        StringBuffer buffer = new StringBuffer(1024);
        if (comment != null) {
            buffer.append("/**\n");
            buffer.append(" * ");
            buffer.append(comment);
            buffer.append("\n */\n");
        }
        buffer.append("protected ");
        buffer.append(returnTypeToJava(adfNode.getReturnTypes()[0]));
        buffer.append(" method");
        buffer.append(adfNode.getID());
        buffer.append("()");
        buffer.append(" {\n");
        buffer.append(toJava(adfNode.getTree(), RETURN_VALUE, 0, null));
        buffer.append("\n}\n\n");
        return buffer.toString();
    }

    public static String toJava(ADFNode node, int treeIndex) {
        return toJava(node.getTree(), RETURN_VALUE, treeIndex, null);
    }

    public static String getMethodSignature(ADFNode adfNode) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("protected ");
        buffer.append(returnTypeToJava(adfNode.getReturnTypes()[0]));
        buffer.append(" method");
        buffer.append(adfNode.getID());
        buffer.append("()");
        return buffer.toString();
    }

    private static String toJava(Node n, int type, int treeIndex, PCM pcm) {

        StringBuilder buffer = new StringBuilder(1024);

        // print children first
        for (int i = 0; i < n.countChildren(); i++) {
            buffer.append(toJava(n.child[i], type == EXPRESSION ? EXPRESSION : STATEMENT, treeIndex, pcm));
        }

        switch (type) {
            case RETURN_VALUE:
                // insert a return statement
                if (n.getReturnTypes()[0] != NodeConstraints.VOID) {
                    if (n.getReturnTypes()[0] == NodeConstraints.SUBSTATEMENT) {
                        buffer.append("    return (int) (");
                    } else {
                        buffer.append("    return ");
                    }
                }
                if (pcm != null && PRINT_PCM) {
                    buffer.append(pcm.name + treeIndex + ".getClassFromOutput(");
                }
                buffer.append(n.toJava());
                if ((pcm != null & PRINT_PCM) || n.getReturnTypes()[0] == NodeConstraints.SUBSTATEMENT) {
                    buffer.append(")");
                }
                buffer.append(";");
                break;
            case STATEMENT:
                // insert as a statement (var = val)
                if (n.countChildren() > 0) {
                    if (n.getReturnTypes()[0] != NodeConstraints.VOID) {
                        buffer.append("    ");
                        buffer.append(returnTypeToJava(n.getReturnTypes()[0]));
                        buffer.append(' ');
                        if (n.getName() == null) {
                            n.setName("val" + nameCounter);
                            nameCounter++;
                        }
                        buffer.append(n.getName());
                        buffer.append(" = ");
                    }
                    buffer.append(n.toJava());
                    buffer.append(";\n");
                }
                break;
            case EXPRESSION:
                // expression only, without setting a variable
                buffer.append(n.toJava());
        }

        return buffer.toString();

    }

    public static String returnTypeToJava(int returnType) {
        switch (returnType) {
            case NodeConstraints.BOOLEAN:
                return "boolean";
            default:
                return "double";
        }
    }

    private static void name(Node n) {
        n.setName("node" + nameCounter);
        nameCounter++;
        for (int i = 0; i < n.countChildren(); i++) {
            name(n.child[i]);
        }
    }

}
