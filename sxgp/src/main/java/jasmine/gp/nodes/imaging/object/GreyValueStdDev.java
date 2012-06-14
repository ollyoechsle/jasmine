package jasmine.gp.nodes.imaging.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class GreyValueStdDev extends Terminal {

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }
    
    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getGreyValueStdDev(); 
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "colour.getGreyValueStdDev()";
    }

    public String getShortName() {
        return "Grsd";
    }

}
