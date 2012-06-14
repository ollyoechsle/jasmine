package jasmine.imaging.core.segmentation;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

public class AdaptiveBinaryThreshold extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.INTEGER, NodeConstraints.RANGE255, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = data.getImage().getAdaptiveBinaryThreshold(data.x, data.y);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "image.getAdaptiveBinaryThreshold(x, y)";
    }

    public String getShortName() {
        return "T";
    }

}
