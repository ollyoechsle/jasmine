package jasmine.gp.nodes.imaging.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.params.RangeTypes;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class NormalisedRedMean extends Terminal {
	
    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }    

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getNormalisedRedMean();  
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "colour.getNormalisedRedMean()";
    }

    public int getDefaultRangeType() {
        return RangeTypes.RANGE_8BIT_INT;
    }

    public String getShortName() {
        return "nRm";
    }


}
