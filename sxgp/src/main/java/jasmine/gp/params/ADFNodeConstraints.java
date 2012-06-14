package jasmine.gp.params;


import jasmine.gp.multiclass.TestResults;
import jasmine.gp.nodes.ADFNode;
import jasmine.gp.selection.Selectable;
import jasmine.gp.selection.Selector;
import jasmine.gp.tree.Node;
import jasmine.gp.util.DeepCopy;

import java.io.Serializable;

/**
 * <p>
 * A node params object that represents a tree of nodes as opposed to an individual originalNode.
 * Functions in the same way as a terminal - it has no children (as far as the originalNode builder is concerned
 * </p>
 *
 * <p>
 * An ADF Node params object should have a fitness which is reflectant in some way upon the fitness
 * of the individuals that it has contributed to.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 28-Feb-2007
 * @version 1.0
 */
public class ADFNodeConstraints extends NodeConstraints implements Selectable, Comparable, Serializable {

    protected int usages = 0;

    protected transient ADFNode node;

    public ADFNodeConstraints(ADFNode node, int[] returnTypes) {
        super("", returnTypes, 0, NodeConstraints.TERMINAL);
        // set the same ID as the node, so it can refer back.
        registerUniqueID(node.getID());
        // keep a reference to this node, we'll use clones of it to make new instances.
        this.node = node;
        resetFitness();
    }

    public Node getNode() {
        return node;
    }


    public void setNode(ADFNode node) {
        this.node = node;
    }


    /**
     * Returns the size of the object. If two selectables have the same fitness
     * then the selector will choose the smallest (supposedly more efficient) one.
     */
    public int getTreeSize() {
        if (node == null) {
            // Todo: FIX
            //throw new RuntimeException("ADF Node Constraints has lost its node");
        }
        return node != null? node.getTree().getTreeSize() : 0;
    }

    /**
     * What niche is this object a member of? Members of the same niche can be
     * selected by the tree builder, providing it is told to do so.
     */
    public int getIslandID() {
        return Selector.ANY_ISLAND;
    }

    /**
     * Gets the fitness of this ADF Node. This value is affected by the performance
     * of individuals to which the ADF originalNode is contributing.
     * @return
     */
    public double getKozaFitness() {
        return fitness;
    }

    /**
     * Sets the fitness of the originalNode param, which is usually the fitness of the
     * individual using this originalNode param. As the same originalNode param will probably be
     * used many time different strategies may be used to discover a fair average
     * measure of this originalNode's contribution to the overall fitness.
     * @param fitness
     */
    public void addFitness(double fitness) {
        // if this fitness is better (lower) than the existing fitness, replace
        if (fitness < this.fitness) this.fitness = fitness;
    }

    /**
     * Resets the fitness of the ADFNodeParams object to the worst value.
     */
    public void resetFitness() {
        // assign fitness the worst value
        fitness = Double.MAX_VALUE;
    }

    public void setUsages(int usages) {
        this.usages = usages;
    }

    public void addUsage() {
        this.usages++;
    }

    public int getUsages() {
        return usages;
    }

    public int compareTo(Object o) {
        if (o instanceof ADFNodeConstraints)  {
            double otherFitness = ((ADFNodeConstraints) o).fitness;
            if (otherFitness > fitness) return -1;
            if (otherFitness < fitness) return +1;
            return 0;
        } throw new RuntimeException("Non ADFNodeParam object in classifier population");
    }

    /**
     * Instantiates an ADF node. Since the logic in an ADF node is created at runtime and not
     * compile time it cannot be instantiated by reflecting an existing class, rather it makes
     * a (deep) clone of the original node. The node is given a reference to this NodeParams class
     * so that it can be identified later if it is present in a successful individual.
     * @return A new node, or null if the copying operation fails.
     */
    public ADFNode getInstance() {
        try {
            return (ADFNode) new DeepCopy().copy(node);
        } catch (Exception e) {
            System.err.println("Could not copy ADF Node: " + node);
        }
        return null;
    }

    /**
     * Test Results class holds the results of the classifier tested against the training data.
     * It provides an equals method that allows us to determine if the classifier is different to
     * any other classifier.
     */
    protected TestResults testResults = null;

    /**
     * Test Results class holds the results of the classifier tested against the training data.
     * It provides an equals method that allows us to determine if the classifier is different to
     * any other classifier.
     */
    public TestResults getTestResults() {
        return testResults;
    }

    /**
     * Test Results class holds the results of the classifier tested against the training data.
     * It provides an equals method that allows us to determine if the classifier is different to
     * any other classifier.
     */    
    public void setTestResults(TestResults testResults) {
        this.testResults = testResults;
    }

    /**
     * Jitters the ADF node - see if we can get any better results
     */
    public void jitter() {
        node.jitter();
    }

    public String toString() {
        return "ADF: " + getID() + ", Fitness: " + fitness + ", Usages: " + usages;
    }

    /**
     * Copies the object. This is a bit of a hack really.
     * @return
     */
    public ADFNodeConstraints copy() {
        ADFNodeConstraints c = (ADFNodeConstraints) new DeepCopy().copy(this);
        c.returnTypes = this.returnTypes;
        c.node = (ADFNode) new DeepCopy().copy(this.node);
        return c;
    }

    public ADFNodeConstraints() {
        // private constructor for copying
        super("", null, 0, NodeConstraints.FUNCTION);
    }



}
