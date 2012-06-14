package jasmine.imaging.shapes;


import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.util.TrainingObject;

import java.util.Vector;

/**
 * An object is one that may consist of many segmented shapes
 */
public class SegmentedObject extends TrainingObject {

    public Vector<SegmentedShape> subobjects;
    public SegmentedShape outline;
    private transient ExtraShapeData outlineESD = null;
    private transient int labelledSubObjects = -1;
    public transient PixelLoader image;
    public String imageFilename;
    
    //POEY
    public String imageName;

    public SegmentedObject(PixelLoader image, int classID, int type, SegmentedShape outline) {
        super(classID, type);
        subobjects = new Vector<SegmentedShape>(10);
        this.outline = outline;
        setImage(image);
    }

    public PixelLoader getImage() {
        try {
            if (image == null) {
                image = new PixelLoader(imageFilename);
            }
        } catch (Exception e) {
            return null;
        }
        return image;
    }

    public void setImage(PixelLoader image) {
        this.image = image;
        if (image != null && image.getFile() != null)
        this.imageFilename = image.getFile().getAbsolutePath();
        
        //POEY
        this.imageName = image.getFilename();
    }

    public int countLabelledSubObjects() {
        //if (labelledSubObjects == -1) {
            labelledSubObjects = 0;
            for (int i = 0; i < subobjects.size(); i++) {
                SegmentedShape segmentedShape = subobjects.elementAt(i);
                if (segmentedShape.classID != -1) labelledSubObjects++;
            }
        //}
        return labelledSubObjects;
    }

    public ExtraShapeData getOutlineData() {
        if (outlineESD == null) {      	
            outlineESD = new ExtraShapeData(outline, getImage());
        }        
        return outlineESD;
    }

    public void addSubObject(SegmentedShape s) {
        subobjects.add(s);
        labelledSubObjects = -1;
    }

    public void remove(SegmentedShape s) {
        subobjects.remove(s);
        labelledSubObjects = -1;
    }

    public Vector<SegmentedShape> getSubObjects() {
        return subobjects;
    }

    public String toString() {
        return super.toString();
    }

}
