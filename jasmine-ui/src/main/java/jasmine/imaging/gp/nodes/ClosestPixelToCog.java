package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Finds the pixel closest to the point of the shape's Center of gravity
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Mar-2007
 * @version 1.0
 */
public class ClosestPixelToCog extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getClosestPixelToCog();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getClosestPixelToCog()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_PERCENTAGE;
    }

    public String getShortName() {
        return "ClosestPxCog";
    }

}
