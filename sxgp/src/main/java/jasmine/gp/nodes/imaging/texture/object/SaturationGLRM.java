package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class SaturationGLRM extends Terminal {

    protected int i;

    public SaturationGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getSaturationGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.SaturationGLRM(shortRun)";
        case 1:
        	return "texture.SaturationGLRM(longRun)";
        case 2:
        	return "texture.SaturationGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.SaturationGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.SaturationGLRM(runPercent)";
        case 5:
        	return "texture.SaturationGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "SatGLRM(shortRun)";
        case 1:
        	return "SatGLRM(longRun)";
        case 2:
        	return "SatGLRM(greyLevelNonUniformity)";
        case 3:
        	return "SatGLRM(runLengthNonUniformity)";
        case 4:
        	return "SatGLRM(runPercent)";
        case 5:
        	return "SatGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

