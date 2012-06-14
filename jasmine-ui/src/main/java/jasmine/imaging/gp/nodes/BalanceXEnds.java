package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Returns the balance of the ends (are they all on one side or the other?)
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Apr-2007
 * @version 1.0
 */
public class BalanceXEnds extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.PERCENTAGE, NodeConstraints.FEATURE};
    }


    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getEndBalanceX();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getEndBalanceX()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_PERCENTAGE;
    }

    public String getShortName() {
        return "BalXEnds";
    }

}
