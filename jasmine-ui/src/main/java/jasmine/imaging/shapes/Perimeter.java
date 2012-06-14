package jasmine.imaging.shapes;


import jasmine.imaging.commons.Pixel;

import java.util.Vector;
import java.util.Stack;
import java.io.Serializable;

/**
 * A perimeter counts as a set of edge pixels. Class also records the area of the
 * bounding box which is useful to figure out which perimeters are inside others
 *
 * @author Olly Oechsle, University of Essex, Date: 02-Feb-2007
 * @version 1.0
 */
public class Perimeter implements Serializable {

    Vector<ShapePixel> pixels;

    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;

    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;

    int width, height;
    int area;
    int pixelsInsidePerimeter;

    public Perimeter(ShapePixel firstNeighbour) {
        pixels = new Vector<ShapePixel>(100);
        add(firstNeighbour);
    }

    public void add(ShapePixel p) {

        // find bounds
        if (p.x < minX) minX = p.x;
        if (p.x > maxX) maxX = p.x;

        if (p.y < minY) minY = p.y;
        if (p.y > maxY) maxY = p.y;

        pixels.add(p);

    }

    public void compile() {
        width = (maxX - minX) + 1;
        height = (maxY - minY) + 1;
        area = width * height;
        pixelsInsidePerimeter = countPixelsInsidePerimeter();
        // perimeter wouldn't exist if there were none, so ensure something is returned.
        if (pixelsInsidePerimeter == 0) pixelsInsidePerimeter = 1;
    }

    public int getArea() {
        return area;
    }

    public int getPixelsInsidePerimeter() {
        return pixelsInsidePerimeter;
    }

    private static final int ALREADY_CHECKED = 2;
    private static final int EDGE = 1;

    private int floodsize;

    int[][] array;

    private int countPixelsInsidePerimeter() {

        // create a spatial array around the bounds of the perimeter
        // but give it a one pixel border all the way round so we can
        // fully flood the outside just by starting at the point 0,0.
        array = new int[width + 2][height + 2];

        // insert the edges into the array
        for (int i = 0; i < pixels.size(); i++) {
            ShapePixel p = pixels.elementAt(i);
            array[(p.x - minX) + 1][(p.y - minY) + 1] = EDGE;
        }

        // now flood, from the top right corner - this will get the area OUTSIDE the perimeter
        // to get the interior area:
        // interior area = outer area - number_of_perimeter_pixels - borders

        Stack<Pixel> floodStack = new Stack<Pixel>();
        floodStack.add(new Pixel(0, 0));
        floodsize = 1;
        while (floodStack.size() > 0) {
            flood(array, floodStack);
        }


        int borderSize = ((width + 2) * 2) + ((height + 2) * 2) - 4;

        return area - (floodsize - borderSize) - pixels.size();

    }

    private void flood(int[][] array, Stack<Pixel> floodStack) {

        Pixel p = floodStack.pop();
        int x = p.x;
        int y = p.y;

        array[x][y] = ALREADY_CHECKED;

        // only go up down, left right, otherwise it'd breach
        // the perimeter through diagonal gaps.
        Pixel[] neighbourLocations = new Pixel[4];
        neighbourLocations[0] = new Pixel(0, -1); // n
        neighbourLocations[1] = new Pixel(1, 0); // e
        neighbourLocations[2] = new Pixel(0, 1); // s
        neighbourLocations[3] = new Pixel(-1, 0); // w

        for (int i = 0; i < neighbourLocations.length; i++) {
            Pixel neighbourLocation = neighbourLocations[i];

                int dX = x + neighbourLocation.x;
                int dY = y + neighbourLocation.y;

                if (dX < 0) continue;
                if (dY < 0) continue;
                if (dX > width + 1) continue;
                if (dY > height + 1) continue;

                if (array[dX][dY] == 0)  {
                    array[dX][dY] = ALREADY_CHECKED;
                    floodStack.add(new Pixel(dX,dY));
                    floodsize++;
                }

            }
        }

    public void fillIn(ExtraShapeData es) {

        SegmentedShape s = es.getShape();

        // TODO - does it update the edgepixels array??? - NO!

        for (int y = 0; y < (height + 1); y++)  {
            for (int x = 0; x < (width + 1); x++) {
                if (array[x][y] == 0) {
                    // this pixel needs to be filled in
                    if (es.array[x + (this.minX - s.minX) - 1][y + (this.minY - s.minY) - 1] == null) {
                        ShapePixel p =  new ShapePixel(x + minX - 1, y + minY - 1);
                        s.pixels.add(p);
                        es.array[x + (this.minX - s.minX) - 1][y + (this.minY - s.minY) -  1] = p;
                    }
                }
            }            
        }

    }

}
