package jasmine.imaging.commons;


/**
 * <p/>
 * All the shape functions should implement this interface, just makes
 * sure that they are all compatible and logical.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 07-Dec-2007
 * @version 1.0
 */
public interface AbstractShape {

    public FastStatistics getStatistics(PixelLoader image, int x, int y);
    
    //public float getMean(PixelLoader image, int x, int y);

}
