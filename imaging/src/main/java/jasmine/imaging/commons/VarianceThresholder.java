package jasmine.imaging.commons;

/**
 * Calculates an optimal threshold using the technique of Otsu (1979)
 */
public class VarianceThresholder extends Thresholder {

    public VarianceThresholder(int max) {
        super(max);
    }

    public int getOptimalThreshold() {

        int argmax_t = 0;
        double max = Double.MIN_VALUE;

        // find the total mean first
        double muT = 0;
        for (int i = 0; i < hist.length; i++) {
            muT += i * getNormalised(i);
        }

        // probabilities for each class
        double p0 = 0, p1 = 0;        

        // find the optimal value of t
        for (int t = 0; t < hist.length; t++) {

            // probability of class A
            p0 += getNormalised(t);

            // probability of class B
            p1 = 1 - p0;

            // mean for class 0
            double mu0 = 0;
            for (int i = 0; i <= t; i++) {
                mu0 += (i * getNormalised(i)) / p0;
            }

            // mean for class 0
            double mu1 = 0;
            for (int i = t + 1; i < hist.length; i++) {
                mu1 += (i * getNormalised(i)) / p1;
            }
            
            // between class variance
            double variance0 = p0 * ((mu0 - muT) * (mu0 - muT));
            double variance1 = p1 * ((mu1 - muT) * (mu1 - muT));
            double betweenClassVariance = variance0 + variance1;

            // find t that maximises the between class variance
            if (betweenClassVariance > max) {
                max = betweenClassVariance;
                argmax_t = t;
            }

        }

        return argmax_t;

    }

}
