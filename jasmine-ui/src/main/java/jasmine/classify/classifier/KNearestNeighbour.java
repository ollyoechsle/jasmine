package jasmine.classify.classifier;


import jasmine.kmeans.DataPoint;

import java.util.Vector;
import java.util.Collections;

/**
 * Implements a naive version of the K Nearest Neighbour Algorithm
 * TODO: Very slow
 * @author Olly Oechsle, University of Essex, Date: 25-Oct-2007
 * @version 1.0
 */
public class KNearestNeighbour {

//    public static void main(String[] args) {
//
//        int group1 = 1;
//        int group2 = 2;
//
//        DataPoint medicineA = new DataPoint(new double[]{1, 1}, group1);
//        DataPoint medicineB = new DataPoint(new double[]{2, 1}, group1);
//        DataPoint medicineC = new DataPoint(new double[]{4, 3}, group2);
//        DataPoint medicineD = new DataPoint(new double[]{5, 4}, group2);
//
//        KNearestNeighbour n = new KNearestNeighbour(1);
//
//        n.add(medicineA);
//        n.add(medicineB);
//        n.add(medicineC);
//        n.add(medicineD);
//
//        int output = n.classify(new double[]{1,2});
//
//        System.out.println("Output: " + output);
//
//
//    }

    public Vector<DataPoint> data;

    protected int k;

    public KNearestNeighbour(int k) {
        this.k = k;
        data = new Vector<DataPoint>(100);
    }

    public void add(DataPoint d)  {
        this.data.add(d);
    }

    public void add(double[] data, int classID)  {
        this.data.add(new DataPoint(data, classID));
    }

    public int classify(double[] data) {
        return classify(new DataPoint(data));
    }

    public int classify(DataPoint newSample) {

        Vector<CloseNeighbour> neighbours = new Vector<CloseNeighbour>(data.size());
        //POEY comment: data.size() = the number of objects
        for (int i = 0; i < data.size(); i++) {
            DataPoint neighbour = data.elementAt(i);
            //POEY comment: newSample contains calculated values
            //distance is the Euclidean distance (of all calculated values) between this point and another
            double distance = neighbour.getDistanceTo(newSample);
            neighbours.add(new CloseNeighbour(neighbour, distance));
        }

        Collections.sort(neighbours);

        // get the closest ones and find a consensus among them
        Vector<ConsensusClass> consensus = new Vector<ConsensusClass>();
        //POEY comment: k = 2       
        main: for (int i = 0; i < k; i++) {
            CloseNeighbour neighbour = neighbours.elementAt(i);
            //POEY comment: consensus.size() = 0 and 1 
            for (int j = 0; j < consensus.size(); j++) {
                ConsensusClass consensusClass = consensus.elementAt(j);
                if (consensusClass.classID == neighbour.d.getClassID()) {
                    consensusClass.votes++;
                    continue main;
                }
            }
            consensus.add(new ConsensusClass(neighbour.d.getClassID()));
        }

        // Find class with biggest consensus
        int mostVotes = 0;
        int classID = -1;
        for (int i = 0; i < consensus.size(); i++) {
            ConsensusClass consensusClass = consensus.elementAt(i);
            if (consensusClass.votes > mostVotes) {
                mostVotes = consensusClass.votes;
                classID = consensusClass.classID;
            }
        }

        return classID;

    }

    class ConsensusClass {

        int classID;
        int votes;

        public ConsensusClass(int classID) {
            this.classID = classID;
            this.votes = 1;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConsensusClass that = (ConsensusClass) o;

            if (classID != that.classID) return false;

            return true;
        }

        public int hashCode() {
            return classID;
        }

    }

    class CloseNeighbour implements Comparable {

        DataPoint d;
        double distance;

        public CloseNeighbour(DataPoint d, double distance) {
            this.d = d;
            this.distance = distance;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.<p>
         */
        public int compareTo(Object o) {
            CloseNeighbour n = (CloseNeighbour) o;
            if (this.distance < n.distance) return -1;
            if (this.distance > n.distance) return +1;
            return 0;
        }
    }

}
