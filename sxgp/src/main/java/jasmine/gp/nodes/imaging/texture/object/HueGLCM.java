package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class HueGLCM extends Terminal {

    protected int i;

    public HueGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getHueGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.HueGLCM(mean)";
        case 1:
        	return "texture.HueGLCM(variance)";
        case 2:
        	return "texture.HueGLCM(entropy)";
        case 3:
        	return "texture.HueGLCM(uniformity)";
        case 4:
        	return "texture.HueGLCM(max)";
        case 5:
        	return "texture.HueGLCM(correlation)";
        case 6:
        	return "texture.HueGLCM(homogeneity)";
        case 7:
        	return "texture.HueGLCM(inertia)";
        case 8:        
        	return "texture.HueGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "HueGLCM(mean)";
        case 1:
        	return "HueGLCM(variance)";
        case 2:
        	return "HueGLCM(entropy)";
        case 3:
        	return "HueGLCM(uniformity)";
        case 4:
        	return "HueGLCM(max)";
        case 5:
        	return "HueGLCM(correlation)";
        case 6:
        	return "HueGLCM(homogeneity)";
        case 7:
        	return "HueGLCM(inertia)";
        case 8:        
        	return "HueGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

