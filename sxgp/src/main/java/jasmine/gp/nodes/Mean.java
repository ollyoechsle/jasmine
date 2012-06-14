package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * Gets the mean value of two nodes
 *
 * @author Olly Oechsle, University of Essex, Date: 19-Nov-2007
 * @version 1.0
 */
public class Mean extends Node {

    public Mean() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = (child[0].execute(data) + child[1].execute(data)) / 2;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "(" + child[0].getName() + " + " + child[1].getName() + ") / 2";
    }

    public String getShortName() {
    	return "m";
    }

}
