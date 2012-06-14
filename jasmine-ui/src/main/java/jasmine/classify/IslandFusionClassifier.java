package jasmine.classify;


import jasmine.classify.classifier.Classifier;
import jasmine.classify.classifier.ClassifierFusion;
import jasmine.classify.classifier.GPClassifier;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataSet;
import jasmine.classify.data.DataStatistics;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.ConsoleListener;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;
import jasmine.imaging.commons.FastStatistics;

import java.io.IOException;
import java.io.File;
import java.util.Vector;

/**
 * Which is better: regular fitness, or fitness with difficulty
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Oct-2008
 * @version 1.0
 */
public class IslandFusionClassifier {

    public static int gens = 5;

//    public static void main(String[] args) throws IOException {
//
//        IslandFusionClassifier a = new IslandFusionClassifier();
//
//        Classifier c1 = a.getClassifier(DataSets.satimage());
//        //c1.save(new File("c:\\PhD\\datasets\\flags\\flagsegmenter.gp"));
//
//        System.out.println(c1.toJava("public int", "segment", "PixelLoader image, int x, int y"));
//
//        //Classifier c2 = a.getClassifier(DataSets.flags());
//        //c2.save(new File("c:\\PhD\\datasets\\flags\\flagclassifier.gp"));
//
//
//    }

    protected Classifier c;

    public Classifier getClassifier(DataSet dataset) throws IOException {
        dataset.init();
        runGP(dataset, 7);
        dataset.close();
        return c;
    }

    public void runGP(DataSet dataset, int islandCount) {

        int folds = dataset.getFolds();
        int testSize = 0;

        // get the average fitness
        float averageTrainingFitness = 0;
        int avCount = 0;
        float testingHits = 0;

        FastStatistics time = new FastStatistics();
        FastStatistics size = new FastStatistics();

        for (int f = 0; f < folds; f++) {

            int seed = 8975 + f;

            Vector<Data> trainingData = dataset.getTrainingData(f);
            Vector<Data> testData = dataset.getTestingData(f);

            MaxMarginClassifierIslandFusion.DRS_TYPE = BetterDRS.TYPE;
            MaxMarginClassifierIslandFusion.FILL_IN = true;
            MaxMarginClassifierIslandFusion.GENS = gens;
            MaxMarginClassifierIslandFusion.ISLAND_COUNT = islandCount;
            MaxMarginClassifierIslandFusion.SAVE_PCM = false;

            Problem pr = new MaxMarginClassifierIslandFusion(trainingData);
            Evolve e = new Evolve(pr, new ConsoleListener(ConsoleListener.LOW_VERBOSITY));
            e.setSeed(seed);
            e.run();

            time.addData(e.getTotalTime());

            // get the best individual(s)
            Individual[] bestIndividuals = e.getBestIndividuals();

            for (int j = 0; j < bestIndividuals.length; j++) {
                // ensure the individual has a PCM
                Individual bestIndividual = bestIndividuals[j];
                MaxMarginClassifierIslandFusion.SAVE_PCM = true;
                pr.evaluate(bestIndividual, new DataStack(), e);
            }

            c = generateClassifier(trainingData, testData, dataset.getTrainingStatistics().getClassIDs(), bestIndividuals, null);

            int[] hits = c.getHits(testData);
            // TP
            testingHits += hits[0];
            // TN
            testingHits += hits[2];
            testSize += testData.size();

            // since the fitness may be a little strange, recalculate it using the standard method
            averageTrainingFitness += c.getKozaFitness(trainingData);
            avCount++;

        }

        averageTrainingFitness /= avCount;
        float testFitnesss = 1 - (testingHits / testSize);

        System.out.println(0 + ", " + averageTrainingFitness + ", " + testFitnesss + ", " + time.getMean());

        c.setMapping(DataStatistics.classIDMapping);


    }

    public static Classifier generateClassifier(Vector<Data> trainingData, Vector<Data> testData, Vector<Integer> classIDs, Individual[] bestIndividuals, EnsembleListener listener) {
        Classifier c = null;

        if (bestIndividuals.length == 1) {
            // just one individual (no islands)
            // record other statistics
            //time.addData(e.getTotalTime());

            c = new GPClassifier(bestIndividuals[0]);

        } else {

            ClassifierFusion.listener = listener;

            double lowestFitness = Double.MAX_VALUE;
            ClassifierFusion fuser = new ClassifierFusion(classIDs);
            for (int j = 0; j < bestIndividuals.length; j++) {
                Individual bestIndividual = bestIndividuals[j];
                GPClassifier gpc = new GPClassifier(bestIndividual);

                double fitness = gpc.getKozaFitness(trainingData);
                if (fitness < lowestFitness) {
                    lowestFitness = fitness;
                    c = gpc;
                    if (listener != null) {
                        listener.onBetterFitness(lowestFitness, c);
                    }
                }

                fuser.add(gpc);
            }

            // majority classification, using three classifiers
            fuser.setMode(ClassifierFusion.MAJORITY_VOTE);
            for (int committeeSize = 3; committeeSize < bestIndividuals.length; committeeSize++) {
                fuser.tryHits(trainingData, testData, committeeSize);

                ClassifierFusion majorityClassifier = new ClassifierFusion(classIDs, fuser.bestClassifiers, ClassifierFusion.MAJORITY_VOTE);

                double majorityFitness = majorityClassifier.getKozaFitness(trainingData);

                if (majorityFitness < lowestFitness) {
                    c = majorityClassifier;
                    lowestFitness = majorityFitness;
                    if (listener != null) {
                        listener.onBetterFitness(lowestFitness, c);
                    }
                }
            }


            // committe classification, using three classifiers
            fuser.setMode(ClassifierFusion.COMMITTEE_VOTE);
            for (int committeeSize = 3; committeeSize < bestIndividuals.length; committeeSize++) {
                //System.out.println("Trying hits for committee size: " + committeeSize);
                fuser.tryHits(trainingData, testData, committeeSize);
                ClassifierFusion committeeClassifier = new ClassifierFusion(classIDs, fuser.bestClassifiers, ClassifierFusion.COMMITTEE_VOTE);
                double committeeFitness = committeeClassifier.getKozaFitness(trainingData);

                if (committeeFitness < lowestFitness) {
                    c = committeeClassifier;
                    lowestFitness = committeeFitness;
                    if (listener != null) {
                        listener.onBetterFitness(lowestFitness, c);
                    }
                }
            }


        }

        return c;
    }

}