package jasmine.gp.nodes.generic;


import jasmine.gp.Evolve;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.util.CSVReader;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * <p/>
 * Provides any feature from a CSV file which has to be loaded statically first.
 * This allows you to interface your Genetic Programming system with data which
 * may be produced in an entirely different way.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Oct-2007
 * @version 1.0
 */
public class CSVFeature extends Terminal {

    public static final int TRAINING = 1;
    public static final int TESTING = 2;

    public static boolean NORMALISING = false;

    protected static Normaliser n;

//    public static void main(String[] args) throws Exception {
//
//        // ensure that the feature can load data
//        File training = new File("/home/ooechs/Desktop/Postures_shape_features.csv");
//        File testing = new File("/home/ooechs/Desktop/Postures-Unseen_shape_features.csv");
//
//        CSVFeature.loadTrainingData(training);
//        CSVFeature.loadTestData(training);
//
//        System.out.println("DONE.");
//
//        // ensure that the feature can be cloned
//        CSVFeature feature1 = new CSVFeature(1);
//        NodeConstraints p = feature1.createNodeConstraintsObject();
//        CSVFeature feature2 = (CSVFeature) p.getInstance();
//
//        System.out.println("Feature1: " + feature1.index);
//        System.out.println("Feature2: " + feature2.index);
//
//    }

    protected int index;

    protected static double[][] trainingData = null;
    protected static double[][] testData = null;

    protected static Vector<Integer> distinctClasses;
    protected static int trainingDataSize = 0;
    protected static int testDataSize = 0;

    protected static boolean trainingMode = true;

    protected static int cols = 0;
    protected static int row = 0;

    public CSVFeature(int index) {
        this.index = index;
        if (trainingData == null) {
            System.err.println("CSV Feature data is null! Use CSVFeature.loadData() to load feature info first.");
        }
    }


    /**
     * Gets the class IDs which are identified during the course of the loadTrainingData method.
     *
     * @return
     */
    public static Vector<Integer> getDistinctClasses() {
        return distinctClasses;
    }

    /**
     * Sets the row from which the data is accessed. This is zero indexed.
     */
    public static void setRow(int row) {
        CSVFeature.row = row;
    }

    /**
     * Sets whether the feature accesses data from either the training data set or the
     * test data set.
     */
    public static void setTrainingMode(boolean trainingMode) {
        CSVFeature.trainingMode = trainingMode;
    }

    /**
     * Reduces the size of the training data to the given size. If the training
     * data is less than this size already then no changes are made. Data is randomly
     * pruned until the correct size is attained.
     *
     * @param size
     */
    public static void pruneTrainingSize(int size) {

        if (trainingData.length > size) {

            double[][] newTrainingData = new double[size][];
            int count = 0;

            while (count < size) {

                double data[] = trainingData[(int) (trainingData.length * Evolve.getRandomNumber())];
                newTrainingData[count] = data;
                count++;

            }

            trainingData = newTrainingData;
            trainingDataSize = size;

        }

    }

    /**
     * Gets the complete set of data for a given row. This will not
     * include the expected output (the last column in the data).
     *
     * @param type Either TRAINING or TESTING
     */
    public static double[] getData(int type, int index) {

        double[] data = new double[cols - 1];

        for (int i = 0; i < (cols - 1); i++) {
            if (type == TRAINING) {
                data[i] = trainingData[index][i];
            } else {
                data[i] = testData[index][i];
            }
        }

        return data;

    }

    /**
     * Returns the number of rows in the training set.
     */
    public static int getTrainingDataSize() {
        return trainingDataSize;
    }

    /**
     * Returns the number of rows in the test set.
     */
    public static int getTestDataSize() {
        return testDataSize;
    }

    /**
     * Returns the number of columns in the CSV file.
     */
    public static int getColumnCount() {
        return cols;
    }

    /**
     * Loads information from a CSV file into the CSVFeature's data cache.
     * The first line in the CSV file is assumed to have headings and is ignored.
     * The rest of the CSV file is assumed to contain only numeric data. Exceptions will
     * occur if this is not the case.
     * ClassID, or expected value is assumed to be the final column in the CSV file.
     *
     * @param CSVFile
     */
    public static void loadTrainingData(File CSVFile) throws Exception {

        trainingDataSize = loadData(true, CSVFile);

        // calculate the distinct classes
        distinctClasses = new Vector<Integer>();
        for (int i = 0; i < trainingDataSize; i++) {
            double[] row = trainingData[i];
            int classID = (int) row[row.length - 1];
            if (!distinctClasses.contains(classID)) distinctClasses.add(classID);
        }

    }

    /**
     * Loads information from a CSV file into the CSVFeature's data cache.
     * The first line in the CSV file is assumed to have headings and is ignored.
     * The rest of the CSV file is assumed to contain only numeric data. Exceptions will
     * occur if this is not the case.
     *
     * @param CSVFile
     */
    public static void loadTestData(File CSVFile) throws Exception {
        testDataSize = loadData(false, CSVFile);
    }    

    /**
     * Loads information from a CSV file into the CSVFeature's data cache.
     * The first line in the CSV file is assumed to have headings and is ignored.
     * The rest of the CSV file is assumed to contain only numeric data. Exceptions will
     * occur if this is not the case.
     *
     * @param CSVFile
     */
    private static int loadData(boolean training, File CSVFile) throws IOException {

        CSVReader c = new CSVReader(CSVFile);

        Vector<Vector<String>> rawData = new Vector<Vector<String>>(300);

        cols = 0;

        // ignore the first line
        c.getLine();

        while (c.hasMoreLines()) {
            Vector<String> l = c.getLine();
            rawData.add(l);
            if (l.size() > cols) cols = l.size();
        }

        if (training) n = new Normaliser(cols);

        double[][] data = new double[rawData.size()][cols];

        for (int i = 0; i < rawData.size(); i++) {
            Vector<String> row = rawData.elementAt(i);
            for (int j = 0; j < row.size(); j++) {
                data[i][j] = Double.parseDouble(row.elementAt(j));
                if (NORMALISING && training) n.addData(j, data[i][j]);
            }
        }

        if (NORMALISING) {
            // normalise the data here
            for (int i = 0; i < rawData.size(); i++) {
                Vector<String> row = rawData.elementAt(i);
                for (int j = 0; j < (row.size() - 1); j++) {
                    data[i][j] = data[i][j] / n.getNormalisationFactor(j);
                }
            }
        }

        if (training) {
            trainingData = data;
        } else {
            testData = data;
        }

        return rawData.size();

    }

    public static int getTrainingClassID(int index) {
        return (int) trainingData[index][cols - 1];
    }

    public static int getTestClassID(int index) {
        return (int) testData[index][cols - 1];
    }


    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        if (trainingMode) {
            data.value = trainingData[row][index];
        } else {
            data.value = testData[row][index];
        }
        return debugger == null? data.value : debugger.record(data.value);
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this node is cloned.
        return new Object[]{index};
    }

    public String getShortName() {
        return "f" + index;
    }

    public String toJava() {
        return "feature[" + index + "]";
    }


}
