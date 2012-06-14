package jasmine.gp.treebuilders;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.params.GPParams;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeUtils;

import java.util.Vector;

/**
 * SXGP 2008 Main Tree Builder.
 *
 * @author Olly Oechsle, University of Essex, Date: 23-Jan-2007
 * @version 1.0
 */
public class TreeBuilder {

    

    DepthSelector s;

    public TreeBuilder(GPParams params) {
            s = new RandomDepthSelector(params);
    }

    public void generatePopulation(Individual[] population, GPParams params, int generation) {

        for (int i = 0; i < population.length; i++) {
            population[i] = generateIndividual(params);
            //POEY comment: can print population at generation 0 here
            //System.err.println("Individual "+i+": "+population[i].toString()+"\n"+population[i].toJava());            
        }
        if (params.usesHereditaryRepulsion()) {
            // each individual needs to have its own unique identifier, to identify its ancestry
            for (int i = 0; i < population.length; i++) {
                Individual individual = population[i];
                individual.createUniqueID(generation, i);
                individual.initAncestry();
            }
        }
    }

    public Individual generateIndividual(GPParams params) {
        Node[] trees = new Node[params.getTreeCount()];
        //POEY comment: trees.length=1       
        for (int i = 0; i < trees.length; i++) {
            trees[i] = createTree(params);        
        }
        return new Individual(trees, params.getReturnType());
    }

    /**
     * Creates a tree for an individual
     * A fully functioning tree, using the appropriate algorithm
     */
    public Node createTree(GPParams params) {

        int maxDepth = params.getMaxTreeDepth();

        int chosenMode = params.getTreeBuilder();

        switch (chosenMode) {
      
            case GPParams.RAMPED_HALF_AND_HALF:		//POEY comment: go to this case always
                // find the ranges of depths that we can use
                int depthRange = maxDepth - params.getMinTreeDepth() + 1;
                // choose what the max depth should be
                //POEY comment: jasmine.gp.treebuilders.RandomDepthSelector.java
                maxDepth = s.selectDepth(params);//params.getMinTreeDepth() + (int) (Evolve.getRandomNumber() * depthRange);
                // alternate between grow and full modes
                chosenMode = Evolve.getRandomNumber() > 0.5 ? GPParams.GROW : GPParams.FULL;
        }

        this.executionOrder = 0;

        Node tree = buildTree(params.getReturnType(), 0, maxDepth, params, chosenMode);

        // ensure the tree meets up to certain criteria
        if (params.isTreeCheckingEnabled()) checkTree(tree, params);

        return tree;

    }

    /**
     * Creates a replacement for a branch in the tree.
     */
    public Node produceMutatedTree(Node originalBranch, GPParams params) {

        this.executionOrder = originalBranch.executionOrder;

        int maxDepth = Math.min(originalBranch.getTreeDepth(), params.getMaxTreeDepth());

        int chosenMode = params.getTreeBuilder();

        switch (chosenMode) {
            case GPParams.RAMPED_HALF_AND_HALF:
                // find the ranges of depths that we can use
            	//POEY comment: getMinTreeDepth() = 1
                int depthRange = maxDepth - params.getMinTreeDepth();
                // choose what the max depth should be
                //POEY comment: maxDepth is between 1 and 6
                maxDepth = s.selectDepth(params);//maxDepth = params.getMinTreeDepth() + (int) (Evolve.getRandomNumber() * depthRange);
                // choose between grow and full modes
                chosenMode = Evolve.getRandomNumber() > 0.5 ? GPParams.GROW : GPParams.FULL;
        }    

        Node tree =  buildTree(originalBranch.getParentsExpectedType(params), 0, maxDepth, params, chosenMode);

        // ensure the tree meets up to certain criteria
        if (params.isTreeCheckingEnabled())     	checkTree(tree, params);	//POEY comment:  not this case       

        return tree;

    }

    int executionOrder = 0;

    public Node buildTree(int returnType, int currentDepth, int maxDepth, GPParams params, int mode) {

        Node n;

        if (currentDepth >= maxDepth) {       		
            // return a random terminal
            n = params.getNode(returnType, NodeConstraints.TERMINAL).getInstance();

            if (currentDepth > maxDepth) {
                System.out.println("Current depth (" + currentDepth + ") > " + maxDepth);
            }

            n.executionOrder = executionOrder;
            this.executionOrder++;

        } else {

            if (mode == GPParams.GROW) {
                // grow builder
                n = params.getNode(returnType, NodeConstraints.ANY).getInstance();              
            } else {
                // full builder
                n = params.getNode(returnType, NodeConstraints.FUNCTION).getInstance();
            }

            n.executionOrder = executionOrder;
            this.executionOrder++;

            if (n.countChildren() > 0) {
                for (int i = 0; i < n.countChildren(); i++) {
                    n.addChild(buildTree(n.getChildType(i), currentDepth + 1, maxDepth, params, mode), i);
                }
            }

        }

        return n;

    }


    /**
     * Does tree checking to make sure that the tree makes sense
     */
    public void checkTree(Node tree, GPParams params) {



        // extract all the nodes from the tree as a serialised vector
        Vector<Node> nodes = TreeUtils.getNodes(tree, GPParams.ANY_RETURN_TYPE);

        // Establish the execution order
        for (int i = 0; i < nodes.size(); i++) {
            Node node =  nodes.elementAt(i);
            node.executionOrder = i + 1;
        }

        // go through each one and see whether its constraints have ben met
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.elementAt(i);
            TreeConstraint constraint = node.getTreeConstraint();
            boolean changed = false;
            if (constraint != null) {

                // this node imposes a constraint
                switch (constraint.mode) {
                    case TreeConstraint.AT_LEAST_ONE_TERMINAL_WITH_TYPE:
                        // the tree must have at least one child with a given type.
                        int requiredType = constraint.value;

                        // see whether it does contain this type and fulfils the criterion
                        Vector<Node> childNodes = TreeUtils.getNodes(node, GPParams.ANY_RETURN_TYPE);
                        boolean fulfilsCriterion = false;

                        for (int j = 0; j < childNodes.size(); j++) {
                            Node child =  childNodes.elementAt(j);
                            if (child.returnTypeMatches(requiredType)) fulfilsCriterion = true;
                        }

                        if (!fulfilsCriterion) {
                            // it does not fulfil the criterion - therefore we need to swap one of the nodes
                            // for a suitable one.
                            Node newNode = params.getNode(requiredType, NodeConstraints.TERMINAL).getInstance();
                            // Choose a node from the tree at random, preferably a terminal
                            Vector<Node> terminals = new Vector<Node>();
                            for (int j = 0; j < childNodes.size(); j++) {
                                Node child = childNodes.elementAt(j);
                                if (child.isTerminal()) terminals.add(child);
                            }

                            // almost certainly will be
                            if (terminals.size() > 0)  {
                               // Choose a random terminal
                                Node t = terminals.elementAt((int) (Evolve.getRandomNumber() * terminals.size()));
                                t.replaceMyselfWith(newNode);
                                changed = true;
                            } else {
                                System.err.println("TreeBuilder.checkTree: Terminals size: 0");
                            }

                        }

                }


                if (changed) {
                    // tree has changed - we need to start over.
                    checkTree(tree, params);
                    break;
                }

            }


        }


    }

}
