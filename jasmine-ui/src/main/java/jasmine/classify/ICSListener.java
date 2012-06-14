package jasmine.classify;

import jasmine.classify.classifier.Classifier;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 24-Apr-2009
 * Time: 14:12:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class ICSListener {

    public ICS ics;

    int numClasses = 0;

    public abstract void onStart();

    public abstract void onStatusUpdate(String message);

    public void setNumClasses(int numClasses) {
        this.numClasses = numClasses;
    }

    public abstract void onLearnNewClass(int classID, double fitness);

    public void saveIndividual() {
        
    }

    public abstract void onClassifierUpdated(float trainingHits, float testingHits);

    public Classifier getBestIndividual() {
        return ics.multiclassClassifier;
    }

    public abstract void onFinish();

}
