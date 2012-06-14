package jasmine.gp.params;


import jasmine.gp.Crossover;
import jasmine.gp.Evolve;
import jasmine.gp.StandardCrossover;
import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.selection.HereditoryRepulsionSelector;
import jasmine.gp.selection.RandomTournamentSelector;
import jasmine.gp.selection.Selector;
import jasmine.gp.tree.Debugger;
import jasmine.gp.tree.Node;

import java.util.Vector;
import java.util.Hashtable;

/**
 * A Repository for all the GP parameters, and accessible programmatically through
 * getter and setter methods. Some parameters are managable through the GPStartDialog GUI.
 *
 * @see jasmine.gp.util.GPStartDialog
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class GPParams {

    public static final String[] treeBuilderNames = new String[]{"SXGP", "GROW", "FULL", "RAMPED HALF AND HALF"};
    public static final String[] generationGapNames = new String[]{"Normal (1+l)", "Overlapping (1,l)"};

    public static final int SXGP = 0;
    public static final int GROW = 1;
    public static final int FULL = 2;
    public static final int RAMPED_HALF_AND_HALF = 3;

    public static final int REPRODUCTION = 0;
    public static final int CROSSOVER = 1;
    public static final int MUTATION = 2;

    public static final int POINT_MUTATION = 20;
    public static final int ERC_MUTATION = 21;
    public static final int ERC_JITTERING = 22;

    public static final int GENERATION_GAP_OVERLAP_OFF = 0;
    public static final int GENERATION_GAP_OVERLAP_ON = 1;



    // two flags to ensure initialisation and customisation only occur once.
    public boolean hasBeenInitialised = false;
    public boolean hasBeenCustomised = false;

    protected Vector<NodeConstraints> nodes;
    protected Vector<NodeConstraints> ercs;

    public Selector selector;

    protected Crossover crossover = new StandardCrossover();

    /**
     * Gets the crossover implementation to use for recombination.
     */
    public Crossover getCrossoverOperator() {
        return crossover;
    }

    /**
     * The max time that GP is allowed to proceed for. Set to minus one for unlimited.
     */
    protected int maxTime = -1;

    /**
     * The max time that GP is allowed to proceed for. Minus one for unlimited.
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * The max time that GP is allowed to proceed for. Set to minus one for unlimited.
     */
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Prevents identical individuals from being evaluated unecessarily
     */
    protected boolean avoidUnnecessaryEvaluations = false;

    /**
     * Prevents identical individuals from being evaluated unecessarily
     */
    public boolean avoidUnnecessaryEvaluations() {
        return avoidUnnecessaryEvaluations;
    }

    /**
     * Prevents identical individuals from being evaluated unecessarily
     */    
    public void setAvoidUnnecessaryEvaluations(boolean avoidUnnecessaryEvaluations) {
        this.avoidUnnecessaryEvaluations = avoidUnnecessaryEvaluations;
    }


    /**
     * Prevents crossover between individuals which are similar to eachother.
     * AKA prevents incest.
     * A Simple Powerful Constraint for Genetic Programming
     * Gearoid Murphy and Conor Ryan
     */
    protected boolean useHereditaryRepulsion;

    public boolean usesHereditaryRepulsion() {
        return useHereditaryRepulsion;
    }

    public void setUsesHereditaryRepulsion(boolean useHereditaryRepulsion) {
        this.useHereditaryRepulsion = useHereditaryRepulsion;
    }

    /**
     * The number of trees inside each individual.
     */
    protected int treeCount = 1;

    public int getTreeCount() {
        return treeCount;
    }

    public void setTreeCount(int treeCount) {
        this.treeCount = treeCount;
    }

    public int getTreeBuilder() {
        return treeBuilder;
    }

    public void setTreeBuilder(int treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    public int getTreeBuilderID() {
        return treeBuilder;
    }

    protected boolean treeCheckingEnabled = false;

    public boolean isTreeCheckingEnabled() {
        return treeCheckingEnabled;
    }

    public void setTreeCheckingEnabled(boolean treeCheckingEnabled) {
        this.treeCheckingEnabled = treeCheckingEnabled;
    }

    protected int treeBuilder = RAMPED_HALF_AND_HALF;

    protected int returnType = NodeConstraints.NUMBER;

    protected int generationGapMethod = GENERATION_GAP_OVERLAP_OFF;

    public int getGenerationGapMethod() {
        return generationGapMethod;
    }

    public void setGenerationGapMethod(int generationGapMethod) {
        this.generationGapMethod = generationGapMethod;
    }

    /**
     * The minimum depth of the tree, a working minimum is usually two.
     */
    protected int minTreeDepth = 1;

    /**
     * The maximum depth of a tree, measured from the root to the leaf.
     */
    protected int maxTreeDepth = 6;

    /**
     * Gets the maximum depth of a tree, measured from the root to the leaf.
     */
    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    /**
     * Sets the maximum depth of a tree, measured from the root to the leaf.
     */
    public void setMaxTreeDepth(int maxTreeDepth) {
        this.maxTreeDepth = maxTreeDepth;
    }


    /**
     * The number of islands in the population
     */
    protected int numIslands = 1;

    /**
     * Gets the number of islands in the population
     */
    public int getIslandCount() {
        return numIslands;
    }

    /**
     * Sets the number of islands in the population
     */
    public void setIslandCount(int numIslands) {
        this.numIslands = numIslands;
    }

    /**
     * Gets the minimum depth of the tree, the default is two.
     */
    public int getMinTreeDepth() {
        return minTreeDepth;
    }

    /**
     * Sets the minimum depth of the tree
     */
    public void setMinTreeDepth(int minTreeDepth) {
        this.minTreeDepth = minTreeDepth;
    }

    /**
     * The number of generation to run the simulation for.
     */
    protected int generations = 50;

    /**
     * The number of individuals in the population. Higher numbers
     * may discover better solutions but the process is more time
     * consuming.
     */
    protected int populationSize = 500;


    /**
     * The size of the tournament from which individuals are
     * selected. Tournament size of 2 is generally recommended
     * as a good bet in the GP literature.
     */
    protected int tournamentSize = 2;

    public Selector getSelector() {
        if (usesHereditaryRepulsion()) {       	
            return new HereditoryRepulsionSelector();
        }
        return new RandomTournamentSelector();
        //return new UnbiasedTournamentSelector();
    }


    /**
     * How many of the old population do we directly inject into the next
     * generation? This prevents the fitness value from regressing.
     */
    protected int eliteCount = 5;


    protected double terminalProbability = -1;

    /**
     * The probability of choosing a terminal when the GROW builder is allowed to choose either.
     */
    protected double getTerminalProbability() {
        return terminalProbability;
    }

    public void setTerminalProbability(double terminalProbability) {
        if (terminalProbability > 1) {
            terminalProbability = 1;
        }
        if (terminalProbability == 0) {
            System.err.println("Terminal probability is zero - this will make GROW builder act like a FULL builder.");
        }
        this.terminalProbability = terminalProbability;
    }

    /**
     * The probability that when a terminal is required by the tree builder that
     * it will be a feature versus an ERC. 
     */
    protected double terminalVsERCProbability = 0.5;


    /**
     * The probability that when a terminal is required by the tree builder that
     * it will be a feature versus an ERC. In this case both ERCs and Terminals are
     * returned with equal probability.
     */
    public double getTerminalVsERCProbability() {
        return terminalVsERCProbability;
    }


    /**
     * The probability that when a terminal is required by the tree builder that
     * it will be a feature versus an ERC. In this case both ERCs and Terminals are
     * returned with equal probability.
     */
    public void setTerminalVsERCProbability(double terminalVsERCProbability) {
        this.terminalVsERCProbability = terminalVsERCProbability;
    }

    /**
     * The probability that two good individuals do crossover.
     * The crossover operation itself may fail so the actual number may
     * be lower.
     */
    protected double crossoverProbability = 0.7;

    /**
     * The probability that mutation will happen (out of 100%)
     */
    protected double mutationProbability = 0.2;

    // IF MUTATION IS SELECTED - WHICH TYPE:

    /**
     * The probability that an entire branch of an individual will be replaced with another one
     */
    protected double pointMutationProbability = 0.50;

    /**
     * The probability that each ERC has of being mutated.
     * Mutation in this case means the ERC will be replaced with a completely new value
     * The larger the individual, the more ERCs will be mutated.
     */
    protected double ERCmutateProbability = 0.25;

    /**
     * If an ERC is NOT mutated, it might be jittered, which means
     * changing it randomly, RELATIVE to its original value (within certain boundaries)
     */
    protected double ERCjitterProbability = 0.25;

    /**
     * Sets whether a node can dictate what kind of children it would like.
     * See Node.getChildType(index) for more details.
     */
    protected boolean nodeChildConstraintsEnabled = false;

    /**
     * The maximum size a tree is allowed to be. If it exceeds this size it is
     * automatically removed from the population. Set this value to -1 to allow
     * all sizes to be evaluated on merit.
     */
    protected int cutoffSize = 100;


    protected int cutoffDepth = -1;

    /**
     * Dynamic size limiting - affects cutoff size according to the size
     * of the best individual in the last generation. If the last best individual had a size
     * of 20, and the max new weight multiplier is 1.33, then the new cutoff size will be
     * 27. This allows the population to grow without a static size ceiling, but still prevents
     * individuals from growing out of control. 
     */
    protected boolean dynamicSizeLimiting = false;
    protected int dynamicSizeLimitingInitSize = 30;
    protected double dynamicSizeLimitingMaxNewWeight = 1.333;

    /**
     * The probability that an ADF will be swapped for another one. This happens after an
     * individual is selected for erc mutation.
     */
    protected double ADFSwapProbability = 0.10;

    /**
     * Whether to ignore warnings when trying to get a non terminal. On rare occasions, such as when
     * using numeric ERCs but not wanting to use numeric functions (such as add/subtract) you'll want
     * to enable this.
     */
    protected boolean ignoreNonTerminalWarnings = false;

    /**
     * Whether to ignore warnings when trying to get a terminal. Sometimes there are no terminals for a given
     * type, only functions.
     */
    protected boolean ignoreTerminalWarnings = false;

    /**
     * When terminals are added, they can be analysed to automatically generate typed ERCs. An ERC is created
     * based on the training data and is associated with a given terminal. When comparisons are made the terminal
     * is compared to known values as determined by the range typed ERC.
     */
    protected boolean automaticRangeTyping = false;

    /**
     * The probability that the the ERC values on the optimum individual are tweaked to
     * optimise its performance at the end of a generation.
     */
    protected boolean ERCOptimisationEnabled = true;


    /**
     * The probability that the the ERC values on the optimum individual are tweaked to
     * optimise its performance at the end of a generation.
     */
    public boolean isERCOptimisationEnabled() {
        return ERCOptimisationEnabled;
    }

    /**
     * The probability that the the ERC values on the optimum individual are tweaked to
     * optimise its performance at the end of a generation.
     */    
    public void setERCOptimisationEnabled(boolean ERCOptimisationEnabled) {
        this.ERCOptimisationEnabled = ERCOptimisationEnabled;
    }

    /**
     * Returns a Genetic Operation, consisting of Reproduction, Crossover or Mutation, proportional
     * to the probabilities in the settings. If Crossover probability is set to 0.8, this method will return
     * crossover 80% of the time.
     */
    public int getOperator() {

        int mode = REPRODUCTION;

        // first decide which genetic operator to use
        double value = Evolve.getRandomNumber();
        if (value <= crossoverProbability) {
            // crossover mode
            mode = CROSSOVER;
        }

        if (value > crossoverProbability && value <= (crossoverProbability + mutationProbability)) {
            mode = MUTATION;
        }

        return mode;

    }

    public int getMutationOperator() {

        int mode = ERC_JITTERING;

        double value = Evolve.getRandomNumber();
        if (value <= pointMutationProbability) {
            // crossover mode
            mode = POINT_MUTATION;
        }

        if (value > pointMutationProbability && value <= (pointMutationProbability + ERCmutateProbability)) {
            mode = ERC_MUTATION;
        }

        return mode;

    }

    /**
     * Crossover probability + point mutation probability must not be more than 1.0.
     */
    public void check() {
        if (crossoverProbability + mutationProbability > 1)  {
            throw new RuntimeException("Crossover probablity + mutation probability cannot exceed 1.0");
        }
        if (pointMutationProbability + ERCmutateProbability + ERCjitterProbability > 1) {
            throw new RuntimeException("Mutation probabilities sum to more than 100%");
        }
        // if there is an overlaying generation gap method, do not allow
        // any reproduction, otherwise individuals will start to dominate the population
        // very quickly.
        if (generationGapMethod == GENERATION_GAP_OVERLAP_ON && (crossoverProbability + mutationProbability) < 1) {
            System.err.println("Increasing crossover probability to remove reproduction for this generation gap method");
            crossoverProbability = 1 - mutationProbability;
        }
        if (minTreeDepth > maxTreeDepth) {
            throw new RuntimeException("Min tree depth must be equal or less than max tree depth");
        }
    }


    /**
     * When terminals are added, they can be analysed to automatically generate typed ERCs. An ERC is created
     * based on the training data and is associated with a given terminal. When comparisons are made the terminal
     * is compared to known values as determined by the range typed ERC.
     */
    public boolean isAutomaticRangeTypingEnabled() {
        return automaticRangeTyping;
    }

    /**
     * When terminals are added, they can be analysed to automatically generate typed ERCs. An ERC is created
     * based on the training data and is associated with a given terminal. When comparisons are made the terminal
     * is compared to known values as determined by the range typed ERC.
     */    
    public void setAutomaticRangeTypingEnabled(boolean automaticRangeTyping) {
        this.automaticRangeTyping = automaticRangeTyping;
    }

    /**
     * Whether to ignore warnings when trying to get a terminal. Sometimes there are no terminals for a given
     * type, only functions.
     */
    public boolean ignoreTerminalWarnings() {
        return ignoreTerminalWarnings;
    }

    /**
     * Whether to ignore warnings when trying to get a terminal. Sometimes there are no terminals for a given
     * type, only functions.
     */
    public void setIgnoreTerminalWarnings(boolean ignoreTerminalWarnings) {
        this.ignoreTerminalWarnings = ignoreTerminalWarnings;
    }


    // TREE FUNCTIONS
    public static final int ANY_RETURN_TYPE = -1;

    // Keep a count of the number of nodes of each type
    double functionCount = 0;
    double terminalCount = 0;
    double ercCount = 0;
    double totalNodes = 0;

    public GPParams() {
        nodes = new Vector<NodeConstraints>(30);
        ercs = new Vector<NodeConstraints>(20);
    }

    private long uniqueIDCounter = 0;

    /**
     * Registers a node with the GP Params object. A node params object is taken from the node
     * which allows the node to be instantiated over and over by the tree builder.
     * @param n An instance of the node to register
     */
    public void registerNode(Node n) {
        registerNode(n, true, 1);
    }

    public void registerNode(Node n, boolean enabled) {
        registerNode(n, enabled, 1);
    }

    /**
     * Registers an ADF node. Since ADF nodes are created at runtime and not compile time they can't be
     * instantiated in the same way and so are registered using their ADFNodeParams object.
     */
    public void registerNode(NodeConstraints constraints) {
        nodes.add(constraints);
        totalNodes++;
        if (constraints.getType() == NodeConstraints.TERMINAL) terminalCount++;
        if (constraints.getType() == NodeConstraints.ERC) ercCount++;

        int[] returnTypes = constraints.getReturnTypes();
        for (int i = 0; i < returnTypes.length; i++) {
            addNodeToCache(constraints, returnTypes[i]);
        }
        
    }

    /**
     * Registers a node with the GP Params object. A node params object is taken from the node
     * which allows the node to be instantiated over and over by the tree builder. This overload
     * offers the additional constraint of specifying how many instances of this node must be included
     * in each individual.
     * @param n An instance of the node to register
     */
    public void registerNode(Node n, boolean enabled, double fitness) {
        uniqueIDCounter++;
        NodeConstraints constraints = n.createNodeConstraintsObject();

        constraints.registerUniqueID(uniqueIDCounter);
        constraints.setEnabled(enabled);
        constraints.setFitness(fitness);
        nodes.add(constraints);
        totalNodes++;
        if (n instanceof BasicERC) {	//POEY comment: ERC(Ephemeral Random Constant) contains constant values       	
            ercs.add(constraints);
            ercCount++;
        } else {        	
            if (constraints.getType() == NodeConstraints.TERMINAL) terminalCount++;
            if (constraints.getType() == NodeConstraints.ERC) ercCount++;  
        }

        int[] returnTypes = n.getReturnTypes();        
        for (int i = 0; i < returnTypes.length; i++) {
            addNodeToCache(constraints, returnTypes[i]);
        }

    }

    public void addNodeToCache(NodeConstraints n, int returnType) {
        NodeCache nodeCache = nodeCaches.get(returnType);
        if (nodeCache == null) {
            nodeCache = new NodeCache(returnType);
            nodeCaches.put(returnType, nodeCache);
        }
        nodeCache.registerNode(n);
    }



    /**
     * Removes all the ADF definitions from the GP Params object. This allows the ADFs to be replaced
     * with new ones. ADFs may be made programmatically, as an alternative to hard coding, or they may be
     * created automatically (see the Coevolution Problem for more specific detail)
     */
    public void clearADFs() {

        Vector<ADFNodeConstraints> toRemove = new Vector<ADFNodeConstraints>();

        for (int i = 0; i < nodes.size(); i++) {
            NodeConstraints nodeConstraints = nodes.elementAt(i);
            if (nodeConstraints instanceof ADFNodeConstraints) {
                toRemove.add((ADFNodeConstraints) nodeConstraints);
                totalNodes--;
                // remove it from the relevant node cache(s)
                Vector<NodeCache> nodeCaches = nodeConstraints.cacheMemberships;
                for (int j = 0; j < nodeCaches.size(); j++) {
                    NodeCache nodeCache = nodeCaches.elementAt(j);
                    nodeCache.deregisterNode(nodeConstraints);
                }
            }
        }

        nodes.removeAll(toRemove);

    }

    public NodeConstraints getERCByType(int returnType) {
        Vector<NodeConstraints> foundParams = new Vector<NodeConstraints>(10);
        for (int i = 0; i < ercs.size(); i++) {
            NodeConstraints nodeConstraints = ercs.elementAt(i);
            if (nodeConstraints.matches(returnType) && nodeConstraints.isEnabled()) foundParams.add(nodeConstraints);
        }

        if (foundParams.size() == 0)  {
            // its okay - for instance there are no ERCs that match the substatement type
            // which is sometimes requested by the tree optmiser.
            return null;
        }

        // now select a foundnode at random
        int index = (int) (Evolve.getRandomNumber() * foundParams.size());
        return foundParams.elementAt(index);
    }

    /**
     * Keeps the nodes arranged by return type. This saves having to search through
     * the whole list when looking for nodes of one type so is more efficient.
     */
    private Hashtable<Integer, NodeCache> nodeCaches = new Hashtable<Integer, NodeCache>(20);

    /**
     * New, faster method replaces the old node getting functions. This one has caching enabled so that
     * nodes are selected faster. It also selects the tournament selection technique for probabilistically
     * selecting terminals,
     * @param type The type of node you need - FUNCTION OR TERMINAL
     * @param returnType The return type required
     * @return A node constraints object of the chosen node.
     */
    public NodeConstraints getNode(int returnType, int type) {

        // get the probability cache for this return type
        NodeCache cache = nodeCaches.get(returnType);

        if (type == NodeConstraints.ANY) {      	
            // choose a random type using the cache
            type = cache.chooseNodeType(this);           
        } else {      	
            // Terminal in this case means either terminal or erc, so be more specific.
            if (type == NodeConstraints.TERMINAL) {
                if (Evolve.getRandomNumber() > terminalVsERCProbability) {
                    type = NodeConstraints.ERC;
                }
            }
        }

        // get the nodes of the chosen type
        Vector<NodeConstraints> nodes = cache.getNodes(type);

        // try to avoid situations where no nodes can be found
        if (nodes.size() == 0) {
            switch (type) {
                case NodeConstraints.TERMINAL:
                    // try ercs instead
                    type = NodeConstraints.ERC;
                    nodes = cache.getNodes(NodeConstraints.ERC);
                    break;
                case NodeConstraints.ERC:
                    type = NodeConstraints.TERMINAL;
                    nodes = cache.getNodes(NodeConstraints.TERMINAL);
                    break;
            }
        }

        // if there still arent' enough nodes.
        if (nodes.size() == 0)  {
            throw new RuntimeException("No nodes with return type: " + NodeConstraints.returnTypeToString(returnType) + " and type " + NodeConstraints.typeNames[type] + " have been registered.");
        }

        // Choose a node at random
        NodeConstraints chosenNode = nodes.elementAt((int) (Evolve.getRandomNumber() * nodes.size()));

        // Choose a node at random from these
        if (cache.hasVariableFitness(type)) {
            // use very basic tournament selection (t=2) to choose the nodes
            NodeConstraints anotherNode = nodes.elementAt((int) (Evolve.getRandomNumber() * nodes.size()));
            if (anotherNode.fitness > chosenNode.fitness) chosenNode = anotherNode;
        }

        return chosenNode;

    }

    /**
     * Gets a node with the given return type.
     * @deprecated
     */
/*    public NodeConstraints getNodeByType(int type) {

        // based on probability, choose which kind of node to return, and


        boolean chooseTerminalNotERC = Evolve.getRandomNumber() < terminalVsERCProbability;

        Vector<NodeConstraints> foundNodes = new Vector<NodeConstraints>(10);

        for (int i = 0; i < nodes.size(); i++) {
            NodeConstraints nodeConstraints = nodes.elementAt(i);
            if (nodeConstraints.returntype == type && nodeConstraints.isEnabled()) {
                switch (nodeConstraints.type)  {
                    case NodeConstraints.FUNCTION:
                        foundNodes.add(nodeConstraints);
                        break;
                    case NodeConstraints.TERMINAL:
                        if (chooseTerminalNotERC) foundNodes.add(nodeConstraints);
                        break;
                    case NodeConstraints.ERC:
                        if (!chooseTerminalNotERC) foundNodes.add(nodeConstraints);
                        break;
                }

            }
        }

        if (foundNodes.size() == 0)  {
            throw new RuntimeException("No nodes with return type: " + NodeConstraints.returnTypeToString(type) + " have been registered.");
        }

        // now select a node at random
        int index = (int) (Evolve.getRandomNumber() * foundNodes.size());
        return foundNodes.elementAt(index);

    }*/

    /**
     * Finds a terminal NodeParams object
     * @param returnType The return type that the Terminal must have
     * @param rangeType The range type that the terminal must have (use RangeTypes.DONT_CARE if you don't care)
     * @deprecated
     */
    /*public NodeConstraints getTerminalNodeByType(int returnType, int rangeType) {

        Vector<NodeConstraints> terminalNodes = new Vector<NodeConstraints>(10);
        Vector<NodeConstraints> ercs = new Vector<NodeConstraints>(10);

        for (int i = 0; i < nodes.size(); i++) {
            NodeConstraints nodeConstraints = nodes.elementAt(i);
            if (nodeConstraints.returntype == returnType && nodeConstraints.isEnabled() && (rangeType == RangeTypes.DONT_CARE || nodeConstraints.rangeType == rangeType) && nodeConstraints.childCount == 0) {
                switch (nodeConstraints.type)  {
                    case NodeConstraints.TERMINAL:
                        terminalNodes.add(nodeConstraints);
                        break;
                    case NodeConstraints.ERC:
                        ercs.add(nodeConstraints);
                        break;
                }
            }
        }

        boolean chooseTerminalNotERC = Evolve.getRandomNumber() < terminalVsERCProbability;

        Vector<NodeConstraints> foundNodes;
        if (chooseTerminalNotERC && terminalNodes.size() > 0)  {
            foundNodes = terminalNodes;
        } else {
            foundNodes = ercs; 
        }

        if (foundNodes.size() == 0)  {
            if (!ignoreTerminalWarnings) {
                throw new RuntimeException("No *terminal* nodes with return type: " + NodeConstraints.returnTypeToString(returnType) + " and range type: " + RangeTypes.rangeTypeToString(rangeType) + " have been registered. Consider removing ERCs which use this Range type");
            } else {
                return getNodeByType(returnType);
            }
        }

        // now select a node at random
        int index = (int) (Evolve.getRandomNumber() * foundNodes.size());
        return foundNodes.elementAt(index);

    }*/

    /**
     * Used to find only non terminal nodes. This is used by the FullBuilder which likes to
     * create trees to their maximum possible extent.
     * @deprecated
     */
    /*public NodeConstraints getNonTerminalNodeByType(int type) {

        Vector<NodeConstraints> foundNodes = new Vector<NodeConstraints>(10);

        for (int i = 0; i < nodes.size(); i++) {
            NodeConstraints nodeConstraints = nodes.elementAt(i);
            if (nodeConstraints.returntype == type && nodeConstraints.isEnabled() && (nodeConstraints.childCount > 0 || nodeConstraints instanceof ADFNodeConstraints)) foundNodes.add(nodeConstraints);
        }

        if (foundNodes.size() == 0)  {
            if (ignoreNonTerminalWarnings() ) {
                return getTerminalNodeByType(type, RangeTypes.DONT_CARE);
            } else {
                throw new RuntimeException("No *non terminal* nodes with return type: " + NodeConstraints.returnTypeToString(type) + " have been registered.");
            }
        }

        // now select a foundnode at random
        int index = (int) (Evolve.getRandomNumber() * foundNodes.size());
        return foundNodes.elementAt(index);

    }*/

    public boolean isNodeChildConstraintsEnabled() {
        return nodeChildConstraintsEnabled;
    }

    public void setNodeChildConstraintsEnabled(boolean nodeChildConstraintsEnabled) {
        this.nodeChildConstraintsEnabled = nodeChildConstraintsEnabled;
    }

    public boolean isDynamicSizeLimitingOn() {
        return dynamicSizeLimiting;
    }

    public void setDynamicSizeLimiting(boolean dynamicSizeLimiting) {
        this.dynamicSizeLimiting = dynamicSizeLimiting;
    }

    public void setDynamicSizeLimitingInitSize(int dynamicSizeLimitingInitSize) {
        this.dynamicSizeLimitingInitSize = dynamicSizeLimitingInitSize;
    }

    public void setDynamicSizeLimitingMaxNewWeight(double dynamicSizeLimitingMaxNewWeight) {
        this.dynamicSizeLimitingMaxNewWeight = dynamicSizeLimitingMaxNewWeight;
    }

    public int getDynamicSizeLimitingInitSize() {
        return dynamicSizeLimitingInitSize;
    }

    public double getDynamicSizeLimitingMaxNewWeight() {
        return dynamicSizeLimitingMaxNewWeight;
    }

    public int getCutoffSize() {
        return cutoffSize;
    }

    public void setCutoffSize(int cutoffSize) {
        this.cutoffSize = cutoffSize;
    }

    public int getCutoffDepth() {
        return cutoffDepth;
    }

    public void setCutoffDepth(int cutoffDepth) {
        this.cutoffDepth = cutoffDepth;
    }

    public int getReturnType() {
        return returnType;
    }

    public void setReturnType(int returnType) {
        this.returnType = returnType;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public boolean ignoreNonTerminalWarnings() {
        return ignoreNonTerminalWarnings;
    }

    public void setIgnoreNonTerminalWarnings(boolean ignoreNonTerminalWarnings) {
        this.ignoreNonTerminalWarnings = ignoreNonTerminalWarnings;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }
    
    public double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public double getERCmutateProbability() {
        return ERCmutateProbability;
    }

    public void setERCmutateProbability(double ERCmutateProbability) {
        this.ERCmutateProbability = ERCmutateProbability;
    }


    public Vector<NodeConstraints> getNodes() {
        return nodes;
    }

    public void setNodes(Vector<NodeConstraints> nodes) {
        this.nodes = nodes;
    }

    public int getEliteCount() {
        return eliteCount;
    }

    public void setEliteCount(int eliteCount) {
        this.eliteCount = eliteCount;
    }

    public boolean isOptimisationEnabled() {
        return Debugger.optimisationEnabled;
    }

    public void setOptimisationEnabled(boolean optimisationEnabled) {
        Debugger.optimisationEnabled = optimisationEnabled;
    }

    public double getERCjitterProbability() {
        return ERCjitterProbability;
    }

    public void setERCjitterProbability(double ERCjitterProbability) {
        this.ERCjitterProbability = ERCjitterProbability;
    }

    public double getPointMutationProbability() {
        return pointMutationProbability;
    }

    public void setPointMutationProbability(double pointMutationProbability) {
        this.pointMutationProbability = pointMutationProbability;
    }

    public double getADFSwapProbability() {
        return ADFSwapProbability;
    }

    public void setADFSwapProbability(double ADFSwapProbability) {
        this.ADFSwapProbability = ADFSwapProbability;
    }
}
