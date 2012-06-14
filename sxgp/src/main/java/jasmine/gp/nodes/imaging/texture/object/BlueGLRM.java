package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class BlueGLRM extends Terminal {

    protected int i;

    public BlueGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getBlueGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.BlueGLRM(shortRun)";
        case 1:
        	return "texture.BlueGLRM(longRun)";
        case 2:
        	return "texture.BlueGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.BlueGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.BlueGLRM(runPercent)";
        case 5:
        	return "texture.BlueGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "BlueGLRM(shortRun)";
        case 1:
        	return "BlueGLRM(longRun)";
        case 2:
        	return "BlueGLRM(greyLevelNonUniformity)";
        case 3:
        	return "BlueGLRM(runLengthNonUniformity)";
        case 4:
        	return "BlueGLRM(runPercent)";
        case 5:
        	return "BlueGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

