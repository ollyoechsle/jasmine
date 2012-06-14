package jasmine.gp.tree;

import java.util.Hashtable;

/**
 * Each time a node is executed, the debugger is called to record the value.
 * This provides valuable information to the Tree Optimiser.
 *
 * @author Olly Oechsle, University of Essex, Date: 16-Jan-2007
 * @version 1.1 // Added normalisation stuff.
 */
public class Debugger {

    /**
     * Turns on optimisation. This compacts the tree of the best individuals
     * just as they are about to produce the next generation. This helps
     * keep code bloat down and makes crossover more effective.
     */
    public static boolean optimisationEnabled = false;

    public static boolean NORMALISE_DATA = false;

    protected int numberOfExecutions = 0;

    private double value = -1;

    // FOR NORMALISATION CODE
    private static Hashtable<String, Integer> indices;
    private static double[] highs;
    private static double[] lows;
    private static int currentIndex = 0;
    // END NORMALISATION VARIABLES

    boolean alwaysTheSame = true;

    protected Terminal t = null;

    public Debugger(Node n) {
        numberOfExecutions = 0;
        if (n instanceof Terminal) t = (Terminal) n;
    }

    public double record(double value) {

        if (!optimisationEnabled) {
            // skip the recording and make evolution faster
            return value;
        }

        if (indices == null) {
            indices = new Hashtable<String, Integer>();
            highs = new double[100];
            lows = new double[100];
        }

        if (this.value != -1 && value != this.value) {
            alwaysTheSame = false;
        }

        numberOfExecutions++;
        this.value = value;

        // Normalisation only applies for terminals, naturally
        if (!NORMALISE_DATA || t == null) {
            // return the raw result.
            return value;
        } else {

            // find the index for this data
            String key = t.getShortName();

            Integer index = indices.get(key);

            if (index == null) {
                // create a new index for this key
                indices.put(key, currentIndex);
                index = currentIndex;
                currentIndex++;
            }

            // Now get the high and low for this data
            double high = highs[index];
            double low = lows[index];

            if (value > high) {
                high = value;
                highs[index] = value;
            }

            if (value < low) {
                low = value;
                lows[index] = value;
            }
            
            // returns the data  normalised between zero and 1.
            return (value - low) / (high - low);

        }
    }

    public double getLastValue() {
        return value;
    }

    public boolean alwaysTheSame() {
        return alwaysTheSame;
    }

    public boolean neverExecuted() {
        return numberOfExecutions == 0;
    }

}
