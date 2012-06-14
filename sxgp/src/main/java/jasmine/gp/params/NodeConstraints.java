package jasmine.gp.params;


import jasmine.gp.tree.Node;

import java.lang.reflect.Constructor;
import java.io.Serializable;
import java.util.Vector;

/**
 * NodeParams is a metadata object which allows nodes to be stored. It also
 * allows us to instantiate new nodes using this classes getInstance() method.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 * @version 1.1 Handles multiple constructors properly. 17th April 2008.
 */
public class NodeConstraints implements Serializable {

    public static final String[] typeNames = new String[]{"Function", "Terminal", "ERC"};

    public static final int ANY = -1;
    public static final int FUNCTION = 0;
    public static final int TERMINAL = 1;
    public static final int ERC = 2;

    public static final long UNINITIALISED = -1;

    public static final int VOID = 1;

    public static final int NUMBER = 2;
    public static final int FLOATING_POINT_NUMBER = 20;
    public static final int INTEGER = 21;
    public static final int PERCENTAGE = 22;
    public static final int RANGE255 = 23;

    public static final int BOOLEAN = 3;
    public static final int SUBSTATEMENT = 4;
    public static final int FEATURE = 5;
    public static final int POINTX = 6;
    public static final int POINTY = 7;
    public static final int VECTOR = 8;
    public static final int OBJECT = 9;
    public static final int PARAMETER = 10; // A parameter for a function - distinct from a number

    /**
     * The classname of the node object
     */
    protected String classname;

    /**
     * The kind of value the node returns
     */
    protected int[] returnTypes;

    /**
     * How many children does the node have
     */
    protected int childCount;

    /**
     * Whether the node can be used by tree builders or not
     */
    protected boolean enabled = true;

    /**
     * What type of node is it: Function, Terminal or Constant (ERC)?
     */
    protected int type;

    /**
     * The unique identifier for the node
     */
    protected long uniqueID = UNINITIALISED;

    /**
     * Arguments, for if the object takes arguments in its constructor. This is sometimes
     * handy when you want a single node type to do various different things but you don't want
     * to have to copy a separate class file for each one. By default this value is null, so the getInstance
     * method assumes the object has a blank constructor, but if you need to pass args, the node object
     * will have to override its getConstructorArgs() method and insert the ones we need.
     */
    protected Object[] args = null;

    public static final double DEFAULT_FITNESS = 1;

    /**
     * Allow the terminal a fitness so we can know which ones are better than others.
     */
    protected double fitness = DEFAULT_FITNESS;

    public Vector<NodeCache> cacheMemberships;

    /**
     * Initialises the Node Params object.
     * @param classname The classname of the node (allows it to be instantiated programmatically)
     * @param returntype The type this node returns (for strong typing)
     * @param rangeType The range type this node returns (terminals only)
     * @param childCount How many children this node should have.
     */
    public NodeConstraints(String classname, int[] returntypes, int childCount, int type) {
        this.classname = classname;
        this.returnTypes = returntypes;
        this.childCount = childCount;
        this.enabled = true;
        this.type = type;
        cacheMemberships = new Vector<NodeCache>();
    }

    public boolean matches(int returnType) {
        for (int i = 0; i < returnTypes.length; i++) {
            if (returnTypes[i] == returnType) return true;
        }
        return false;
    }

    public int[] getReturnTypes() {
        return returnTypes;
    }

    /**
     * Allow the terminal a fitness so we can know which ones are better than others.
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Allow the terminal a fitness so we can know which ones are better than others.
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * What type of node is it: Function, Terminal or Constant (ERC)?
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the arguments that would be used to instantiate this object if its constructor has more than
     * zero arguments. (used in the getInstance() method)
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * Returns whether the node is enabled - that is whether the tree builder is actually
     * allowed to use it.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the Node Params object may actually be used by the GP system tree builder.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets a unique ID for this Node Params object which is passed on to the nodes
     * that it creates. This allows each node to be able to refer back to the node params
     * object, which is useful for some operations, for instance where you want to count
     * how many times a node params object is used in any particular individual.
     * @param uniqueID The uniqueId to be assigned to this node params object.
     */
    public void registerUniqueID(long uniqueID) {
        this.uniqueID = uniqueID;
    }

    /**
     * Gets the ID associated with this node constraints object.
     */
    public long getID() {
        if (uniqueID == UNINITIALISED) throw new RuntimeException("ID is not initialised for node params");
        return uniqueID;
    }

    private Class c = null;
    private Constructor con = null;

    //POEY comment: I don't know what this function is for ???
    public Node getInstance() {
        try {

            if (c == null) {           	
                c = Class.forName(classname);
            }

            Node n;

            if (args == null)  {
                n = (Node) c.newInstance();
            } else {
            	
                if (con == null) {
                    Constructor[] constructors = c.getConstructors();
                    //POEY comment: constructors.length == 1
                    if (constructors.length == 1) { 
                        // use the only constructor available
                        con = constructors[0];

                    } else { 
                        // there are multiple constructors - find the appropriate one
                        loop1: for (int i = 0; i < constructors.length; i++) {
                            Constructor constructor = constructors[i];
                            if (constructor.getParameterTypes().length == args.length) {
                                for (int j = 0; j < constructor.getParameterTypes().length; j++) {
                                    Class cl = constructor.getParameterTypes()[j];
                                    if (cl.equals(args[j].getClass())) continue loop1;
                                }
                                con = constructor;
                            }
                        }

                    }

                }

                // wrong constructor
                n = (Node) con.newInstance(args);
            }

            n.setID(uniqueID);
            return n;
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("NodeParams.getInstance(): Could not instantiate node - : " + classname + " not found.");
        } catch (InstantiationException e) {
            throw new RuntimeException("NodeParams.getInstance(): Could not instantiate class - is it abstract?: " + classname);
        } catch (Exception e) {
            throw new RuntimeException("NodeParams.getInstance(): Could not instantiate class " + classname + ": " + e.toString());
        }
    }

    /**
     * Converts return types into english
     */
    public static String returnTypeToString(int nodeType) {
        switch (nodeType) {
            case VOID: return "void";
            case NUMBER: return "number";
            case BOOLEAN: return "boolean";
            case SUBSTATEMENT: return "substatement";
            case FEATURE: return "feature";
            case POINTX: return "pointX";
            case POINTY: return "pointY";
            case VECTOR: return "vector";
            case OBJECT: return "object";
            case PARAMETER: return "parameter";
        }
        return "Unknown node type (" + nodeType + ")";
    }

    public String toString() {
        return classname;
    }

}
