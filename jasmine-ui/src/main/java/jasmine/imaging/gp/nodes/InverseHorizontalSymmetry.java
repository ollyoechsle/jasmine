package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

/**
 * Returns the horizontal symmetry for an object that has had half of it flipped over.
 *
 * @author Olly Oechsle, University of Essex, Date: 16-Apr-2007
 * @version 1.0
 */
public class InverseHorizontalSymmetry extends Terminal {

    public int getReturnType() {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getInverseHorizontalSymmetry();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getInverseHorizontalSymmetry()";
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.PERCENTAGE, NodeConstraints.FEATURE};
    }

    public String getShortName() {
        return "IHS";
    }

}
