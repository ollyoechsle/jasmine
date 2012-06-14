package jasmine.gp.nodes.imaging;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
* Returns the hue component of the pixel in the image. Coordinates are defined by the DataStack object.
 *
 * @author Olly Oechsle, University of Essex, Date: 19-Jan-2007
 * @version 1.0
 */
public class Hue extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.INTEGER, NodeConstraints.RANGE255, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = data.getImage().getHue(data.x, data.y);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "image.getHue(x, y)";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_8BIT_INT;
    }

    public String getShortName() {
        return "H";
    }

}
