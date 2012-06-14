package jasmine.gp;


import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.GPParams;
import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeUtils;
import jasmine.gp.treebuilders.TreeBuilder;

import java.util.Vector;

/**
 * <p>
 * Mutation - changes something about an individual randomly
 * </p>
 *
 * <p>
 * SXGP supports point mutation and ERC mutation.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 16-Jan-2007
 * @version 1.0
 */
public class Mutation {

    /**
     * Performs point mutation on part of the individual's tree.
     */
    public static void pointMutate(TreeBuilder t, Node tree, GPParams params) {

        //int oldSize = ind.getTreeSize();

        // pick a random subtree
        Node randomSubTree = TreeUtils.getRandomSubtree(tree, GPParams.ANY_RETURN_TYPE);

        // small individuals may not have subtrees, behave gracefully
        if (randomSubTree == null) {
            OperationCounter.FAILED_MUTATION_COUNT++;
            return;
        }

        // create an equivalent mutated tree using whatever tree builder Params tells us to use
        //POEY comment: create a new tree
        Node newTree = t.produceMutatedTree(randomSubTree, params);

        // replace sub tree
        //POEY comment: the subtree is replaced by the new tree
        randomSubTree.getParent().replaceChild(randomSubTree, newTree);

        // record the operation
        OperationCounter.POINT_MUTATION_COUNT++;

        //int newSize = ind.getTreeSize();

        //if (newSize > params.getMaxTreeSize()) {
            //System.out.println("MUTATION: Individual is now: " + newSize + "(was " + oldSize + ")");
        //}

    }

    /**
     * Mutates the ERCs of a tree (replaces one with a new value), rather than changing its structure
     * This is done probabalistically such that the chance of one ERC or ADF being changed is 1.     
     */
    public static void mutateERCs(Node tree, GPParams params) {

    	//POEY comment: put numeric nodes of the tree into ercs
        Vector<BasicERC> ercs = TreeUtils.getERCs(tree);
        //Vector<ADFNode> adfNodes = TreeUtils.getADFNodes(tree);

        int mutations = 0;

        double probability = 1d / ercs.size();

        for (int i = 0; i < ercs.size(); i++) {
            BasicERC erc =  ercs.elementAt(i);
            if (Evolve.getRandomNumber() <= probability) {
            	//POEY comment: change a number into the numeric node
                erc.mutate();
                mutations++;
            }

        }

         // TODO: Removed - doesn't work - causes null pointer exceptions
/*        for (int i = 0; i < adfNodes.size(); i++) {
            ADFNode adfNode = adfNodes.elementAt(i);

            if (Evolve.getRandomNumber() <= probability) {
                adfNode.replaceMyselfWith(params.getNode(adfNode.getReturnType(), NodeConstraints.ANY).getInstance());
                mutations++;
            }

        }*/

        if (mutations == 0) {
            OperationCounter.FAILED_MUTATION_COUNT++;
        } else {
            OperationCounter.ERC_MUTATION_COUNT++;
        }

    }


    /**
     * Jitters the ERCs in a tree (replaces one with a new value), rather than changing its structure
     * This is done probabalistically such that the chance of one ERC being changed is 1.
     */
    public static void jitterERCs(Node tree, GPParams params) {

        Vector<BasicERC> ercs = TreeUtils.getERCs(tree);

        double probability = 1d / ercs.size();

        int mutations = 0;

        for (int i = 0; i < ercs.size(); i++) {
            BasicERC erc =  ercs.elementAt(i);

            if (Evolve.getRandomNumber() <= probability) {
                erc.jitter();
                mutations++;
            }

        }

        if (mutations == 0) {
            OperationCounter.FAILED_MUTATION_COUNT++;
        } else {
            OperationCounter.ERC_JITTER_COUNT++;
        }        

    }


}
