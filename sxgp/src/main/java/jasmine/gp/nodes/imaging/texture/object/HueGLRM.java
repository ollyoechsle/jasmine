package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class HueGLRM extends Terminal {

    protected int i;

    public HueGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getHueGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.HueGLRM(shortRun)";
        case 1:
        	return "texture.HueGLRM(longRun)";
        case 2:
        	return "texture.HueGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.HueGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.HueGLRM(runPercent)";
        case 5:
        	return "texture.HueGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "HueGLRM(shortRun)";
        case 1:
        	return "HueGLRM(longRun)";
        case 2:
        	return "HueGLRM(greyLevelNonUniformity)";
        case 3:
        	return "HueGLRM(runLengthNonUniformity)";
        case 4:
        	return "HueGLRM(runPercent)";
        case 5:
        	return "HueGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

