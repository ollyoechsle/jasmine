package jasmine.imaging.commons;

import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.ShapePixel;


/**
 * Gets statistics along a line
 *
 * @author Olly Oechsle, University of Essex, Date: 05-Jul-2007
 * @version 1.1 Fixed some rather serious bugs and made it run faster.
 */
public class Line implements AbstractShape {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    protected int length, orientation;

    protected int edgeCount = -1;
    
    protected int edgeCountObject = -1;

    public Line(int height, int orientation) {

        this.length = height;
        this.orientation = orientation;

    }

//    public static void main(String[] args) throws Exception {
//        PixelLoader image = new PixelLoader("/home/ooechs/Desktop/Lenna.png");
//        StatisticsSolver s = new StatisticsSolver(11);
//        for (int t = 0; t <= 10; t++) {
//            long start = System.currentTimeMillis();
//            for (int i = 0; i <= 307200; i++) {
//                //image.getPerimeter2().getStatistics(image, 50, 50).getMean();
//                //image.getHLine1().getStatistics(image, 50, 50).getMean();
//                image.getHLine1().getMean(image, 50, 50);
//            }
//            long end = System.currentTimeMillis();
//            long time = end - start;
//            if (t > 0) s.addData(time);
//            System.out.println(time);
//        }
//
//        System.out.println(s.getMean() + " | " + s.getStandardDeviation());
//    }

    public FastStatistics getStatistics(PixelLoader image, int x, int y) {

        double halfImageStandardDeviation = image.getStdDeviation() / 2;

        FastStatistics solver = new FastStatistics();

        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;

        int edges = 0;
        int previous = -1;

        if (orientation == HORIZONTAL) {
            for (int i = -length; i < length; i++) {
                int px = x + i;

                if (px < 0) continue;
                if (px >= width - 1) continue;

                int value = image.getGreyValue(px, y);
                solver.addData(value);
                if (previous == -1) {
                    previous = value;
                } else {
                    if (Math.abs(value - previous) > halfImageStandardDeviation) {
                        edges++;
                        previous = value;
                    }
                }
            }
        }

        if (orientation == VERTICAL) {
            for (int i = -length; i < length; i++) {

                int py = y + i;

                if (py < 0) continue;
                if (py >= height - 1) continue;

                int value = image.getGreyValue(x, py);
                solver.addData(value);
                if (previous == -1) {
                    previous = value;
                } else {
                    if (Math.abs(value - previous) > halfImageStandardDeviation) {
                        edges++;
                        previous = value;
                    }
                }

            }
        }

        edgeCount = edges;

        return solver;

    }
    
    //POEY
    public FastStatistics getStatistics(PixelLoader image, ExtraShapeData img, int x, int y, double halfObjectStandardDeviation) {

        FastStatistics solver = new FastStatistics();

    	int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;

        int edges = 0;
        int previous = -1;

        if (orientation == HORIZONTAL) {
            for (int i = -length; i < length; i++) {
                int px = x + i;

                if (px < 0) continue;
                if (px >= boundingWidth - 1) continue;

                int value = image.getGreyValue(px, y);
                solver.addData(value);
                if (previous == -1) {
                    previous = value;
                } else {
                    if (Math.abs(value - previous) > halfObjectStandardDeviation) {
                        edges++;
                        previous = value;
                    }
                }
            }
        }

        if (orientation == VERTICAL) {
            for (int i = -length; i < length; i++) {

                int py = y + i;

                if (py < 0) continue;
                if (py >= boundingHeight - 1) continue;

                int value = image.getGreyValue(x, py);
                solver.addData(value);
                if (previous == -1) {
                    previous = value;
                } else {
                    if (Math.abs(value - previous) > halfObjectStandardDeviation) {
                        edges++;
                        previous = value;
                    }
                }

            }
        }

        edgeCountObject = edges;

        return solver;

    }


    public float getMean(PixelLoader image, int x, int y) {

        float total = 0;
        int n = 0;

        int width = image.getWidth() - 1;
        int height = image.getHeight() - 1;

        if (orientation == HORIZONTAL) {
        	//length = radius
            for (int i = -length; i < length; i++) {
                int px = x + i;

                if (px < 0) continue;
                if (px >= width - 1) continue;

                total += image.getGreyValue(px, y);
                n++;
            }
        }

        if (orientation == VERTICAL) {
            for (int i = -length; i < length; i++) {
                int py = y + i;

                if (py < 0) continue;
                if (py >= height - 1) continue;

                total += image.getGreyValue(x, py);
                n++;
            }
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
			        if (orientation == HORIZONTAL) {
			        	//length = radius
			            for (int i = -length; i < length; i++) {
			                int px = p.x + i;
			
			                if (px < 0) continue;
			                if (px >= boundingWidth - 1) continue;
			
			                total += image.getGreyValue(px, p.y);
			                n++;
			            }
			        }
			
			        if (orientation == VERTICAL) {
			            for (int i = -length; i < length; i++) {
			                int py = p.y + i;
			
			                if (py < 0) continue;
			                if (py >= boundingHeight - 1) continue;
			
			                total += image.getGreyValue(p.x, py);
			                n++;
			            }
			        }			
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
    	double halfObjectStandardDeviation = img.getStdDeviation() / 2;
    	int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = img.array[x][y];
                if (p != null) {
                	value = getStatistics(image, img, p.x, p.y, halfObjectStandardDeviation).getStandardDeviationCheck();
                	if(value != -1)	//if value = NaN then return -1
                		solver.addData(value);
                }
            }
    	}
        return solver.getMean();
    }

    public int getEdgeCount(PixelLoader image, int x, int y) {
        getStatistics(image, x, y);
        return edgeCount;
    }
    
    //POEY
    public float getEdgeCountMean(PixelLoader image, ExtraShapeData img) {
    	FastStatistics solver = new FastStatistics();
    	double halfObjectStandardDeviation = img.getStdDeviation() / 2;
    	int boundingWidth = img.getWidth() - 1;
        int boundingHeight = img.getHeight() - 1;
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = img.array[x][y];
                if (p != null) {
                	getStatistics(image, img, x, y, halfObjectStandardDeviation);
                }               
                solver.addData(edgeCountObject);
            }
    	}
    	return solver.getMean();
    }

}
