package jasmine.gp.selection;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;

import java.util.Vector;

/**
 * <p>
 * Testing Implementation of Unbiased Tournament selection (Sokolov and Whitley, 2005)
 * The algorithm matches up the individuals in such a way that every individual
 * in the population is guaranteed two tournaments.
 * </p>
 *
 * <p>
 * TODO: Caution: This is not working yet and doesn't support niches. I've made it
 * TODO: abstract so you can't use it by mistake
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 07-Jan-2008
 * @version 1.0
 */
public abstract class UnbiasedTournamentSelector extends Selector {

    protected int index = 0;
    protected int P[];
    protected Vector<Individual> population;

    public void initialise(GPParams params) {    	
        this.population = population;
        if (params.getTournamentSize() != 2) {
            throw new RuntimeException("Alert - Unbiased tournament selector only supports a population size of 2");
        }

        Vector<Integer> indices = new Vector<Integer>(population.size());
        for (int i = 0; i < population.size(); i++) {
            indices.add(i);
        }

        P = new int[population.size()];
        for (int i = 0; i < P.length; i++) {
            // todo: P[i] should never equal i, although sometimes it will with this basic implementation.
            Integer index = indices.get((int) (Evolve.getRandomNumber() * indices.size()));
            P[i] = index;
            indices.remove(index);
        }
    }

    public Individual select() {
        Individual ind = select(index);
        index++;
        if (index == population.size()) index = 0;
        return ind;
    }

    public Individual select(int index) {
        Individual ind1 = population.elementAt(index);
        Individual ind2 = population.elementAt(P[index]);
        if (ind1.getKozaFitness() > ind2.getKozaFitness() || ind1.getKozaFitness() == ind2.getKozaFitness() && ind1.getTreeSize() < ind2.getTreeSize()) return ind1;
        return ind2;
    }

}
