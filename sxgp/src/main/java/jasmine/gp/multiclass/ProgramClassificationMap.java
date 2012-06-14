package jasmine.gp.multiclass;


import jasmine.gp.multiclass.CachedOutput;

import java.util.Vector;
import java.io.Serializable;

/**
 * Program Classification Map - is used to convert a floating point output into
 * a classification which may be either binary or multiclass. It includes functions
 * to automatically choose good thresholds (Dynamic Range Selection), or you can pass the
 * thresholds manually through the constructor (helpful for Static Range Selection).
 * @author Olly Oechsle, University of Essex, Date: 29-Jul-2007
 * @version 1.1 - Added the automatic threshold calculation code 22-Oct-2007
 */
public class ProgramClassificationMap extends PCM implements Serializable {


    /**
     * A mapping of a particular class (whose ID is stored in the ClassPosition object)
     * and a location on the number line.
     */
    protected Vector<ClassPosition> classPositions;

    /**
     * Initialises the program classification map without any classes. This is the starting
     * point for dynamic range selection. Add examples of outputs using the addResult() method
     * and then call the calculateThresholds() function to figure out good threshold positions.
     */
    public ProgramClassificationMap() {
        // do nothing
    }

    /**
     * Initialises the program classification map with predefined thresholds. This is useful
     * if you want to use static range selection (where the thresholds are always fixed), or
     * if you have a different dynamic strategy to calculate the positions of thresholds.
     */
    public ProgramClassificationMap(int[] classIDs, double[] thresholds) {
        classPositions = new Vector<ClassPosition>();
        for (int i = 0; i < classIDs.length; i++) {
            int classID = classIDs[i];
            double threshold = thresholds[i];
            classPositions.add(new ClassPosition(classID, threshold));
        }
    }

    /**
     * Calculates good thresholds to be used for classification. This method finds the average
     * output for each class and uses that.
     * Note that any existing class positions will be overwritten by this method.
     * You can set thresholds manually either using the alternative constructor or using
     * the addThreshold method.
     */
    public void calculateThresholds() {

        if (cachedResults == null) {
            throw new RuntimeException("Cannot calculate thresholds - no results added using the addResult() method.");
        }

        if (classPositions == null) {
            classPositions = new Vector<ClassPosition>();
        } else {
            classPositions.clear();
        }

        Vector<Integer> classes = discoverClasses();

        // now go through each class and find the average output
        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);
            // used to calculate the averagae
            double total = 0;
            int count = 0;
            for (int j = 0; j < cachedResults.size(); j++) {
                CachedOutput cachedOutput = cachedResults.elementAt(j);
                if (cachedOutput.expectedClass == classID) {
                    total += cachedOutput.rawOutput;
                    count++;
                }
            }
            double mean = total / count;
            classPositions.add(new ClassPosition(classID, mean));
        }

    }



    /**
     * Allows you to add a new class position after the object is constructed.
     */
    public void addThreshold(int classID, double centerPosition) {
        if (classPositions == null) {
            classPositions = new Vector<ClassPosition>();
        }
        classPositions.add(new ClassPosition(classID, centerPosition));
    }


    /**
     * Returns the position by class
     */
    public double getThresholdFromClass(int classID) {

        if (classPositions == null) {
            throw new RuntimeException("Class positions not initialised - did you call the calculateThresholds() method?");
        }

        for (int i = 0; i < classPositions.size(); i++) {
            ClassPosition classPosition = classPositions.elementAt(i);
            if (classPosition.classID == classID) return classPosition.centerPosition;
        }
        return 0;
    }

    /**
     * Classifys output into one of the classes registered with the program classification map.
     * @param output
     */
    public int getClassFromOutput(double output) {

        if (classPositions == null) {
            throw new RuntimeException("Class positions not initialised - did you call the calculateThresholds() method?");
        }

        // Create the program classification map
        double closestDist = Double.MAX_VALUE;
        int bestClass = 0;

        for (int i = 0; i < classPositions.size(); i++) {

            ClassPosition cp = classPositions.elementAt(i);

            double dist = Math.abs(output - cp.centerPosition);

            if (dist < closestDist) {
                closestDist = dist;
                bestClass = cp.classID;
            }

        }

        return bestClass;

    }

    /**
     * Returns this instance of the program classification map as java code
     * which you can easily insert into your programs.
     */
    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("new ProgramClassificationMap(");
        buffer.append("new int[]{");
        int count = 0;
        for (int i = 0; i < classPositions.size(); i++) {
            ClassPosition classPosition = classPositions.elementAt(i);
            if (Double.isNaN(classPosition.centerPosition)) continue;
            if (count > 0) buffer.append(",");
            buffer.append(classPosition.classID);
            count++;
        }
        buffer.append("}, new double[]{");
        count = 0;
        for (int i = 0; i < classPositions.size(); i++) {
            ClassPosition classPosition = classPositions.elementAt(i);
            if (Double.isNaN(classPosition.centerPosition)) continue;
            if (count > 0) buffer.append(",");
            buffer.append(classPosition.centerPosition);
            count++;
        }
        buffer.append("})");
        return buffer.toString();
    }


    protected class ClassPosition implements Serializable {

        int classID;
        double centerPosition;

        public ClassPosition(int classID, double centerPosition) {
            this.classID = classID;
            this.centerPosition = centerPosition;
        }

    }

}
