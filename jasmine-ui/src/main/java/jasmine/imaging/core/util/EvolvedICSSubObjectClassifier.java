package jasmine.imaging.core.util;

import jasmine.classify.classifier.Classifier;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.SubObjectClassifier;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: olly
 * Date: May 12, 2008
 * Time: 7:48:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvolvedICSSubObjectClassifier extends SubObjectClassifier implements Serializable {

    protected Classifier ind;

    public EvolvedICSSubObjectClassifier(Classifier ind) {
        this.ind = ind;
    }

    public int classify(ExtraShapeData shape) {
        DataStack s = new DataStack();
        JasmineUtils.setupDataStack(s, shape);
        int value;
        value = ind.classify(s);
        if (value < 0) value = 0;
        return value;
    }

    /**
     * Saves the individual to disk in serialised form. This is primarily
     * to allow the JavaWriter output to be compared with the actual individual
     * in a debugging environment.
     */
    public void save(File f) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}