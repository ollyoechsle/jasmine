package jasmine.gp.nodes.logic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

/**
 * AND Boolean Operator
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Mar-2007
 * @version 1.0
 */
public class OR_FP extends Node {

    public OR_FP() {
        super(2);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER};
    }

    public int getChildType(int index) {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.value = (child[0].execute(data) > 0 || child[1].execute(data) > 0)? 1 : 0;
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return child[0].getName() + " > 0 || " + child[1].getName() + " > 0 ? 1 : 0";
    }

    public String getShortName() {
        return "|";
    }

}