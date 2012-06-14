package jasmine.gp;

import jasmine.gp.params.GPParams;
import jasmine.gp.tree.Node;

/**
 * Performs crossover on two trees in a way that is compatible
 * with strong typing.
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class StandardCrossover implements Crossover {

    private Selector s;
    
    public int maxDepth = 5;

    public StandardCrossover() {
        s = new KozaNodeSelector();
    }

    /**
     * How many parents does the crossover operator require?
     */
    public int getParentCount() {
        return 2;
    }

    public final Node[] produceOffspring(GPParams params, Node maleTree, Node femaleTree) {

        // get random subtree from the male individual
        //Node maleNode = TreeUtils.getRandomSubtree(maleTree, GPParams.ANY_RETURN_TYPE);
    	
    	//POEY comment: jasmine.gp.KozaNodeSelector.java
        Node maleNode = s.select(maleTree, GPParams.ANY_RETURN_TYPE);


        if (maleNode == null || maleNode.getTreeDepth() > maxDepth) {
            OperationCounter.FAILED_CROSSOVER_COUNT++;
            return null;
        }

        // get one from female, but ensure they match (in terms of return type AND size)
        //Node femaleNode = TreeUtils.getRandomSubtree(femaleTree, maleNode.getParentsExpectedType(params));
        Node femaleNode = s.select(femaleTree, maleNode.getParentsExpectedType(params));

        if (femaleNode == null || femaleNode.getTreeDepth() > maxDepth) {
            OperationCounter.FAILED_CROSSOVER_COUNT++;
            return null;
        }

        // save parents (replaceChild updates them)
        Node maleParent = maleNode.getParent();
        Node femaleParent = femaleNode.getParent();

        // elegant debugging
        if (maleParent == null) System.err.println("MALE PARENT IS NULL");
        if (femaleParent == null) System.err.println("FEMALE PARENT IS NULL");

        if (maleTree.getTreeSize() < 10 && femaleTree.getTreeSize() < 10) {
  /*          System.out.println("Male Tree");
            System.out.println(maleTree.toLisp());
            System.out.println("Female Tree");
            System.out.println(femaleTree.toLisp());
            System.out.println("Male Node");
            System.out.println(maleNode.toLisp());
            System.out.println("Female Node");
            System.out.println(femaleNode.toLisp());*/
        }

        // swap subtrees
        
        //POEY comment: print parents
        //System.err.println("\nmaleParent Before:"+maleParent); 
        //for(int i=0;i<maleParent.child.length;i++)
        	//System.err.print(maleParent.child[i].toJava());
        //System.err.println();
        
        maleParent.replaceChild(maleNode, femaleNode);
        
        //System.err.println("maleParent After:"+maleParent); 
        //for(int i=0;i<maleParent.child.length;i++)
        	//System.err.print(maleParent.child[i].toJava());
        //System.err.println("\n");
        //System.err.println("femaleParent Before:"+femaleParent); 
        //for(int i=0;i<femaleParent.child.length;i++)
        	//System.err.print(femaleParent.child[i].toJava());
        //System.err.println();
        
        femaleParent.replaceChild(femaleNode, maleNode);
        
        //System.err.println("femaleParent After:"+femaleParent); 
        //for(int i=0;i<femaleParent.child.length;i++)
        	//System.err.print(femaleParent.child[i].toJava());
        //System.err.println();

        OperationCounter.CROSSOVER_COUNT+=2;

		if (maleTree.getTreeSize() < 10 && femaleTree.getTreeSize() < 10) {
		/*                    System.out.println("Male Tree");
		            System.out.println(maleTree.toLisp());
		            System.out.println("Female Tree");
		            System.out.println(femaleTree.toLisp());*/
		}

        return new Node[]{maleTree,femaleTree};

    }




}
