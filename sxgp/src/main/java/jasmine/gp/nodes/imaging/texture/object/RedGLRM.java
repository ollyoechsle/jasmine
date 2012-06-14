package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class RedGLRM extends Terminal {

    protected int i;

    public RedGLRM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getRedGLRM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.RedGLRM(shortRun)";
        case 1:
        	return "texture.RedGLRM(longRun)";
        case 2:
        	return "texture.RedGLRM(greyLevelNonUniformity)";
        case 3:
        	return "texture.RedGLRM(runLengthNonUniformity)";
        case 4:
        	return "texture.RedGLRM(runPercent)";
        case 5:
        	return "texture.RedGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "RedGLRM(shortRun)";
        case 1:
        	return "RedGLRM(longRun)";
        case 2:
        	return "RedGLRM(greyLevelNonUniformity)";
        case 3:
        	return "RedGLRM(runLengthNonUniformity)";
        case 4:
        	return "RedGLRM(runPercent)";
        case 5:
        	return "RedGLRM(runEntropy)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

