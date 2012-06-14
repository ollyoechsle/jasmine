package jasmine.imaging.core.util;


import jasmine.gp.util.Deployable;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.Segmenter;

import java.io.*;

/**
 * Allows an evolved program to be packaged and saved as a segmenter easily
 * without the need to compile it.
 */
public abstract class EvolvedSegmenter extends Segmenter implements Serializable, Deployable {

    public abstract int segment(PixelLoader pixelLoader, int x, int y);

/**
     * Saves the individual to disk in serialised form. This is primarily
     * to allow the JavaWriter output to be compared with the actual individual
     * in a debugging environment.
     */
    public void save(File f){
        try {
            System.out.println("Saving File");
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
