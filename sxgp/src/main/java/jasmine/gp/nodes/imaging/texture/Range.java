package jasmine.gp.nodes.imaging.texture;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
* Returns the result of a 3x3 range operation on the image, at its current position.
 *
 * @author Olly Oechsle, University of Essex, Date: 29-Jan-2007
 * @version 1.0
 */
public class Range extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.INTEGER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;        
        data.value = data.getImage().get3x3Range(data.x, data.y);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "image.get3x3Range(x, y)";
    }

    public String getShortName() {
        return "RA";
    }

}
