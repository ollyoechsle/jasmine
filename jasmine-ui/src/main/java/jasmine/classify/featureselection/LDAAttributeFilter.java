package jasmine.classify.featureselection;


import jasmine.classify.classifier.LDA;
import jasmine.classify.data.Data;

import java.util.Vector;

/**
 * Uses PCMs as a classifier to score attributes
 */
public class LDAAttributeFilter extends AttributeFilter {

    public String getName() {
        return "Linear Discriminant Analysis";
    }


    public float getScore(int columnID, Vector<Data> traingData, int numClasses) {

        int numFeatures = 1;

        LDA lda = new LDA(numFeatures);

        //POEY comment: traingData.size() = the number of selected pixels or segmented objects
        for (int i = 0; i < traingData.size(); i++) {
            Data data = traingData.elementAt(i);        
            lda.add(new double[]{data.values[columnID]}, data.classID);
        }

        lda.compute();

        float total = 0, n = 0;
        for (int i = 0; i < traingData.size(); i++) {
            Data data = traingData.elementAt(i);
            int classID = lda.classify(new double[]{data.values[columnID]});
            if (classID == data.classID) total++;
            n++;
        }        

        return total / n;

    }
}