package jasmine.gp.params;

/**
 * Range Typing allows the GP system to match terminals to the most appropriate ERCs (or other terminals).
 *
 * <p>
 * For instance terminals
 * that only tend to output values between 0-1 are not likely to provide useful answers when compared to values
 * from an ERC that outputs numbers between 0-255.
 * </p>
 * <p>
 * This class enumerates the different range types, which an ERC must return in its getDefaultRangeType function.
 * </p>
 * @author Olly Oechsle, University of Essex, Date: 23-Feb-2007
 * @version 1.0
 */
public class RangeTypes {

    /**
     * When it doesn't matter or if range typing is disabled.
     */
    public static final int DONT_CARE = -1;

    /**
     * Values in the continuous range -1 to +1
     */
    public static final int RANGE_TINY_DOUBLE = 1;

    /**
     * Values in the continuous range 0-5
     */
    public static final int RANGE_SMALL_DOUBLE = 5;

    /**
     * Values in the range 0 to 10
     */
    public static final int RANGE_SMALL_INT = 2;

    /**
     * Continuous values between 0-1.
     */
    public static final int RANGE_PERCENTAGE = 3;

    /**
     * A value between 0-255
     */
    public static final int RANGE_8BIT_INT = 4;

    public static String rangeTypeToString(int rangeType) {
        switch (rangeType) {
            case DONT_CARE:
                return "Don't care";
            case RANGE_TINY_DOUBLE:
                return "Tiny Double";
            case RANGE_SMALL_DOUBLE:
                return "Small Double";
            case RANGE_SMALL_INT:
                return "Small Int";
            case RANGE_PERCENTAGE:
                return "Percentage";
            case RANGE_8BIT_INT:
                return "8 Bit Int";
        }
        return "Unknown range type";
    }
}
