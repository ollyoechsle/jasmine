package jasmine.imaging.core;

/**
 * Data structure to record statistics about a certain class in a Jasmine Project.
 *
 * @author Olly Oechsle, University of Essex, Date: 19-Apr-2007
 * @version 1.0
 */
public class JasmineClassStatistics {

    public JasmineClass c;
    public int instances;
    public int images;

    public JasmineClassStatistics(JasmineClass c, int instances, int images) {
        this.c = c;
        this.instances = instances;
        this.images = images;
    }
}
