package jasmine.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
 * Represents a X variable in the math problem.
 *
 * The value of X comes from the datastack's getX() method.
 *
 * You can set the value of X in the problem using the setX() method on the datastack.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class X extends Terminal {

    int specialType = -1;

    public X()  {
        specialType = -1;
    }

    public X(int returnType) {
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
        data.value = data.getX();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "X";
    }


    /**
     * Gets the arguments that would have to be passed to the constructor if the constructor takes arguments.
     * In most cases this is not the case so this method doesn't need to be overridden.
     */
    public Object[] getConstructorArgs() {
        return new Object[]{specialType};
    }
}
