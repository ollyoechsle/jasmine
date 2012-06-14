package jasmine.gp.nodes.imaging.image;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
 * Gets the average hue of the whole image
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Jun-2007
 * @version 1.0
 */
public class ImageHueMean extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }    

    public double execute(DataStack data) {
        data.value = data.getImage().getHueMean();       
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "image.getHueMean()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_8BIT_INT;
    }

    public String getShortName() {
        return "IHm";
    }

}
