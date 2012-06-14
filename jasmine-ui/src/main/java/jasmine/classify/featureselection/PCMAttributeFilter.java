package jasmine.classify.featureselection;


import jasmine.classify.data.Data;
import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.CachedOutput;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.PCM;
import jasmine.gp.multiclass.VarianceThreshold;

import java.util.Vector;

/**
 * Uses PCMs as a classifier to score attributes
 */
public class PCMAttributeFilter extends AttributeFilter {

    int DRSType;

    public PCMAttributeFilter(int DRSType) {
        this.DRSType = DRSType;
    }

    public String getName() {
        return "PCM Classifier";
    }


    public float getScore(int columnID, Vector<Data> traingData, int numClasses) {

        PCM pcm = null;

        //POEY comment: for segmentation it chooses BetterDRS
        switch (DRSType) {
            case BasicDRS.TYPE:
                pcm = new BasicDRS();
                break;
            case BetterDRS.TYPE:
                pcm = new BetterDRS();
                break;
            case VarianceThreshold.TYPE:
                pcm = new VarianceThreshold();
                break;
            case EntropyThreshold.TYPE:
                pcm = new EntropyThreshold();
                break;
        }

        //POEY comment: traingData.size() = the number of selected pixels
        for (int i = 0; i < traingData.size(); i++) {
            Data data = traingData.elementAt(i);
            pcm.addResult(data.values[columnID], data.classID);
        }
        pcm.calculateThresholds();
        float total = 0, n = 0;
      //POEY comment: for segmentation results contain values of a pixel, classID and weight = 1
        Vector<CachedOutput> results = pcm.getCachedResults();
        for (int i = 0; i < results.size(); i++) {
            CachedOutput cachedOutput = results.elementAt(i);
            //POEY comment: rawOutput is a value of a pixel
            //predicted class == real class
            if (pcm.getClassFromOutput(cachedOutput.rawOutput) == cachedOutput.expectedClass) total++;
            n++;
        }

        return total / n;

        }
    }
