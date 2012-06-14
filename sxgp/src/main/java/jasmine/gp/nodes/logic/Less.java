package jasmine.gp.nodes.logic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.treebuilders.TreeConstraint;

/**
* Returns TRUE if the value of the child is BELOW a threshold
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public class Less extends Node {

    public Less() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.BOOLEAN, NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = child[0].execute(data) < child[1].execute(data)? 1 : 0;
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
        return child[0].getName() + " < " + child[1].getName();
    }

    public String getShortName() {
        return "<";
    }

}
