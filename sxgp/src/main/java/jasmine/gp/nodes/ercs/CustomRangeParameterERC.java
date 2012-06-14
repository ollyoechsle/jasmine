package jasmine.gp.nodes.ercs;

import jasmine.gp.Evolve;
import jasmine.gp.params.NodeConstraints;

/**
 * Allows you to set an arbitrary range on values for the ERC.
 * Values are distributed uniformly.
 *
 * @author Olly Oechsle, University of Essex, Date: 21-Mar-2008
 * @version 1.0
 */
public class CustomRangeParameterERC extends BasicERC {

    private int min, max;

    public CustomRangeParameterERC(int min, int max) {
        this.min = min;
        this.max = max;
        setValue(initialise());
    }

    public void jitter() {
        double d = Evolve.getRandomNumber();
        if (d < 0.5) {
            if (getValue() > min) setValue(getValue() - 1);
        } else {
            if (getValue() < max) setValue(getValue() + 1);
        }  
    }

    public double initialise() {
        double range = max - min + 1;
        return (int) (min + (Evolve.getRandomNumber() * range));
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.INTEGER, NodeConstraints.PARAMETER};
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{min, max};
    }

}
