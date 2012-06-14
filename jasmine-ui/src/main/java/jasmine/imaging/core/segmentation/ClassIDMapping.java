package jasmine.imaging.core.segmentation;

/**
 * Allows the user to override Jasmine classes and either disable ones
 * that shouldn't be solved or change its class output such that two can be combined
 * together.
 *
 * @author Olly Oechsle, University of Essex, Date: 25-Feb-2008
 * @version 1.0
 */
public class ClassIDMapping {

    /**
     * The class ID of the corresponding jasmine class
     */
    public int jasmineClassID;

    /**
     * The class ID for the segmenter to output. Will most likely be the same as jasmineclassID
     */
    public int newClassID;

    public ClassIDMapping(int jasmineClassID, int newClassID) {
        this.jasmineClassID = jasmineClassID;
        this.newClassID = newClassID;
    }


    public int getJasmineClassID() {
        return jasmineClassID;
    }

    public int getNewClassID() {
        return newClassID;
    }

}
