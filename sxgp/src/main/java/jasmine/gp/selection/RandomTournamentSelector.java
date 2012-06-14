package jasmine.gp.selection;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;
import jasmine.gp.selection.Selectable;

import java.util.Vector;

/**
 * <p>
 * Basic Random Tournament Selection. Randomly finds x individuals where
 * x is the tournament size. It then chooses the best individual from
 * this tournament. This is defined as the individual with the
 * highest fitness, or (if there is a tie) the fittest individual with
 * the smallest tree size.
 * </p>
 * <p>
 * Tournament selection is generally regarded as the better selection method
 * as it is not affected by outliers (as is Fitness Proportional Selection),
 * and doesn't require any sorting (as does Ranked Selection), and can easily
 * tune the selection pressure using the tournament size parameter.
 * </p>
 * <p>
 * Although it is generally the best at maintaining diversity in a population,
 * some individuals may never be sampled and therefore never selected. In this
 * case you may prefer to use the UnBiasedTournamentSelector, which matches up
 * the individuals in such a way that every individual in the population is guaranteed
 * two tournaments.
 * </p>
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.01 Supports niched populations too 22-Jan-2008
 * @see jasmine.gp.selection.UnbiasedTournamentSelector
 */
public class RandomTournamentSelector extends Selector {

    /**
     * How many individuals compete in the tournament?
     */
    protected int tournamentSize;

    /**
     * Initialises the tournament selector. Grabs the tournament size
     * from the params object.
     */
    public void initialise(GPParams params) {    	
        this.tournamentSize = params.getTournamentSize();
    }

    /**
     * Selects the individual with the best (lowest koza) fitness from the
     * tournament of randomly selected individuals. If two individuals have exactly
     * the same fitness, then the smaller one is chosen.
     */
    public Selectable select() {
   	
        Selectable best = null;
        for (int t = 0; t < tournamentSize; t++) {
            Selectable ind = getRandomIndividual();            
            if (best == null || best.getKozaFitness() > ind.getKozaFitness() || best.getKozaFitness() == ind.getKozaFitness() && best.getTreeSize() < ind.getTreeSize()) 
            	best = ind;
        }        
        return best;
    }

    /**
     * Gets a random individual for the selection process. Individuals are chosen
     * with uniform probability.
     */
    protected Individual getRandomIndividual() {
        // returns the list of suitable indices
        Vector<Integer> pop = getPopulation();
        if (pop == null) {
            // if null is returned, then can use any index
            return population[((int) (Evolve.getRandomNumber() * population.length))];
        } else {
            // choose a specific index
            int index = pop.elementAt(((int) (Evolve.getRandomNumber() * pop.size())));
            return super.population[index];
        }        
    }

}
