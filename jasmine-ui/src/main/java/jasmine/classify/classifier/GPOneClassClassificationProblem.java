package jasmine.classify.classifier;




import jasmine.classify.data.Data;
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
import jasmine.gp.nodes.logic.AND_FP;
import jasmine.gp.nodes.logic.Less_FP;
import jasmine.gp.nodes.logic.More_FP;
import jasmine.gp.nodes.logic.OR_FP;
import jasmine.gp.nodes.math.*;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;

import java.util.Vector;

/**
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version,
 * provided that any use properly credits the author.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details at http://www.gnu.org
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class GPOneClassClassificationProblem extends Problem {

    public static boolean SAVE_PCM = true;

    protected Vector<Data> trainingData;

    protected ProblemSettings p;

    protected int classID;

    public GPOneClassClassificationProblem(int classID, ProblemSettings p, Vector<Data> trainingData) {
        this.classID = classID;
        this.p = p;
        this.trainingData = trainingData;
    }

    public ProblemSettings getProblemSettings() {
        return p;
    }

    public String getName() {
        return "Classification " + p.toString();
    }

    public void initialise(Evolve e, GPParams params) {

        p.apply(e);

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

        EuclideanERC.trainingData = trainingData;
        for (int i = 0; i < 25; i++) {
            //params.registerNode(new EuclideanERC());
        }

    }

    public void customiseParameters(GPParams params) {    	
        p.apply(params);
        params.setMinTreeDepth(1);
        params.setMaxTreeDepth(8);
        params.setCutoffDepth(8);
    }

    /*
     * Builds the dynamic classification map
     */
    public PCM buildProgramClassificationMap(DataStack data, Individual ind) {

        PCM pcm = null;

        if (p != null) {
            switch (p.DRSMethod) {
                case BasicDRS.TYPE:
                    if (p.slotCount < 0) {
                        pcm = new BasicDRS();
                    } else {
                        pcm = new BasicDRS(p.slotCount);
                    }
                    break;
                case BetterDRS.TYPE:	//POEY comment: if the number of classes > 2
                    if (p.slotCount < 0) {
                        pcm = new BetterDRS();
                    } else {
                        pcm = new BetterDRS(p.slotCount);	//POEY comment: the number of slot = 51
                    }
                    break;
                case VarianceThreshold.TYPE:
                    pcm = new VarianceThreshold();
                    break;
                case EntropyThreshold.TYPE:		//POEY comment: if the number of classes <= 2
                    pcm = new EntropyThreshold();
                    break;
            }
        } else {
            pcm = new BetterDRS();
        }
        
        //POEY comment: trainingData.size() = the number of objects
        for (int i = 0; i < trainingData.size(); i++) {

            // get the data
            Data d = trainingData.elementAt(i);

            // don't evaluate everything
            if (d.weight == 0) continue;

            // hook that allows us to ignore some of the training data.
            //if (shouldIgnore(d)) continue;

            // calculate what classID this should have
            int classID = getClassID(d);//d.classID;

            // put the data onto the stack
            DataValueTerminal.currentValues = d.values;

            // run the individual
            //POEY comment: calculate the object's value by classification functions
            double result = ind.execute(data);

            // don't bother executing individuals which don't use the imaging functions.
            if (!data.usesImaging) {
                return null;
            }

            // add the result to the pcm
            pcm.addResult(result, classID);

        }

        //POEY comment: jasmine.gp.multiclass
        pcm.calculateThresholds();

        if (pcm instanceof BetterDRS) {
            ((BetterDRS) pcm).fillInSlots();
        }

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

    public void evaluate(Individual ind, DataStack data, Evolve e) {
    	    	
        // execute the individual and get a program classification map
        PCM pcm = buildProgramClassificationMap(data, ind);

        if (pcm == null) {
            // this means that the individual doesn't use imaging functions. Assign it worst fitness
        	//POEY comment: such as numeric calculation which doesn't have any functions
            ind.setWorstFitness();
        } else {
            float TP = 0;
            float TN = 0;
            float FP = 0;
            float FN = 0;
            float N = 0;
            int hits = 0;
            int mistakes = 0;

            // when evaluating fitness, don't bother evaluating the individual again, use cached results
            Vector<CachedOutput> cachedOutput = pcm.getCachedResults();
            //POEY comment: cachedOutput.size()= the number of objects
            for (int i = 0; i < cachedOutput.size(); i++) {

                CachedOutput output = cachedOutput.elementAt(i);

                //POEY comment: every object's weight = 1
                N += output.weight;

                // get the individual's answer for this data
                int outputClass = pcm.getClassFromOutput(output.rawOutput);

                if (classID != -1) {
                    if (outputClass == 0) {
                        // returned false
                        if (output.expectedClass == 0) {
                            TN += output.weight;
                            hits++;
                        } else {
                            mistakes++;
                            FN += output.weight;
                        }
                    } else {
                        if (output.expectedClass == 0) {
                            mistakes++;
                            FP += output.weight;
                        } else {
                            hits++;
                            TP += output.weight;
                        }
                    }
                }  else {
                    if (outputClass == output.expectedClass) {
                        TP+= output.weight;
                    } else {
                        FP+= output.weight;
                    }
                }

            }

            // compute the fitness
            double fitness = (FP + FN) / (double) N;

            // give equal weight to both classes
            //fitness = (FP / (FP + TN)) + (FN / (FN + TP));

            ind.setKozaFitness(fitness);
            ind.setHits(hits);
            ind.setMistakes(mistakes);
            ind.setAlternativeFitness(FN);

            // remember the PCM, but clear the cached results to save memory
            pcm.clearCachedResults();
            if (SAVE_PCM) {
                ind.setPCM(pcm);
            }

        }

    }
    
    public void ensureHasPCM(Individual ind, Evolve e) {
        boolean oldValue = SAVE_PCM;
        SAVE_PCM = true;
        evaluate(ind, new DataStack(), e);
        SAVE_PCM = oldValue;
    }


}
