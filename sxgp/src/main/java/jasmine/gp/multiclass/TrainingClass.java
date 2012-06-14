package jasmine.gp.multiclass;

import java.util.Vector;

/**
 * Data structure to record if a given class is fully solved or not. If it has not been solved, which shapes
 * are unsolved and which shapes are solved?
 */
public class TrainingClass {

    public int classID;
    public Vector<Integer> shapeIndexesNotSolved;
    public Vector<Integer> shapeIndexesSolved;
    public boolean fullySolved;
    public int totalInstances;
    public int totalSolved;

    public TrainingClass(int classID) {
        this.classID = classID;
        shapeIndexesNotSolved = new Vector<Integer>(10);
        shapeIndexesSolved = new Vector<Integer>(10);
        fullySolved = false;
        totalInstances = 0;
        totalSolved = 0;
    }

    public void addInstance(int trainingDataIndex) {
        shapeIndexesNotSolved.add(trainingDataIndex);
        totalInstances++;
    }

    public void registerInstanceSolved(int trainingDataIndex) {
        if (shapeIndexesNotSolved.remove(new Integer(trainingDataIndex))) {
            totalSolved++;
            shapeIndexesSolved.add(trainingDataIndex);
        }

        //if (shapeIndexesNotSolved.size() == 0) {
        if (totalSolved >= totalInstances) {
            fullySolved = true;
        }
    }

    /**
     * Returns whether a specific training data instance has been solved or not.
     * @param index
     * @return
     */
    public boolean isSolved(int index) {
        return !shapeIndexesNotSolved.contains(new Integer(index));
    }

    /**
     * Returns whether one or more classifiers have solved all instances of a particular class.
     */
    public boolean isFullySolved() {
        return fullySolved;
    }

    /**
     * Returns the total number of instances of this classID in the training data.
     */
    public int getTotalInstances() {
        return totalInstances;
    }

    /**
     * Returns the number of instances that have not been solved yet.
     */
    public int getUnsolvedCount() {
        return totalInstances - totalSolved;
    }

    public double getPercentageSolved() {
        // find out how many have been solved
        return (totalSolved / (double) totalInstances) * 100;
    }

    public Vector<Integer> getShapeIndexesNotSolved() {
        return shapeIndexesNotSolved;
    }

    public Vector<Integer> getShapeIndexesSolved() {
        return shapeIndexesSolved;
    }

}