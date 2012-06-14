package jasmine.kmeans;

import java.util.Vector;
import java.text.DecimalFormat;

/**
 * Represents a position in n dimensional feature space.
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Apr-2007
 * @version 1.0
 */
public class Position {

    /**
     * The position is stored as an array of double values.
     */
    protected double[] values;

    /**
     * Initialises the position with an array of double values, each of which represents
     * the value of one feature.
     */
    public Position(double[] values) {
        this.values = values;
    }

    /**
     * Finds the Euclidian distance from this position to another position
     * which is the root sum of squared differences.
     */
    public double getEuclideanDistanceTo(Position other) {

        double sum = 0;
        //POEY comment: values.length = the number of extraction functions
        //find different values of every object's values in each function (round-robin tournament event itself)
        for (int i = 0; i < values.length; i++) {
            double value = values[i];
            double otherValue = other.values[i];
            double diff = value - otherValue;
            sum += (diff * diff);
        }

        return Math.sqrt(sum);
        
    }

    /**
     * Finds the average position among a set of points' positions and then
     * updates the current position to the average position.
     */
    public void updateToAverageOf(Vector<DataPoint> objects) {   	
        double[] average = new double[values.length];
        for (int i = 0; i < objects.size(); i++) {
        	//POEY comment: position is a calculated value of a pixel
            Position position =  objects.elementAt(i).position;
            for (int j = 0; j < average.length; j++) {
                average[j] += position.values[j];
            }
        }
        for (int i = 0; i < average.length; i++) {
        	//POEY comment: objects.size() = the number of members in a cluster
            average[i] /= objects.size();
        }
        values = average;
    }

    /**
     * Makes a copy of this position so that it can be used somewhere else without
     * updating this object's position by accident.
     */
    public Position copy() {
        return new Position(values.clone());
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        DecimalFormat format = new DecimalFormat("0.00");
        for (double value : values) {
            buffer.append(format.format(value));
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public double get(int index) {
        return values[index];
    }

}
