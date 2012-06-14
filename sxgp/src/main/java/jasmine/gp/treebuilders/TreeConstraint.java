package jasmine.gp.treebuilders;

/**
 * Represents a constraint that individual nodes are entitled
 * to place upon the tree.
 * 
 * @author Olly Oechsle, University of Essex, Date: 27-Oct-2008
 * @version 1.0
 */
public class TreeConstraint {

    public static final int AT_LEAST_ONE_TERMINAL_WITH_TYPE = 1;

    public int mode;
    public int value;

    public TreeConstraint(int mode, int value)  {
        this.mode = mode;
        this.value = value;
    }
    
}
