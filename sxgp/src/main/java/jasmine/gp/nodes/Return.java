package jasmine.gp.nodes;


import jasmine.gp.Evolve;
import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;

import java.util.Vector;

/**
 * Returns a particular class ID - used in decision trees.
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public class Return extends BasicERC {

    // default 1 - should be overridden by the ImagingProblem
    public static int[] classes;

    /**
     * Sets up the return class to return the classIDs referenced
     * in a vector.
     */
    public static void setClasses(Vector<Integer> distinctClasses) {
        classes = new int[distinctClasses.size()];
        for (int i = 0; i < distinctClasses.size(); i++) {
            classes[i] = distinctClasses.elementAt(i);
        }
    }

    public double initialise() {
        return classes[(int) (Evolve.getRandomNumber() * classes.length)];
    }

    public void jitter() {
        double d = Evolve.getRandomNumber();
        if (d > 0.9) {
            initialise();
        }
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.SUBSTATEMENT, NodeConstraints.NUMBER};
    }

    public String toJava() {
        return String.valueOf((int) getValue());
    }

    public boolean isOptimisable() {
        return false;
    }

    public int getRangeID() {
        return RangeTypes.DONT_CARE;
    }
    
}
