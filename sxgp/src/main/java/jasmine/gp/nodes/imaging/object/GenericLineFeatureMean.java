package jasmine.gp.nodes.imaging.object;


import jasmine.gp.nodes.imaging.parameterised.ParameterisedTerminal;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.Line;
import jasmine.imaging.commons.Perimeter;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.shapes.ExtraShapeData;

import java.util.Vector;

//POEY
public class GenericLineFeatureMean extends ParameterisedTerminal {

    public static final int MAX_RADIUS = 10;
    public static final int MIN_RADIUS = 3;

    protected transient Line l;
    protected int mode, orientation, radius;

    public GenericLineFeatureMean() {
        this(0, PixelLoader.feature1Size, Line.HORIZONTAL);
    }

    public GenericLineFeatureMean(int mode, int radius, int orientation) {
        this.mode = mode;
        this.radius = radius;
        this.orientation = orientation;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack ds) {

        if (l == null) {
            l = new Line(radius, orientation);
        }

        ds.usesImaging = true;
        switch (mode) {
            case Perimeter.MEAN:
                ds.value = l.getMean(ds.getImage(), (ExtraShapeData) ds.getData()); 
                break;
            case Perimeter.VARIANCE:
                ds.value = l.getStdDeviationMean(ds.getImage(), (ExtraShapeData) ds.getData());
                break;
            case Perimeter.EDGE_COUNT:
                ds.value = l.getEdgeCountMean(ds.getImage(), (ExtraShapeData) ds.getData());
                break;
        }

        return debugger == null ? ds.value : debugger.record(ds.value);

    }

    public String toJava() {
        switch (mode) {
            case Perimeter.MEAN:
                return "new Line(" + radius + ", " + orientation + ").getMean(object)";
            case Perimeter.VARIANCE:
                return "new Line(" + radius + ", " + orientation + ").getStdDeviationMean(object)";
            case Perimeter.EDGE_COUNT:
                return "new Line(" + radius + ", " + orientation + ").getEdgeCountMean(object)";
        }
        return "";
    }

    public String getShortName() {
        return "Line Mean";
    }

    public ParameterisedTerminal getRandom() {    	
        int radius = getRandom(MIN_RADIUS, MAX_RADIUS);
        int mode = getRandom(0, 2);
        int orientation = getRandom(0,1);
        return new GenericLineFeatureMean(mode, radius, orientation);
    }

    public Vector<ParameterisedTerminal> getDefaults() {    	
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();

        defaults.add(new GenericLineFeatureMean(0, PixelLoader.feature1Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeatureMean(0, PixelLoader.feature1Size, Line.VERTICAL));
        defaults.add(new GenericLineFeatureMean(0, PixelLoader.feature2Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeatureMean(0, PixelLoader.feature2Size, Line.VERTICAL));

        defaults.add(new GenericLineFeatureMean(1, PixelLoader.feature1Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeatureMean(1, PixelLoader.feature1Size, Line.VERTICAL));
        defaults.add(new GenericLineFeatureMean(1, PixelLoader.feature2Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeatureMean(1, PixelLoader.feature2Size, Line.VERTICAL));

        defaults.add(new GenericLineFeatureMean(2, PixelLoader.feature1Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeatureMean(2, PixelLoader.feature1Size, Line.VERTICAL));
        defaults.add(new GenericLineFeatureMean(2, PixelLoader.feature2Size, Line.HORIZONTAL));
        defaults.add(new GenericLineFeatureMean(2, PixelLoader.feature2Size, Line.VERTICAL));

        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{mode, radius, orientation};
    }

    public String toString() {
        return "Line Mean Features";
    }
}
