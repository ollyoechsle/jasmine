package jasmine.imaging.shapes;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;

import java.util.Vector;
import java.util.Stack;
import java.awt.image.BufferedImage;

/**
 * <p>
 * This program takes an array and creates a number of shapes (SegmentedShape objects that is)
 * according to their "colour" (class label).
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 31-Jan-2007
 * @version 1.0
 */
public class Grouper {

    private Stack<Pixel> pixelsToGroup;
    private Pixel[] neighbourLocations;

    public Grouper() {
        pixelsToGroup = new Stack<Pixel>();
        // move around the ring (twice)
        neighbourLocations = new Pixel[16];
        neighbourLocations[0] = new Pixel(0, -1); // n
        neighbourLocations[1] = new Pixel(+1, -1); // ne
        neighbourLocations[2] = new Pixel(1, 0); // e
        neighbourLocations[3] = new Pixel(+1, +1); // se
        neighbourLocations[4] = new Pixel(0, 1); // s
        neighbourLocations[5] = new Pixel(-1, +1); // sw
        neighbourLocations[6] = new Pixel(-1, 0); // w
        neighbourLocations[7] = new Pixel(-1, -1); // nw
        neighbourLocations[8] = new Pixel(0, -1); // n
        neighbourLocations[9] = new Pixel(+1, -1); // ne
        neighbourLocations[10] = new Pixel(1, 0); // e
        neighbourLocations[11] = new Pixel(+1, +1); // se
        neighbourLocations[12] = new Pixel(0, 1); // s
        neighbourLocations[13] = new Pixel(-1, +1); // sw
        neighbourLocations[14] = new Pixel(-1, 0); // w
        neighbourLocations[15] = new Pixel(-1, -1); // nw
    }

    /**
     * Needed for bounds checking
     */
    protected int width, height;

    /**
     * Creates the shapes from a mask image.
     */
    public Vector<SegmentedShape> getShapes(BufferedImage image) {
        int[][] array = new int[image.getWidth()][image.getHeight()];
        // first thing to do is to push the whole image into an editable array
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                array[x][y] = image.getRGB(x, y);
            }
        }
        return getShapes(array);
    }

    /**
     * Gets Extra Shape data about shapes that can immediately be used for shape analysis.
     */
    public Vector<ExtraShapeData> getShapesWithData(BufferedImage image) {
        Vector<SegmentedShape> shapes = getShapes(image);
        Vector<ExtraShapeData> extraShapeData = new Vector<ExtraShapeData>(shapes.size());
        for (int i = 0; i < shapes.size(); i++) {
            SegmentedShape segmentedShape = shapes.elementAt(i);
            extraShapeData.add(new ExtraShapeData(segmentedShape, new PixelLoader(image)));
        }
        return extraShapeData;
    }

    /**
     * Takes an image and cuts it up into a number of regions, each one defined
     * as a SegmentedShape object.
     * This raw data can then be used for whatever purpose you have. The ExtraShapeData
     * class offers many functions that can analyse the shape in detail.
     */
    public Vector<SegmentedShape> getShapes(int[][] array) {

        this.width = array.length;
        this.height = array[0].length;

        Vector<SegmentedShape> shapes = new Vector<SegmentedShape>(5);

        // to ensure a pixel isn't added twice
        boolean[][] addedAlready = new boolean[this.width][this.height];

        // now go through the array and start grouping
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int val = array[x][y];
                //POEY comment: if it is an object's pixel
                if (val != 0 && !addedAlready[x][y]) {
                    // create a new group
                    try {
                        SegmentedShape s = new SegmentedShape(val);

                        pixelsToGroup.add(new Pixel(x, y));
                        while (pixelsToGroup.size() > 0) group(array, addedAlready, s, val);

                        if (s.pixels.size() > 1) {
                            shapes.add(s);
                        }

                    } catch (Exception e) {

                        System.out.println("Could not create shape.");

                    }
                }
            }
        }

        return shapes;

    }



    /**
     * Recursive function to populate a group
     */
    private void group(int[][] array, boolean[][] addedAlready, SegmentedShape s, int val) {

        Pixel p = pixelsToGroup.pop();

        int x = p.x;
        int y = p.y;

        // clear from the array (so it isn't added more than once)
        addedAlready[x][y] = true;

        // count neighbours
        int nonDiagonallyAdjacentNeighbours = 0;
        int neighbourCount = 0;

        // check neighbours
        if (x > 0 && y > 0 && x < width - 1 && y < height - 1) {
            for (int dY = -1; dY <= 1; dY++)
                for (int dX = -1; dX <= 1; dX++) {
                    if (dX == 0 && dY == 0) continue;
                    if (array[x + dX][y + dY] == val) {
                        if (!addedAlready[x + dX][y + dY]) {
                            addedAlready[x + dX][y + dY] = true;
                            pixelsToGroup.add(new Pixel(x + dX, y + dY));
                        }
                        neighbourCount++;
                    } else {
                        if (dY == 0 || dX == 0) {
                            nonDiagonallyAdjacentNeighbours++;
                        }
                    }
                }
        }

        boolean isEdge = nonDiagonallyAdjacentNeighbours > 0;

        // we need to remove anomalous edge pixels - that is ones that stick out a little bit
        // so are technically on the edge, but actually mess up the perimeter finding algorithms later
        // on. These pixels are distinguished by the fact that they are adjacent to five or more "non shape"
        // pixels BUT all of those neighbours are connected to eachother. Essentially we need to look
        // in a ring around this pixel and see if 5 or more are connected

        // only applies to edges which is defined as...
        if (isEdge)  {

            int chainLength = 0;
            int maxChainLength = 0;

            for (int i = 0; i < neighbourLocations.length; i++) {
                Pixel neighbourLocation = neighbourLocations[i];
                    if (array[x + neighbourLocation.x][y + neighbourLocation.y] != val) {
                    chainLength++;
                    if (chainLength > maxChainLength) maxChainLength = chainLength;
                } else {
                    chainLength = 0;
                }
            }

            // don't add the pixel as an edge
            if (maxChainLength >= 6)  {
                //isEdge = false;
                // don't add the pixel at all
                return;
            }

        }

        // add pixel to the group
        // we work out if a pixel is on an edge by looking at its neighbour count. Each
        // pixel may have 8 neighbours, so one with 8 is completely enclosed. One with 7 has only
        // one non-neighbour, therefore is surrounded by pixels on all other sides so isn't on the edge, rather it
        // is diagonally connected to the edge. One with 6 neighbours is connected to two non-neighbours,
        // this may make it a legitimate edge, or they may both be diagonally opposite. If this is the case then
        // its not an edge.

        s.add(x, y, neighbourCount, isEdge);


    }

}
