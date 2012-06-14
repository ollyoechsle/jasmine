package jasmine.gp.nodes;

import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.treebuilders.TreeConstraint;

/**
 * If-then-else function
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public class If extends Node {

    public If() {
        super(3);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.SUBSTATEMENT, NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        switch (index)  {
            case 0:
                return NodeConstraints.BOOLEAN;
            default:
                return NodeConstraints.SUBSTATEMENT;
        }
    }

    public double execute(DataStack data) {
        data.value = child[0].execute(data);
        if (data.value == 1) {
            data.value = child[1].execute(data);
        } else {
            data.value = child[2].execute(data);
        }
        return debugger == null? data.value : debugger.record(data.value);
    }

    public Node optimise() {
        if (child[1].debugger.neverExecuted()) {
            // it would appear that the program would work just as well if we just returned
            // the second tree and removed the if statement all together
            replaceMyselfWith(child[2]);
            return child[2];
        }
        if (child[2].debugger.neverExecuted()) {
            // it would appear that the program would work just as well if we just returned
            // the first tree and removed the if statement all together
            replaceMyselfWith(child[1]);
            return child[1];
        }
        return this;
    }

    /**
     * Returns the tree constraint that can be used to
     * ensure trees are logical.
     */
    public TreeConstraint getTreeConstraint() {
        return new TreeConstraint(TreeConstraint.AT_LEAST_ONE_TERMINAL_WITH_TYPE, NodeConstraints.FEATURE);
    }

    public String toJava() {
        String expression = child[0].getName();
        if (child[0] instanceof BasicERC) {
            // if it was replaced by an ERC by the optimiser there will just be
            // 0.0, or 1.0, which won't compile. Convert this to "true" or "false";
            double d = ((jasmine.gp.nodes.ercs.BasicERC) child[0]).getValue();
            if (d == 1.0) {
                expression = "true";
            }
            if (d == 0.0) {
                expression = "false";
            }
        }
        return expression + "? " + child[1].getName() + " : " + child[2].getName();
    }

    public String getShortName() {
        return "If";
    }

}
