package jasmine.gp.nodes.ercs;


import jasmine.gp.Evolve;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Node;

import java.text.DecimalFormat;

/**
 * <p/>
 * ERC Stands for Ephemeral Random Constant. It allows you to evolve programs
 * containing constant values, (in addition to the other fixed terminal values).
 * These values may be used as thresholds, or in mathematical functions, and can
 * be very useful. They have a value which is set when they are created and kept
 * until mutation happens.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public abstract class BasicERC extends Node {

    protected final int max = 20;

    private double value;

    private boolean valueSet = false;    

    public BasicERC() {
        super(0);
        setValue(initialise());
    }

    /**
     * Sets the value of the ERC to something different.
     */
    public void setValue(double value) {
        this.value = value;       
    }

    /**
     * Mutates the ERC by calling the initialise() method again.
     * Override this method if you want to disable mutation completely
     * on a particular type of ERC.
     */
    public void mutate() {
    	//POEY comment: for initialise() -> jasmine.gp.nodes.ercs
        //such as CustomRangeIntegerERC.java, PercentageERC.java, BoolERC.java 
    	setValue(initialise());
    }

    /**
     * Jitters the ERC, by default within 20% of its current value, although
     * you can override this method if you prefer.
     */
    public void jitter() {
        // jitter +-  20% of the value
        double amount = 0.20;
        double change = 1.0 - amount + (Evolve.getRandomNumber() * amount * 2);      
        value *= change;
    }

    /**
     * Initialises the ERC with its value.
     * @return The value chosen for the ERC
     */
    public abstract double initialise();

    public double getValue() {
        return value;
    }

    /**
     * The return type of the ERC, by default this is a number.
     * @return
     */
    public abstract int[] getReturnTypes();

    /**
     * Executes the ERC
     */
    public double execute(DataStack data) {
        data.value = getValue();
        return debugger == null? data.value : debugger.record(data.value);
    }

    /**
     * Returns the ERC as java code, essentially just returns the value as a string
     */
    public String toJava() {
        return String.valueOf(getValue());
    }

    public String getShortName() {
        return new DecimalFormat("0.0").format(getValue());
    }

    public int getChildType(int index) {
        return NodeConstraints.VOID;
    }

    /**
     * Standard nodes don't have range types, but Terminals do, so we need to override the NodeParams object.
     * @return
     */
    public NodeConstraints createNodeConstraintsObject() {
        NodeConstraints c = new NodeConstraints(getClass().getCanonicalName(), getReturnTypes(),  numChildren, NodeConstraints.ERC);
        Object[] args = getConstructorArgs();
        if (args != null) {
            c.setArgs(args);
        }
        return c;
    }


}
