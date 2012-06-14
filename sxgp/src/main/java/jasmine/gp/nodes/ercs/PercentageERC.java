package jasmine.gp.nodes.ercs;

import jasmine.gp.Evolve;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;

/**
 * Returns a value between 0.0-1.0
 *
 * @author Olly Oechsle, University of Essex, Date: 23-Feb-2007
 * @version 1.0
 */
public class PercentageERC extends BasicERC {

    public void jitter() {
        double value = getValue();
        // plus or minus 10%
        value *= (1.1 - (Evolve.getRandomNumber() * 0.2));
        if (value > 1) value = 1;
        else if (value < 0) value = 0;
        setValue(value);
    }

    public double initialise() {
        return Evolve.getRandomNumber();
    }

    /**
     * Sets the value of the ERC to something different.
     */
    public void setValue(double value) {
        super.setValue(value);
        if (value > 1) {
            value = 1;
            throw new RuntimeException("Percentage ERC: Setting value too high: " + value);
        }
    }

    /**
     * Executes the ERC
     */
    public double execute(DataStack data) {
        double d =  super.execute(data);    //To change body of overridden methods use File | Settings | File Templates.
        if (d > 1) throw new RuntimeException("PERC is too high: " + d);
        return d;
    }


    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.PERCENTAGE};
    }

}
