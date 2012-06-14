package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
 * Represents a Y variable in the math problem.
 *
 * The value of Y comes from the datastack's getY() method.
 *
 * You can set the value of Y in the problem using the setY() method on the datastack.
 *
 * @author Olly Oechsle, University of Essex, Date: 22-Jan-2008
 * @version 1.0
 */
public class Y extends Terminal {

    int specialType = -1;

    public Y()  {
        specialType = -1;
    }

    public Y(int returnType) {
        this.specialType = returnType;
    }

    public int[] getReturnTypes() {
        if (specialType != -1 && specialType != NodeConstraints.NUMBER) {
            return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE,  specialType};
        } else {
            return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
        }
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = data.getY();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "Y";
    }

    
    /**
     * Gets the arguments that would have to be passed to the constructor if the constructor takes arguments.
     * In most cases this is not the case so this method doesn't need to be overridden.
     */
    public Object[] getConstructorArgs() {
        return new Object[]{specialType};
    }

}
