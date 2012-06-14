package jasmine.gp.interfaces;


import jasmine.gp.Evolve;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;
import jasmine.imaging.commons.FastStatistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Sends output from the GP system to the standard output.
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public class ConsoleListener extends GPActionListener {

    public static class ConsoleSettings {

        public boolean showEvaluations, showGenerationSummary, showJava, showMessages, showPopulationSize;

        public ConsoleSettings(boolean showEvaluations, boolean showGenerationSummary, boolean showJava, boolean showMessages, boolean showPopulationSize) {
            this.showEvaluations = showEvaluations;
            this.showGenerationSummary = showGenerationSummary;
            this.showJava = showJava;
            this.showMessages = showMessages;
            this.showPopulationSize = showPopulationSize;
        }

    }

    public static final ConsoleSettings SHOW_POP_STATISTICS = new ConsoleSettings(false, true, false, true, true) ;
    public static final ConsoleSettings HIGH_VERBOSITY = new ConsoleSettings(true, true, true, true, true) ;
    public static final ConsoleSettings LOW_VERBOSITY = new ConsoleSettings(false, true, false, true, true);
    public static final ConsoleSettings SILENT = new ConsoleSettings(false, false, false, false, false);

    public static final String DOT = ".";

    private int counter = 0;
    private long startTime;
    public ConsoleSettings verbosity;
    private long totalTime = 0;

    private long nextPrintTime = -1;
    private long printDelay = 10000;

    private boolean describeIndividuals = true;

    File outFile = null;
    BufferedWriter out;

    protected Evolve e;

    public ConsoleListener() {
        this(LOW_VERBOSITY);
    }

    public ConsoleListener(ConsoleSettings verbosity) {
        this.verbosity = verbosity;
    }

    public ConsoleListener(ConsoleSettings verbosity, File out) throws IOException {
        this(verbosity);
        if (out != null) {
            this.outFile = out;
            this.out = new BufferedWriter(new FileWriter(out));
        }
    }

    public void dispose() {
        try { if (out != null) out.close(); } catch (IOException e) {}
        System.out.println("// Disposing.");
    }


    public GPActionListener copy() throws IOException {
        return new ConsoleListener(verbosity, outFile);
    }

    Problem p;

    public void onStartEvolution(Evolve e, Problem p) {   	
        finished = false;
        this.e = e;
        this.p = p;
        if (verbosity.showMessages) {
            System.out.println("// " + Evolve.APP_NAME);
            System.out.println("// by Olly Oechsle");
            System.out.println("// Problem: " + p.getName());
        }
        startTime = System.currentTimeMillis();
        nextPrintTime = startTime + printDelay;
    }

    public void onGenerationStart(int generation) {  	
        // do nothing
    }

    public void incrementIndividualEvaluations(int evaluations) {
        super.incrementIndividualEvaluations(evaluations);
        if (verbosity.showEvaluations) {
            for (int i = 0; i < evaluations; i++) {
                System.out.print(DOT);
            }
            counter++;
            if (counter % 50 == 0) System.out.println();
        }
        if (out != null && nextPrintTime != -1 && System.currentTimeMillis() > nextPrintTime) {
            //System.out.print(bestIndividual.getKozaFitness() + ",");
            //p.describe(this, bestIndividual, new DataStack(), 0);
            //System.out.println(bestIndividual.getHits());
            try {
                out.write(bestIndividual.getHits() + "\n");
            } catch (Exception e) {
                System.err.println(e);
            }
            nextPrintTime += printDelay;
        }
    }

    public void onStopped() {   	
        finished = true;
        try { if (out != null) out.close(); } catch (IOException e) {}
        totalTime = System.currentTimeMillis() - startTime;
        if (verbosity.showMessages) {
            //System.out.println(OperationCounter.getResults());
            System.out.println("// Genetic Programming halted by the user");
            System.out.println("// " + getTotalIndividualsEvaluated() + " individuals evaluated.");
        }
    }

    DecimalFormat f = new DecimalFormat("0.000");

    public void onGenerationEnd(int generation) {
        if (describeIndividuals) p.describe(this, bestIndividual, new DataStack(), 0);
        if (verbosity.showGenerationSummary) {

            if (generation == 0) {
                System.out.print("\n// Gen, Fitness, Alt Fitness, Hits, Errors, Size, Av. Size, Time");
                if (verbosity.showPopulationSize) {
                    System.out.print(", avg(psize), stdev(psize), avg(pdepth), stdev(pdepth), redundancy");
                }
                System.out.println();
            }

            if (bestIndividual != null)  {
                long time = System.currentTimeMillis() - startTime;
                System.out.print("// " + generation + ", " + f.format(bestIndividual.getKozaFitness()) + ", " + f.format(bestIndividual.getAlternativeFitness()) + ", " + bestIndividual.getHits() + ", " + bestIndividual.getMistakes() + ", " + bestIndividual.getTreeSize() + ", " + time);
                if (e != null && verbosity.showPopulationSize) {
                    FastStatistics f = e.getPopulationSizeStatistics();
                    System.out.print(", " + f.getMean() + ", " + f.getStandardDeviation());
                    f = e.getPopulationDepthStatistics();
                    System.out.print(", " + f.getMean() + ", " + f.getStandardDeviation());
                    System.out.print(", " + e.getRedundancy());
                }
                System.out.println();
            }

        }
        if (verbosity.showJava) {
            //TreeOptimiser.optimise(bestIndividual, params);
            if (bestIndividual != null) {
                System.out.println(bestIndividual.toJava(p.getMethodSignature(bestIndividual)));
            }
        }        
        counter = 0;
        //if (((System.currentTimeMillis() - startTime) / 1000) >= 100) System.exit(0);
        lastGeneration = generation;
    }

    int lastGeneration = 0;

    public void onEndEvolution(int generation, GPParams params) {  	
        finished = true;
        try { if (out != null) out.close(); } catch (IOException e) {}
        if (isIdeal)  {
            if (verbosity.showMessages) System.out.println("// Found ideal individual");
            if (verbosity.showJava) System.out.println(bestIndividual.toString());
        }
        totalTime = System.currentTimeMillis() - startTime;
        if (verbosity == HIGH_VERBOSITY) {
            //TreeOptimiser.optimise(bestIndividual, params);
            System.out.println(bestIndividual.toJava());
        }

        if (e != null && verbosity.showPopulationSize) {
            FastStatistics f = e.getPopulationSizeStatistics();
            System.out.println("// Population size mean, std dev");
            System.out.println("//" + f.getMean() + ", " + f.getStandardDeviation() + ", " + e.getRedundancy());
        }

        if (verbosity.showMessages) {
            System.out.println("// Finished Evolution. " + (totalTime/1000) + "secs. " + generation + " generations.");
            System.out.println("// " + getTotalIndividualsEvaluated() + " individuals evaluated.");
        }
        lastGeneration = generation;
    }

    /**
     * Gets the last generation executed.
     * @return
     */
    public int getGeneration() {
        return lastGeneration;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void fatal(String message) {
        System.err.println("// " + message);
    }

    public void message(String message) {
        if (verbosity != SILENT) System.out.println("// " + message);
    }

}
