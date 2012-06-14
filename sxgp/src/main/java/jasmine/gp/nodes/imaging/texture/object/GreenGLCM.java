package jasmine.gp.nodes.imaging.texture.object;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

//POEY
public class GreenGLCM extends Terminal {

    protected int i;

    public GreenGLCM(int i) {
        super();
        this.i = i;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
    }

    public double execute(DataStack data) {
    	data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getGreenGLCM(i);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        
        switch (i) {
        case 0:
        	return "texture.GreenGLCM(mean)";
        case 1:
        	return "texture.GreenGLCM(variance)";
        case 2:
        	return "texture.GreenGLCM(entropy)";
        case 3:
        	return "texture.GreenGLCM(uniformity)";
        case 4:
        	return "texture.GreenGLCM(max)";
        case 5:
        	return "texture.GreenGLCM(correlation)";
        case 6:
        	return "texture.GreenGLCM(homogeneity)";
        case 7:
        	return "texture.GreenGLCM(inertia)";
        case 8:        
        	return "texture.GreenGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public String getShortName() {
        
        switch (i) {
        case 0:
        	return "GreenGLCM(mean)";
        case 1:
        	return "GreenGLCM(variance)";
        case 2:
        	return "GreenGLCM(entropy)";
        case 3:
        	return "GreenGLCM(uniformity)";
        case 4:
        	return "GreenGLCM(max)";
        case 5:
        	return "GreenGLCM(correlation)";
        case 6:
        	return "GreenGLCM(homogeneity)";
        case 7:
        	return "GreenGLCM(inertia)";
        case 8:        
        	return "GreenGLCM(clusterShade)";
        default:
        	return "not in case";
        }
    }

    public Object[] getConstructorArgs() {
        // make sure the min and max values are remembered when this node is cloned.
        return new Object[]{i};
    }
    

}

