package jasmine.gp.tree;

//import jasmine.gp.treeanimator.AnimatedNode;

import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.nodes.ADFNode;
import jasmine.gp.nodes.Return;
import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.nodes.ercs.BoolERC;
import jasmine.gp.params.GPParams;
import jasmine.gp.treeanimator.AnimatedNode;

import java.util.Vector;

/**
 * A collection of utilities used for manipulating trees.
 * @author Olly Oechsle, University of Essex, Date: 25-Jun-2007
 * @version 1.0
 */
public class TreeUtils {

    /**
     * Part of the GUI allows the trees to animate themselves. This class converts
     * a regular tree into one that can be animated by it.
     * @param ind
     * @return
     */
    public static AnimatedNode getAnimatedTree(Individual ind) {

        Node n = ind.getTree(0);

        AnimatedNode root = new AnimatedNode(n.getShortName());

        getAnimatedTree(n, root);

        return root;

    }

    /*
     * Part of the GUI allows the trees to animate themselves. This class converts
     * a regular tree into one that can be animated by it.
     * @param ind
     * @return
     */
    private static void getAnimatedTree(Node n1, AnimatedNode n2) {

        // look at all the children of n1
        for (int i = 0; i < n1.child.length; i++) {
            Node child1 = n1.child[i];
            AnimatedNode child2 = new AnimatedNode(child1.getShortName());
            n2.add(child2);

            getAnimatedTree(child1, child2);

        }

    }

    /**
     * @param bias Between 1-0. High values favours any subtree, lower values favour the
     * larger nodes closer to the root of the tree
     */
    public static Node getRandomSubtree(Node root, int returnType) {
        return getRandomNode(getNodes(root, returnType));
    }



    /**
     * Serialises nodes into a vector
     */
    public static Vector<Node> getNodes(Node root, int returnType) {
        Vector<Node> nodes = new Vector<Node>(10);
        nodes.clear();
        getNodes(root, nodes, 0, returnType);
        return nodes;
    }

    /**
     * Serialises nodes into a vector, from which we can choose a node at random
     * Certain conditions determine which nodes may be added to the vector.
     */
    private static void getNodes(Node node, Vector<Node> nodes, int depth, int returnType)  {

      // don't add the root node, otherwise crossover would just be swapping the individuals over
      // so only add if...
      if (depth > 0) {
          if (returnType == GPParams.ANY_RETURN_TYPE || node.returnTypeMatches(returnType)) {
              nodes.add(node);              
          }
      }

      for (int i = 0; i < node.countChildren(); i++) {
          Node child = node.child[i];
          getNodes(child, nodes, depth + 1, returnType);
      }

    }

    /**
     * Returns a random node from a list of nodes
     */
    private static Node getRandomNode(Vector<Node> nodes) {
        if (nodes.size() == 0) return null;
        double randomness = Evolve.getRandomNumber();
        return nodes.elementAt((int) (nodes.size() * randomness));
    }

    /**
     * Finds all the ADF nodes in this individual
     * @return A vector of the ADF nodes in the individual
     */
    public static Vector<ADFNode> getADFNodes(Node tree) {
        Vector<ADFNode> foundNodes = new Vector<ADFNode>(10);
        searchForADFs(tree, foundNodes);
        return foundNodes;
    }

    private static void searchForADFs(Node tree, Vector<ADFNode> foundNodes) {
        if (tree instanceof ADFNode) {
            foundNodes.add((ADFNode) tree);
        }
        for (int i = 0; i < tree.countChildren(); i++) {
            searchForADFs(tree.child[i], foundNodes);
        }
    }

    /**
     * Finds all the ERCs in this individual.
     * This does not include ReturnClass ERCs.
     */
    public static Vector<BasicERC> getERCs(Node tree) {
        Vector<BasicERC> foundERCs = new Vector<BasicERC>(10);
        searchForERCs(tree, foundERCs);
        return foundERCs;
    }

    private static void searchForERCs(Node tree, Vector<BasicERC> foundNodes) {
        if (tree instanceof BasicERC && !(tree instanceof Return) && !(tree instanceof BoolERC)) {       	
            foundNodes.add((BasicERC) tree);
        }
        for (int i = 0; i < tree.countChildren(); i++) {
            searchForERCs(tree.child[i], foundNodes);
        }
    }

}
