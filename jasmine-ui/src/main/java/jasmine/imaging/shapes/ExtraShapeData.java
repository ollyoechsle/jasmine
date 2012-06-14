package jasmine.imaging.shapes;


import jasmine.imaging.commons.ColourChannels;
import jasmine.imaging.commons.ColourConvertor;
import jasmine.imaging.commons.FastStatistics;
import jasmine.imaging.commons.FastStatisticsGrey;
import jasmine.imaging.commons.IntegralImage;
import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.TextureStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * This class contains the methods for calculating a variety of different measures about a shape.
 * The raw information about the shape (ie which pixels are part of the shape) are stored in instances
 * of SegmentedShape.
 *
 * @author Olly Oechsle, University of Essex, Date: 25-Apr-2007
 * @version 1.0
 */
public final class ExtraShapeData {
	
	//POEY
    public static boolean CACHING_OTHERS = true;
    public static boolean CACHE_RGB_HSL = true;
    
	/**
     * These points represent the point where there is an end. You would assume a hand shape to have five ends.
     * They are discovered following skeletonisation.
     */
    protected Vector<Pixel> ends;

    /**
     * These points represent the points where there is a joint. They are discovered
     * following skeletonisation.
     */
    protected Vector<Pixel> joints;

    /**
     * The ExtraShapeData class is a wrapper around the SegmentedShape class, which is a lightweight, serializable
     * class that only contains only the most basic shape information (the pixels that make up the shape).
     */
    protected SegmentedShape s;

    protected PixelLoader image;

    /**
     * Initialises the object with a reference to the shape about which analysis is required.
     */
    public ExtraShapeData(SegmentedShape s, PixelLoader image) {
        this.s = s;
        this.image = image;
        compile();
    }

    public PixelLoader getImage() {
        return image;
    }

    /**
     * Returns the shape data that this class wraps around.
     */
    public SegmentedShape getShape() {
        return s;
    }

    /**
     * Draws the shape onto an image so that you can visualise it in a GUI or elsewhere.
     */
    public void draw(BufferedImage img, int color) {
        for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                    img.setRGB(p.x, p.y, color);
                }
            }
        }
    }

    /**
     * Draws the shape onto an image so that you can visualise it in a GUI or elsewhere.
     */
    public BufferedImage toImage() {
        int color = Color.WHITE.getRGB();
        BufferedImage image = new BufferedImage(boundingWidth, boundingHeight, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                    image.setRGB(x, y, color);
                }
            }
        }
        return image;
    }

    public BufferedImage getImageDepth() {
        int maxDepth = getMaxDepth();
        BufferedImage image = new BufferedImage(boundingWidth, boundingHeight, BufferedImage.TYPE_INT_ARGB);
        try {
            for (int y = 0; y < boundingHeight; y++) {
                for (int x = 0; x < boundingWidth; x++) {
                    ShapePixel p = array[x][y];
                    if (p != null) {
                        double dp = depths[x][y] / (double) maxDepth;
                        int c = (int) (dp * 255);
                        image.setRGB(x, y, new Color(c, c, c).getRGB());
                    }                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public BufferedImage getImage(BufferedImage original) {
        int color = Color.BLACK.getRGB();
        BufferedImage image = new BufferedImage(boundingWidth, boundingHeight, BufferedImage.TYPE_INT_ARGB);
        try {
            for (int y = 0; y < boundingHeight; y++) {
                for (int x = 0; x < boundingWidth; x++) {
                    ShapePixel p = array[x][y];
                    if (p != null) {
                        image.setRGB(x, y, original.getRGB(p.x, p.y));
                    }
                    // overlay the skeleton on top
                    if (skeletonArray != null) {
                        p = skeletonArray[x][y];
                        if (p != null) {
                            image.setRGB(x, y, color);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Returns the classID of the shape. This is a label that we can attach to the shape for training
     * purposes.
     */
    public int getClassID() {
        return s.classID;
    }

    /**
     * Sets the classID of the shape
     */
    public void setClassID(int classID) {
        s.classID = classID;
    }


    /**
     * Returns the mass of the shape, which is easily calculated as the number of pixels in the shape.
     * Each pixel is assumed to weigh one "unit".
     */
    public int getMass() {
        return s.getMass();
    }

    /**
     * Returns the bounding width of the shape.
     * Note that this is not rotationally invariant. Ideally the bounding width and height would
     * be calculated from the minimum bounding box around the shape, something that I never got round to.
     */
    public int getWidth() {
        return boundingWidth;
    }

    /**
     * Returns the bounding height of the shape.
     * Note that this is not rotationally invariant. Ideally the bounding width and height would
     * be calculated from the minimum bounding box around the shape, something that I never got round to.
     */
    public int getHeight() {
        return boundingHeight;
    }

    /**
     * After everything is added, and the compile function is completed the data is stored in this array.
     * It essentially allows spatial information about a pixel and its neighbours to be accessed easily.
     */
    public ShapePixel[][] array;

    /* This is for the skeletonisation */
    public ShapePixel[][] skeletonArray;
    public int[][] depths;

    /**
     * The width of the bounding box surrounding this shape
     */
    public int boundingWidth = -1;

    /**
     * The height of the bounding box surrounding this shape
     */
    public int boundingHeight = -1;

    /**
     * A basic true false map of what is shape and what is not. The main array
     * gets mucked about with by the skeletonisation but we need to still know
     * where the shape starts and ends (when joining up in skeletonisation)
     */
    protected boolean[][] insideShape;

    public void fillIn() {
        Vector<Perimeter> p = getPerimeters();
        if (p != null && p.size() > 0) {
            p.elementAt(0).fillIn(this);
        }
    }
    
    //POEY
    //colour storage from the image's shape
    private int[][] greyImage;
    private int[][] hueImage;
    private int[][] satImage;
    private int[][] lightImage;
    private int[][] redImage;
    private int[][] greenImage;
    private int[][] blueImage;
    
    private ArrayList<Integer> greyColour = new ArrayList<Integer>();
    private ArrayList<Integer> hueColour = new ArrayList<Integer>();
    private ArrayList<Integer> satColour = new ArrayList<Integer>();
    private ArrayList<Integer> lightColour = new ArrayList<Integer>();
    private ArrayList<Integer> redColour = new ArrayList<Integer>();
    private ArrayList<Integer> greenColour = new ArrayList<Integer>();
    private ArrayList<Integer> blueColour = new ArrayList<Integer>();

    /**
     * Compiles the shape pixels into a 2D array. Once it is in this configuration,
     * it's a lot easier to do some of the calculations we're about to do because
     * it restores spatial information which is lost if you store the shape as a series
     * of independent "pixel" objects.
     * Once the array has been made, it runs the findPerimeters() function which locates
     * the shape edges.
     */
    protected void compile() {

        // calculate the bounding width and height (simplistically)
    	//POEY comment: s.maxX, s.minX, s.maxY and s.minY belongs to a segmented object
        boundingWidth = s.maxX - s.minX + 1;
        boundingHeight = s.maxY - s.minY + 1;
        // make the array slightly oversize so the perimeter finder methods don't have to
        // do any upper bounds checks
        array = new ShapePixel[boundingWidth + 1][boundingHeight + 1];
        depths = new int[boundingWidth + 1][boundingHeight + 1];
        //second array that keeps the original array even after skeletonisation
        insideShape = new boolean[boundingWidth + 1][boundingHeight + 1];
        
        //POEY
        greyImage = new int[boundingWidth][boundingHeight];
        hueImage = new int[boundingWidth][boundingHeight];
        satImage = new int[boundingWidth][boundingHeight];
        lightImage = new int[boundingWidth][boundingHeight];
        redImage = new int[boundingWidth][boundingHeight];
        greenImage = new int[boundingWidth][boundingHeight];
        blueImage = new int[boundingWidth][boundingHeight];

        //POEY comment: s.pixels.size() = the number of pixels of a segmented object
        for (int i = 0; i < s.pixels.size(); i++) {
            ShapePixel pixel = s.pixels.elementAt(i);
            //POEY comment: pixel = a coordinate of a pixel of a segmented object
            //to change the object's coordinates to be stored in an array with start at [0][0] and end at [boundingWidth + 1][boundingHeight + 1]
            array[pixel.x - s.minX][pixel.y - s.minY] = pixel;	           
            insideShape[pixel.x - s.minX][pixel.y - s.minY] = true;
        }

        // and find all the perimeters - we need this for the next set
        perimeters = findPerimeters();
/*
        if (perimeters == null) {
            throw new RuntimeException("Perimeters are null");
        }
*/

    }


    /**
     * Pixel that stores the shape's center of gravity.
     */
    protected Pixel cog = null;


    public Vector<Pixel> getSmoothedPixels() {
        Vector<Pixel> smoothedPixels = new Vector<Pixel>(100);
        for (int x = 1; x < boundingWidth - 1; x++) {
            for (int y = 1; y < boundingHeight - 1; y++) {
                if (array[x][y] != null) {
                    int count = 0;
                    if (array[x + 1][y] != null) count++;
                    if (array[x + 1][y - 1] != null) count++;
                    if (array[x][y - 1] != null) count++;
                    if (array[x - 1][y - 1] != null) count++;
                    if (array[x - 1][y] != null) count++;
                    if (array[x - 1][y + 1] != null) count++;
                    if (array[x][y + 1] != null) count++;
                    if (array[x + 1][y + 1] != null) count++;
                    if (count >= 6) {
                        smoothedPixels.add(array[x][y]);
                    }
                }
            }
        }
        return smoothedPixels;
    }

    public int getMaxDepth() {
        if (skeletonArray == null) skeletonise();
        int maxDepth = 0;
        for (int x = 1; x < boundingWidth - 1; x++) {
            for (int y = 1; y < boundingHeight - 1; y++) {
                if (array[x][y] != null) {
                    if (depths[x][y] > maxDepth) maxDepth = depths[x][y];
                }
            }
        }
        return maxDepth;
    }


    public double getAverageDepth() {
        if (skeletonArray == null) skeletonise();
        FastStatistics fs = new FastStatistics();
        for (int x = 1; x < boundingWidth - 1; x++) {
            for (int y = 1; y < boundingHeight - 1; y++) {
                if (array[x][y] != null) {
                    fs.addData(depths[x][y]);
                }
            }
        }
        return fs.getMean();
    }


    /**
     * Finds the shape's center of gravity. This is the point where the shape is equally balanced
     * in both the X and Y directions.
     */
    public Pixel getCentreOfGravity() {
        if (cog == null || cog.x == -1) {
            int cogX = (int) getCentreOfGravityX();
            int cogY = getCentreOfGravityY();
            cog = new Pixel(cogX, cogY);
        }
        return cog;
    }

    /**
     * Calculates the center of gravity of the shape in the horizontal direction, relative to origin of shape.
     */
    public int getCentreOfGravityX() {

        // calculate how many pixels is half
        int halfWeight = s.totalPixels / 2;

        // tot up the weight from one side
        int weight = 0;

        // work from the left hand side, adding up the pixels
        for (int x = 0; x < boundingWidth; x++) {
            for (int y = 0; y < boundingHeight; y++) {
                if (array[x][y] != null) weight++;
            }
            if (weight >= halfWeight) return x;
        }

        return -1;

    }

    /**
     * Calculates the center of gravity of the shape in the horizontal direction, relative to origin of shape.
     */
    public int getCentreOfGravityY() {

        // calculate how many pixels is half
        int halfWeight = s.totalPixels / 2;

        // tot up the weight from one side
        int weight = 0;

        // work from the left hand side, adding up the pixels
        for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                if (array[x][y] != null) weight++;
            }
            if (weight >= halfWeight) return y;
        }

        return -1;

    }

    // Gets the distance from the right side to the first pixel it encounters
    public double getDistFromRight() {
        int y = boundingHeight / 2;
        for (int x = boundingWidth - 1; x >= 0; x--) {
            if (array[x][y] != null) {
                return ((boundingWidth - x) / (double) boundingWidth);
            }
        }
        return 1;
    }

    public double getDistFromLeft() {
        int y = boundingHeight / 2;
        for (int x = 0; x < boundingWidth; x++) {
            if (array[x][y] != null) {
                return x / (double) boundingWidth;
            }
        }
        return 1;
    }

    public double getDistFromBottom() {
        int x = boundingWidth / 2;
        for (int y = boundingHeight - 1; y >= 0; y--) {
            if (array[x][y] != null) {
                return ((boundingHeight - y) / (double) boundingHeight);
            }
        }
        return 1;
    }

    public double getAverage(int blocksX, int blocksY, int x, int y) {
        int blockWidth = boundingWidth / blocksX;
        int blockHeight = boundingHeight / blocksY;
        int startX = x * blockWidth;
        int startY = y * blockHeight;
        int N = 0;
        int total = 0;
        for (int sy = startY; sy < (startY + blockWidth) && sy < boundingHeight; sy++)
            for (int sx = startX; sx < (startX + blockWidth) && sx < boundingWidth; sx++) {
                if (array[sx][sy] != null) total++;
                N++;
            }
        return N / (double) total;
    }

    // Gets data direct from the image about the shape
    public double getAverage(int blocksX, int blocksY, int x, int y, PixelLoader image, int statistic) {
        int blockWidth = boundingWidth / blocksX;
        int blockHeight = boundingHeight / blocksY;
        int startX = x * blockWidth;
        int startY = y * blockHeight;
        FastStatistics stat = new FastStatistics();
        for (int sy = startY; sy < (startY + blockWidth) && sy < boundingHeight; sy++)
            for (int sx = startX; sx < (startX + blockWidth) && sx < boundingWidth; sx++) {
                ShapePixel p = array[sx][sy];
                //if (p != null) {
                    stat.addData(image.getGreyValue(p.x, p.y));
                //}
            }
        return stat.getStatistic(statistic);
    }

    public float getIntensityStatistic(PixelLoader image, int statistic) {
        FastStatistics stat = new FastStatistics();
        for (int x = 1; x < boundingWidth - 1; x++) {
            for (int y = 1; y < boundingHeight - 1; y++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                    stat.addData(image.getGreyValue(p.x, p.y));
                }
            }
        }
        return stat.getStatistic(statistic);
    }

    public double getDistFromTop() {
        int x = boundingWidth / 2;
        for (int y = 0; y < boundingHeight; y++) {
            if (array[x][y] != null) {
                return ((y) / (double) boundingHeight);
            }
        }
        return 1;
    }

    /**
     * The number of corners found in the shape (CACHE)
     */
    private int corners = -1;

    /**
     * Returns the number of corners found in the shape.
     */
    public int countCorners() {
        if (corners == -1) {
            // get radiuses method finds corners. Don't know why I called the method that.
            getRadiuses();
        }
        return corners;
    }
    
    public Vector<Double> getRadiuses() {

        // find the middle of the shape
        Pixel cog = getCentreOfGravity();

        // get the outer perimeter
        Perimeter p = perimeters.elementAt(0);

        // store a value for every pixel
        //POEY comment: p.pixels.size() = the number of object's pixels which are on the object's perimeter        
        double values[] = new double[p.pixels.size()];

        // find the dist of each pixel from the center
        double highest = -1;
       
        for (int i = 0; i < p.pixels.size(); i++) {
            ShapePixel pixel = p.pixels.elementAt(i);
            values[i] = dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y);
            if (highest == -1 || values[i] > highest) highest = values[i];
        }

        // normalise values so that we deal in percentage terms, this way the value outputted by this
        // method will be the same regardless of the shape's size.
        for (int i = 0; i < values.length; i++) {
            values[i] /= highest;
        }

        // crude differentiation
        Vector<Double> differentiatedValues = new Vector<Double>(100);
        double lastValue = -1;
        for (int i = 0; i < values.length; i += 2) {
            if (i > 0) {
                double diff = (values[i] - lastValue);
                differentiatedValues.add(diff);
            }
            lastValue = values[i];
        }

        corners = 0;

        final int smoothness = 1;

        // smooth the graph
        Vector<Double> smoothedValues = new Vector<Double>(100);
        for (int i = 0; i < differentiatedValues.size(); i++) {

            double total = 0;
            double numValues = 0;

            for (int s = -smoothness; s <= smoothness; s++) {
                int dI = i + s;
                if (dI > 0 && dI < differentiatedValues.size()) {
                    total += differentiatedValues.elementAt(dI);
                    numValues++;
                }
            }

            double average = total / numValues;
            // found a corner
            if (smoothedValues.size() > 0 && smoothedValues.lastElement() < 0 && average >= 0) {
                corners++;
            }

            smoothedValues.add(average);
        }

        //return values;
        return smoothedValues;
    }

    /**
     * Returns the number of hollows, or holes, in this particular shape.
     * Dependent upon the compile method having been called first, which initiated
     * the findPerimeters() method.
     */
    public int countHollows() {
        if (perimeters == null) {
            // should never happen as the perimeters object is defined
            // during the compile() method which executes on initialisation.
            System.out.println("Cannot count hollows: Perimeters is null.");
        }
        return perimeters.size() - 1;
    }

    /**
     * The distance in the X direction from the actual centre of gravity to the bounding rectangle's
     * centre of gravity.
     * Todo: not scale invariant and possibly not rotation invariant
     */
    public double getBalanceX() {
        return (getCentreOfGravity().x - getBoundingRectangleCentreOfGravity().x) / ((double) boundingWidth);
    }

    /**
     * The distance in the Y direction from the actual centre of gravity to the bounding rectangle's
     * centre of gravity.
     * Todo: not scale invariant and possibly not rotation invariant
     */
    public double getBalanceY() {
        return (getCentreOfGravity().y - getBoundingRectangleCentreOfGravity().y) / ((double) boundingHeight);
    }

    // Another cached value, saves re-calculating it
    private Pixel boundingRectangleCentreOfGravity = null;

    /**
     * Gets the bounding rectangle's centre of gravity
     * which may be different to the shape's actual center of gravity.
     */
    public Pixel getBoundingRectangleCentreOfGravity() {
        if (boundingRectangleCentreOfGravity == null) {
            if (boundingWidth == -1) compile();
            boundingRectangleCentreOfGravity = new Pixel(boundingWidth / 2, boundingHeight / 2);
        }
        return boundingRectangleCentreOfGravity;
    }

    // Another cached value, saves re-calculating it
    private double density = -1;

    /**
     * Density is a percentage of the volume of the shape, minus
     * the volumes of any hollow holes inside the shape.
     */
    public double getDensity() {

        if (density == -1) {
            density = s.getMass() / (double) getVolume();
        }

        return density;

    }

    // Another cached value, saves re-calculating it
    private int volume = -1;
    private double averageHollowSize = -1;

    public double getAverageHollowSize() {
        if (averageHollowSize == -1) {
            getVolume();
        }
        return averageHollowSize;
    }


    /**
     * If a shape has hollow areas in it, then its volume is higher than its mass. This method
     * calculates the volume of a shape by finding how many pixels there are within its outer perimeter.
     *
     * @return
     */
    public int getVolume() {

        if (volume == -1) {

            if (perimeters == null || perimeters.size() == 1) {
                volume = s.totalPixels;
                averageHollowSize = 0;
            } else {

                // density = mass / volume

                // count up hollow areas
                int totalHollowArea = 0;
                for (int i = 1; i < perimeters.size(); i++) {
                    Perimeter hollow = perimeters.elementAt(i);
                    totalHollowArea += hollow.pixelsInsidePerimeter;
                }

                // the average size of hollows as a percentage of the shape's mass
                averageHollowSize = ((double) totalHollowArea / (perimeters.size() - 1)) / getMass();

                volume = s.totalPixels + totalHollowArea;
            }

        }
        
        return volume;

    }

    // CACHE
    private double balanceXRightVariance = -1;

    /**
     * Gets the variance of edge pixels on the right hand side of the centre of gravity.
     */
    public double getBalanceXRightVariance() {

        if (balanceXRightVariance == -1) {

            Pixel cog = getCentreOfGravity();

            try {

                FastStatistics solver = new FastStatistics();

                // go down from the top
                for (int y = 0; y < boundingHeight; y++) {
                    // look to the right of cog
                    int furthestRight = -1;
                    for (int x = cog.x; x < boundingWidth; x++) {
                        if (array[x][y] != null) {
                            if (x > furthestRight) furthestRight = x;
                        }
                    }
                    if (furthestRight != -1) {
                        furthestRight -= cog.x;
                        solver.addData(furthestRight);
                    }
                }

                balanceXRightVariance = solver.getStandardDeviation();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Can't get BXRV: " + cog.x);
            }

        }

        return balanceXRightVariance;

    }

    // CACHE
    private double balanceXLeftVariance = -1;

    /**
     * Gets the variance of edge pixels on the left hand side of the centre of gravity.
     */
    public double getBalanceXLeftVariance() {

        if (balanceXLeftVariance == -1) {

            Pixel cog = getCentreOfGravity();

            FastStatistics solver = new FastStatistics();

            // go down from the top
            for (int y = 0; y < boundingHeight; y++) {
                // look to the right of cog
                int furthestLeft = -1;
                for (int x = 0; x < cog.x; x++) {
                    if (array[x][y] != null) {
                        if (furthestLeft == -1 || x < furthestLeft) furthestLeft = x;
                    }
                }
                if (furthestLeft != -1) {
                    furthestLeft = cog.x - furthestLeft;
                    solver.addData(furthestLeft);
                }
            }

            balanceXLeftVariance = solver.getStandardDeviation();

        }

        return balanceXLeftVariance;

    }

    // CACHE
    private Pixel furthestPixelFromCentre = null;

    /**
     * Finds the pixel that is furthest from the centre (of gravity). Quite useful if you have a shape,
     * such as a silhouette of a pointing finger, and you want to know where it is pointing to.
     */
    public Pixel getFurthestPixelFromCentre() {

        if (furthestPixelFromCentre == null) {
            Pixel cog = getCentreOfGravity();
            double furthest = -1;
            for (int i = 0; i < s.edgePixels.size(); i++) {
                ShapePixel pixel = s.edgePixels.elementAt(i);
                //if ((pixel.x - s.minX) < cog.x) {
                double d = dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y);
                if (d > furthest) {
                    furthest = d;
                    furthestPixelFromCentre = pixel;
                }
                //}
            }
        }
        return furthestPixelFromCentre;
    }

    // CACHE
    private double roundness = -1;

    /**
     * Returns the roundness of the shape, which is the variance of the distances
     * of the edge pixels from the center.
     */
    public double getRoundness() {

        if (roundness == -1) {

            // find the centre of the shape
            Pixel cog = getCentreOfGravity();

            // for each edge pixel - find the distance from the centre of gravity
            FastStatistics solver = new FastStatistics();

            Perimeter edge = perimeters.elementAt(0);
            for (int i = 0; i < edge.pixels.size(); i++) {
                Pixel pixel = edge.pixels.elementAt(i);
                solver.addData(dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y));
            }

            // crude way of getting some scale invariance
            return solver.getStandardDeviation() / solver.getMean();

        }

        return roundness;

    }

    /**
     * TODO: Implement this method!
     */
    public int getEndsBeneathCog() {
        return -1;
    }

    // CACHE
    private double aspectRatio = -1;

    /**
     * Gets the aspect ratio of the shape. Note that this aspect ratio is relative to the bounding box
     * which is not rotated, hence the aspect ratio of rotated shapes may be wrong.
     */
    public double getAspectRatio() {

        if (aspectRatio == -1) {
            if (boundingWidth == -1) {
                System.err.println("Can't Get Aspect Ratio: BOUNDING BOX WAS NOT INITIALISED");
                compile();
            }

            // TODO - This is accurate but is not rotation invariant.
            aspectRatio = boundingWidth / (double) boundingHeight;

        }

        return aspectRatio;

    }

    // CACHE
    private Hashtable<Integer, Float> roughnessCache = null;

    /**
     * If a shape is smooth, there will always be a low differential between radii.
     * Roughness is scale dependent of course, like a fractal, so this function allows
     * you to find roughness at different levels of "zoom"
     *
     * @return
     */
    public float getRoughness(int step) {

        if (roughnessCache == null) {
            roughnessCache = new Hashtable<Integer, Float>();
        }

        // Get cached entry
        Float cached = roughnessCache.get(step);

        if (cached == null) {
            Pixel cog = getCentreOfGravity();
            FastStatistics solver = new FastStatistics();
            Perimeter p = perimeters.elementAt(0);
            float lastValue = -1;
            for (int i = 0; i < p.pixels.size(); i += step) {
                Pixel pixel = p.pixels.elementAt(i);
                float radius = dist(pixel.x - s.minX, pixel.y - s.minY, cog.x, cog.y);
                if (lastValue > -1) {
                    float diff = radius - lastValue;
                    solver.addData(diff);
                }
                lastValue = radius;
            }
            cached = solver.getStandardDeviation();

            if (cached.isNaN()) {
                //System.out.println("Roughness is NaN, for some reason...");
                cached = 0f;
            }

            roughnessCache.put(step, cached);
        }

        return cached;

    }

    // CACHE
    private double verticalSymmetry = -1;

    /**
     * Calculates the symmetry, if you put a mirror up on the vertical axis.
     * Not very advanced, but gets the job done.
     */
    public double getVerticalSymmetry() {

        if (cog == null) {
            getCentreOfGravity();
            verticalSymmetry = -1;
        }

        if (verticalSymmetry == -1) {

            double symmetricPixelCount = 0;

            for (int y = 0; y < boundingHeight; y++) {
                for (int x = 0; x <= (boundingWidth / 2); x++) {

                    // ensure both pixels are in bounds
                    if ((cog.x + x) < boundingWidth && (cog.x - x) >= 0) {
                        boolean rightPixel = insideShape[cog.x + x][y];
                        boolean leftPixel = insideShape[cog.x - x][y];
                        if (rightPixel && rightPixel == leftPixel) {
                            if (x == 0) {
                                // central pixel is true
                                symmetricPixelCount++;
                            } else {
                                // two matching pixels
                                symmetricPixelCount += 2;
                            }
                        }
                    }

                }
            }

            verticalSymmetry = symmetricPixelCount / s.totalPixels;

        }

        return verticalSymmetry;

    }

    protected double horizontalSymmetry = -1;

    /**
     * Calculates the symmetry, if you put a mirror up on the horizontal axis.
     * Not very advanced, but gets the job done.
     */
    public double getHorizontalSymmetry() {

        if (cog == null) {
            getCentreOfGravity();
            horizontalSymmetry = -1;
        }

        if (horizontalSymmetry == -1) {

            double symmetricPixelCount = 0;

            for (int x = 0; x < boundingWidth; x++) {
                for (int y = 0; y <= (boundingHeight / 2); y++) {

                    // ensure both pixels are in bounds
                    if ((cog.y + y) < boundingHeight && (cog.y - y) >= 0) {
                        boolean below = insideShape[x][cog.y + y];
                        boolean above = insideShape[x][cog.y - y];
                        if (below && below == above) {
                            if (x == 0) {
                                // central pixel is true
                                symmetricPixelCount++;
                            } else {
                                // two matching pixels
                                symmetricPixelCount += 2;
                            }
                        }
                    }

                }
            }

            horizontalSymmetry = symmetricPixelCount / s.totalPixels;

        }

        return horizontalSymmetry;

    }

    protected double inverseHorizontalSymmetry = -1;

    /**
     * Calculates the inverse horizontal symmetry, if you put a mirror up on the vertical axis and flip the other side.
     * Useful for letter analysis.
     */
    public double getInverseHorizontalSymmetry() {

        if (cog == null) {
            getCentreOfGravity();
            inverseHorizontalSymmetry = -1;
        }

        if (inverseHorizontalSymmetry == -1) {

            double symmetricPixelCount = 0;

            for (int x = 0; x < boundingWidth; x++) {
                for (int y = 0; y <= (boundingHeight / 2); y++) {

                    // ensure both pixels are in bounds
                    if ((cog.y + y) < boundingHeight && (cog.y - y) >= 0) {
                        boolean below = insideShape[x][cog.y + y];
                        boolean above = insideShape[boundingWidth - x - 1][cog.y - y];
                        if (below && below == above) {
                            if (x == 0) {
                                // central pixel is true
                                symmetricPixelCount++;
                            } else {
                                // two matching pixels
                                symmetricPixelCount += 2;
                            }
                        }
                    }

                }
            }

            inverseHorizontalSymmetry = symmetricPixelCount / s.totalPixels;

        }

        return inverseHorizontalSymmetry;

    }

    protected double inverseVerticalSymmetry = -1;

    /**
     * Calculates the inverse vertical symmetry, if you put a mirror up on the vertical axis and flip the other side.
     * Useful for letter analysis. Think of the letter "S"
     */
    public double getInverseVerticalSymmetry() {
        if (cog == null) {
            getCentreOfGravity();
            inverseVerticalSymmetry = -1;
        }

        if (inverseVerticalSymmetry == -1) {

            double symmetricPixelCount = 0;

            for (int y = 0; y < boundingHeight; y++) {
                for (int x = 0; x <= (boundingWidth / 2); x++) {

                    // ensure both pixels are in bounds
                    if ((cog.x + x) < boundingWidth && (cog.x - x) >= 0) {
                        boolean rightPixel = insideShape[cog.x + x][y];
                        boolean leftPixel = insideShape[cog.x - x][boundingHeight - y - 1];
                        if (rightPixel && rightPixel == leftPixel) {
                            if (x == 0) {
                                // central pixel is true
                                symmetricPixelCount++;
                            } else {
                                // two matching pixels
                                symmetricPixelCount += 2;
                            }
                        }
                    }

                }
            }

            inverseVerticalSymmetry = symmetricPixelCount / s.totalPixels;

        }

        return inverseVerticalSymmetry;
    }

    private double closestEndToCog;

    /**
     * Finds the end point (found by skeletonisation) which is closest to the middle.
     *
     * @return
     */
    public double getClosestEndToCog() {

        if (closestEndToCog == -1) {

            if (boundingWidth == -1) compile();

            Pixel cog = getCentreOfGravity();

            double distance = Double.MAX_VALUE;
            double closestX = -1;
            double closestY = -1;

            // ensure ends are calculated
            getEnds();

            for (int i = 0; i < ends.size(); i++) {
                Pixel end = ends.elementAt(i);
                // see how close this pixel is to the CoG
                int diffX = end.x - cog.x;
                int diffY = end.y - cog.y;
                double dist = Math.sqrt((diffX * diffX) + (diffY * diffY));
                if (dist < distance) {
                    distance = dist;
                    closestX = end.x;
                    closestY = end.y;
                }

            }

            if (distance == 0) {
                return 0;
            }

            // scale X and Y in terms of the bounding height so this distance is scale invariant
            double distX = (closestX - cog.x);
            double distY = (closestY - cog.y);

            // now, do pythag.
            double unscaledDistance = Math.sqrt((distX * distX) + (distY * distY));

            // find the furthest distance between two points in this shape
            double maxDistance = Math.sqrt((boundingWidth * boundingWidth) + (boundingHeight * boundingHeight));

            // return the scaled distance
            closestEndToCog = unscaledDistance / maxDistance;

        }

        return closestEndToCog;


    }

    private double closestPixelToCog = -1;

    /**
     * Finds the pixel closest to the center of gravity. This distance may well be zero, but if the shape
     * is hollow the pixel may be quite far away. A shape like a ring will have a non zero answer.
     */
    public double getClosestPixelToCog() {

        if (closestPixelToCog == -1) {

            if (boundingWidth == -1) compile();


            Pixel cog = getCentreOfGravity();
            double distance = Double.MAX_VALUE;
            double closestX = -1;
            double closestY = -1;
            for (int y = 0; y < boundingHeight; y++) {
                for (int x = 0; x < boundingWidth; x++) {
                    if (array[x][y] != null) {
                        // see how close this pixel is to the CoG
                        int diffX = x - cog.x;
                        int diffY = y - cog.y;
                        double dist = Math.sqrt((diffX * diffX) + (diffY * diffY));
                        if (dist < distance) {
                            distance = dist;
                            closestX = x;
                            closestY = y;
                        }
                    }
                }
            }

            if (distance == 0) {
                return 0;
            }

            // scale X and Y in terms of the bounding height so this distance is scale invariant
            double distX = (closestX - cog.x);
            double distY = (closestY - cog.y);

            // now, do pythag.
            double unscaledDistance = Math.sqrt((distX * distX) + (distY * distY));

            // find the furthest distance between two points in this shape
            double maxDistance = Math.sqrt((boundingWidth * boundingWidth) + (boundingHeight * boundingHeight));

            // return the scaled distance
            closestPixelToCog = unscaledDistance / maxDistance;

        }

        return closestPixelToCog;


    }

    public int getBoundingArea() {
        if (boundingWidth == -1) compile();
        return boundingWidth * boundingHeight;
    }

    public double getRectangularity() {
        return getMass() / (double) getBoundingArea();
    }

    /**
     * Returns if the Center of gravity is over a hollow, or over solid
     */
    public boolean isCoGOverHollow() {
        return getClosestPixelToCog() < 0.025;
    }

    /**
     * Gets the number of joints that make up the shape. An "X" has one, an "L" has none.
     */
    public int getJoints() {
        if (joints == null) skeletonise();
        return joints.size();
    }

    /**
     * Finds how many "ends" the shape has. An "X" has four. An "L" has two. An "0" has none.
     */
    public int getEnds() {
        if (ends == null) skeletonise();
        return ends.size();
    }


    /**
     * Performs the skeletonisation necessary to detect joints and ends.
     */
    public void skeletonise() {

        // bit nasty to implement, this one.

        if (boundingWidth == -1) compile();

        // use hildritch's algorithm to thin down to a single line.
        int c = 0;
        while (true) {
            if (!thin(c, c == 0 ? array : skeletonArray)) break;
            c++;
        }

        // find pixels with only one neighbour (end pixels)
        // try to join them to other pixels. Do this twice so that
        // single pixels are connected at BOTH ends.

        joinUp(skeletonArray);
        cleanUp(skeletonArray);

        joinUp(skeletonArray);
        cleanUp(skeletonArray);

        joints = new Vector<Pixel>(10);
        ends = new Vector<Pixel>(10);

        // now count how many joints there are - we deal with three point joins and above only
        // already checked array allows us to make some joins uncheckable - such as ones very close
        // to others that have just been found - this prevents multiple responses for parts of the same join.
        boolean[][] alreadyChecked = new boolean[boundingWidth + 1][boundingHeight + 1];

        Vector<Pixel> endsToBeRemoved = new Vector<Pixel>(10);

        for (int y = 1; y < boundingHeight; y++) {
            for (int x = 1; x < boundingWidth; x++) {
                if (skeletonArray[x][y] != null) {

                    if (alreadyChecked[x][y]) continue;

                    int neighbours = countNeighbours(skeletonArray, x, y);

                    if (neighbours > 2) {
                        // make sure that other pixels in this vicinity are not also checked - they
                        // are probably part of the same join
                        for (int dy = -2; dy <= 2; dy++) {
                            for (int dx = -2; dx <= 2; dx++) {
                                try {
                                    alreadyChecked[x + dx][y + dy] = true;
                                } catch (Exception e) {

                                }
                            }
                        }

                        // from this point find the closest end point. If that point is
                        // less than 10 pixels away then ignore this as a joint - its just an artifact
                        // caused by a serif or pointy bit.

                        Pixel closestEndPoint = findClosestEndPoint(skeletonArray, x, y);

                        // if distance is more than equal five pixels away
                        if (closestEndPoint.value >= 5) {
                            joints.add(new Pixel(x, y));
                        } else {
                            // don't add the joint and remove the end point too
                            endsToBeRemoved.add(closestEndPoint);
                        }

                    }

                    if (neighbours == 1) {
                        ends.add(new Pixel(x, y));
                    }

                }
            }
        }

        for (int i = 0; i < endsToBeRemoved.size(); i++) {
            Pixel pixel = endsToBeRemoved.elementAt(i);
            if (!ends.remove(pixel)) {
                System.err.println("// Could not remove end point (" + pixel.x + ", " + pixel.y + ").");

            }
        }

    }

    // What follows is mostly code for the skeletonisation procedure. Its rather unpleasant.


    /**
     * Finds the shortest distance from a given point to an end point. This is used
     * to see if a joint is actually a joint or not.
     */
    private Pixel findClosestEndPoint(ShapePixel[][] array, int x, int y) {

        boolean[][] alreadyChecked = new boolean[boundingWidth + 1][boundingHeight + 1];
        Stack<Pixel> stack = new Stack<Pixel>();
        stack.add(new Pixel(x, y, 0));

        int shortestDistance = Integer.MAX_VALUE;
        int shortestX = -1;
        int shortestY = -1;

        while (stack.size() > 0) {
            Pixel p = stack.pop();
            alreadyChecked[p.x][p.y] = true;

            // record how many steps away from the start point this pixel is
            int distance = p.value;

            // look at this pixel's neighbours
            Vector<Pixel> neighbours = getNeighbours(array, p.x, p.y);

            if (neighbours.size() == 1) {
                // we have reached an end point
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    shortestX = p.x;
                    shortestY = p.y;
                }
                continue;
            }

            for (int i = 0; i < neighbours.size(); i++) {
                Pixel neighbour = neighbours.elementAt(i);
                if (!alreadyChecked[neighbour.x][neighbour.y]) {
                    alreadyChecked[neighbour.x][neighbour.y] = true;
                    neighbour.value = distance + 1;
                    stack.add(neighbour);
                }
            }

        }

        return new Pixel(shortestX, shortestY, shortestDistance);

    }

    protected double endBalanceX = -1;

    /**
     * Finds the balance from the average position of the ends to the centre of gravity's X position.
     */
    public double getEndBalanceX() {

        // make sure ends are calculated
        getEnds();

        if (ends.size() == 0) return 0;

        // find the average position of the ends
        double total = 0;
        for (int i = 0; i < ends.size(); i++) {
            Pixel end = ends.elementAt(i);
            total += end.x;
        }

        double averageX = total / ends.size();

        return ((getCentreOfGravity().x - averageX) / (double) boundingWidth);

    }

    protected double endBalanceY = -1;

    /**
     * Finds the balance from the average position of the ends to the centre of gravity's Y position.
     */
    public double getEndBalanceY() {

        // make sure ends are calculated
        getEnds();

        if (ends.size() == 0) return 0;

        // find the average position of the ends
        double total = 0;
        for (int i = 0; i < ends.size(); i++) {
            Pixel end = ends.elementAt(i);
            total += end.y;
        }

        double averageY = total / ends.size();

        return ((getCentreOfGravity().y - averageY) / (double) boundingHeight);

    }

    private void cleanUp(ShapePixel[][] array) {
        // clear unnecessary pixels to make the line as thin as possible
        for (int y = 1; y < boundingHeight; y++) {
            for (int x = 1; x < boundingWidth; x++) {
                if (array[x][y] != null) {
                    boolean N = array[x][y - 1] != null;
                    boolean E = array[x + 1][y] != null;
                    boolean S = array[x][y + 1] != null;
                    boolean W = array[x - 1][y] != null;
                    boolean SW = array[x - 1][y + 1] != null;
                    boolean NW = array[x - 1][y - 1] != null;
                    boolean NE = array[x + 1][y - 1] != null;
                    boolean SE = array[x + 1][y + 1] != null;
                    if (N && E && !SW) array[x][y] = null;
                    if (E && S && !NW) array[x][y] = null;
                    if (S && W && !NE) array[x][y] = null;
                    if (W && N && !SE) array[x][y] = null;
                }
            }
        }
    }

    private void joinUp(ShapePixel[][] array) {
        for (int y = 1; y < boundingHeight; y++) {
            for (int x = 1; x < boundingWidth; x++) {
                if (array[x][y] != null) {

                    if (countNeighbours(array, x, y) <= 1) {

                        // search around looking for a close neighbour
                        Pixel other = findClosestPixelToConnectTo(array, x, y);

                        if (other != null) {

                            // only join the dots if the other pixel also wants to join this pixel
                            Pixel otherPixelsPreference = findClosestPixelToConnectTo(array, other.x, other.y);

                            if (otherPixelsPreference == null || otherPixelsPreference.x != x || otherPixelsPreference.y != y) {
                                // the other pixel has discovered a closer pixel itself.
                                continue;
                            }

                            // now join the dots
                            Vector<Pixel> path = new Vector<Pixel>(10);

                            int cX = x;
                            int cY = y;
                            boolean abort = false;
                            while (true) {
                                boolean changed = false;
                                if (cX != other.x) {
                                    if (cX < other.x) cX++;
                                    else cX--;
                                    changed = true;
                                }
                                if (cY != other.y) {
                                    if (cY < other.y) cY++;
                                    else cY--;
                                    changed = true;
                                }
                                if (changed) {
                                    // the join strays outside the shape - this connection would connect
                                    // parts of the shape that could never be connected, abort!
                                    if (!insideShape[cX][cY]) {
                                        abort = true;
                                        break;
                                    }
                                    path.add(new Pixel(cX, cY));
                                } else break;
                            }

                            if (!abort) {
                                // now fill in the path
                                for (int i = 0; i < path.size(); i++) {
                                    Pixel p = path.elementAt(i);
                                    if (array[p.x][p.y] == null)
                                        array[p.x][p.y] = new ShapePixel(p.x + s.minX, p.y + s.minY);
                                }
                            }

                        }

                    }
                }
            }
        }
    }

    /**
     * @param pX
     * @param pY
     * @return
     */
    private Pixel findClosestPixelToConnectTo(ShapePixel[][] array, int pX, int pY) {

        final int maxJoinDistance = 10;

        // look in increasing square circles from the point until we find another pixel
        for (int dist = 1; dist < maxJoinDistance; dist++) {

            Vector<Pixel> possibleNeighbours = new Vector<Pixel>(100);

            for (int y = -dist; y <= dist; y++) {
                if (y == -dist || y == dist) {
                    for (int x = -dist; x <= dist; x++) {
                        if (getArray(array, pX + x, pY + y) != null) {
                            possibleNeighbours.add(new Pixel(x, y));
                        }
                    }
                } else {
                    if (getArray(array, pX - dist, pY + y) != null) possibleNeighbours.add(new Pixel(-dist, y));
                    if (getArray(array, pX + dist, pY + y) != null) possibleNeighbours.add(new Pixel(+dist, y));
                }
            }

            // now check the possible neighbours and see if they are any good
            Pixel bestNeighbour = null;
            double smallestDistance = Double.MAX_VALUE;

            for (int j = 0; j < possibleNeighbours.size(); j++) {
                Pixel possibleNeighbour = possibleNeighbours.elementAt(j);

                if (!connectedTo(array, pX, pY, pX + possibleNeighbour.x, pY + possibleNeighbour.y, maxJoinDistance * 2)) {

                    // find the distance between the point and this neighbour
                    double distance = pythag(possibleNeighbour.x, possibleNeighbour.y);

                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        bestNeighbour = new Pixel(pX + possibleNeighbour.x, pY + possibleNeighbour.y);
                    }

                }

            }

            if (bestNeighbour != null) {
                return bestNeighbour;
            }

        }

        // found nothing in range
        return null;

    }

    /**
     * Returns whether a point is connected to another point within a certain number
     * of pixels. It may be the case that they are connected, when you look at the whole
     * contect of the image, but in this case we'd just like to know if there are dist or fewer
     * pixels that connect pX to pY
     */
    private boolean connectedTo(ShapePixel[][] array, int pX, int pY, int oX, int oY, int dist) {

        boolean[][] alreadyChecked = new boolean[boundingWidth + 1][boundingHeight + 1];

        alreadyChecked[pX][pY] = true;

        return getPath(array, pX, pY, oX, oY, alreadyChecked, 0, dist);

    }

    private boolean getPath(ShapePixel[][] array, int cX, int cY, int oX, int oY, boolean[][] alreadyChecked, int pathLength, int maxDist) {

        // don't go beyond the boundary
        if (pathLength > maxDist) return false;

        // see if we are at the destination
        if (cX == oX && cY == oY) return true;

        // look at the neighbours of the current point
        Vector<Pixel> neighbours = getNeighbours(array, cX, cY);

        // look at each neighbour in turn
        for (int j = 0; j < neighbours.size(); j++) {
            Pixel neighbour = neighbours.elementAt(j);

            if (!alreadyChecked[neighbour.x][neighbour.y]) {
                alreadyChecked[neighbour.x][neighbour.y] = true;
                if (getPath(array, neighbour.x, neighbour.y, oX, oY, alreadyChecked, pathLength + 1, maxDist))
                    return true;
            }

        }

        return false;

    }

    /**
     * Thins a shape down to a skeleton using Hilditch's algorithm
     */
    private boolean thin(int dist, ShapePixel[][] array) {

        int pixelsThinned = 0;

        ShapePixel[][] skeletonArray = new ShapePixel[boundingWidth + 1][boundingHeight + 1];

        // go through all the pixels
        // change a pixel from black to white if it meets the following conditions
        for (int y = 1; y < boundingHeight; y++) {
            for (int x = 1; x < boundingWidth; x++) {
                if (array[x][y] != null) {

                    int B = countNeighbours(array, x, y);
                    int A = countConnections(array, x, y);

                    skeletonArray[x][y] = array[x][y];

                    // Delete the pixel if it has more than 1 neighbour, and fewer than 7 neighbours
                    if (B >= 2 && B <= 6 && A == 1) {

                        boolean P9 = array[x - 1][y - 1] != null;
                        boolean P8 = array[x - 1][y] != null;
                        boolean P7 = array[x - 1][y + 1] != null;

                        boolean P2 = array[x][y - 1] != null;

                        boolean P3 = array[x + 1][y - 1] != null;
                        boolean P4 = array[x + 1][y] != null;
                        boolean P5 = array[x + 1][y + 1] != null;

                        boolean P6 = array[x][y + 1] != null;

                        // preserves vertical lines
                        if (x < boundingWidth - 1) {
                            boolean P10 = array[x + 2][y - 1] != null;
                            boolean P11 = array[x + 2][y] != null;
                            boolean P12 = array[x + 2][y + 1] != null;
                            //if (!P9 && !P8 && !P7 && P3 && P4 && P5 && P6 && !P10 && !P11 && !P12) continue;	//POEY comment: How about p2?
                            //POEY
                            if (!P9 && !P8 && !P7 && P2 && P3 && P4 && P5 && P6 && !P10 && !P11 && !P12) continue;
                        }

                        // preserves horizontal lines
                        if (y < boundingHeight - 1) {
                            boolean P13 = array[x - 1][y + 2] != null;
                            boolean P14 = array[x][y + 2] != null;
                            boolean P15 = array[x + 1][y + 2] != null;
                            if (!P9 && !P2 && !P3 && P4 && P8 && P5 && P6 && P7 && !P13 && !P14 && !P15) continue;
                        }


                        pixelsThinned++;
                        depths[x][y] = dist;
                        skeletonArray[x][y] = null;
                    }

                }
            }

        }

        this.skeletonArray = skeletonArray;

        return pixelsThinned > 0;

    }

    private Vector<Pixel> getNeighbours(ShapePixel[][] array, int x, int y) {
        Vector<Pixel> neighbours = new Vector<Pixel>(8);
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                if (array[x + dx][y + dy] != null) {
                    neighbours.add(new Pixel(x + dx, y + dy));
                }
            }
        }
        return neighbours;
    }

    /**
     * Finds out how many neighbours a given pixel has
     */
    private int countNeighbours(ShapePixel[][] array, int x, int y) {
        int neighbourCount = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                if (array[x + dx][y + dy] != null) {
                    neighbourCount++;
                }
            }
        }
        return neighbourCount;
    }

    /**
     * Counts the number of connections to this pixel by counting
     * the number of 0-1 patterns in a clockwise direction there are.
     */
    private int countConnections(ShapePixel[][] array, int x, int y) {
        int connections = 0;
        Pixel[] neighbourLocations = new Pixel[8];
        neighbourLocations[0] = new Pixel(0, -1); // n
        neighbourLocations[1] = new Pixel(+1, -1); // ne
        neighbourLocations[2] = new Pixel(1, 0); // e
        neighbourLocations[3] = new Pixel(+1, +1); // se
        neighbourLocations[4] = new Pixel(0, 1); // s
        neighbourLocations[5] = new Pixel(-1, +1); // sw
        neighbourLocations[6] = new Pixel(-1, 0); // w
        neighbourLocations[7] = new Pixel(-1, -1); // nw
        boolean lastWas = array[x + neighbourLocations[7].x][y + neighbourLocations[7].y] != null;
        for (int j = 0; j < neighbourLocations.length; j++) {
            boolean current = array[x + neighbourLocations[j].x][y + neighbourLocations[j].y] != null;
            if (!lastWas && current) {
                connections++;
            }
            lastWas = current;
        }
        return connections;
    }

    private double pythag(int width, int height) {
        return Math.sqrt((width * width) + (height * height));
    }

    private ShapePixel getArray(ShapePixel[][] array, int x, int y) {
        if (x < 0 || y < 0 || x >= boundingWidth || y >= boundingHeight) return null;
        return array[x][y];
    }
    // END THINNING CODE

    /**
     * The Perimeters
     */
    Vector<Perimeter> perimeters;

    public Vector<Perimeter> getPerimeters() {
        return perimeters;
    }

    /**
     * Finds which edges are on the outer perimeter, and which edges are inner edges - holes
     * The holes aren't really as important to us as the perimeters, so we need to know which
     * is which. Once we've found all the perimeters (connected edge paths), all we need to do
     * is find the largest one - the outer perimeter, and all others (if any) will therefore be
     * hollow holes inside the main shape.
     */
    private Vector<Perimeter> findPerimeters() {

        Vector<Perimeter> perimeters = new Vector<Perimeter>(10);

        int filledIn = 0;
        //POEY comment: s.edgePixels.size() = the number of pixels on the edge
        //edgePixel is a coordinate of edge of an object
        for (int i = 0; i < s.edgePixels.size(); i++) {
            ShapePixel edgePixel = s.edgePixels.elementAt(i);

            // check that it hasn't already been classified
            if (!edgePixel.alreadyChecked) { //POEY comment: some pixels come in this case
                // so that we don't check it twice
                edgePixel.alreadyChecked = true;

                // find its coordinates on the array
                int x = edgePixel.x - s.minX;
                int y = edgePixel.y - s.minY;

                // find a neighbour. it doesn't matter which direction this is because we're
                // looking for a loop.
                ShapePixel neighbour = null;
                chooseNeighbour:
                for (int dY = -1; dY <= 1; dY++)
                    for (int dX = -1; dX <= 1; dX++) {
                        if (!(dX == 0 && dY == 0)) {
                            if (x + dX < 0) continue;
                            if (y + dY < 0) continue;
                            neighbour = array[x + dX][y + dY];
                            if (neighbour != null && neighbour.isEdge && !neighbour.alreadyChecked) {
                                x += dX;
                                y += dY;
                                neighbour.alreadyChecked = true;
                                break chooseNeighbour;
                            }
                        }
                    }

                // null - shouldn't happen
                if (neighbour == null) {
                    // this edge pixel has no neighbours at all.
                    // this would only be the case if the shape has precisely one pixel
                    // this shouldn't happen because the grouper throws away shapes with
                    // only one pixel (they can't provide meaningful shape statistics because
                    // they don't have one)
                    // however, to be polite, just print a message stating that this shape's
                    // existence is futile and move on.
                    //System.err.println("Edge pixel with no neighbours - single pixel shape allowed by Grouper? Ignoring.");
                    continue;
                }

                // store all the edges on this perimeter in this structure:
                Perimeter perimeter = new Perimeter(neighbour);
                perimeter.add(edgePixel);

                // now, find the perimeter using this recursive method.
                Stack<PerimeterStack> s = new Stack<PerimeterStack>();
                s.add(new PerimeterStack(edgePixel, x, y, -1));
                while (s.size() > 0) {
                    //findPerimeter(edgePixel, perimeter, x, y, -1);
                    findPerimeter(s, perimeter);
                }

                if (perimeter.pixels.size() > 1) {

                    // get stats for perimeter
                    perimeter.compile();

                    if (perimeters.size() > 0 && perimeter.pixelsInsidePerimeter < 10) {
                        // ignore this perimeter, it is noise and will interfere with countHollows
                        // and the skeletonisation process. Fill in the gap
                        filledIn++;
                        perimeter.fillIn(this);
                    } else {
                        // save this perimeter
                        perimeters.add(perimeter);
                    }

                }

            }

        }

        // reset
        for (int i = 0; i < s.edgePixels.size(); i++) {
            ShapePixel edgePixel = s.edgePixels.elementAt(i);
            edgePixel.alreadyChecked = false;
        }

        // check that we have actually found some perimeters, otherwise we'd get a NullPointerException in later
        // code. This shouldn't happen as all shapes by definition have at least one (outer) perimeter.
        if (perimeters.size() == 0) {
            System.err.println("No perimeters found for shape. Shape size: " + getVolume() + ", filledIn: " + filledIn);
            return null;
        }

        // the largest perimeter is the first one added, as this derives from the first edgepixel found, which will
        // clearly always be on the outer edge.
        Perimeter largestPerimeter = perimeters.elementAt(0);

        // Now that we've established which is the largest perimeter, and conveniently we have a vector that
        // contains every pixel, we can finally use this data. Knowing which edge pixels are on the outside and which
        // are on the inside is actually very useful, as it allows us to make totally correct calculations of
        // things like the shape's density and area, as well as being able to make accurate analysis of the shape's
        // general appearance, which is defined by its outer edges.

        // For now we'll simply mark the edges in the outer perimeter as being "outer" edges. ShapePixels are
        // naturally defined as being "inner" edges by default, so we don't need to change any of the inner
        // edges' definitions.
        for (int i = 0; i < largestPerimeter.pixels.size(); i++) {
            largestPerimeter.pixels.elementAt(i).insideEdge = false;
        }

        // return the perimeter, so it can be used in further calculations.
        return perimeters;

    }

    class PerimeterStack {

        ShapePixel edgePixel;
        int x, y, dir;

        PerimeterStack(ShapePixel edgePixel, int x, int y, int dir) {
            this.edgePixel = edgePixel;
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

    }

    //private void findPerimeter(ShapePixel startPixel, Perimeter perimeter, int x, int y, int prevDirection) {
    private void findPerimeter(Stack<PerimeterStack> stack, Perimeter perimeter) {

        PerimeterStack s = stack.pop();
        int x = s.x;
        int y = s.y;
        ShapePixel startPixel = s.edgePixel;
        int prevDirection = s.dir;

        // look for start pixel's neighbours, so that we can move around the perimeter, and with any luck
        // return to the pixel at which we started. As the perimeter is linear, there is only one way to go
        // but we need to make sure that we're not going backwards, hence the lastDirection parameter which
        // allows us to check.


        final Pixel[] neighbourLocations = new Pixel[8];
        // go round clockwise (helps the algorithm not to miss out pixels)
        neighbourLocations[0] = new Pixel(0, -1); // n
        neighbourLocations[1] = new Pixel(+1, -1); // ne
        neighbourLocations[2] = new Pixel(1, 0); // e
        neighbourLocations[3] = new Pixel(+1, +1); // se
        neighbourLocations[4] = new Pixel(0, 1); // s
        neighbourLocations[5] = new Pixel(-1, +1); // sw
        neighbourLocations[6] = new Pixel(-1, 0); // w
        neighbourLocations[7] = new Pixel(-1, -1); // nw

        Vector<NextStep> candidates = new Vector<NextStep>(4);
        NextStep best = null;

        for (int i = 0; i < neighbourLocations.length; i++) {
            Pixel neighbourLocation = neighbourLocations[i];

            int dX = neighbourLocation.x;
            int dY = neighbourLocation.y;

            if (x + dX < 0) continue;
            if (y + dY < 0) continue;

            ShapePixel neighbour = array[x + dX][y + dY];

            if (neighbour != null) {
                int neighbourNeighbours = countUncheckedNeighbours(x + dX, y + dY);
                if (neighbour.isEdge && !neighbour.alreadyChecked) {
                    best = new NextStep(neighbour, x + dX, y + dY);
                    if (neighbourNeighbours > 0 || (perimeter.pixels.size() > 3 && connectedTo(neighbour, startPixel))) {
                        candidates.add(best);
                    }
                } else {

                    // if we reach the end of the perimeter, add the final neighbour and complete.
                    if (perimeter.pixels.size() > 3) {
                        if (neighbour == startPixel) {
                            return;
                        }
                    }

                }
            }

        }

        if (candidates.size() == 0) {
            // no neighbour found. Most annoying - perimeter doesn't work properly.
            // happens if the perimeter is too thin. There are a few circumstances that cause this to happen.
            // when this happens - try to jump two pixels
            // step 1 - if there is a poorly connected neighbour, use that for now
            if (best != null) {
                candidates.add(best);
            } else {

                // the problem is that a pixel may have been incorrectly classified, and we may have to
                // use the same pixel twice. So - find all used pixels and see if any of those have unchecked
                // neighbours. If there is one such neighbour, add that one and proceed along that course.
                // this doesn't cause infinite loops as it only makes this check once, and is still only looking
                // for a bridge - a pixel can't be used as a bridge over and over and over.
                for (int i = 0; i < neighbourLocations.length; i++) {
                    Pixel neighbourLocation = neighbourLocations[i];

                    int dX = neighbourLocation.x;
                    int dY = neighbourLocation.y;

                    if (x + dX < 0) continue;
                    if (y + dY < 0) continue;

                    ShapePixel neighbour = array[x + dX][y + dY];

                    if (neighbour != null && neighbour.isEdge) {
                        int neighbourNeighbours = countUncheckedNeighbours(x + dX, y + dY);
                        if (neighbourNeighbours > 0) {
                            // found the next step
                            candidates.add(new NextStep(neighbour, x + dX, y + dY));
                        }
                    }

                }

                // but if we still fail, give up
                if (candidates.size() == 0) {
                    //System.err.println("No neighbour found for pixel on perimeter: " + x + ", " + y);
                    return;
                }

            }
        }

        // choose the most appropriate neighbour here...
        NextStep next = null;

        // although oftentimes there is no choice!
        if (candidates.size() == 1) {
            next = candidates.elementAt(0);
        } else {

            // figure out what direction the current pixel is facing outwards onto
            Direction d1 = getDirection(x, y);

            // try to find a neighbour that has a compatible direction
            for (int i = 0; i < candidates.size(); i++) {
                NextStep nextStep = candidates.elementAt(i);

                Direction d2 = getDirection(nextStep.x, nextStep.y);

                if (d1.isCompatibleWith(d2)) {
                    next = nextStep;
                    break;
                }

            }

            // what if neighbour is still null? None of them match quite properly, so just
            // use the first one.
            if (next == null) {
                next = candidates.elementAt(0);
            }

        }

        ShapePixel neighbour = next.pixel;

        // mark the neighbour as being checked
        neighbour.alreadyChecked = true;

        // add start pixel to the path
        perimeter.add(neighbour);

        // keep searching. Move on to the neighbour and continue
        //findPerimeter(startPixel, perimeter, next.x, next.y, prevDirection);
        stack.add(new PerimeterStack(startPixel, next.x, next.y, prevDirection));

    }

    private boolean connectedTo(ShapePixel pixel, ShapePixel other) {
        return Math.abs(pixel.x - other.x) <= 1 && Math.abs(pixel.y - other.y) <= 1;
    }

    /**
     * Looks at an edge pixel and determines what directin that pixel is facing. All edge pixels
     * face outward, but some face up, some down etc.
     */
    private Direction getDirection(int x, int y) {

        // figure out whats in the horizontal direction
        int hDirection = Direction.NO_HORIZONTAL;


        if (array[x + 1][y] == null) hDirection = Direction.EAST;
        if (x - 1 < 0 || array[x - 1][y] == null) hDirection = Direction.WEST;

        int vDirection = Direction.NO_VERTICAL;

        if (y - 1 < 0 || array[x][y - 1] == null) vDirection = Direction.NORTH;
        if (array[x][y + 1] == null) vDirection = Direction.SOUTH;

        if (hDirection == Direction.NO_HORIZONTAL && vDirection == Direction.NO_VERTICAL) {
            // need to look in the diagonal corners too
            if (array[x + 1][y + 1] == null) {
                // south east
                vDirection = Direction.SOUTH;
                hDirection = Direction.EAST;
            }
            if (array[x + 1][y - 1] == null) {
                // north east
                vDirection = Direction.NORTH;
                hDirection = Direction.EAST;
            }
            if (array[x - 1][y + 1] == null) {
                // south east
                vDirection = Direction.SOUTH;
                hDirection = Direction.WEST;
            }
            if (array[x - 1][y - 1] == null) {
                // north east
                vDirection = Direction.NORTH;
                hDirection = Direction.WEST;
            }
        }

        return new Direction(hDirection, vDirection);

    }

    private class Direction {

        public static final int NO_HORIZONTAL = -1;
        public static final int NO_VERTICAL = 0;
        public static final int NORTH = 1;
        public static final int SOUTH = 2;
        public static final int EAST = 3;
        public static final int WEST = 4;

        int hDirection;
        int vDirection;

        public Direction(int hDirection, int vDirection) {
            this.hDirection = hDirection;
            this.vDirection = vDirection;
        }

        public boolean isCompatibleWith(Direction other) {
            return ((this.hDirection > 0 && this.hDirection == other.hDirection) || (this.vDirection > 0 && this.vDirection == other.vDirection));
        }

    }

    private class NextStep {
        ShapePixel pixel;
        int x, y;

        public NextStep(ShapePixel pixel, int x, int y) {
            this.pixel = pixel;
            this.x = x;
            this.y = y;
        }
    }

    private int countUncheckedNeighbours(int x, int y) {

        int uncheckedNeighbours = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx < 0 || ny < 0) continue;
                if (nx >= boundingWidth || ny >= boundingHeight) continue;
                ShapePixel neighbour = array[nx][ny];
                if (neighbour != null && neighbour.isEdge && !neighbour.alreadyChecked) {
                    uncheckedNeighbours++;
                }
            }
        }

        return uncheckedNeighbours;

    }

    /**
     * Finds the Euclidean distance between two points. A utility method.
     */
    protected float dist(int x1, int y1, int x2, int y2) {
        double a = x1 - x2;
        a = a * a;
        double b = y1 - y2;
        b = b * b;
        return (float) Math.sqrt(a + b);
    }

    public void printStats() {
        System.out.println("-----------------------------------");
        System.out.println("SHAPE STATISTICS");
        System.out.println("Corners: " + countCorners());
        System.out.println("Ends: " + getEnds());
        System.out.println("Joints: " + getJoints());
        System.out.println("Aspect Ratio: " + getAspectRatio());
        System.out.println("Hollows: " + countHollows());
        System.out.println("Density: " + getDensity());
        System.out.println("CoG over Hollow?: " + isCoGOverHollow());
        System.out.println("Closest Pixel to COG: " + getClosestPixelToCog());
        System.out.println("BalanceX: " + getBalanceX());
        System.out.println("BalanceY: " + getBalanceY());
    }

    
    //POEY
    //the following functions are used to calculate colour features of a segmented object

    //cache
    private double normalisedRedMean = -1;
    private double normalisedRedStdDev = -1;

    public double getNormalisedRedMean() {
        if (normalisedRedMean == -1) {
        	getNormalisedRed();
        }
        return normalisedRedMean;
    }
    
    public double getNormalisedRedStdDev() {
        if (normalisedRedStdDev == -1) {
        	getNormalisedRed();
        }
        return normalisedRedStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for red of a segmented object's pixels
     */
    public void getNormalisedRed() {   	
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getNormalisedRed(p.x,p.y));
                }
            }
    	}
    	normalisedRedMean = solver.getMean();
    	normalisedRedStdDev = solver.getStandardDeviation();
    }
    
    //cache
    private double normalisedGreenMean = -1;
    private double normalisedGreenStdDev = -1;

    public double getNormalisedGreenMean() {
        if (normalisedGreenMean == -1) {
        	getNormalisedGreen();
        }
        return normalisedGreenMean;
    }
    
    public double getNormalisedGreenStdDev() {
        if (normalisedGreenStdDev == -1) {
        	getNormalisedGreen();
        }
        return normalisedGreenStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for green of a segmented object's pixels
     */
    public void getNormalisedGreen() {   	
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getNormalisedGreen(p.x,p.y));
                }
            }
    	}
    	normalisedGreenMean = solver.getMean();
    	normalisedGreenStdDev = solver.getStandardDeviation();
    }
    
    //cache
    private double normalisedBlueMean = -1;
    private double normalisedBlueStdDev = -1;

    public double getNormalisedBlueMean() {
        if (normalisedBlueMean == -1) {
        	getNormalisedBlue();
        }
        return normalisedBlueMean;
    }
    
    public double getNormalisedBlueStdDev() {
        if (normalisedBlueStdDev == -1) {
        	getNormalisedBlue();
        }
        return normalisedBlueStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for blue of a segmented object's pixels
     */
    public void getNormalisedBlue() {  
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getNormalisedBlue(p.x,p.y));
                }
            }
    	}
    	normalisedBlueMean = solver.getMean();
    	normalisedBlueStdDev = solver.getStandardDeviation();
    }
    
    //cache
    private double[] C1C2C3Mean = {-1, -1, -1};
    private double[] C1C2C3StdDev = {-1, -1, -1};
    
    public double getC1C2C3Mean(int i) {
        if (C1C2C3Mean[i] == -1) {
        	getC1C2C3(i);
        }
        return C1C2C3Mean[i];
    }
    
    public double getC1C2C3StdDev(int i) {
        if (C1C2C3StdDev[i] == -1) {
        	getC1C2C3(i);
        }
        return C1C2C3StdDev[i];
    }
    
    /**
     * to calculate average and standard deviation values for C1C2C3 of a segmented object's pixels
     */
    public void getC1C2C3(int i) {   	
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getC1C2C3(p.x,p.y,i));
                }
            }
    	}
    	C1C2C3Mean[i] = solver.getMean();
    	C1C2C3StdDev[i] = solver.getStandardDeviation();
    }
    
    //cache
    private double[] L1L2L3Mean = {-1, -1, -1};
    private double[] L1L2L3StdDev = {-1, -1, -1};
    
    public double getL1L2L3Mean(int i) {
        if (L1L2L3Mean[i] == -1) {
        	getL1L2L3(i);
        }
        return L1L2L3Mean[i];
    }
    
    public double getL1L2L3StdDev(int i) {
        if (L1L2L3StdDev[i] == -1) {
        	getL1L2L3(i);
        }
        return L1L2L3StdDev[i];
    }
    
    /**
     * to calculate average and standard deviation values for L1L2L3 of a segmented object's pixels
     */
    public void getL1L2L3(int i) {   	
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getL1L2L3(p.x,p.y,i));
                }
            }
    	}
    	L1L2L3Mean[i] = solver.getMean();
    	L1L2L3StdDev[i] = solver.getStandardDeviation();
    }
    
    //cache
    private double greyValueMean = -1;
    private double greyValueStdDev = -1;

    public double getGreyValueMean() {
        if (greyValueMean == -1) {
        	getGreyValue();
        }
        return greyValueMean;
    }
    
    public double getGreyValueStdDev() {
        if (greyValueStdDev == -1) {
        	getGreyValue();
        }
        return greyValueStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for grey of a segmented object's pixels
     */
    public void getGreyValue(){
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getGreyValue(p.x,p.y));
                }
            }
    	}
    	greyValueMean = solver.getMean();
    	greyValueStdDev = solver.getStandardDeviation();
    }

    //cache
    private double hueMean = -1;
    private double hueStdDev = -1;

    public double getHueMeanObject() {
        if (hueMean == -1) {
        	getHueObject();
        }
        return hueMean;
    }
    
    public double getHueStdDevObject() {
        if (hueStdDev == -1) {
        	getHueObject();
        }
        return hueStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for hue of a segmented object's pixels
     */
    public void getHueObject(){
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getHue(p.x,p.y));
                }
            }
    	}
    	hueMean = solver.getMean();
    	hueStdDev = solver.getStandardDeviation();
    }
    
    //cache
    private double satMean = -1;
    private double satStdDev = -1;

    public double getSaturationMeanObject() {
        if (satMean == -1) {
        	getSaturationObject();
        }
        return satMean;
    }
    
    public double getSaturationStdDevObject() {
        if (satStdDev == -1) {
        	getSaturationObject();
        }
        return satStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for saturation of a segmented object's pixels
     */
    public void getSaturationObject(){
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getSaturation(p.x,p.y));
                }
            }
    	}
    	satMean = solver.getMean();
    	satStdDev = solver.getStandardDeviation();
    }
    
    //cache
    private double lightMean = -1;
    private double lightStdDev = -1;

    public double getLightnessMeanObject() {
        if (lightMean == -1) {
        	getLightnessObject();
        }
        return lightMean;
    }
    
    public double getLightnessStdDevObject() {
        if (lightStdDev == -1) {
        	getLightnessObject();
        }
        return lightStdDev;
    }
    
    /**
     * to calculate average and standard deviation values for lightness of a segmented object's pixels
     */
    public void getLightnessObject(){
    	FastStatistics solver = new FastStatistics();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData((float)image.getLightness(p.x,p.y));
                }
            }
    	}
    	lightMean = solver.getMean();
    	lightStdDev = solver.getStandardDeviation();
    }   
    
    /**
     * to return a range of grey value of a segmented object
     */
    public double get3x3RangeObject(){
    	int value = 0;
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	value = image.get3x3Range(p.x,p.y);
                }
            }
    	}
        return value;
    }
    
    /**
     * to return a variance value of a segmented object
     */
    public double get3x3VarianceObject(){
    	double value = 0;
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	value = image.get3x3Variance(p.x,p.y);
                }
            }
    	}
        return value;
    }
    
    /**
     * to return a value of 0 or 255 as the most frequent value of a segmented object's pixels which are compared by a threshold value
     */
    public int getAdaptiveBinaryThresholdObect() {
    	FastStatisticsGrey solver = new FastStatisticsGrey();
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData(image.getAdaptiveBinaryThreshold(p.x,p.y));
                }
            }
    	}
        return solver.getMaxNIndex();
    }
    
    /**
     * to get the standard deviation of greyness of a segmented object
     */
    public double getStdDeviation() {
        FastStatistics solver = new FastStatistics();
        for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData(image.getGreyValue(p.x, p.y));
                }
            }
        }
        return solver.getStandardDeviation();
    }
    
    /**
     * to get the number of a segmented object's pixels
     */
    public double getCountPixels() {
        FastStatistics solver = new FastStatistics();
        for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];
                if (p != null) {
                	solver.addData(image.getGreyValue(p.x, p.y));
                }
            }
        }
        return solver.getN();
    }
     
    /**
     * to get grey from the image's shape
     */
    public void getGreyColour(){

    	//get grey colour from the image's shape
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){
                	greyImage[x][y] = image.getGreyValue(p.x,p.y);
                }
            	else{
                	//this pixel is not in shape
            		greyImage[x][y] = -1;
            	}               
        	}
    	}
        	
		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	               
                if (greyImage[i][j] != -1) {
					if(!greyColour.contains(greyImage[i][j]))
						greyColour.add(greyImage[i][j]);
				}
			}
		}
		Collections.sort(greyColour);
    }
    
    /**
     * to get hue from the image's shape
     */
    public void getHueColour(){
    	//get hue colour from the image's shape
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){ 
                	hueImage[x][y] = image.getHue(p.x,p.y);
                }
            	else{
                	//this pixel is not in shape
            		hueImage[x][y] = -1;
            	}                
        	}
    	}
    	
		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	 
                if (hueImage[i][j] != -1) {
					if(!hueColour.contains(hueImage[i][j]))
						hueColour.add(hueImage[i][j]);
				}
			}
		}
		Collections.sort(hueColour);
    }
    
    /**
     * to get saturation from the image's shape
     */
    public void getSat(){
    	//get saturation from the image's shape
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){ 
                	satImage[x][y] = image.getSaturation(p.x,p.y);                	
                }
            	else{
                	//this pixel is not in shape
            		satImage[x][y] = -1;
            	}                
        	}
    	}

		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	 
                if (satImage[i][j] != -1) {
					if(!satColour.contains(satImage[i][j])){
						satColour.add(satImage[i][j]);					
					}
				}
			}
		}

		Collections.sort(satColour);
    }
    
    /**
     * to get lightness from the image's shape
     */
    public void getLight(){
    	//get lightness from the image's shape
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){ 
                	lightImage[x][y] = image.getLightness(p.x,p.y);
                }
            	else{
                	//this pixel is not in shape
            		lightImage[x][y] = -1;
            	}                
        	}
    	}

		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	 
                if (lightImage[i][j] != -1) {
					if(!lightColour.contains(lightImage[i][j])){
						lightColour.add(lightImage[i][j]);
					}
				}
			}
		}

		Collections.sort(lightColour);
    }
    
    /**
     * to get red from the image's shape
     */
    public void getRedColour(){
    	int red;
    	//retrieve hue colour
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){ 
                	red = image.getRGB(p.x,p.y);
                	redImage[x][y] = (red >> 16) & 0xFF;
                }
            	else
                	//this pixel is not in shape
            		redImage[x][y] = -1;
                
        	}
    	}
        	
		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	               
                if (redImage[i][j] != -1) {
					if(!redColour.contains(redImage[i][j]))
						redColour.add(redImage[i][j]);
				}
			}
		}
		Collections.sort(redColour);
    }
    
    /**
     * to get green from the image's shape
     */
    public void getGreenColour(){
    	int green;
    	//get hue colour from the image's shape
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){ 
                	green = image.getRGB(p.x,p.y);
                	greenImage[x][y] = (green >> 8) & 0xFF;
                }
            	else
                	//this pixel is not in shape
            		greenImage[x][y] = -1;
                
        	}
    	}
        	
		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	               
                if (greenImage[i][j] != -1) {
					if(!greenColour.contains(greenImage[i][j]))
						greenColour.add(greenImage[i][j]);
				}
			}
		}
		Collections.sort(greenColour);
    }
    
    /**
     * to get blue from the image's shape
     */
    public void getBlueColour(){
    	int blue;
    	//get blue colour from the image's shape
    	for (int y = 0; y < boundingHeight; y++) {
            for (int x = 0; x < boundingWidth; x++) {
                ShapePixel p = array[x][y];                
                if (p != null){ 
                	blue = image.getRGB(p.x,p.y);
                	blueImage[x][y] = blue & 0xFF;
                }
            	else
                	//this pixel is not in shape
            		blueImage[x][y] = -1;
                
        	}
    	}
        	
		for(int j=0; j<boundingHeight; j++){
			for(int i=0; i<boundingWidth; i++){	               
                if (blueImage[i][j] != -1) {
					if(!blueColour.contains(blueImage[i][j]))
						blueColour.add(blueImage[i][j]);
				}
			}
		}
		Collections.sort(blueColour);

    }

 
  
    //cache    
    private float[] greyGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean greyCheck = false;
    private float[][] glcmGrey;
    private boolean glcmGreyCheck = false;
    
    public double getGreyGLCM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(greyCheck == false){
    		getGreyColour();
    		greyCheck = true;
    	}
    	if(glcmGreyCheck == false){
    		glcmGrey = new float[greyColour.size()][greyColour.size()];
    		glcmCal(greyImage, glcmGrey, greyColour);			
    		glcmGreyCheck = true;
    	}
        if (greyGLCM[i] == -1) 
        	greyGLCM[i] = getGLCMStat(i, greyImage, glcmGrey, greyColour);
        
        return greyGLCM[i];
    }
    
    //cache
    private float[] hueGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean hueCheck = false;
    private float[][] glcmHue;
    private boolean glcmHueCheck = false;
    
    public double getHueGLCM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(hueCheck == false){
    		getHueColour();
    		hueCheck = true;
    	}
    	if(glcmHueCheck == false){
    		glcmHue = new float[hueColour.size()][hueColour.size()];
    		glcmCal(hueImage, glcmHue, hueColour);			
    		glcmHueCheck = true;
    	}
        if (hueGLCM[i] == -1) 
        	hueGLCM[i] = getGLCMStat(i, hueImage, glcmHue, hueColour);
        
        return hueGLCM[i];
    }
    
  //cache
    private float[] satGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean satCheck = false;
    private float[][] glcmSat;
    private boolean glcmSatCheck = false;
    
    public double getSaturationGLCM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(satCheck == false){
    		getSat();
    		satCheck = true;
    	}
    	if(glcmSatCheck == false){
    		glcmSat = new float[satColour.size()][satColour.size()];
    		glcmCal(satImage, glcmSat, satColour);			
    		glcmSatCheck = true;
    	}
        if (satGLCM[i] == -1) 
        	satGLCM[i] = getGLCMStat(i, satImage, glcmSat, satColour);
        
        return satGLCM[i];
    }
    
  //cache
    private float[] lightGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean lightCheck = false;
    private float[][] glcmLight;
    private boolean glcmLightCheck = false;
    
    public double getLightnessGLCM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(lightCheck == false){
    		getLight();
    		lightCheck = true;
    	}
    	if(glcmLightCheck == false){
    		glcmLight = new float[lightColour.size()][lightColour.size()];
    		glcmCal(lightImage, glcmLight, lightColour);			
    		glcmLightCheck = true;
    	}
        if (lightGLCM[i] == -1) 
        	lightGLCM[i] = getGLCMStat(i, lightImage, glcmLight, lightColour);
        
        return lightGLCM[i];
    }
    
    //cache
    private float[] redGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean redCheck = false;
    private float[][] glcmRed;
    private boolean glcmRedCheck = false;
    
    public double getRedGLCM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(redCheck == false){
    		getRedColour();
    		redCheck = true;
    	}
    	if(glcmRedCheck == false){
    		glcmRed = new float[redColour.size()][redColour.size()];
    		glcmCal(redImage, glcmRed, redColour);			
    		glcmRedCheck = true;
    	}
        if (redGLCM[i] == -1) 
        	redGLCM[i] = getGLCMStat(i, redImage, glcmRed, redColour);
        
        return redGLCM[i];
    }
    
    //cache
    private float[] greenGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean greenCheck = false;
    private float[][] glcmGreen;
    private boolean glcmGreenCheck = false;
    
    public double getGreenGLCM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(greenCheck == false){
    		getGreenColour();
    		greenCheck = true;
    	}
    	if(glcmGreenCheck == false){
    		glcmGreen = new float[greenColour.size()][greenColour.size()];
    		glcmCal(blueImage, glcmGreen, greenColour);			
    		glcmGreenCheck = true;
    	}
        if (greenGLCM[i] == -1) 
        	greenGLCM[i] = getGLCMStat(i, greenImage, glcmGreen, greenColour);
        
        return greenGLCM[i];
    }
    
    //cache
    private float[] blueGLCM = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    private boolean blueCheck = false;
    private float[][] glcmBlue;
    private boolean glcmBlueCheck = false;
    
    public double getBlueGLCM(int i) {    	
    	//have not got colour from the image's shape yet    	
    	if(blueCheck == false){
    		getBlueColour();
    		blueCheck = true;
    	}
    	if(glcmBlueCheck == false){
    		glcmBlue = new float[blueColour.size()][blueColour.size()];
    		glcmCal(blueImage, glcmBlue, blueColour);			
    		glcmBlueCheck = true;
    	}    		
        if (blueGLCM[i] == -1) 
        	blueGLCM[i] = getGLCMStat(i, blueImage,glcmBlue, blueColour);
        
        return blueGLCM[i];
    }
    
    /**
     * texture properties regarding GLCM
     */
    public float getGLCMStat(int i, int[][] image, float[][] glcm, ArrayList<Integer> colour){				
		TextureStatistics solver = new TextureStatistics();
		switch (i) {
		case 0:		//mean
            return solver.mean(glcm,colour);
        case 1:		//variance
            return solver.variance(glcm,colour);
        case 2:		//entropy
            return solver.entropy(glcm);
        case 3:		//uniformity
            return solver.uniformity(glcm);
        case 4:		//max
            return solver.max(glcm);
        case 5:		//correlation
        	solver.mean(glcm,colour);
        	solver.variance(glcm,colour);
            return solver.correlation(glcm,colour);
        case 6:		//homogeneity
            return solver.homogeneity(glcm,colour);
        case 7:		//ineria
            return solver.ineria(glcm,colour);
        case 8:		//clusterShade
        	solver.mean(glcm,colour);
            return solver.clusterShade(glcm,colour);
        default:
            return -1;
		}		
    }
       
    /**
     * texture GLCM: Grey Level Co-occurrence Matrix
     */
    public void glcmCal(int[][] image, float[][] glcm, ArrayList<Integer> colour){
    	//System.gc();
    	
    	int sum, startJ;
		int width = boundingWidth, height = boundingHeight;
		
		int conts = (2*boundingWidth*(boundingHeight-1))+(2*boundingHeight*(boundingWidth-1))+(4*(boundingWidth-1)*(boundingHeight-1));
		int skip = 4;	//skip pixels
		int dist = 1; 	//the distance of neighbours
		//half matrix loop
		startJ = -1;
		for(int i=0; i<glcm.length; i++){
			startJ++;
			for(int j=startJ; j<glcm.length; j++){
				sum = 0;
				//image loop
				for(int x=0; x<width; x+=skip){
					for(int y=0; y<height; y+=skip){
						if(image[x][y]==colour.get(i)){
							//specific angels
							//0 degree 
							if(x<width-1){
								//degree 0 positive
								if(image[x+dist][y]==colour.get(j))
									sum++;
							}
							if(x>0){
								//0 degree negative
								if(image[x-dist][y]==colour.get(j))
									sum++;
							}
							//45 degree 
							if(x<width-1 && y>0){
								//45 degrees positive
								if(image[x+dist][y-dist]==colour.get(j))
									sum++;
							}
							if(x>0 && y<height-1){
								//45 degrees negative
								if(image[x-dist][y+dist]==colour.get(j))
									sum++;
							}
							//90 degree 
							if(y>0){
								//90 degrees positive
								if(image[x][y-dist]==colour.get(j))
									sum++;
							}
							if(y<height-1){
								//90 degrees negative
								if(image[x][y+dist]==colour.get(j))
									sum++;
							}
							//degree 135
							if(x>0 && y>0){
								//135 degrees positive
								if(image[x-dist][y-dist]==colour.get(j))
									sum++;
							}
							if(x<width-1 && y<height-1){
								//135 degrees negative
								if(image[x+dist][y+dist]==colour.get(j))
									sum++;
							}
						}
					}
				}
				glcm[i][j] = (float)sum/conts;
				if(i!=j)
					glcm[j][i] = (float)sum/conts;
			}
		}
	}  
    
    //cache
    private float[] greyGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmGrey;
    private boolean glrmGreyCheck = false; 
    
    public double getGreyGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(greyCheck == false){
    		getGreyColour();
    		greyCheck = true;
    	}
    	if(glrmGreyCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);    		
    		//run length starts at 0 that its length is 1
    		glrmGrey = new int[greyColour.size()][max];
    		glrmCal(greyImage, glrmGrey, greyColour);
    		glrmGreyCheck = true;
    		
    	}
        if (greyGLRM[i] == -1) 
        	greyGLRM[i] = getGLRMStat(i, greyImage, glrmGrey, greyColour);
        
        return greyGLRM[i];
    }
    
    //cache
    private float[] hueGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmHue;
    private boolean glrmHueCheck = false;
    
    public double getHueGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(hueCheck == false){
    		getHueColour();
    		hueCheck = true;
    	}
    	if(glrmHueCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);   		
    		//run length starts at 0 that its length is 1
    		glrmHue = new int[hueColour.size()][max];
    		glrmCal(hueImage, glrmHue, hueColour);
    		glrmHueCheck = true;
    	}
        if (hueGLRM[i] == -1) 
        	hueGLRM[i] = getGLRMStat(i, hueImage, glrmHue, hueColour);
        
        return hueGLRM[i];
    }
    
  //cache
    private float[] satGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmSat;
    private boolean glrmSatCheck = false;
    
    public double getSaturationGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(satCheck == false){
    		getSat();
    		satCheck = true;
    	}
    	if(glrmSatCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);   		
    		//run length starts at 0 that its length is 1
    		glrmSat = new int[satColour.size()][max];
    		glrmCal(satImage, glrmSat, satColour);
    		glrmSatCheck = true;
    	}
        if (satGLRM[i] == -1) 
        	satGLRM[i] = getGLRMStat(i, satImage, glrmSat, satColour);
        
        return satGLRM[i];
    }
    
  //cache
    private float[] lightGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmLight;
    private boolean glrmLightCheck = false;
    
    public double getLightnessGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(lightCheck == false){
    		getLight();
    		lightCheck = true;
    	}
    	if(glrmLightCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);   		
    		//run length starts at 0 that its length is 1
    		glrmLight = new int[lightColour.size()][max];
    		glrmCal(lightImage, glrmLight, lightColour);
    		glrmLightCheck = true;
    	}
        if (lightGLRM[i] == -1) 
        	lightGLRM[i] = getGLRMStat(i, lightImage, glrmLight, lightColour);
        
        return lightGLRM[i];
    }
    
    //cache
    private float[] redGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmRed;
    private boolean glrmRedCheck = false;
    
    public double getRedGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(redCheck == false){
    		getRedColour();
    		redCheck = true;
    	}
    	if(glrmRedCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);   		
    		//run length starts at 0 that its length is 1
    		glrmRed = new int[redColour.size()][max];
    		glrmCal(redImage, glrmRed, redColour);
    		glrmRedCheck = true;
    	}
        if (redGLRM[i] == -1) 
        	redGLRM[i] = getGLRMStat(i, redImage, glrmRed, redColour);
        
        return redGLRM[i];
    }
    
  //cache
    private float[] greenGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmGreen;
    private boolean glrmGreenCheck = false;
    
    public double getGreenGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(greenCheck == false){
    		getGreenColour();
    		greenCheck = true;
    	}
    	if(glrmGreenCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);  		
    		//run length starts at 0 that its length is 1
    		glrmGreen = new int[greenColour.size()][max];
    		glrmCal(greenImage, glrmGreen, greenColour);
    		glrmGreenCheck = true;
    	}
        if (greenGLRM[i] == -1) 
        	greenGLRM[i] = getGLRMStat(i, greenImage, glrmGreen, greenColour);
        
        return greenGLRM[i];
    }
    
  //cache
    private float[] blueGLRM = {-1, -1, -1, -1, -1, -1};
    private int[][] glrmBlue;
    private boolean glrmBlueCheck = false;
    
    public double getBlueGLRM(int i) {
    	//have not got colour from the image's shape yet    	
    	if(blueCheck == false){
    		getBlueColour();
    		blueCheck = true;
    	}
    	if(glrmBlueCheck == false){
    		int max = Math.max(boundingWidth,boundingHeight);   		
    		//run length starts at 0 that its length is 1
    		glrmBlue = new int[blueColour.size()][max];
    		glrmCal(blueImage, glrmBlue, blueColour);
    		glrmBlueCheck = true;
    	}
        if (blueGLRM[i] == -1) 
        	blueGLRM[i] = getGLRMStat(i, blueImage, glrmBlue, blueColour);
        
        return blueGLRM[i];
    }
    
    /**
     * texture properties regarding GLRM
     */
    public float getGLRMStat(int i, int[][] colourImage, int[][] glrm , ArrayList<Integer> colour){
		TextureStatistics solver = new TextureStatistics();		
		switch (i) {
		case 0:		//shortRun
            return solver.shortRun(glrm);
        case 1:		//longRun
            return solver.longRun(glrm);
        case 2:		//greyLevelNonUniformity
            return solver.greyLevelNonUniformity(glrm);
        case 3:		//runLengthNonUniformity
            return solver.runLengthNonUniformity(glrm);
        case 4:		//runPercent
            return solver.runPercent(glrm);
        case 5:		//runEntropy
            return solver.runEntropy(glrm);
        default:
            return -1;
		}				
    }
      
    
    /**
     * texture GLRM: Grey Level Run Matrix
     */
    public void glrmCal(int[][] image, int[][] glrm, ArrayList<Integer> colour){
    	//System.gc();
		
		int width = boundingWidth, height = boundingHeight;		
		int length=0, x, y, start = 0, total = 0, check, cx, cy, index;
		int skip=4;	//skip pixels
		//horizontal loop
		for(y=0; y<height; y+=skip){
			x = 0;
			check = 0;
			while(x<width-1){
				start = image[x][y];
				if(start != -1){					
					length = 0;		//length+1 = run length 
					while(image[++x][y]==start){
						length++;
						if(x == width-1){
							index = colour.indexOf(start);
							glrm[index][length]++;
							total++;
							check = 1;
							break;
						}
					}
					if(x != width-1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
				}
				else{
					x++;					
				}
			}
			//x = width-1
			if(check==0){
				if(start!=-1){
					if(image[x][y] == -1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
					else{	//image.value[x][y]!=start					
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
				else{
					if(image[x][y] != -1){
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
			}
		}
		
		
		//vertical loop
		for(x=0; x<width; x+=skip){
			y=0;
			check = 0;
			while(y<height-1){
				start = image[x][y];
				if(start != -1){					
					length = 0;		//length+1 = run length 
					while(image[x][++y]==start){
						length++;
						if(y==height-1){
							index = colour.indexOf(start);
							glrm[index][length]++;
							total++;
							check = 1;
							break;
						}
					}
					if(y!=height-1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
				}
				else{
					y++;					
				}
			}
			//y = height-1
			if(check==0){
				if(start!=-1){
					if(image[x][y] == -1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
					else{	//image.value[x][y]!=start					
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
				else{
					if(image[x][y] != -1){
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
			}
		}
	
				
		//135 degrees down
		cx = 0;
		//half top 
		while(cx < width-1){			
			x = cx;
			y = 0;
			check = 0;
			while(x<width-1 && y<height-1){
				start = image[x][y];
				if(start != -1){					
					length = 0;		//length+1 = run length 
					while(image[++x][++y]==start){
						length++;
						if(x == width-1 || y==height-1){
							index = colour.indexOf(start);
							glrm[index][length]++;
							total++;
							check = 1;
							break;
						}
					}
					if(x != width-1 && y!=height-1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
				}
				else{
					x++;
					y++;					
				}
			}
			//x = width-1 or y = height-1
			if(check==0){
				if(start!=-1){
					if(image[x][y] == -1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
					else{	//image.value[x][y]!=start					
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
				else{
					if(image[x][y] != -1){
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
			}
			cx += skip;
		}
		//x = width-1 and y = 0
		if(image[width-1][0]!=-1){
			index = colour.indexOf(image[width-1][0]);
			glrm[index][0]++;
			total++;
		}

		cy = 1;
		//half bottom 
		while(cy < height-1){
			y = cy;
			x = 0;
			check = 0;
			while(x<width-1 && y<height-1){
				start = image[x][y];
				if(start != -1){					
					length = 0;		//length+1 = run length 
					while(image[++x][++y]==start){
						length++;
						if(x==width-1 || y==height-1){
							index = colour.indexOf(start);
							glrm[index][length]++;
							total++;
							check = 1;
							break;
						}
					}
					if(x!=width-1 && y!=height-1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
				}
				else{
					x++;
					y++;					
				}
			}
			//x = width-1 or y = height-1
			if(check==0){
				if(start!=-1){
					if(image[x][y] == -1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
					else{	//image.value[x][y]!=start					
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
				else{
					if(image[x][y] != -1){
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
			}
			cy += skip;
		}
		//x = 0 and y = height-1
		if(image[0][height-1]!=-1){
			index = colour.indexOf(image[0][height-1]);
			glrm[index][0]++;
			total++;
		}
		
		
		//45 degrees up
		cx = width-1;
		//half top 
		while(cx>0){			
			x = cx;
			y = 0;
			check = 0;
			while(x>0 && y<height-1){
				start = image[x][y];
				if(start != -1){					
					length = 0;		//length+1 = run length 
					while(image[--x][++y]==start){
						length++;
						if(x==0 || y==height-1){
							index = colour.indexOf(start);
							glrm[index][length]++;
							total++;
							check = 1;
							break;
						}
					}
					if(x!=0 && y!=height-1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
				}
				else{
					x--;
					y++;					
				}
			}
			//x = 0 or y = height-1
			if(check==0){
				if(start!=-1){
					if(image[x][y] == -1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
					else{	//image.value[x][y]!=start					
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
				else{
					if(image[x][y] != -1){
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
			}
			cx -= skip;
		}
		//x = 0 and y = 0
		if(image[0][0]!=-1){
			index = colour.indexOf(image[0][0]);
			glrm[index][0]++;
			total++;
		}

		cy = 1;
		//half bottom 
		while(cy < height-1){			
			y = cy;
			x = width-1;
			check = 0;
			while(x>0 && y<height-1){
				start = image[x][y];
				if(start != -1){					
					length = 0;		//length+1 = run length 
					while(image[--x][++y]==start){
						length++;
						if(x==0 || y==height-1){
							index = colour.indexOf(start);
							glrm[index][length]++;
							total++;
							check = 1;
							break;
						}
					}
					if(x!=0 && y!=height-1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
				}
				else{
					x--;
					y++;					
				}
			}
			//y = height-1
			if(check==0){
				if(start!=-1){
					if(image[x][y] == -1){
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
					}
					else{	//image.value[x][y]!=start					
						index = colour.indexOf(start);
						glrm[index][length]++;
						total++;
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
				else{
					if(image[x][y] != -1){
						index = colour.indexOf(image[x][y]);
						glrm[index][0]++;
						total++;
					}
				}
			}
			cy += skip;
		}
		//x = width-1 and y = height-1
		if(image[width-1][height-1]!=-1){
			index = colour.indexOf(image[width-1][height-1]);
			glrm[index][0]++;
			total++;
		}
		
	}
                
}

