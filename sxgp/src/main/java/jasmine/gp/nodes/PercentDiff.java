package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.treebuilders.TreeConstraint;

/**
 * Calculates the percentage difference between two values.
 *
 * @author Olly Oechsle, University of Essex, Date: 31-Oct-2007
 * @version 1.0
 */
public class PercentDiff extends Node {

    public PercentDiff() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {

        double oldValue = child[0].execute(data);
        double newValue = child[1].execute(data);
        double difference = newValue - oldValue;
        data.value = difference / oldValue;
        //System.out.println("%Diff: " + oldValue + ", " + newValue + ", " + data.value);
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
        return "(" + child[1].getName() + " - " + child[0].getName() + ") / " + child[0].getName();
    }

    public String getShortName() {
        return "pd";
    }

}