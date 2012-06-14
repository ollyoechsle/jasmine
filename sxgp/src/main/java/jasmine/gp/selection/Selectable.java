package jasmine.gp.selection;

/**
 * Any object which implements this interface can be selected by the selection algorithms.
 *
 * @author Olly Oechsle, University of Essex, Date: 30-Jan-2008
 * @version 1.0
 */
public interface Selectable {

    /**
     * Returns the size of the object. If two selectables have the same fitness
     * then the selector will choose the smallest (supposedly more efficient) one.
     */
    public int getTreeSize();

    /**
     * What niche is this object a member of? Members of the same niche can be
     * selected by the tree builder, providing it is told to do so. 
     */
    public int getIslandID();

    /**
     * Gets the fitness of the individual. This being Koza fitness, the closer
     * to zero the number is the better the individual. An individual with zero
     * fitness is ideal.
     */
    public double getKozaFitness();

}
