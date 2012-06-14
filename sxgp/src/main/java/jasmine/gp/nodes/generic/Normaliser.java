package jasmine.gp.nodes.generic;

public class Normaliser {

    private boolean hasBeenCalculated = false;
    private double[] sums;
    private double[] factors;

    public Normaliser(int columns) {
        sums = new double[columns];
        factors = new double[columns];
    }

    public void addData(int column, double data) {
        sums[column] += (data * data);
        hasBeenCalculated = false;
    }

    private void calculateNormalisationFactors() {
        for (int i = 0; i < sums.length; i++) {
            factors[i] = Math.sqrt(sums[i]);
        }
        hasBeenCalculated = true;
    }

    public double getNormalisationFactor(int column) {
        if (!hasBeenCalculated) calculateNormalisationFactors();
        return factors[column];
    }

}


