package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class BlueGLCM extends Terminal {

    protected int i;

    public BlueGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getBlueGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.BlueGLCM(mean)";
        case 1:
        	return "texture.BlueGLCM(variance)";
        case 2:
        	return "texture.BlueGLCM(entropy)";
        case 3:
        	return "texture.BlueGLCM(uniformity)";
        case 4:
        	return "texture.BlueGLCM(max)";
        case 5:
        	return "texture.BlueGLCM(correlation)";
        case 6:
        	return "texture.BlueGLCM(homogeneity)";
        case 7:
        	return "texture.BlueGLCM(inertia)";
        case 8:        
        	return "texture.BlueGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "BlueGLCM(mean)";
        case 1:
        	return "BlueGLCM(variance)";
        case 2:
        	return "BlueGLCM(entropy)";
        case 3:
        	return "BlueGLCM(uniformity)";
        case 4:
        	return "BlueGLCM(max)";
        case 5:
        	return "BlueGLCM(correlation)";
        case 6:
        	return "BlueGLCM(homogeneity)";
        case 7:
        	return "BlueGLCM(inertia)";
        case 8:        
        	return "BlueGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

