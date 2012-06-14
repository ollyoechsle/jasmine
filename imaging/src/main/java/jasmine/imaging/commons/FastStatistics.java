package jasmine.imaging.commons;


/**
 * A much faster way to calculate the standard deviation. There
 * is no need to store all the values of x.
 * <p/>
 * Formula taken from this excellent site:
 * http://www.sciencebuddies.org/mentoring/project_data_analysis_variance_std_deviation.shtml
 *
 * @author Olly Oechsle, University of Essex, Date: 06-Mar-2008
 * @version 1.0
 */
public class FastStatistics {

    private float sumXSquared = 0;
    private float totalX = 0;
    private int n = 0;
    private float max = Float.MIN_VALUE;
    private float min = Float.MAX_VALUE;

/*
    public static void main(String[] args) {
        StatisticsSolver s = new StatisticsSolver();
        FastStatistics f = new FastStatistics();
        for (int i = 0; i <100; i++) {
            for (float j = 10; j < 10.1; j+= 0.01) {
                System.out.println(j);
                f.addData(j);
                s.addData(j);
            }
        }
        System.out.println(s.getVariance());
        System.out.println(f.getVariance());
        System.out.println(f.getVariance2());
    }
*/

//    public static void main(String[] args) {
//
//        StatisticsSolver mean1 = new StatisticsSolver();
//
//        for (int n = 0; n < 10; n++) {
//
//            long start = System.currentTimeMillis();
//            for (int i = 0; i < 100000; i++) {
//                StatisticsSolver s = new StatisticsSolver();
//                for (int j = 0; j < 20; j++) {
//                    s.addData(j);
//                }
//                s.getVariance();
//            }
//
//            long time = System.currentTimeMillis() - start;
//            mean1.addData(time);
//
//            System.out.println(time + "ms");
//
//        }
//
//        System.out.println("Original Mean Time: " + mean1.getMean());
//
//        StatisticsSolver mean2 = new StatisticsSolver();
//
//        for (int n = 0; n < 10; n++) {
//
//            FastStatistics s = new FastStatistics();
//            long start = System.currentTimeMillis();
//            for (int i = 0; i < 100000; i++) {
//                for (int j = 0; j < 20; j++) {
//                    s.addData(j);
//                }
//                s.getVariance();
//                s.reset();
//            }
//            long time = System.currentTimeMillis() - start;
//            mean2.addData(time);
//
//            System.out.println(time + "ms");
//
//        }
//
//        System.out.println("Efficient Mean Time: " + mean2.getMean());
//
//    }

    /**
     * Allows you to get the statistic by a numeric type instead of calling the right method.
     * Useful if you want to choose which method during runtime.
     */
    public float getStatistic(int type) {
        switch (type) {
            case StatisticsSolver.TOTAL:	
                return getTotal();
            case StatisticsSolver.MEAN:
                return getMean();
            case StatisticsSolver.MAX:
                return getMax();
            case StatisticsSolver.MIN:
                return getMin();
            case StatisticsSolver.RANGE:
                return getRange();
            case StatisticsSolver.VARIANCE:
                return getVariance();
            case StatisticsSolver.STANDARD_DEVIATION:
                return getStandardDeviation();
        }
        throw new RuntimeException("Invalid statistic type: " + type);
    }

    private float k_mean;
    private float k_M2 = 0;

    public final void addData(float x) {

        totalX += x;
        if (x > max) max = x;
        if (x < min) min = x;
        n++;

        float k_delta = x - k_mean;       
        k_mean += k_delta / n;
        k_M2 += k_delta * (x - k_mean);

    }

    public int getN() {
        return n;
    }

    public float getTotal() {
        return totalX;
    }

    public float getMean() {
        return totalX / n;
    }


    /*  public float getVariance() {
        float mean = getMean();
        return (sumXSquared - (n * mean * mean)) / (n - 1);
    }*/

    public final float getVariance() { 
        return k_M2 / (n - 1);
    }
    
    //POEY
    public final float getVarianceCheck() { 
    	float value;
    	if(n>1){
    		value = k_M2 / (n - 1);    		
    		return value;
    	}
    	else  
    		return -1;
    }

    public float getStandardDeviation() {
        return (float) Math.sqrt(getVariance());
    }
    
    //POEY
    public float getStandardDeviationCheck() {
    	float value;
    	value = getVarianceCheck();
    	if(value != -1)
    		return (float) Math.sqrt(value);
    	else
    		return -1;
    }

    public float getRange() {    	
        return max - min;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }
    
    //POEY
    public float getNorm(float data) {
    	float value;
    	value = (data-getMin())/(getMax()-getMin());
        return value;
    }
    

    public void reset() {
        //sumXSquared = 0;
        k_mean = 0;
        k_M2 = 0;
        totalX = 0;
        n = 0;
    }

    public String toString() {
        return "mean=" + getMean() + ", min=" + getMin() + ", max=" + getMax() + ", sd=" + getStandardDeviation() + ", n=" + getN();
    }

}
