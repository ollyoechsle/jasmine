package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Gets the variance of edges to the left of the centre of gravity
 *
 * @author Olly Oechsle, University of Essex, Date: 25-Apr-2007
 * @version 1.0
 */
public class BalanceXLeftVariance extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }
    
    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getBalanceXLeftVariance();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getBalanceXLeftVariance()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_TINY_DOUBLE;
    }

    public String getShortName() {
        return "BXLV";
    }

}
