package jasmine.gp.nodes.logic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * Performs a NOT operation on a boolean value.
 *
 * @author Olly Oechsle, University of Essex, Date: 02-Apr-2007
 * @version 1.0
 */
public class NOT extends Node {

    public NOT() {
        super(1);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.BOOLEAN, NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.BOOLEAN;
    }

    public double execute(DataStack data) {
        data.value = child[0].execute(data) != 1? 1 : 0;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "!" + child[0].getName();
    }

    public String getShortName() {
        return "!";
    }

}
