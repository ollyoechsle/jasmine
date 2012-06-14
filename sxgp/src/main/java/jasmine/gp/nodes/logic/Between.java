package jasmine.gp.nodes.logic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.treebuilders.TreeConstraint;

/**
* Returns TRUE if the value of the child is BETWEEN two thresholds
 *
 * @author Olly Oechsle, University of Essex, Date: 28-Mar-2007
 * @version 1.0
 */
public class Between extends Node {

    public Between() {
        super(3);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.BOOLEAN, NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        double value1 = child[1].execute(data);
        double value2 = child[2].execute(data);
        if (value1 > value2) {
            // swap so that value 1 is always less than value2
            Node child1 = child[1];
            child[1] = child[2];
            child[2] = child1;
        }
        double value0 = child[0].execute(data);
        data.value = ((value0 > child[1].execute(data)) && (value0 < child[2].execute(data)))? 1 : 0;
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
        return "((" + child[0].getName() + " > " + child[1].getName() + ") && (" + child[0].getName() + " < " + child[2].getName() + "))";
    }

    public String getShortName() {
        return "Between";
    }

}
