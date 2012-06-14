package jasmine.gp.multiclass;

import java.text.DecimalFormat;

/**
 * Class which holds data about a particular class's results.
*
* @author Olly Oechsle, University of Essex, Date: 09-Feb-2007
* @version 1.0
*/
public final class ClassResult implements Comparable {

    protected static DecimalFormat format;

    public String name;

    public int classID;

    public int correct;

    public int total;


    public ClassResult(String name, int classID) {
        this.name = name;
        this.classID = classID;
        this.correct = 0;
        this.total = 0;
    }

    public void registerHit() {
        correct++;
        total++;       
    }

    public void registerMiss() {    	
        total++;
    }

    public int getHits() {
        return correct;
    }

    public int getTotal() {
        return total;
    }

    public int getMistakes() {
        return total - correct;
    }

    public String getPercentage() {
        if (total == 0) return "-";
        if (format == null);
        format = new DecimalFormat("0.00");
        return format.format(100*correct/(float) total);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassResult that = (ClassResult) o;

        if (classID != that.classID) return false;

        return true;
    }

    public int compareTo(Object o) {
        int otherClass = ((ClassResult) o).classID;
        if (otherClass > classID) {
            return -1;
        }
        if (otherClass < classID) {
            return +1;
        }
        return 0;
    }

    public int hashCode() {
        return classID;
    }

}
