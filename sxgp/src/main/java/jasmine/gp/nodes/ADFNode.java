package jasmine.gp.nodes;

import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.ADFNodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * ADF Nodes behave like an ERC although they execute a tree of their own. An ADF node
 * has a unique ID that is used to identify itself so the performance of each ADF originalNode can
 * be measured.
 *
 * @author Olly Oechsle, University of Essex, Date: 07-Mar-2007
 * @version 1.0
 */
public class ADFNode extends Node {

    /**
     * The ADF node's logic tree
     */
    protected Node tree;

    /**
     * The return type of this ADF node
     */
    protected int[] returnTypes;

    /**
     * Reference to the ADF Node Params object that created this ADF Node
     */
    protected long id;

    /**
     * Constructs the node with its ID, subtree and return type
     * @param id The ID determined by the ADFNodeConstraints object
     * @param tree The parse tree the encompasses the logic of this node
     * @param returnType What kind of value this ADF returns
     */
    public ADFNode(long id, Node tree, int[] returnTypes) {
        // I have no children
        super(0);
        this.id = id;
        this.tree = tree;
        this.returnTypes = returnTypes;
    }

    public String getShortName() {
        return "ADF";
    }

    /**
     * Returns an ID which can be set for the ADF node. Allows us to figure
     * out which ADFNodeConstraints object created this ADF node and reward or p
     * punish it in coevolutionary learning.
     */
    public long getID() {
        return id;
    }

    public double execute(DataStack data) {
        return tree.execute(data);
    }

    public int[] getReturnTypes() {
        return returnTypes;
    }

    public String toJava() {
        return "method" + id + "()";
    }                   

    public int getChildType(int index) {
        return 0;
    }

    public Node getTree() {
        return tree;
    }

    /**
     * Provides information about this node which is used by the tree builder and allows the me to be copied (via
     * the NodeParams getInstance() method.
     */
    public ADFNodeConstraints createNodeConstraintsObject() {
        return new ADFNodeConstraints(this, getReturnTypes());
    }

    /**
     * Jitters the ERCs in the ADF
     */
    public void jitter() {
        jitter(tree);
    }

    /**
     * What the hell does this do?
     */
    private void jitter(Node root) {
        System.out.println("ROOT JITTER");
        if (root instanceof BasicERC) {
            ((BasicERC) root).mutate();
        }
        for (int i = 0; i < root.child.length; i++) {
            Node child = root.child[i];
            jitter(child);
        }
    }

    public String toString() {
        return "ADF: " + getID();
    }

}
