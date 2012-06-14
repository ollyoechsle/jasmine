package jasmine.gp.problems;


import jasmine.gp.Crossover;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.Mutation;
import jasmine.gp.OperationCounter;
import jasmine.gp.nodes.ADFNode;
import jasmine.gp.params.ADFNodeConstraints;
import jasmine.gp.params.GPParams;
import jasmine.gp.selection.Selector;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeUtils;
import jasmine.gp.treebuilders.TreeBuilder;
import jasmine.gp.util.DeepCopy;
import jasmine.gp.util.FoundBestIndividualException;
import jasmine.imaging.commons.StatisticsSolver;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Collections;

/**
 * Coevolution is a variant of GP where the operator set and program logic
 * are evolved in two separate populations with mutual feedback.  The idea is
 * that good operators will make better programs, and the operators used in
 * good programs are given a higher fitness.
 *
 * @author Olly Oechsle, University of Essex, Date: 27-Feb-2007
 * @version 1.0
 */
public abstract class CoevolutionProblem extends Problem {

    /**
     * The problem has a separate params object used for co-evolution
     */
    protected GPParams coevolutionParams;

    /**
     * All the ADFs are stored here
     */
    protected Vector<ADFNodeConstraints> adfs;

    /**
     * Allows us to assign a unique ID to each new classifier
     */
    protected long IDcounter = 1000;

    /**
     * Maps each ID onto the corresponding ADF.
     */
    protected Hashtable<Long, ADFNodeConstraints> mappings;

    /**
     * Initialises the Coevolution Problem.
     */
    public CoevolutionProblem() {

        // get the coevolution params
        coevolutionParams = getCoevolutionParameters();

        // ensures that classifiers are created with full strongly typed constraints
        coevolutionParams.setNodeChildConstraintsEnabled(true);

        // initialise the mappings
        mappings = new Hashtable<Long, ADFNodeConstraints>(coevolutionParams.getPopulationSize());

    }


    /**
     * Implements the method on Problem and delegates it to an abstract
     * method which also includes the coevolution parameters.
     */
    public final void initialise(Evolve e, GPParams params) {   	
        initialise(e, params, coevolutionParams);
    }

    /**
     * Initialises the problem. This is where the training data is loaded
     * and the GP params object initialised with Nodes to use. The return
     * object should also be set up.
     * As this is a coevolution problem, the coevolution params also need to
     * be initialised, which includes
     * @param params The parameters of the main evolution process
     * @param coevolutionParams The parameters of the co-evolved population*
     */
    public abstract void initialise(Evolve e, GPParams params, GPParams coevolutionParams);

    /**
     * Gets the parameters that the coevolution problem needs.
     * Initialises the parameters with a few sensible values. The best
     * place to override these parameters is via the customise parameters method.
     */
    public GPParams getCoevolutionParameters() {
        GPParams p = new GPParams();
        // set crossover and mutation much lower so there is a good chance of reproduction for good classifiers
        p.setCrossoverProbability(0.20);
        p.setMutationProbability(0.20);
        p.setPopulationSize(100);
        return p;
    }


    /**
     * Implements the method on Problem and delegates it to an abstract
     * method which also includes the coevolution parameters.
     */
    public void customiseParameters(GPParams params) {  	   	
        customiseParameters(params, coevolutionParams);
    }

    /**
     * Where the problem can customise its GP parameters.
     * @param params The parameters of the main evolution process
     * @param coevolutionParams The parameters of the co-evolved population
     */
    public abstract void customiseParameters(GPParams params, GPParams coevolutionParams);

    /**
     * Creates the ADF classifiers that are to be used by the
     * individuals. It updates the GP params object so the
     * system has access to these new "nodes"
     *
     * @param p The GPParams object to be updated with the new retainedClassifiers. If the retainedClassifiers
     *          are not registered with a GPParams object they can't be accessed by the GP system.
     * @version The dumb way
     */
    public void initialiseClassifiers(GPParams p) {

        if (adfs == null) {
            adfs = new Vector<ADFNodeConstraints>(coevolutionParams.getPopulationSize());
        }

        TreeBuilder t = new TreeBuilder(coevolutionParams);

        // fill the population
        while (adfs.size() < coevolutionParams.getPopulationSize()) {
            ADFNodeConstraints adf = createNewRandomADF(t);
            adfs.add(adf);
        }

        // allow the main population access to the these features.
        registerADFs(p);

    }

    /**
     * Generates a new, random ADF using the tree builder specified by the coevolution params.
     */
    public ADFNodeConstraints createNewRandomADF(TreeBuilder t) {
        Node tree = t.createTree(coevolutionParams);
        ADFNode n = new ADFNode(getNextID(), tree, new int[]{coevolutionParams.getReturnType()});
        return n.createNodeConstraintsObject();
    }

    public long getNextID() {
        IDcounter++;
        return IDcounter;
    }

    /**
     * Sets up the GPParams object so that the retainedClassifiers can be accessed by the main
     * GP environment and individuals be constructed from the retainedClassifiers.
     */
    protected void registerADFs(GPParams p) {

        if (p == null) {
            System.err.println("Called registerADFs() on null GPParams object");
            return;
        }

        // remove all existing adfs from the GP params object
        p.clearADFs();
        mappings.clear();

        // now register the adfs with the GPParams object
        for (int i = 0; i < adfs.size(); i++) {
            ADFNodeConstraints adf = adfs.elementAt(i);
            p.registerNode(adf);
            mappings.put(adf.getID(), adf);
        }

    }

    /**
     * Looks at the best individuals from the population and assigns any ADFs that
     * the individual used
     */
    public void updateClassifierFitness(Individual[] bestIndividuals) {

        /**
         * Clear the fitness values of each ADF Node, as fitness is taken based on the
         * context of only the previous generation. Previous good behaviour is thus ignored.
         */
        for (int i = 0; i < adfs.size(); i++) {
            ADFNodeConstraints adfNodeParams = adfs.elementAt(i);
            adfNodeParams.resetFitness();
            adfNodeParams.setUsages(0);
        }

        for (int i = 0; i < bestIndividuals.length; i++) {
            Individual individual = bestIndividuals[i];
            // go through the individual's ADF nodes
            Vector<ADFNode> adfNodes = TreeUtils.getADFNodes(individual.getTree(0));

            // for each adf node, find the equivalent adf originalNode params object
            // this is easy, as each ADF node has a unique id - all we need to do is find the ADFNodeParams object
            // with the same id.
            for (int j = 0; j < adfNodes.size(); j++) {

                // give the ADF node the fitness of the individual
                final ADFNode adfNode = adfNodes.elementAt(j);

                // get the ADFNodeParam
                ADFNodeConstraints p = mappings.get(adfNode.getID());

                if (p != null) {
                    //System.err.println("Adding fitness");
                    p.addFitness(individual.getKozaFitness());
                    p.addUsage();
                } else {
                    System.err.println("ADF Node's parent cannot be found: " + adfNode.getID());
                }

            }
        }

        StatisticsSolver f = new StatisticsSolver();
        for (int i = 0; i < adfs.size(); i++) {
            ADFNodeConstraints adfNodeConstraints = adfs.elementAt(i);
            if (adfNodeConstraints.getUsages() > 0) {
                float fitness = (float) adfNodeConstraints.getFitness();
                f.addData(adfNodeConstraints.getFitness());
            }
        }

        System.out.println("ADF Fitness: " + f.getMean() + "+-" + f.getStandardDeviation());
        

    }

    /**
     * Evolution of co-evolved classifiers is quite different to regular evolution. For one thing we
     * can't just replace them with new ones because the other population is already bound to them. The
     * only thing we can do is change the trees in order to make poor ones more successful. We don't
     * want to break the co-evolved classifiers so instead we apply genetic operators to the weakest
     * classifiers.
     */
    public void evolveClassifiers(GPParams p) {

        // use a selector to choose the parents.
        Selector selector = coevolutionParams.getSelector();

        // TODO: Need to fix this
        //selector.setPopulation(adfs);
        selector.initialise(coevolutionParams);

        Crossover crossover = coevolutionParams.getCrossoverOperator();

        // Find the ADF nodes not in use
        for (int i = 0; i < adfs.size(); i++) {
            ADFNodeConstraints adf = adfs.elementAt(i);
            if (adf.getUsages() == 0) {

                //Node tree = coevolutionParams.getTreeBuilder().createTree(coevolutionParams);
                //adf.setNode(new ADFNode(adf.getID(), tree, coevolutionParams.getReturnType()));

                boolean updated = false;

                // genetic operations sometimes fail - loop until they produce something useful.
                while (!updated) {

                // select the generic operator probabilistically.
                int operator = coevolutionParams.getOperator();

                // by default, select individuals from any island
                selector.setIsland(Selector.ANY_ISLAND);

                switch (operator) {
                case GPParams.CROSSOVER:

                        // choose the first parent
                        ADFNodeConstraints ind1 = getParent(selector, coevolutionParams);
                        // make sure that the second parent is in the same island
                        selector.setIsland(ind1.getIslandID());
                        // choose the second parent
                        ADFNodeConstraints ind2 = getParent(selector, coevolutionParams);

                        Node[] offspring = crossover.produceOffspring(coevolutionParams, ind1.getNode(), ind2.getNode());

                        if (offspring != null) {
                            ADFNodeConstraints child1 = new ADFNodeConstraints(new ADFNode(getNextID(), offspring[0], new int[]{coevolutionParams.getReturnType()}), new int[]{coevolutionParams.getReturnType()});
                            adfs.setElementAt(child1, i);
                            updated = true;
                        }

                        break;
                    case GPParams.MUTATION:
                        // mutation needs just one sacrificial lamb
                        ADFNode ind3 = (ADFNode) getParent(selector, coevolutionParams).getNode();

                        // select the mutation operator probabilistically
                        int mutationOperator = coevolutionParams.getMutationOperator();

                        switch (mutationOperator) {
                            case GPParams.POINT_MUTATION:
                                Mutation.pointMutate(new TreeBuilder(coevolutionParams), ind3, coevolutionParams);
                                break;
                            case GPParams.ERC_MUTATION:
                                Mutation.mutateERCs(ind3, coevolutionParams);
                                break;
                            case GPParams.ERC_JITTERING:
                                Mutation.jitterERCs(ind3, coevolutionParams);
                                break;
                        }

                        ADFNodeConstraints child2 = new ADFNodeConstraints(new ADFNode(getNextID(), ind3, new int[]{coevolutionParams.getReturnType()}), new int[]{coevolutionParams.getReturnType()});
                        adfs.setElementAt(child2, i);
                        updated = true;
                        break;
                    case GPParams.REPRODUCTION:
                        // lucky individuals who are reproduced get put straight into the next gen
                        ADFNode ind4 = (ADFNode) getParent(selector, coevolutionParams).getNode();
                        ADFNodeConstraints child3 = new ADFNodeConstraints(new ADFNode(getNextID(), ind4, new int[]{coevolutionParams.getReturnType()}), new int[]{coevolutionParams.getReturnType()});
                        adfs.setElementAt(child3, i);
                        updated = true;
                }
                }
            } else {

/*                boolean updated = false;

                // genetic operations sometimes fail - loop until they produce something useful.
                while (!updated) {

                // select the generic operator probabilistically.
                int operator = coevolutionParams.getOperator();

                // by default, select individuals from any island
                selector.setIsland(Selector.ANY_ISLAND);

                switch (operator) {
                case GPParams.CROSSOVER:

                        // choose the first parent
                        ADFNodeConstraints ind1 = adf;
                        // make sure that the second parent is in the same island
                        selector.setIsland(ind1.getIslandID());
                        // choose the second parent
                        ADFNodeConstraints ind2 = getParent(selector, coevolutionParams);

                        Node[] offspring = crossover.produceOffspring(ind1.getNode(), ind2.getNode());

                        if (offspring != null) {
                            adf.setNode(new ADFNode(adf.getID(), offspring[0], coevolutionParams.getReturnType()));
                            updated = true;
                        }

                        break;
                    case GPParams.MUTATION:
                        // mutation needs just one sacrificial lamb
                        ADFNode ind3 = (ADFNode) adf.getNode();

                        // select the mutation operator probabilistically
                        int mutationOperator = coevolutionParams.getMutationOperator();

                        switch (mutationOperator) {
                            case GPParams.POINT_MUTATION:
                                Mutation.pointMutate(ind3, coevolutionParams);
                                break;
                            case GPParams.ERC_MUTATION:
                                Mutation.mutateERCs(ind3, coevolutionParams);
                                break;
                            case GPParams.ERC_JITTERING:
                                Mutation.jitterERCs(ind3, coevolutionParams);
                                break;
                        }

                        adf.setNode(new ADFNode(adf.getID(), ind3, coevolutionParams.getReturnType()));
                        updated = true;
                        break;
                    case GPParams.REPRODUCTION:
                        updated = true;
                        break;
                }
                }*/


            }
        }

        // there will now be some holes in the classifier list
        // fill it up by calling initialise classifiers again
        registerADFs(p);

    }

    /**
     * Takes the worst retainedClassifiers in the population and replaces them with
     * fresh ones.
     *
     * @param p The GPParams object to be updated with the new retainedClassifiers. If the retainedClassifiers
     *          are not registered with a GPParams object they can't be accessed by the GP system.*
     */
    public void evolveClassifiers_OLD() {

        Collections.sort(adfs);

        // start generating the next generation
        Vector<ADFNodeConstraints> nextGeneration = new Vector<ADFNodeConstraints>(coevolutionParams.getPopulationSize());

        // use a selector to choose the parents.
        Selector selector = coevolutionParams.getSelector();
        //selector.setPopulation(adfs);
        selector.initialise(coevolutionParams);

        // BREEDING:
        //Crossover crossover = params.getCrossoverOperator();


        // build the next generation
        while (nextGeneration.size() < coevolutionParams.getPopulationSize()) {

            // select the generic operator probabilistically.
            int operator = coevolutionParams.getOperator();

            // by default, select individuals from any island
            selector.setIsland(Selector.ANY_ISLAND);

            switch (operator) {
                // TODO: Redo crossover
/*                case GPParams.CROSSOVER:
                    // choose the first parent
                    ADFNodeConstraints ind1 = getParent(selector, coevolutionParams);
                    // make sure that the second parent is in the same island
                    selector.setIsland(ind1.getIslandID());
                    // choose the second parent
                    ADFNodeConstraints ind2 = getParent(selector, coevolutionParams);

                    if (ind1.getNode() == null) continue;
                    if (ind2.getNode() == null) continue;

                    StandardCrossover.produceOffspring(ind1.getNode(), ind2.getNode());
                    nextGeneration.add(ind1);
                    nextGeneration.add(ind2);
                    break;*/
                case GPParams.MUTATION:
                    // mutation needs just one sacrificial lamb
                    ADFNodeConstraints ind3 = getParent(selector, coevolutionParams);

                    if (ind3.getNode() == null) continue;

                    // select the mutation operator probabilistically
                    int mutationOperator = coevolutionParams.getMutationOperator();

                    switch (mutationOperator) {
                        case GPParams.POINT_MUTATION:
                            Mutation.pointMutate(new TreeBuilder(coevolutionParams), ind3.getNode(), coevolutionParams);
                            break;
                        case GPParams.ERC_MUTATION:
                            Mutation.mutateERCs(ind3.getNode(), coevolutionParams);
                            break;
                        case GPParams.ERC_JITTERING:
                            Mutation.jitterERCs(ind3.getNode(), coevolutionParams);
                            break;
                    }

                    nextGeneration.add(ind3);
                    break;
                case GPParams.REPRODUCTION:
                    // lucky individuals who are reproduced get put straight into the next gen
                    ADFNodeConstraints ind4 = getParent(selector, coevolutionParams);
                    nextGeneration.add(ind4);
                    OperationCounter.REPRODUCTION_COUNT++;
            }

        }

        // and add any elites (best in generation) on top, these guys get injected straight into the next
        // generation without any genetic freakery
        DeepCopy copier = new DeepCopy();
        for (int i = 0; i < coevolutionParams.getEliteCount(); i++) {
            nextGeneration.add((ADFNodeConstraints) copier.copy(adfs.elementAt(i)));
            OperationCounter.REPRODUCTION_COUNT++;
        }

        // swap across
        adfs = nextGeneration;

        // there will now be some holes in the classifier list
        // fill it up by calling initialise classifiers again
        registerADFs(coevolutionParams);

    }

    private void addNewClassifier(Vector<ADFNodeConstraints> population, ADFNodeConstraints adf) {
        population.add(adf);
    }

    /**
     * Returns the individual selected via the selection mechanism. Returned individual is a copy
     * of the original so it may be changed in any way without affecting the previous generation.
     *
     * @throws FoundBestIndividualException If the ERC optimiser is run and optimises to fitness of 0.
     */
    private ADFNodeConstraints getParent(Selector s, GPParams params) {
        ADFNodeConstraints a = (ADFNodeConstraints) s.select();
        return a.copy();
    }

}
