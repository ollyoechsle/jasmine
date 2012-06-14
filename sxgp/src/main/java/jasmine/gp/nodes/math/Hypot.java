package jasmine.gp.nodes.math;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * Hypotenuse Function
 *
 * @author Olly Oechsle, University of Essex, Date: 02-Nov-2007
 * @version 1.0
 */
public class Hypot extends Node {

    public Hypot() {
        super(2);        
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = Math.hypot(child[0].execute(data), child[1].execute(data));
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "Math.hypot(" + child[0].getName() + ", " + child[1].getName() + ")";
    }

    public String getShortName() {
        return "hypot";
    }

}