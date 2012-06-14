package jasmine.imaging.core.util;


import jasmine.gp.tree.Terminal;

import java.util.Vector;
import java.io.Serializable;

/**
 * Stores various meta data about a terminal such as whether it is
 * enabled and its fitness.
 *
 * @author Olly Oechsle, University of Essex, Date: 21-Feb-2008
 * @version 1.0
 */
public class TerminalMetaData implements Serializable, Comparable {

    protected String name;
    protected String classname;
    protected boolean enabled;
    protected double fitness;
    protected Terminal t;

    public TerminalMetaData(Terminal t) {
        this.name = t.getName();
        this.classname = t.getClass().getCanonicalName();
        this.enabled = true;
        this.fitness = 1.0;
        this.t = t;
    }

    public String getName() {
        return name;
    }

    public String getClassname() {
        return classname;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getFitness() {
        return fitness;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Terminal getTerminal(Vector<Terminal> terminals) {
        /*for (int i = 0; i < terminals.size(); i++) {
            Terminal terminal = terminals.elementAt(i);
            if (terminal.getClass().getCanonicalName().equals(classname)) return terminal;
        }
        return null;*/
        return t;
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     */
    public int compareTo(Object o) {
        TerminalMetaData other = (TerminalMetaData) o;
        if (other.fitness > this.fitness) return +1;
        if (other.fitness < this.fitness) return -1;
        return 0;
    }
    
}
