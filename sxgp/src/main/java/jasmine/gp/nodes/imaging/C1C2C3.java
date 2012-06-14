package jasmine.gp.nodes.imaging;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
* Returns the red component of the pixel in the image. Coordinates are defined by the DataStack object.
 *
 * @author Olly Oechsle, University of Essex, Date: 18-Jan-2007
 * @version 1.0
 */
public class C1C2C3 extends Terminal {

    protected int i;

    public C1C2C3(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.PERCENTAGE, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = data.getImage().getC1C2C3(data.x, data.y, i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "image.getC1C2C3(x, y, " + i + ")";
    }

    public String getShortName() {
        return "C1C2C3("+i+")";
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}