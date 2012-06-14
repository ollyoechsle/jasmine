package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.NodeExecutor;

/**
 * Subtracts the value of one node from the other
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class Sub extends Node {

    public Sub() {
        super(2);
        shortcut = NodeExecutor.SUB;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = child[0].execute(data) - child[1].execute(data);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return child[0].getName() + " - " + child[1].getName();
    }

    public String getShortName() {
        return "-";
    }

}
