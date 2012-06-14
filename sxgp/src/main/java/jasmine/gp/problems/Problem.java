package jasmine.gp.problems;

import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.util.JavaWriter;

import java.util.Vector;
import java.util.Hashtable;
import java.io.*;

/**
 * <p>
 * Basic Problem Class upon which all GP problems are based.
 * The two most important methods are the initialise() and evaluate() methods
 * where the training data is loaded, and individuals fitness assessed respectively.
 * </p>
 * <p/>
 * <p>
 * Any class implementing these methods can be plugged straight into the Evolve class which runs
 * the GP simulation on the problem.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public abstract class Problem implements Serializable {

    /**
     * The kind of output that the describe method of this problem
     * produces. The graphical listener checks this so it can display
     * the output appropriately.
     */
    protected int outputType;

    /**
     * Provides a name for the problem so that it can be identified via the
     * user interface.
     */
    public abstract String getName();

    /**
     * Initialises the problem. This is where the training data is loaded
     * and the GP params object initialised with Nodes to use. The return
     * object should also be set up.
     */
    public abstract void initialise(Evolve e, GPParams params);

    /**
     * Where the problem can customise its GP parameters.
     */
    public abstract void customiseParameters(GPParams params);

    /**
     * Cache the evaluations of individuals to increase the learning time
     * if there are some individuals in the population the same
     */
    protected Hashtable<Integer, Individual> fitnessCache;

    public void clearFitnessCache() {
        if (fitnessCache != null) {
            fitnessCache.clear();
            evaluationsAvoided = 0;
        }
    }

    public int evaluationsAvoided = 0;
    private int evals;
    private int evaluationCount = 0;

    /**
     * Evaluates all the individuals in the population. You don't need to worry about this
     * unless you want to add a different evaluation mechanism (see the Art problem for an example)
     */
    public int evaluateIndividuals(Individual[] population, Evolve e, GPParams params, GPActionListener gpinterface) {

        if (params.avoidUnnecessaryEvaluations()) {
            if (fitnessCache == null) {
                fitnessCache = new Hashtable<Integer, Individual>();
            }
        }

        evals = 0;
        // execute individuals
        evaluationCount = 0;
        
        for (Individual individual : population) {
            evaluateIndividual(individual, e, params, gpinterface);
        }

        // don't update the interface all the time
        //POEY comment: evals is the number of population,
        //and it is showed (refreshed) on the (method) - Genetic Programming window in the bottom area, in field Count: 
        if (evals > 50) {
            gpinterface.incrementIndividualEvaluations(evals);
            evals = 0;
        }

        return evaluationCount;
    }

    public void evaluateIndividual(Individual individual, Evolve e, GPParams params, GPActionListener gpinterface) {
        // don't evaluate an individual unnecessarily.
        if (individual.hasBeenEvaluated()) return;

        evals++;

        // don't update the interface all the time
        //POEY comment: evals is the number of population,
        //and it is showed (refreshed) on the (method) - Genetic Programming window in the bottom area, in field Count:
        if (evals > 50) {
            gpinterface.incrementIndividualEvaluations(evals);
            evals = 0;
        }

        int size = individual.getTreeSize();

        if (params.getCutoffSize() > -1 && size > params.getCutoffSize()) {
            individual.setWorstFitness();
        } else {

            if (params.getCutoffDepth() > -1 && individual.getTreeDepth() > params.getCutoffDepth()) {
                individual.setWorstFitness();
            } else {

                if (params.avoidUnnecessaryEvaluations()) {		//POEY comment: not this case
                    int hashcode = individual.toLisp().hashCode();
                    Individual equivalent = fitnessCache.get(hashcode);
                    if (equivalent != null) {
                        individual.setKozaFitness(equivalent.getKozaFitness());
                        individual.setHits(equivalent.getHits());
                        individual.setMistakes(equivalent.getMistakes());                      
                        individual.setAlternativeFitness(equivalent.getAlternativeFitness());
                        evaluationsAvoided++;
                    } else {

                        evaluate(individual, new DataStack(), e);
                        evaluationCount++;
                        fitnessCache.put(hashcode, individual);
                    }
                } else {
                    // evaluate all individuals
                	//POEY comment: for segmentation: jasmine.imaging.core.segmentation.JasmineSegmentationProblemDRSBetter.java
                	//for classification in function learnToClassify(): jasmine.classify.classifier.GPOneClassClassificationProblem.java
                    evaluate(individual, new DataStack(), e);
                    evaluationCount++;                    
                }

            }
        }

        individual.setHasBeenEvaluated(true);
    }

    /**
     * Evaluates a single individual, fitness should be assigned using the
     * setKozaFitness() method on the individual.
     */
    public abstract void evaluate(Individual ind, DataStack data, Evolve e);

    /**
     * Allows the Problem to return some kind of data after evaluating the best
     * individual of a generation. In imaging situations, this is usually an image
     * that can be displayed in the GUI to indicate how well the GP is progressing.
     * It is not necessary to implement this function.
     */
    public Object describe(GPActionListener listener, Individual ind, DataStack data, int index) {
        return null;
    }

    /**
     * Returns the expected method signature when used to convert the individual into java code.
     * Can be overridden with whatever you want.
     */
    public String getMethodSignature(Individual ind) {
        return "public " + JavaWriter.returnTypeToJava(ind.getReturnType()) + " eval()";
    }


    /**
     * A hook which allows the problem to execute additional logic just as it is finished,
     * or indeed start up a new Evolution Process.
     */
    public void onFinish(Individual bestIndividual, Evolve e) {
        // by default do nothing
    }

    /**
     * Allows the problem to choose the best individual. Most of the time this is the first
     * element in the sorted population but occasionally it is different.
     */
    public Individual getBestIndividual(Individual[] sortedPopulation) {
        return sortedPopulation[0];
    }

    /**
     * Hook in case you want to do something special each time a new generation commences.
     * By default this method does nothing.
     */
    public void onGenerationStart() {
     	
    }

    /**
     * Hook in case you want to do anything to a population when it is first created.
     * This could involve assigning niches to the population for island-like selection.
     * By default it does nothing.
     */
    public void processNewGeneration(Individual[] population) {
    }

    /**
     * Saves the problem to disk in serialised form.
     */
    public void save(File f) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a serialised problem from disk.
     */
    public static Problem load(File f) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream in = new ObjectInputStream(fis);
        Problem problem = (Problem) in.readObject();
        in.close();
        return problem;
    }

}
