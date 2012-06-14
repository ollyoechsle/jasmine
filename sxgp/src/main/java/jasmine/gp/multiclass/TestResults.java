package jasmine.gp.multiclass;

import java.util.Vector;

/**
 * Records the test results of a particular classifier (ADFNodeParams object) against
 * a set of training data, and implements an equals method to determine if these results
 * are distinct from a different set of test results. This allows the coevolution system
 * to only use retainedClassifiers which produce different responses. This makes coevolution more efficient
 * and ensures that one type of classifier does not dominate the available options.
 *
 * @author Olly Oechsle, University of Essex, Date: 13-Mar-2007
 * @version 1.0
 */
public class TestResults {

    private boolean[] classResults;
    private int numClasses;

    private int returnedTrue = 0;
    private int returnedFalse = 0;

    private int classID = -1;

    /**
     * Classes for which the classifier returned a verdict of true
     */
    private Vector<Integer> classesReturnedTrue;

    public TestResults(int numClasses) {
        classesReturnedTrue = new Vector<Integer>(10);
        this.numClasses = numClasses;
        classResults = new boolean[numClasses];
    }

    public void setResult(int index, boolean result, int classID) {
        classResults[index] = result;
        if (result) {
            returnedTrue++;
            this.classID = classID;
            classesReturnedTrue.add(classID);
        }
        else returnedFalse++;
    }

    public boolean equals(TestResults other) {
        if (this.numClasses != other.numClasses) {
            // this is very unlikely to be the case but prevents there being
            // any array index exceptions being thrown.
            return false;
        } else {
            // compare each result
            for (int i = 0; i < classResults.length; i++) {
                if (classResults[i] != other.classResults[i]) return false;
            }
            return true;
        }
    }

    /**
     * Returns the result of analysis of what a classifier can do and whether it
     * is actually discriminating between one or more classes and the rest of the training
     * data - or not.
     */
    public boolean canDiscriminate() {
        if (returnedTrue != 0 && returnedFalse != 0) {
            return true;
        }
        return false;        
    }
    
    public boolean canDiscriminateSingleClass() {
        if (returnedTrue != 0 && returnedFalse != 0 && (returnedTrue == 1)) {
            return true;
        }
        return false;
    }

    public boolean canSeparate(Vector<Integer> unsolvedClasses) {

        StringBuffer buffer = new StringBuffer();

        // see how many of the unsolved classes were solved by this classifier
        int solved = 0;

        for (int i = 0; i < unsolvedClasses.size(); i++) {
            Integer classID = unsolvedClasses.elementAt(i);
            if (classesReturnedTrue.contains(classID)) {
                solved++;
                buffer.append(classID + ", ");
            }
        }

        // if SOME were classified, and some were NOT, then this classifier can
        // separate some of the unsolved classes. Yay.
        if (solved > 0 && solved < unsolvedClasses.size()) {
            System.out.println("Can distinguish " + buffer.toString() + " from other classes.");
            return true;
        } else {
            return false;
        }

    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < classResults.length; i++) {
            boolean classResult = classResults[i];
            buffer.append(classResult? 'T' : 'F');
        }
        return buffer.toString();
    }

    public int getClassID() {
        return classID;
    }
    
}
