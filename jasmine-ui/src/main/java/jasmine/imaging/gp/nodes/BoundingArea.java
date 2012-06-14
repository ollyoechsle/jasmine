package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 06-May-2009
 * Time: 12:16:43
 * To change this template use File | Settings | File Templates.
 */
public class BoundingArea extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getBoundingArea();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getBoundingArea()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_PERCENTAGE;
    }

    public String getShortName() {
        return "BoundArea";
    }

}
