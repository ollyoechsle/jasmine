package jasmine.imaging.core.classification;


import jasmine.classify.ICS;
import jasmine.classify.classifier.Classifier;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataSet;
import jasmine.gp.nodes.DataValueTerminal;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.shapes.SegmentedObject;

import java.util.Vector;

/**
* Runs super classifier on the problem. Essentially just a wrapper.
 */
public class JasmineICSObject extends JasmineICS {

    protected Vector<Terminal> set;
    protected ICS ics;
    protected Vector<Data> d;
    Vector<SegmentedObject> trainingData;
    protected DataSet ds;

    public JasmineICSObject(JasmineProject project) {
        super(project);
    }

    public void setTrainingData(Vector<SegmentedObject> trainingData) {
        this.trainingData = trainingData;
    }

    public void setDs(DataSet ds) {
        this.ds = ds;
    }

    public void run() {
        if (trainingData == null) trainingData = JasmineUtils.getLabelledObjects(project);
        set = JasmineUtils.getTerminalSet(JasmineGP.getStandardTerminals(trainingData), terminalMetaData);
        if (ds == null) ds = JasmineUtils.generateObjectDataSet(set, trainingData);
        ics = new ICS();
        ICS.NORMALISE = false;
        ICS.RUNS = 1;
        ICS.TIME_PER_RUN = 60;
        ics.addListener(listener);
        //POEY comment: jasmine.classify.data.MemoryDataSet.java
        d = ds.getAllTrainingData();
        ics.generateClassifier(d, d);
    }

    public void ensureTerminalMetaDataKnowsTerminals(Classifier c) {
        DataValueTerminal.imagingTerminals = set;
        DataStack ds = new DataStack();
        for (int i = 0; i < trainingData.size(); i++) {
            JasmineUtils.setupDataStack(ds, trainingData.elementAt(i));
            c.classify(ds);            
        }
    }

}
