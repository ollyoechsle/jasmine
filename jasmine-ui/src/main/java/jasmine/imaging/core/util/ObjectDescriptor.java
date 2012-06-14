package jasmine.imaging.core.util;


import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;

import java.util.Vector;

/**
 * Utility methods for describing a segmented object
 */
public class ObjectDescriptor {

    public static Vector<Integer> getAllSubObjectClassIDs(Vector<SegmentedObject> objects) {
        Vector<Integer> classes = new Vector<Integer>(10);
        for (int k = 0; k < objects.size(); k++) {
            SegmentedObject segmentedObject = objects.elementAt(k);
            for (int l = 0; l < segmentedObject.subobjects.size(); l++) {
                SegmentedShape segmentedShape = segmentedObject.subobjects.elementAt(l);
                if (segmentedShape.originalValue > 0 && !classes.contains(segmentedShape.originalValue)) {
                    classes.add(segmentedShape.originalValue);
                }
            }
        }
        return classes;
    }

    public static double classProportion(SegmentedObject object, int classID) {
        double total = 0;
        if (object == null || object.getSubObjects() == null) return 0;
        for (int i = 0; i < object.getSubObjects().size(); i++) {
            SegmentedShape segmentedShape = object.getSubObjects().elementAt(i);
            if (segmentedShape.originalValue == classID) {
                total += segmentedShape.getPerimeter();
            }
        }
        return total / object.outline.getMass();
    }

    public static double subObjectPerimeter(SegmentedObject object) {
        double total = 0;
        int N = 0;
        if (object == null || object.getSubObjects() == null) return 0;
        for (int i = 0; i < object.getSubObjects().size(); i++) {
            SegmentedShape segmentedShape = object.getSubObjects().elementAt(i);
            if (segmentedShape.originalValue > 0) {
                total += segmentedShape.getPerimeter();
            }
        }
        return total / N;
    }

    public static double subObjectSize(SegmentedObject object) {
        double total = 0;
        int N = 0;
        if (object == null || object.getSubObjects() == null) return 0;
        for (int i = 0; i < object.getSubObjects().size(); i++) {
            SegmentedShape segmentedShape = object.getSubObjects().elementAt(i);
            if (segmentedShape.originalValue > 0) {
                total += segmentedShape.getMass();
            }
        }
        return total / N;
    }

    public static double subObjectProportion(SegmentedObject object) {
        double total = 0;
        if (object == null || object.getSubObjects() == null) return 0;
        for (int i = 0; i < object.getSubObjects().size(); i++) {
            SegmentedShape segmentedShape = object.getSubObjects().elementAt(i);
            if (segmentedShape.originalValue > 0) {
                total += segmentedShape.getMass();
            }
        }
        return total / object.outline.getMass();
    }

    public static int countClasses(SegmentedObject object) {
        Vector<Integer> classes = new Vector<Integer>();
        if (object == null || object.getSubObjects() == null) return 0;
        for (int i = 0; i < object.getSubObjects().size(); i++) {
            SegmentedShape segmentedShape = object.getSubObjects().elementAt(i);
            if (segmentedShape.originalValue > 0 && !classes.contains(segmentedShape.originalValue)) {
                classes.add(segmentedShape.originalValue);
            }
        }
        return classes.size();
    }


}
