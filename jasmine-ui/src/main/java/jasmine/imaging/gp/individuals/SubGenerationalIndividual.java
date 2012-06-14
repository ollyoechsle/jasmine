package jasmine.imaging.gp.individuals;


import jasmine.gp.Individual;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.javasource.JavaClass;

import java.util.Vector;

/**
 * This kind of individual is outputted by the SubGenerational Problem.
 * These individuals consist of a set of weak classifiers bound together.
 *
 * @author Olly Oechsle, University of Essex, Date: 10-May-2007
 * @version 1.0
 */
public class SubGenerationalIndividual extends Individual {

    public Vector<WeakLearner> weakLearners;

    JavaClass javaSource;

    public SubGenerationalIndividual(JavaClass javaSource, int returnType) {
        super(null, returnType);
        weakLearners = new Vector<WeakLearner>();
        this.javaSource = javaSource;
    }

    public void addWeakLearner(WeakLearner learner) {
        weakLearners.add(learner);
    }

    /**
     * Returns how many weak learners there are in this individual.
     * @return
     */
    public int getWeakLearnerCount() {
        return weakLearners.size();
    }

    public double execute(DataStack stack) {
        for (int i = 0; i < weakLearners.size(); i++) {
            WeakLearner weakLearner = weakLearners.elementAt(i);
            // TODO - need to reset the stack after executions?
            //if ((int) weakLearner.ind.execute(stack) == (weakLearner.invert ? 0 : 1)) {
            //    return weakLearner.classID;
            //}
            if (weakLearner.execute(stack)) return weakLearner.classID;
        }
        return -1;
    }

   
    public int test(JasmineProject project) {
        try {

            int TP = 0;

            // evaluate on all data
            Vector<ExtraShapeData> data = JasmineUtils.getTrainingData(project);

            for (int i = 0; i < data.size(); i++) {
                ExtraShapeData extraShapeData = data.elementAt(i);
                DataStack s = new DataStack();
                s.setData(extraShapeData);
                int classID = (int) execute(s);
                if (classID == extraShapeData.getShape().classID) {
                    TP++;
                }
            }

            return TP;
        } catch (Exception err) {
            System.err.println("// Can't run on unseen data.");
            err.printStackTrace();
        }
        return -1;
    }

    public String toJava() {
        return javaSource.toSource();
    }

    public String toString() {
        return toJava();
    }

}
