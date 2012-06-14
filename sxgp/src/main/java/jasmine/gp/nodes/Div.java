package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.NodeExecutor;

/**
 * Performs protected division.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-May-2007
 * @version 1.0
 */
public class Div extends Node {

    public Div() {
        super(2);
        shortcut = NodeExecutor.DIV;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        double denominator = child[1].execute(data);
        data.value = denominator != 0?  child[0].execute(data) / denominator : 0;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return child[1].getName() + " != 0 ? " + child[0].getName() + " / " + child[1].getName() + " : 0";
    }

    public String getShortName() {
        return "/";
    }

}
