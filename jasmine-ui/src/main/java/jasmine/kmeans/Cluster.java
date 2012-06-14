package jasmine.kmeans;

import java.util.Vector;

/**
 *
 * A Centroid is a position in N dimensional space, supposedly in the center
 * of a cluster of points. In this case it is represented by a position, a reference
 * to a cluster class (for classification) and a vector of members (for the training part)
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Apr-2007
 * @version 1.0
 */
public class Cluster {

    protected Position position;
    protected Vector<DataPoint> members;
    protected int classID;

    /**
     * Initialises the currentCentroid. Every currentCentroid has a position and is associated with a particular
     * class.
     * @param clusterClass The class that this currentCentroid attempts to
     * @param position The currentCentroid's position in n dimensional space
     */
    public Cluster(Position position) {
        this.classID = -1;
        this.position = position;
        members = new Vector<DataPoint>(10);
    }

    /**
     * Returns the members of the class represented by this currentCentroid.
     */
    public Vector<DataPoint> getMembers() {
        return members;
    }

    /**
     * Returns the position of this currentCentroid in n-dimensional space.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Registers an object whose closest currentCentroid is this one.
     * This information is used to update the Centroid's position.
     */
    public void registerPoint(DataPoint object) {
        members.add(object);
        object.currentCluster = this;
    }

    /**
     * Clears out all points registered with this currentCentroid.
     */
    public void clearPoints() {
        members.clear();
    }

    /**
     * Sets the classID of this centroid.
     */
    public void setClassID(int classID) {
        this.classID = classID;
    }


    public int getClassID() {
        return classID;
    }

    public String toString() {
        return "CENTROID:  at " + position.toString();
    }

}
