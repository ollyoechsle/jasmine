package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
 * If you prefer to use fixed value constants, instead of random constants
 * then register some of these nodes instead. Simply initialise the value
 * in the constructor with whatever value you want.
 *
 * @author Olly Oechsle, University of Essex, Date: 29-Jul-2007
 * @version 1.1 October 23 2008
 */
public class FixedValueTerminal extends Terminal {

    protected double VALUE = 1.0;
    protected int[] returnTypes;

    public FixedValueTerminal(double value) {
        this(value, new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER});
    }

    public FixedValueTerminal(int value) {
        this(value, new int[]{NodeConstraints.NUMBER, NodeConstraints.INTEGER});
    }

    public FixedValueTerminal(double value, int[] returnTypes) {
        this.VALUE = value;
        this.returnTypes = returnTypes;
    }

    public int[] getReturnTypes() {
        return returnTypes;
    }

    public double execute(DataStack data) {
        data.value = VALUE;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return String.valueOf(VALUE);
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this node is cloned.
        return new Object[]{VALUE};
    }    

}
