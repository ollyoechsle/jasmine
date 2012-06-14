package jasmine.classify.featureselection;


import jasmine.classify.data.Data;

import java.util.Vector;

/**
 * Uses PCMs as a classifier to score attributes
 */
public class IGAttributeFilter extends AttributeFilter {

    public static final int DISCRETISATION_FACTOR = 10;

    public String getName() {
        return "Information Gain";
    }


    public float getScore(int columnID, Vector<Data> trainingData, int numClasses) {

        // go through the training data and find the max and the min
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        //POEY comment: trainingData.size()= the number of selected pixels
        for (int i = 0; i < trainingData.size(); i++) {
            Data data = trainingData.elementAt(i);
            double value = data.values[columnID];
            if (value > max) max = value;
            if (value < min) min = value;
        }

        Table t = new Table(DISCRETISATION_FACTOR+1, numClasses);
        for (int i = 0; i < trainingData.size(); i++) {
            Data data = trainingData.elementAt(i);
            double value = data.values[columnID];
            int slot = getSlotIndex(value, min, max);
            //POEY comment: count the number of data in this slot and its class and put it in t.table
            t.add(slot, data.classID-1);
        }

        return new InformationGain().getInformationGain(t.table);

    }

    public static int getSlotIndex(double raw, double MIN, double MAX) {
        if (raw > MAX) raw = MAX;
        else if (raw < MIN) raw = MIN;
        double RANGE = MAX - MIN;
        double adjusted = ((raw - MIN) / RANGE) * DISCRETISATION_FACTOR;
        return (int) adjusted;
    }    

}