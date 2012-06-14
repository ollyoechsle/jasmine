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
public class Rectangularity extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.PERCENTAGE, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getRectangularity();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getRectangularity()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_PERCENTAGE;
    }

    public String getShortName() {
        return "Rectangularity";
    }

}