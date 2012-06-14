package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.treebuilders.TreeConstraint;

/**
 * Adds the values of two nodes together.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class Min extends Node {

    public Min() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = Math.min(child[0].execute(data),child[1].execute(data));
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
        return "Math.min(" + child[0].getName() + ", " + child[1].getName() + ")";
    }

    public String getShortName() {
        return "min";
    }

}
