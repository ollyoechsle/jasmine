package jasmine.gp.selection;


import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;
import jasmine.gp.selection.Selectable;

import java.util.Vector;
import java.util.Hashtable;

/**
 * Base class for a selector, which chooses parents for crossover, mutation
 * and reproduction. The most common one is Tournament Selection, owing to
 * its simplicity and ease of tuning, but there are many others. SXGP uses
 * tournament selection by default, have a look at the RandomTournamentSelector
 * class for more details.
 *
 * @author Olly Oechsle, University of Essex, Date: 22-Jan-2007
 * @version 1.0
 * @see jasmine.gp.selection.RandomTournamentSelector
 */
public abstract class Selector {

    public Selector() {
        islandCache = new Hashtable<Integer, Vector<Integer>>();
    }

    /**
     * Default niche - select from the whole population - so called
     * panmictic selection.
     */
    public static final int ANY_ISLAND = -1;

    /**
     * Niche - determines where the selector will choose individuals
     * from. If it is ANY_NICE, the selector will select from the whole
     * population.
     */
    protected int island = ANY_ISLAND;

    /**
     * A reference to the the (already evaluated) population. This member
     * is kept private because we want to ensure that extending classes get access
     * to the population through the getPopulation() method, which allows
     * this class to ensure that niches are adhered to.
     */
    protected Individual[] population;

    /**
     * If operating in population niches, we'll chop up the population
     * into bits so that we can make the selection more efficient. If niches
     * are not used this variable remains null and isn't used.
     */
    protected Hashtable<Integer, Vector<Integer>> islandCache = null;

    /**
     * Gives the selector a reference to the current population.
     * The population must contain objects of type Selectable
     * @see jasmine.gp.selection.Selectable
     */
    public void setPopulation(Individual[] population) {
        this.population = population;
    }

    /**
     * Allows the selector to initialise, giving it a reference to the params object,
     * from which it may take any parameters it requires.
     */
    public abstract void initialise(GPParams params);

    /**
     * Selects an individual. This method may be implemented by different
     * selection methods.
     */
    public abstract Selectable select();

    /**
     * Returns the niche that the selector will choose individuals from.
     * If it is -1 the selector will select from the whole population.
     */
    public int getIsland() {
        return island;
    }

    /**
     * Sets the niche that the selector will choose individuals from.
     * Set it to -1 if you want the selector to select from the whole
     * population (the default) -- so called panmictic selection.
     */
    public void setIsland(int island) {
        this.island = island;
    }

    public void clear() {
        if (islandCache != null) {
            //System.out.println("Clearing cache");
            islandCache.clear();
        }
    }


    /**
     * Gets the population indices (needed by selection implementations). Ensures that what
     * is returned complies with the niche this selector has been instructed to choose.
     */
    protected Vector<Integer> getPopulation() {
        
        if (island == ANY_ISLAND) {
            // return the whole population
            return null;
        } else {
            // return individuals with the given islandID

            Vector<Integer> suitable = islandCache.get(island);

            if (suitable == null) {

                suitable = new Vector<Integer>(population.length / 10);

                for (int i = 0; i < population.length; i++) {
                    Individual individual = population[i];
                    if (individual.getIslandID() == island || individual.getIslandID() == ANY_ISLAND) {
                        suitable.add(i);
                    }
                }

                islandCache.put(island, suitable);

            }

            return suitable;

        }
    }
}
