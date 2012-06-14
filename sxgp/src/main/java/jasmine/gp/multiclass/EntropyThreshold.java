package jasmine.gp.multiclass;



import jasmine.imaging.commons.EntropyThresholder;

import java.util.Vector;

/**
 * <p>
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
 * @author Olly Oechsle, University of Essex, Date: 05-Dec-2008
 * @version 1.0
 */
public class EntropyThreshold extends PCM {

    public static final int TYPE = 3;

    protected int numSlots = 50;
    protected int threshold;
    protected double MAX = Double.MIN_VALUE, MIN = Double.MAX_VALUE;
    protected int class0, class1;

    // default
    public EntropyThreshold() {

    }

    public EntropyThreshold(int threshold, double MAX, double MIN, int class0, int class1) {
        this.threshold = threshold;
        this.MAX = MAX;
        this.MIN = MIN;
        this.class0 = class0;
        this.class1 = class1;
    }

    /**
     * Gives a result to the program classification map. This information can be used
     * to calculate good values for the threshold using the calculateThresholds method.
     *
     * @param output  The raw output from the program
     * @param classID The class that we expect to see
     */
    public void addResult(double output, int classID, double weight) {
        super.addResult(output, classID, weight);    //To change body of overridden methods use File | Settings | File Templates.
        if (output > MAX) MAX = output;
        if (output < MIN) MIN = output;
    }

    boolean invert = false;

    /**
     * Calculates the thresholds in some way so as to enable classification.
     */
    public void calculateThresholds() {
    	//POEY comment: numSlots = 50, so it generates 51 slots
        EntropyThresholder t = new EntropyThresholder(numSlots);
        //POEY comment: cachedResults.size() = the number of selected pixels        
        for (int i = 0; i < cachedResults.size(); i++) {
            CachedOutput cachedOutput = cachedResults.elementAt(i);
            int slot = getSlotIndex(cachedOutput.rawOutput);
            t.addData(slot);
        }

        threshold = t.getOptimalThreshold();

        //POEY comment: classes contain 2 classes of pixels
        Vector<Integer> classes = discoverClasses();

        // see which combination of classes yields the best hits
        class0 = classes.elementAt(0);
        class1 = classes.elementAt(1);
        int hits1 = getHits();

        class0 = classes.elementAt(1);
        class1 = classes.elementAt(0);
        int hits2 = getHits();

        if (hits1 > hits2) {
            class0 = classes.elementAt(0);
            class1 = classes.elementAt(1);
        }

    }

    public int getSlotIndex(double raw) {
        if (raw > MAX) raw = MAX;
        else if (raw < MIN) raw = MIN;
        double RANGE = MAX - MIN;
        double adjusted = ((raw - MIN) / RANGE) * numSlots;       
        return (int) adjusted;
    }

    /**
     * Given an output, returns the classification
     */
    public int getClassFromOutput(double raw) {   	
        return getSlotIndex(raw) > threshold? class1 : class0;
    }

    /**
     * Turns the map into java that allows it to be reinstantiated.
     */
    public String toJava() {
        return "new EntropyThreshold(" + threshold + ", " + MAX + ", " + MIN + ", " + class0 + ", " + class1 + ")";
    }
}