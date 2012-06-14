package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class SaturationGLCM extends Terminal {

    protected int i;

    public SaturationGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getSaturationGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.SaturationGLCM(mean)";
        case 1:
        	return "texture.SaturationGLCM(variance)";
        case 2:
        	return "texture.SaturationGLCM(entropy)";
        case 3:
        	return "texture.SaturationGLCM(uniformity)";
        case 4:
        	return "texture.SaturationGLCM(max)";
        case 5:
        	return "texture.SaturationGLCM(correlation)";
        case 6:
        	return "texture.SaturationGLCM(homogeneity)";
        case 7:
        	return "texture.SaturationGLCM(inertia)";
        case 8:        
        	return "texture.SaturationGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "SatGLCM(mean)";
        case 1:
        	return "SatGLCM(variance)";
        case 2:
        	return "SatGLCM(entropy)";
        case 3:
        	return "SatGLCM(uniformity)";
        case 4:
        	return "SatGLCM(max)";
        case 5:
        	return "SatGLCM(correlation)";
        case 6:
        	return "SatGLCM(homogeneity)";
        case 7:
        	return "SatGLCM(inertia)";
        case 8:        
        	return "SatGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

