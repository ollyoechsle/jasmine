package jasmine.classify;


import jasmine.classify.classifier.FeatureERC;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataPartitioner;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.CachedOutput;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.PCM;
import jasmine.gp.multiclass.VarianceThreshold;
import jasmine.gp.nodes.*;
import jasmine.gp.nodes.ercs.CustomRangeERC;
import jasmine.gp.nodes.ercs.PercentageERC;
import jasmine.gp.nodes.logic.*;
import jasmine.gp.nodes.math.*;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;

import java.util.Vector;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Visual representation of the margins.
 * Works for a two class problem only.
 */
public class MaxMarginClassifierIslandFusion extends Problem {

    public static int GENS = 50;

    protected Vector<Data> trainingData;

    public static boolean FILL_IN = true;

    public static int SLOT_COUNT = 50;

    public static boolean AVOID_UNNECESSARY_EVALS = true;

    public static boolean SAVE_PCM = true;

    public static int TOURNAMENT_SIZE = -1;

    public static int MAX_TIME = -1;

    public static int DRS_TYPE = BetterDRS.TYPE;

    public static int ISLAND_COUNT = 1;

    public static int classID = -1;

    public static double TERMINAL_PROBABILITY = -1;

    public MaxMarginClassifierIslandFusion(Vector<Data> trainingData) {
        this.trainingData = trainingData;
    }

    public String getName() {
        return "Island Fusion Problem";
    }

    public void initialise(Evolve e, GPParams params) {

        // Register the functions
/*        params.registerNode(new Add());
        params.registerNode(new Mul());
        params.registerNode(new Sub());
        params.registerNode(new Div());

        params.registerNode(new Mean());
        params.registerNode(new PercentDiff());

        params.registerNode(new Ln());
        params.registerNode(new Squared());
        params.registerNode(new Cubed());
        params.registerNode(new Sin());

        params.registerNode(new Max());
        params.registerNode(new Min());

        params.registerNode(new PercentageERC());
        params.registerNode(new CustomRangeERC(0,255));
        params.registerNode(new CustomRangeERC(0,25));

        // register terminals to have access to the data
        int numColumns = trainingData.elementAt(0).getColumnCount();
        for (int i = 0; i < numColumns; i++) {
            params.registerNode(new DataValueTerminal(i));
        }*/

        // Register the functions
        params.registerNode(new Add());
        params.registerNode(new Mul());
        params.registerNode(new Sub());
        params.registerNode(new Div());

        params.registerNode(new Mean());
        params.registerNode(new PercentDiff());

        params.registerNode(new Ln());
        params.registerNode(new Squared());
        params.registerNode(new Cubed());
        params.registerNode(new Sqrt());
        params.registerNode(new Exp());
        params.registerNode(new Hypot());

        params.registerNode(new Sin());
        params.registerNode(new Cos());
        params.registerNode(new Tan());

        params.registerNode(new Max());
        params.registerNode(new Min());

        params.registerNode(new More_FP());
        params.registerNode(new Less_FP());
        params.registerNode(new AND_FP());
        params.registerNode(new OR_FP());

        params.registerNode(new PercentageERC());
        params.registerNode(new CustomRangeERC(0, 255));
        params.registerNode(new CustomRangeERC(0, 25));

        // register terminals to have access to the data
        int numColumns = trainingData.elementAt(0).getColumnCount();
        for (int i = 0; i < numColumns; i++) {
            params.registerNode(new DataValueTerminal(i));
        }

  /*      EuclideanERC.trainingData = trainingData;
        for (int i = 0; i < 10; i++) {
            params.registerNode(new EuclideanERC());
        }*/

    }

    /*
    * Builds the dynamic classification map
    */
    private PCM buildProgramClassificationMap(DataStack data, Individual ind) {

        PCM pcm = null;

        switch (DRS_TYPE) {
            case BetterDRS.TYPE:
                pcm = new BetterDRS(SLOT_COUNT);
                break;
            case BasicDRS.TYPE:
                pcm = new BasicDRS(SLOT_COUNT);
                break;
            case VarianceThreshold.TYPE:
                pcm = new VarianceThreshold();
                break;
            case EntropyThreshold.TYPE:
                pcm = new EntropyThreshold();
                break;
        }

        for (int i = 0; i < trainingData.size(); i++) {

            // get the data
            Data d = trainingData.elementAt(i);

            // don't evaluate everything
            if (d.weight == 0) continue;

            // avoid validation data
            if (d.type == DataPartitioner.VALIDATION) continue;

            // put the data onto the stack
            DataValueTerminal.currentValues = d.values;
            FeatureERC.values = d.values;

            // calculate what classID this should have
            int classID = getClassID(d);

            // run the individual
            double result = ind.execute(data);

            // don't bother executing individuals which don't use the imaging functions.
            if (!data.usesImaging) {
                return null;
            }

            // add the result to the pcm
            pcm.addResult(result, classID);

        }

        pcm.calculateThresholds();

        return pcm;

    }

    private int getClassID(Data d) {
        if (classID == -1) return d.classID;
        if (d.classID == classID) {
            return classID;
        } else {
            return 0;
        }
    }

    public void customiseParameters(GPParams params) {
        // do nothing
        //params.setMinTreeDepth(1);
        //params.setMaxTreeDepth(3);
        if (TOURNAMENT_SIZE > -1) {
            params.setTournamentSize(TOURNAMENT_SIZE);
        }
        params.setMaxTime(MAX_TIME);
        params.setAvoidUnnecessaryEvaluations(AVOID_UNNECESSARY_EVALS);
        params.setGenerations(GENS);
        params.setCutoffSize(100);
        params.setTreeCheckingEnabled(false);
        params.setIslandCount(ISLAND_COUNT);
        params.setTerminalProbability(TERMINAL_PROBABILITY);
    }

    int nullIndividuals = 0;

    public void evaluate(Individual ind, DataStack data, Evolve e) {
 
        PCM pcm = buildProgramClassificationMap(data, ind);

        if (pcm == null) {
            ind.setWorstFitness();
            return;
        }

        if (FILL_IN && pcm instanceof BetterDRS) {
            ((BetterDRS) pcm).fillInSlots();
        }

        int hits = 0;
        float mistakes = 0;

        Vector<CachedOutput> cachedOutput = pcm.getCachedResults();

        for (int i = 0; i < cachedOutput.size(); i++) {

            CachedOutput output = cachedOutput.elementAt(i);

            int classID = pcm.getClassFromOutput(output.rawOutput);

            if (classID > -1) {
                if (output.expectedClass == classID) {
                    hits++;
                } else {
                    mistakes++;
                }
            } else {
                // the point falls into no man's land
                // standard penalty
                mistakes += 0.5;
            }

        }

        ind.setKozaFitness(mistakes);
        ind.setHits(hits);

        if (mistakes == 0) {
            e.stopFlag = true;
        }

        if (SAVE_PCM) ind.setPCM(pcm);

    }

    public void ensureHasPCM(Individual ind, Evolve e) {
        boolean oldValue = SAVE_PCM;
        SAVE_PCM = true;
        evaluate(ind, new DataStack(), e);
        SAVE_PCM = oldValue;
    }

}