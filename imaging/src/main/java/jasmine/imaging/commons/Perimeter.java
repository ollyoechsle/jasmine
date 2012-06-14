package jasmine.imaging.commons;



import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.ShapePixel;

import java.util.ArrayList;

/**
 * Provides statistics about a single-pixel-thickness set of pixels
 * which are a certain radius from the central pixel. The statistics are:
 * mean, standard deviation and edge count. Because this set of pixels
 * is the circular, they will provide the same output even when the image
 * is rotated, which makes it useful for detecting a set of objects which are not
 * all facing the same way.
 * <p/>
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version,
 * provided that any use properly credits the author.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details at http://www.gnu.org
 * </p>
 * <p/>
 * @author Olly Oechsle, University of Essex, Date: 04-Jul-2007
 * @version 1.1
 */
public class Perimeter implements AbstractShape {

    public static final int MEAN = 0;
    public static final int VARIANCE = 1;
    public static final int EDGE_COUNT = 2;

    public double[][] mean;
    public double[][] stddev;
    public int[] edgeCount;

    protected ArrayList<Pixel> mask;
    protected int[] maskX, maskY;
    protected int c;

    public Perimeter(int radius) {

        // Discretised but faster...
        int steps = Math.min(radius, 50);

        maskX = new int[steps];
        maskY = new int[steps];

        c = 0;

        double angleStep = (Math.PI * 2) / steps;
        for (int i = 0; i < steps; i++) {
            double angle = angleStep * i; // two pi radians in a circle
            int dx = (int) (radius * Math.cos(angle));
            int dy = (int) (radius * Math.sin(angle));
            maskX[c] = dx;
            maskY[c] = dy;
            c++;
        }

    }

    public FastStatistics getStatistics(PixelLoader image, int x, int y) {

        FastStatistics statistics = new FastStatistics();

        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;

        for (int i = 0; i < c; i++) {

            int px = x + maskX[i];

            if (px < 0) continue;
            if (px >= width - 1) continue;

            int py = y + maskY[i];

            if (py < 0) continue;
            if (py >= height - 1) continue;

            statistics.addData(image.getGreyValue(px, py));

        }

        return statistics;

    }
    
    //POEY
    public FastStatistics getStatistics(PixelLoader image, ExtraShapeData img, int x, int y) {

        FastStatistics statistics = new FastStatistics();

        int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;

        for (int i = 0; i < c; i++) {

            int px = x + maskX[i];

            if (px < 0) continue;
            if (px >= boundingWidth - 1) continue;

            int py = y + maskY[i];

            if (py < 0) continue;
            if (py >= boundingHeight - 1) continue;

            statistics.addData(image.getGreyValue(px, py));

        }

        return statistics;

    }

    public float getMean(PixelLoader image, int x, int y) {

        float total = 0;
        int n = 0;

        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;
        //POEY comment: c = steps => either 2 or 5
        for (int i = 0; i < c; i++) {

            int px = x + maskX[i];

            if (px < 0) continue;
            if (px >= width - 1) continue;

            int py = y + maskY[i];

            if (py < 0) continue;
            if (py >= height - 1) continue;

            total += image.getGreyValue(px, py);
            n++;

        }
        return total / n;

    }

    //POEY
    public float getMean(PixelLoader image, ExtraShapeData img) {

        float total;
        int n;
        FastStatistics solver = new FastStatistics();
        int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = img.array[x][y];
                if (p != null) {
                	total = 0;
                	n = 0;
			        //POEY comment: c = steps => either 2 or 5
			        for (int i = 0; i < c; i++) {
                	
			            int px = p.x + maskX[i];
			
			            if (px < 0) continue;
			            if (px >= boundingWidth - 1) continue;
			
			            int py = p.y + maskY[i];
			
			            if (py < 0) continue;
			            if (py >= boundingHeight - 1) continue;
			
			            total += image.getGreyValue(px, py);
			            n++;

                    }
			        if(n!=0)
			        	solver.addData(total / n);
                }
        	}
        }
        return solver.getMean();

    }
    
    public float getStdDeviation(PixelLoader image, int x, int y) {
        return getStatistics(image, x, y).getStandardDeviation();
    }
    
    //POEY
    public float getStdDeviationMean(PixelLoader image, ExtraShapeData img) {
    	float value;
    	FastStatistics solver = new FastStatistics();
    	int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = img.array[x][y];
                if (p != null) {
                	value = getStatistics(image, img, p.x, p.y).getStandardDeviationCheck();
                	if(value != -1)	//if value = NaN then return -1
                		solver.addData(value);
                }
            }
    	}
        return solver.getMean();
    }

    public int getEdgeCount(PixelLoader image, int x, int y) {

        double halfImageStandardDeviation = image.getStdDeviation() / 2;

        int previous = -1;

        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;
        int edges = 0;

        for (int i = 0; i < c; i++) {

            int px = x + maskX[i];

            if (px < 0) continue;
            if (px >= width - 1) continue;

            int py = y + maskY[i];

            if (py < 0) continue;
            if (py >= height - 1) continue;

            int value = image.getGreyValue(px, py);

            if (previous == -1) {
                previous = value;
            } else {
                if (Math.abs(value - previous) > halfImageStandardDeviation) {
                    edges++;
                    previous = value;
                }
            }

        }

        return edges;

    }
    
    //POEY
    public float getEdgeCountMean(PixelLoader image, ExtraShapeData img) {

        double halfImageStandardDeviation = img.getStdDeviation() / 2;

        int previous = -1;
        FastStatistics solver = new FastStatistics();
    	int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;
        int edges;

    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = img.array[x][y];
                if (p != null) {
                	edges = 0;
			        for (int i = 0; i < c; i++) {
			
			            int px = p.x + maskX[i];
			
			            if (px < 0) continue;
			            if (px >= boundingWidth - 1) continue;
			
			            int py = p.y + maskY[i];
			
			            if (py < 0) continue;
			            if (py >= boundingHeight - 1) continue;
			
			            int value = image.getGreyValue(px, py);
			
			            if (previous == -1) {
			                previous = value;
			            } else {
			                if (Math.abs(value - previous) > halfImageStandardDeviation) {
			                    edges++;
			                    previous = value;
			                }
			            }
			
			        }

			        solver.addData(edges);
                }
            }
    	}
    	return solver.getMean();

    }


}
