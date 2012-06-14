package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class LightnessGLCM extends Terminal {

    protected int i;

    public LightnessGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getLightnessGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.LightnessGLCM(mean)";
        case 1:
        	return "texture.LightnessGLCM(variance)";
        case 2:
        	return "texture.LightnessGLCM(entropy)";
        case 3:
        	return "texture.LightnessGLCM(uniformity)";
        case 4:
        	return "texture.LightnessGLCM(max)";
        case 5:
        	return "texture.LightnessGLCM(correlation)";
        case 6:
        	return "texture.LightnessGLCM(homogeneity)";
        case 7:
        	return "texture.LightnessGLCM(inertia)";
        case 8:        
        	return "texture.LightnessGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "LightGLCM(mean)";
        case 1:
        	return "LightGLCM(variance)";
        case 2:
        	return "LightGLCM(entropy)";
        case 3:
        	return "LightGLCM(uniformity)";
        case 4:
        	return "LightGLCM(max)";
        case 5:
        	return "LightGLCM(correlation)";
        case 6:
        	return "LightGLCM(homogeneity)";
        case 7:
        	return "LightGLCM(inertia)";
        case 8:        
        	return "LightGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

