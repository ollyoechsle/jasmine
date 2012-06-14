package jasmine.gp.tree;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.treebuilders.TreeConstraint;

import java.io.*;
import java.util.Vector;

/**
 * <p>A basic representation of a node which can be built into a tree.</p>
 * <p/>
 * <p>GP uses tree representations to simulate programmatic structures.,</p>
 * <p/>
 * <p>A collection of nodes is assembled into a tree using one of the Tree Builders in SXGP</p>
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public abstract class Node implements Cloneable, Serializable {

    public int shortcut = 0;

    /**
     * How many children the node is expected to have
     */
    protected int numChildren;

    /**
     * An array of the ndoe's children
     */
    public Node[] child;

    /**
     * The parent of this node
     */
    protected Node parent;

    /**
     * The GP <i>individual</i> to which this node belongs.
     */
    public Individual root;

    public int index;

    /**
     * The name of the node
     */
    protected String name;

    /**
     * A debugger object which is used by the Tree Optimiser to
     * make sure this node is efficient. If it isn't it may be removed.
     */
    public transient Debugger debugger = null;

    /**
     * Records the execution order of the node. Useful for some optimisation routines.
     */
    public transient int executionOrder = 0;

    /**
     * Constructs a blank node
     *
     * @param numChildren How many children this node will have
     */
    public Node(int numChildren) {
        this.numChildren = numChildren;
        this.child = new Node[numChildren];
        this.parent = null;
        if (Debugger.optimisationEnabled) {
            this.debugger = new Debugger(this);
        }
    }

    /**
     * Returns whether the node is a terminal or not. This is defined by whether
     * it should have any children.
     */
    public boolean isTerminal() {
        return numChildren == 0;
    }

    /**
     * Deep-clones the tree rooted at this node, and returns the entire
     * copied tree.  The result has everything set except for the root
     * node's parent and argposition.  This method is identical to
     * cloneReplacing for historical reasons, except that it returns
     * the object as a GPNode, not an Object.
     */

    public final Node copy(Individual root) throws CloneNotSupportedException {
        Node newnode = (Node) (clone());
        newnode.child = new Node[child.length];
        newnode.root = root;
        newnode.parent = null;
        if (Debugger.optimisationEnabled) {
            newnode.debugger = new Debugger(this);
        }
        for (int x = 0; x < newnode.child.length; x++) {
            newnode.child[x] = (child[x].copy(root));
            // if you think about it, the following CAN'T be implemented by
            // the children's clone method.  So it's set here.
            newnode.child[x].parent = newnode;
        }
        return newnode;
    }

    /**
     * Strongly typed GP: What type does the node return?
     */
    public abstract int[] getReturnTypes();

    public int getRandomReturnType() {
        int[] returnTypes = getReturnTypes();
        return returnTypes[(int) (Evolve.getRandomNumber() * returnTypes.length)];
    }

    public boolean returnTypeMatches(int returnType) {
        int[] returnTypes = getReturnTypes();
        for (int i = 0; i < returnTypes.length; i++) {
            if (returnTypes[i] == returnType) return true;
        }
        return false;
    }


    /**
     * Any processing the node does takes place here
     */
    public abstract double execute(DataStack data);

    /**
     * What would the java be if this node were written in it?
     */
    public abstract String toJava();

    /**
     * Tells what return type each of the node's children should have
     *
     * @param index The index of the child, starting from 0
     */
    public abstract int getChildType(int index);

    /**
     * Figures out what index the child has. Returns -1 if this is not a child of the node.
     * @return
     */
    public int getChildIndex(Node c) {
        for (int i = 0; i < child.length; i++) {
            if (child[i] == c) return i;
        }
        return -1;
    }

    public int getParentsExpectedType(GPParams p) {
        if (parent == null) {
            return p.getReturnType();
        } else {
        	//POEY comment: for getChildType() -> jasmine.gp.nodes
        	//such as Add.java, Div.java, Mean.java, Mul.java, PercentDiff.java and Sub.java
            return parent.getChildType(parent.getChildIndex(this));
        }
    }

    /**
     * The id of the node
     */
    protected long id;

    /**
     * Sets the node's ID. Used to identify nodes in Automatically Defined Functions (ADFS)
     *
     * @param id
     */
    public void setID(long id) {
        this.id = id;
    }

    /**
     * Gets the node's ID. Used to identify nodes in Automatically Defined Functions (ADFS)
     *
     * @return
     */
    public long getID() {
        return id;
    }

    /**
     * Returns how many children the node is expected to have.
     *
     * @return
     */
    public int countChildren() {
        return numChildren;
    }

    /**
     * Gets a short name for the node that can be used in the tree display (or for LISP output);
     *
     * @return
     */
    public String getShortName() {
        return getClass().getName();
    }

    public String toLisp() {
        StringBuffer buffer = new StringBuffer();
        toLisp(buffer);
        return buffer.toString();
    }

    public void toLisp(StringBuffer buffer) {
        if (numChildren > 0) {
            buffer.append("(");
            buffer.append(getShortName());
            for (int i = 0; i < child.length; i++) {
                Node node = child[i];
                //buffer.append(' ');
                node.toLisp(buffer);
            }
            buffer.append(")");
        } else {
            buffer.append(getName());
        }
    }

    /**
     * Returns the tree constraint that can be used to
     * ensure trees are logical.
     *
     * @return
     */
    public TreeConstraint getTreeConstraint() {
        return null;
    }

    /**
     * Reteurns the children of this node as a Node array.
     */
    public Node[] getChildren() {
        return child;
    }

    /**
     * Returns a reference to the node's parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Inserts a child at the given index point (zero indexed)
     */
    public void addChild(Node child, int index) {
        if (child == null) {
            throw new RuntimeException("Cannot add child - child object is null");
        }
        this.child[index] = child;
        child.parent = this;
    }

    /**
     * Replaces one child with another. Used by the mutation and crossover operators.
     */
    public void replaceChild(Node oldChild, Node newChild) {
        for (int i = 0; i < numChildren; i++) {
            Node node = child[i];
            if (node == oldChild) {
                child[i] = newChild;
                newChild.parent = this;
            }
        }
    }

    /**
     * Allows a node to swap <i>itselt</i> for something else. This is used by the tree
     * optimiser which can replace silly mathematics with ERCs to reduce tree size.
     */
    public void replaceMyselfWith(Node newChild) {
        if (parent != null) {
            parent.replaceChild(this, newChild);
        } else {
            // already at the top of the tree
            if (root != null) root.setTree(index, newChild);
        }
    }

    /**
     * Gets the Node Params object which contains metadata about this node and allows this
     * node to be copied.
     */
    public NodeConstraints createNodeConstraintsObject() {
        NodeConstraints np = new NodeConstraints(getClass().getCanonicalName(), getReturnTypes(), numChildren, numChildren == 0 ? NodeConstraints.TERMINAL : NodeConstraints.FUNCTION);
        np.setArgs(getConstructorArgs());
        return np;
    }

    /**
     * The name of the Node. This is used by the Java Writer to assign variable names to nodes
     * as they are converted into code.
     */
    public String getName() {
        return numChildren == 0 ? toJava() : name;
    }

    /**
     * Sets the name of the node. This is used by the Java Writer to assign variable names to nodes
     * as they are converted into code.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Called after a node has been copied by deep cloning. The debugger is transient and needs to
     * be instantiated.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (Debugger.optimisationEnabled) {
            this.debugger = new Debugger(this);
        }
    }

    /**
     * Calculates the size of the tree
     *
     * @return The number of nodes in the tree, including this (parent) node.
     */
    public int getTreeSize() {
        int treesize = 1;
        for (int i = 0; i < numChildren; i++) {
            treesize += child[i].getTreeSize();
        }
        return treesize;
    }

    /**
     * Calculates how deep this node is in the tree
     *
     * @return The depth of the node in the tree. The root originalNode is at depth 1.
     */
    public int getDepthFromTop() {
        if (parent != null) {
            return 1 + parent.getDepthFromTop();
        } else {
            return 1;
        }
    }

    /**
     * Calculates how many levels this node is from the bottom of the tree.
     */
    public int getTreeDepth() {
        getTreeDepth(this, 1);
        return maxDepth;
    }

    private int maxDepth;

    private void getTreeDepth(Node n, int currentDepth) {
        if (currentDepth > maxDepth) maxDepth = currentDepth;       
        for (int i = 0; i < n.numChildren; i++) {
            getTreeDepth(n.child[i], currentDepth + 1);
        }
    }

    /**
     * Counts the number of nodes as defined by the node params
     * which exist in a particular tree.
     */
    public int countNodes(NodeConstraints p) {
        Vector<Node> foundNodes = new Vector<Node>(10);
        findNodes(this, p.getID(), foundNodes);
        return foundNodes.size();
    }

    private void findNodes(Node parent, long nodeID, Vector<Node> foundNodes) {
        if (parent.getID() == nodeID) {
            foundNodes.add(parent);
        }
        for (int i = 0; i < parent.child.length; i++) {
            Node child = parent.child[i];
            if (child != null) findNodes(child, nodeID, foundNodes);
        }
    }

    /**
     * All items are optimisable by default. Unless you override this method to return false.
     */
    public boolean isOptimisable() {
        return true;
    }

    /**
     * Gives nodes the opportunity to run their own optimisation routines.
     *
     * @return The Node that has been optimised. 90% of the time, this relates to the originalNode
     *         itself, so just return using <code>this</code>. However, sometimes a originalNode may choose to delete
     *         itself and pass focus to its replacement. It has to be run this way otherwise the TreeOptimiser
     *         would attempt to follow along branches that no longer existed.
     */
    public Node optimise() {
        // do nothing
        return this;
    }

    /**
     * Gets the arguments that would have to be passed to the constructor if the constructor takes arguments.
     * In most cases this is not the case so this method doesn't need to be overridden.
     */
    public Object[] getConstructorArgs() {
        return null;
    }

    public String toString() {
        return getShortName() + ", size: " + getTreeSize() + ", depth:" + getTreeDepth();
    }

    public static Individual load(File f) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream in = new ObjectInputStream(fis);
        Individual obj = (Individual) in.readObject();
        in.close();
        return obj;
    }

    public void save(File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(this);
        out.close();
    }

}
