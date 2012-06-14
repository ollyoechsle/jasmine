package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class GreenGLRM extends Terminal {

    protected int i;

    public GreenGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getGreenGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.GreenGLRM(shortRun)";
        case 1:
        	return "texture.GreenGLRM(longRun)";
        case 2:
        	return "texture.GreenGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.GreenGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.GreenGLRM(runPercent)";
        case 5:
        	return "texture.GreenGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "GreenGLRM(shortRun)";
        case 1:
        	return "GreenGLRM(longRun)";
        case 2:
        	return "GreenGLRM(greyLevelNonUniformity)";
        case 3:
        	return "GreenGLRM(runLengthNonUniformity)";
        case 4:
        	return "GreenGLRM(runPercent)";
        case 5:
        	return "GreenGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

