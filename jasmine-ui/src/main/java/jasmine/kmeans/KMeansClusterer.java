package jasmine.kmeans;

import java.util.Vector;

/**
 * An implementation of the K means clustering algorith, which attempts to find the centroids assumed to be at
 * the center of spherical clusters of points in n dimensional feature space.
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Apr-2007
 * @version 1.0
 */
public class KMeansClusterer extends Thread {

    protected Vector<Cluster> clusters;
    protected Vector<DataPoint> points;
    protected Vector<Integer> classes;

    private int highestClassID = 0;

    protected int k = 10;

    public boolean verbose = true;

    /**
     * Initialises the K Means Clusterer
     * @param k The number of clusters to use.
     */
    public KMeansClusterer(int k) {
        this.classes = new Vector<Integer>(20);
        this.k = k;
        points = new Vector<DataPoint>(100);
    }

    public void add(DataPoint obj) {
        points.add(obj);
        //POEY comment: obj.knownClassID = -1 (initial value)        
        if (!classes.contains(obj.knownClassID)) addClass(obj.knownClassID);
    }

    public void addClass(int classID)  {
        classes.add(classID);
        if (classID > highestClassID) highestClassID = classID;
    }

    public void run() {

        boolean uninitialised = true;
        int movements;

        do {

            if (uninitialised) {
                // initialise the centroids' positions with random positions from the data
            	//POEY comment: k = the number of pixels' classes * 2
                clusters = new Vector<Cluster>(k);
                for (int i = 0; i < k; i++) {
                	//POEY comment: points.size()= the number of all selected pixels
                	//each clusters contains n calculated values at index Math.random()*points.size()
                	//n = the number of extraction functions
                	Position p = points.elementAt((int) (Math.random() * points.size())).position;
                    clusters.add(new Cluster(p.copy()));                    
                }  
                uninitialised = false;
            } else {
                // look at the members of each currentCentroid and update the currentCentroid's location to be in the middle           	
				//POEY comment: clusters.size() = k            	
                for (int i = 0; i < clusters.size(); i++) {
                    Cluster cluster = clusters.elementAt(i);
                    // move the currentCentroid position to the average position of its members                     
                    cluster.position.updateToAverageOf(cluster.members);
                    // clear out the members of the currentCentroid
                    cluster.clearPoints();
                }
            }

            // reset movements;
            movements = 0;

            // for each object, find its closest currentCentroid
            //POEY comment: it is not clear how to group pixels in each cluster ???
            for (int i = 0; i < points.size(); i++) {
                DataPoint point = points.elementAt(i);
                Cluster closestCluster = point.findClosestCentroid(clusters);

                if (closestCluster == null) {
                    System.err.println("Could not find closest centroid. Some of your points have positions equalling infinity.");
                }

                // if the object moves to a different currentCentroid
                if (closestCluster != point.currentCluster) {
                    movements++;
                }

                closestCluster.registerPoint(point);              
            }

            // go through each cluster and figure out what class it should return
            for (int i = 0; i < clusters.size(); i++) {
                Cluster cluster = clusters.elementAt(i);
                Vector<DataPoint> members = cluster.getMembers();               
                int[] classCounts = new int[highestClassID + 1];
                boolean classesFound = false;

                for (int j = 0; j < members.size(); j++) {
                    DataPoint member = members.elementAt(j);
                    //POEY comment: member.getClassID() = -1                   
                    if (member.getClassID() > -1) {
                        classCounts[member.getClassID()]++;
                        classesFound = true;
                    }
                }

                if (classesFound) {
                    int highestClassCount = 0;

                    for (int j = 0; j < classCounts.length; j++) {
                        int classCount = classCounts[j];
                        if (classCount > highestClassCount)  {
                            cluster.setClassID(j);
                        }
                    }

                } else { 	//POEY comment: it should be this case               	
                    cluster.setClassID(i);
                }
            }

            message(".");

        } while(movements > 0);

        message("\nK-Means Clustering Complete.\n");

    }

    public void message(String message) {
        if (verbose) System.out.print(message);
    }

    /**
     * Gets the centroids which encode the solution to the problem.
     * @return
     */
    public Vector<Cluster> getCentroids() {
        return clusters;
    }

    /**
     * Returns a cluster solution object which allows the algorithm to be used as a classifier.
     * @see KMeansSolution
     */
    public KMeansSolution getSolution() {
        return new KMeansSolution(clusters);
    }

    /**
     * Finds a random point. Can be used to initialise the centroids' positions.    
     */
    public DataPoint getRandomPoint() {
        return (points.elementAt((int) (Math.random() * points.size())));
    }

    /**
     * Finds a random point with a given classID. Can be used in the initialisation of the K-means clusterer
     * as a heuristic to find a reasonable starting point for each currentCentroid.
     */
    public DataPoint getRandomPoint(int classID) {

        Vector<DataPoint> pointsWithClassID = new Vector<DataPoint>(10);
        for (int i = 0; i < points.size(); i++) {
            DataPoint dataPoint =  points.elementAt(i);
            if (dataPoint.knownClassID == classID) {
                pointsWithClassID.add(dataPoint);
            }
        }

        return (pointsWithClassID.elementAt((int) (Math.random() * pointsWithClassID.size())));

    }

}
