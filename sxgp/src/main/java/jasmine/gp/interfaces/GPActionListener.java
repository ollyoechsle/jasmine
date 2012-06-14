package jasmine.gp.interfaces;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.Problem;

import java.io.IOException;

/**
 * Extend this class to make your own custom listener.
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public abstract class GPActionListener {

    protected int totalIndividualsEvaluated;
    protected boolean isIdeal;
    protected Individual bestIndividual;

    public GPActionListener() {
        totalIndividualsEvaluated = 0;
        isIdeal = false;
        bestIndividual = null;
    }

    public boolean finished = false;

    public abstract void fatal(String message);
    public abstract void message(String message);

    public abstract void onStartEvolution(Evolve e, Problem p);

    public void incrementIndividualEvaluations(int numEvaluations) {
        totalIndividualsEvaluated+=numEvaluations;
    }

    public abstract int getGeneration();

    public abstract GPActionListener copy() throws IOException;

    /**
     * Called just as a generation is starting
     */
    public abstract void onGenerationStart(int generation);

    /**
     * Called once after a generation has completed.
     */
    public abstract void onGenerationEnd(int generation);

    /**
     * Called once the GP has finished.
     */
    public abstract void onStopped();
    

    public void setBestIndividual(Individual individual) {
        bestIndividual = individual;
    }

    public int getTotalIndividualsEvaluated() {
        return totalIndividualsEvaluated;
    }

    public void setIdeal(boolean isIdeal) {
        this.isIdeal = isIdeal;
    }

    public boolean bestIndividualWasIdeal() {
        return isIdeal;
    }

    public Individual getBestIndividual() {
        return bestIndividual;
    }

    public abstract void dispose();

    /**
     * Called once the evolution process has completed.
     */
    public abstract void onEndEvolution(int generation, GPParams params);

}
