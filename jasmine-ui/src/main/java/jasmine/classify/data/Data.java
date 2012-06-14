package jasmine.classify.data;


import java.io.Serializable;

/**
 * Represents a sample of data
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class Data extends AdaBoostSample implements Serializable {

    public static final float DEFAULT_DIFFICULTY = 1;

    public float values[];
    public String className;
    public int classID = -1;
    public int fold = -1;
    public boolean type = DataPartitioner.TRAINING;
    public float weight;

    //public int mistakes = 0;
    //public static int totalMistakes = 0;

    public Data(float[] data, String className) {
        super(null);
        this.values = data;
        this.className = className.trim().toLowerCase();
        this.weight = 1f;
    }

    public double[] getValuesDouble() {
        double[] dv = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            dv[i] = values[i];
        }
        return dv;
    }

    public Data(float[] data, String className, int classID) {
        super(null);
        this.values = data;
        if (className != null) this.className = className.trim().toLowerCase();
        this.classID = classID;
        this.weight = 1f;
    }

    public int getColumnCount() {
        return values.length;
    }
    
    public Object getData() {
        return values;
    }

    public int getLabel() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

/*    public float difficulty = DEFAULT_DIFFICULTY;

    public float getDifficulty() {
        return difficulty;
    }

    public void calculateDifficulty(int trainingSize, float learningRate) {
        // calculate a weight related to the number of mistakes
        if (totalMistakes == 0) difficulty = 1;
        else {
            float newDifficulty = (mistakes / (float) (totalMistakes)) * trainingSize;
            // each item will never go underneath 25% weighting.
            newDifficulty = (0.3f * newDifficulty) + 0.70f;

            // tend towards the new difficulty
            difficulty += (newDifficulty - difficulty) * learningRate;
            if (difficulty < 0) difficulty = 0;
        }
    }*/

    public String toString() {
        return "Data | class=" + classID + " values=" + values;
    }

}
