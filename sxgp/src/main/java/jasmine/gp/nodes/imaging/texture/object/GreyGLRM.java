package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class GreyGLRM extends Terminal {

    protected int i;

    public GreyGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getGreyGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.GreyGLRM(shortRun)";
        case 1:
        	return "texture.GreyGLRM(longRun)";
        case 2:
        	return "texture.GreyGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.GreyGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.GreyGLRM(runPercent)";
        case 5:
        	return "texture.GreyGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "GreyGLRM(shortRun)";
        case 1:
        	return "GreyGLRM(longRun)";
        case 2:
        	return "GreyGLRM(greyLevelNonUniformity)";
        case 3:
        	return "GreyGLRM(runLengthNonUniformity)";
        case 4:
        	return "GreyGLRM(runPercent)";
        case 5:
        	return "GreyGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

