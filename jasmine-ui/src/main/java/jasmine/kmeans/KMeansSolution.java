package jasmine.kmeans;

import java.util.Vector;

/**
 * A utility class that allows the output of the K means clusterer to be tested on training or unseen data.
 *
 * @author Olly Oechsle, University of Essex, Date: 27-Apr-2007
 * @version 1.0
 */
public class KMeansSolution {

    /**
     * The centroids that were discovered after the K means clustering algorithm was run
     */
    protected Vector<Cluster> clusters;

    /**
     * Initialises the solution, giving it the list of centroids
     * that were discovered after the K means clustering algorithm was run
     */
    public KMeansSolution(Vector<Cluster> clusters) {
        this.clusters = clusters;
    }


    /**
     * Returns the likely class of an object based on its position in n dimensional space.
     */    
    public int test(DataPoint obj) {
        return test(obj.position.values);
    }

    /**
     * Returns the likely class of an object based on its position in n dimensional space.
     */
    public int test(double[] values) {
        DataPoint obj = new DataPoint(values, -1);
        Cluster closest = obj.findClosestCentroid(clusters);
        return closest.classID;
    }

    /**
     * Gets the clusters that make up this solution.
     */
    public Vector<Cluster> getClusters() {
        return clusters;
    }

}
