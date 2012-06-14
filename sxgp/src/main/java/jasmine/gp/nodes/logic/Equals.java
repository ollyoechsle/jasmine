package jasmine.gp.nodes.logic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
* Returns TRUE if the value of the child is EQUALS another valu
 *
 * @author Olly Oechsle, University of Essex, Date: 13-Mar-2007
 * @version 1.0
 */
public class Equals extends Node {

    public Equals() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.BOOLEAN, NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = child[0].execute(data) == child[1].execute(data)? 1 : 0;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return child[0].getName() + " == " + child[1].getName();
    }

    public String getShortName() {
        return "=";
    }

}
