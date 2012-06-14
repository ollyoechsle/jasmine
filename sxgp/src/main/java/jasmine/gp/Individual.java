package jasmine.gp;


import jasmine.gp.multiclass.PCM;
import jasmine.gp.problems.DataStack;
import jasmine.gp.selection.Selectable;
import jasmine.gp.selection.Selector;
import jasmine.gp.tree.Node;
import jasmine.gp.util.JavaWriter;

import java.io.*;
import java.util.Vector;

/**
 * Represents an individual in SXGP. This is the combination of an
 * execution tree and other associated characteristics, such as
 * the fitness of the individul (if it has been evaluated). It also
 * contains a few utility methods that allow the tree to be manipulated.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0 Initial Version
 */
public class Individual implements Selectable, Comparable, Serializable, Cloneable {

    /**
     * The individual's tree, the root of which is of course a node object
     */
    protected Node[] trees;

    /**
     * How big am I? Cached version of the tree size
     */
    protected transient int cachedSize;

    /**
     * My fitness
     */
    protected double fitness;

    /**
     * An alternative fitness metric - not used by evolution in any way
     */
    protected double alternativeFitness;

    /**
     * How many hits did I get?
     */
    protected int hits;

    /**
     * How many mistakes did I make?
     */
    protected int misses;

    /**
     * The individual's program classification map
     */
    protected PCM[] pcms;

    /**
     * Custom values, for your own applications.
     */
    protected double customValue1, customValue2;

    /**
     * What data type do I return?
     */
    protected int returnType;

    /**
     * Am I a member of a niche?
     */
    protected int islandID = Selector.ANY_ISLAND;

    /**
     * Has the individual been evaluated yet?
     */
    protected transient boolean hasBeenEvaluated = false;

    /**
     * For checking the ancestry
     */
    protected Double uniqueID = null;
    protected Vector<Double> ancestry = null;

    /**
     * Initialises the individual with a pre-made tree and return type
     */
    public Individual(Node[] trees, int returnType) {
        if (trees != null) {
            setTrees(trees);
            //POEY comment: trees.length = 1            
            pcms = new PCM[trees.length];
        } else {
            pcms = new PCM[1];
        }
        this.fitness = 0;
        this.hits = 0;
        this.misses = 0;
        this.cachedSize = 0;
        this.returnType = returnType;
    }

    public Individual copy() throws CloneNotSupportedException {
        // a deep clone
        Individual newInd = (Individual) clone();

        // make new arrays, so that the references are distinct
        newInd.trees = new Node[trees.length];
        newInd.pcms = new PCM[pcms.length];

        // reset transient properties
        newInd.hasBeenEvaluated = false;
        newInd.cachedSize = 0;
        newInd.islandID = islandID;

        // copy the tree array
        for (int x = 0; x < newInd.trees.length; x++) {
            newInd.trees[x] = (trees[x].copy(newInd));  // force a deep clone
        }

        return newInd;
    }

    /**
     * Custom values, for your own applications.
     */
    public double getCustomValue1() {
        return customValue1;
    }

    /**
     * Custom values, for your own applications.
     */
    public void setCustomValue1(double customValue1) {
        this.customValue1 = customValue1;
    }

    /**
     * Custom values, for your own applications.
     */
    public double getCustomValue2() {
        return customValue2;
    }

    /**
     * Custom values, for your own applications.
     */
    public void setCustomValue2(double customValue2) {
        this.customValue2 = customValue2;
    }

    /**
     * Returns whether the individual has been evaluated
     */
    public boolean hasBeenEvaluated() {
        return hasBeenEvaluated;
    }

    /**
     * Sets whether the individual has been evaluated
     */
    public void setHasBeenEvaluated(boolean hasBeenEvaluated) {
        this.hasBeenEvaluated = hasBeenEvaluated;
    }

    /**
     * Gets the niche ID of this individual, all individuals e a nicheID
     * of -1 by default.
     *
     * @return
     */
    public int getIslandID() {
        return islandID;
    }

    /**
     * Sets the nicheID of this individual. Crossover and mutation will only
     * occur between individuals of the same niche.
     *
     * @param islandID Set a nicheID of -1 for this individual to be member of any niche.
     */
    public void setIslandID(int islandID) {
        this.islandID = islandID;
    }

    /**
     * Gets the individuals program classification map which is used to
     * translate the floating point output of the program into classes.
     */
    public PCM getPCM() {
        return pcms[0];
    }

    public PCM getPCM(int index) {
        return pcms[index];
    }

    public PCM[] getPCMs() {
        return pcms;
    }

    /**
     * Sets the individual's program classification map which is used to
     * translate the floating point output of the program into classes.
     *
     * @param pcm
     */
    public void setPCM(PCM pcm, int index) {
        this.pcms[index] = pcm;
    }

    /**
     * @param pcm
     */
    public void setPCM(PCM pcm) {
        setPCM(pcm, 0);
    }

    /**
     * Gets the individual's return type which is usually that defined by the appropriate GP Params object.
     */
    public int getReturnType() {
        return returnType;
    }

    public int getTreeSize() {
        int totalSize = 0;
        if (trees != null) {
            if (cachedSize == 0) {
                for (int i = 0; i < trees.length; i++) {
                    cachedSize += getTreeSize(i);
                }
            }
            totalSize = cachedSize;
        }
        return totalSize;
    }

    public int getTreeDepth() {
        if (trees != null) {
            return trees[0].getTreeDepth();
        } else {
            return 0;
        }
    }

    /**
     * Parses the individual's tree to count how many nodes it has. Note that ADFs count as one node.
     * Note that size is cached to reduce computation. The cache is transient so gets wiped
     * each time the invidual is copied.
     */
    public int getTreeSize(int treeIndex) {
        Node tree = trees[treeIndex];
        if (tree != null) {
            return tree.getTreeSize();
        }
        return 0;
    }

    /**
     * Gets the individual's execution tree
     */
    public Node[] getTrees() {
        return trees;
    }

    public Node getTree(int index) {
        return trees[index];
    }

    public void setTree(int index, Node tree) {
        trees[index] = tree;
    }

    public int getTreeCount() {
        return trees.length;
    }

    /**
     * Sets the individual's execution tree.
     */
    public void setTrees(Node[] trees) {
        this.trees = trees;
        // give the top level node a reference to this individual.
        // this is for tree optimisation purposes if that originalNode chooses
        // to replace itself - it then has a reference to a parent
        // to do the swapping.
        for (int i = 0; i < trees.length; i++) {
            Node tree = trees[i];
            tree.root = this;
            tree.index = i;
        }
    }

    /**
     * Hits is related to fitness but only measures the successes of the individual (the
     * true positive (TP) results). As fitness tends toward zero (perfect), hits will tend
     * to increase.
     */
    public int getHits() {
        return hits;
    }

    /**
     * Hits is related to fitness but only measures the successes of the individual (the
     * true positive (TP) results). As fitness tends toward zero (perfect), hits will tend
     * to increase.
     *
     * @param hits An int value counting the number of instances where this individual returns the correct answer.
     */
    public void setHits(int hits) {
        this.hits = hits;
    }

    private transient int mistakes = 0;

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    /**
     * Gets the fitness of the individual. This being Koza fitness, the closer
     * to zero the number is the better the individual.
     *
     * @return
     */
    public double getKozaFitness() {
        return fitness;
    }

    /**
     * Sets the individuals fitness. This being Koza fitness, the closer
     * to zero the number is the better the individual.
     *
     * @param fitness A float value representing fitness. 0 is ideal.
     */
    public void setKozaFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * An alternative fitness metric - not used by evolution in any way
     */
    public double getAlternativeFitness() {
        return alternativeFitness;
    }

    /**
     * An alternative fitness metric - not used by evolution in any way
     */
    public void setAlternativeFitness(double alternativeFitness) {
        this.alternativeFitness = alternativeFitness;
    }

    /**
     * Finds all nodes in an individual with a particular return type
     */
    public Vector<Node> getNodesByReturnType(int treeIndex, int returnType) {
        Vector<Node> nodes = new Vector<Node>(10);
        getNodesByReturnType(trees[treeIndex], nodes, returnType);
        return nodes;
    }

    private void getNodesByReturnType(Node tree, Vector<Node> foundNodes, int returnType) {
        if (tree.returnTypeMatches(returnType)) {
            foundNodes.add(tree);
        }
        for (int i = 0; i < tree.countChildren(); i++) {
            getNodesByReturnType(tree.child[i], foundNodes, returnType);
        }
    }

    /**
     * Executes the individual's tree
     *
     * @param stack A stack which contains information that some of the nodes need to know, such as a reference to the current image.
     */
    public double execute(DataStack stack) {
        return execute(stack, 0);
    }

    public double execute(DataStack stack, int treeIndex) {
        this.trees[treeIndex].execute(stack);
        return stack.value;
    }

    /**
     * Compares this individual to another individual based on fitness, allows the individuals
     * to be ordered using Collections.Sort() so that the best individual is at the top.
     */
    public int compareTo(Object o) {
        if (o instanceof Individual) {
            double otherFitness = ((Individual) o).getKozaFitness();
            if (otherFitness > fitness) return -1; // I am better
            if (otherFitness < fitness) return +1; // I am worse
            // if it has the same fitness, then take into account the size of each individual
            int mySize = getTreeSize();
            int otherSize = ((Individual) o).getTreeSize();
            if (otherSize > mySize) return -1; // I am better
            if (otherSize < mySize) return +1;
            // there's nothing significantly different about us
            return 0;
        }
        throw new RuntimeException("Non individual in population");
    }

    public String toString() {
        //return "sxGP Individual, fitness: " + fitness + ", hits: " + hits + ", size: " + getTreeSize() + ", depth: " + "ID: " + uniqueID;
    	//POEY
    	return "sxGP Individual, fitness: " + fitness + ", hits: " + hits + ", size: " + getTreeSize() + ", depth: " + getTreeDepth() + ", ID: " + uniqueID;
    }

    /**
     * Converts the individual into Java Code
     *
     * @return
     */
    public String toJava() {
        return JavaWriter.toJava(this);
    }

    public String toLisp() {
        return trees[0].toLisp();
    }

    /**
     * Converts the individual into Java Code
     *
     * @return
     */
    public String toJava(String methodSignature) {
        return JavaWriter.toJava(this, methodSignature);
    }

    /**
     * If the individual is to be completely removed from the population, it can
     * be assigned the worst possible fitness, which is Integer.MAX_VALUE
     */
    public void setWorstFitness() {
        setKozaFitness(Integer.MAX_VALUE);
    }

    /**
     * For hereditary repulsion
     */
    public void createUniqueID(int generation, int populationIndex) {
        uniqueID = generation + (populationIndex/10000d);
    }

    public void initAncestry() {
        ancestry = new Vector<Double>(20);
    }

    public int getAncestrySimilarity(Individual other) {
        int similarity = 0;
        for (Double id : ancestry) {
            if (other.ancestry.contains(id)) similarity++;
        }
        return similarity;
    }

    public double getUniqueID() {
        return uniqueID;
    }

    /**
     * Adds the ancestry of the parent to the vector
     */
    public void updateAncestry(Individual parent) {
        for (Double id : parent.ancestry) {
            if (!ancestry.contains(id)) {
                ancestry.add(id);
            }
        }
        // and add the unique ID of the parent too
        ancestry.add(parent.uniqueID);
    }        

    public void addAncestor(double ancestorID) {
        ancestry.add(ancestorID);
    }

    public void purgeAncestors(int beforeGen) {
        Vector<Double> newAncestors = new Vector<Double>();
        for (Double id : ancestry) {
            if (id > beforeGen) {
                newAncestors.add(id);
            }
        }
        ancestry = newAncestors;
    }

    /**
     * Saves the individual to disk in serialised form. This is primarily
     * to allow the JavaWriter output to be compared with the actual individual
     * in a debugging environment.
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
     * Loads a serialised individual from disk. Mainly used for debugging the
     * Java Writer.
     */
    public static Individual load(File f) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream in = new ObjectInputStream(fis);
        Individual individual = (Individual) in.readObject();
        in.close();
        return individual;
    }

}
