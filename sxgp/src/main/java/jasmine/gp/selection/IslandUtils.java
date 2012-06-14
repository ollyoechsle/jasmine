package jasmine.gp.selection;

import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;

/**
 * Assigns individuals to an island
 */
public class IslandUtils {

    /**
     * Assigns individuals to islands in round robin style.
     */
    public static void assign(Individual[] population, GPParams params) {

        int islandID = 0;

        // allocate individuals to islands fairly.
        for (int i = 0; i < population.length; i++) {
            Individual individual = population[i];
            individual.setIslandID(islandID);
            islandID++;
            if (islandID >= params.getIslandCount()) islandID = 0;
        }

    }

    public static Individual[] getBestIndividuals(Evolve e) {
        return getBestIndividuals(e.getPopulation(), e.getParams());
    }

    /**
     * Gets the best individuals from each island in the population
     */
    public static Individual[] getBestIndividuals(Individual[] population, GPParams params) {

        Individual[] individuals = new Individual[params.getIslandCount()];

        for (int i = 0; i < individuals.length; i++) {
            Individual best = null;
            int count = 0;

            for (int j = 0; j < population.length; j++) {
                Individual individual = population[j];
                if (individual.getIslandID() == i || params.getIslandCount() == 1) {
                    count++;
                    if (best == null || individual.getKozaFitness() < best.getKozaFitness() || ((individual.getKozaFitness() == best.getKozaFitness()) && (individual.getTreeSize() < best.getTreeSize()))) {
                        best = individual;
                    }
                }
            }

            //System.out.println("There are " + count + " individuals with islandID " + i);

            individuals[i] = best;

        }

        return individuals;

    }

}
