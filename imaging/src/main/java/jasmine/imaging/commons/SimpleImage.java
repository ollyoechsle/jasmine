package jasmine.imaging.commons;

/**
 *
 * A simple interface that allows us to get some information about
 * an image object whose class cannot be determined. Essentially,
 * the ImageClassificationProblem needs to know the image's dimensions,
 * and possibly the name of the file.
 *
 * @author Olly Oechsle, University of Essex, Date: 19-Oct-2006
 * @version 1.0
 */
public interface SimpleImage {

    public int getWidth();
    public int getHeight();
    public String getFilename();

}

