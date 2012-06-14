package jasmine.gp.nodes.imaging.object;


import jasmine.gp.nodes.imaging.parameterised.ParameterisedTerminal;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.Perimeter;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.shapes.ExtraShapeData;

import java.util.Vector;

//POEY
public class GenericPerimeterFeatureMean extends ParameterisedTerminal {

    public static final int MAX_RADIUS = 10;
    public static final int MIN_RADIUS = 3;
    
    protected transient Perimeter p;
    protected int mode, radius;

    public GenericPerimeterFeatureMean() {
        this(0, PixelLoader.feature1Size);
    }

    public GenericPerimeterFeatureMean(int mode, int radius) {
        this.mode = mode;
        this.radius = radius;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack ds) {

        if (p == null) {
            p = new Perimeter(radius);
        }

        ds.usesImaging = true;
        switch (mode) {
            case Perimeter.MEAN:
                ds.value = p.getMean(ds.getImage(), (ExtraShapeData) ds.getData());  
                break;
            case Perimeter.VARIANCE:
                ds.value = p.getStdDeviationMean(ds.getImage(), (ExtraShapeData) ds.getData()); 
                break;
            case Perimeter.EDGE_COUNT:
            	ds.value = p.getEdgeCountMean(ds.getImage(), (ExtraShapeData) ds.getData());
                break;
        }

        return debugger == null ? ds.value : debugger.record(ds.value);

    }

    public String toJava() {
        switch (mode) {
            case Perimeter.MEAN:
                return "new Perimeter(" + radius + ").getMean(object)";
            case Perimeter.VARIANCE:
                return "new Perimeter(" + radius + ").getStdDeviationMean(object)";
            case Perimeter.EDGE_COUNT:
                return "new Perimeter(" + radius + ").getEdgeCountMean(object)";
        }
        return "";
    }

    public String getShortName() {
        return "Perim";
    }

    public ParameterisedTerminal getRandom() {
        int radius = getRandom(MIN_RADIUS, MAX_RADIUS);
        int mode = getRandom(0,2);
        return new GenericPerimeterFeatureMean(mode, radius);
    }

    public Vector<ParameterisedTerminal> getDefaults() {
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
        defaults.add(new GenericPerimeterFeatureMean(0, PixelLoader.feature1Size));
        defaults.add(new GenericPerimeterFeatureMean(0, PixelLoader.feature2Size));
        defaults.add(new GenericPerimeterFeatureMean(1, PixelLoader.feature1Size));
        defaults.add(new GenericPerimeterFeatureMean(1, PixelLoader.feature2Size));
        defaults.add(new GenericPerimeterFeatureMean(2, PixelLoader.feature1Size));
        defaults.add(new GenericPerimeterFeatureMean(2, PixelLoader.feature2Size));
        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{mode, radius};
    }

    public String toString() {
        return "Perimeter Features Mean";
    }

}
