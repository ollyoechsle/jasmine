package jasmine.gp.nodes.imaging.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class C1C2C3StdDev extends Terminal {

    protected int i;

    public C1C2C3StdDev(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getC1C2C3StdDev(i);  
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "colour.getC1C2C3StdDev(" + i + ")";
    }

    public String getShortName() {
        return "C1C2C3sd("+i+")";
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}
