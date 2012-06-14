//package jasmine.imaging.shapes;
//
//
//import ColourChannels;
//import ColourConvertor;
//import FastStatistics;
//import FastStatisticsGrey;
//import IntegralImage;
//import Pixel;
//import PixelLoader;
//
//import java.util.Vector;
//import java.util.Stack;
//import java.util.Hashtable;
//import java.awt.image.BufferedImage;
//import java.awt.*;
//
///**
// * This class contains the methods for calculating a variety of different measures about a shape.
// * The raw information about the shape (ie which pixels are part of the shape) are stored in instances
// * of SegmentedShape.
// *
// * @author Olly Oechsle, University of Essex, Date: 25-Apr-2007
// * @version 1.0
// */
//
//
//public final class ExtraShapeDataTemp {
//	
//	//POEY
//    public static boolean CACHING_OTHERS = true;
//    public static boolean CACHE_RGB_HSL = true;
//    
//	/**
//     * These points represent the point where there is an end. You would assume a hand shape to have five ends.
//     * They are discovered following skeletonisation.
//     */
//    protected Vector<Pixel> ends;
//
//    /**
//     * These points represent the points where there is a joint. They are discovered
//     * following skeletonisation.
//     */
//    protected Vector<Pixel> joints;
//
//    /**
//     * The ExtraShapeData class is a wrapper around the SegmentedShape class, which is a lightweight, serializable
//     * class that only contains only the most basic shape information (the pixels that make up the shape).
//     */
//    protected SegmentedShape s;
//
//    protected PixelLoader image;
//
//    /**
//     * Initialises the object with a reference to the shape about which analysis is required.
//     */
//    public ExtraShapeData(SegmentedShape s, PixelLoader image) {
//        this.s = s;
//        this.image = image;
//        compile();
//    }
//
//    public PixelLoader getImage() {
//        return image;
//    }
//
//    /**
//     * Returns the shape data that this class wraps around.
//     */
//    public SegmentedShape getShape() {
//        return s;
//    }
//
//    /**
//     * Draws the shape onto an image so that you can visualise it in a GUI or elsewhere.
//     */
//    public void draw(BufferedImage img, int color) {
//        for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                    img.setRGB(p.x, p.y, color);
//                }
//            }
//        }
//    }
//
//    /**
//     * Draws the shape onto an image so that you can visualise it in a GUI or elsewhere.
//     */
//    public BufferedImage toImage() {
//        int color = Color.WHITE.getRGB();
//        BufferedImage image = new BufferedImage(boundingWidth, boundingHeight, BufferedImage.TYPE_INT_ARGB);
//        for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                    image.setRGB(x, y, color);
//                }
//            }
//        }
//        return image;
//    }
//
//    public BufferedImage getImageDepth() {
//        int maxDepth = getMaxDepth();
//        BufferedImage image = new BufferedImage(boundingWidth, boundingHeight, BufferedImage.TYPE_INT_ARGB);
//        try {
//            for (int y = 0; y < boundingHeight; y++) {
//                for (int x = 0; x < boundingWidth; x++) {
//                    ShapePixel p = array[x][y];
//                    if (p != null) {
//                        double dp = depths[x][y] / (double) maxDepth;
//                        int c = (int) (dp * 255);
//                        image.setRGB(x, y, new Color(c, c, c).getRGB());
//                    }                    
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return image;
//    }
//
//    public BufferedImage getImage(BufferedImage original) {
//        int color = Color.BLACK.getRGB();
//        BufferedImage image = new BufferedImage(boundingWidth, boundingHeight, BufferedImage.TYPE_INT_ARGB);
//        try {
//            for (int y = 0; y < boundingHeight; y++) {
//                for (int x = 0; x < boundingWidth; x++) {
//                    ShapePixel p = array[x][y];
//                    if (p != null) {
//                        image.setRGB(x, y, original.getRGB(p.x, p.y));
//                    }
//                    // overlay the skeleton on top
//                    if (skeletonArray != null) {
//                        p = skeletonArray[x][y];
//                        if (p != null) {
//                            image.setRGB(x, y, color);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return image;
//    }
//
//    /**
//     * Returns the classID of the shape. This is a label that we can attach to the shape for training
//     * purposes.
//     */
//    public int getClassID() {
//        return s.classID;
//    }
//
//    /**
//     * Sets the classID of the shape
//     */
//    public void setClassID(int classID) {
//        s.classID = classID;
//    }
//
//
//    /**
//     * Returns the mass of the shape, which is easily calculated as the number of pixels in the shape.
//     * Each pixel is assumed to weigh one "unit".
//     */
//    public int getMass() {
//        return s.getMass();
//    }
//
//    /**
//     * Returns the bounding width of the shape.
//     * Note that this is not rotationally invariant. Ideally the bounding width and height would
//     * be calculated from the minimum bounding box around the shape, something that I never got round to.
//     */
//    public int getWidth() {
//        return boundingWidth;
//    }
//
//    /**
//     * Returns the bounding height of the shape.
//     * Note that this is not rotationally invariant. Ideally the bounding width and height would
//     * be calculated from the minimum bounding box around the shape, something that I never got round to.
//     */
//    public int getHeight() {
//        return boundingHeight;
//    }
//
//    /**
//     * After everything is added, and the compile function is completed the data is stored in this array.
//     * It essentially allows spatial information about a pixel and its neighbours to be accessed easily.
//     */
//    public ShapePixel[][] array;
//
//    /* This is for the skeletonisation */
//    public ShapePixel[][] skeletonArray;
//    public int[][] depths;
//
//    /**
//     * The width of the bounding box surrounding this shape
//     */
//    public int boundingWidth = -1;
//
//    /**
//     * The height of the bounding box surrounding this shape
//     */
//    public int boundingHeight = -1;
//
//    /**
//     * A basic true false map of what is shape and what is not. The main array
//     * gets mucked about with by the skeletonisation but we need to still know
//     * where the shape starts and ends (when joining up in skeletonisation)
//     */
//    protected boolean[][] insideShape;
//
//    public void fillIn() {
//        Vector<Perimeter> p = getPerimeters();
//        if (p != null && p.size() > 0) {
//            p.elementAt(0).fillIn(this);
//        }
//    }
//
//    /**
//     * Compiles the shape pixels into a 2D array. Once it is in this configuration,
//     * it's a lot easier to do some of the calculations we're about to do because
//     * it restores spatial information which is lost if you store the shape as a series
//     * of independent "pixel" objects.
//     * Once the array has been made, it runs the findPerimeters() function which locates
//     * the shape edges.
//     */
//    protected void compile() {
//
//        // calculate the bounding width and height (simplistically)
//        boundingWidth = s.maxX - s.minX + 1;
//        boundingHeight = s.maxY - s.minY + 1;
//
//        // make the array slightly oversize so the perimeter finder methods don't have to
//        // do any upper bounds checks
//        array = new ShapePixel[boundingWidth + 1][boundingHeight + 1];
//        depths = new int[boundingWidth + 1][boundingHeight + 1];
//        //second array that keeps the original array even after skeletonisation
//        insideShape = new boolean[boundingWidth + 1][boundingHeight + 1];
//
//        //POEY comment: s.pixels.size() = the number of pixels of a segmented object
//        for (int i = 0; i < s.pixels.size(); i++) {
//            ShapePixel pixel = s.pixels.elementAt(i);
//            array[pixel.x - s.minX][pixel.y - s.minY] = pixel;	//POEY comment: pixel = a coordinate of a pixel of segmented object
//            insideShape[pixel.x - s.minX][pixel.y - s.minY] = true;
//        }
//
//        // and find all the perimeters - we need this for the next set
//        perimeters = findPerimeters();
///*
//        if (perimeters == null) {
//            throw new RuntimeException("Perimeters are null");
//        }
//*/
//
//    }
//
//
//    /**
//     * Pixel that stores the shape's center of gravity.
//     */
//    protected Pixel cog = null;
//
//
//    public Vector<Pixel> getSmoothedPixels() {
//        Vector<Pixel> smoothedPixels = new Vector<Pixel>(100);
//        for (int x = 1; x < boundingWidth - 1; x++) {
//            for (int y = 1; y < boundingHeight - 1; y++) {
//                if (array[x][y] != null) {
//                    int count = 0;
//                    if (array[x + 1][y] != null) count++;
//                    if (array[x + 1][y - 1] != null) count++;
//                    if (array[x][y - 1] != null) count++;
//                    if (array[x - 1][y - 1] != null) count++;
//                    if (array[x - 1][y] != null) count++;
//                    if (array[x - 1][y + 1] != null) count++;
//                    if (array[x][y + 1] != null) count++;
//                    if (array[x + 1][y + 1] != null) count++;
//                    if (count >= 6) {
//                        smoothedPixels.add(array[x][y]);
//                    }
//                }
//            }
//        }
//        return smoothedPixels;
//    }
//
//    public int getMaxDepth() {
//        if (skeletonArray == null) skeletonise();
//        int maxDepth = 0;
//        for (int x = 1; x < boundingWidth - 1; x++) {
//            for (int y = 1; y < boundingHeight - 1; y++) {
//                if (array[x][y] != null) {
//                    if (depths[x][y] > maxDepth) maxDepth = depths[x][y];
//                }
//            }
//        }
//        return maxDepth;
//    }
//
//
//    public double getAverageDepth() {
//        if (skeletonArray == null) skeletonise();
//        FastStatistics fs = new FastStatistics();
//        for (int x = 1; x < boundingWidth - 1; x++) {
//            for (int y = 1; y < boundingHeight - 1; y++) {
//                if (array[x][y] != null) {
//                    fs.addData(depths[x][y]);
//                }
//            }
//        }
//        return fs.getMean();
//    }
//
//
//    /**
//     * Finds the shape's center of gravity. This is the point where the shape is equally balanced
//     * in both the X and Y directions.
//     */
//    public Pixel getCentreOfGravity() {
//        if (cog == null || cog.x == -1) {
//            int cogX = (int) getCentreOfGravityX();
//            int cogY = getCentreOfGravityY();
//            cog = new Pixel(cogX, cogY);
//        }
//        return cog;
//    }
//
//    /**
//     * Calculates the center of gravity of the shape in the horizontal direction, relative to origin of shape.
//     */
//    public int getCentreOfGravityX() {
//
//        // calculate how many pixels is half
//        int halfWeight = s.totalPixels / 2;
//
//        // tot up the weight from one side
//        int weight = 0;
//
//        // work from the left hand side, adding up the pixels
//        for (int x = 0; x < boundingWidth; x++) {
//            for (int y = 0; y < boundingHeight; y++) {
//                if (array[x][y] != null) weight++;
//            }
//            if (weight >= halfWeight) return x;
//        }
//
//        return -1;
//
//    }
//
//    /**
//     * Calculates the center of gravity of the shape in the horizontal direction, relative to origin of shape.
//     */
//    public int getCentreOfGravityY() {
//
//        // calculate how many pixels is half
//        int halfWeight = s.totalPixels / 2;
//
//        // tot up the weight from one side
//        int weight = 0;
//
//        // work from the left hand side, adding up the pixels
//        for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                if (array[x][y] != null) weight++;
//            }
//            if (weight >= halfWeight) return y;
//        }
//
//        return -1;
//
//    }
//
//    // Gets the distance from the right side to the first pixel it encounters
//    public double getDistFromRight() {
//        int y = boundingHeight / 2;
//        for (int x = boundingWidth - 1; x >= 0; x--) {
//            if (array[x][y] != null) {
//                return ((boundingWidth - x) / (double) boundingWidth);
//            }
//        }
//        return 1;
//    }
//
//    public double getDistFromLeft() {
//        int y = boundingHeight / 2;
//        for (int x = 0; x < boundingWidth; x++) {
//            if (array[x][y] != null) {
//                return x / (double) boundingWidth;
//            }
//        }
//        return 1;
//    }
//
//    public double getDistFromBottom() {
//        int x = boundingWidth / 2;
//        for (int y = boundingHeight - 1; y >= 0; y--) {
//            if (array[x][y] != null) {
//                return ((boundingHeight - y) / (double) boundingHeight);
//            }
//        }
//        return 1;
//    }
//
//    public double getAverage(int blocksX, int blocksY, int x, int y) {
//        int blockWidth = boundingWidth / blocksX;
//        int blockHeight = boundingHeight / blocksY;
//        int startX = x * blockWidth;
//        int startY = y * blockHeight;
//        int N = 0;
//        int total = 0;
//        for (int sy = startY; sy < (startY + blockWidth) && sy < boundingHeight; sy++)
//            for (int sx = startX; sx < (startX + blockWidth) && sx < boundingWidth; sx++) {
//                if (array[sx][sy] != null) total++;
//                N++;
//            }
//        return N / (double) total;
//    }
//
//    // Gets data direct from the image about the shape
//    public double getAverage(int blocksX, int blocksY, int x, int y, PixelLoader image, int statistic) {
//        int blockWidth = boundingWidth / blocksX;
//        int blockHeight = boundingHeight / blocksY;
//        int startX = x * blockWidth;
//        int startY = y * blockHeight;
//        FastStatistics stat = new FastStatistics();
//        for (int sy = startY; sy < (startY + blockWidth) && sy < boundingHeight; sy++)
//            for (int sx = startX; sx < (startX + blockWidth) && sx < boundingWidth; sx++) {
//                ShapePixel p = array[sx][sy];
//                //if (p != null) {
//                    stat.addData(image.getGreyValue(p.x, p.y));
//                //}
//            }
//        return stat.getStatistic(statistic);
//    }
//
//    public float getIntensityStatistic(PixelLoader image, int statistic) {
//        FastStatistics stat = new FastStatistics();
//        for (int x = 1; x < boundingWidth - 1; x++) {
//            for (int y = 1; y < boundingHeight - 1; y++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                    stat.addData(image.getGreyValue(p.x, p.y));
//                }
//            }
//        }
//        return stat.getStatistic(statistic);
//    }
//
//    public double getDistFromTop() {
//        int x = boundingWidth / 2;
//        for (int y = 0; y < boundingHeight; y++) {
//            if (array[x][y] != null) {
//                return ((y) / (double) boundingHeight);
//            }
//        }
//        return 1;
//    }
//
//    /**
//     * The number of corners found in the shape (CACHE)
//     */
//    private int corners = -1;
//
//    /**
//     * Returns the number of corners found in the shape.
//     */
//    public int countCorners() {
//        if (corners == -1) {
//            // get radiuses method finds corners. Don't know why I called the method that.
//            getRadiuses();
//        }
//        return corners;
//    }
//    
//    public Vector<Double> getRadiuses() {
//
//        // find the middle of the shape
//        Pixel cog = getCentreOfGravity();
//
//        // get the outer perimeter
//        Perimeter p = perimeters.elementAt(0);
//
//        // store a value for every pixel
//        //POEY comment: p.pixels.size() = the number of object's pixels which are on the object's perimeter        
//        double values[] = new double[p.pixels.size()];
//
//        // find the dist of each pixel from the center
//        double highest = -1;
//       
//        for (int i = 0; i < p.pixels.size(); i++) {
//            ShapePixel pixel = p.pixels.elementAt(i);
//            values[i] = dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y);
//            if (highest == -1 || values[i] > highest) highest = values[i];
//        }
//
//        // normalise values so that we deal in percentage terms, this way the value outputted by this
//        // method will be the same regardless of the shape's size.
//        for (int i = 0; i < values.length; i++) {
//            values[i] /= highest;
//        }
//
//        // crude differentiation
//        Vector<Double> differentiatedValues = new Vector<Double>(100);
//        double lastValue = -1;
//        for (int i = 0; i < values.length; i += 2) {
//            if (i > 0) {
//                double diff = (values[i] - lastValue);
//                differentiatedValues.add(diff);
//            }
//            lastValue = values[i];
//        }
//
//        corners = 0;
//
//        final int smoothness = 1;
//
//        // smooth the graph
//        Vector<Double> smoothedValues = new Vector<Double>(100);
//        for (int i = 0; i < differentiatedValues.size(); i++) {
//
//            double total = 0;
//            double numValues = 0;
//
//            for (int s = -smoothness; s <= smoothness; s++) {
//                int dI = i + s;
//                if (dI > 0 && dI < differentiatedValues.size()) {
//                    total += differentiatedValues.elementAt(dI);
//                    numValues++;
//                }
//            }
//
//            double average = total / numValues;
//            // found a corner
//            if (smoothedValues.size() > 0 && smoothedValues.lastElement() < 0 && average >= 0) {
//                corners++;
//            }
//
//            smoothedValues.add(average);
//        }
//
//        //return values;
//        return smoothedValues;
//    }
//
//    /**
//     * Returns the number of hollows, or holes, in this particular shape.
//     * Dependent upon the compile method having been called first, which initiated
//     * the findPerimeters() method.
//     */
//    public int countHollows() {
//        if (perimeters == null) {
//            // should never happen as the perimeters object is defined
//            // during the compile() method which executes on initialisation.
//            System.out.println("Cannot count hollows: Perimeters is null.");
//        }
//        return perimeters.size() - 1;
//    }
//
//    /**
//     * The distance in the X direction from the actual centre of gravity to the bounding rectangle's
//     * centre of gravity.
//     * Todo: not scale invariant and possibly not rotation invariant
//     */
//    public double getBalanceX() {
//        return (getCentreOfGravity().x - getBoundingRectangleCentreOfGravity().x) / ((double) boundingWidth);
//    }
//
//    /**
//     * The distance in the Y direction from the actual centre of gravity to the bounding rectangle's
//     * centre of gravity.
//     * Todo: not scale invariant and possibly not rotation invariant
//     */
//    public double getBalanceY() {
//        return (getCentreOfGravity().y - getBoundingRectangleCentreOfGravity().y) / ((double) boundingHeight);
//    }
//
//    // Another cached value, saves re-calculating it
//    private Pixel boundingRectangleCentreOfGravity = null;
//
//    /**
//     * Gets the bounding rectangle's centre of gravity
//     * which may be different to the shape's actual center of gravity.
//     */
//    public Pixel getBoundingRectangleCentreOfGravity() {
//        if (boundingRectangleCentreOfGravity == null) {
//            if (boundingWidth == -1) compile();
//            boundingRectangleCentreOfGravity = new Pixel(boundingWidth / 2, boundingHeight / 2);
//        }
//        return boundingRectangleCentreOfGravity;
//    }
//
//    // Another cached value, saves re-calculating it
//    private double density = -1;
//
//    /**
//     * Density is a percentage of the volume of the shape, minus
//     * the volumes of any hollow holes inside the shape.
//     */
//    public double getDensity() {
//
//        if (density == -1) {
//            density = s.getMass() / (double) getVolume();
//        }
//
//        return density;
//
//    }
//
//    // Another cached value, saves re-calculating it
//    private int volume = -1;
//    private double averageHollowSize = -1;
//
//    public double getAverageHollowSize() {
//        if (averageHollowSize == -1) {
//            getVolume();
//        }
//        return averageHollowSize;
//    }
//
//
//    /**
//     * If a shape has hollow areas in it, then its volume is higher than its mass. This method
//     * calculates the volume of a shape by finding how many pixels there are within its outer perimeter.
//     *
//     * @return
//     */
//    public int getVolume() {
//
//        if (volume == -1) {
//
//            if (perimeters == null || perimeters.size() == 1) {
//                volume = s.totalPixels;
//                averageHollowSize = 0;
//            } else {
//
//                // density = mass / volume
//
//                // count up hollow areas
//                int totalHollowArea = 0;
//                for (int i = 1; i < perimeters.size(); i++) {
//                    Perimeter hollow = perimeters.elementAt(i);
//                    totalHollowArea += hollow.pixelsInsidePerimeter;
//                }
//
//                // the average size of hollows as a percentage of the shape's mass
//                averageHollowSize = ((double) totalHollowArea / (perimeters.size() - 1)) / getMass();
//
//                volume = s.totalPixels + totalHollowArea;
//            }
//
//        }
//
//        return volume;
//
//    }
//
//    // CACHE
//    private double balanceXRightVariance = -1;
//
//    /**
//     * Gets the variance of edge pixels on the right hand side of the centre of gravity.
//     */
//    public double getBalanceXRightVariance() {
//
//        if (balanceXRightVariance == -1) {
//
//            Pixel cog = getCentreOfGravity();
//
//            try {
//
//                FastStatistics solver = new FastStatistics();
//
//                // go down from the top
//                for (int y = 0; y < boundingHeight; y++) {
//                    // look to the right of cog
//                    int furthestRight = -1;
//                    for (int x = cog.x; x < boundingWidth; x++) {
//                        if (array[x][y] != null) {
//                            if (x > furthestRight) furthestRight = x;
//                        }
//                    }
//                    if (furthestRight != -1) {
//                        furthestRight -= cog.x;
//                        solver.addData(furthestRight);
//                    }
//                }
//
//                balanceXRightVariance = solver.getStandardDeviation();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("Can't get BXRV: " + cog.x);
//            }
//
//        }
//
//        return balanceXRightVariance;
//
//    }
//
//    // CACHE
//    private double balanceXLeftVariance = -1;
//
//    /**
//     * Gets the variance of edge pixels on the left hand side of the centre of gravity.
//     */
//    public double getBalanceXLeftVariance() {
//
//        if (balanceXLeftVariance == -1) {
//
//            Pixel cog = getCentreOfGravity();
//
//            FastStatistics solver = new FastStatistics();
//
//            // go down from the top
//            for (int y = 0; y < boundingHeight; y++) {
//                // look to the right of cog
//                int furthestLeft = -1;
//                for (int x = 0; x < cog.x; x++) {
//                    if (array[x][y] != null) {
//                        if (furthestLeft == -1 || x < furthestLeft) furthestLeft = x;
//                    }
//                }
//                if (furthestLeft != -1) {
//                    furthestLeft = cog.x - furthestLeft;
//                    solver.addData(furthestLeft);
//                }
//            }
//
//            balanceXLeftVariance = solver.getStandardDeviation();
//
//        }
//
//        return balanceXLeftVariance;
//
//    }
//
//    // CACHE
//    private Pixel furthestPixelFromCentre = null;
//
//    /**
//     * Finds the pixel that is furthest from the centre (of gravity). Quite useful if you have a shape,
//     * such as a silhouette of a pointing finger, and you want to know where it is pointing to.
//     */
//    public Pixel getFurthestPixelFromCentre() {
//
//        if (furthestPixelFromCentre == null) {
//            Pixel cog = getCentreOfGravity();
//            double furthest = -1;
//            for (int i = 0; i < s.edgePixels.size(); i++) {
//                ShapePixel pixel = s.edgePixels.elementAt(i);
//                //if ((pixel.x - s.minX) < cog.x) {
//                double d = dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y);
//                if (d > furthest) {
//                    furthest = d;
//                    furthestPixelFromCentre = pixel;
//                }
//                //}
//            }
//        }
//        return furthestPixelFromCentre;
//    }
//
//    // CACHE
//    private double roundness = -1;
//
//    /**
//     * Returns the roundness of the shape, which is the variance of the distances
//     * of the edge pixels from the center.
//     */
//    public double getRoundness() {
//
//        if (roundness == -1) {
//
//            // find the center of the shape
//            Pixel cog = getCentreOfGravity();
//
//            // for each edge pixel - find the distance from the centre of gravity
//            FastStatistics solver = new FastStatistics();
//
//            Perimeter edge = perimeters.elementAt(0);
//            for (int i = 0; i < edge.pixels.size(); i++) {
//                Pixel pixel = edge.pixels.elementAt(i);
//                solver.addData(dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y));
//            }
//
//            // crude way of getting some scale invariance
//            return solver.getStandardDeviation() / solver.getMean();
//
//        }
//
//        return roundness;
//
//    }
//
//    /**
//     * TODO: Implement this method!
//     */
//    public int getEndsBeneathCog() {
//        return -1;
//    }
//
//    // CACHE
//    private double aspectRatio = -1;
//
//    /**
//     * Gets the aspect ratio of the shape. Note that this aspect ratio is relative to the bounding box
//     * which is not rotated, hence the aspect ratio of rotated shapes may be wrong.
//     */
//    public double getAspectRatio() {
//
//        if (aspectRatio == -1) {
//            if (boundingWidth == -1) {
//                System.err.println("Can't Get Aspect Ratio: BOUNDING BOX WAS NOT INITIALISED");
//                compile();
//            }
//
//            // TODO - This is accurate but is not rotation invariant.
//            aspectRatio = boundingWidth / (double) boundingHeight;
//
//        }
//
//        return aspectRatio;
//
//    }
//
//    // CACHE
//    private Hashtable<Integer, Float> roughnessCache = null;
//
//    /**
//     * If a shape is smooth, there will always be a low differential between radii.
//     * Roughness is scale dependent of course, like a fractal, so this function allows
//     * you to find roughness at different levels of "zoom"
//     *
//     * @return
//     */
//    public float getRoughness(int step) {
//
//        if (roughnessCache == null) {
//            roughnessCache = new Hashtable<Integer, Float>();
//        }
//
//        // Get cached entry
//        Float cached = roughnessCache.get(step);
//
//        if (cached == null) {
//            Pixel cog = getCentreOfGravity();
//            FastStatistics solver = new FastStatistics();
//            Perimeter p = perimeters.elementAt(0);
//            float lastValue = -1;
//            for (int i = 0; i < p.pixels.size(); i += step) {
//                Pixel pixel = p.pixels.elementAt(i);
//                float radius = dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y);
//                if (lastValue > -1) {
//                    float diff = radius - lastValue;
//                    solver.addData(diff);
//                }
//                lastValue = radius;
//            }
//            cached = solver.getStandardDeviation();
//
//            if (cached.isNaN()) {
//                //System.out.println("Roughness is NaN, for some reason...");
//                cached = 0f;
//            }
//
//            roughnessCache.put(step, cached);
//        }
//
//        return cached;
//
//    }
//
//    // CACHE
//    private double verticalSymmetry = -1;
//
//    /**
//     * Calculates the symmetry, if you put a mirror up on the vertical axis.
//     * Not very advanced, but gets the job done.
//     */
//    public double getVerticalSymmetry() {
//
//        if (cog == null) {
//            getCentreOfGravity();
//            verticalSymmetry = -1;
//        }
//
//        if (verticalSymmetry == -1) {
//
//            double symmetricPixelCount = 0;
//
//            for (int y = 0; y < boundingHeight; y++) {
//                for (int x = 0; x <= (boundingWidth / 2); x++) {
//
//                    // ensure both pixels are in bounds
//                    if ((cog.x + x) < boundingWidth && (cog.x - x) >= 0) {
//                        boolean rightPixel = insideShape[cog.x + x][y];
//                        boolean leftPixel = insideShape[cog.x - x][y];
//                        if (rightPixel && rightPixel == leftPixel) {
//                            if (x == 0) {
//                                // central pixel is true
//                                symmetricPixelCount++;
//                            } else {
//                                // two matching pixels
//                                symmetricPixelCount += 2;
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            verticalSymmetry = symmetricPixelCount / s.totalPixels;
//
//        }
//
//        return verticalSymmetry;
//
//    }
//
//    protected double horizontalSymmetry = -1;
//
//    /**
//     * Calculates the symmetry, if you put a mirror up on the horizontal axis.
//     * Not very advanced, but gets the job done.
//     */
//    public double getHorizontalSymmetry() {
//
//        if (cog == null) {
//            getCentreOfGravity();
//            horizontalSymmetry = -1;
//        }
//
//        if (horizontalSymmetry == -1) {
//
//            double symmetricPixelCount = 0;
//
//            for (int x = 0; x < boundingWidth; x++) {
//                for (int y = 0; y <= (boundingHeight / 2); y++) {
//
//                    // ensure both pixels are in bounds
//                    if ((cog.y + y) < boundingHeight && (cog.y - y) >= 0) {
//                        boolean below = insideShape[x][cog.y + y];
//                        boolean above = insideShape[x][cog.y - y];
//                        if (below && below == above) {
//                            if (x == 0) {
//                                // central pixel is true
//                                symmetricPixelCount++;
//                            } else {
//                                // two matching pixels
//                                symmetricPixelCount += 2;
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            horizontalSymmetry = symmetricPixelCount / s.totalPixels;
//
//        }
//
//        return horizontalSymmetry;
//
//    }
//
//    protected double inverseHorizontalSymmetry = -1;
//
//    /**
//     * Calculates the inverse horizontal symmetry, if you put a mirror up on the vertical axis and flip the other side.
//     * Useful for letter analysis.
//     */
//    public double getInverseHorizontalSymmetry() {
//
//        if (cog == null) {
//            getCentreOfGravity();
//            inverseHorizontalSymmetry = -1;
//        }
//
//        if (inverseHorizontalSymmetry == -1) {
//
//            double symmetricPixelCount = 0;
//
//            for (int x = 0; x < boundingWidth; x++) {
//                for (int y = 0; y <= (boundingHeight / 2); y++) {
//
//                    // ensure both pixels are in bounds
//                    if ((cog.y + y) < boundingHeight && (cog.y - y) >= 0) {
//                        boolean below = insideShape[x][cog.y + y];
//                        boolean above = insideShape[boundingWidth - x - 1][cog.y - y];
//                        if (below && below == above) {
//                            if (x == 0) {
//                                // central pixel is true
//                                symmetricPixelCount++;
//                            } else {
//                                // two matching pixels
//                                symmetricPixelCount += 2;
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            inverseHorizontalSymmetry = symmetricPixelCount / s.totalPixels;
//
//        }
//
//        return inverseHorizontalSymmetry;
//
//    }
//
//    protected double inverseVerticalSymmetry = -1;
//
//    /**
//     * Calculates the inverse vertical symmetry, if you put a mirror up on the vertical axis and flip the other side.
//     * Useful for letter analysis. Think of the letter "S"
//     */
//    public double getInverseVerticalSymmetry() {
//        if (cog == null) {
//            getCentreOfGravity();
//            inverseVerticalSymmetry = -1;
//        }
//
//        if (inverseVerticalSymmetry == -1) {
//
//            double symmetricPixelCount = 0;
//
//            for (int y = 0; y < boundingHeight; y++) {
//                for (int x = 0; x <= (boundingWidth / 2); x++) {
//
//                    // ensure both pixels are in bounds
//                    if ((cog.x + x) < boundingWidth && (cog.x - x) >= 0) {
//                        boolean rightPixel = insideShape[cog.x + x][y];
//                        boolean leftPixel = insideShape[cog.x - x][boundingHeight - y - 1];
//                        if (rightPixel && rightPixel == leftPixel) {
//                            if (x == 0) {
//                                // central pixel is true
//                                symmetricPixelCount++;
//                            } else {
//                                // two matching pixels
//                                symmetricPixelCount += 2;
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            inverseVerticalSymmetry = symmetricPixelCount / s.totalPixels;
//
//        }
//
//        return inverseVerticalSymmetry;
//    }
//
//    private double closestEndToCog;
//
//    /**
//     * Finds the end point (found by skeletonisation) which is closest to the middle.
//     *
//     * @return
//     */
//    public double getClosestEndToCog() {
//
//        if (closestEndToCog == -1) {
//
//            if (boundingWidth == -1) compile();
//
//            Pixel cog = getCentreOfGravity();
//
//            double distance = Double.MAX_VALUE;
//            double closestX = -1;
//            double closestY = -1;
//
//            // ensure ends are calculated
//            getEnds();
//
//            for (int i = 0; i < ends.size(); i++) {
//                Pixel end = ends.elementAt(i);
//                // see how close this pixel is to the CoG
//                int diffX = end.x - cog.x;
//                int diffY = end.y - cog.y;
//                double dist = Math.sqrt((diffX * diffX) + (diffY * diffY));
//                if (dist < distance) {
//                    distance = dist;
//                    closestX = end.x;
//                    closestY = end.y;
//                }
//
//            }
//
//            if (distance == 0) {
//                return 0;
//            }
//
//            // scale X and Y in terms of the bounding height so this distance is scale invariant
//            double distX = (closestX - cog.x);
//            double distY = (closestY - cog.y);
//
//            // now, do pythag.
//            double unscaledDistance = Math.sqrt((distX * distX) + (distY * distY));
//
//            // find the furthest distance between two points in this shape
//            double maxDistance = Math.sqrt((boundingWidth * boundingWidth) + (boundingHeight * boundingHeight));
//
//            // return the scaled distance
//            closestEndToCog = unscaledDistance / maxDistance;
//
//        }
//
//        return closestEndToCog;
//
//
//    }
//
//    private double closestPixelToCog = -1;
//
//    /**
//     * Finds the pixel closest to the center of gravity. This distance may well be zero, but if the shape
//     * is hollow the pixel may be quite far away. A shape like a ring will have a non zero answer.
//     */
//    public double getClosestPixelToCog() {
//
//        if (closestPixelToCog == -1) {
//
//            if (boundingWidth == -1) compile();
//
//
//            Pixel cog = getCentreOfGravity();
//            double distance = Double.MAX_VALUE;
//            double closestX = -1;
//            double closestY = -1;
//            for (int y = 0; y < boundingHeight; y++) {
//                for (int x = 0; x < boundingWidth; x++) {
//                    if (array[x][y] != null) {
//                        // see how close this pixel is to the CoG
//                        int diffX = x - cog.x;
//                        int diffY = y - cog.y;
//                        double dist = Math.sqrt((diffX * diffX) + (diffY * diffY));
//                        if (dist < distance) {
//                            distance = dist;
//                            closestX = x;
//                            closestY = y;
//                        }
//                    }
//                }
//            }
//
//            if (distance == 0) {
//                return 0;
//            }
//
//            // scale X and Y in terms of the bounding height so this distance is scale invariant
//            double distX = (closestX - cog.x);
//            double distY = (closestY - cog.y);
//
//            // now, do pythag.
//            double unscaledDistance = Math.sqrt((distX * distX) + (distY * distY));
//
//            // find the furthest distance between two points in this shape
//            double maxDistance = Math.sqrt((boundingWidth * boundingWidth) + (boundingHeight * boundingHeight));
//
//            // return the scaled distance
//            closestPixelToCog = unscaledDistance / maxDistance;
//
//        }
//
//        return closestPixelToCog;
//
//
//    }
//
//    public int getBoundingArea() {
//        if (boundingWidth == -1) compile();
//        return boundingWidth * boundingHeight;
//    }
//
//    public double getRectangularity() {
//        return getMass() / (double) getBoundingArea();
//    }
//
//    /**
//     * Returns if the Center of gravity is over a hollow, or over solid
//     */
//    public boolean isCoGOverHollow() {
//        return getClosestPixelToCog() < 0.025;
//    }
//
//    /**
//     * Gets the number of joints that make up the shape. An "X" has one, an "L" has none.
//     */
//    public int getJoints() {
//        if (joints == null) skeletonise();
//        return joints.size();
//    }
//
//    /**
//     * Finds how many "ends" the shape has. An "X" has four. An "L" has two. An "0" has none.
//     */
//    public int getEnds() {
//        if (ends == null) skeletonise();
//        return ends.size();
//    }
//
//
//    /**
//     * Performs the skeletonisation necessary to detect joints and ends.
//     */
//    public void skeletonise() {
//
//        // bit nasty to implement, this one.
//
//        if (boundingWidth == -1) compile();
//
//        // use hildritch's algorithm to thin down to a single line.
//        int c = 0;
//        while (true) {
//            if (!thin(c, c == 0 ? array : skeletonArray)) break;
//            c++;
//        }
//
//        // find pixels with only one neighbour (end pixels)
//        // try to join them to other pixels. Do this twice so that
//        // single pixels are connected at BOTH ends.
//
//        joinUp(skeletonArray);
//        cleanUp(skeletonArray);
//
//        joinUp(skeletonArray);
//        cleanUp(skeletonArray);
//
//        joints = new Vector<Pixel>(10);
//        ends = new Vector<Pixel>(10);
//
//        // now count how many joints there are - we deal with three point joins and above only
//        // already checked array allows us to make some joins uncheckable - such as ones very close
//        // to others that have just been found - this prevents multiple responses for parts of the same join.
//        boolean[][] alreadyChecked = new boolean[boundingWidth + 1][boundingHeight + 1];
//
//        Vector<Pixel> endsToBeRemoved = new Vector<Pixel>(10);
//
//        for (int y = 1; y < boundingHeight; y++) {
//            for (int x = 1; x < boundingWidth; x++) {
//                if (skeletonArray[x][y] != null) {
//
//                    if (alreadyChecked[x][y]) continue;
//
//                    int neighbours = countNeighbours(skeletonArray, x, y);
//
//                    if (neighbours > 2) {
//                        // make sure that other pixels in this vicinity are not also checked - they
//                        // are probably part of the same join
//                        for (int dy = -2; dy <= 2; dy++) {
//                            for (int dx = -2; dx <= 2; dx++) {
//                                try {
//                                    alreadyChecked[x + dx][y + dy] = true;
//                                } catch (Exception e) {
//
//                                }
//                            }
//                        }
//
//                        // from this point find the closest end point. If that point is
//                        // less than 10 pixels away then ignore this as a joint - its just an artifact
//                        // caused by a serif or pointy bit.
//
//                        Pixel closestEndPoint = findClosestEndPoint(skeletonArray, x, y);
//
//                        // if distance is more than equal five pixels away
//                        if (closestEndPoint.value >= 5) {
//                            joints.add(new Pixel(x, y));
//                        } else {
//                            // don't add the joint and remove the end point too
//                            endsToBeRemoved.add(closestEndPoint);
//                        }
//
//                    }
//
//                    if (neighbours == 1) {
//                        ends.add(new Pixel(x, y));
//                    }
//
//                }
//            }
//        }
//
//        for (int i = 0; i < endsToBeRemoved.size(); i++) {
//            Pixel pixel = endsToBeRemoved.elementAt(i);
//            if (!ends.remove(pixel)) {
//                System.err.println("// Could not remove end point (" + pixel.x + ", " + pixel.y + ").");
//
//            }
//        }
//
//    }
//
//    // What follows is mostly code for the skeletonisation procedure. Its rather unpleasant.
//
//
//    /**
//     * Finds the shortest distance from a given point to an end point. This is used
//     * to see if a joint is actually a joint or not.
//     */
//    private Pixel findClosestEndPoint(ShapePixel[][] array, int x, int y) {
//
//        boolean[][] alreadyChecked = new boolean[boundingWidth + 1][boundingHeight + 1];
//        Stack<Pixel> stack = new Stack<Pixel>();
//        stack.add(new Pixel(x, y, 0));
//
//        int shortestDistance = Integer.MAX_VALUE;
//        int shortestX = -1;
//        int shortestY = -1;
//
//        while (stack.size() > 0) {
//            Pixel p = stack.pop();
//            alreadyChecked[p.x][p.y] = true;
//
//            // record how many steps away from the start point this pixel is
//            int distance = p.value;
//
//            // look at this pixel's neighbours
//            Vector<Pixel> neighbours = getNeighbours(array, p.x, p.y);
//
//            if (neighbours.size() == 1) {
//                // we have reached an end point
//                if (distance < shortestDistance) {
//                    shortestDistance = distance;
//                    shortestX = p.x;
//                    shortestY = p.y;
//                }
//                continue;
//            }
//
//            for (int i = 0; i < neighbours.size(); i++) {
//                Pixel neighbour = neighbours.elementAt(i);
//                if (!alreadyChecked[neighbour.x][neighbour.y]) {
//                    alreadyChecked[neighbour.x][neighbour.y] = true;
//                    neighbour.value = distance + 1;
//                    stack.add(neighbour);
//                }
//            }
//
//        }
//
//        return new Pixel(shortestX, shortestY, shortestDistance);
//
//    }
//
//    protected double endBalanceX = -1;
//
//    /**
//     * Finds the balance from the average position of the ends to the center of gravity's X position.
//     */
//    public double getEndBalanceX() {
//
//        // make sure ends are calculated
//        getEnds();
//
//        if (ends.size() == 0) return 0;
//
//        // find the average position of the ends
//        double total = 0;
//        for (int i = 0; i < ends.size(); i++) {
//            Pixel end = ends.elementAt(i);
//            total += end.x;
//        }
//
//        double averageX = total / ends.size();
//
//        return ((getCentreOfGravity().x - averageX) / (double) boundingWidth);
//
//    }
//
//    protected double endBalanceY = -1;
//
//    /**
//     * Finds the balance from the average position of the ends to the center of gravity's Y position.
//     */
//    public double getEndBalanceY() {
//
//        // make sure ends are calculated
//        getEnds();
//
//        if (ends.size() == 0) return 0;
//
//        // find the average position of the ends
//        double total = 0;
//        for (int i = 0; i < ends.size(); i++) {
//            Pixel end = ends.elementAt(i);
//            total += end.y;
//        }
//
//        double averageY = total / ends.size();
//
//        return ((getCentreOfGravity().y - averageY) / (double) boundingHeight);
//
//    }
//
//    private void cleanUp(ShapePixel[][] array) {
//        // clear unnecessary pixels to make the line as thin as possible
//        for (int y = 1; y < boundingHeight; y++) {
//            for (int x = 1; x < boundingWidth; x++) {
//                if (array[x][y] != null) {
//                    boolean N = array[x][y - 1] != null;
//                    boolean E = array[x + 1][y] != null;
//                    boolean S = array[x][y + 1] != null;
//                    boolean W = array[x - 1][y] != null;
//                    boolean SW = array[x - 1][y + 1] != null;
//                    boolean NW = array[x - 1][y - 1] != null;
//                    boolean NE = array[x + 1][y - 1] != null;
//                    boolean SE = array[x + 1][y + 1] != null;
//                    if (N && E && !SW) array[x][y] = null;
//                    if (E && S && !NW) array[x][y] = null;
//                    if (S && W && !NE) array[x][y] = null;
//                    if (W && N && !SE) array[x][y] = null;
//                }
//            }
//        }
//    }
//
//    private void joinUp(ShapePixel[][] array) {
//        for (int y = 1; y < boundingHeight; y++) {
//            for (int x = 1; x < boundingWidth; x++) {
//                if (array[x][y] != null) {
//
//                    if (countNeighbours(array, x, y) <= 1) {
//
//                        // search around looking for a close neighbour
//                        Pixel other = findClosestPixelToConnectTo(array, x, y);
//
//                        if (other != null) {
//
//                            // only join the dots if the other pixel also wants to join this pixel
//                            Pixel otherPixelsPreference = findClosestPixelToConnectTo(array, other.x, other.y);
//
//                            if (otherPixelsPreference == null || otherPixelsPreference.x != x || otherPixelsPreference.y != y) {
//                                // the other pixel has discovered a closer pixel itself.
//                                continue;
//                            }
//
//                            // now join the dots
//                            Vector<Pixel> path = new Vector<Pixel>(10);
//
//                            int cX = x;
//                            int cY = y;
//                            boolean abort = false;
//                            while (true) {
//                                boolean changed = false;
//                                if (cX != other.x) {
//                                    if (cX < other.x) cX++;
//                                    else cX--;
//                                    changed = true;
//                                }
//                                if (cY != other.y) {
//                                    if (cY < other.y) cY++;
//                                    else cY--;
//                                    changed = true;
//                                }
//                                if (changed) {
//                                    // the join strays outside the shape - this connection would connect
//                                    // parts of the shape that could never be connected, abort!
//                                    if (!insideShape[cX][cY]) {
//                                        abort = true;
//                                        break;
//                                    }
//                                    path.add(new Pixel(cX, cY));
//                                } else break;
//                            }
//
//                            if (!abort) {
//                                // now fill in the path
//                                for (int i = 0; i < path.size(); i++) {
//                                    Pixel p = path.elementAt(i);
//                                    if (array[p.x][p.y] == null)
//                                        array[p.x][p.y] = new ShapePixel(p.x + s.minX, p.y + s.minY);
//                                }
//                            }
//
//                        }
//
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * @param pX
//     * @param pY
//     * @return
//     */
//    private Pixel findClosestPixelToConnectTo(ShapePixel[][] array, int pX, int pY) {
//
//        final int maxJoinDistance = 10;
//
//        // look in increasing square circles from the point until we find another pixel
//        for (int dist = 1; dist < maxJoinDistance; dist++) {
//
//            Vector<Pixel> possibleNeighbours = new Vector<Pixel>(100);
//
//            for (int y = -dist; y <= dist; y++) {
//                if (y == -dist || y == dist) {
//                    for (int x = -dist; x <= dist; x++) {
//                        if (getArray(array, pX + x, pY + y) != null) {
//                            possibleNeighbours.add(new Pixel(x, y));
//                        }
//                    }
//                } else {
//                    if (getArray(array, pX - dist, pY + y) != null) possibleNeighbours.add(new Pixel(-dist, y));
//                    if (getArray(array, pX + dist, pY + y) != null) possibleNeighbours.add(new Pixel(+dist, y));
//                }
//            }
//
//            // now check the possible neighbours and see if they are any good
//            Pixel bestNeighbour = null;
//            double smallestDistance = Double.MAX_VALUE;
//
//            for (int j = 0; j < possibleNeighbours.size(); j++) {
//                Pixel possibleNeighbour = possibleNeighbours.elementAt(j);
//
//                if (!connectedTo(array, pX, pY, pX + possibleNeighbour.x, pY + possibleNeighbour.y, maxJoinDistance * 2)) {
//
//                    // find the distance between the point and this neighbour
//                    double distance = pythag(possibleNeighbour.x, possibleNeighbour.y);
//
//                    if (distance < smallestDistance) {
//                        smallestDistance = distance;
//                        bestNeighbour = new Pixel(pX + possibleNeighbour.x, pY + possibleNeighbour.y);
//                    }
//
//                }
//
//            }
//
//            if (bestNeighbour != null) {
//                return bestNeighbour;
//            }
//
//        }
//
//        // found nothing in range
//        return null;
//
//    }
//
//    /**
//     * Returns whether a point is connected to another point within a certain number
//     * of pixels. It may be the case that they are connected, when you look at the whole
//     * contect of the image, but in this case we'd just like to know if there are dist or fewer
//     * pixels that connect pX to pY
//     */
//    private boolean connectedTo(ShapePixel[][] array, int pX, int pY, int oX, int oY, int dist) {
//
//        boolean[][] alreadyChecked = new boolean[boundingWidth + 1][boundingHeight + 1];
//
//        alreadyChecked[pX][pY] = true;
//
//        return getPath(array, pX, pY, oX, oY, alreadyChecked, 0, dist);
//
//    }
//
//    private boolean getPath(ShapePixel[][] array, int cX, int cY, int oX, int oY, boolean[][] alreadyChecked, int pathLength, int maxDist) {
//
//        // don't go beyond the boundary
//        if (pathLength > maxDist) return false;
//
//        // see if we are at the destination
//        if (cX == oX && cY == oY) return true;
//
//        // look at the neighbours of the current point
//        Vector<Pixel> neighbours = getNeighbours(array, cX, cY);
//
//        // look at each neighbour in turn
//        for (int j = 0; j < neighbours.size(); j++) {
//            Pixel neighbour = neighbours.elementAt(j);
//
//            if (!alreadyChecked[neighbour.x][neighbour.y]) {
//                alreadyChecked[neighbour.x][neighbour.y] = true;
//                if (getPath(array, neighbour.x, neighbour.y, oX, oY, alreadyChecked, pathLength + 1, maxDist))
//                    return true;
//            }
//
//        }
//
//        return false;
//
//    }
//
//    /**
//     * Thins a shape down to a skeleton using Hilditch's algorithm
//     */
//    private boolean thin(int dist, ShapePixel[][] array) {
//
//        int pixelsThinned = 0;
//
//        ShapePixel[][] skeletonArray = new ShapePixel[boundingWidth + 1][boundingHeight + 1];
//
//        // go through all the pixels
//        // change a pixel from black to white if it meets the following conditions
//        for (int y = 1; y < boundingHeight; y++) {
//            for (int x = 1; x < boundingWidth; x++) {
//                if (array[x][y] != null) {
//
//                    int B = countNeighbours(array, x, y);
//                    int A = countConnections(array, x, y);
//
//                    skeletonArray[x][y] = array[x][y];
//
//                    // Delete the pixel if it has more than 1 neighbour, and fewer than 7 neighbours
//                    if (B >= 2 && B <= 6 && A == 1) {
//
//                        boolean P9 = array[x - 1][y - 1] != null;
//                        boolean P8 = array[x - 1][y] != null;
//                        boolean P7 = array[x - 1][y + 1] != null;
//
//                        boolean P2 = array[x][y - 1] != null;
//
//                        boolean P3 = array[x + 1][y - 1] != null;
//                        boolean P4 = array[x + 1][y] != null;
//                        boolean P5 = array[x + 1][y + 1] != null;
//
//                        boolean P6 = array[x][y + 1] != null;
//
//                        // preserves vertical lines
//                        if (x < boundingWidth - 1) {
//                            boolean P10 = array[x + 2][y - 1] != null;
//                            boolean P11 = array[x + 2][y] != null;
//                            boolean P12 = array[x + 2][y + 1] != null;
//                            if (!P9 && !P8 && !P7 && P3 && P4 && P5 && P6 && !P10 && !P11 && !P12) continue;
//                        }
//
//                        // preserves horizontal lines
//                        if (y < boundingHeight - 1) {
//                            boolean P13 = array[x - 1][y + 2] != null;
//                            boolean P14 = array[x][y + 2] != null;
//                            boolean P15 = array[x + 1][y + 2] != null;
//                            if (!P9 && !P2 && !P3 && P4 && P8 && P5 && P6 && P7 && !P13 && !P14 && !P15) continue;
//                        }
//
//
//                        pixelsThinned++;
//                        depths[x][y] = dist;
//                        skeletonArray[x][y] = null;
//                    }
//
//                }
//            }
//
//        }
//
//        this.skeletonArray = skeletonArray;
//
//        return pixelsThinned > 0;
//
//    }
//
//    private Vector<Pixel> getNeighbours(ShapePixel[][] array, int x, int y) {
//        Vector<Pixel> neighbours = new Vector<Pixel>(8);
//        for (int dy = -1; dy <= 1; dy++) {
//            for (int dx = -1; dx <= 1; dx++) {
//                if (dx == 0 && dy == 0) continue;
//                if (array[x + dx][y + dy] != null) {
//                    neighbours.add(new Pixel(x + dx, y + dy));
//                }
//            }
//        }
//        return neighbours;
//    }
//
//    /**
//     * Finds out how many neighbours a given pixel has
//     */
//    private int countNeighbours(ShapePixel[][] array, int x, int y) {
//        int neighbourCount = 0;
//        for (int dy = -1; dy <= 1; dy++) {
//            for (int dx = -1; dx <= 1; dx++) {
//                if (dx == 0 && dy == 0) continue;
//                if (array[x + dx][y + dy] != null) {
//                    neighbourCount++;
//                }
//            }
//        }
//        return neighbourCount;
//    }
//
//    /**
//     * Counts the number of connections to this pixel by counting
//     * the number of 0-1 patterns in a clockwise direction there are.
//     */
//    private int countConnections(ShapePixel[][] array, int x, int y) {
//        int connections = 0;
//        Pixel[] neighbourLocations = new Pixel[8];
//        neighbourLocations[0] = new Pixel(0, -1); // n
//        neighbourLocations[1] = new Pixel(+1, -1); // ne
//        neighbourLocations[2] = new Pixel(1, 0); // e
//        neighbourLocations[3] = new Pixel(+1, +1); // se
//        neighbourLocations[4] = new Pixel(0, 1); // s
//        neighbourLocations[5] = new Pixel(-1, +1); // sw
//        neighbourLocations[6] = new Pixel(-1, 0); // w
//        neighbourLocations[7] = new Pixel(-1, -1); // nw
//        boolean lastWas = array[x + neighbourLocations[7].x][y + neighbourLocations[7].y] != null;
//        for (int j = 0; j < neighbourLocations.length; j++) {
//            boolean current = array[x + neighbourLocations[j].x][y + neighbourLocations[j].y] != null;
//            if (!lastWas && current) {
//                connections++;
//            }
//            lastWas = current;
//        }
//        return connections;
//    }
//
//    private double pythag(int width, int height) {
//        return Math.sqrt((width * width) + (height * height));
//    }
//
//    private ShapePixel getArray(ShapePixel[][] array, int x, int y) {
//        if (x < 0 || y < 0 || x >= boundingWidth || y >= boundingHeight) return null;
//        return array[x][y];
//    }
//    // END THINNING CODE
//
//    /**
//     * The Perimeters
//     */
//    Vector<Perimeter> perimeters;
//
//    public Vector<Perimeter> getPerimeters() {
//        return perimeters;
//    }
//
//    /**
//     * Finds which edges are on the outer perimeter, and which edges are inner edges - holes
//     * The holes aren't really as important to us as the perimeters, so we need to know which
//     * is which. Once we've found all the perimeters (connected edge paths), all we need to do
//     * is find the largest one - the outer perimeter, and all others (if any) will therefore be
//     * hollow holes inside the main shape.
//     */
//    private Vector<Perimeter> findPerimeters() {
//
//        Vector<Perimeter> perimeters = new Vector<Perimeter>(10);
//
//        int filledIn = 0;
//
//        for (int i = 0; i < s.edgePixels.size(); i++) {
//            ShapePixel edgePixel = s.edgePixels.elementAt(i);
//
//            // check that it hasn't already been classified
//            if (!edgePixel.alreadyChecked) {
//
//                // so that we don't check it twice
//                edgePixel.alreadyChecked = true;
//
//                // find its coordinates on the array
//                int x = edgePixel.x - s.minX;
//                int y = edgePixel.y - s.minY;
//
//                // find a neighbour. it doesn't matter which direction this is because we're
//                // looking for a loop.
//                ShapePixel neighbour = null;
//                chooseNeighbour:
//                for (int dY = -1; dY <= 1; dY++)
//                    for (int dX = -1; dX <= 1; dX++) {
//                        if (!(dX == 0 && dY == 0)) {
//                            if (x + dX < 0) continue;
//                            if (y + dY < 0) continue;
//                            neighbour = array[x + dX][y + dY];
//                            if (neighbour != null && neighbour.isEdge && !neighbour.alreadyChecked) {
//                                x += dX;
//                                y += dY;
//                                neighbour.alreadyChecked = true;
//                                break chooseNeighbour;
//                            }
//                        }
//                    }
//
//                // null - shouldn't happen
//                if (neighbour == null) {
//                    // this edge pixel has no neigbours at all.
//                    // this would only be the case if the shape has precisely one pixel
//                    // this shouldn't happen because the grouper throws away shapes with
//                    // only one pixel (they can't provide meaningful shape statistics because
//                    // they don't have one)
//                    // however, to be polite, just print a message stating that this shape's
//                    // existence is futile and move on.
//                    //System.err.println("Edge pixel with no neighbours - single pixel shape allowed by Grouper? Ignoring.");
//                    continue;
//                }
//
//                // store all the edges on this perimeter in this structure:
//                Perimeter perimeter = new Perimeter(neighbour);
//                perimeter.add(edgePixel);
//
//                // now, find the perimeter using this recursive method.
//                Stack<PerimeterStack> s = new Stack<PerimeterStack>();
//                s.add(new PerimeterStack(edgePixel, x, y, -1));
//                while (s.size() > 0) {
//                    //findPerimeter(edgePixel, perimeter, x, y, -1);
//                    findPerimeter(s, perimeter);
//                }
//
//                if (perimeter.pixels.size() > 1) {
//
//                    // get stats for perimeter
//                    perimeter.compile();
//
//                    if (perimeters.size() > 0 && perimeter.pixelsInsidePerimeter < 10) {
//                        // ignore this perimeter, it is noise and will interfere with countHollows
//                        // and the skeletonisation process. Fill in the gap
//                        filledIn++;
//                        perimeter.fillIn(this);
//                    } else {
//                        // save this perimeter
//                        perimeters.add(perimeter);
//                    }
//
//                }
//
//            }
//
//        }
//
//        // reset
//        for (int i = 0; i < s.edgePixels.size(); i++) {
//            ShapePixel edgePixel = s.edgePixels.elementAt(i);
//            edgePixel.alreadyChecked = false;
//        }
//
//        // check that we have actually found some perimeters, otherwise we'd get a NullPointerException in later
//        // code. This shouldn't happen as all shapes by definition have at least one (outer) perimeter.
//        if (perimeters.size() == 0) {
//            System.err.println("No perimeters found for shape. Shape size: " + getVolume() + ", filledIn: " + filledIn);
//            return null;
//        }
//
//        // the largest perimeter is the first one added, as this derives from the first edgepixel found, which will
//        // clearly always be on the outer edge.
//        Perimeter largestPerimeter = perimeters.elementAt(0);
//
//        // Now that we've established which is the largest perimeter, and conveniently we have a vector that
//        // contains every pixel, we can finally use this data. Knowing which edge pixels are on the outside and which
//        // are on the inside is actually very useful, as it allows us to make totally correct calculations of
//        // things like the shape's density and area, as well as being able to make accurate analysis of the shape's
//        // general appearance, which is defined by its outer edges.
//
//        // For now we'll simply mark the edges in the outer perimeter as being "outer" edges. ShapePixels are
//        // naturally defined as being "inner" edges by default, so we don't need to change any of the inner
//        // edges' definitions.
//        for (int i = 0; i < largestPerimeter.pixels.size(); i++) {
//            largestPerimeter.pixels.elementAt(i).insideEdge = false;
//        }
//
//        // return the perimeter, so it can be used in further calculations.
//        return perimeters;
//
//    }
//
//    class PerimeterStack {
//
//        ShapePixel edgePixel;
//        int x, y, dir;
//
//        PerimeterStack(ShapePixel edgePixel, int x, int y, int dir) {
//            this.edgePixel = edgePixel;
//            this.x = x;
//            this.y = y;
//            this.dir = dir;
//        }
//
//    }
//
//    //private void findPerimeter(ShapePixel startPixel, Perimeter perimeter, int x, int y, int prevDirection) {
//    private void findPerimeter(Stack<PerimeterStack> stack, Perimeter perimeter) {
//
//        PerimeterStack s = stack.pop();
//        int x = s.x;
//        int y = s.y;
//        ShapePixel startPixel = s.edgePixel;
//        int prevDirection = s.dir;
//
//        // look for start pixel's neighbours, so that we can move around the perimeter, and with any luck
//        // return to the pixel at which we started. As the perimeter is linear, there is only one way to go
//        // but we need to make sure that we're not going backwards, hence the lastDirection parameter which
//        // allows us to check.
//
//
//        final Pixel[] neighbourLocations = new Pixel[8];
//        // go round clockwise (helps the algorithm not to miss out pixels)
//        neighbourLocations[0] = new Pixel(0, -1); // n
//        neighbourLocations[1] = new Pixel(+1, -1); // ne
//        neighbourLocations[2] = new Pixel(1, 0); // e
//        neighbourLocations[3] = new Pixel(+1, +1); // se
//        neighbourLocations[4] = new Pixel(0, 1); // s
//        neighbourLocations[5] = new Pixel(-1, +1); // sw
//        neighbourLocations[6] = new Pixel(-1, 0); // w
//        neighbourLocations[7] = new Pixel(-1, -1); // nw
//
//        Vector<NextStep> candidates = new Vector<NextStep>(4);
//        NextStep best = null;
//
//        for (int i = 0; i < neighbourLocations.length; i++) {
//            Pixel neighbourLocation = neighbourLocations[i];
//
//            int dX = neighbourLocation.x;
//            int dY = neighbourLocation.y;
//
//            if (x + dX < 0) continue;
//            if (y + dY < 0) continue;
//
//            ShapePixel neighbour = array[x + dX][y + dY];
//
//            if (neighbour != null) {
//                int neighbourNeighbours = countUncheckedNeighbours(x + dX, y + dY);
//                if (neighbour.isEdge && !neighbour.alreadyChecked) {
//                    best = new NextStep(neighbour, x + dX, y + dY);
//                    if (neighbourNeighbours > 0 || (perimeter.pixels.size() > 3 && connectedTo(neighbour, startPixel))) {
//                        candidates.add(best);
//                    }
//                } else {
//
//                    // if we reach the end of the perimeter, add the final neighbour and complete.
//                    if (perimeter.pixels.size() > 3) {
//                        if (neighbour == startPixel) {
//                            return;
//                        }
//                    }
//
//                }
//            }
//
//        }
//
//        if (candidates.size() == 0) {
//            // no neighbour found. Most annoying - perimeter doesn't work properly.
//            // happens if the perimeter is too thin. There are a few circumstances that cause this to happen.
//            // when this happens - try to jump two pixels
//            // step 1 - if there is a poorly connected neighbour, use that for now
//            if (best != null) {
//                candidates.add(best);
//            } else {
//
//                // the problem is that a pixel may have been incorrectly classified, and we may have to
//                // use the same pixel twice. So - find all used pixels and see if any of those have unchecked
//                // neighbours. If there is one such neighbour, add that one and proceed along that course.
//                // this doesn't cause infinite loops as it only makes this check once, and is still only looking
//                // for a bridge - a pixel can't be used as a bridge over and over and over.
//                for (int i = 0; i < neighbourLocations.length; i++) {
//                    Pixel neighbourLocation = neighbourLocations[i];
//
//                    int dX = neighbourLocation.x;
//                    int dY = neighbourLocation.y;
//
//                    if (x + dX < 0) continue;
//                    if (y + dY < 0) continue;
//
//                    ShapePixel neighbour = array[x + dX][y + dY];
//
//                    if (neighbour != null && neighbour.isEdge) {
//                        int neighbourNeighbours = countUncheckedNeighbours(x + dX, y + dY);
//                        if (neighbourNeighbours > 0) {
//                            // found the next step
//                            candidates.add(new NextStep(neighbour, x + dX, y + dY));
//                        }
//                    }
//
//                }
//
//                // but if we still fail, give up
//                if (candidates.size() == 0) {
//                    //System.err.println("No neighbour found for pixel on perimeter: " + x + ", " + y);
//                    return;
//                }
//
//            }
//        }
//
//        // choose the most appropriate neighbour here...
//        NextStep next = null;
//
//        // although oftentimes there is no choice!
//        if (candidates.size() == 1) {
//            next = candidates.elementAt(0);
//        } else {
//
//            // figure out what direction the current pixel is facing outwards onto
//            Direction d1 = getDirection(x, y);
//
//            // try to find a neighbour that has a compatible direction
//            for (int i = 0; i < candidates.size(); i++) {
//                NextStep nextStep = candidates.elementAt(i);
//
//                Direction d2 = getDirection(nextStep.x, nextStep.y);
//
//                if (d1.isCompatibleWith(d2)) {
//                    next = nextStep;
//                    break;
//                }
//
//            }
//
//            // what if neighbour is still null? None of them match quite properly, so just
//            // use the first one.
//            if (next == null) {
//                next = candidates.elementAt(0);
//            }
//
//        }
//
//        ShapePixel neighbour = next.pixel;
//
//        // mark the neighbour as being checked
//        neighbour.alreadyChecked = true;
//
//        // add start pixel to the path
//        perimeter.add(neighbour);
//
//        // keep searching. Move on to the neighbour and continue
//        //findPerimeter(startPixel, perimeter, next.x, next.y, prevDirection);
//        stack.add(new PerimeterStack(startPixel, next.x, next.y, prevDirection));
//
//    }
//
//    private boolean connectedTo(ShapePixel pixel, ShapePixel other) {
//        return Math.abs(pixel.x - other.x) <= 1 && Math.abs(pixel.y - other.y) <= 1;
//    }
//
//    /**
//     * Looks at an edge pixel and determines what directin that pixel is facing. All edge pixels
//     * face outward, but some face up, some down etc.
//     */
//    private Direction getDirection(int x, int y) {
//
//        // figure out whats in the horizontal direction
//        int hDirection = Direction.NO_HORIZONTAL;
//
//
//        if (array[x + 1][y] == null) hDirection = Direction.EAST;
//        if (x - 1 < 0 || array[x - 1][y] == null) hDirection = Direction.WEST;
//
//        int vDirection = Direction.NO_VERTICAL;
//
//        if (y - 1 < 0 || array[x][y - 1] == null) vDirection = Direction.NORTH;
//        if (array[x][y + 1] == null) vDirection = Direction.SOUTH;
//
//        if (hDirection == Direction.NO_HORIZONTAL && vDirection == Direction.NO_VERTICAL) {
//            // need to look in the diagonal corners too
//            if (array[x + 1][y + 1] == null) {
//                // south east
//                vDirection = Direction.SOUTH;
//                hDirection = Direction.EAST;
//            }
//            if (array[x + 1][y - 1] == null) {
//                // north east
//                vDirection = Direction.NORTH;
//                hDirection = Direction.EAST;
//            }
//            if (array[x - 1][y + 1] == null) {
//                // south east
//                vDirection = Direction.SOUTH;
//                hDirection = Direction.WEST;
//            }
//            if (array[x - 1][y - 1] == null) {
//                // north east
//                vDirection = Direction.NORTH;
//                hDirection = Direction.WEST;
//            }
//        }
//
//        return new Direction(hDirection, vDirection);
//
//    }
//
//    private class Direction {
//
//        public static final int NO_HORIZONTAL = -1;
//        public static final int NO_VERTICAL = 0;
//        public static final int NORTH = 1;
//        public static final int SOUTH = 2;
//        public static final int EAST = 3;
//        public static final int WEST = 4;
//
//        int hDirection;
//        int vDirection;
//
//        public Direction(int hDirection, int vDirection) {
//            this.hDirection = hDirection;
//            this.vDirection = vDirection;
//        }
//
//        public boolean isCompatibleWith(Direction other) {
//            return ((this.hDirection > 0 && this.hDirection == other.hDirection) || (this.vDirection > 0 && this.vDirection == other.vDirection));
//        }
//
//    }
//
//    private class NextStep {
//        ShapePixel pixel;
//        int x, y;
//
//        public NextStep(ShapePixel pixel, int x, int y) {
//            this.pixel = pixel;
//            this.x = x;
//            this.y = y;
//        }
//    }
//
//    private int countUncheckedNeighbours(int x, int y) {
//
//        int uncheckedNeighbours = 0;
//
//        for (int dy = -1; dy <= 1; dy++) {
//            for (int dx = -1; dx <= 1; dx++) {
//                if (dx == 0 && dy == 0) continue;
//                int nx = x + dx;
//                int ny = y + dy;
//                if (nx < 0 || ny < 0) continue;
//                if (nx >= boundingWidth || ny >= boundingHeight) continue;
//                ShapePixel neighbour = array[nx][ny];
//                if (neighbour != null && neighbour.isEdge && !neighbour.alreadyChecked) {
//                    uncheckedNeighbours++;
//                }
//            }
//        }
//
//        return uncheckedNeighbours;
//
//    }
//
//    /**
//     * Finds the Euclidean distance between two points. A utility method.
//     */
//    protected float dist(int x1, int y1, int x2, int y2) {
//        double a = x1 - x2;
//        a = a * a;
//        double b = y1 - y2;
//        b = b * b;
//        return (float) Math.sqrt(a + b);
//    }
//
//    public void printStats() {
//        System.out.println("-----------------------------------");
//        System.out.println("SHAPE STATISTICS");
//        System.out.println("Corners: " + countCorners());
//        System.out.println("Ends: " + getEnds());
//        System.out.println("Joints: " + getJoints());
//        System.out.println("Aspect Ratio: " + getAspectRatio());
//        System.out.println("Hollows: " + countHollows());
//        System.out.println("Density: " + getDensity());
//        System.out.println("CoG over Hollow?: " + isCoGOverHollow());
//        System.out.println("Closest Pixel to COG: " + getClosestPixelToCog());
//        System.out.println("BalanceX: " + getBalanceX());
//        System.out.println("BalanceY: " + getBalanceY());
//    }
//
//    
//    //POEY
//    //the following functions are used to calculate colour features of a segmented object
//    
//    /**
//     * to return an average red value of a segmented object's pixels
//     */
//    public double getNormalisedRedMean() {   	
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getNormalisedRed(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average green value of a segmented object's pixels
//     */
//    public double getNormalisedGreenMean() {   	
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getNormalisedGreen(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average blue value of a segmented object's pixels
//     */
//    public double getNormalisedBlueMean() {   	
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getNormalisedBlue(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average value generated by C1C2C3 of a segmented object's pixels
//     */
//    public double getC1C2C3Mean(int i) {   	
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getC1C2C3(p.x,p.y,i));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average value generated by L1L2L3 of a segmented object's pixels
//     */
//    public double getL1L2L3Mean(int i) {   	
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getL1L2L3(p.x,p.y,i));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average grey value of a segmented object's pixels
//     */
//    public double getGreyValueMean(){
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getGreyValue(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//
//    /**
//     * to return an average hue value of a segmented object's pixels
//     */
//    public double getHueMeanObject(){
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getHue(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average saturation value of a segmented object's pixels
//     */
//    public double getSatMeanObject(){
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getSaturation(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }
//    
//    /**
//     * to return an average lightness value of a segmented object's pixels
//     */
//    public double getLightnessMeanObject(){
//    	FastStatistics solver = new FastStatistics();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData((float)image.getLightness(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMean();
//    }   
//    
//    /**
//     * to return a range of grey value of a segmented object
//     */
//    public double get3x3RangeObject(){
//    	int value = 0;
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	value = image.get3x3Range(p.x,p.y);
//                }
//            }
//    	}
//        return value;
//    }
//    
//    /**
//     * to return a variance value of a segmented object
//     */
//    public double get3x3VarianceObject(){
//    	double value = 0;
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	value = image.get3x3Variance(p.x,p.y);
//                }
//            }
//    	}
//        return value;
//    }
//    
//    /**
//     * to return a value of 0 or 255 as the most frequent value of a segmented object's pixels which are compared by a threshold value
//     */
//    public int getAdaptiveBinaryThresholdObect() {
//    	FastStatisticsGrey solver = new FastStatisticsGrey();
//    	for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData(image.getAdaptiveBinaryThreshold(p.x,p.y));
//                }
//            }
//    	}
//        return solver.getMaxNIndex();
//    }
//    
//    /**
//     * to get the standard deviation of greyness of a object
//     */
//    public double getStdDeviation() {
//        FastStatistics solver = new FastStatistics();
//        for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//                	solver.addData(image.getGreyValue(p.x, p.y));
//                }
//            }
//        }
//        return solver.getStandardDeviation();
//    }
//    
//    protected IntegralImage integralImage;
//
//    public IntegralImage getIntegralImage(PixelLoader image, ExtraShapeData img) {
//        if (integralImage == null) {       	
//            integralImage = new IntegralImage(image, img);
//        }
//        return integralImage;
//    }
//    
//    public FastStatistics get3x3StatsMean(int channel) {
//        return get3x3StatsMean(channel, 1);
//    }
//
//    public FastStatistics get3x3StatsMean(int channel, int size) {
//
//        FastStatistics solver = new FastStatistics();
//        
//        for (int y = 0; y < boundingHeight; y++) {
//            for (int x = 0; x < boundingWidth; x++) {
//                ShapePixel p = array[x][y];
//                if (p != null) {
//
//			        if (p.y - size < 0) return solver;
//			        if (p.y + size > (getHeight() - 1)) return solver;
//			        if (p.x - size < 0) return solver;
//			        if (p.x + size > (getWidth() - 1)) return solver;
//
//System.err.println("CACHING_OTHERS="+CACHING_OTHERS); 			
//			        //POEY comment: for segmentation CACHING_OTHERS = false
//			        if (CACHING_OTHERS) {        	
//			            switch (channel) {
//			                case ColourChannels.HUE:
//			                case ColourChannels.SATURATION:
//			                case ColourChannels.LIGHTNESS:
//			                    if (hueCache == null) createHSLCache();
//			                default:
//			                    if (redColourCache == null) createRGBCache();
//			                    break;
//			            }
//			
//			            int C = ((y - size) * width) + (x - size);
//			            int windowSize = (size * 2) + 1;
//			
//			            for (int dy = 0; dy < windowSize; dy++) {
//			                int c = C;
//			                for (int dx = 0; dx < windowSize; dx++) {
//			                    switch (channel) {
//			                        case ColourChannels.NORMALISED_RED:
//			                            solver.addData(normalisedRGBCache[c][0]);
//			                            break;
//			                        case ColourChannels.NORMALISED_GREEN:
//			                            solver.addData(normalisedRGBCache[c][1]);
//			                            break;
//			                        case ColourChannels.NORMALISED_BLUE:
//			                            solver.addData(normalisedRGBCache[c][2]);
//			                            break;
//			                        case ColourChannels.C1:
//			                            solver.addData(c1c2c3Cache[c][0]);
//			                            break;
//			                        case ColourChannels.C2:
//			                            solver.addData(c1c2c3Cache[c][1]);
//			                            break;
//			                        case ColourChannels.C3:
//			                            solver.addData(c1c2c3Cache[c][2]);
//			                            break;
//			                        case ColourChannels.L1:
//			                            solver.addData(l1l2l3Cache[c][0]);
//			                            break;
//			                        case ColourChannels.L2:
//			                            solver.addData(l1l2l3Cache[c][1]);
//			                            break;
//			                        case ColourChannels.L3:
//			                            solver.addData(l1l2l3Cache[c][2]);
//			                            break;
//			                        case ColourChannels.HUE:
//			                            solver.addData(hueCache[c]);
//			                            break;
//			                        case ColourChannels.SATURATION:
//			                            solver.addData(satCache[c]);
//			                            break;
//			                        case ColourChannels.LIGHTNESS:
//			                            solver.addData(lightnessCache[c]);
//			                            break;
//			                        case ColourChannels.GREYSCALE:
//			                            solver.addData(greyColourCache[c]);
//			                            break;
//			                        case ColourChannels.RED:
//			                            solver.addData((float) redColourCache[c]);
//			                            break;
//			                        case ColourChannels.GREEN:
//			                            solver.addData((float) greenColourCache[c]);
//			                            break;
//			                        case ColourChannels.BLUE:
//			                            solver.addData((float) blueColourCache[c]);
//			                            break;
//			                        case ColourChannels.EDGE:
//			                            try {
//			                            solver.addData((float) getEdgeMagnitude(x+dx, y+dy));
//			                            } catch (Exception e) {}
//			                            break;
//			                    }
//			                    c++;
//			                }
//			                C += width;
//			            }
//			        } else { 
//			            for (int dy = -size; dy <= size; dy++) {
//			                int newY = y + dy;
//			                for (int dx = -size; dx <= size; dx++) {
//			                	//POEY comment: for segmentation by AdaotuveBinaryThreshold, channel = GREYSCALE
//			                	//for segmentation by GenericN*NFeature, calculate 14 channels
//			                	//compare channel by name not index
//			                    switch (channel) {
//			                        case ColourChannels.NORMALISED_RED:	
//			                            solver.addData((float) getNormalisedRed(x + dx, newY));
//			                            break;
//			                        case ColourChannels.NORMALISED_GREEN:	
//			                            solver.addData((float) getNormalisedGreen(x + dx, newY));                            
//			                            break;
//			                        case ColourChannels.NORMALISED_BLUE:	
//			                            solver.addData((float) getNormalisedBlue(x + dx, newY));  
//			                            break;
//			                        case ColourChannels.C1:	
//			                            solver.addData((float) getC1C2C3(x + dx, newY, 0));   
//			                            break;
//			                        case ColourChannels.C2:	
//			                            solver.addData((float) getC1C2C3(x + dx, newY, 1));   
//			                            break;
//			                        case ColourChannels.C3:	
//			                            solver.addData((float) getC1C2C3(x + dx, newY, 2));   
//			                            break;
//			                        case ColourChannels.L1:	
//			                            solver.addData((float) getL1L2L3(x + dx, newY, 0));   
//			                            break;
//			                        case ColourChannels.L2:	
//			                            solver.addData((float) getL1L2L3(x + dx, newY, 1));   
//			                            break;
//			                        case ColourChannels.L3:	
//			                            solver.addData((float) getL1L2L3(x + dx, newY, 2));  
//			                            break;
//			                        case ColourChannels.HUE:	
//			                            solver.addData(getHue(x, y));   
//			                            break;
//			                        case ColourChannels.SATURATION:	
//			                            solver.addData(getSaturation(x + dx, newY));   
//			                            break;
//			                        case ColourChannels.LIGHTNESS:	
//			                            solver.addData(getLightness(x + dx, newY));   
//			                            break;
//			                        case ColourChannels.GREYSCALE:	            	
//			                            solver.addData(getGreyValue(x + dx, newY)); 
//			                            break;
//			                        case ColourChannels.RED:	
//			                            solver.addData(getRed(x + dx, newY));  
//			                            break;
//			                        case ColourChannels.GREEN:	
//			                            solver.addData(getGreen(x + dx, newY));   
//			                            break;
//			                        case ColourChannels.BLUE:	
//			                            solver.addData(getBlue(x + dx, newY));  
//			                            break;
//			                        case ColourChannels.EDGE:	
//			                            solver.addData((float) getEdgeMagnitude(x+dx, newY));  
//			                            break;
//			                    }
//			                }
//			            }
//			        }
//			        return solver;
//                }
//            }
//        }
//        return solver;
//    }
//    
//    private int[] hueCache;
//    private int[] satCache;
//    private int[] lightnessCache;   
//
//    public int getHue(int x, int y) {
//        if (CACHE_RGB_HSL) {
//            if (hueCache == null) createHSLCache();
//            int c = (y * boundingWidth) + x;
//            return hueCache[c];
//        } else {
//        	//POEY comment: for segmentation and classification, CACHE_RGB_HSL = false
//        	return ColourConvertor.RGB2HSL(image.getRGB(x, y))[0];
//        }
//    }
//
//    public int getSaturation(int x, int y) {
//        if (CACHE_RGB_HSL) {
//            if (satCache == null) createHSLCache();
//            int c = (y * width) + x;
//            return satCache[c];
//        } else {
//            return ColourConvertor.RGB2HSL(img.getRGB(x, y))[1];
//        }
//    }
//
//    public int getLightness(int x, int y) {
//        if (CACHE_RGB_HSL) {
//            if (lightnessCache == null) createHSLCache();
//            int c = (y * width) + x;
//            return lightnessCache[c];
//        } else {
//            return ColourConvertor.RGB2HSL(img.getRGB(x, y))[2];
//        }
//    }
//    
//    private void createHSLCache() {
//
//        int numPixels = s.pixels.size();
//
//        hueCache = new int[numPixels];
//        satCache = new int[numPixels];
//        lightnessCache = new int[numPixels];
//
//        int c = 0;
//
//        for (int yPos = 0; yPos < boundingHeight; yPos++) {
//            for (int xPos = 0; xPos < boundingWidth; xPos++) {
//            	ShapePixel p = array[xPos][yPos];
//                if (p != null) {
//	                int[] hsl = ColourConvertor.RGB2HSL(image.getRGB(xPos, yPos));
//	
//	                hueCache[c] = hsl[0];
//	                satCache[c] = hsl[1];
//	                lightnessCache[c] = hsl[2];
//	
//	                c++;
//                }
//            }
//        }
//    }
//    
//    private int rMax, rMin, gMax, gMin, bMax, bMin;
//
//    private int[] greyColourCache;
//    private int[] redColourCache;
//    private int[] greenColourCache;
//    private int[] blueColourCache;
//    private float[][] c1c2c3Cache;
//    private float[][] normalisedRGBCache;
//    private float[][] l1l2l3Cache;
//
//    final double rx = 0.299;
//    final double gx = 0.587;
//    final double bx = 0.114;
//
//
//    public void clearCaches() {
//        greyColourCache = null;
//        redColourCache = null;
//        greenColourCache = null;
//        blueColourCache = null;
//        c1c2c3Cache = null;
//        l1l2l3Cache = null;
//        meanCache = null;
//        varianceCache = null;
//        hueCache = null;
//        satCache = null;
//        lightnessCache = null;
//        normalisedRGBCache = null;
//        vsobelCache = null;
//        hsobelCache = null;
//        laplacianCache = null;
//    }
//
//    
//    private void createRGBCache() {
//
//        rMax = 0;
//        gMax = 0;
//        bMax = 0;
//        rMin = 255;
//        gMin = 255;
//        bMin = 255;
//
//
//        int numPixels = s.pixels.size();
//
//        greyColourCache = new int[numPixels];
//        redColourCache = new int[numPixels];
//        greenColourCache = new int[numPixels];
//        blueColourCache = new int[numPixels];
//        if (CACHING_OTHERS) {
//            c1c2c3Cache = new float[numPixels][3];
//            l1l2l3Cache = new float[numPixels][3];
//            normalisedRGBCache = new float[numPixels][3];
//        }
//
//        int c = 0;
//
//        for (int y = 0; y < boundingHeight; y++) {
//
//            for (int x = 0; x < boundingWidth; x++) {
//            	ShapePixel p = array[x][y];
//                if (p != null) {
//
//	                // get a colour object, which saves us having to shift bits and other stuff.
//	                int rgb = image.getRGB(p.x, p.y);
//	
//	                // extract the colours
//	                int red = (rgb >> 16) & 0xFF;
//	                int green = (rgb >> 8) & 0xFF;
//	                int blue = rgb & 0xFF;
//	
//	                if (red > rMax) {
//	                    rMax = red;
//	                }
//	                if (red < rMin) {
//	                    rMin = red;
//	                }
//	                if (green > gMax) {
//	                    gMax = green;
//	                }
//	                if (green < gMin) {
//	                    gMin = green;
//	                }
//	                if (blue > bMax) {
//	                    bMax = blue;
//	                }
//	                if (blue < bMin) {
//	                    bMin = blue;
//	                }
//	
//	                greyColourCache[c] = (int) ((red * rx) + (green * gx) + (blue * bx));
//	                redColourCache[c] = red;
//	                greenColourCache[c] = green;
//	                blueColourCache[c] = blue;
//	
//	                if (CACHING_OTHERS) {
//	                    // avoid divide by zero
//	                    red++;
//	                    green++;
//	                    blue++;
//	
//	                    int maxGB = Math.max(green, blue);
//	                    int maxRB = Math.max(red, blue);
//	                    int maxRG = Math.max(red, green);
//	
//	                    c1c2c3Cache[c][0] = (float) Math.atan(maxGB != 0 ? red / maxGB : 0);
//	                    c1c2c3Cache[c][1] = (float) Math.atan(maxRB != 0 ? green / maxRB : 0);
//	                    c1c2c3Cache[c][2] = (float) Math.atan(maxRG != 0 ? blue / maxRG : 0);
//	
//	                    float sum = red + green + blue;
//	
//	                    normalisedRGBCache[c][0] = red / sum;
//	                    normalisedRGBCache[c][1] = green / sum;
//	                    normalisedRGBCache[c][2] = blue / sum;
//	
//	                    float rMinusGSquared = (red - green) ^ 2;
//	                    float rMinusBSquared = (red - blue) ^ 2;
//	                    float gMinusBSquared = (green - blue) ^ 2;
//	
//	                    float M = rMinusGSquared + rMinusBSquared + gMinusBSquared;
//	                    l1l2l3Cache[c][0] = rMinusGSquared / M;
//	                    l1l2l3Cache[c][1] = rMinusBSquared / M;
//	                    l1l2l3Cache[c][2] = gMinusBSquared / M;
//	                }
//	
//	                c++;
//                }
//
//            }
//
//        }
//    }
//
//                
//
//}
//

