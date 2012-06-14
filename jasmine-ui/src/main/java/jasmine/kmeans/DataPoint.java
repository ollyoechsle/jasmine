package jasmine.kmeans;

import java.util.Vector;

/**
 * This datastructure represents a point of training data in the k means clustering scenario.
 * It consists of a position in n dimensional space and the id of the last currentCentroid it was closest to.
 *
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Apr-2007
 * @version 1.0
 */
public class DataPoint {

    /**
     * The position of this point in n dimensional space
     */
    protected Position position;

    /**
     * The last cluster this point was closest to
     */
    protected Cluster currentCluster;

    /**
     * The known knownClassID of this point
     */
    protected int knownClassID;

    /**
     * An ID, in case you want to remember where this data point came from
     */
    protected int ID;


    /**
     * Initialises the clusterable point by setting its position in n dimensional space.
     * No classID is known.
     */
    public DataPoint(double[] positionValues) {
        this(positionValues, -1);
    }

    /**
     * Initialises the clusterable point by setting its position in n dimensional space,
     * together with a classID object.
     */
    public DataPoint(double[] positionValues, ClusterClass knownClass) {
        this(positionValues, knownClass.classID);
    }

    /**
     * Initialises the clusterable point by setting its position in n dimensional space,
     * together with a classID.
     */
    public DataPoint(double[] positionValues, int knownClassID) {
        this.position = new Position(positionValues);
        this.knownClassID = knownClassID;
        this.currentCluster = null;
    }

    /**
     * Finds, from a list of centroids, the currentCentroid that this point is closest to.
     */
    public Cluster findClosestCentroid(Vector<Cluster> clusters) {
        double closestDistance = Double.MAX_VALUE;
        Cluster closestCluster = null;
        //POEY comment: clusters.size() = the number of pixels' classes * 2        
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.elementAt(i);
            //POEY comment: cluster.position contains n centriods (n = the number of extraction functions)
            double distance = position.getEuclideanDistanceTo(cluster.position);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCluster = cluster;
            }
        }     
        return closestCluster;
    }

    /**
     * Gets the Euclidean distance between this point and another.
     * @param other
     * @return
     */
    public double getDistanceTo(DataPoint other) {
        return position.getEuclideanDistanceTo(other.position);
    }

    /**
     * Gets the classID of this data point
     * @return
     */
    public int getClassID() {
        return knownClassID;
    }

    /**
     * Sets the classID of this point.
     * @param classID
     */
    public void setClassID(int classID) {
        this.knownClassID = classID;
    }

    public Position getPosition() {
        return position;
    }

    public String toString() {
        return "POINT: " + position.toString() + ", cluster: " + currentCluster;
    }

    /**
     * Returns any ID associated with this data point.
     * @return
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets an ID to be associated with this data point.
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }

}
