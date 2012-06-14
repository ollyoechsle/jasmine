package jasmine.classify.data;


import java.util.Vector;
import java.io.IOException;

/**
 * Interface for training sets. Allows any kind of training data to be solved using the same problem.
 */
public abstract class DataSet {

    public String name;
    public long start;

    public DataSet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the number of classes in the problem
     */
    public abstract int getClassCount();

    /**
     * Initialises the dataset to actually load the data.
     */
    public abstract void init() throws IOException;

    /**
     * Closes the dataset and frees up memory
     */
    public abstract void close();

    /**
     * Returns how many folds of data are there. If there is a separate training and testing set then
     * there is only one.
     */
    public abstract int getFolds();

    public abstract int getTrainingSize();

    public abstract DataStatistics getTrainingStatistics();

    /**
     * Returns the training data for the appropriate fold
     */
    public abstract Vector<Data> getTrainingData(int fold);

    public abstract Vector<Data> getAllTrainingData();

    /**
     * Returns the testing data for the appropriate fold
     */
    public abstract Vector<Data> getTestingData(int fold);


    /**
     * Normalises the dataset.
     */
    public abstract void normalise();

    public String toString() {
        return getName() + " N=" + getTrainingSize();
    }

    public static void resetDifficulties(Vector<Data> trainingData) {
        for (int i = 0; i < trainingData.size(); i++) {
            Data data = trainingData.elementAt(i);
            //data.difficulty = Data.DEFAULT_DIFFICULTY;
        }
    }

}
