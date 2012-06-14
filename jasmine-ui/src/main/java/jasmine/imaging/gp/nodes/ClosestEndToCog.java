package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Returns the distance to the end which is closest to the Centre of Gravity
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Apr-2007
 * @version 1.0
 */
public class ClosestEndToCog extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getClosestEndToCog();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getClosestEndToCog()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_PERCENTAGE;
    }


    public String getShortName() {
        return "Close-End-CoG";
    }


}
