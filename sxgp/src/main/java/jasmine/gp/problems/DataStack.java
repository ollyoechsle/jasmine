package jasmine.gp.problems;


import jasmine.gp.nodes.registers.Registers;
import jasmine.imaging.commons.HaarRegions;
import jasmine.imaging.commons.IntegralImage;
import jasmine.imaging.commons.PixelLoader;

import java.util.Hashtable;

/**
 * The Data Stack is passed to all nodes as they are being executed. It acts as memory for the application
 * and allows each node access to the data that they will be processing. In practical terms it contains details
 * of which x,y coordinates are being looked at, and a reference to the current training image. 
 *
 * @author Olly Oechsle, University of Essex, Date: 15-Jan-2007
 * @version 1.0
 */
public class DataStack {

    protected int width;
    protected int height;

    /**
     * Registers (gives the individual some memory)
     */
    public Registers registers;

    /**
     * The value of the stack as it is passed through the tree.
     */
    public double value;

    /**
     * Images if you're doing image processing of some kind
     */
    protected PixelLoader[] image;

    /**
     * A haar region, also for image processing
     */
    public HaarRegions haar;

    /**
     * An integral image.
     */
    public IntegralImage integralImage;

    /**
     * Other kinds of data can be stored here
     */
    public Object data1, data2;

    /**
     * Were any imaging functions used?
     */
    public boolean usesImaging = false;

    /**
     * Store features information here
     */
    public double[] features;

    // X and Y are commonly used in maths problems
    public int x;
    public int y;

    // All other common data is stored in this hashtable
    protected Hashtable<String, Object> values;

    public DataStack() {
        values = new Hashtable<String, Object>(10);
        this.value = 0d;
        this.image = null;
        registers = new Registers();
    }

    public void add(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        Object value = values.get(key);
        if (value == null) {
            throw new RuntimeException("No object exists in DataStack for key: " + key);
        } else {
            return value;
        }
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public HaarRegions getHaar() {
        usesImaging = true;
        return haar;
    }

    public void setHaar(HaarRegions haar) {
        this.haar = haar;
        this.width = haar.getWidth();
        this.height = haar.getHeight();
    }

    public PixelLoader getImage() {
        if (image == null) return null;
        usesImaging = true;
        return image[0];
    }

    public PixelLoader getImage(int index) {
        usesImaging = true;
        return image[index];
    }

    public void setImage(PixelLoader image) {
        if (this.image == null) {
            this.image = new PixelLoader[1];
        }
        this.image[0] = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void setImage(PixelLoader image, int index) {
        if (this.image == null) {
            this.image = new PixelLoader[index+1];
        }
        this.image[index] = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void setImages(PixelLoader[] images) {
        this.image = images;
        this.width = images[0].getWidth();
        this.height = images[0].getHeight();
    }

    public IntegralImage getIntegralImage() {
        return integralImage;
    }

    public void setIntegralImage(IntegralImage integralImage) {
        this.integralImage = integralImage;
    }

    public Object getData() {
        return data1;
    }

    public void setData(Object data) {
        this.data1 = data;
    }

    public Object getData2() {
        return data2;
    }

    public void setData2(Object data2) {
        this.data2 = data2;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
