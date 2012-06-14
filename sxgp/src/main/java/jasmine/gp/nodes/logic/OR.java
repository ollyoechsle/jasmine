package jasmine.gp.nodes.logic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * OR Boolean Operator. Returns true if one or both inputs
 * is true.
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Mar-2007
 * @version 1.0
 */
public class OR extends Node {

    public OR() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.BOOLEAN, NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.BOOLEAN;
    }

    public double execute(DataStack data) {
        data.value = (child[0].execute(data) == 1 || child[1].execute(data) == 1)? 1 : 0;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return child[0].getName() + " || " + child[1].getName();
    }

    public String getShortName() {
        return "|";
    }

}
