package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class LightnessGLRM extends Terminal {

    protected int i;

    public LightnessGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getLightnessGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.LightnessGLRM(shortRun)";
        case 1:
        	return "texture.LightnessGLRM(longRun)";
        case 2:
        	return "texture.LightnessGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.LightnessGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.LightnessGLRM(runPercent)";
        case 5:
        	return "texture.LightnessGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "LightGLRM(shortRun)";
        case 1:
        	return "LightGLRM(longRun)";
        case 2:
        	return "LightGLRM(greyLevelNonUniformity)";
        case 3:
        	return "LightGLRM(runLengthNonUniformity)";
        case 4:
        	return "LightGLRM(runPercent)";
        case 5:
        	return "LightGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

