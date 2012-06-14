package jasmine.classify.evaluation;


import jasmine.classify.data.Data;

import java.util.Vector;

/**
 * Results of the nearest neighbour testBinary for a particular class. Its overall
 * score (0-1), and details of False Positives (class confusions) are stored.
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class ClassifierTestResults implements Comparable {

    protected int classID;
    protected int classSize;
    protected float percentageCorrect;
    protected Vector<ClassConfusion> classConfusions;
    protected ClassConfusionMatrix classConfusionMatrix;
    int TP;

    public ClassifierTestResults(int classID, int classSize, float score, int TP) {
        this.classID = classID;
        this.classSize = classSize;
        this.percentageCorrect = score;
        classConfusions = new Vector<ClassConfusion>(5);
        this.TP = TP;
    }

    public void add(ClassConfusion c) {
        classConfusions.add(c);
    }

    public int getClassID() {
        return classID;
    }

    public float getPercentageCorrect() {
        return percentageCorrect;
    }

    public Vector<ClassConfusion> getClassConfusions() {
        return classConfusions;
    }

    public float getErrorEstimate() {
        return (1 - percentageCorrect) * classSize;
    }

    public int compareTo(Object o) {
        ClassifierTestResults other = (ClassifierTestResults) o;
        double potentialErrors = getErrorEstimate();
        if (other.getErrorEstimate() > potentialErrors) return -1;
        if (other.getErrorEstimate() < potentialErrors) return +1;
        return 0;
    }

    public String toString() {
        return classID + ": " + (percentageCorrect*100) + "%, e=" + getErrorEstimate();
    }

    protected Vector<Data> falseNegatives, falsePositives;

    public Vector<Data> getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(Vector<Data> falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    public Vector<Data> getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(Vector<Data> falsePositives) {
        this.falsePositives = falsePositives;
    }

    public void printConfusionMatrix() {
        classConfusionMatrix.print();
    }


    
}
