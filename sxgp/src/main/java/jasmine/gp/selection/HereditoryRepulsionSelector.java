package jasmine.gp.selection;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;
import jasmine.gp.selection.Selectable;

import java.util.Vector;

/**
 * Prevents crossover between individuals which are similar to eachother.
 * AKA prevents incest.
 * From the paper:
 * "A Simple Powerful Constraint for Genetic Programming"
 * by Gearoid Murphy and Conor Ryan, LNCS 4971, 2008
 * @author Olly Oechsle, University of Essex, Date: 6-March-2009
 */
public class HereditoryRepulsionSelector extends Selector {

    public static Individual compareIndividual;

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
     * Finds a pair of individuals that have the least common ancestry
     */
    public Selectable select() {
        if (compareIndividual == null) {
            Selectable best = null;
            for (int t = 0; t < tournamentSize; t++) {
                Selectable ind = getRandomIndividual();
                if (best == null || best.getKozaFitness() > ind.getKozaFitness() || best.getKozaFitness() == ind.getKozaFitness() && best.getTreeSize() < ind.getTreeSize()) best = ind;
            }
            return best;
        }

        Individual best = null;
        int lowestsimilarity = Integer.MAX_VALUE;

        for (int t = 0; t < tournamentSize; t++) {
            Individual ind = getRandomIndividual();
            int similarity = ind.getAncestrySimilarity(compareIndividual);
            if (similarity < lowestsimilarity) {
                lowestsimilarity = similarity;
                best = ind;
                if (lowestsimilarity == 0) break;
            }
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