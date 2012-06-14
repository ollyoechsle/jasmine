package jasmine.gp.training;


import jasmine.imaging.commons.PixelLoader;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * An image which is associated with a particular classID.
 *
 * @author Olly Oechsle, University of Essex, Date: 06-Nov-2007
 * @version 1.0
 */
public class LabelledImage extends PixelLoader {

    protected int classID;

    public LabelledImage(String image, int classID) throws Exception {
        super(image);
        this.classID = classID;
    }

    public LabelledImage(File image, int classID) throws Exception {
        super(image);
        this.classID = classID;
    }

    public LabelledImage(BufferedImage image, int classID) {
        super(image);
        this.classID = classID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

}
