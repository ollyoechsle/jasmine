package jasmine.gp.multiclass;

import java.io.Serializable;

/**
 * Class used to cache the output of the program, so that we don't need to
 * evaluate it twice. (It has to be evaluated once to calculate the dynamic
 * range selection, and then again to get the classification, otherwise).
 */
public class CachedOutput implements Serializable {

    public double rawOutput;
    public int expectedClass;
    public double weight;

    public CachedOutput(double rawOutput, int expectedClass) {
        this(rawOutput, expectedClass, 1);
    }

    public CachedOutput(double rawOutput, int expectedClass, double weight) {
        this.rawOutput = rawOutput;
        this.expectedClass = expectedClass;
        this.weight = weight;
    }

    public String toString() {
        return "output= " + rawOutput + ", class=" + expectedClass;
    }

}
