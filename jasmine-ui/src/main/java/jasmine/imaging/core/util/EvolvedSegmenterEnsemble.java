package jasmine.imaging.core.util;


import jasmine.classify.classifier.ClassifierFusion;
import jasmine.classify.classifier.GPClassifier;
import jasmine.gp.nodes.DataValueTerminal;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.gp.util.Deployable;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.JasmineFeatureSelectionDialog;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblem;

import java.io.*;
import java.util.Vector;

/**
 * Allows an evolved program to be packaged and saved as a segmenter easily
 * without the need to compile it.
 */
public class EvolvedSegmenterEnsemble extends EvolvedSegmenter implements Serializable, Deployable {

    public ClassifierFusion fusion;

    public EvolvedSegmenterEnsemble(ClassifierFusion fusion) {
        this.fusion = fusion;
    }

    public int segment(PixelLoader pixelLoader, int x, int y) {

        DataStack s = new DataStack();
        s.setImage(pixelLoader);
        s.setX(x);
        s.setY(y);
        GPClassifier.dataStack = s;
        int value = fusion.classify();
        if (value < 0) value = 0;
        return value;
    }

    public String getJavaTemplate() {
        // create the java code
        return "import ac.essex.ooechs.imaging.commons.PixelLoader;\n" +
                "import ac.essex.ooechs.imaging.commons.segmentation.Segmenter;\n" +
                "import jasmine.gp.multiclass.PCM;\n" +
                "import jasmine.gp.multiclass.BetterDRS;\n" +
                "import ac.essex.ooechs.imaging.commons.texture.*;\n" +
                "\n" +
                "import java.util.Vector;\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "public class CLASSNAME extends Segmenter {\n" +
                "\n" +
                "CODE" +
                "\n" +
                "}";
    }

    public String getCode() {
        
        // get the feature set

        Vector<TerminalMetaData> terminalMetaData = (Vector<TerminalMetaData>) Jasmine.currentInstance.project.getProperty(JasmineFeatureSelectionDialog.MATERIAL_FEATURE_SET_HANDLE);
        Vector<Terminal> set = JasmineUtils.getTerminalSet(JasmineSegmentationProblem.getStandardTerminals(), terminalMetaData);
        // give a reference to DataValueTerminal, so it can work on images as well as generic datasets
        DataValueTerminal.imagingTerminals = set;

        return fusion.toJava("public int", "segment", "PixelLoader image, int x, int y");
    }

}