package jasmine.imaging.shapes;

import java.io.Serializable;
import java.util.Vector;

/**
 * A relatively basic data structure that contains all the information needed about a particular shape.
 * The class is Serializable which means that it can be saved to disk as part of a Jasmine Project. For this reason
 * the size of the class is kept as small as possible so the file size will be small.
 *
 * All the interesting functions which analyse the shape to return meaningful data about it are found in the
 * ExtraShapeData class.
 *
 * @author Olly Oechsle, University of Essex
 * @version 1.0 31-Jan-2007 Initial Version
 * @version 1.1 01-Jun-2007 All analysis takes place in ExtraShapeData.java instead which makes it faster to serialise and also
 * allows additional functions to be added without breaking the serialised version on the class.
 */
public class SegmentedShape implements Serializable {

    /**
     * The class label associated with this shape.
     */
    public int classID = -1;

    /**
     * The pixels which make up this shape
     */
    public Vector<ShapePixel> pixels = new Vector<ShapePixel>(100);

    /**
     * Pixels that are part of the edge are also kept in this separate vector.
     */
    public Vector<ShapePixel> edgePixels = new Vector<ShapePixel>(100);

    /**
     * The smallest X value of a pixel in the shape (on the Image coordinates)
     */
    public int minX = Integer.MAX_VALUE;

    /**
     * The largest X value of a pixel in the shape (on the Image coordinates)
     */
    public int maxX = Integer.MIN_VALUE;

    /**
     * The smallest Y value of a pixel in the shape (on the Image coordinates)
     */
    public int minY = Integer.MAX_VALUE;

    /**
     * The largest Y value of a pixel in the shape (on the Image coordinates)
     */
    public int maxY = Integer.MIN_VALUE;

    /**
     * The number of pixels that make up this shape
     */
    protected int totalPixels = 0;

    /**
     * The original value which is the commonality between all pixels in the image.
     */
    public int originalValue;
  
    /**
     * Constructs the segmented shape with the original value.
     */
    public SegmentedShape(int value) {
        this.originalValue = value;
    }
  
    public void add(int x, int y, boolean isEdge) {
    	
        add(x, y, 8, isEdge);
    }

    /**
     *
     *
     * Registers a pixel as being part of this image.
     * Note that pixel coordinates are relative to the image and not the shape so you don't need to worry about
     * translating the coordinates or anything.
     */
    public void add(int x, int y, int neighbours, boolean isEdge) {

        // find bounds
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;

        if (y < minY) minY = y;
        if (y > maxY) maxY = y;

        // add to vector
        ShapePixel p = new ShapePixel(x, y, isEdge);
        p.neighbours = neighbours;

        // save the pixel
        pixels.add(p);

        if (isEdge) {
            // use this vector to store edges in one location
            // thus the same pixel object in accessible from both places.
            edgePixels.add(p);
        }

        // count how many pixels there are too
        totalPixels++;

    }

    /**
     * Returns the number of pixels that make up this shape which gives a good approximation of its weight.
     * Its volume is harder to calculate as it may have hollows. See the getVolume() function in ExtraShapeData
     * for more information.
     */
    public int getMass() {    	
        return totalPixels;
    }

    /**
     * Returns the how many pixels are along the edge of the shape which is a reasonable indicator of its
     * perimeter, although this does not hold true for hollow shapes. To get the outer perimeter find the size of
     * the first Perimeter which is accessible via the GetPerimeters() function in ExtraShapeData.
     */
    public int getPerimeter() {
        return edgePixels.size();
    }

}
