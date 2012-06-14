package jasmine.imaging.gp.individuals;

import jasmine.gp.Individual;
import jasmine.gp.problems.DataStack;

/**
 * A weak learner is associated with a class for which it either returns true or false.
 *
 * @author Olly Oechsle, University of Essex, Date: 11-May-2007
 * @version 1.0
 */
public class WeakLearner {

    protected int classID;

    // TODO: change to protected
    public Individual ind;

    protected boolean invert;

    // TODO: Remove;
    public String results;
    public String code;
    public String pcm;

    public WeakLearner(int classID, Individual ind, String results)  {
        this(classID, ind, false, results);
    }

    public WeakLearner(int classID, Individual ind, boolean invert) {
        this.classID = classID;
        this.ind = ind;
        this.invert = invert;
        this.results = null;
    }

    public WeakLearner(int classID, Individual ind, boolean invert, String results) {
        this.classID = classID;
        this.ind = ind;
        this.invert = invert;
        this.results = results;
    }

    public boolean execute(DataStack stack) {

        if (ind.getPCM() != null) {

           int result = ind.getPCM().getClassFromOutput(ind.execute(stack));

           if (invert) {
               if (result == -1) return true;
           } else {
               if (result != -1) return true;
           }

        } else {

            if ((int) ind.execute(stack) == (invert ? 0 : 1)) {
                return true;
            }

        }

        return false;

    }

    public boolean executeWithoutInversion(DataStack stack) {

        if (ind.getPCM() != null) {

           int result =  ind.getPCM().getClassFromOutput(ind.execute(stack));
           if (result != -1) return true;

        } else {

            int result = (int) ind.execute(stack);
            return result == 1;

        }

        return false;

    }

    public int getClassID() {
        return classID;
    }

    public String toString() {
        return "Weak Learner, Class=" + classID + ", Invert=" + invert;
    }

}
