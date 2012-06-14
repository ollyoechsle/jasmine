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
import jasmine.imaging.shapes.ExtraShapeData;
import java.util.Vector;

/**
 * Runs super classifier on the problem. Essentially just a wrapper.
 */
public class JasmineICSSubObject extends JasmineICS {

    protected Vector<Terminal> set;
    protected ICS ics;
    protected Vector<Data> d;
    Vector<ExtraShapeData> trainingData;

    public JasmineICSSubObject(JasmineProject project) {
        super(project);
    }

    public void run() {
        trainingData = JasmineUtils.getLabelledSubObjects(project);
        set = JasmineUtils.getTerminalSet(JasmineGP.getStandardTerminals(null), terminalMetaData);
        DataSet ds = JasmineUtils.generateObjectDataSet(set, trainingData);
        ics = new ICS();
        ICS.NORMALISE = false;
        ics.addListener(listener);
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
