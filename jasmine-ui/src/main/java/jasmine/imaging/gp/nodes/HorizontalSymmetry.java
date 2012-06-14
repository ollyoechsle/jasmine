package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Finds out how symmetric a shape is. If every pixel is perfectly mirrored, then the function
 * returns 1.
 *
 * @author Olly Oechsle, University of Essex, Date: 16-Apr-2007
 * @version 1.0
 */
public class HorizontalSymmetry extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.PERCENTAGE,  NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getHorizontalSymmetry();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getHorizontalSymmetry()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_PERCENTAGE;
    }

    public String getShortName() {
        return "Horiz-Sym";
    }

}
