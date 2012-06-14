package jasmine.imaging.core.visionsystem;

import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.Segmenter;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.util.JasmineDeployer;
import jasmine.imaging.core.util.TrainingObject;
import jasmine.imaging.shapes.Grouper;
import jasmine.imaging.shapes.ObjectClassifier;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;
import jasmine.imaging.shapes.ShapePixel;
import jasmine.imaging.shapes.SubObjectClassifier;

import java.io.Serializable;
import java.util.Vector;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 31-Mar-2009
 * Time: 09:23:50
 * To change this template use File | Settings | File Templates.
 */
public class VisionSystem implements Serializable {

    public static int minObjectSize = 8*8;
    public static int minSubObjectSize = 8*8;

    // constant values
    public static final String BACKGROUND_SUBTRACTER_HANDLE = "bg-subtract";
    public static final String SEGMENTER_HANDLE = "segmenter";
    public static final String OBJECT_CLASSIFIER_HANDLE = "classifier";
    public static final String SUB_OBJECT_CLASSIFIER_HANDLE = "sub-classifier";
    public static final String SHAPE_PROCESSOR_HANDLE = "shape-processor";

    public static String OBJECT_SIZE_HANDLE = "OBJECT_MIN_SIZE";
    public static String SUB_OBJECT_SIZE_HANDLE = "SUB_OBJECT_MIN_SIZE";

    // processing
    public static final int ORIGINAL_SHAPE = 0;
    public static final int BOUNDING_BOX = 1;
    public static final String[] processingNames = new String[]{"Original Shape", "Bounding Box"};
    int shapeProcessing = 0;

    // the classes
    public Vector<JasmineClass> materialClasses;
    public Vector<JasmineClass> objectClasses;
    public Vector<JasmineClass> subObjectClasses;
    public String name;

    // the evolved components of the vision system
    public Segmenter backgroundSubtracter, segmenter;
    public ObjectClassifier objectClassifier;
    public SubObjectClassifier subobjectClassifier;

    protected transient VisionSystemListener listener;

    public VisionSystem(String name) {
        this.name = name;
    }

    public static VisionSystem load(JasmineProject project) throws Exception {

        if (project.getProperty(OBJECT_SIZE_HANDLE) != null) {
            minObjectSize = (Integer) project.getProperty(OBJECT_SIZE_HANDLE);
        }
        if (project.getProperty(SUB_OBJECT_SIZE_HANDLE) != null) {
            minSubObjectSize = (Integer) project.getProperty(SUB_OBJECT_SIZE_HANDLE);
        }

        VisionSystem vs = new VisionSystem(project.getName());
        vs.materialClasses = project.getMaterialClasses();
        vs.objectClasses = project.getObjectClasses();
        vs.subObjectClasses = project.getSubObjectClasses();
        vs.backgroundSubtracter = JasmineDeployer.getSegmenter(project, BACKGROUND_SUBTRACTER_HANDLE);
        vs.segmenter = JasmineDeployer.getSegmenter(project, SEGMENTER_HANDLE);
        try {
        vs.objectClassifier = JasmineDeployer.getObjectClassifier(project);
        } catch (Exception e) {}
        try {
        vs.subobjectClassifier = JasmineDeployer.getShapeClassifier(project);
        } catch (Exception e) {}
        if (project.getProperty(SHAPE_PROCESSOR_HANDLE) != null)
        vs.shapeProcessing = (Integer) project.getProperty(SHAPE_PROCESSOR_HANDLE);
        return vs;
    }

    public void addVisionSystemListener(VisionSystemListener listener) {
        this.listener = listener;
    }

    public int classify(SegmentedShape subObject, PixelLoader image) {

        if (subobjectClassifier != null) {
            return subobjectClassifier.classify(subObject, image);
        }

        return -1;

    }

    public int classify(SegmentedObject object) {

        if (objectClassifier != null) {
            return objectClassifier.classify(object);
        }

        return -1;

    }

    public boolean isBackground(SegmentedObject object) {
        if (object.getClassID() <= 0) return true;
        for (int i = 0; i < objectClasses.size(); i++) {
            JasmineClass jasmineClass = objectClasses.elementAt(i);
            if (jasmineClass.classID == object.getClassID()) return jasmineClass.background;
        }
        return false;
    }

    public boolean isBackground(SegmentedShape object) {
        if (object.classID <= 0) return false;
        for (int i = 0; i < subObjectClasses.size(); i++) {
            JasmineClass jasmineClass = subObjectClasses.elementAt(i);
            if (jasmineClass.classID == object.classID) return jasmineClass.background;
        }
        return false;
    }


    public String getName(SegmentedObject object) {
        if (object.getClassID() <= 0) return "0";
        for (int i = 0; i < objectClasses.size(); i++) {
            JasmineClass jasmineClass = objectClasses.elementAt(i);
            if (jasmineClass.classID == object.getClassID()) return jasmineClass.name;
        }
        return "Unknown";
    }

    public String getName(SegmentedShape s) {
        if (s.classID <= 0) return null;
        for (int i = 0; i < subObjectClasses.size(); i++) {
            JasmineClass jasmineClass = subObjectClasses.elementAt(i);
            if (jasmineClass.classID == s.classID) return jasmineClass.name;
        }
        return "Unknown";
    }

    public Vector<SegmentedObject> processImage(PixelLoader image) throws Exception {
    	//Don't comment
        //System.out.println("Min object size: " + minObjectSize);

        if (listener != null) listener.onStart();

        Vector<SegmentedObject> objects = getObjects(image);

        if (listener != null)  listener.onFinishedSegmentation(objects);

        // classify subobjects first
        if (subobjectClassifier != null) {
            for (int i = 0; i < objects.size(); i++) {
                SegmentedObject object = objects.elementAt(i);
                for (int j = 0; j < object.subobjects.size(); j++) {
                    SegmentedShape segmentedShape = object.subobjects.elementAt(j);
                    try {
                        segmentedShape.classID = classify(segmentedShape, image);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // classify objects last
        if (objectClassifier != null) {
            for (int i = 0; i < objects.size(); i++) {
                SegmentedObject segmentedObject = objects.elementAt(i);
                try {
                    segmentedObject.setClassID(classify(segmentedObject));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        if (listener != null) listener.onFinished(objects);

        return objects;

    }

    public int[][] segment(PixelLoader image, Segmenter s) {

        PixelLoader.CACHE_RGB_HSL = false;
        PixelLoader.CACHING_OTHERS =false;

        if (listener != null) listener.onStart();
        int lastProgress = -1;
        int[][] array = new int[image.getWidth()][image.getHeight()];
        // first thing to do is to push the whole image into an editable array
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
            	//POEY comment: jasmine.imaging.core.util.EvolvedSegmenterSingle.java
            	//array[x][y] contains estimated classID
                array[x][y] = s.segment(image, x, y);             
            }
            if (listener != null) { 
                int progress = (int) ((y / (float) image.getHeight()) * 100);
                //POEY comment: set a progress dialog
                if (progress != lastProgress) {	
                    listener.onSegmentationProgress(progress);
                    lastProgress = progress;
                }
            }
        }

        int[][] smoothed = new int[image.getWidth()][image.getHeight()];
        int[] numbers = new int[9];
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                numbers[0] = array[x][y];
                numbers[1] = array[x-1][y];
                numbers[2] = array[x+1][y];
                numbers[3] = array[x-1][y-1];
                numbers[4] = array[x-1][y+1];
                numbers[5] = array[x+1][y-1];
                numbers[6] = array[x+1][y+1];
                numbers[7] = array[x][y-1];
                numbers[8] = array[x][y+1];
                //POEY comment: smoothed[x][y] contains classID which is the most number of classID in numbers[]
                smoothed[x][y] = getMode(numbers);
            }
        }
        return smoothed;
    }

    public int getMode(int[] numbers) {
        Arrays.sort(numbers);
        int v = -1;
        int last = -2;
        int max = 0;
        int con = 0;
        for (int i = 0; i < numbers.length; i++) {
            int number = numbers[i];
            if (number == last) { 
                con++;
                if (con > max) {
                    v = number;
                    max = con;
                }
            } else {
                con = 0;
            }
            last = number;
        }
        return v;
    }

    public Vector<SegmentedObject> getObjects(PixelLoader image) throws Exception {

        if (backgroundSubtracter == null) {
            throw new RuntimeException("Cannot segment: background subtracter is not set up");
        }

        // first put the jasmine classes into an array indexed by their classID. This is
        // faster than repeated calls to the getMaterialClass() method on the project.
        JasmineClass[] pixelClasses = new JasmineClass[100];
        //POEY comment: for general segmentation, materialClasses.size() = 0
        for (int i = 0; i < this.materialClasses.size(); i++) {
            JasmineClass jasmineClass = this.materialClasses.elementAt(i);
            pixelClasses[jasmineClass.classID] = jasmineClass;
        }

        // segment the image using the background segmenter
        //POEY comment: pixels[][] contains classID which is calculated by the number of neighbour pixels 3*3
        int[][] pixels = segment(image, backgroundSubtracter);

        // Now get the groups which each correspond to one object
        //POEY comment: ???
        Vector<SegmentedShape> objectOutlines = new Grouper().getShapes(pixels);

        int[][] objectPixels = null;
        if (segmenter != null) objectPixels = new int[image.getWidth()][image.getHeight()];

        Vector<SegmentedObject> objects = new Vector<SegmentedObject>(objectOutlines.size());


        // do shape processing here

        Vector<SegmentedShape> toRemove = new Vector<SegmentedShape>();
        //POEY comment: objectOutlines.size() = the number of objects in a picture
        for (int i = 0; i < objectOutlines.size(); i++) {
            SegmentedShape outline = objectOutlines.elementAt(i);

            // size filter for objects
            //POEY comment: eliminate a pixel group which is smaller that 8*8 pixels
            if (outline.getMass() < minObjectSize) {
                toRemove.add(outline);
                continue;
            }

            // dont bother processing shapes marked for removal
            if (toRemove.contains(outline)) continue;

            switch (shapeProcessing) {
                case BOUNDING_BOX:
                    SegmentedShape s = new SegmentedShape(outline.originalValue);
                    for (int y = outline.minY; y <= outline.maxY; y++)
                    for (int x = outline.minX; x <= outline.maxX; x++) {
                        boolean isEdge = y == outline.minY || y == outline.maxY || x == outline.minX || x == outline.maxX;
                        s.add(x, y, isEdge);
                    }
                    outline = s;
                    objectOutlines.setElementAt(s, i);

                    // since this process will fill in any gaps, the objects may be overlapping, which we don't want.
                    // delete any objects that overlap with this one
                    //POEY comment: remove a smaller object overlapping another bigger one
                    for (int j = 0; j < objectOutlines.size(); j++) {
                        SegmentedShape shape =  objectOutlines.elementAt(j);
                        if (shape.minX > outline.minX && shape.maxX < outline.maxX && shape.minY > outline.minY && shape.maxY < outline.maxY) {
                            toRemove.add(shape);
                        }
                    }

                    break;
            }

        }

        objectOutlines.removeAll(toRemove);

        // Go through every object outline
        for (int i = 0; i < objectOutlines.size(); i++) {
            SegmentedShape outline =  objectOutlines.elementAt(i);

            SegmentedObject object;

            if (segmenter == null) {
                object = new SegmentedObject(image, -1, TrainingObject.TRAINING, outline);
            } else {
                object = new SegmentedObject(image, -1, TrainingObject.TRAINING, outline);

                // Segment only within the outline of the object
                Vector<ShapePixel> outlinePixels = outline.pixels;

                for (int j = 0; j < outlinePixels.size(); j++) {
                    ShapePixel p = outlinePixels.elementAt(j);
                    //System.out.println(p.x + ", " + p.y);
                    int classID = segmenter.segment(image, p.x, p.y);
                    if (classID > 0) {
                        // make sure background shape doesn't appear
                        objectPixels[p.x][p.y] = classID;
                    }
                }

                // Now, find the SUB-OBJECTS within the outline
                Vector<SegmentedShape> subobjects = new Grouper().getShapes(objectPixels);

                for (int j = 0; j < subobjects.size(); j++) {
                    SegmentedShape subObject = subobjects.elementAt(j);

                    // size filter for sub objects
                    if (subObject.getMass() < minSubObjectSize) continue;

                    JasmineClass c = null;
                    if (subObject.originalValue != -1 && subObject.originalValue < pixelClasses.length) {
                        c = pixelClasses[subObject.originalValue];
                    }
                    if (c != null && !c.background) {
                        // add sub object to main object
                        object.addSubObject(subObject);
                    }
                }

                // Undo the changes to the objectPixels array, so that we can re-use it
                for (int j = 0; j < outlinePixels.size(); j++) {
                    ShapePixel p = outlinePixels.elementAt(j);
                    objectPixels[p.x][p.y] = 0;
                }
            }

            objects.add(object);

        }

        if (listener != null) {
            listener.onFinishedSegmentation(objects);
        }

        return objects;

    }

    public void classifySubObjects(JasmineProject project, SegmentedObject object)  throws Exception {

        SubObjectClassifier c = JasmineDeployer.getShapeClassifier(project);

        for (int i = 0; i < object.subobjects.size(); i++) {
            SegmentedShape shape = object.subobjects.elementAt(i);
            try {
                shape.classID = c.classify(shape, object.getImage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
