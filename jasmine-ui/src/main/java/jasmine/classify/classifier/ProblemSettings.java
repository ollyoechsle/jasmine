package jasmine.classify.classifier;


import jasmine.gp.Evolve;
import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.params.GPParams;
import jasmine.imaging.commons.util.DeepCopy;

import java.io.Serializable;
import java.util.Vector;

/**
 * Defines a set of problem settings
 * @author Olly Oechsle, University of Essex, Date: 08-Aug-2008
 * @version 1.0
 */
public class ProblemSettings implements Serializable {

    public int maxTime = -1;
    public int seed;
    public int tournamentSize = 5;
    public int generationGapMethod;
    public int DRSMethod = BetterDRS.TYPE;

    public int eliteCount = 5;
    public int generations = 50;
    public int population = 500;
    public int slotCount = 50;
    public int numIslands = 1;

    public boolean treeChecking = false;

    public ProblemSettings() {

    }

    public ProblemSettings(int maxTime) {
        this.maxTime = maxTime;
    }

    public ProblemSettings(int maxTime, int seed, int tournamentSize) {
        this.maxTime = maxTime;
        this.seed = seed;
        this.tournamentSize = tournamentSize;
        this.generationGapMethod = GPParams.GENERATION_GAP_OVERLAP_OFF;
    }

    public ProblemSettings(int maxTime, int seed, int tournamentSize, int generationGapMethod) {
        this.maxTime = maxTime;
        this.seed = seed;
        this.tournamentSize = tournamentSize;
        this.generationGapMethod = generationGapMethod;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public void apply(Evolve e) {
       e.setSeed(seed);
    }
    
    public void apply(GPParams params) {
        //System.out.println("Setting max time to: " + maxTime);
        params.setMaxTime(maxTime);
        params.setGenerations(generations);
        params.setGenerationGapMethod(generationGapMethod);
        params.setPopulationSize(population);
        params.setEliteCount(eliteCount);
        params.setTournamentSize(tournamentSize);
        params.setTreeCheckingEnabled(treeChecking);
        params.setIslandCount(numIslands);
        params.setMaxTreeDepth(8);
    }
    
    public void registerStatistics(Vector<ParameterStatistics> stats, float result) {
        //stats.elementAt(0).addStatistic(tournamentSize, result);
        stats.elementAt(0).addStatistic(DRSMethod, result);
        stats.elementAt(1).addStatistic(generationGapMethod, result);
    }

    public static Vector<ParameterStatistics> getStatistics() {
        Vector<ParameterStatistics> stats = new Vector<ParameterStatistics>();
        
/*        ParameterStatistics t = new ParameterStatistics("Tournament Size") {
            public void applyToProblem(ProblemSettings s, int value) {
                s.tournamentSize = value;
            }
        };


        t.addValue(2);
        t.addValue(7);
        stats.add(t);*/

        ParameterStatistics drs = new ParameterStatistics("DRS Method") {
            public void applyToProblem(ProblemSettings s, int value) {
                s.DRSMethod = value;
            }
        };

        drs.addValue(BasicDRS.TYPE);
        drs.addValue(BetterDRS.TYPE);
        stats.add(drs);
        
        ParameterStatistics g = new ParameterStatistics("Generation Gap Method") {
            public void applyToProblem(ProblemSettings s, int value) {
                s.generationGapMethod = value;
            }
        };

        g.addValue(GPParams.GENERATION_GAP_OVERLAP_OFF);
        g.addValue(GPParams.GENERATION_GAP_OVERLAP_ON);
        stats.add(g);
        
        return stats;
    }

    public static Vector<ProblemSettings> getProblemSettings(int maxTime) {

        Vector<ParameterStatistics> statistics = getStatistics();

        // calculate how many problem settings there will be
        int total = 1;
        for (int i = 0; i < statistics.size(); i++) {
            ParameterStatistics parameterStatistic =  statistics.elementAt(i);
            if (i < statistics.size() - 1) {
                parameterStatistic.next = statistics.elementAt(i+1);
            }
            total *= parameterStatistic.values.size();
        }

        Vector<ProblemSettings> settings = new Vector<ProblemSettings>(total);

        getProblemSettings(statistics.elementAt(0), new ProblemSettings(maxTime), settings);

        return settings;
        
    }

    private static void getProblemSettings(ParameterStatistics s, ProblemSettings problem, Vector<ProblemSettings> settings) {

        for (int i = 0; i < s.values.size(); i++) {
            int parameterValue = s.values.elementAt(i);
            ProblemSettings copy = problem.copy();
            s.applyToProblem(copy, parameterValue);
            if (s.next == null) {
                settings.add(copy);
            } else {
                getProblemSettings(s.next, copy, settings);
            }
        }

    }

    public String toString() {
        return "t=" + tournamentSize;
    }

    public ProblemSettings copy() {
        return (ProblemSettings) new DeepCopy().copy(this);
    }

}
