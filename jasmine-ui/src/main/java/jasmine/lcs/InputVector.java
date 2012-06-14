package jasmine.lcs;

/**
 * Represents a basic input vector which gives information about that state of the
 * environment and is matched in some way by a condition. This abstract class
 * keeps the implementation of the vector deliberately vague so that different
 * representations (bit-string, s-expression) can be used.
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public abstract class InputVector {

    /**
     * Returns the size of the input in terms of the number of features it contains.
     */
    public abstract int getLength();

    /**
     * Returns the feature with the given index. This can be implemented however you like.
     */
    public abstract double getValue(int index);

}
