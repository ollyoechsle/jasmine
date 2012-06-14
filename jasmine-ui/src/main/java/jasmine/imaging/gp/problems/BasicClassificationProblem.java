package jasmine.imaging.gp.problems;

import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.multiclass.ClassResults;
import jasmine.gp.nodes.generic.CSVFeature;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.gp.nodes.AspectRatio;
import jasmine.imaging.gp.nodes.BalanceX;
import jasmine.imaging.gp.nodes.BalanceXEnds;
import jasmine.imaging.gp.nodes.BalanceY;
import jasmine.imaging.gp.nodes.BalanceYEnds;
import jasmine.imaging.gp.nodes.ClosestEndToCog;
import jasmine.imaging.gp.nodes.ClosestPixelToCog;
import jasmine.imaging.gp.nodes.Corners;
import jasmine.imaging.gp.nodes.CountHollows;
import jasmine.imaging.gp.nodes.Density;
import jasmine.imaging.gp.nodes.Ends;
import jasmine.imaging.gp.nodes.HorizontalSymmetry;
import jasmine.imaging.gp.nodes.InverseHorizontalSymmetry;
import jasmine.imaging.gp.nodes.InverseVerticalSymmetry;
import jasmine.imaging.gp.nodes.Joints;
import jasmine.imaging.gp.nodes.Roughness;
import jasmine.imaging.gp.nodes.Roundness;
import jasmine.imaging.gp.nodes.VerticalSymmetry;
import jasmine.imaging.shapes.ExtraShapeData;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * Jasmine Shape problems get their training data from a Jasmine project.
 * They may also have weights associated with each item in the training data
 * so that a boosting algorithm may be employed to improve their performance.
 *
 * @author Olly Oechsle, University of Essex, Date: 10-May-2007
 * @version 1.0
 */
public abstract class BasicClassificationProblem extends Problem {

    public static final int MODE_CSV = 1;
    public static final int MODE_JASMINE = 2;

    public boolean verbose = false;

    /**
     * Whether we are in CSV or Jasmine mode. Set according to which constructor the
     * programmer uses.
     */
    private int mode;

    /**
     * The project which contains the shape training data.
     */
    private JasmineProject jasmineTrainingProject;

    /**
     * An unseen project that can be used for testing
     */
    private JasmineProject jasmineTestProject;

    /**
     * A CSV file containing some classification data
     */
    private File csvTrainingFile;

    /**
     * A CSV file containing some test data
     */
    private File csvTestFile;

    /**
     * If using the training data comes from a jasmine project, we cache it here.
     */
    private Vector<ExtraShapeData> trainingData, testData;

    /**
     * How much training data and test data there is.
     */
    private int trainingCount = 0, testCount = 0;

    /**
     * The different classes. Derived from the training data.
     */
    protected Vector<Integer> distinctClasses;

    /**
     * The highest value of class that we find in the training data.
     */
    private int highestClassID = 0;

    /**
     * Figure out the name of the training data - either from the name of the jasmine
     * project, or from the name of the CSV file.
     */
    private String name;

    /**
     * Weights which may be associated with the training data. This allows the classification
     * problem to be run with AdaBoost.
     */
    protected double[] weights = null;

    /**
     * Constructs the Jasmine Shape Problem with an instance
     * of a Jasmine Project. Jasmine Projects may be loaded using
     * JasmineProject.load()
     */
    public BasicClassificationProblem(JasmineProject project, JasmineProject testProject) {
        this.jasmineTrainingProject = project;
        this.jasmineTestProject = testProject;
        this.name = project.getName();
        this.mode = MODE_JASMINE;
    }

    /**
     * Constructs the problem using CSV data instead of a Jasmine project. This
     * is arguably the more useful situation. There are certain constraints placed
     * upon the CSV file - which are in FeatureERC in the SXGP source.
     */
    public BasicClassificationProblem(File trainingDataFile, File testDataFile) {
        this.csvTrainingFile = trainingDataFile;
        this.csvTestFile = testDataFile;
        if (!trainingDataFile.exists()) {
            System.err.println(trainingDataFile.getName() + " does not exist.");
        }
        if (!testDataFile.exists()) {
            System.err.println(trainingDataFile.getName() + " does not exist.");
        }
        this.name = trainingDataFile.getName();
        this.mode = MODE_CSV;
    }

    private boolean loadedAlready = false;

    /**
     * Loads the training data
     *
     * @param e
     */
    public void loadData(Evolve e) {

        if (loadedAlready) return;

        switch (mode) {

            case MODE_JASMINE:

                // load the data from the Jasmine projects
                try {

                    // get the training data
                    trainingData = JasmineUtils.getTrainingData(jasmineTrainingProject);
                    trainingCount = trainingData.size();

                    if (trainingData.size() == 0) {
                        e.fatal("No shapes defined - GP cannot proceed without training data.");
                    }

                    // and test data
                    if (jasmineTestProject != null) {
                        testData = JasmineUtils.getTrainingData(jasmineTestProject);
                        testCount = testData.size();
                    }

                    // find how many classes the training data contains
                    distinctClasses = JasmineUtils.getDistinctClasses(trainingData);


                } catch (Exception err) {
                    e.fatal("GP system cannot load Jasmine project: " + err.toString());
                }

                break;

            case MODE_CSV:

                // load the data from the CSV files
                try {

                    CSVFeature.loadTrainingData(csvTrainingFile);

                    if (csvTestFile != null)
                        CSVFeature.loadTestData(csvTestFile);

                    // CSV feature makes other useful calculations that we need
                    trainingCount = CSVFeature.getTrainingDataSize();
                    testCount = CSVFeature.getTestDataSize();

                    distinctClasses = CSVFeature.getDistinctClasses();

                } catch (Exception err) {
                    System.out.println(err.toString());
                    e.fatal("GP system cannot load data from CSV: " + err.toString());
                }

                break;

            default:

                e.fatal("Invalid operation more for classification problem.");

        }

        // calculate the highest class ID. Useful to know if you want
        // to create an associative array to accommodate all the classes.
        for (int i = 0; i < distinctClasses.size(); i++) {
            Integer classID = distinctClasses.elementAt(i);
            if (classID > highestClassID) highestClassID = classID;
        }

        loadedAlready = true;

    }

    /**
     * Ensures that only a maximum number of data is used.
     *
     * @param size
     */
    public void pruneTrainingSize(int size) {

        if (trainingCount == 0) {
            throw new RuntimeException("No data to prune!");
        }

        switch (mode) {
            case MODE_JASMINE:
                System.err.println("Feature not supported yet");
                break;
            case MODE_CSV:
                CSVFeature.pruneTrainingSize(size);
                trainingCount = CSVFeature.getTrainingDataSize();
                System.err.println("Pruned training data to " + trainingCount);
                break;
        }

    }


    /**
     * Returns how many items of training data there are.
     */
    public int getTrainingCount() {
        return trainingCount;
    }

    /**
     * Returns how many items of test data there are.
     *
     * @return
     */
    public int getTestCount() {
        return testCount;
    }

    /**
     * Puts the piece of training data at the given index onto the DataStack
     * or otherwise makes it available to the GP system. We encapsulate this inside
     * its own method so the source of data: Jasmine or CSV is transparent to the
     * GP problems.
     */
    public void setupDataStackForTraining(DataStack data, int index) {

        switch (mode) {
            case MODE_JASMINE:
                data.setData(trainingData.elementAt(index));
                break;
            case MODE_CSV:
                CSVFeature.setTrainingMode(true);
                CSVFeature.setRow(index);
                break;
        }

    }

    /**
     * Puts the piece of test data at the given index onto the DataStack
     * or otherwise makes it available to the GP system. We encapsulate this inside
     * its own method so the source of data: Jasmine or CSV is transparent to the
     * GP problems.
     */
    public void setupDataStackForTesting(DataStack data, int index) {

        switch (mode) {
            case MODE_JASMINE:
                data.setData(testData.elementAt(index));
                break;
            case MODE_CSV:
                CSVFeature.setTrainingMode(false);
                CSVFeature.setRow(index);
                break;
        }

    }

    /**
     * Allows access to the classID of training data at a given index.
     */
    public int getTrainingClassID(int index) {
        switch (mode) {
            case MODE_JASMINE:
                return trainingData.elementAt(index).getClassID();
            case MODE_CSV:
                return CSVFeature.getTrainingClassID(index);
        }
        return -1;
    }

    /**
     * Allows access to the classID of training data at a given index.
     */
    public int getTestClassID(int index) {
        switch (mode) {
            case MODE_JASMINE:
                return testData.elementAt(index).getClassID();
            case MODE_CSV:
                return CSVFeature.getTestClassID(index);
        }
        return -1;
    }

    /**
     * Returns the classname of the class with a particular ID.
     */
    public String getClassName(int classID) {
        switch (mode) {
            case MODE_JASMINE:
                return jasmineTrainingProject.getShapeClass(classID).name;
            case MODE_CSV:
                return "class" + classID;
        }
        return "-error-";
    }

    /**
     * Gets the features that a shape problem is permitted to use in classification
     *
     * @return
     */
    public Vector<Terminal> getFeatures() {

        Vector<Terminal> featureList = new Vector<Terminal>();

        switch (mode) {

            case MODE_JASMINE:

                featureList.add(new Corners());
                featureList.add(new CountHollows());
                featureList.add(new BalanceX());
                featureList.add(new BalanceY());
                featureList.add(new Density());
                featureList.add(new AspectRatio());
                featureList.add(new Joints());
                featureList.add(new Ends());
                featureList.add(new Roundness());
                featureList.add(new Roughness());
                featureList.add(new BalanceXEnds());
                featureList.add(new BalanceYEnds());
                featureList.add(new ClosestEndToCog());
                featureList.add(new ClosestPixelToCog());
                featureList.add(new HorizontalSymmetry());
                featureList.add(new VerticalSymmetry());
                featureList.add(new InverseHorizontalSymmetry());
                featureList.add(new InverseVerticalSymmetry());

                break;

            case MODE_CSV:

                for (int i = 0; i < CSVFeature.getColumnCount() - 1; i++) {
                    featureList.add(new CSVFeature(i));
                }

        }

        return featureList;

    }

    /**
     * Sets the weights: allows AdaBoost to control the learning process.
     *
     * @param weights
     */
    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    /**
     * Returns the value of the highest class ID. This is useful if you want to know
     * how big to create an associative array to store some information about each classID.
     *
     * @return
     */
    public int getHighestClassID() {
        return highestClassID;
    }

    /**
     * Returns the number of classes in the problem.
     */
    public int getClassCount() {
        return distinctClasses.size();
    }

    /**
     * Returns a vector of the classIDs
     *
     * @return
     */
    public Vector<Integer> getClasses() {
        return distinctClasses;
    }

    /**
     * Returns the name of the problem
     */
    public String getName() {
        return name;
    }

    /**
     * Here only for compatibility with AdaBoost.
     * TODO: Remove.
     *
     * @return
     */
    public JasmineProject getProject() {
        return jasmineTrainingProject;
    }

    /**
     * Abstract method which needs to be implemented -
     * Do whatever it is that needs to be done to an individual to get a classification
     * And return it.
     */
    public abstract int execute(Individual ind, DataStack data);

    public double[] test(Individual ind)  {
        return test(ind, new DataStack());
    }

    /**
     * Prints out the results of testing the individual on unseen data.
     *
     * @return An array of results, [0] = training percentage, [1] = test percentage (0 if no test data)
     */
    public double[] test(Individual ind, DataStack data) {

        DecimalFormat f = new DecimalFormat("0.00");

        int trainingTP = 0;

        for (int j = 0; j < getTrainingCount(); j++) {

            setupDataStackForTraining(data, j);

            // run the individual
            int classID = execute(ind, data);

            if (classID == getTrainingClassID(j)) trainingTP++;

        }

        double trainingPercentage = (trainingTP / (double) getTrainingCount()) * 100;

        if (verbose) System.out.println("TRAINING HITS: " + trainingTP + " / " + getTrainingCount() + " (" + f.format(trainingPercentage) + "%)");

        if (getTestCount() > 0) {

            int unseenTP = 0;

            for (int j = 0; j < getTestCount(); j++) {

                setupDataStackForTesting(data, j);

                // run the individual
                int classID = execute(ind, data);

                if (classID == getTestClassID(j)) unseenTP++;

            }

            double testPercentage = (unseenTP / (double) getTestCount()) * 100;

            if (verbose) System.out.println("TEST HITS: " + unseenTP + " / " + getTestCount() + " (" + f.format(testPercentage) + "%)");

            return new double[]{trainingPercentage, testPercentage};

        }

        return new double[]{trainingPercentage, 0};

    }

    /**
     * Produces a class results object which summarises the ability of the classifier
     * on a per-class basis.
     */
    public ClassResults describe(GPActionListener gpActionListener, Individual ind, DataStack data, int index) {

        ClassResults results = new ClassResults();

        int hits = 0;

        for (int i = 0; i < getTrainingCount(); i++) {

            // set up the image on the stack
            setupDataStackForTraining(data, i);

            int classID = getTrainingClassID(i);

            if (results.getClassResult(classID) == null) {
                // if not added, add class to the class results data structure
                results.addClass(getClassName(classID), classID);
            }

            // run the individual
            int result = execute(ind, data);

            if (result != classID) {
                results.addMiss(classID);
            } else {
                results.addHit(classID);
                hits++;
            }

        }

        if (ind != null) {
            if (hits != ind.getHits()) {
                //System.err.println("// Wrong hits value: Should be " + hits + " but is " + ind.getHits());
            }
            ind.setHits(hits);

            test(ind, data);
        }

        return results;

    }

    private int uniqueRangeID = 100;

    /**
     * Allows a terminal to have an ERC custom built for it.
     *
     * @param params
     * @param n
     */
    public void registerTerminal(GPParams params, Terminal n) {
/*        if (params.isAutomaticRangeTypingEnabled()) {
            AutorangeERC erc = new AutorangeERC(params, uniqueRangeID);
            // now go through the training data and initialise the ERC with all the data
            DataStack data = new DataStack();
            for (int i = 0; i < getTrainingCount(); i++) {
                setupDataStackForTraining(data, i);
                erc.addData(n.execute(data));
            }
            uniqueRangeID++;
            params.registerNode(erc);
            n.setRangeID(erc.getRangeID());
        }*/
        params.registerNode(n);
    }

}
