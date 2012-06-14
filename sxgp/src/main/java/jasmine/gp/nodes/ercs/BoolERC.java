package jasmine.gp.nodes.ercs;

import jasmine.gp.Evolve;
import jasmine.gp.params.NodeConstraints;

/**
 * For If statement compatibility only. Not really very useful.
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public class BoolERC extends BasicERC {

    public void jitter() {
        // do nothing - boolean's can't be jittered
    }

    public void setValue(double value) {
        super.setValue(value == 0? 0 : 1);
    }

    public double initialise() {     	
        return Evolve.getRandomNumber() > 0.5? 1 : 0;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.BOOLEAN};
    }

    public String toJava() {
        return getValue() == 1? "true" : "false";
    }

}
