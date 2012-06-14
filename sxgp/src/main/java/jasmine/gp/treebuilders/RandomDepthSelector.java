package jasmine.gp.treebuilders;

import jasmine.gp.Evolve;
import jasmine.gp.params.GPParams;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 14-Apr-2009
 * Time: 10:15:38
 * To change this template use File | Settings | File Templates.
 */
public class RandomDepthSelector implements DepthSelector {

    protected int depthRange;
    protected int minDepth;

    public RandomDepthSelector(GPParams params) {
        minDepth = params.getMinTreeDepth();
        int maxDepth = params.getMaxTreeDepth();
        // find the ranges of depths that we can use
        depthRange = maxDepth - params.getMinTreeDepth() + 1;
    }

    public int selectDepth(GPParams params) {
        // choose what the max depth should be
    	//POEY comment: minDepth = 1  depthRange = 6    	
        return minDepth + (int) (Evolve.getRandomNumber() * depthRange);
    }
    
}
