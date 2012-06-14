package jasmine.gp;

import jasmine.gp.params.GPParams;
import jasmine.gp.tree.Node;

/**
 * <p/>
 * Represents a generic interface for crossover operators. There
 * are many crossover operators which may be used, some are better
 * than others.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Feb-2008
 * @version 1.0
 */
public interface Crossover {

    /**
     * Performs crossover on an array of parents (usually two) in order
     * to produce offspring.
     * @return The resulting trees, or null if an error occurs.
     */
    public Node[] produceOffspring(GPParams params, Node parent1, Node parent2);

}
