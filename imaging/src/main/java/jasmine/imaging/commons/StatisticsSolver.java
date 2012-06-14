package jasmine.imaging.commons;

import org.jfree.data.xy.XYSeries;
import java.util.Hashtable;
import java.util.Arrays;

/**
 * Statistics Solver
 * A quick implementation of a few statistical techniques. Instantiate the StatisticsSolver
 * and then call the addData(float) method to insert as many values as required.
 * You can then call some of the Statistical Methods on the values, such as getStandardDeviation()
 * or get Mean();
 * @version 1.1 07-Nov-2006 Added support for float data.
 * @version 1.2 22-Jan-2007 Removed arraylist and replaced with custom primitive array for better performance.
 */
public class StatisticsSolver {

    private float[] v;
    private int cursor;
    public float total = -1;

    protected int numElements;
    protected float highest = Float.MIN_VALUE;
    protected float lowest = Float.MAX_VALUE;

    public float mean = -1;
    public float variance = -1;

    public static final int TOTAL = 1;
    public static final int MIN = 2;
    public static final int MAX = 3;
    public static final int RANGE = 4;
    public static final int MEAN = 5;
    public static final int VARIANCE = 6;
    public static final int STANDARD_DEVIATION = 7;
    public static final int MODE = 8;

    /**
     * Allows you to get the statistic by a numeric type instead of calling the right method.
     * Useful if you want to choose which method during runtime.
     */
    public float getStatistic(int type) {
        switch (type) {
            case TOTAL:
                return getTotal();
            case MIN:
                return getMin();
            case MAX:
                return getMax();
            case RANGE:
                return getRange();
            case MEAN:
                return getMean();
            case MODE:
                return getMode();
            case VARIANCE:
                return getVariance();
            case STANDARD_DEVIATION:
                return getStandardDeviation();
        }
        throw new RuntimeException("Invalid statistic type: " + type);
    }

    /**
     * Returns the name of each type, if you want it as a java variable.
     */
    public static String getStatisticName(int type)  {
        switch (type) {
            case TOTAL:
                return "StatisticsSolver.TOTAL";
            case MIN:
                return "StatisticsSolver.MIN";
            case MAX:
                return "StatisticsSolver.MAX";
            case RANGE:
                return "StatisticsSolver.RANGE";
            case MEAN:
                return "StatisticsSolver.MEAN";
            case MODE:
                return "StatisticsSolver.MODE";
            case VARIANCE:
                return "StatisticsSolver.VARIANCE";
            case STANDARD_DEVIATION:
                return "StatisticsSolver.STANDARD_DEVIATION";
        }
        throw new RuntimeException("Unknown statistic type: " + type);        
    }

//    public static void main(String[] args) {
//        StatisticsSolver s = new StatisticsSolver();
//        for (int i =0; i < 10; i++) {
//            s.addData(4);
//            s.addData(2);
//            s.addData(1);
//            s.addData(3);
//            s.addData(5);
//            System.out.println(s.getRange());
//            System.out.println(s.getMean());
//            System.out.println(s.getMedian());
//            s.clear();
//            s.addData(5);
//            s.addData(5);
//            s.addData(5);
//            s.addData(5);
//            s.addData(5);
//            System.out.println(s.getRange());
//            System.out.println(s.getMean());
//            System.out.println(s.getMedian());
//            s.clear();
//        }
//    }



    /**
     * Initialises the solver, initially with space for 20 items of data.
     */
    public StatisticsSolver() {
        this(20);
    }

    /**
     * Returns how many items of data are in the statistics solver
     */
    public int size() {
        return numElements;
    }

    /**
     * Initialises the solver
     */
    public StatisticsSolver(int size) {
        v = new float[size + 1];
        cursor = 0;
    }

    /**
     * Gets the statistics solver as a series so it can be plotted by JFreeChart.
     * @param name
     * @return
     */
    public XYSeries getXYSeries(String name) {
        XYSeries series = new XYSeries(name);
        for (int i = 0; i < v.length; i++) {
            float v1 = v[i];
            series.add(i, v1);
        }
        return series;
    }

    /**
     * Returns a percentage term of how much two distributions overlap, with
     * 100% meaning that this distribution is entirely inside the other distribution
     * and 0% indicating that they share no points in common at all.
     */
    public float getDistributionOverlapWith(StatisticsSolver other) {

        if (other.numElements != this.numElements) {
            throw new RuntimeException("Distribution Overlap failed: Both statistics objects need the same number of entries.");
        }

        float total = 0, share_amount = 0;
        for (int i = 0; i < numElements; i++) {
            float v1 = v[i];
            if (v1 > 0) {
                total += v1;
                if (other.v[i] > 0) {
                    share_amount += Math.min(v1, other.v[i]);
                }
            }
        }
        return total / share_amount;
    }

    public StatisticsSolver getDistribution(float min, float max, int steps) {

        float step = (max - min) / steps;

        StatisticsSolver distribution = new StatisticsSolver(steps);

        for (float x = min; x <= max; x += step) {
            distribution.addData(countDataInRange(x, x + step));
        }

        return distribution;

    }

    public int countDataInRange(float min, float max) {
        int total = 0;
        for (int i = 0; i < v.length; i++) {
            float v1 = v[i];
            if (v1 <= max && v1 >= min) total++;
        }
        return total;
    }

    /**
     * Adds a set of value to the statistics solver.
     */
    public void addData(float[] data) {
        for (int i = 0; i < data.length; i++) {
            addData(data[i]);
        }
    }

    /**
     *
     * @deprecated
     */
    public void addData(double data) {
        addData((float) data);
    }

    /**
     * Add a value to the statistics solver to be used in any calculations
     * @param data The number to save
     */
    public void addData(float data) {
        v[cursor] = data;

        // expand the array if necessary
        cursor++;
        if (cursor == v.length) {
            // increase the size of the array
            float[] newV = new float[v.length * 2];
            System.arraycopy(v,0,newV,0,v.length);
            v = newV;
            //System.out.println("Statistics Solver - ArrayCopy");
        }

        if (data > highest) highest = data;
        if (data < lowest) lowest = data;

        numElements++;

        variance = -1;
        mean = -1;
        total = -1;

    }

    /**
     * Calculates the correlation between this set of data and the data
     * in another statistics solver.
     */
    public double getCorrelationWith(StatisticsSolver other) {

        if (other.numElements != this.numElements) {
            throw new RuntimeException("Correlation failed: Both statistics objects need the same number of entries.");
        }

        float sumXY = 0;
        float sumX = 0;
        float sumY = 0;
        float sumXSquared = 0;
        float sumYSquared = 0;
        float N = numElements;
        for (int i = 0; i < numElements; i++) {
            sumXY += v[i] * other.v[i];
            sumX += v[i];
            sumY += other.v[i];
            sumXSquared += v[i] * v[i];
            sumYSquared += other.v[i] * other.v[i];
        }

        float numerator = sumXY - ((sumX * sumY) / N);
        double denominator = Math.sqrt((sumXSquared - ((sumX * sumX) / N)) * (sumYSquared - ((sumY * sumY) / N)));

        return numerator / denominator;

    }

    /**
     * @return Returns the sum of all the values stored in the StatistcsSolver.
     */
    public float getTotal() {
        if (total == -1) {
            total = 0;
            for (int i = 0; i < numElements; i++) {
                total += v[i];
            }
        }
        return total;
    }


    /**
     * @return Gets the highest value
     */
    public float getMax() {
        return highest;
    }

    /**
     * @return Gets the lowest value in the solver.
     */
    public float getMin() {
        return lowest;
    }

    public float getRange() {
        if (numElements == 0) return 0;
        return highest - lowest;
    }

    /**
     * @return Returns the average of all the values stored in the StatistcsSolver.
     * @deprecated Use the FastStatistics object instead.
     */
    public float getMean() {
        if (mean == -1) {
            if (numElements == 0) mean = 0;
            else mean = getTotal() / numElements;
        }
        return mean;
    }

    public float getMedian() {
        if (numElements == 0) return 0;
        // have to sort the elements
        float[] elements = new float[numElements];
        System.arraycopy(v, 0, elements, 0, numElements);
        Arrays.sort(elements);
        if (numElements % 2 == 0) {
            int mid = numElements / 2;
            return (elements[mid] + elements[mid - 1]) / 2;
        } else {
            return elements[numElements / 2];
        }
    }



    /**
     * Calculates the sample variance
     * @return Returns the variance between all the values stored in the StatistcsSolver.
     * @deprecated Use the FastStatistics object instead.
     */
    public float getVariance() {
        if (variance == -1) {
            float mean = getMean();
            float numerator = 0;
            for (int i = 0; i < numElements; i++) {
                float d = v[i] - mean;
                numerator += d * d;
            }
            variance = numerator / (numElements - 1);
        }
        return variance;
    }    

    /**
     * @return Returns the Standard Deviation of the values, which is the Square Root
     * of the Variance.
     * @deprecated Use the FastStatistics object instead.*
     */
    public float getStandardDeviation() {
        return (float) Math.sqrt(getVariance());
    }

    /**
     * @return Gets the most popular unique value in the set.
     */
    public float getMode() {
        Hashtable<Float, Integer> colours = new Hashtable<Float, Integer>(255);
        float mode = -1;
        int highestColourCount = 0;
        for (int i = 0; i < numElements; i++) {

            // see if there is already an entry for this colour
            Integer entry = colours.get(v[i]);

            if (entry != null) {
                colours.put(v[i], entry + 1);
                if ((entry + 1) > highestColourCount) {
                    mode = v[i];
                    highestColourCount = entry + 1;
                }
            } else {
                colours.put(v[i], 1);
            }

        }

        return mode;
    }

    /**
     * @return Counts the number of unique values that have been added to the solver.
     */
    public int countUnique() {
        Hashtable<Float, Integer> colours = new Hashtable<Float, Integer>(255);

        for (int i = 0; i < numElements; i++) {

            // see if there is already an entry for this colour
            Integer entry = colours.get(v[i]);

            // if not, make one
            if (entry == null) colours.put(v[i], 1);

        }

        return colours.size();
    }




    /**
     * Clears all values so StatisticsSolver can be used again from scratch.
     */
    public void clear() {
        highest = Float.MIN_VALUE;
        lowest = Float.MAX_VALUE;
        variance = -1;
        mean = -1;
        total = -1;
        numElements = 0;
        cursor = 0;
    }

}
