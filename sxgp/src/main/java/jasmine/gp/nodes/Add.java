package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.NodeExecutor;
import jasmine.gp.treebuilders.TreeConstraint;

/**
 * Adds the values of two nodes together.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class Add extends Node {

    public Add() {
        super(2);
        shortcut = NodeExecutor.ADD;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        if (child[0] == null) System.err.println("Child 0 is null");
        if (child[1] == null) System.err.println("Child 1 is null");
        data.value = child[0].execute(data) + child[1].execute(data);
        return debugger == null? data.value : debugger.record(data.value);
    }

    /**
     * Returns the tree constraint that can be used to
     * ensure trees are logical.
     */
    public TreeConstraint getTreeConstraint() {
        return new TreeConstraint(TreeConstraint.AT_LEAST_ONE_TERMINAL_WITH_TYPE, NodeConstraints.FEATURE);
    }     

    public String toJava() {
        return child[0].getName() + " + " + child[1].getName();
    }

    public String getShortName() {
        return "+";
    }

}
