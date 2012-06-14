package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class GreyGLCM extends Terminal {

    protected int i;

    public GreyGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getGreyGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.GreyGLCM(mean)";
        case 1:
        	return "texture.GreyGLCM(variance)";
        case 2:
        	return "texture.GreyGLCM(entropy)";
        case 3:
        	return "texture.GreyGLCM(uniformity)";
        case 4:
        	return "texture.GreyGLCM(max)";
        case 5:
        	return "texture.GreyGLCM(correlation)";
        case 6:
        	return "texture.GreyGLCM(homogeneity)";
        case 7:
        	return "texture.GreyGLCM(inertia)";
        case 8:        
        	return "texture.GreyGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "GreyGLCM(mean)";
        case 1:
        	return "GreyGLCM(variance)";
        case 2:
        	return "GreyGLCM(entropy)";
        case 3:
        	return "GreyGLCM(uniformity)";
        case 4:
        	return "GreyGLCM(max)";
        case 5:
        	return "GreyGLCM(correlation)";
        case 6:
        	return "GreyGLCM(homogeneity)";
        case 7:
        	return "GreyGLCM(inertia)";
        case 8:        
        	return "GreyGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

