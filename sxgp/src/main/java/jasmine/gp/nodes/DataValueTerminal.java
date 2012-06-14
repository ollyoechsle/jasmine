//package ac.ooechs.classify.classifier.gp;
package jasmine.gp.nodes;


import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

import java.io.PrintWriter;
import java.util.Vector;

/**
 * <p/>
 * Provides access to one of the values inside a data object.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 2.0 Compatible with CSV and Arbitrary Imaging Terminals, March 2009
 */
public class DataValueTerminal extends Terminal {

    // need to know which imaging terminals are being used
    public static Vector<Terminal> imagingTerminals;

    // by default, the datavalueterminal gets its data from the array of pre-calculated
    // values, which is the most efficient way of proceeding.
    public static float[] currentValues;

    // the terminal that I use for imaging
    public Terminal terminal;

    // the column index for extracting information from current values
    protected int index;

    public DataValueTerminal(int index) {
        this.index = index;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        if (data.getImage() != null || data.getData() != null) {
            // since current values is null, I will use my own terminal to execute directly
            // on the image. I need to get this from a list of terminals which should have been
            // set as a static member of this class.
            if (terminal == null) {
                if (imagingTerminals == null) {
                    throw new RuntimeException("Data Value Terminal execute(): Cannot get terminal since static member imagingTerminals has not been set.");
                }
                // get the terminal from the list
                terminal = imagingTerminals.elementAt(index);
                System.out.println("Setting terminal to " + terminal.toJava());
            }
            data.value = terminal.execute(data);
        } else {
            data.value = currentValues[index];
        }
        return debugger == null? data.value : debugger.record(data.value);
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this terminal is copied.
        return new Object[]{index};
    }

    public String toJava() {
        if (terminal != null) {
            return terminal.toJava();
        } else {
            // default is to return the index of the CSV column
            return "feature[" + index + "]";
        }
    }

    public String getShortName() {
        return "f" + index;
    }

}
