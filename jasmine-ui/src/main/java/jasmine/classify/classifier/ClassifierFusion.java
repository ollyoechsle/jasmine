package jasmine.classify.classifier;


import jasmine.classify.EnsembleListener;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataPartitioner;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.problems.DataStack;
import jasmine.gp.util.JavaWriter;

import java.util.Vector;
import java.util.Collections;
import java.util.StringTokenizer;
import java.io.Serializable;

/**
 * Implements two classifier fusion techniques, majority vote (each vote has equal weight)
 * and Committee Vote (each member indicates confidence for every class).
 * <p/>
 * Requires GP classifiers with individuals using the BetterDRS classifier for this to work.
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Oct-2008
 * @version 1.0
 */
public class ClassifierFusion extends Classifier {

    public static transient EnsembleListener listener;

    public static final int MAJORITY_VOTE = 1;
    public static final int COMMITTEE_VOTE_2 = 3;
    public static final int COMMITTEE_VOTE = 2;

    public boolean weightByFitness = true;

    public int mode;

    public Vector<GPClassifier> gpClassifiers;
    public Vector<ClassVote> classVotes;
    public Vector<Integer> classes;

    public ClassifierFusion(Vector<Integer> classes) {
        this.classes = classes;
        this.gpClassifiers = new Vector<GPClassifier>();
        classVotes = new Vector<ClassVote>(classes.size());
        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);
            classVotes.add(new ClassVote(classID));
        }
    }


    public ClassifierFusion(Vector<Integer> classes, Vector<GPClassifier> bestClassifiers, int mode) {
        this.classes = classes;
        this.mode = mode;
        this.gpClassifiers = bestClassifiers;
        classVotes = new Vector<ClassVote>(classes.size());
        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);
            classVotes.add(new ClassVote(classID));
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setMode(int mode, boolean weight_by_fitness) {
        this.mode = mode;
        this.weightByFitness = weight_by_fitness;
    }

    public void add(GPClassifier c) {
        gpClassifiers.add(c);
    }

    public int getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(int defaultClass) {
        this.defaultClass = defaultClass;
    }

    public static final int FIRST_CHOICE = 1;
    public static final int SECOND_CHOICE = 2;
    int defaultClass = 0;

    public int classify() {
        return classify(gpClassifiers, (float[]) null);
    }

    public int classify(DataStack ds) {
        return classify(gpClassifiers, ds);
    }

    public int classify(Vector<GPClassifier> gpClassifiers, DataStack ds) {

        getClassVotes(gpClassifiers, ds);

        // Now, go through the class votes one more time and pick the one with the highest number of votes
        ClassVote highest = null;
        for (int i = 0; i < classVotes.size(); i++) {
            ClassVote classVote = classVotes.elementAt(i);
            if (highest == null || classVote.votes > highest.votes) {
                highest = classVote;
            }
        }

        if (highest != null && highest.votes > 0) {
            return highest.classID;
        }

        return defaultClass;

    }

    public int classify(float[] values) {
//System.out.println("ClassifierFusion");    	
        return classify(gpClassifiers, values);
    }

    public int classify(Vector<GPClassifier> gpClassifiers, float[] values) {

        getClassVotes(gpClassifiers, values);

        // Now, go through the class votes one more time and pick the one with the highest number of votes
        ClassVote highest = null;
        for (int i = 0; i < classVotes.size(); i++) {
            ClassVote classVote = classVotes.elementAt(i);
            if (highest == null || classVote.votes > highest.votes) {
                highest = classVote;
            }
        }

        if (highest != null && highest.votes > 0) {
            return highest.classID;
        }

        return defaultClass;

    }

    public Vector<ClassVote> getClassVotes(Vector<GPClassifier> gpClassifiers, DataStack ds) {
        // reset class vote objects
        for (int i = 0; i < classVotes.size(); i++) {
            classVotes.elementAt(i).votes = 0;
        }

        // majority vote - easy - count votes for each class
        switch (mode) {
            case MAJORITY_VOTE:
                for (int i = 0; i < gpClassifiers.size(); i++) {
                    GPClassifier classifier = gpClassifiers.elementAt(i);
                    // get the vote
                    int classID = classifier.classify(ds);
                    float weight = 1;
                    if (weightByFitness) weight = classifier.weight;
                    voteFor(classID, weight);
                }
                break;
            case COMMITTEE_VOTE:
                // experiments appear to show that this is the best fusion technique
                for (int i = 0; i < gpClassifiers.size(); i++) {
                    GPClassifier classifier = gpClassifiers.elementAt(i);
                    double rawOutput = classifier.getRawOutput(ds);

                    float weight = 1;
                    if (weightByFitness) weight = classifier.weight;

                    // go through each class; find the confidence for each classification
                    for (int j = 0; j < classVotes.size(); j++) {
                        ClassVote classVote = classVotes.elementAt(j);

                        float confidence = 0;
                        if (classifier.ind.getPCM() instanceof BetterDRS) {
                            confidence = (classifier.ind.getPCM()).getConfidence(classVote.classID, rawOutput);
                        }
                        classVote.votes += confidence * weight;
                    }
                }
                break;
        }
        return classVotes;
    }

    public Vector<ClassVote> getClassVotes(Vector<GPClassifier> gpClassifiers, float[] values) {
        // reset class vote objects
        for (int i = 0; i < classVotes.size(); i++) {
            classVotes.elementAt(i).votes = 0;
        }

        // majority vote - easy - count votes for each class
        switch (mode) {
            case MAJORITY_VOTE:
                for (int i = 0; i < gpClassifiers.size(); i++) {
                    GPClassifier classifier = gpClassifiers.elementAt(i);
                    // get the vote
                    int classID = classifier.classify(values);
                    float weight = 1;
                    if (weightByFitness) weight = classifier.weight;
                    voteFor(classID, weight);
                }
                break;
            case COMMITTEE_VOTE:
                // experiments appear to show that this is the best fusion technique
                for (int i = 0; i < gpClassifiers.size(); i++) {
                    GPClassifier classifier = gpClassifiers.elementAt(i);
                    double rawOutput = classifier.getRawOutput(values);

                    float weight = 1;
                    if (weightByFitness) weight = classifier.weight;

                    // go through each class; find the confidence for each classification
                    for (int j = 0; j < classVotes.size(); j++) {
                        ClassVote classVote = classVotes.elementAt(j);

                        float confidence = 0;
                        if (classifier.ind.getPCM() instanceof BetterDRS) {
                            confidence = (classifier.ind.getPCM()).getConfidence(classVote.classID, rawOutput);
                        }
                        classVote.votes += confidence * weight;
                    }
                }
                break;
            case COMMITTEE_VOTE_2:
                for (int i = 0; i < gpClassifiers.size(); i++) {
                    GPClassifier classifier = gpClassifiers.elementAt(i);
                    double rawOutput = classifier.getRawOutput(values);

                    // save the choices here
                    Vector<Choice> choices = new Vector<Choice>();

                    // go through each class; find the confidence for each classification
                    for (int j = 0; j < classVotes.size(); j++) {
                        ClassVote classVote = classVotes.elementAt(j);

                        float confidence = 0;
                        if (classifier.ind.getPCM() instanceof BetterDRS) {
                            confidence = (classifier.ind.getPCM()).getConfidence(classVote.classID, rawOutput);
                        }

                        choices.add(new Choice(classVote, confidence));
                    }

                    // sort the choices
                    Collections.sort(choices);

                    // let the top two vote
                    for (int j = 0; j < choices.size() && j < 2; j++) {
                        Choice choice = choices.elementAt(j);
                        choice.classID.votes += choice.confidence;
                    }
                }
                break;

        }
        return classVotes;
    }

    class Choice implements Comparable {

        ClassVote classID;
        double confidence;

        Choice(ClassVote classID, double confidence) {
            this.classID = classID;
            this.confidence = confidence;
        }

        public int compareTo(Object o) {
            Choice other = (Choice) o;
            if (other.confidence > this.confidence) return +1;
            if (other.confidence < this.confidence) return -1;
            return 0;
        }

    }

    public void voteFor(int classID, float voteWeight) {
        for (int i = 0; i < classVotes.size(); i++) {
            ClassVote classVote = classVotes.elementAt(i);
            if (classVote.classID == classID) {
                classVote.votes += voteWeight;
                break;
            }
        }
    }

    public Vector<GPClassifier> bestClassifiers;

    public int tryHits(Vector<Data> trainingData, Vector<Data> testingData, int committeeSize) {
        return tryHits(trainingData, testingData, committeeSize, DataPartitioner.TRAINING);
    }

    public int tryHits(Vector<Data> trainingData, Vector<Data> testingData, int committeeSize, boolean dataType) {

        Vector<int[]> combinations = getCombinations(committeeSize);

        Vector<int[]> triedBefore = new Vector<int[]>();

        int bestTP = 0;
        //int testTP = 0;

        
        loop:
        //POEY comment: combinations.size() = such as 42, 210, 840, 2520
        for (int j = 0; j < combinations.size(); j++) {
            int[] combination = combinations.elementAt(j);
            //POEY comment: triedBefore.size() = the number of best individuals
            for (int i = 0; i < triedBefore.size(); i++) {
                int[] alreadyTried = triedBefore.elementAt(i);
                //POEY comment: does alreadyTried contain combination's data and are they the same size?
                if (equivalentTo(combination, alreadyTried)) continue loop;
            }

            Vector<GPClassifier> classifiers = new Vector<GPClassifier>();

            for (int i = 0; i < combination.length; i++) {
                int index = combination[i];
                classifiers.add(gpClassifiers.elementAt(index));
            }

            int hits = 0;

            for (int i = 0; i < trainingData.size(); i++) {
                Data d = trainingData.elementAt(i);
                if (d.weight == 0) continue;
                if (d.type != dataType) continue;
                if (classify(classifiers, d.values) == d.classID) {
                    hits++;
                }
            }

            if (hits > bestTP) {

                bestTP = hits;

                
                if (listener != null) {
                    double pc = bestTP / (double) trainingData.size();
                    listener.onBetterFitness(1-pc, this);
                }

                bestClassifiers = classifiers;

            }

            triedBefore.add(combination);

        }

        //System.out.println("There are " + triedBefore.size() + " combinations");

        //System.out.println("Training: " + bestTP + ", Testing: " + testTP);
        return bestTP;

    }

    public boolean equivalentTo(int[] combination1, int[] combination2) {

        int similarities = 0;
        for (int i = 0; i < combination1.length; i++) {
            if (contains(combination2, combination1[i])) similarities++;
        }

        if (similarities == combination1.length) {
            //System.out.println(toString(combination1) + " == " + toString(combination2));
            return true;
        }

        return false;

    }

    public String toString(int[] combination) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < combination.length; i++) {
            int i1 = combination[i];
            buffer.append(i1);
            if (i < combination.length - 1) buffer.append(", ");
        }
        return buffer.toString();
    }


    public Vector<int[]> getCombinations(int committeeSize) {
        Vector<int[]> combinations = new Vector<int[]>();
        int[] c = new int[committeeSize];
        for (int i = 0; i < c.length; i++) {
            c[i] = -1;
        }
        getCombinations(combinations, c, 0);
        return combinations;
    }

    private void getCombinations(Vector<int[]> combinations, int[] currentCombination, int index) {
        if (index >= currentCombination.length) {
            combinations.add(currentCombination);
        } else {
            for (int i = 0; i < gpClassifiers.size(); i++) {
                if (!contains(currentCombination, i)) {
                    int[] copy = new int[currentCombination.length];
                    System.arraycopy(currentCombination, 0, copy, 0, copy.length);
                    copy[index] = i;
                    getCombinations(combinations, copy, index + 1);
                }
            }
        }
    }

    private boolean contains(int[] combination, int value) {
        for (int i = 0; i < combination.length; i++) {
            int i1 = combination[i];
            if (i1 == value) return true;
        }
        return false;
    }

    public class ClassVote implements Serializable {

        public float votes;
        public int classID;

        public ClassVote(int classID) {
            this.classID = classID;
        }

        public String toString() {
            return "Class=" + classID + ", votes=" + votes;
        }
    }

    public int tryHits(Vector<Data> data, int fold) {

        Vector<int[]> combinations = getCombinations(3);

        int bestTP = 0;

        for (int j = 0; j < combinations.size(); j++) {
            int[] combination = combinations.elementAt(j);

            Vector<GPClassifier> classifiers = new Vector<GPClassifier>();

            for (int i = 0; i < combination.length; i++) {
                int index = combination[i];
                classifiers.add(gpClassifiers.elementAt(index));
            }

            int TP = 0;
            int FP = 0;

            for (int i = 0; i < data.size(); i++) {
                Data d = data.elementAt(i);

                if (d.fold != fold) continue;

                int outputClass = classify(classifiers, d.values);

                if (outputClass == d.classID) {
                    TP++;
                } else {
                    FP++;
                }

            }

            if (TP > bestTP) bestTP = TP;

        }

        return bestTP;

    }

    public String toJava(String modifiers, String methodName, String arguments) {


        // get the argument names
        StringTokenizer st = new StringTokenizer(arguments, " ");
        StringBuffer argNamesBuffer = new StringBuffer();
        boolean name = false;
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            if (name) {
                argNamesBuffer.append(token);
            }
            name = !name;
        }


        String[] methodNames = new String[gpClassifiers.size()];
        for (int i = 0; i < methodNames.length; i++) {
            methodNames[i] = "classify" + i;
        }

        StringBuffer buffer = new StringBuffer();

        buffer.append("public Vector<ClassVote> classVotes;\n");
        buffer.append("private final int defaultClass = " + defaultClass + ";\n\n");

        if (mode != MAJORITY_VOTE) JavaWriter.PRINT_PCM = false;
        for (int i = 0; i < gpClassifiers.size(); i++) {
            GPClassifier gpClassifier = gpClassifiers.elementAt(i);
             if (mode == MAJORITY_VOTE) {
            buffer.append(gpClassifier.toJava("protected int", methodNames[i], arguments));
             } else {
                 buffer.append(gpClassifier.toJava("protected double", methodNames[i], arguments));
             }
        }
        JavaWriter.PRINT_PCM = true;

        String methodSignature = modifiers + " " + methodName + "(" + arguments + ")";
        buffer.append(methodSignature);
        buffer.append(" {\n");

        String argNames = argNamesBuffer.toString();

        if (mode == MAJORITY_VOTE) {
            for (int i = 0; i < methodNames.length; i++) {
                String method = methodNames[i];
                buffer.append("\tvoteFor(");
                buffer.append(method);
                buffer.append("(");
                buffer.append(argNames);
                buffer.append("), 1);\n");
            }
        } else {

            buffer.append("Vector<ClassVote> classVotes = new Vector<ClassVote>(" + classes.size() + ");\n\n");

            for (int i = 0; i < methodNames.length; i++) {
                String method = methodNames[i];
                buffer.append("double rawOutput_" + i + " = " + method + "(" + argNames + ");\n");
            }

            for (int j = 0; j < classes.size(); j++) {
                Integer classID = classes.elementAt(j);
                buffer.append("ClassVote classVote" + j + " = new ClassVote(" + classID + ");\n");
                // now go through each method
                for (int i = 0; i < methodNames.length; i++) {
                    GPClassifier gpc = gpClassifiers.elementAt(i);
                    buffer.append("classVote" + j + ".votes += " + gpc.ind.getPCM().name + "0.getConfidence(" + classID + ", rawOutput_" + i + ");\n");
                }
                buffer.append("classVotes.add(classVote" + j + ");\n\n");
            }


        }


        buffer.append("\n  // Now, go through the class votes one more time and pick the one with the highest number of votes\n" +
                "        ClassVote highest = null;\n" +
                "        for (int i = 0; i < classVotes.size(); i++) {\n" +
                "            ClassVote classVote = classVotes.elementAt(i);\n" +
                "            if (highest == null || classVote.votes > highest.votes) {\n" +
                "                highest = classVote;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        if (highest != null && highest.votes > 0) {\n" +
                "            return highest.classID;\n" +
                "        }\n" +
                "\n" +
                "        return defaultClass;\n\n");


        buffer.append("}\n\n");


        buffer.append("    protected void voteFor(int classID, float voteWeight) {\n" +
                "        for (int i = 0; i < classVotes.size(); i++) {\n" +
                "            ClassVote classVote = classVotes.elementAt(i);\n" +
                "            if (classVote.classID == classID) {\n" +
                "                classVote.votes += voteWeight;\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n\n");

        buffer.append("    protected class ClassVote implements Serializable {\n" +
                "\n" +
                "        public float votes;\n" +
                "        public int classID;\n" +
                "\n" +
                "        public ClassVote(int classID) {\n" +
                "            this.classID = classID;\n" +
                "        }\n" +
                "\n" +
                "        public String toString() {\n" +
                "            return \"Class=\" + classID + \", votes=\" + votes;\n" +
                "        }\n" +
                "    }\n\n");

        return buffer.toString();

    }

    public String toString() {
        if (mode == MAJORITY_VOTE) {
            return ("Majority vote with " + gpClassifiers.size() + " classifiers");
        } else {
            return ("Committee vote with " + gpClassifiers.size() + " classifiers");
        }
    }

}
