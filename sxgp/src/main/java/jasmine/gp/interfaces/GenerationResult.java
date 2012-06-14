package jasmine.gp.interfaces;

import jasmine.gp.Individual;

/**
 * @author Olly Oechsle, University of Essex, Date: 26-Jan-2007
 * @version 1.0
 */
class GenerationResult {

    int generation;
    Individual individual;
    long time;
    
    public GenerationResult(int generation, Individual individual, long time) {
        this.generation = generation;
        this.individual = individual;
        this.time = time;     
    }

    public static String getCSVHeader() {
        return "Time, Generation, Fitness, Hits, Size";
    }

    public String toCSV() {
        return time + ", " + generation + ", " + individual.getKozaFitness() + ", " + individual.getHits() + ", " + individual.getTreeSize();
    }

}
