package jasmine.imaging.commons;

/**
 * Calculates an optimal threshold using the entropy thresholding (Kapur et al, 1985)
 */
public class EntropyThresholder extends Thresholder {

    public EntropyThresholder(int max) {
        super(max);
    }

    public int getOptimalThreshold() {

        // we will find t that maximises the entropy
        int argmax_t = 0;
        double max = Double.MIN_VALUE;

        // probability of class A
        double p0 = 0, p1 = 0;

        // find the optimal value of t       
        for (int t = 0; t < hist.length; t++)  {

            // probability of class 0
            p0 += getNormalised(t);

            // probability of class 1
            p1 = 1 - p0;

            // don't bother if either has zero probability
            if (p0 == 0 || p1 == 0) continue;

            // entropy for each class
            double entropy0 = 0;
            double entropy1 = 0;

            // entropy for class 0
            for (int i = 0; i <= t; i++) {
                double p = getNormalised(i) / p0;
                if (p > 0)
                entropy0 -= p * Math.log(p);
            }

            // entropy for class 1
            for (int i = t+1; i < hist.length; i++) {
                double p = getNormalised(i) / p1;
                if (p > 0)
                entropy1 -= p * Math.log(p);
            }

            // total entropy
            double totalEntropy = entropy0 + entropy1;

            // find t that maximises the entropy
            if (totalEntropy > max) {
                max = totalEntropy;
                argmax_t = t;
            }

        }

        return argmax_t;

    }

}