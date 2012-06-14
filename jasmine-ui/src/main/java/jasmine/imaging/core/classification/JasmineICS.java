package jasmine.imaging.core.classification;


import jasmine.classify.ICSListener;
import jasmine.classify.classifier.Classifier;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.util.TerminalMetaData;

import java.util.Vector;

/**
 * Unified interface for initiating the super classifier (ICS)
 */
public abstract class JasmineICS {

    protected Vector<TerminalMetaData> terminalMetaData = null;
    protected ICSListener listener;
    protected JasmineProject project;

    public JasmineICS(JasmineProject project) {
        this.project = project;
    }

    public void addListener(ICSListener listener) {
        this.listener = listener;
    }

    public void setTerminalMetaData(Vector<TerminalMetaData> terminalMetaData) {
        this.terminalMetaData = terminalMetaData;
    }

    public abstract void run();
    
    public abstract void ensureTerminalMetaDataKnowsTerminals(Classifier c);

}
