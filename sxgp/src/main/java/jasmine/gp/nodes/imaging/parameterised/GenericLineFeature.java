package jasmine.gp.nodes.imaging.parameterised;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.Line;
import jasmine.imaging.commons.Perimeter;
import jasmine.imaging.commons.PixelLoader;

import java.util.Vector;

/**
 * Returns the mean intensity of a region. See feature node for more info.
 *
 * @author Olly Oechsle, University of Essex, Date: 06-Nov-2007
 * @version 1.0
 */
public class GenericLineFeature extends ParameterisedTerminal {

    public static final int MAX_RADIUS = 10;
    public static final int MIN_RADIUS = 3;

    protected transient Line l;
    protected int mode, orientation, radius;

    public GenericLineFeature() {
        this(0, PixelLoader.feature1Size, Line.HORIZONTAL);
    }

    public GenericLineFeature(int mode, int radius, int orientation) {
        this.mode = mode;
        this.radius = radius;
        this.orientation = orientation;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255, NodeConstraints.FEATURE};
    }

    public double execute(DataStack ds) {

        if (l == null) {
            l = new Line(radius, orientation);
        }

        switch (mode) {
            case Perimeter.MEAN:
                ds.value = l.getMean(ds.getImage(), ds.getX(), ds.getY());     
                break;
            case Perimeter.VARIANCE:
                ds.value = l.getStdDeviation(ds.getImage(), ds.getX(), ds.getY());
                break;
            case Perimeter.EDGE_COUNT:
                ds.value = l.getEdgeCount(ds.getImage(), ds.getX(), ds.getY());
                break;
        }

        return debugger == null ? ds.value : debugger.record(ds.value);

    }

    public String toJava() {
        switch (mode) {
            case Perimeter.MEAN:
                return "new Line(" + radius + ", " + orientation + ").getMean(image, x, y)";
            case Perimeter.VARIANCE:
                return "new Line(" + radius + ", " + orientation + ").getStdDeviation(image, x, y)";
            case Perimeter.EDGE_COUNT:
                return "new Line(" + radius + ", " + orientation + ").getEdgeCount(image, x, y)";
        }
        return "";
    }

    public String getShortName() {
        return "Line";
    }

    public ParameterisedTerminal getRandom() {    	
        int radius = getRandom(MIN_RADIUS, MAX_RADIUS);
        int mode = getRandom(0, 2);
        int orientation = getRandom(0,1);
        return new GenericLineFeature(mode, radius, orientation);
    }

    public Vector<ParameterisedTerminal> getDefaults() {    	
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();

        defaults.add(new GenericLineFeature(0, PixelLoader.feature1Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeature(0, PixelLoader.feature1Size, Line.VERTICAL));
        defaults.add(new GenericLineFeature(0, PixelLoader.feature2Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeature(0, PixelLoader.feature2Size, Line.VERTICAL));

        defaults.add(new GenericLineFeature(1, PixelLoader.feature1Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeature(1, PixelLoader.feature1Size, Line.VERTICAL));
        defaults.add(new GenericLineFeature(1, PixelLoader.feature2Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeature(1, PixelLoader.feature2Size, Line.VERTICAL));

        defaults.add(new GenericLineFeature(2, PixelLoader.feature1Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeature(2, PixelLoader.feature1Size, Line.VERTICAL));
        defaults.add(new GenericLineFeature(2, PixelLoader.feature2Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeature(2, PixelLoader.feature2Size, Line.VERTICAL));

        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{mode, radius, orientation};
    }

    public String toString() {
        return "Line Features";
    }
}