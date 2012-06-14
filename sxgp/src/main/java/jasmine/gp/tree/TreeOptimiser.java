package jasmine.gp.tree;

import jasmine.gp.Individual;
import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.GPParams;
import jasmine.gp.params.NodeConstraints;

/**
 * <p>
 * Optimises individuals' trees. It is quite simple. If a subtree always returns
 * the same value (which is recorded by a node's debugger) then that tree can be replaced
 * with a single ERC containing that value. This helps to eliminate much of the spurious
 * arithmetic that can arise in certain situations.
 * </p>
 * <p>
 * At the same time it is possible to see if the same value is returned by a <i>terminal<i>
 * this indicates that the terminal is no use on this particular training data and should be removed.
 * </p>
 * @author Olly Oechsle, University of Essex, Date: 16-Jan-2007
 * @version 1.0
 */
public class TreeOptimiser {

    //public static int nodesSaved = 0;

    public static void optimise(Individual ind, GPParams params) {

        // as each node is executed, basic usage statistics are gathered
        // about what it has been returning. We can use this information to
        // delete parts of a tree, or whether to use it in crossover.
        //int beforeSize = ind.getTreeSize();
        optimise(ind.getTree(0), params);
        //int afterSize = ind.getTreeSize();

        //nodesSaved += (beforeSize - afterSize);

        //System.out.println("OPTIMISE: " + beforeSize + " -> " + afterSize);

    }

    private static void optimise(Node node, GPParams params) {

        // only optimise nodes that allow optimisation.
        if (node.isOptimisable()) {

            // see if the node returns different values (ie does it classify anything?)
            if (node.debugger.alwaysTheSame()) {

                // if this node is a terminal, then this is an interesting situation, because the terminal
                // is not providing any useful data.
                if (node instanceof Terminal) {
                    System.err.println("Terminal " + (node.toString()) + " may not be useful for this training data: Value is always the same");
                }

                // replace it with a constant instead - see if there is one available
                NodeConstraints suitableERC = params.getERCByType(node.getParentsExpectedType(params));
                if (suitableERC != null) {
                    jasmine.gp.nodes.ercs.BasicERC basicErc = (BasicERC) suitableERC.getInstance();
                    basicErc.setValue(node.debugger.getLastValue());
                    // okay - now replace the node with the erc
                    Node parent = node.getParent();
                    if (parent != null) {
                        parent.replaceChild(node, basicErc);
                        return;
                    }
                }

            }

            if (node.debugger.neverExecuted()) {
                // Replace with an empty constant
/*                Node parent = node.getParent();
                if (parent != null) {
                    parent.replaceChild(node, new FixedValueTerminal(0));
                    return;
                }*/
            }

            // run the originalNode's own optimisation routines too
            node = node.optimise();

        }

        for (int i = 0; i < node.countChildren(); i++) {
            optimise(node.child[i], params);
        }

    }

}
