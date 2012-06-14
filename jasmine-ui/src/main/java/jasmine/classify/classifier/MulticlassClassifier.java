package jasmine.classify.classifier;

import jasmine.gp.problems.DataStack;

import java.io.*;

/**
 * Executes several classifiers in one go
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class MulticlassClassifier extends Classifier implements Serializable {

    public Classifier[] classifiers;
    public int defaultClass = 0;

    public MulticlassClassifier(int numClasses) {
       classifiers = new Classifier[numClasses];
    }

    public void set(Classifier classifier, int index) {
        classifiers[index] = classifier;
    }

    public int classify(DataStack ds) {
        for (int i = 0; i < classifiers.length; i++) {
            Classifier classifier = classifiers[i];
            if (classifier == null) continue;
            int result = classifier.classify(ds);
            if (result > 0) {
                return result;
            }
        }
        return defaultClass;
    }

    public int classify(float[] values) {
//System.out.println("MulticlassClassifier");
        for (int i = 0; i < classifiers.length; i++) {
            Classifier classifier = classifiers[i];
            if (classifier == null) continue;
            int result = classifier.classify(values);
            if (result > 0) {
                return result;
            }
        }
        return defaultClass;
    }

}
