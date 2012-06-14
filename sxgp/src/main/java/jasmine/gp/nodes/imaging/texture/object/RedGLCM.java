package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class RedGLCM extends Terminal {

    protected int i;

    public RedGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getRedGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.RedGLCM(mean)";
        case 1:
        	return "texture.RedGLCM(variance)";
        case 2:
        	return "texture.RedGLCM(entropy)";
        case 3:
        	return "texture.RedGLCM(uniformity)";
        case 4:
        	return "texture.RedGLCM(max)";
        case 5:
        	return "texture.RedGLCM(correlation)";
        case 6:
        	return "texture.RedGLCM(homogeneity)";
        case 7:
        	return "texture.RedGLCM(inertia)";
        case 8:        
        	return "texture.RedGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "RedGLCM(mean)";
        case 1:
        	return "RedGLCM(variance)";
        case 2:
        	return "RedGLCM(entropy)";
        case 3:
        	return "RedGLCM(uniformity)";
        case 4:
        	return "RedGLCM(max)";
        case 5:
        	return "RedGLCM(correlation)";
        case 6:
        	return "RedGLCM(homogeneity)";
        case 7:
        	return "RedGLCM(inertia)";
        case 8:        
        	return "RedGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

