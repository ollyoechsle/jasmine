package jasmine.classify.data;

import java.util.Vector;
import java.io.Serializable;

public class DataNormaliser implements Serializable {

    private double[] factors;

    public DataNormaliser(Vector<Data> data) {
        int columns = data.elementAt(0).getColumnCount();

        double[] sums = new double[columns];
        factors = new double[columns];

        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);
            for (int j = 0; j < d.values.length; j++) {
                double value = d.values[j];
                sums[j] += (value * value);
            }
        }

        // calculate the normalisation factors
        for (int i = 0; i < sums.length; i++) {
            factors[i] = Math.sqrt(sums[i]);
        }

    }

    public void normalise(Vector<Data> data) {

        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);
            for (int j = 0; j < d.values.length; j++) {
                d.values[j] /= factors[j];
            }
        }

    }

}