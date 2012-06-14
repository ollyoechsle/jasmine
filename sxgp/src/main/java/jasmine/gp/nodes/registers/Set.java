package jasmine.gp.nodes.registers;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * Sets a given register to a particular value.
 *
 * @author Olly Oechsle, University of Essex, Date: 23-Oct-2008
 * @version 1.0
 */
public class Set extends Node {

   protected int index;

   public Set(int index) {
       super(1);
       this.index = index;
   }

    /**
     * Any processing the node does takes place here
     */
    public double execute(DataStack data) {
        double value = child[0].execute(data);
        data.registers.values[index] = value;
        return value;
    }

    /**
     * Tells what return type each of the node's children should have
     *
     * @param index The index of the child, starting from 0
     */
    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    /**
     * Strongly typed GP: What type does the node return?
     */
    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.VOID};
    }

    /**
     * What would the java be if this node were written in it?
     */
    public String toJava() {
        return "register" + index + " = " + child[0].getName();
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this node is cloned.
        return new Object[]{index};
    }

}
