package jasmine.classify.evaluation;

/**
* Represents a class confusion, or false positive, where a class
* was incorrectly recognised as another one.
*
* @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
* @version 1.0
*/
public class ClassConfusion implements Comparable {

    public int classID;
    public int n;
    public float percentage;

    public ClassConfusion(int classID, int n, float percentage) {
        this.classID = classID;
        this.n = n;
        this.percentage = percentage;
    }

    public int compareTo(Object o) {
        ClassConfusion other = (ClassConfusion) o;
        if (other.percentage > this.percentage) return +1;
        if (other.percentage < this.percentage) return -1;
        return 0;
    }

    public String toString() {
        return "Confused with: " + classID + " " + (percentage * 100) + " % ( " + n + " times)";
    }

}
