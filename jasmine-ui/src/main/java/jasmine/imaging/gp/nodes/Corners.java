package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Returns the number of corners a shape has.
 *
 * @author Olly Oechsle, University of Essex, Date: 13-Feb-2007
 * @version 1.0
 */
public class Corners extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).countCorners();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.countCorners()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_SMALL_INT;
    }

    public String getShortName() {
        return "Corners";
    }

}
