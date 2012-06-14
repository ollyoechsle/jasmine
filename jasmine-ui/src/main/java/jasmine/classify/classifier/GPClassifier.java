package jasmine.classify.classifier;


import jasmine.gp.Individual;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.nodes.DataValueTerminal;
import jasmine.gp.problems.DataStack;
import java.util.Vector;

/**
 * GP Classifier. Uses a GP program to do classifications
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class GPClassifier extends Classifier {

    public static boolean REFINEMENT_ON = true;

    public Individual ind;
    public Vector<Classifier> FNRefinements;
    public Vector<Classifier> FPRefinements;

    public boolean lastFNRefinementFailed = false;
    public boolean lastFPRefinementFailed = false;

    public int classID;


    public GPClassifier(Individual ind) {
        this.ind = ind;
        FNRefinements = new Vector<Classifier>();
        FPRefinements = new Vector<Classifier>();
    }

    public GPClassifier(int classID, Individual ind) {
        this.classID = classID;
        this.ind = ind;
        FNRefinements = new Vector<Classifier>();
        FPRefinements = new Vector<Classifier>();
    }

    public int classify(DataStack dataStack) {
        double rawOutput = ind.execute(dataStack);
        if (ind.getPCM() == null) {
            return (int) rawOutput;
        } else {
            return ind.getPCM().getClassFromOutput(rawOutput);
        }
    }

    public int getSize() {
        return ind.getTreeSize();
    }

    public int classify(float[] values) {
//System.out.println("GPClassifier");
        DataValueTerminal.currentValues = values;

        if (!REFINEMENT_ON) {
            return getOutput(ind);
        }

        int currentDecision = getOutput(ind);

        // loop until our FN refinement confirms that it is a negative
        for (int i = 0; i < FNRefinements.size(); i++) {
            Classifier FNRefinement = FNRefinements.elementAt(i);
            if (currentDecision == 0) {
                currentDecision = FNRefinement.classify(values);
            } else {
                break;
            }
        }

        // loop until our FP refinements confirm that it is positive
        for (int i = 0; i < FPRefinements.size(); i++) {
            Classifier FPRefinement = FPRefinements.elementAt(i);
            if (currentDecision != 0) {
                currentDecision = FPRefinement.classify(values);
            } else {
                break;
            }
        }

        return currentDecision;
    }

    private double rawOutput;

    public static DataStack dataStack = new DataStack();

    public int getOutput(Individual ind)  {
        rawOutput = ind.execute(dataStack);
        if (ind.getPCM() == null) {
            return (int) rawOutput;
        } else {
            return ind.getPCM().getClassFromOutput(rawOutput);
        }
    }

    public double getRawOutput(float[] values) {
        DataValueTerminal.currentValues = values;
        return ind.execute(dataStack);
    }

    public double getRawOutput(DataStack dataStack) {
        return ind.execute(dataStack);
    }

    public float getConfidence(double rawOutput, int classID) {
        if (ind.getPCM() == null ||!(ind.getPCM() instanceof BetterDRS)) {
            return 1;
        } else {
            return (ind.getPCM()).getConfidence(classID, rawOutput);
        }
    }

    public String toJava(String modifiers, String methodName, String arguments) {
        String methodSignature = modifiers  + " " + methodName + "(" + arguments + ")";
        return ind.toJava(methodSignature);
    }

    public String toString() {
        return "Single GP Individual";
    }

}
