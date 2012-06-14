package jasmine.imaging.core.util;


import jasmine.gp.Individual;
import jasmine.gp.problems.DataStack;
import jasmine.gp.util.Deployable;
import jasmine.imaging.commons.PixelLoader;

import java.io.*;

/**
 * Allows an evolved program to be packaged and saved as a segmenter easily
 * without the need to compile it.
 */
public class EvolvedSegmenterSingle extends EvolvedSegmenter implements Serializable, Deployable {

    public Individual ind;

    public EvolvedSegmenterSingle(Individual ind) {
        this.ind = ind;
    }

    public int segment(PixelLoader pixelLoader, int x, int y) {
	
        DataStack s = new DataStack();
        s.setImage(pixelLoader);
        s.setX(x);
        s.setY(y);
        int value;
        if (ind.getPCM() != null) {
        	//POEY comment: for getClassFromOutput(), jasmine.gp.multiclass
        	//BasicDRS.java, BetterDRS.java, EntropyThreshold.java, VarianceThreshold.java
            value = ind.getPCM().getClassFromOutput(ind.execute(s));
        } else {
            value = (int) ind.execute(s);
        }
        if (value < 0) value = 0;
        return value;
    }


    public String getJavaTemplate() {
        // create the java code
        return "import ac.essex.ooechs.imaging.commons.segmentation.Segmenter;\n" +
                "import ac.essex.ooechs.imaging.commons.PixelLoader;\n" +
                "import jasmine.gp.multiclass.*;\n" +
                "import ac.essex.ooechs.imaging.commons.texture.*;\n" +
                "\n" +
                "public class CLASSNAME extends Segmenter {\n" +
                "\n" +
                "CODE" +
                "\n" +
                "}";
    }

    public String getCode() {
        String methodSignature = "\npublic int segment(PixelLoader image, int x, int y)";
        return ind.toJava(methodSignature);
    }


}