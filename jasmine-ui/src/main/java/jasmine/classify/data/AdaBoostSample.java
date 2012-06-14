package jasmine.classify.data;

import java.io.Serializable;

/**
 * A sample consists of a pieces of training data and a class label.
 * When used in training you have to set the label, when used during
 * delployment you don't need to set the label in the constructor -
 * the solution should then find it for you!
 *
 * You can call the test() method on AdaBoost against a set of samples
 * with known class labels to test the solution you create.
 *
 * @author Olly Oechsle, University of Essex, Date: 04-May-2007
 * @version 1.0
 */
public class AdaBoostSample implements Serializable {

    protected Object data;
    protected int label;

    public AdaBoostSample(Object data) {
        this(data, -1);
    }

    public AdaBoostSample(Object data, int label) {
        this.data = data;
        this.label = label;
    }

    public Object getData() {
        return data;
    }

    public int getLabel() {
        return label;
    }

}

