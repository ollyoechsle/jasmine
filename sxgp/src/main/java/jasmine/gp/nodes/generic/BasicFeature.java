package jasmine.gp.nodes.generic;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

/**
 * Allows you to access the features array in the datastack
 * by index. Allows you to plug in generic data into the GP
 * system. See CSV Feature for a more advanced version.
 *
 * @author Olly Oechsle, University of Essex, Date: 18-Mar-2008
 * @version 1.0
 */
public class BasicFeature extends Terminal {

    protected int index;

    public BasicFeature(int index) {
        this.index = index;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = data.features[index];
        return debugger == null? data.value : debugger.record(data.value);
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this node is cloned.
        return new Object[]{index};
    }

    public String getShortName() {
        return "b"+index;
    }

    public String toJava() {
        return "features[" + index + "]";
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

}



