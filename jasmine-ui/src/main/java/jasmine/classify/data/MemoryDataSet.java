package jasmine.classify.data;


import java.util.Vector;
import java.io.IOException;

/**
 * A kind of dataset with separate training and testing data. The best kind!
 * Easy to validate.
 */
public class MemoryDataSet extends DataSet {

    private Vector<Data> training = null, testing = null;
    private DataStatistics trainingStatistics;

    public MemoryDataSet(String name) {
        super(name);
        training = new Vector<Data>();
        testing = new Vector<Data>();
    }

    public void addTrainingData(float[] features, int classID) {
        training.add(new Data(features, String.valueOf(classID), classID));
    }

    public void addTestingData(float[] features, int classID) {
        testing.add(new Data(features, null, classID));
    }

    public void init() throws IOException {
        start = System.currentTimeMillis();
        DataStatistics.reset();
        trainingStatistics = new DataStatistics(this.training);
        new DataStatistics(this.testing);
    }

    public void normalise() {
        DataNormaliser normaliser = new DataNormaliser(training);
        normaliser.normalise(training);
        normaliser.normalise(testing);
    }

    public int getClassCount() {
        return trainingStatistics.getClassCount();
    }

    public void close() {
        training.removeAllElements();
        training = null;
        testing.removeAllElements();
        testing = null;
        trainingStatistics = null;
    }

    public int getTrainingSize() {
        return training.size();
    }

    public DataStatistics getTrainingStatistics() {
        return trainingStatistics;
    }

    public int getFolds() {
        return 1;
    }

    public Vector<Data> getTrainingData(int fold) {
        return training;
    }

    public Vector<Data> getAllTrainingData() {
        return training;
    }

    public Vector<Data> getTestingData(int fold) {
        return testing;
    }

}