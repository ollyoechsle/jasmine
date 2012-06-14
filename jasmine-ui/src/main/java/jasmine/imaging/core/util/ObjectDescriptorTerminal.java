package jasmine.imaging.core.util;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.shapes.SegmentedObject;

/**
 * Describes a particular object
 */
public class ObjectDescriptorTerminal extends Terminal {

    protected PixelLoader image;
    protected SegmentedObject object;
    protected int code, param;

    public ObjectDescriptorTerminal(int code) {
        this(code, -1);
    }

    public ObjectDescriptorTerminal(int code, int param) {
        this.code = code;
        this.param = param;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this terminal is copied.
        return new Object[]{code, param};
    }

    public double execute(DataStack data) {

        SegmentedObject object = ((SegmentedObject) data.getData2());

        switch (code) {
            case 1:
                return object.getSubObjects().size();
            case 2:
                return object.outline.getMass();
            case 3:
                return object.outline.getPerimeter();
            case 4:
                return ObjectDescriptor.countClasses(object);
            case 5:
                return ObjectDescriptor.subObjectProportion(object);
            case 6:
                return ObjectDescriptor.subObjectSize(object);
            case 7:
                return ObjectDescriptor.subObjectPerimeter(object);
            case 8:
                return ObjectDescriptor.classProportion(object, param);
        }
        return 0;

    }

    public String toJava() {
        switch (code) {
            case 1:
                return "object.getSubObjects().size()";
            case 2:
                return "object.outline.getMass()";
            case 3:
                return "object.outline.getPerimeter()";
            case 4:
                return "ObjectDescriptor.countClasses(object)";
            case 5:
                return "ObjectDescriptor.subObjectProportion(object)";
            case 6:
                return "ObjectDescriptor.subObjectSize(object)";
            case 7:
                return "ObjectDescriptor.subObjectPerimeter(object)";
            case 8:
                return "ObjectDescriptor.classProportion(object, " + param + ")";
        }
        return "";
    }



}
