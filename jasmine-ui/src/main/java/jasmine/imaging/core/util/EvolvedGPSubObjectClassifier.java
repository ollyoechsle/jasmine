package jasmine.imaging.core.util;


import jasmine.gp.Individual;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.Segmenter;
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
public class EvolvedGPSubObjectClassifier extends SubObjectClassifier implements Serializable {

    protected Individual ind;

    public EvolvedGPSubObjectClassifier(Individual ind) {
        this.ind = ind;
    }

    public int classify(ExtraShapeData shape) {
        DataStack s = new DataStack();
        JasmineUtils.setupDataStack(s, shape);
        int value;
        if (ind.getPCM() != null) {
            value =  ind.getPCM().getClassFromOutput(ind.execute(s));
        } else {
            value = (int) ind.execute(s);
        }
        if (value < 0) value = 0;
        return value;
    }

/**
     * Saves the individual to disk in serialised form. This is primarily
     * to allow the JavaWriter output to be compared with the actual individual
     * in a debugging environment.
     */
    public void save(File f){
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a serialised individual from disk. Mainly used for debugging the
     * Java Writer.
     */
    public static Segmenter load(File f) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream in = new ObjectInputStream(fis);
        Segmenter individual = (Segmenter) in.readObject();
        in.close();
        return individual;
    }


}
