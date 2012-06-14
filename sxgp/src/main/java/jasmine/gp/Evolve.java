package jasmine.gp;


import jasmine.gp.interfaces.ConsoleListener;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.*;
import jasmine.gp.selection.HereditoryRepulsionSelector;
import jasmine.gp.selection.IslandUtils;
import jasmine.gp.selection.Selector;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeOptimiser;
import jasmine.gp.treebuilders.TreeBuilder;
import jasmine.gp.util.DeepCopy;
import jasmine.gp.util.FoundBestIndividualException;
import jasmine.imaging.commons.FastStatistics;

import java.util.*;

/**
 * <p/>
 * The main entry point to start Genetic Programming, contains the code for the main GP loop. Initialise it
 * by giving an instance of the problem you want to solve in the constructor. You may
 * also supply a GP params instance and listener if you don't want to use the defaults.
 * </p>
 * <p/>
 * <p/>
 * This class is a Thread so it can run in the background if you want. If you want to do
 * this then call the start() method. If you don't want it to run in parallel, call the
 * run() method to get the evolution process going
 * </p>
 * <p/>
 * <p/>
 * Evolution is dependent on the params in the GPParams object. If you don't supply your own
 * then the default parameters will be used (see GPParams.java). If you want to change params but
 * enjoy GUIs, then consider the GP start dialog (util.GPStartDialog)
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 0.1.06 Made the code easier to execute and read, standardised and improved the algorithms.
 */
public class Evolve extends Thread {

    public static final String APP_NAME = "sxGP v0.1.10";

//    public static void main(String[] args) {
//        System.out.println("This is " + APP_NAME);
//    }

    /**
     * The problem that has to be solved
     */
    protected Problem p;

    /**
     * The object which holds all the parameter values we need
     */
    protected GPParams params;

    /**
     * The action listener which displays results and feedback to the user
     */
    public GPActionListener gpinterface;

    /**
     * Should the program stop its execution prematurely?
     */
    public boolean stopFlag = false;

    /**
     * Has something terrible happened?
     */
    public boolean fatal = false;

    /**
     * Is the problem requesting a fresh (random) generation?
     */
    protected boolean requestFreshPopulation = false;

    /**
     * How long is this all taking?
     */
    protected long totalTime = -1;

    /**
     * What is the best individual we've found so far?
     */
    protected Individual bestIndividual;

    /**
     * How many individuals were evaluated?
     */
    protected int totalEvaluations = 0;

    // Copies individuals
    private DeepCopy copier;

    /**
     * Starts the evolve object with a standard GP params object
     * and the console listener.
     */
    public Evolve(Problem p) {
        this(p, new ConsoleListener(), null);
    }

    /**
     * Starts the evolve object with a standard GP params object and whatever
     * problem and listener you supply.
     */
    public Evolve(Problem p, GPActionListener listener) {
        this(p, listener, null);
    }

    /**
     * Starts the evolve object. You supply the problem, an instance of a GP params
     * object and the listener you want to use (console or graphical is available).
     */
    public Evolve(Problem p, GPActionListener gpInterface, GPParams params) {
        this.p = p;
        this.gpinterface = gpInterface;
        if (params == null) {
            // problem usually initialises the params
            this.params = new GPParams();
        } else {
            this.params = params;

        }
        // initialise the problem - this is where nodes are registered and training data loaded
        if (!this.params.hasBeenInitialised) {  
        	//POEY comment: go to the file jasmine.imaging.core.segmentation.JasmineSegmentationProblemDRSBetter > JasmineSegmentationProblemDRSBetter
            p.initialise(this, this.params);
            this.params.hasBeenInitialised = true;
        }

        // initialise any custom parameters - changing population size etc.
        if (!this.params.hasBeenCustomised) {
            p.customiseParameters(this.params);	//POEY comment: do nothing
            this.params.hasBeenCustomised = true;
        }

        copier = new DeepCopy();

    }

    int treeIndex = 0;
    long startTime;

    // The population
    private Individual[] population;
    private Individual[] bestIndividuals;

    float redundancy = 0;

    /**
     * Call this method to start the evolution. If you're using SXGP from within a GUI
     * and you prefer to have your mouse still working while you wait, use the start() method
     * to call this method as a thread.
     */
    public void run() {

    	//POEY comment: params contain nodes
        TreeBuilder t = new TreeBuilder(params);

        initialiseRandomNumberGenerator();

        // reset the number of evaluations
        totalEvaluations = 0;

        // current island for the selector
        int currentIsland = 0;

        // reset the operation counter (counts how many genetic operations occur, a debugging mechanism)
        OperationCounter.reset();

        p.clearFitnessCache();

        try {
            // check that the params don't have any silly values
            params.check();
        } catch (Exception err) {
            gpinterface.fatal(err.getMessage());
            return;
        }

        if (fatal) {
            // perhaps some fatal error will stop the GP before we even begin...
            System.err.println("Fatal: Evolve stopped.");
            return;
        }

        // make a note of the time
        startTime = System.currentTimeMillis();

        // tell the gp interface that we're ready to start
        //POEY comment: go to file jasmine.gp.interfaces.GPActionListener.java
        //set the window's title with p's name and set the timer
        gpinterface.onStartEvolution(this, p);
        
        // for coevolution problems...
        if (p instanceof CoevolutionProblem) {    	//POEY comment: there is no p which is a instance of CoevolutionProblem       	
            // Create the ADF nodes that will be coevolved. These ADF nodes are then inserted
            // into the GP params object as normal nodes so they can be accessed by the GP system
            // in the regular manner.  
            ((CoevolutionProblem) p).initialiseClassifiers(params);
        }

        // create an initial population
        population = new Individual[params.getPopulationSize()];
        t.generatePopulation(population, params, 0);

        // allow the problem to process the new generation
        p.processNewGeneration(population);		//POEY comment: do nothing

        // island assignment
        if (params.getIslandCount() > 1) {
            IslandUtils.assign(population, params);
        }

        // now crack on for however many generations it is
        for (int g = 0; g < params.getGenerations() && !stopFlag; g++) {

            if (params.usesHereditaryRepulsion()) {
                for (Individual ind : population) {
                    ind.purgeAncestors(g - 5);   
                }
            }

            // check nothing has gone wrong at the beginning of each generation
            if (fatal) {
                System.err.println("Fatal: Evolve stopped.");
                return;
            }

            // now tell the gp interface that we're starting a new generation. Let the problem know too.
            //POEY comment: jasmine.gp.interfacesGraphicalListener.java
            gpinterface.onGenerationStart(g);
            p.onGenerationStart();	//POEY comment: do nothing

            // dynamic size limiting.
            // Proposed in paper "Reducing Bloat in GP" (Monsieurs/Flerackers, 2001)
            if (params.isDynamicSizeLimitingOn()) {
                if (bestIndividual == null) {
                    // set the size limit to the initial size
                    params.setCutoffSize(params.getDynamicSizeLimitingInitSize());
                } else {
                    // is the best individual smaller than the initial size
                    if (bestIndividual.getTreeSize() < params.getDynamicSizeLimitingInitSize()) {
                        // make the cutoff size double the best individual's tree size
                        params.setCutoffSize(bestIndividual.getTreeSize() * 2);
                    } else {
                        // make the cutoff size the size of the best individual x some weighting
                        params.setCutoffSize((int) (params.getDynamicSizeLimitingMaxNewWeight() * bestIndividual.getTreeSize()));
                    }
                }
            }

            // evaluate all the individuals. This is farmed out to a method in problem. In most situations
            // this is fine, but in some problems where evaluation takes place from human input, the hook
            // is there for it to be changed by overriding this method directly from the problem.
            //POEY comment: evaluate each individual then gain their fitness values
            totalEvaluations += p.evaluateIndividuals(population, this, params, gpinterface);

            // sometimes a fresh population can be requested by the problem. This essentially
            // resets the GP and starts again from scratch.
            if (requestFreshPopulation) {	//not this case
                // stop asking
                requestFreshPopulation = false;

                gpinterface.onGenerationEnd(g);

                gpinterface.message("Generating fresh population");

                // recreate the initial population
                population = new Individual[params.getPopulationSize() + 10];
                t.generatePopulation(population, params, g);

                // allow the problem to process the new generation
                p.processNewGeneration(population);

                // try to clear up the mess we are inevitably leaving in our wake
                System.gc();

            } else {
                // But most of the time we update the population by breeding:

                // arrange the evaluated population by fitness (for elitism, and to find best individual)
                Arrays.sort(population);
                
                //POEY comment: to show population's fitness and features
				//for(int i =0; i<population.length ; i++)                
					//System.err.println("fitness:"+population[i].fitness+" -> "+ population[i].toJava());                

                // Get the best individual
                bestIndividual = p.getBestIndividual(population);

                //POEY comment: params contain nodes
                //getIslandCount() is defined to be 1 by default
                if (params.getIslandCount() > 1) {               	
                    bestIndividuals = IslandUtils.getBestIndividuals(population, params);
                }

                /*if (params.isERCOptimisationEnabled()) {
                    try {
                        ERCOptimiser.optimise(bestIndividual, p, this);
                    } catch (FoundBestIndividualException e) {
                        // do nothing - it will be dealt with later.
                    }
                }*/

                // let the gp interface know what we've got. The graphical listener will kindly display
                // the individual as java code

                gpinterface.setBestIndividual(bestIndividual);
                //POEY comment: jasmine.gp.interfaces.GraphicalListener
                gpinterface.onGenerationEnd(g);

                // Coevolution requires an additional couple of steps
                if (p instanceof CoevolutionProblem) {	//POEY comment: not this case
                    // use the fitness data from the population to update the co-evolved ADFs
                    ((CoevolutionProblem) p).updateClassifierFitness(population);

                    // cause the ADFs themselves to be evolved
                    ((CoevolutionProblem) p).evolveClassifiers(params);

                }

                // if the best individual isn't perfect, and if we've still got generations to go...
                //POEY comment: getGenerations() = 50
                //for classification in function learnToClassify(), params.getGenerations() = 1000000                
                if (bestIndividual.getKozaFitness() > 0 && g < params.getGenerations() - 1) {

                    // this object makes deep copies of objects. We have to copy everything properly
                    // otherwise everything would go horribly wrong

                    // start generating the next generation
                    Individual[] nextGeneration = new Individual[params.getPopulationSize()];
                    int c = 0;

                    // add any elites (best in generation) first, these guys get injected straight into the next
                    // generation without any genetic freakery
                    //POEY comment: params.getEliteCount() = 5 by default 
                    //copy the first five individuals to the next generation
                    for (int i = 0; i < params.getEliteCount(); i++) {
                        nextGeneration[c] = (copier.copyIndividual(population[i]));
                        c++;
                        OperationCounter.REPRODUCTION_COUNT++;
                    }

                    // use a selector to choose the parents.
                    Selector selector = params.getSelector();
                    selector.setPopulation(population);
                    //POEY comment: jasmine.gp.selection.RandomTournamentSelector.java
                    //to define tournament size = 2
                    selector.initialise(params);

                    try {

                        // BREEDING:
                        Crossover crossover = params.getCrossoverOperator();

                        // build the next generation

                        while (c < nextGeneration.length) {

                            treeIndex++;

                            // rotate the tree index to mutate different trees (if the individual has more than one)
                            if (treeIndex > params.getTreeCount() - 1) {
                                treeIndex = 0;
                            }
                            
                            // select the generic operator probabilistically.
                            int operator = params.getOperator();

                            // by default, select individuals from any island
                            if (params.getIslandCount() == 1) {
                                selector.setIsland(Selector.ANY_ISLAND);
                            } else {
                                // go through each island in round robin style
                                selector.setIsland(currentIsland);
                                currentIsland++;
                                if (currentIsland >= params.getIslandCount()) currentIsland = 0;
                            }

                            switch (operator) {
                                case GPParams.CROSSOVER:

                                    Individual ind1, ind2;

                                    if (params.usesHereditaryRepulsion()) {                                                                                

                                        HereditoryRepulsionSelector.compareIndividual = null;
                                        ind1 = getParent(selector, params);
                                        HereditoryRepulsionSelector.compareIndividual = ind1;
                                        // make sure that the second parent is in the same island
                                        selector.setIsland(ind1.getIslandID());
                                        // choose the second parent
                                        ind2 = getParent(selector, params);

                                    } else{
                                        // choose the first parent
                                        ind1 = getParent(selector, params);

                                        // make sure that the second parent is in the same island
                                        selector.setIsland(ind1.getIslandID());

                                        // choose the second parent
                                        ind2 = getParent(selector, params);
                                    }

                                    //POEY comment: jasmine.gp.StandardCrossover.java
                                    Node[] offspring = crossover.produceOffspring(params, ind1.getTree(treeIndex), ind2.getTree(treeIndex));

                                    if (offspring != null) {
                                    	//POEY comment: an offspring contains maleParent and femaleParent 
                                        for (int i = 0; i < offspring.length; i++) {

                                            // Create the full complement of trees
                                            Node[] trees = new Node[ind1.getTreeCount()];
                                            //POEY comment: trees.length = 1
                                            for (int j = 0; j < trees.length; j++) {
                                            	//POEY comment: copy an offspring to trees[]
                                                if (j == treeIndex) {	//POEY comment: use this case
                                                    trees[j] = offspring[i];
                                                } else {
                                                    // copy other trees from a parent
                                                    if (i == 0) {
                                                        trees[j] = ind1.getTree(j);
                                                    } else {
                                                        trees[j] = ind2.getTree(j);
                                                    }
                                                }
                                            }

                                            if (c < nextGeneration.length) {

                                                Individual child = new Individual(trees, ind1.getReturnType());
                                                child.setIslandID(ind1.getIslandID());

                                                if (params.usesHereditaryRepulsion()) {
                                                    // individuals must be better than both parents before
                                                    // considered for insertion into the next generation
                                                    p.evaluateIndividual(child, this, params, gpinterface);
                                                    if (child.getKozaFitness() <= ind1.getKozaFitness() && child.getKozaFitness() <= ind2.getKozaFitness()) {
                                                        child.createUniqueID(g+1, c);
                                                        child.initAncestry();
                                                        child.updateAncestry(ind1);
                                                        child.updateAncestry(ind2);
                                                        nextGeneration[c] = child;
                                                        c++;
                                                    } else {
                                                        // child is not good enough
                                                        // add the better of the parents
                                                        if (ind1.getKozaFitness() <= ind2.getKozaFitness()) {
                                                            double parentID = ind1.getUniqueID();
                                                            ind1.createUniqueID(g+1, c);
                                                            ind1.addAncestor(parentID);
                                                            nextGeneration[c] = ind1;
                                                        } else {
                                                            double parentID = ind2.getUniqueID();
                                                            ind2.createUniqueID(g+1, c);
                                                            ind2.addAncestor(parentID);
                                                            nextGeneration[c] = ind2;
                                                        }
                                                        c++;
                                                    }
                                                } else {
                                                    // normal GP - just add the child
                                                    nextGeneration[c] = child;
                                                    c++;
                                                }
                                            }
                                        }
                                    }

                                    break;

                                case GPParams.MUTATION:
                                    // mutation needs just one sacrificial lamb
                                    Individual ind3 = getParent(selector, params);

                                    // select the mutation operator probabilistically
                                    int mutationOperator = params.getMutationOperator();

                                    switch (mutationOperator) {
                                    	//POEY comment: treeIndex = 0
                                        case GPParams.POINT_MUTATION:
                                        	//POEY comment: parent's subtree is replaced by a new tree
                                            Mutation.pointMutate(t, ind3.getTree(treeIndex), params);
                                            break;
                                        case GPParams.ERC_MUTATION:
                                        	//POEY comment: a value of parent's numeric node is changed by a new number
                                            Mutation.mutateERCs(ind3.getTree(treeIndex), params);
                                            break;
                                        case GPParams.ERC_JITTERING:
                                            Mutation.jitterERCs(ind3.getTree(treeIndex), params);
                                            break;
                                    }

                                    if (params.usesHereditaryRepulsion()) {
                                        // individuals must be better than both parents before
                                        // considered for insertion into the next generation
                                        double parentFitness = ind3.getKozaFitness();
                                        p.evaluateIndividual(ind3, this, params, gpinterface);
                                        if (ind3.getKozaFitness() <= parentFitness) {
                                            double parentID = ind3.getUniqueID();
                                            ind3.createUniqueID(g+1, c);
                                            ind3.addAncestor(parentID);
                                            nextGeneration[c] = ind3;
                                        } else {
                                            // add a parent instead
                                            nextGeneration[c] = getParent(selector, params);
                                        }
                                        c++;
                                    } else {
                                        // normal GP
                                        nextGeneration[c] = ind3;
                                        c++;
                                    }

                                    break;
                                case GPParams.REPRODUCTION:
                                    // lucky individuals who are reproduced get put straight into the next gen
                                    Individual ind4 = getParent(selector, params);

                                    if (params.usesHereditaryRepulsion()) {
                                        // this individual will be the same as its parent so
                                        // don't need to check the fitness.
                                        double parentID = ind4.getUniqueID();
                                        ind4.createUniqueID(g+1, c);
                                        ind4.addAncestor(parentID);
                                    }

                                    nextGeneration[c] = ind4;
                                    c++;

                                    OperationCounter.REPRODUCTION_COUNT++;

                            }

                        }

                    } catch (FoundBestIndividualException e) {
                        // did we find the best invidual while optimising ERCs? It can happen...
                        gpinterface.setIdeal(true);
                        bestIndividual = e.getInd();
                        break;
                    }

                    selector.clear();
                    selector = null;

                    if (params.getGenerationGapMethod() == GPParams.GENERATION_GAP_OVERLAP_OFF) {                   	
                        // (N,N) evolution
                        // Children always completely replace the parents
                        // simple - just switch the populations and continue
                        population = nextGeneration;	//POEY comment: use this case
                    } else {
                        // (N+N) evolution
                        // The parents compete with children to stay in the population
                        // This means we have to evaluate the children first
                        p.evaluateIndividuals(nextGeneration, this, params, gpinterface);

                        // create an intermediate population that we can sort
                        Individual[] intermediatePopulation = new Individual[population.length + nextGeneration.length];
                        System.arraycopy(population, 0, intermediatePopulation, 0, population.length);
                        System.arraycopy(nextGeneration, 0, intermediatePopulation, population.length, intermediatePopulation.length);
                        Arrays.sort(intermediatePopulation);

                        // cream off the best individuals from the intermediate population
                        for (int i = 0; i < params.getPopulationSize(); i++) {
                            population[i] = intermediatePopulation[i];
                        }

                    }


                } else {
                    // found the best individual?
                    if (bestIndividual.getKozaFitness() == 0) gpinterface.setIdeal(true);
                    //POEY comment: jasmine.gp.interfaces.GraphicalLintener.java
                    //show a message on a complete window
                    gpinterface.onEndEvolution(g, params);
                    p.onFinish(bestIndividual, this);
                    break;
                }

            }

            //System.out.println("Gen " + g + " Evaluations avoided: "  + p.evaluationsAvoided);
            //p.evaluationsAvoided = 0;
            redundancy = p.evaluationsAvoided / (float) params.getPopulationSize();
            p.clearFitnessCache();

            // also - check if we are out of time
            if (params.getMaxTime() > 0) {
                long time = (System.currentTimeMillis() - startTime) / 1000;
                if (time >= params.getMaxTime()) {
                    stopFlag = true;
                }
            }

        }

        // record how long it took.
        totalTime = System.currentTimeMillis() - startTime;

        // finish
        p.onFinish(null, this);
        //POEY comment: jasmine.gp.interfaces.GraphicalListener.java
        gpinterface.onStopped();

    }

    /**
     * Gets the population of individuals.
     */
    public Individual[] getPopulation() {
        return population;
    }

    /**
     * Gets statistics about the population.
     */
    public FastStatistics getPopulationSizeStatistics() {
        FastStatistics f = new FastStatistics();
        for (int i = 0; i < population.length; i++) {
            Individual individual = population[i];
            //System.out.println(individual.getTreeSize());
            f.addData(individual.getTreeSize());
        }
        return f;
    }

    public FastStatistics getPopulationDepthStatistics() {
        FastStatistics f = new FastStatistics();
        for (int i = 0; i < population.length; i++) {
            Individual individual = population[i];
            f.addData(individual.getTree(0).getTreeDepth());
        }
        return f;
    }

    public float getRedundancy() {
        return redundancy;
    }

    /**
     * Returns the individual selected via the selection mechanism. Returned individual is a copy
     * of the original so it may be changed in any way without affecting the previous generation.
     *
     * @throws FoundBestIndividualException If the ERC optimiser is run and optimises to fitness of 0.
     */
    private Individual getParent(Selector s, GPParams params) throws FoundBestIndividualException {
    	//POEY comment: for select() -> jasmine.gp.selection.RandomTournamentSelector.java
        Individual i = copier.copyIndividual((Individual) s.select());
        if (params.isOptimisationEnabled()) { 	//POEY comment: not this case
            TreeOptimiser.optimise(i, params);
        }
        if (i == null) {
            fatal("Selector did not select individual successfully. Is t > 0?");
        }
        return i;
    }

    /**
     * Allows access to the best of generation.
     */
    public Individual getBestIndividual() {
        return bestIndividual;
    }

    /**
     * Allows access to the best of generation (Island Selection)
     */
    public Individual[] getBestIndividuals() {
        if (bestIndividuals == null) {
            return new Individual[]{bestIndividual};
        }
        return bestIndividuals;
    }

    /**
     * Gets the params file which contains all the GP settings
     */
    public GPParams getParams() {
        return this.params;
    }

    /**
     * Gets the problem that Evolve is trying to solve.
     */
    public Problem getProblem() {
        return p;
    }

    /**
     * Gets the listener
     */
    public GPActionListener getListener() {
        return gpinterface;
    }

    /**
     * Can be used by a problem if it encounters a serious problem (such as having no
     * training data, from which it cannot continue. Evolve sends this message to the interface
     * and then exits.
     *
     * @param message The error message to display
     */
    public void fatal(String message) {
        gpinterface.fatal(message);
        fatal = true;
    }

    /**
     * Allows access to the interface to print out or display a message of some kind.
     */
    public void message(String message) {
        gpinterface.message(message);
    }

    /**
     * Called when the GP system wants a new population. Used by GP with partial solutions.
     */
    public void requestFreshPopulation() {
        requestFreshPopulation = true;
    }

    /**
     * @return How long it took to evaluate the population. -1 if the population was not evaluated.
     */
    public long getTotalTime() {
        return totalTime;
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the number of individuals evaluated.
     */
    public int getTotalEvaluations() {
        return totalEvaluations;
    }

    public static long seed = 2357;
    protected static Random r = null;

    public static double getRandomNumber() {

        if (r == null) {
            initialiseRandomNumberGenerator();
        }        
        return r.nextDouble();

    }

    public static void initialiseRandomNumberGenerator() {   	
        if (seed == -1) {        	
            r = new Random();
        } else {
        	//POEY comment: use this case when 
        	//-> to generate population at generation #0
        	//-> to random a crossover position 
            r = new Random(seed);
        }
    }

    public void setSeed(long seed) {
        r = null;
        Evolve.seed = seed;
    }

    public void optimise(Individual individual) {
        try {
            // remember the optmisation state first
            boolean enabled = getParams().isOptimisationEnabled();
            // turn on optimisation
            getParams().setOptimisationEnabled(true);
            // node debugger is only instantiated when individual is created, so copy it to do so
            Individual ind = individual.copy();
            // evaluate the individual as normal
            p.evaluate(ind, new DataStack(), this);
            // run the tree optimiser
            TreeOptimiser.optimise(ind, getParams());
            // restore the value of the optimiser
            getParams().setOptimisationEnabled(enabled);
        } catch (CloneNotSupportedException err) {
            err.printStackTrace();
        }
    }

    public int countDuplicates(Individual[] population) {

        //Hashtable<Integer, Vector<Individual>> mappings = new Hashtable<Integer, Vector<Individual>>();
        //Vector<Integer> lisps = new Vector<Integer>(population.length);
        Vector<String> lisps = new Vector<String>(population.length);
        for (int i = 0; i < population.length; i++) {
            Individual individual = population[i];
            String lisp = individual.toLisp();
            //int hashcode = lisp.hashCode();
            if (!lisps.contains(lisp)) {
                lisps.add(lisp);
                //Vector<Individual> individuals = new Vector<Individual>(5);
                //individuals.add(individual);
                //mappings.put(hashcode, individuals);
            } else {
                //mappings.get(hashcode).add(individual);
            }
        }

        //System.out.println("Pop size: " + population.length);
        //System.out.println("Unique trees: " + lisps.size());
        return lisps.size();

    }

    public Individual[] makePopulation(TreeBuilder t, int popSize) {
        // recreate the initial population
        Individual[] population = new Individual[popSize];
        t.generatePopulation(population, params, 0);
        return population;
    }

}
