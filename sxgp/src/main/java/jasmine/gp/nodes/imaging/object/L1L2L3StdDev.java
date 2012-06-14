package jasmine.gp.nodes.imaging.object;


import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class L1L2L3StdDev extends Terminal {

    protected int i;

    public L1L2L3StdDev(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getL1L2L3StdDev(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "colour.getL1L2L3StdDev(" + i + ")";
    }

    public String getShortName() {
        return "L1L2L3sd("+i+")";
    }
    
    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }


}

