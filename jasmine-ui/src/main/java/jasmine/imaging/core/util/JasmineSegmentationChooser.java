package jasmine.imaging.core.util;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.ConsoleListener;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;
import jasmine.gp.treebuilders.TreeBuilder;
import jasmine.imaging.commons.StatisticsSolver;

import java.text.DecimalFormat;
import java.util.Vector;

/**
 *
 * The aim is to generate a collection of individuals and evaluate them
 * using another problem. Then calculate their mean fitness and the variance thereof.
 *
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Feb-2008
 * @version 1.0
 */
public class JasmineSegmentationChooser {

    DecimalFormat f = new DecimalFormat("0.0000");

    double meanFitness = 0;

    public JasmineSegmentationChooser(Problem p, int runs) {

/*        StatisticsSolver mean = new StatisticsSolver();
        StatisticsSolver stdv = new StatisticsSolver();

        for (int i = 0; i < runs; i++) {
            double[] results = test(p);
            mean.addData(results[0]);
            stdv.addData(results[1]);
        }

        double fitness = (1 / mean.getMean()) / stdv.getMean();

        meanFitness = mean.getMean();

        //System.out.print(f.format(meanFitness) + ", " + f.format(stdv.getMean()) + ", ");
        //System.out.print(f.format(fitness));
        //System.out.println();*/

    }

    public double getMeanFitness() {
        return meanFitness;
    }

    private double[] test(Problem p) {

        Evolve e = new Evolve(p, new ConsoleListener());

        GPParams params = new GPParams();
        p.initialise(e, params);

        // create an intial population
        Individual[] population = new Individual[params.getPopulationSize() + 10];
        new TreeBuilder(params).generatePopulation(population, params, 0);

        // save results in the statistics solver
        StatisticsSolver solver = new StatisticsSolver();

        // evaluate them all
        for (int i = 0; i < population.length; i++) {
            Individual individual = population[i];
            p.evaluate(individual, new DataStack(), e);
            if (individual.getKozaFitness() < 2000) {
                solver.addData(individual.getKozaFitness());
            }
        }

        return new double[]{solver.getMean(), solver.getStandardDeviation()};

    }

}
