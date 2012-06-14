package jasmine.kmeans;

/**
 * A basic class object with an ID and a name that allows some class information
 * to be attached to each currentCentroid. This allows the solution to be run backwards and
 * run classification on unseen data.
 */
public class ClusterClass {

    /**
     * The name of the class
     */
    public String name;

    /**
     * A numeric ID assigned to the class
     */
    public int classID;

    public ClusterClass(int id, String name) {
        this.name = name;
        this.classID = id;
    }
    
}
