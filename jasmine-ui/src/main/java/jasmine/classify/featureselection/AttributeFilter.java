package jasmine.classify.featureselection;


import jasmine.classify.data.Data;

import java.util.Vector;

/**
 * Returns the score for a particular attribute
 */
public abstract class AttributeFilter {

    public abstract float getScore(int columnID, Vector<Data> traingData, int numClasses);

    public abstract String getName();

    public String toString() {
        return getName();
    }

}
