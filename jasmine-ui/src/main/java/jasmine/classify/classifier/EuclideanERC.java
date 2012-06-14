package jasmine.classify.classifier;


import jasmine.classify.data.Data;
import jasmine.gp.Evolve;
import jasmine.gp.nodes.DataValueTerminal;
import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;

import java.util.Vector;

/**
 * Finds the distance of the current feature from one item of training data
 */
public class EuclideanERC extends BasicERC {

    protected int index;
    public static Vector<Data> trainingData;

    public EuclideanERC() {
        initialise();
    }

    public EuclideanERC(int index) {
        this.index = index;
    }

    public void jitter() {
        // find similar one using tournament selection
        double bestSimilarity = Double.MIN_VALUE;
        int bestIndex = index;
        float[] currentValues = trainingData.elementAt(index).values;
        for (int i = 0; i < 7; i++) {
            int newIndex = (int) (Evolve.getRandomNumber() * trainingData.size());
            double similarity = euclid(currentValues, trainingData.elementAt(newIndex).values);
            if (similarity < bestSimilarity) {
                bestSimilarity = similarity;
                bestIndex = newIndex;
            }

        }
        index = bestIndex;
    }

    public double initialise() {   	
        index = (int) (Evolve.getRandomNumber() * trainingData.size());
        return index;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = euclid(DataValueTerminal.currentValues, trainingData.elementAt(index).values);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this terminal is copied.
        return new Object[]{index};
    }

    public static double euclid(float[] point1, float[] point2) {
        double euclid = 0;
        for (int i = 0; i < point1.length; i++) {
            double diff = (point1[i] - point2[i]);
            euclid += (diff * diff);
        }
        return Math.sqrt(euclid);
    }

    public String toJava() {
       return "Euclidean.euclid(features, training[" + index + "])";
    }

    public String getShortName() {
        return "euclid[" + index + "])";
    }

}
