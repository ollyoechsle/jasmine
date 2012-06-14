package jasmine.gp.nodes.imaging.parameterised;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.Perimeter;
import jasmine.imaging.commons.PixelLoader;

import java.util.Vector;

/**
 * Returns the mean intensity of a region. See feature node for more info.
 *
 * @author Olly Oechsle, University of Essex, Date: 06-Nov-2007
 * @version 1.0
 */
public class GenericPerimeterFeature extends ParameterisedTerminal {

    public static final int MAX_RADIUS = 10;
    public static final int MIN_RADIUS = 3;
    
    protected transient Perimeter p;
    protected int mode, radius;

    public GenericPerimeterFeature() {
        this(0, PixelLoader.feature1Size);
    }

    public GenericPerimeterFeature(int mode, int radius) {
        this.mode = mode;
        this.radius = radius;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255, NodeConstraints.FEATURE};
    }

    public double execute(DataStack ds) {

        if (p == null) {
            p = new Perimeter(radius);
        }

        switch (mode) {
            case Perimeter.MEAN:
                ds.value =  p.getMean(ds.getImage(), ds.getX(), ds.getY());  
                break;
            case Perimeter.VARIANCE:
                ds.value = p.getStdDeviation(ds.getImage(), ds.getX(), ds.getY());  
                break;
            case Perimeter.EDGE_COUNT:
                ds.value = p.getEdgeCount(ds.getImage(), ds.getX(), ds.getY());
                break;
        }

        return debugger == null? ds.value : debugger.record(ds.value);

    }

    public String toJava() {
        switch (mode) {
            case Perimeter.MEAN:
                return "new Perimeter(" + radius + ").getMean(image, x, y)";
            case Perimeter.VARIANCE:
                return "new Perimeter(" + radius + ").getStdDeviation(image, x, y)";
            case Perimeter.EDGE_COUNT:
                return "new Perimeter(" + radius + ").getEdgeCount(image, x, y)";
        }
        return "";
    }

    public String getShortName() {
        return "Perim";
    }

    public ParameterisedTerminal getRandom() {
        int radius = getRandom(MIN_RADIUS, MAX_RADIUS);
        int mode = getRandom(0,2);
        return new GenericPerimeterFeature(mode, radius);
    }

    public Vector<ParameterisedTerminal> getDefaults() {
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
        defaults.add(new GenericPerimeterFeature(0, PixelLoader.feature1Size));
        defaults.add(new GenericPerimeterFeature(0, PixelLoader.feature2Size));
        defaults.add(new GenericPerimeterFeature(1, PixelLoader.feature1Size));
        defaults.add(new GenericPerimeterFeature(1, PixelLoader.feature2Size));
        defaults.add(new GenericPerimeterFeature(2, PixelLoader.feature1Size));
        defaults.add(new GenericPerimeterFeature(2, PixelLoader.feature2Size));
        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{mode, radius};
    }

    public String toString() {
        return "Perimeter Features";
    }

}