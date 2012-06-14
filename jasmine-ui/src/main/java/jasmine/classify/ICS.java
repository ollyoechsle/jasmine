package jasmine.classify;


import jasmine.classify.classifier.Classifier;
import jasmine.classify.classifier.ClassifierFusion;
import jasmine.classify.classifier.GPClassifier;
import jasmine.classify.classifier.GPOneClassClassificationProblem;
import jasmine.classify.classifier.MulticlassClassifier;
import jasmine.classify.classifier.NearestNeighbourClassifier;
import jasmine.classify.classifier.ProblemSettings;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataNormaliser;
import jasmine.classify.data.DataStatistics;
import jasmine.classify.data.TrainingDataStore;
import jasmine.classify.evaluation.ClassifierTestResults;
import jasmine.classify.evaluation.ClassifierTester;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.ConsoleListener;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.VarianceThreshold;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;
import jasmine.gp.util.DeepCopy;

import java.util.Vector;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.io.IOException;

/**
 * Non parallel version of the superclassifier, for use in experiments.
 */
public class ICS {

    public static final int BINARY_DECOMPOSITION_ONLY = 1;
    public static final int REMOVE_CLASSES = 2;
    public static final int REMOVE_CLASSES_AND_ORDER_BY_EASIEST = 3;
    public static final int REMOVE_CLASSES_AND_ORDER_BY_HARDEST = 4;
    public static int MODE = REMOVE_CLASSES_AND_ORDER_BY_EASIEST;

    public static boolean SILENT = false;
    public static boolean NORMALISE = true;
    public static int RUNS = 5;
    public static int TIME_PER_RUN = 300;
    public static final int[] TOURNAMENT_SIZES = new int[]{2,5,7};
    public static int RANDOM_SEED = 9876;
    public static  int ISLAND_COUNT = 7;
    public static  int[] DRS_TYPES = new int[]{BetterDRS.TYPE};

    public static final String VERSION = "SUPER CLASSIFIER V3.1";

    protected Vector<Data> trainingData, testingData;

    protected DataStatistics trainingStatistics, testingStatistics;

    protected int maxTraining, maxTesting;

    public volatile MulticlassClassifier multiclassClassifier;

    TrainingDataStore store = new TrainingDataStore();

    protected ICSListener listener;

    public void addListener(ICSListener listener) {
        this.listener = listener;
        if (listener != null) listener.ics = this;
    }

//    public static void main(String[] args) throws IOException {
//        //CSVDataReader training = new CSVDataReader(new File("c:\\PhD\\jasmine-data\\sat-training.ssv"));
//        //CSVDataReader testing = new CSVDataReader(new File("c:\\PhD\\jasmine-data\\sat-test.ssv"));
//
//        //CSVDataReader training = new CSVDataReader(new File("C:\\PhD\\datasets2\\anpr\\ANPR_sub_objects.csv"));
//        //CSVDataReader training = new CSVDataReader(new File("C:\\car\\coil-100\\coil-100\\COIL-100.csv"));
//        CSVDataReader training = new CSVDataReader(new File("C:\\PhD\\datasets2\\MNIST\\mnist_valid_features.csv"));
//        CSVDataReader testing = new CSVDataReader(new File("C:\\PhD\\datasets2\\MNIST\\mnist_test_features.csv"));
//        //CSVDataReader skin = new CSVDataReader(new File("C:\\PhD\\datasets2\\lesions.csv"));
//        //DataSet ds = new TrainTestDataSet("ANPR", training, training);
//
//        ICS s = new ICS();
//        s.addListener(new ICSListenerGraphical());
//        Vector<Data> d = training.getData();
//        s.generateClassifier(d,testing.getData());
//    }

    public void setRandomSeed(int seed) {
        RANDOM_SEED = seed;
    }

    public Classifier generateClassifier(Vector<Data> train, Vector<Data> test) {

        this.trainingData = train;
        this.testingData = test;

        out(VERSION);
        if (listener != null) listener.onStart();
        long start = System.currentTimeMillis();


        if (trainingData == null) {
            err("Training/Testing data not set up!");
            throw new RuntimeException("Training/Testing data not set up");
        }

        status("Loading the data");

        // Establish the number of classes and class frequencies
        //POEY comment: count objects and objects in each class
        trainingStatistics = new DataStatistics(trainingData);
        testingStatistics = new DataStatistics(testingData);

        // automatically choose the appropriate DRS type
        if (trainingStatistics.getClassCount() <= 2) {
            DRS_TYPES = new int[]{EntropyThreshold.TYPE, VarianceThreshold.TYPE};
        } else {
            DRS_TYPES = new int[]{BetterDRS.TYPE};
        }

        status("Assessing class difficulty");

        // Search for additional features in the data using Principle Components Analysis
        // Not implemented yet

        // Generate a confusion matrix of the data using K-means clustering
        Vector<Data> knnTraining = trainingData;
        //POEY comment: knnTraining.size() = the number of objects
        if (knnTraining.size() > 5000) {
            knnTraining = new Vector<Data>();
            for (int i = 0; i < 5000; i++) {
                knnTraining.add(trainingData.elementAt(i));
            }            
        }
        	
        //POEY comment: knn contains calculated values and classID of objects
        NearestNeighbourClassifier knn = new NearestNeighbourClassifier(knnTraining);
        Vector<ClassifierTestResults> testResults = new Vector<ClassifierTestResults>(trainingStatistics.getClassCount());

        // Go through each class and get the results
        for (int i = 0; i < trainingStatistics.getClassIDs().size(); i++) {
            int classID = trainingStatistics.getClassIDs().elementAt(i);
            out("Running k-nearest neighbour on class: " + classID);
            //POEY comment: testBinarySingleClass returns the classified results including a percentage value of TP/N
            //using the k-nearest neighbour method
            testResults.add(ClassifierTester.testBinarySingleClass(knn, testingData, classID, false));
        }

        status("Getting Confusion Matrix");

        ClassifierTestResults r = ClassifierTester.testMulticlass(trainingStatistics.getClassIDs(), knn, testingData);
        //POEY comment: print classID and the number of objects in a matrix format
        if (!SILENT) r.printConfusionMatrix();

        // Normalise the data, do this after KNN because it makes it slightly worse in some cases.
        if (NORMALISE) {	//POEY comment: not this case
            status("Normalising data");
            DataNormaliser normaliser = new DataNormaliser(trainingData);
            normaliser.normalise(trainingData);
            normaliser.normalise(testingData);
        }

        // Now, decide which classes to evolve first, and how to evolve them
        // Start by ordering them by how easy they are to solve by k means
        // Now, decide which classes to evolve first, and how to evolve them
        // Start by ordering them by how easy they are to solve by k means
        
        switch (MODE) {
            case REMOVE_CLASSES_AND_ORDER_BY_EASIEST:	//POYE comment: this case
                Collections.sort(testResults);
                break;
            case REMOVE_CLASSES_AND_ORDER_BY_HARDEST:
                Collections.sort(testResults);
                Collections.reverse(testResults);
                break;
            case REMOVE_CLASSES:
                Collections.shuffle(testResults, new Random(RANDOM_SEED));
                break;
        }


        // Start the classifier
        multiclassClassifier = new MulticlassClassifier(trainingStatistics.getClassCount());

        Vector<Data> trainingDataClone = (Vector<Data>) new DeepCopy().copy(this.trainingData);

        if (listener != null) listener.setNumClasses(testResults.size() - 1);

        // Learn each class in turn
        //POEY comment: testResults contain accuracy of classified results of each class
        //testResults.size() = the number of classes
        for (int i = 0; i < testResults.size(); i++) {
            ClassifierTestResults classifierTestResults = testResults.elementAt(i);

            int classID = classifierTestResults.getClassID();

            // create all the tasks

            if (i < testResults.size() - 1) {

                status("Classifying Class " + classID + ", e=" + (int) classifierTestResults.getErrorEstimate());

                Vector<Data> trainingDataCopy = (Vector<Data>) new DeepCopy().copy(trainingDataClone);

                // set classIDs to either classID OR 0.
                updateClasses(trainingDataCopy, classID);

                // store this data for later
                store.put(classID, trainingDataCopy);

                // learn the classification
                learnToClassify(classID, i);

                // remove classes from the main data
                removeClasses(trainingDataClone, classID);

            } else {
                // the hardest class, just set this as the default

                //out("Default Class " + classID);

                // Let us see what doesn't get classified in the end
                int defaultHits = 0;
                Vector<Data> finalTrainingData = new Vector<Data>();
                for (int j = 0; j < trainingData.size(); j++) {
                    Data data = trainingData.elementAt(j);
                    if (multiclassClassifier.classify(data) != data.classID) {
                        finalTrainingData.add(data);
                    }
                    if (data.classID == multiclassClassifier.defaultClass) defaultHits++;
                }

                multiclassClassifier.defaultClass = classifierTestResults.getClassID();

                // evolve a multiclass classifier for this.
                out("Final training size: " + finalTrainingData.size());

            }

        }

        // Print the results and finish.
        printClassifierResults();

        long time = System.currentTimeMillis() - start;

        status("Finished, after " + time + "ms.");

        if (listener != null) {
            listener.saveIndividual();
            listener.onFinish();
        }

        return multiclassClassifier;



    }

    public void updateClasses(Vector<Data> trainingDataCopy, int classID) {
        for (int i = 0; i < trainingDataCopy.size(); i++) {
            Data data = trainingDataCopy.elementAt(i);
            if (data.classID != classID) data.classID = 0;	//POEY comment: this case
        }
    }

    public void learnToClassify(int classID, int index) {

        Vector<Data> trainingData = store.get(classID);

        Vector<Classifier> individuals = new Vector<Classifier>(RUNS);

        Vector<Integer> classIDs = new Vector<Integer>();
        classIDs.add(classID);
        classIDs.add(0);

        // run several times.
        //POEY comment: RUNS = 5
        for (int run = 0; run < RUNS; run++) {

            // learn how to classify this class versus all the others
            out("Learning to classify: " + classID);
            ProblemSettings settings = getProblemSettings();
            GPOneClassClassificationProblem p = new GPOneClassClassificationProblem(classID, settings, trainingData);
            //POEY comment: c is the best individual
            Classifier c = runProblem(p, trainingData, classIDs);
            individuals.add(c);
            // don't needlessly re-run if it gets a perfect score
            double fitness = getKozaFitness(c, trainingData);
            if (fitness == 0) break;

        }

        // From the runs, choose the best.
        Classifier best = null;
        double lowestFitness = Double.MAX_VALUE;
        //POEY comment: individuals.size() = 1
        for (int i = 0; i < individuals.size(); i++) {
            Classifier classifier = individuals.elementAt(i);
            double fitness = getKozaFitness(classifier, trainingData);
            if (fitness < lowestFitness) {
                lowestFitness = fitness;
                best = classifier;
            }
        }

        out("The best classifier is: " + best + ", achieving error: " + lowestFitness);
        if (listener != null) {
            listener.onLearnNewClass(classID, lowestFitness);
        }

        // add the classifier to the multiclassifier
        multiclassClassifier.set(best, index);

        // test the multiclass classifier
        printClassifierResults();

    }

    /**
     * Uses fusion and whatever other tricks there are to make the best of the classifier.
     */
    public Classifier getBestPossibleClassifier(GPClassifier[] classifiers, Vector<Data> trainingData, Vector<Integer> classIDs) {

        // if there is only one individual we don't have any choice
    	//POEY comment: classifiers.length = the number of the best individual in each genteration
        if (classifiers.length == 1) {
            double fitness = getKozaFitness(classifiers[0], trainingData);
            out("Best individual: " + fitness);
            return classifiers[0];
        }

        // looking for the best classifier
        Classifier best = null;
        double lowestFitness = Double.MAX_VALUE;

        // look at classifiers individually
        for (int j = 0; j < classifiers.length; j++) {
            GPClassifier classifier = classifiers[j];
            double fitness = getKozaFitness(classifier, trainingData);
            if (fitness < lowestFitness) {
                lowestFitness = fitness;
                best = classifier;
            }
        }

        out("Best single: " + lowestFitness);

        // set up the classifier fusion
        ClassifierFusion fuser = new ClassifierFusion(classIDs);
        for (int j = 0; j < classifiers.length; j++) {
            GPClassifier classifier = classifiers[j];
            fuser.add(classifier);
        }

        boolean weightByFitness = false;

        // majority classification, using n classifiers
        fuser.setMode(ClassifierFusion.MAJORITY_VOTE, weightByFitness);
        for (int committeeSize = 2; committeeSize < classifiers.length; committeeSize++) {
            fuser.tryHits(trainingData, null, committeeSize);
            ClassifierFusion majorityClassifier = new ClassifierFusion(classIDs, fuser.bestClassifiers, ClassifierFusion.MAJORITY_VOTE);
            majorityClassifier.setMode(ClassifierFusion.MAJORITY_VOTE, weightByFitness);

            double fitness = getKozaFitness(majorityClassifier, trainingData);
            if (fitness < lowestFitness) {
                lowestFitness = fitness;
                best = majorityClassifier;
            }
        }

        // committee classification, using three classifiers
        fuser.setMode(ClassifierFusion.COMMITTEE_VOTE, weightByFitness);
        for (int committeeSize = 2; committeeSize < classifiers.length; committeeSize++) {
            fuser.tryHits(trainingData, null, committeeSize);
            ClassifierFusion committeeClassifier = new ClassifierFusion(classIDs, fuser.bestClassifiers, ClassifierFusion.COMMITTEE_VOTE);
            committeeClassifier.setMode(ClassifierFusion.COMMITTEE_VOTE, weightByFitness);

            double fitness = getKozaFitness(committeeClassifier, trainingData);
            if (fitness < lowestFitness) {
                lowestFitness = fitness;
                best = committeeClassifier;
            }
        }

        weightByFitness = true;

        // majority classification, using n classifiers
        fuser.setMode(ClassifierFusion.MAJORITY_VOTE, weightByFitness);
        for (int committeeSize = 2; committeeSize < classifiers.length; committeeSize++) {
            fuser.tryHits(trainingData, null, committeeSize);
            ClassifierFusion majorityClassifier = new ClassifierFusion(classIDs, fuser.bestClassifiers, ClassifierFusion.MAJORITY_VOTE);
            majorityClassifier.setMode(ClassifierFusion.MAJORITY_VOTE, weightByFitness);

            double fitness = getKozaFitness(majorityClassifier, trainingData);
            if (fitness < lowestFitness) {
                lowestFitness = fitness;
                best = majorityClassifier;
            }
        }

        // committe classification, using three classifiers
        fuser.setMode(ClassifierFusion.COMMITTEE_VOTE, weightByFitness);
        for (int committeeSize = 2; committeeSize < classifiers.length; committeeSize++) {
            fuser.tryHits(trainingData, null, committeeSize);
            ClassifierFusion committeeClassifier = new ClassifierFusion(classIDs, fuser.bestClassifiers, ClassifierFusion.COMMITTEE_VOTE);
            committeeClassifier.setMode(ClassifierFusion.COMMITTEE_VOTE, weightByFitness);

            double fitness = getKozaFitness(committeeClassifier, trainingData);
            if (fitness < lowestFitness) {
                lowestFitness = fitness;
                best = committeeClassifier;
            }
        }

        out("Best overall: " + lowestFitness);

        return best;
    }

    public Classifier runProblem(Problem p, Vector<Data> trainingData, Vector<Integer> classIDs) {
        Evolve e = new Evolve(p, new ConsoleListener(ConsoleListener.SILENT));
        e.run();
        Individual[] bestIndividuals = e.getBestIndividuals();
        if (p instanceof GPOneClassClassificationProblem) {
            for (int i = 0; i < bestIndividuals.length; i++) {
                Individual ind = bestIndividuals[i];
                ((GPOneClassClassificationProblem) p).ensureHasPCM(ind, e);
            }
        }
        GPClassifier[] classifiers = new GPClassifier[bestIndividuals.length];
        for (int i = 0; i < bestIndividuals.length; i++) {
            Individual bestIndividual = bestIndividuals[i];
            p.evaluate(bestIndividual, new DataStack(), e);
            classifiers[i] = new GPClassifier(bestIndividual);

        }
        return getBestPossibleClassifier(classifiers, trainingData, classIDs);
    }

    public void printClassifierResults() {
        out("*****************************");
        float trainingResult = test(multiclassClassifier, trainingData);
        float testingResult = test(multiclassClassifier, testingData);
        out("Classifier Results: TRAINING: " + trainingResult);
        out("Classifier Results:  TESTING: " + testingResult);
        if (listener != null) listener.onClassifierUpdated(trainingResult, testingResult);
    }

    public synchronized float test(Classifier c, Vector<Data> data) {
        int N = 0;
        int TP = 0;
        //POEY comment: data.size() = the number of objects
        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);
            N++;
            if (c.classify(d) == d.classID) {
                TP++;
            }
        }
        return TP / (float) N;
    }

    public synchronized float getKozaFitness(Classifier c, Vector<Data> data) {
        int N = 0;
        int hits = 0;
        float FP = 0, FN = 0;
        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);
            if (d.weight == 0) continue;
            //POEY comment: jasmine.classify.classifier
            //maybe GPClassifier.java or ClassifierFusion.java
            int output = c.classify(d);
            N++;
            if (output == 0) {
                // returned false
                if (d.classID == 0) {
                    hits++;
                } else {
                    FN += d.weight;
                }
            } else {
                if (d.classID == 0) {
                    FP += d.weight;
                } else {
                    hits++;
                }
            }
        }
        // compute the fitness
        return (FP + FN) / (float) N;
    }

    public void removeClasses(Vector<Data> trainingData, int classID) {
        if (MODE == BINARY_DECOMPOSITION_ONLY) return;
        for (int i = 0; i < trainingData.size(); i++) {
            Data data = trainingData.elementAt(i);
            if (data.classID == classID) {
                data.weight = 0;
            }
        }
    }

    public void saveClassifier(File f) {
        multiclassClassifier.save(f);
    }

    public String getVersion() {
        return VERSION;
    }

    int tIndex = 0;
    int drsTypeIndex = 0;

    public ProblemSettings getProblemSettings() {

        // create the problem settings object
        ProblemSettings settings = new ProblemSettings(TIME_PER_RUN, RANDOM_SEED, TOURNAMENT_SIZES[tIndex]);

        settings.numIslands = ISLAND_COUNT;
        settings.DRSMethod = DRS_TYPES[drsTypeIndex];
        settings.generations = 1000000;

        // increment tournament counter
        tIndex++;
        if (tIndex >= TOURNAMENT_SIZES.length) tIndex = 0;
        
        drsTypeIndex++;
        if (drsTypeIndex >= DRS_TYPES.length) drsTypeIndex = 0;

        // change the random seed
        RANDOM_SEED++;

        // return the settings
        return settings;

    }

    public void status(String message) {
        if (listener != null) {
            listener.onStatusUpdate(message);
        }
    }

    public void out(String message) {
        if (!SILENT) {
            System.out.println(message);
        }
    }

    public void err(String message) {
        if (!SILENT) {
            System.err.println(message);
        }
    }

}