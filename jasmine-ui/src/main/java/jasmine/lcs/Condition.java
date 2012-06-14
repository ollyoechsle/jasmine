package jasmine.lcs;


/**
 * General abstraction of a condition.
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public abstract class Condition {

    /**
     * Returns true of the condition matches a given input vector
     */
    public abstract boolean matches(InputVector i);

    /**
     * Performs crossover on the condition and returns an array of two children
     */
    public abstract Condition[] crossover(Condition other);

    /**
     * Performs mutation on the condition
     */
    public abstract void mutate(double probability);

    /**
     * Makes a fresh copy of the condition
     */
    public abstract Condition copy();

    public abstract boolean equals(Object other);

}
