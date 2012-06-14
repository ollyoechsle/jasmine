package jasmine.gp.multiclass;

import java.util.Vector;
import java.io.Serializable;

/**
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version,
 * provided that any use properly credits the author.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details at http://www.gnu.org
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 27-Feb-2008
 * @version 1.0
 */
public abstract class PCM implements Serializable {

    /**
     * The addResult method saves all the results to this vector so they can be used later.
     */
    protected Vector<CachedOutput> cachedResults;

    protected Vector<Integer> classes;

    public String name = "pcm";        

    /**
     * Gives a result to the program classification map. This information can be used
     * to calculate good values for the threshold using the calculateThresholds method.
     *
     * @param output  The raw output from the program
     * @param classID The class that we expect to see
     */
    public void addResult(double output, int classID) {
        addResult(output, classID, 1);
    }

    public PCM() {
        classes = new Vector<Integer>(10);
    }

    /**
     * Gives a result to the program classification map. This information can be used
     * to calculate good values for the threshold using the calculateThresholds method.
     *
     * @param output  The raw output from the program
     * @param classID The class that we expect to see
     */
    public void addResult(double output, int classID, double weight) {
            if (cachedResults == null) {
                cachedResults = new Vector<CachedOutput>(500);
            }
            //POEY comment: for segmentation an output is a calculated value of a pixel, weight = 1
            cachedResults.add(new CachedOutput(output, classID, weight));
        if (!classes.contains(classID)) {
            classes.add(classID);
        }
    }

    /**
     * Calculates the thresholds in some way so as to enable classification.
     */
    public abstract void calculateThresholds();

    /**
     * Given an output, returns the classification
     */
    public abstract int getClassFromOutput(double raw);

    /** Returns an indication of the classifier's confidence, between 0 and 1 that the class is correct **/
    public float getConfidence(int classID, double raw) {
        return classID == getClassFromOutput(raw) ? 1 : 0;
    }

    /**
     * Turns the map into java that allows it to be reinstantiated.
     */
    public abstract String toJava();

    protected Vector<Integer> discoverClasses() {
        Vector<Integer> classes = new Vector<Integer>(10);

        if (cachedResults == null) {
            System.err.println("Cached results is null. Cannot discover classes.");
        }

        // first discover every class there is
        for (int i = 0; i < cachedResults.size(); i++) {
            CachedOutput cachedOutput = cachedResults.elementAt(i);
            if (!classes.contains(cachedOutput.expectedClass)) {
                classes.add(cachedOutput.expectedClass);
            }
        }

        return classes;
    }

    /**
     * As we have a set of cached results (or should have, if this method is called)
     * we can easily calculate the hits once the thresholds have been calculated.
     */
    public int getHits() {
        int hits = 0;
        for (int i = 0; i < cachedResults.size(); i++) {
            CachedOutput cachedOutput = cachedResults.elementAt(i);
            if (getClassFromOutput(cachedOutput.rawOutput) == cachedOutput.expectedClass) hits++;
        }
        return hits;
    }

    /**
     * Gets the hits for a specific class only.
     */
    public int getHits(int classID) {
        int hits = 0;
        for (int i = 0; i < cachedResults.size(); i++) {
            CachedOutput cachedOutput = cachedResults.elementAt(i);
            if (cachedOutput.expectedClass == classID && getClassFromOutput(cachedOutput.rawOutput) == cachedOutput.expectedClass)
                hits++;
        }
        return hits;
    }

    /**
     * Returns the cached results - other programs may want to use them without having to recalculate the data.
     */
    public Vector<CachedOutput> getCachedResults() {
        return cachedResults;
    }

    /**
     * Removes the cached results to save memory.
     */
    public void clearCachedResults() {
        if (cachedResults != null) cachedResults.clear();
    }

}
