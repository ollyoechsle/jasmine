package jasmine.gp.nodes.math;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * Square Root Function
 *
 * @author Olly Oechsle, University of Essex, Date: 02-Nov-2007
 * @version 1.0
 */
public class Sqrt extends Node {

    public Sqrt() {
        super(1);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = Math.sqrt(child[0].execute(data));
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "Math.sqrt(" + child[0].getName() + ")";
    }

    public String getShortName() {
        return "sqrt";
    }

}