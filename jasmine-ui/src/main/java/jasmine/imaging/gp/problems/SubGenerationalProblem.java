package jasmine.imaging.gp.problems;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.interfaces.GraphicalListener;
import jasmine.gp.multiclass.TrainingClass;
import jasmine.gp.nodes.*;
import jasmine.gp.nodes.ercs.*;
import jasmine.gp.nodes.logic.*;
import jasmine.gp.nodes.math.Exp;
import jasmine.gp.nodes.math.Ln;
import jasmine.gp.params.ADFNodeConstraints;
import jasmine.gp.params.GPParams;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.gp.util.*;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.gp.individuals.SubGenerationalIndividual;
import jasmine.imaging.gp.individuals.WeakLearner;
import jasmine.javasource.JavaClass;
import jasmine.javasource.JavaMethod;
import jasmine.javasource.JavaVariable;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;
import java.io.File;
import java.text.DecimalFormat;

/**
 * The idea of this problem is not to generate a single solution, but to find a number of
 * unique, good solutions (weak retainedClassifiers)- the building blocks to solve the larger problem.
 * <p/>
 * Good retainedClassifiers are stored as they are discovered, with additional fitness criteria
 * being based on the uniqueness of the remaining retainedClassifiers. Thus a high performing classifier, once
 * chosen, will have its fitness decrease immediately afterward, allowing the GP problem
 * to discover multiple retainedClassifiers in a single GP simulation.
 * <p/>
 * These retainedClassifiers can be saved as ADFs and then used in a subsequent GP problem.
 *
 * @author Olly Oechsle, University of Essex, Date: 28-Mar-2007
 * @version 1.0
 */
public class SubGenerationalProblem extends BasicClassificationProblem {

    final double ALPHA = 1.0;
    final double BETA = 1.0;
    boolean silent = false;

    /**
     * Store retainedClassifiers here as they are evolved. They will then affect the fitness of the
     * population as no two retainedClassifiers which return the same result are allowed.
     */
    protected Vector<ADFNodeConstraints> retainedClassifiers;

    /**
     * Keeps track of which exact instances of training data have been solved.
     */
    protected Vector<TrainingClass> trainingClasses;

    /**
     * Strong classifier is created by this method, consisting of lots of smaller ones.
     */
    protected SubGenerationalIndividual strongClassifier;

    /**
     * The classifier will be written out to java. This helper class allows us to write
     * syntactically correct java code easily.
     */
    protected JavaClass javaSource;
    protected JavaMethod classifyMethod;

    /**
     * For every classifier stored, also store its results on the training set. This allows
     * us to compare if a classifier returns the same results or not. Clearly we only
     * want unique retainedClassifiers.
     */
    protected Vector<String> savedClassifierResults;


    /**
     * Classes for which there exists a classifier that can solve this particular class.
     */
    protected Vector<Integer> classesFullySolved;

    /**
     * Keep a vector of all the different shape classes
     * and how many instances of each exist
     */
    public Hashtable<Integer, Integer> classes;

    /**
     * Allows us to assign a unique ID to each ADF node
     */
    public long idCounter = 0;

    /**
     * Should this individual be written to java file?
     */
    protected boolean writeToFile = false;

    public PrintingBuffer buffer;
    StringBuffer executionOrderBuffer = new StringBuffer();

    protected String className;
    File outputFile;

//    public static void main(String[] args) throws Exception {
//
//
//        CSVFeature.NORMALISING = true;
//
//        final int MULTI_TEST = 1;
//        final int RUN_WITH_GUI = 2;
//
//        final int mode = MULTI_TEST;
//
//        if (mode == MULTI_TEST) {
//
//            final int RUNS = 1;
//
//            // RUNS it multiple times on the console.
//
//            System.out.println("Sub Generational GP, running on " + Evolve.APP_NAME);
//
//            GPActionListener silent = new jasmine.gp.interfaces.console.ConsoleListener(jasmine.gp.interfaces.console.ConsoleListener.LOW_VERBOSITY);
//
//            System.out.println("Training, Unseen, Time, Learners");
//
//            int highestUnseenTP = 0;
//            String best = null;
//
//            for (int j = 0; j < RUNS; j++) {
//
//                SubGenerationalProblem problem = new SubGenerationalProblem(TrainingData.training, TrainingData.testing, false);
//                Evolve e = new Evolve(problem, silent);
//                e.run();
//                SubGenerationalIndividual ind = problem.strongClassifier;
//
//                //int unseenTP = ind.test(unseen);
//                int unseenTP = problem.test(ind);
//
//                double unseenPercentage = (unseenTP / (double) problem.getTestCount()) * 100;
//                DecimalFormat f = new DecimalFormat("0.00");
//
//                System.err.println(ind.getHits() + ", " + unseenTP + "(" + f.format(unseenPercentage) + "%), " + e.getTotalTime() + ", " + ind.getWeakLearnerCount());
//                if (unseenTP > highestUnseenTP) {
//                    highestUnseenTP = unseenTP;
//                    best = ind.getHits() + ", " + unseenTP + "(" + f.format(unseenPercentage) + "%), " + e.getTotalTime() + ", " + ind.getWeakLearnerCount();
//                }
//                System.gc();
//
//            }
//
//            System.err.println("Best:\n" + best);
//
//        } else {
//
//            // Runs the problem once using the GUI.
//
//            SubGenerationalProblem problem = new SubGenerationalProblem(TrainingData.training, null, false);
//
//            Evolve e = new Evolve(problem, new GraphicalListener());
//            e.run();
//
//        }
//
//
//    }


    public int execute(Individual ind, DataStack data) {
        return (int) strongClassifier.execute(data);
    }


    public int test(SubGenerationalIndividual ind) {

        DataStack data = new DataStack();

        int score = 0;

        for (int i = 0; i < getTestCount(); i++) {

            setupDataStackForTesting(data, i);

            int classID = (int) ind.execute(data);

            if (classID == getTestClassID(i)) score++;

        }

        return score;

    }

    /**
     * Creates a new classifier picking problem. Training data and class information
     * comes from a Jasmine Project file. (Jasmine is part of the imaging library)
     */
    public SubGenerationalProblem(JasmineProject project, JasmineProject unseenProject, boolean writeToFile) {
        super(project, unseenProject);
        this.writeToFile = writeToFile;
        buffer = new PrintingBuffer(writeToFile);
    }

    public SubGenerationalProblem(File trainingFile, File unseenFile, boolean writeToFile) {
        super(trainingFile, unseenFile);
        this.writeToFile = writeToFile;
        buffer = new PrintingBuffer(writeToFile);
    }

    /**
     * Provides a name for the problem so that it can be identified via the
     * user interface.
     */
    public String getName() {
        return "Sub Generational GP";
    }

    protected int distinctClassCount;

    /**
     * Returns how many classes the problem must solve
     */
    public int getClassCount() {
        return distinctClassCount;
    }

    /**
     * Initialises the problem. This is where the training data is loaded
     * and the GP params object initialised with Nodes to use. The return
     * object should also be set up.
     */
    public void initialise(Evolve e, GPParams params) {

        // after loading training data, we need to specify which Nodes the classifier is permitted to use.
        loadData(e);

        pruneTrainingSize(100);

        // the retainedClassifiers we want to keep
        retainedClassifiers = new Vector<ADFNodeConstraints>(100);

        // keeps a record of which classes have been partly solved and which are fully solved
        trainingClasses = new Vector<TrainingClass>(100);

        // and the classifier results vectors (easiest way to ensure uniqueness)
        savedClassifierResults = new Vector<String>(100);

        // and which classes have been solved
        classesFullySolved = new Vector<Integer>(100);

        // initialise how many things are unsolved.
        totalInstancesUnsolved = getTrainingCount();

        // the number of distinct classes there are
        distinctClassCount = 0;

        try {

            // keep a record of every distinct classID and record how many instances of
            // each classID there are.
            classes = new Hashtable<Integer, Integer>(100);

            for (int i = 0; i < getTrainingCount(); i++) {

                int classID = getTrainingClassID(i);

                try {

                    // add a reference to each classID only once
                    Integer classCount = classes.get(classID);

                    if (classCount == null) {
                        // class isn't in the hashtable yet - create a new entry
                        classes.put(classID, 1);
                        distinctClassCount++;
                    } else {
                        // class is already in the hashtable - increment the class count
                        classes.put(classID, classCount + 1);
                    }

                    TrainingClass trainingClass = getTrainingClass(classID);

                    if (trainingClass == null) {
                        trainingClass = new TrainingClass(classID);
                        trainingClasses.add(trainingClass);
                    }

                    // say there is an instance of this class at the index i in the training data.
                    trainingClass.addInstance(i);
                } catch (Exception err) {
                    // Could not initialise shape, don't worry too much
                    System.err.println(err.toString());
                }

            }

            // halt if there isn't sufficient data
            if (getTrainingCount() == 0) {
                e.fatal("No shapes defined - GP cannot proceed without training data.");
            }

            // use the distinct classes to setup the Return ERC. The return ERC is a special
            // ERC that returns classIDs. So that the GP works effectively, the ReturnERC needs
            // to be given all the classIDs so it doesn't return meaningless classes.
            Return.classes = new int[distinctClassCount];
            Enumeration<Integer> classIDs = classes.keys();
            // allow return the "I don't know" option.
            int i = 0;
            while (classIDs.hasMoreElements()) {
                Integer classID = classIDs.nextElement();
                Return.classes[i] = classID;
                i++;
            }

        } catch (Exception err) {
            e.fatal("GP system cannot load Jasmine project: " + err.toString());
            err.printStackTrace();
        }

        params.registerNode(new If());

        // boolean functions
        params.registerNode(new More());
        params.registerNode(new Less());
        params.registerNode(new Equals());
        params.registerNode(new Between());

        params.registerNode(new AND());
        params.registerNode(new OR());
        params.registerNode(new NOT());
        params.registerNode(new NAND());
        params.registerNode(new NOR());
        params.registerNode(new BoolERC());

        params.registerNode(new Add());
        params.registerNode(new Mul());
        params.registerNode(new Sub());
        params.registerNode(new Div());

        params.registerNode(new Mean());
        params.registerNode(new PercentDiff());

        params.registerNode(new Ln());
        params.registerNode(new Exp());

        //params.registerNode(new Mean());

        //params.setAutomaticRangeTypingEnabled(true);

        // range typed ERCs
        if (!params.isAutomaticRangeTypingEnabled()) {
            //params.registerNode(new SmallIntERC());
            //params.registerNode(new SmallDoubleERC());
            //params.registerNode(new TinyDoubleERC());
            params.registerNode(new PercentageERC());
            //params.registerNode(new LargeIntERC());
        }

        // shape attributes
        Vector<Terminal> features = getFeatures();
        System.out.println("There are " + features.size() + " features");
        for (int i = 0; i < features.size(); i++) {
            registerTerminal(params, features.elementAt(i));
        }

        // as each of these is a binary classifier, we need to specify that boolean values are to be returned
        params.setReturnType(NodeConstraints.BOOLEAN);

        className = getName().replaceAll(" ", "_") + System.currentTimeMillis();

        javaSource = new JavaClass(className);

/*        if (writeToFile) {

            try {

                outputFile = new File("/home/ooechs/ecj-imaging/src/ac/essex/ooechs/imaging/commons/apps/jasmine/results/" + className + ".sxgp");

                System.setOut(new PrintStream(new FileOutputStream(outputFile, true)));

            } catch (FileNotFoundException err) {

                err.printStackTrace();
                System.exit(1);

            }

        }*/

        javaSource.setPackage("ac.essex.ooechs.imaging.commons.apps.jasmine.results");
        javaSource.addImport("ac.essex.ooechs.imaging.commons.apps.shapes.ExtraShapeData");
        javaSource.setExtends("SubObjectClassifier");

        javaSource.javadoc.addLine(getName() + "Classifier. This program was evolved automatically using SXGP.");
        javaSource.javadoc.setAuthor("Olly Oechsle, University of Essex, " + new Date().toString());
        javaSource.javadoc.setVersion("1.0");

        javaSource.addVariable(new JavaVariable("ExtraShapeData", "shape"));

        classifyMethod = new JavaMethod("public int classify(ExtraShapeData shape)");
        classifyMethod.addLine("this.shape = shape;");

        javaSource.addMethod(classifyMethod);

        strongClassifier = new SubGenerationalIndividual(javaSource, NodeConstraints.NUMBER);

    }

    public void customiseParameters(GPParams params) {
        // this enables the range typing so that different functions are tied with certain ERCs
        // this is only applicable to functions which implement the constraints mechanism, which less(), more(), between() and equals() do.
        params.setNodeChildConstraintsEnabled(true);

        params.setPointMutationProbability(0.25);
        params.setCrossoverProbability(0.50);

        // make sure that jittering and erc mutation are likely
        params.setERCjitterProbability(0.25);
        params.setERCmutateProbability(0.25);

        // elites count for nothing in this scenario
        params.setEliteCount(0);

        // keep the partial solutions small
        //params.setMaxTreeSize(50);

        // we want to use numeric ERCs but not numeric functions, so ensure there are no errors
        params.setIgnoreNonTerminalWarnings(true);

        // don't take too long
        params.setGenerations(50);

        // use a large initial population size so all different structures may be evaluated.
        params.setPopulationSize(2500);
    }

    /**
     * Returns a training class data structure associated with a given classID.
     */
    public TrainingClass getTrainingClass(int classID) {
        for (int i = 0; i < trainingClasses.size(); i++) {
            TrainingClass c = trainingClasses.elementAt(i);
            if (c.classID == classID) return c;
        }
        return null;
    }

    /**
     * Returns the strong classifier that this problem produces.
     */
    public SubGenerationalIndividual getStrongClassifier() {
        return strongClassifier;
    }

    /**
     * There is only one individual - the strong classifier. Override the class and return that instead
     * of the best of the weak classifiers.
     */
    public Individual getBestIndividual(Vector<Individual> sortedPopulation) {
        return strongClassifier;
    }

    /**
     * Evaluates a single individual, fitness should be assigned using the
     * setKozaFitness() method on the individual.
     */
    public void evaluate(Individual ind, DataStack data, Evolve e) {
    	
        boolean classifierWasAdded = false;

        totalIndividuals++;

        // The Evolve class passes the problem a single individual to be evaluated.
        // The individual must be evaluated against each item in the training data.
        // We'll store the results of the evaluation (which is a sequence of true/false decisions)
        // We'll encode this in a string
        String results = "";

        int returnedTrue = 0;
        int returnedFalse = 0;

        // note exactly which classes were returned true, and how many times this occurred per class.
        Hashtable<Integer, Integer> classesReturnedTrue = new Hashtable<Integer, Integer>(getTrainingCount());
        Hashtable<Integer, Integer> classesReturnedFalse = new Hashtable<Integer, Integer>(getTrainingCount());

        Vector<Integer> indexesReturnedTrue = new Vector<Integer>(50);
        Vector<Integer> indexesReturnedFalse = new Vector<Integer>(50);

        // iterate through each item of training data
        for (int i = 0; i < getTrainingCount(); i++) {

            // the data stack allows the individual access to data, so we'll put the shape onto the stack
            setupDataStackForTraining(data, i);

            // execute the individual
            boolean result = ind.execute(data) == 1;

            int classID = getTrainingClassID(i);

            // record the number of TPs;FPs
            if (result) {
                returnedTrue++;
                indexesReturnedTrue.add(i);
                Integer timesReturnedThisClassTrue = classesReturnedTrue.get(classID);
                if (timesReturnedThisClassTrue == null) {
                    classesReturnedTrue.put(classID, 1);
                } else {
                    classesReturnedTrue.put(classID, timesReturnedThisClassTrue + 1);
                }
            } else {
                returnedFalse++;
                indexesReturnedFalse.add(i);
                Integer timesReturnedThisClassFalse = classesReturnedFalse.get(classID);
                if (timesReturnedThisClassFalse == null) {
                    classesReturnedFalse.put(classID, 1);
                } else {
                    classesReturnedFalse.put(classID, timesReturnedThisClassFalse + 1);
                }
            }

            // record the result
            results += result ? "1" : "0";

        }

        // now we've evaluated the individual it is time to look at the results more closely.
        // our first criterion is that the individual be unique - that is it produces a different set of results
        // to any individual that we have already got.

        boolean isUnique = !savedClassifierResults.contains(results);

        if (!isUnique) {
            totalNonUniqueClassifiers++;
        }

        // clearly it needs to be able to discriminate, otherwise it is worthless
        boolean cantDiscriminate = returnedTrue == 0 || returnedFalse == 0;

        if (cantDiscriminate) {
            totalNonDiscriminatingClassifiers++;
        }

        // as well as understanding the classifer's uniqueness, we'd also like to see how
        // many different classes the classifier is able to find.

        Enumeration<Integer> classEnumeration;

        boolean invert = false;
        if (returnedTrue < returnedFalse) {
            classEnumeration = classesReturnedTrue.keys();
        } else {
            classEnumeration = classesReturnedFalse.keys();
            invert = true;
            indexesReturnedTrue = indexesReturnedFalse;
        }

        // since enumerations are difficult to manage (not easy to iterate through them multiple times)
        // we'll convert the data into a vector, called classesIdentified
        Vector<Integer> classesIdentified = new Vector<Integer>(getTrainingCount());
        while (classEnumeration.hasMoreElements()) {
            classesIdentified.add(classEnumeration.nextElement());
        }

        double bestFitness = Integer.MAX_VALUE;

        // Iterate through each class, and for each one
        // calculate its fitness as a classifier for that particular class.
        // Assign the individual whatever fitness is the lowest

        if (isUnique && !cantDiscriminate) {

            for (int i = 0; i < classesIdentified.size(); i++) {
                double TP = 0;
                double FP = 0;
                double totalTP;

                int currentClassID = classesIdentified.elementAt(i);

                // Get some data about this class
                TrainingClass c = getTrainingClass(currentClassID);

                // If the class has already been solved we don't need a classifier
                // to solve it again.
                if (c.isFullySolved()) {
                    continue;
                }

                // The total TP is the unsolved instances that this classifier needs
                // TODO - Have this add up all the weights of unsolved classes (Training Data Class)
                totalTP = c.getUnsolvedCount();

                // Now iterate through every instance for which this individual returned TRUE (we are finding TP/FP only)
                for (int j = 0; j < indexesReturnedTrue.size(); j++) {

                    // the training dataID that was returned true
                    int trainingDataID = indexesReturnedTrue.elementAt(j);

                    // get the associated classID of this training data
                    int dataClassID = getTrainingClassID(trainingDataID);

                    // if this is an instance of the current class
                    if (dataClassID == currentClassID) {

                        // this data is an instance of the current class

                        // if it has NOT been solved yet, award a TP
                        if (!c.isSolved(trainingDataID)) {
                            // TODO: Add up the weight if necessary.
                            TP++;
                        }

                    } else {

                        // this data is NOT an instance of the current class

                        // if this class has NOT been fully solved, this is a false positive
                        if (!getTrainingClass(dataClassID).fullySolved) {
                            FP++;
                        }

                    }


                }

                // make sure TP is at least one, otherwise the fitness will be zero which is the ideal fitness.
                if (TP > 0) {

                    // calculate the fitness
                    // invert fitness to make it koza fitness
                    double fitness = 1 / ((TP * ALPHA) / (totalTP + (FP * BETA)));

                    // fitness: reduce errors
                    fitness = FP;

                    // keep the lowest (best) fitness
                    if (fitness < bestFitness) {
                        bestFitness = fitness;
                    }

                }

                // must solve two or more things
                int TP_THRESHOLD = 1;

                // make sure threshold is not too high
                //if (totalInstancesUnsolved < TP_THRESHOLD) TP_THRESHOLD = 1;
                //if (totalInstancesSolved > (getTrainingCount() * 0.50)) TP_THRESHOLD = 1;

                // here's the nice part. If this classifier works a little bit, then add it to the classifier set
                // We define that as having at last one TP and no FPs.
                if (FP == 0 && TP >= TP_THRESHOLD) {
                    // This is a bug fix
                    //addClassifier(e, TP, ind, invert, results, c, indexesReturnedTrue);
                    DeepCopy copy = new DeepCopy();
                    addClassifier(e, TP, (Individual) copy.copy(ind), invert, results, c, indexesReturnedTrue);
                    // End bug fix

                    classifierWasAdded = true;
                    break;
                }

            }

        }

        // make sure it is never 0
        bestFitness++;

        ind.setKozaFitness(bestFitness);
        ind.setHits(totalInstancesSolved);

        strongClassifier.setKozaFitness(bestFitness);
        strongClassifier.setHits(totalInstancesSolved);

        if (classesFullySolved.size() == getClassCount()) {
            e.stopFlag = true;
        }

        GPActionListener listener = e.getListener();
        if (classifierWasAdded && listener != null && listener instanceof jasmine.gp.interfaces.GraphicalListener) {
            ((GraphicalListener) listener).onGoodIndividual(strongClassifier);
        }


    }

    /**
     * Adds a successful individual to the classifier list.
     */
    public void addClassifier(Evolve e, double TP, Individual ind, boolean invert, String results, TrainingClass c, Vector<Integer> classesReturnedTrue) {

        e.requestFreshPopulation();

        // definitely add this classifier - its a gem
        idCounter++;
        ADFNode node = new ADFNode(idCounter, ind.getTree(0), new int[]{NodeConstraints.BOOLEAN});
        ADFNodeConstraints n = node.createNodeConstraintsObject();

        // save this classifier
        retainedClassifiers.add(n);
        WeakLearner l = new WeakLearner(c.classID, ind, invert, results);
        strongClassifier.addWeakLearner(l);
        totalClassifiersAdded++;

        // TODO: Remove when bug fixed
        l.code = ind.toJava();
        // TODO: End remove when bug fixed

        // remember this classifier's result string
        savedClassifierResults.add(results);

        // register which instances are solved by this classifier
        for (int j = 0; j < classesReturnedTrue.size(); j++) {
            Integer trainingDataIndex = classesReturnedTrue.elementAt(j);
            c.registerInstanceSolved(trainingDataIndex);
        }

        String name = getClassName(c.classID);

        if (c.fullySolved) {
            // all instances of this class have been solved, so mark the class as completely solved.
            classesFullySolved.add(c.classID);
            buffer.append("// Class " + name + " is fully solved. (" + (getClassCount() - classesFullySolved.size()) + " remaining.)");
        }

        // Makes the code easier to understand / debug
        String comment = "Returns " + (invert ? "false" : "true") + " for classes: ";

        // And state what the objective of the classifier is
        if (c.fullySolved) {
            comment += ", fully identifies: " + name;
        } else {
            comment += ", partly identifies " + TP + " / " + c.getTotalInstances() + " instances of " + name + "  ( now " + c.getPercentageSolved() + "% solved)";
        }

        comment += "\n * " + results;

        JavaMethod m = new JavaMethod(JavaWriter.getMethodSignature(node));
        m.setComment(comment);
        javaSource.addMethod(m);
        m.setSource(JavaWriter.toJava(node, 0));

        // print out the individual, as a java method
        buffer.append(JavaWriter.toJava(node, comment));

        if (!invert) {
            executionOrderBuffer.append("\tif (method" + idCounter + "()) return " + c.classID + "; // " + name + "\n");
        } else {
            executionOrderBuffer.append("\tif (!method" + idCounter + "()) return " + c.classID + "; // " + name + "\n");
        }

        classifyMethod.setSource(executionOrderBuffer.toString());
        classifyMethod.addLine("return 0;");

        calculateTotalInstancesSolved();

    }

    // TODO - Debug code - delete when done with.

    public int check(SubGenerationalIndividual ind) {

        int score = 0;

        for (int i = 0; i < getTrainingCount(); i++) {
            DataStack data = new DataStack();
            setupDataStackForTraining(data, i);

            if ((int) ind.execute(data) == getTrainingClassID(i)) score++;
        }

        return score;

    }

    public int check(SubGenerationalIndividual ind, int i) {

        DataStack data = new DataStack();

        setupDataStackForTraining(data, i);

        return (int) ind.execute(data);

    }

    public boolean check(WeakLearner l, int i) {
        DataStack data = new DataStack();
        setupDataStackForTraining(data, i);
        return l.execute(data);
    }

    public String getResults(WeakLearner l) {
        String s = "";
        for (int i = 0; i < getTrainingCount(); i++) {
            DataStack data = new DataStack();
            setupDataStackForTraining(data, i);
            if (l.executeWithoutInversion(data)) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }

    int totalInstancesUnsolved = 0;
    int totalInstancesSolved = 0;
    int totalInstances = 0;

    public void calculateTotalInstancesSolved() {
        totalInstances = 0;
        totalInstancesSolved = 0;

        // DEBUG
        int errors = 0;
        // END DEBUG

        for (int i = 0; i < trainingClasses.size(); i++) {
            TrainingClass c = trainingClasses.elementAt(i);
            totalInstancesSolved += c.totalSolved;
            totalInstancesUnsolved = c.getUnsolvedCount();
            totalInstances += c.totalInstances;
        }

            // DEBUG CODE STARTING HERE

            for (int k = 0; k < strongClassifier.weakLearners.size(); k++) {
                WeakLearner l = strongClassifier.weakLearners.elementAt(k);

                String java = l.ind.toJava();

                if (!java.equals(l.code)) {
                    System.out.println("AHA: Code is different");
                    System.out.println(l.code);
                    System.out.println("Compare to:");
                    System.out.println(java);
                }

            }

            // DEBUG CODE ENDS HERE

        if (!silent) {
            System.out.println("TotalInstancesSolved: " + totalInstancesSolved);
            if (errors > 0) {
                System.out.println("ERROR COUNT: " + errors);
            }
        }


    }

    protected boolean alreadyFinished = false;

    public void onFinish(Individual bestOfGeneration, Evolve e) {

        if (!alreadyFinished) {

            buffer.append("    ExtraShapeData shape;\n    public int classify(ExtraShapeData shape) {\n" +
                    "        this.shape = shape;");
            //buffer.append("if (new LetterDetector().classify(shape) == LetterDetector.NOT_LETTER) return -1;");
            buffer.append(executionOrderBuffer.toString());
            buffer.append("\treturn -1;\n");
            buffer.append("\t}");

            calculateTotalInstancesSolved();

            buffer.append("// TOTAL INSTANCES SOLVED: " + totalInstancesSolved);
            buffer.append("// of " + totalInstances);

            buffer.append("}");

            if (writeToFile)
                outputFile.renameTo(new File("/home/ooechs/ecj-imaging/src/ac/essex/ooechs/imaging/commons/apps/jasmine/results/" + className + ".java"));


            if (!silent) System.out.println(javaSource.toSource());

            alreadyFinished = true;

        }

    }

    private int totalIndividuals;
    private int totalClassifiersAdded;
    private int totalNonUniqueClassifiers;
    private int totalNonDiscriminatingClassifiers;

    public Object describe(Individual ind, DataStack data, int index) {
        buffer.append(" // End of generation");
        buffer.append(" // Total individuals: " + totalIndividuals);
        buffer.append(" // Total classifiers added: " + totalClassifiersAdded);
        buffer.append(" // Total non-unique classifiers discarded: " + totalNonUniqueClassifiers);
        buffer.append(" // Total non-discriminating classifiers discarded: " + totalNonDiscriminatingClassifiers);
        totalIndividuals = 0;
        totalClassifiersAdded = 0;
        totalNonUniqueClassifiers = 0;
        totalNonDiscriminatingClassifiers = 0;
        return null;
    }


}
