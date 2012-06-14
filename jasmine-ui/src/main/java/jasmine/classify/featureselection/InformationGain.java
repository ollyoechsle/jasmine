package jasmine.classify.featureselection;


/**
 * InformationGain.java
 *
 * An implementation of information gain in Java, based on the excellent tutorial
 * by Andrew W. Moore at:
 *
 * http://www.cs.cmu.edu/~awm/tutorials
 *
 *@version 1.0 by Olly Oechsle, March 2009
 */
public class InformationGain {

    public static final boolean VERBOSE = true;
    public static final boolean SILENT = false;

    protected boolean verbose;

//    public static void main(String[] args) {
//
//        // to aid legibility
//        final int FEMALE = 0;
//        final int MALE = 1;
//
//        final int POOR = 0;
//        final int RICH = 1;
//
//        // create a table to store the totals
//        // first example: the information gained by knowledge of gender in predicting
//        // whether a person is rich or poor
//        Table t1 = new Table(2,2);
//        t1.add(FEMALE, POOR, 14423);
//        t1.add(FEMALE, RICH, 1769);
//        t1.add(MALE, POOR, 22732);
//        t1.add(MALE, RICH, 9918);
//
//        // get the information gain
//        InformationGain e = new InformationGain(VERBOSE);
//        System.out.println("Wealth v. Gender Example");
//        e.getInformationGain(t1.table);
//
//        // second example: the information gained by knowledge of a person's age in
//        // in predicting whether they are rich or poor
//        Table t2 = new Table(9, 2);
//        t2.add(0, new int[]{2507,3});
//        t2.add(1, new int[]{11262,743});
//        t2.add(2, new int[]{9468,3461});
//        t2.add(3, new int[]{6738,3986});
//        t2.add(4, new int[]{4110,2509});
//        t2.add(5, new int[]{2245,809});
//        t2.add(6, new int[]{668,147});
//        t2.add(7, new int[]{115,16});
//        t2.add(8, new int[]{42,13});
//
//        System.out.println("\n\nWealth v. Agegroup Example");
//        e.getInformationGain(t2.table);
//
//    }

    /**
     * Initialises the class in silent mode
     */
    public InformationGain() {
        this(false);
    }

    /**
     * Initialises the class with your choice of silence or high verbosity
     */
    public InformationGain(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Calculates the information gain on a given table. The more information gained
     * the more useful is an attribute in predicting the outcome.
     */
    public float getInformationGain(int[][] table) {

        // ensure the table is not empty
        if (table.length == 0) {
            return 0;
        }

        // have a look at the table and find out how many outcomes there are
        int numValues = table.length;	//POEY comment: DISCRETISATION_FACTOR+1
        int numOutcomes = table[0].length;	//POEY comment: the number of classes

        // get the overall entropy
        EntropyValue Hy = getEntropy(table, numOutcomes, -1);
        if (verbose) System.out.println("H(Y) = " + Hy.entropy);

        // get the specific conditional entropy for each outcome //POEY comment: for each slot
        EntropyValue[] specificEntropies = new EntropyValue[numValues];
        for (int i = 0; i < numValues; i++) {
            specificEntropies[i] = getEntropy(table, numOutcomes, i);           
            if (verbose) System.out.println("H(Y|X=" + i + ") = " + specificEntropies[i].entropy);
        }

        // get the average conditional entropy
        float conditionalEntropy = getAverageConditionalEntropy(specificEntropies);
        if (verbose) System.out.println("H(Y|X) = " + conditionalEntropy);

        // compute the information gain
        float informationGain = Hy.entropy - conditionalEntropy;
        if (verbose) System.out.println("IG(Y/X) = " + informationGain);
        return informationGain;
        
    }

    /**
     * Gets the average conditional entropy which is an average of each specific
     * conditional entropy (one for each predictor), weighted by the the number of instances
     * of each.
     */
    public float getAverageConditionalEntropy(EntropyValue[] specificEntropies) {

        float total = 0;
        //POEY comment: specificEntropies.length = DISCRETISATION_FACTOR+1
        for (int i = 0; i < specificEntropies.length; i++) {
            EntropyValue specificEntropy = specificEntropies[i];
            total += specificEntropy.prob * specificEntropy.entropy;
        }

        return total;

    }

    /**
     * Given a table, gets the probability for the outcomes
     *
     * @param samples A contingency table, attributes on the vertical side, outcomes on the horizontal side.
     */
    public EntropyValue getEntropy(int[][] samples, int numOutcomes, int specificValue) {

        // the total number of samples for this specific attribute value
        float total = 0;
        // the total number of samples
        float n = 0;

        // record the probability for each outcome
        double[] outcomeProbabilities = new double[numOutcomes];

        // tot up the total
        //POEY comment: samples.length = DISCRETISATION_FACTOR+1
        for (int value = 0; value < samples.length; value++) {

            int[] outcomeTotals = samples[value];	
            //POEY comment: outcomeTotals.length = the number of classes
            for (int outcome = 0; outcome < outcomeTotals.length; outcome++) {
                if (specificValue == -1 || specificValue == value) {	
                    // add to the total
                    total += outcomeTotals[outcome];
                    // record the total for each outcome
                    outcomeProbabilities[outcome] += outcomeTotals[outcome];
                }
                n += outcomeTotals[outcome];
            }

        }

        // let's not get into divide by zero errors
        if (total == 0) {
            return new EntropyValue(0,0);
        }

        // divide by the totals to get probability values
        for (int i = 0; i < outcomeProbabilities.length; i++) {
            outcomeProbabilities[i] /= total;
        }

        // calculate the entropy
        //POEY comment: if specificValue == -1 then total == n
        return new EntropyValue(getEntropy(outcomeProbabilities), total/n);

    }

    /**
     * Gets the entropy given a series of probability values
     *
     * @param pValues An array of probability values, each between 0 and 1.
     */
    public float getEntropy(double[] pValues) {

        float entropy = 0;

        for (double pValue : pValues) {
            // can't take logs of 0
            if (pValue == 0) continue;
            // log_2(n) = log_10(n) / log_10(2)
            entropy -= pValue * (Math.log(pValue) / Math.log(2));
        }

        return entropy;

    }

    class EntropyValue {

        protected float prob;
        protected float entropy;

        EntropyValue(float entropy, float prob) {
            this.prob = prob;
            this.entropy = entropy;
        }

    }

}
