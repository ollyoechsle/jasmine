package jasmine.classify.data;


import java.util.Vector;
import java.util.Random;

/**
 * Partitions data into training and validation sets.
 */
public class DataPartitioner {

    public static final boolean TRAINING = false;
    public static final boolean VALIDATION = true;

    protected Random r = null;

    public DataPartitioner(int seed) {
        if (seed == -1) {
            r = new Random();
        } else {
            r = new Random(seed);
        }
    }

    public void clearValidationSet(Vector<Data> data) {
        for (int i = 0; i < data.size(); i++) {
            Data data1 = data.elementAt(i);
            data1.type = TRAINING;
        }
    }

    public void createValidationSet(Vector<Data> data, DataStatistics s, float validationProportion) {

        if (validationProportion < 0 || validationProportion > 1) {
            throw new RuntimeException("Validation proportion must be between 0-1");
        }

        // Figure out which classes the data belongs to
        DataBin[] bins = new DataBin[s.getClassCount()];
        //System.out.println("Created " + bins.length + " bins");
        for (int i = 0; i < s.getClassIDs().size(); i++) {
            int classID = s.getClassIDs().elementAt(i);
            //System.out.println("BIN ClassID: " + classID);
            bins[classID - 1] = new DataBin(classID);
        }

        // Put the data into the relevant bins
        for (int i = 0; i < data.size(); i++) {
            Data data1 = data.elementAt(i);
            data1.type = TRAINING;
            bins[data1.getLabel() - 1].add(data1);
        }

        // go through each class in turn
        for (int i = 0; i < bins.length; i++) {
            DataBin bin = bins[i];
            int validationSize = (int) (bin.size() * validationProportion);
            for (int j = 0; j < validationSize; j++) {
                Data d = bin.popData(r);
                if (d == null) break;
                d.type = VALIDATION;
            }
        }

    }

}
