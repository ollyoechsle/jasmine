package jasmine.gp.tree;

import jasmine.gp.params.NodeConstraints;

/**
 * A terminal node is one that introduces data into the GP
 * system. It is usually connected in some way to the training data
 * (see subclasses of Terminal for how this is done). Terminals are
 * leaves on the tree with no children. A terminal may often have an idea
 * of what <i>kind</i> of data it outputs, so the getDefaultRangeType() method is also
 * provided. Tree builders can use this data to match certain terminals to certain
 * ERC types in the hopes of producing more useful code.
 * 
 * @author Olly Oechsle, University of Essex, Date: 18-Jan-2007
 * @version 1.0
 */
public abstract class Terminal extends Node {

    public Terminal() {
        super(0); // no children
    }

    /**
     * Being a terminal this node has no children.
     */
    public int getChildType(int index) {
        return NodeConstraints.VOID;
    }

    /**
     * Standard nodes don't have range types, but Terminals do, so we need to override the NodeParams object.
     * @return
     */
    public NodeConstraints createNodeConstraintsObject() {
        NodeConstraints constraints = new NodeConstraints(getClass().getCanonicalName(), getReturnTypes(), numChildren, NodeConstraints.TERMINAL);
        constraints.setArgs(getConstructorArgs());
        return constraints;
    }

}
