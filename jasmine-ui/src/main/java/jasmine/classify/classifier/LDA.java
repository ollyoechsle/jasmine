package jasmine.classify.classifier;

import java.util.Vector;
import java.util.Hashtable;


/**
 * An implementation of Linear Discriminant Analysis which is useful for
 * feature extraction or indeed making a classifier.
 *
 * @author Olly Oechsle, University of Essex, Date: 20-Feb-2008
 * @version 1.0
 */
public class LDA {

    /**
     * A collection of the different classes which make up the problem
     */
    protected Vector<Integer> classes;

    /**
     * A hashtable allowing us to access groups by classID.
     */
    protected Hashtable<Integer, Group> groups;

    /**
     * The total number of training samples
     */
    protected int totalSamples;

    /**
     * The number of features found per sample
     */
    protected int numFeatures;

    /**
     * The global mean, one entry per feature.
     */
    protected double[] globalMean;

    /**
     * The inverted within groups covariance matrix
     */
    double[][] inverted;

    /**
     * Prior probabilities for each class
     */
    double[] priorProbabilities;

    /**
     * Constructs the Linear Discriminant Analysis program
     * @param numFeatures The number of features that each sample uses.
     */
    public LDA(int numFeatures) {
        this.totalSamples = 0;
        this.numFeatures = numFeatures;
        this.globalMean = new double[numFeatures];
        classes = new Vector<Integer>(5);
        groups = new Hashtable<Integer, Group>(5);
    }

    /**
     * Adds a sample of training data to the LDA. This consists
     * of an array of data (which should have the same size as the num
     * features argument supplied to the constructor), and the class identifier by which
     * it should be identified. ClassID should be positive.
     */
    public void add(double[] data, int classID) {

        if (!classes.contains(classID)) {
            classes.add(classID);
            groups.put(classID, new Group(classID));
        }

        groups.get(classID).add(data);

    }

    /**
     * Causes the relevant matrices to be calculated so the LDA becomes ready to
     * make classifications.
     */
    public void compute() {

        // calculate the global mean
    	//POEY comment: globalMean is the total value of every pixel
    	//For segmentation: totalSamples are the number of pixels
    	//For object classification: totalSamples are the number of objects
        for (int i = 0; i < globalMean.length; i++) {
            globalMean[i] /= totalSamples;
        }

        // initialise the prior probabilities
        priorProbabilities = new double[classes.size()];

        // initialise the pooled covariance matrix
        double[][] pooled = new double[numFeatures][numFeatures];

        // go through each group in turn
        for (int i = 0; i < classes.size(); i++) {

            // get the class ID
            Integer classID = classes.elementAt(i);

            // and find the associated group
            Group group = groups.get(classID);

            // subtracts the global mean from the data
            group.adjustDataByGlobalMean();

            // get the covariance matrix
            double[][] cov = group.getCovarianceMatrix();

            // calculate the prior probability for this group
            priorProbabilities[i] = group.data.size() / (double) totalSamples;

            // add to the pooled covariance matrix which is the group's matrix multiplied by the prior probability
            for (int col1 = 0; col1 < numFeatures; col1++) {
                for (int col2 = 0; col2 < numFeatures; col2++) {
                    pooled[col1][col2] += priorProbabilities[i] * cov[col1][col2];
                }
            }

        }

        // invert the matrix. Fortunately the nasty business of matrix inversion is taken care of by this code:
        //POEY comment: As numFeatures = 1, so the before and after matrixes are same
        inverted = jasmine.classify.classifier.Matrix.invert(pooled);

    }

    /**
     * Classifies a set of data using the LDA. The best fitting class is returned.
     */
    public int classify(double[] x) {

        double highestF = 0;
        int classification = -1;

        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);

            double f1 = 0, f2 = 0;

            // and find the associated group
            Group group = groups.get(classID);

            // Finally, the discriminant function
            //POEY comment: numFeatures=1
            for (int row = 0; row < numFeatures; row++) {
                double sum = 0;
                for (int col = 0; col < numFeatures; col++) {
                    sum += (inverted[row][col] * group.unadjustedMean[col]);
                }
                f1 += sum * x[row];
                f2 += sum * group.unadjustedMean[row];
            }

            double f = f1 - (0.5 * f2) + Math.log(priorProbabilities[i]);

            if (classification == -1 || f > highestF) {
                highestF = f;
                classification = classID;
            }

        }

        return classification;

    }

    /**
     * Tests the LDA against a known piece of training data. Returns true if the LDA returned the correct answer; false otherwise.
     */
    public boolean test(double[] data, int classID) {
        boolean b =  classify(data) == classID;
        if (b) System.out.println("Correct");
        else System.out.println("Wrong");
        return b;
    }

    /**
     * Tests against the data already added to the classifier.
     * @return
     */
    public double test() {
        double correct = 0;
        double n = 0;

        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);

            // and find the associated group
            Group group = groups.get(classID);

            for (int j = 0; j < group.data.size(); j++) {
                double[] data = group.data.elementAt(j);
                n++;
                if (classify(data) == group.classID) correct++;
            }

        }

        return correct / n;
    }




//    public static void main(String[] args) {
//        LDA lda = new LDA(2);
//        lda.add(new double[]{2.95, 6.63}, 1);
//        lda.add(new double[]{2.53, 7.79}, 1);
//        lda.add(new double[]{3.57, 5.65}, 1);
//        lda.add(new double[]{3.16, 5.47}, 1);
//        lda.add(new double[]{2.58, 4.46}, 2);
//        lda.add(new double[]{2.16, 6.22}, 2);
//        lda.add(new double[]{3.27, 3.52}, 2);
//        lda.compute();
///*        lda.test(new double[]{2.95, 6.63}, 1);
//        lda.test(new double[]{2.53, 7.79}, 1);
//        lda.test(new double[]{3.57, 5.65}, 1);
//        lda.test(new double[]{3.16, 5.47}, 1);
//        lda.test(new double[]{2.58, 4.46}, 2);
//        lda.test(new double[]{2.16, 6.22}, 2);
//        lda.test(new double[]{3.27, 3.52}, 2);*/
//        System.out.println(lda.test());
//    }

    private final class Group {

        double[] adjustedGroupMean;
        double[] unadjustedMean;
        Vector<double[]> data;
        int classID;

        public Group(int classID) {
            this.classID = classID;
            this.data = new Vector<double[]>();
            this.unadjustedMean = new double[numFeatures];
        }

        public void add(double[] data) {
        	//POEY comment: data is a calculated value of a pixel
            this.data.add(data);
            for (int i = 0; i < data.length; i++) {
                globalMean[i] += data[i];
                unadjustedMean[i] += data[i];
            }
            //POEY comment: the number of selected pixels
            totalSamples++;
        }

        /**
         * Subtracts the data from the global mean in order to calculate the mean corrected data.
         * Also calculates the group mean which is the mean of the corrected data.
         */

        public void adjustDataByGlobalMean() {
            adjustedGroupMean = new double[numFeatures];
            //POEY comment: data stores calculated values of pixels of this class
            for (int i = 0; i < data.size(); i++) {
                double[] d = data.elementAt(i);
                for (int j = 0; j < d.length; j++) {
                    adjustedGroupMean[j] += (d[j] - globalMean[j]);
                }
            }
            //POEY comment: numFeatures=1
            for (int i = 0; i < numFeatures; i++) {
            	//POEY comment: data.size = the number of pixels of its class
            	//unadjustedMean is the total value of selected pixel of its class
                adjustedGroupMean[i] /= data.size();
                unadjustedMean[i] /= data.size();
            }
        }

        public double[][] getCovarianceMatrix() {

            // create the matrix, its size is a square of the number of features
        	//POEY comment: numFeatures=1
            double[][] matrix = new double[numFeatures][numFeatures];

            // n is the number of rows
            double n = data.size();

            for (int col1 = 0; col1 < numFeatures; col1++) {
                for (int col2 = 0; col2 < numFeatures; col2++) {

                    // covariance at [col1][col2] is the comparison of col 1 to col 2
                    double covariance = 0;

                    for (int row = 0; row < data.size(); row++) {
                    	//POEY comment: data.elementAt(row)[col1] = data.elementAt(row)[col2] = a value of a pixel
                    	//globalMean[col1] = globalMean[col2]
                        double data1 = data.elementAt(row)[col1] - globalMean[col1];
                        double data2 = data.elementAt(row)[col2] - globalMean[col2];
                        covariance += (data1) * (data2);
                    }

                    matrix[col1][col2] = covariance / n;
                }                
            }

            return matrix;

        }


    }


}
