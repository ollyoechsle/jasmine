package jasmine.classify.classifier;


import jasmine.classify.data.ClassIDMapping;
import jasmine.classify.data.Data;
import jasmine.gp.problems.DataStack;

import java.io.*;
import java.util.Vector;

/**
 * Abstract classifier interface
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public abstract class Classifier implements Serializable {

    public int FN = 0;
    public int FP = 0;

    public float weight = 1;

    protected ClassIDMapping mapping;

    public int classify(Data d) {
        return classify(d.values);
    }

    public int classify(DataStack ds) {
        return -1;
    }

    public ClassIDMapping getMapping() {
        return mapping;
    }

    public void setMapping(ClassIDMapping mapping) {
        this.mapping = mapping;
    }

    public abstract int classify(float[] values);

    /**
     * Saves the individual to disk in serialised form. This is primarily
     * to allow the JavaWriter output to be compared with the actual individual
     * in a debugging environment.
     */
    public void save(File f) {
        try {
            System.out.println("Saving Classifier: " + f.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toJava(String modifiers, String methodName, String arguments) {
        return "";
    }

    public int getSize() {
        return 0;
    }

    /**
     * Loads a serialised individual from disk. Mainly used for debugging the
     * Java Writer.
     */
    public static Classifier load(File f) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream in = new ObjectInputStream(fis);
        Classifier individual = (Classifier) in.readObject();
        in.close();
        return individual;
    }

    public synchronized float test(Vector<Data> data) {
        int N = 0;
        int TP = 0;
        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);
            N++;
            if (classify(d) == d.classID) {
                TP++;
            }
        }
        return TP / (float) N;
    }



    public synchronized int[] getHits(Vector<Data> data, int fold) {

        int TP = 0;
        int FP = 0;
        int TN = 0;
        int FN = 0;

        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);

            if (d.fold != fold) continue;

            int outputClass = classify(d);

            if (outputClass == 0) {
                // returned false
                if (d.classID == 0) {
                    TN++;
                } else {
                    FN++;
                }
            } else {
                if (outputClass == d.classID) {
                    TP++;
                } else {
                    FP++;
                }
            }

        }

        return new int[]{TP, FP, TN, FN};

    }

    public synchronized int[] getHits(Vector<Data> data) {

        int TP = 0;
        int FP = 0;
        int TN = 0;
        int FN = 0;

        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);

            if (d.weight == 0) continue;

            int outputClass = classify(d);

            if (outputClass == 0) {
                // returned false
                if (d.classID == 0) {
                    TN++;
                } else {
                    FN++;
                }
            } else {
                if (outputClass == d.classID) {
                    TP++;
                } else {
                    FP++;
                }
            }

        }

        return new int[]{TP, FP, TN, FN};

    }

    public synchronized double getKozaFitness(Vector<Data> data) {

        int[] results = getHits(data);
        int FP = results[1];
        int FN = results[3];
        int N = data.size();

        return (FP + FN) / (double) N;

    }

   public synchronized int[] getHitsOnSpecificDataType(Vector<Data> data, boolean type) {

        int TP = 0;
        int FP = 0;
        int TN = 0;
        int FN = 0;

        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);

            if (d.weight == 0) continue;
            if (d.type != type) continue;

            int outputClass = classify(d);

            if (outputClass == 0) {
                // returned false
                if (d.classID == 0) {
                    TN++;
                } else {
                    FN++;
                }
            } else {
                if (outputClass == d.classID) {
                    TP++;
                } else {
                    FP++;
                }
            }

        }

        return new int[]{TP, FP, TN, FN};

    }

    public synchronized double getKozaFitnessOnSpecificDataType(Vector<Data> data, boolean dataType) {

        int[] results = getHitsOnSpecificDataType(data, dataType);
        int FP = results[1];
        int FN = results[3];
        int N = data.size();

        return (FP + FN) / (double) N;

    }

}
