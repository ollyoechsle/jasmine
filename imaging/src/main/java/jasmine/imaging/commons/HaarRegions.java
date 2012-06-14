package jasmine.imaging.commons;



import java.io.File;

/**
 * <p/>
 * Allows certain Haar Statistics about an image to be returned.
 * The class uses optimisations as described by Viola and Jones's
 * Integral Image which means that while the constructor has a slight
 * time delay in preparing the optimisation, the imaging functions themselves
 * should be extremely fast.
 * </p>
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
 *
 * @author Olly Oechsle, University of Essex, Date: 07-Sep-2006
 * @version 1.0
 */
public class HaarRegions implements SimpleImage {

    protected PixelLoader image;
    protected int imageWidth, imageHeight;

//    public static void main(String[] args) throws Exception {
//        // tests haar regions
//
//        // create a pixel loader
//        final File trueDirectory2 = new File("/home/ooechs/Data/faces");
//        PixelLoader image = new PixelLoader(new File(trueDirectory2, "blended.bmp"));
//        HaarRegions haar = new HaarRegions(image);
//        haar.makeWindowFillImage(19, 19);
//        System.out.println(haar.getOneRectangleFeature(16, 0, 1, 2));
//
//    }


    /**
     * Allows access to the original image pixels.
     */
    public PixelLoader getPixelLoader() {
        return image;
    }

    public static final int HORIZONTALLY_ADJACENT = 1;
    public static final int VERTICALLY_ADJACENT = 2;

    public static final int FIRST_SHAPE = 1;
    public static final int SECOND_SHAPE = 2;

    public static final int DEFAULT_WINDOW_WIDTH = 32;
    public static final int DEFAULT_WINDOW_HEIGHT = 40;

    public static final int DEFAULT_BLOCKS_X = 16;
    public static final int DEFAULT_BLOCKS_Y = 20;

    /**
     * The number of blocks in the window in the X direction
     */
    protected int windowBlocksX = DEFAULT_BLOCKS_X;

    /**
     * The number of blocks in the window in the Y direction
     */
    protected int windowBlocksY = DEFAULT_BLOCKS_Y;

    /**
     * The width of the window in pixels. This number should be evenly
     * divisible by windowBlocksX
     */
    protected int windowWidth = DEFAULT_WINDOW_WIDTH;

    /**
     * The height of the window in pixels. This number should be evenly
     * divisible by windowBlocksY
     */
    protected int windowHeight = DEFAULT_WINDOW_HEIGHT;

    /**
     * The X position of the window's top left corner.
     */
    protected int windowX = 0;

    /**
     * The Y position of the window's top left corner.
     */
    protected int windowY = 0;

    public void makeWindowFillImage(int windowBlocksX, int windowBlocksY) {
        setWindowPosition(0, 0, image.getWidth(), image.getHeight(), windowBlocksX, windowBlocksY);
    }


    public void setWindowPosition(int windowX, int windowY) {

        if (windowX > (image.getWidth() - windowWidth)) {
            throw new RuntimeException("WindowX is too large - the window is outside the image boundary. (windowX=" + windowX + ", imageWidth=" + image.getWidth() + ", windowWidth=" + windowWidth + ")");
        }

        if (windowY > (image.getHeight() - windowHeight)) {
            throw new RuntimeException("WindowY is too large - the window is outside the image boundary. (windowY=" + windowY + ", imageHeight=" + image.getHeight() + ", windowHeight=" + windowHeight + ")");
        }

        this.windowX = windowX;
        this.windowY = windowY;

    }

    /**
     * Function to set the position of the window. Calls to the various get
     * rectangle functions are made relative to this window position and
     * size.
     * <p/>
     * An exception is thrown if the window is placed outside the boundaries of the
     * image, or if the window is larger than the image.
     *
     * @param windowBlocksX The number of "blocks" in the X direction
     * @param windowBlocksY The number of "blocks" in the Y direction
     */
    public void setWindowPosition(int windowX, int windowY, int windowWidth, int windowHeight, int windowBlocksX, int windowBlocksY) {

        // ensure that the width and height chosen divide evenly into the chosen number of blocks
        if (windowWidth % windowBlocksX != 0) {
            throw new RuntimeException(image.getFile().getName() + ": The window's width (" + windowWidth + ") does not divide evenly into " + windowBlocksX + " vertical segments.");
        }

        if (windowHeight % windowBlocksY != 0) {
            throw new RuntimeException(image.getFile().getName() + ": The window's height (" + windowHeight + ") does not divide evenly into " + windowBlocksY + " horizontal segments.");
        }

        if (windowWidth > image.getWidth()) {
            throw new RuntimeException(image.getFile().getName() + ": Window is wider (" + windowWidth + ") than the image (" + getWidth() + ")");
        }

        if (windowHeight > image.getHeight()) {
            throw new RuntimeException(image.getFile().getName() + ": Window is taller (" + windowHeight + ") than the image (" + getHeight() + ")");
        }

        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        setWindowPosition(windowX, windowY);

        this.windowBlocksX = windowBlocksX;
        this.windowBlocksY = windowBlocksY;

        this.blockWidth = windowWidth / windowBlocksX;
        this.blockHeight = windowHeight / windowBlocksY;

    }

    /**
     * The width of a block in pixels, which is windowWidth / windowBlocksX
     */
    private int blockWidth;

    /**
     * The height of a block in pixels, which is windowHeight / windowBlocksY
     */
    private int blockHeight;

    /**
     * Finds the actual position on the integral image when we are presented with a block's X position.
     */
    public int getActualPositionX(int blocksLeft) {

        return windowX + (blocksLeft * blockWidth);

    }

    /**
     * Finds the actual position on the integral image when we are presented with a block's Y position.
     */
    public int getActualPositionY(int blocksTop) {

        return windowY + (blocksTop * blockHeight);

    }

    /**
     * Initialises the Haar Regions object, and performs preliminary calculations
     * on the image.
     * <p/>
     * MaxX and MaxY define the maximum size of the window being used.
     * <p/>
     * ResX and ResY define how many blocks across the image are to be used in
     * each direction. If resX and ResY are the same as MaxX and MaxY then
     * an image recognition program is created, but if you want to use this image
     * in a sliding window approach, then ResX and ResY will be larger than MaxX
     * and windowBlocksY.
     *
     * @param image An image from which we are allowed access to its width, height, and greyscale pixel values (0-255)
     */
    public HaarRegions(PixelLoader image) {

        this.integralImage = getIntegralImage(image, 1, 1);

        this.image = image;

        this.imageWidth = image.getWidth();

        this.imageHeight = image.getHeight();

    }

    protected int[][] integralImage;

    /**
     * Calculates the sum of pixels at any given point on an image. An implementation of the algorithm as
     * described by Viola and Jones. By storing sums of all regions relative to the origin (0,0), we can use
     * basic arithmetic to find the sum of any arbitrary region.
     *
     * @param image An image from which we are allowed access to its width, height, and greyscale pixel values (0-255)
     * @return A 2D array of the pixel sums at any pixel, relative to the origin at (0,0)
     */
    private int[][] getIntegralImage(PixelLoader image, int baseResolutionX, int baseResolutionY) {

        // Prepare an array to store the results in
        int integralImage[][] = new int[image.getWidth() / baseResolutionX + 1][image.getHeight() / baseResolutionY + 1];

        // The array is at a different position to the image
        // If the baseResolution is more than 1. A bit messy but
        // avoids unnecessary multiplications
        int arrX = 1;

        // Move through each column in the image
        for (int x = 0; x < image.getWidth(); x += baseResolutionX) {

            int columnTotal = 0;
            int arrY = 1;

            // Move down the column
            for (int y = 0; y < image.getHeight(); y += baseResolutionY) {

                // Find the sum of pixels in this column, to this particular Y value
                // This involves evaluating a square of baseResolution x baseResolution pixels.
                for (int dx = 0; dx < baseResolutionX; dx++)
                    for (int dy = 0; dy < baseResolutionY; dy++)
                        columnTotal += image.getGreyValue(x + dx, y + dy);

                // sum total = value of row to left + columnTotal
                integralImage[arrX][arrY] = (arrX == 0 ? 0 : integralImage[arrX - 1][arrY]) + columnTotal;

                // results Y index
                arrY++;

            }

            // results X index
            arrX++;

        }

        return integralImage;

    }

    /**
     * Gets the sum total of pixels in a given region.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public int getRegionSum(int x, int y, int width, int height) {

        int val1 = integralImage[x][y];
        int val2 = integralImage[x + width][y];
        int val3 = integralImage[x][y + height];
        int val4 = integralImage[x][y];

        return val4 - val2 - val3 + val1;

    }

    public float getOneRectangleFeature(int x, int y, int width, int height) {

        try {

            if (x + width > windowBlocksX) {
                if (width > windowBlocksX) {
                    //System.err.println("Width is too much!" + width + " > " + windowBlocksX);
                    return -1;
                } else {
                    x = windowBlocksX - width;
                }
            }

            if (y + height > windowBlocksY) {
                if (height > windowBlocksY) {
                    //System.err.println("Height is too much!" + height + " > " + windowBlocksY);
                    return -1;
                } else {
                    y = windowBlocksY - height;
                }
            }

            // average function
            float pixelsPerSquare = (width * blockWidth) * (height * blockHeight);

            int val1 = integralImage[getActualPositionX(x)][getActualPositionY(y)];
            int val2 = integralImage[getActualPositionX(x + width)][getActualPositionY(y)];
            int val4 = integralImage[getActualPositionX(x)][getActualPositionY(y + height)];
            int val5 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height)];

            int firstSum = val5 - val2 - val4 + val1;

            return firstSum / pixelsPerSquare;

        } catch (ArrayIndexOutOfBoundsException e) {

            System.out.println("Array Index Out of Bounds\n----------");
            System.out.println("Image width: " + getWidth());
            System.out.println("Image height: " + getHeight());
            System.out.println("X: " + x);
            System.out.println("Y: " + y);
            System.out.println("Width: " + width);
            System.out.println("Height: " + height);

            throw e;

        }

    }

    public double getOneRectangleMin(int x, int y, int width, int height) {

        if (x + width > windowBlocksX) {
            if (width > windowBlocksX) {
                System.err.println("Width is too much!" + width + " > " + windowBlocksX);
                return -1;
            } else {
                x = windowBlocksX - width;
            }
        }

        if (y + height > windowBlocksY) {
            if (height > windowBlocksY) {
                System.err.println("Height is too much!" + height + " > " + windowBlocksY);
                return -1;
            } else {
                y = windowBlocksY - height;
            }
        }

        x = getActualPositionX(x);
        y = getActualPositionY(y);

        // how many pixels

        int min = 256;

        for (int dY = y; dY < y + (height * blockHeight); dY++) {

            for (int dX = x; dX < x + (width * blockWidth); dX++) {

                if (image.getGreyValue(dX, dY) < min) {
                    min = image.getGreyValue(dX, dY);
                }

            }

        }

        return min;

    }

    public double getOneRectangleMax(int x, int y, int width, int height) {

        if (x + width > windowBlocksX) {
            if (width > windowBlocksX) {
                //System.err.println("Width is too much!" + width + " > " + windowBlocksX);
                return -1;
            } else {
                x = windowBlocksX - width;
            }
        }

        if (y + height > windowBlocksY) {
            if (height > windowBlocksY) {
                //System.err.println("Height is too much!" + height + " > " + windowBlocksY);
                return -1;
            } else {
                y = windowBlocksY - height;
            }
        }

        x = getActualPositionX(x);
        y = getActualPositionY(y);

        // how many pixels

        int max = 0;

        for (int dY = y; dY < y + (height * blockHeight); dY++) {

            for (int dX = x; dX < x + (width * blockWidth); dX++) {

                if (image.getGreyValue(dX, dY) > max) {
                    max = image.getGreyValue(dX, dY);
                }

            }

        }

        return max;

    }

    public double getOneRectangleStdDev(int x, int y, int width, int height) {

        if (x + width > windowBlocksX) {
            if (width > windowBlocksX) {
                //System.err.println("Width is too much!" + width + " > " + windowBlocksX);
                return -1;
            } else {
                x = windowBlocksX - width;
            }
        }

        if (y + height > windowBlocksY) {
            if (height > windowBlocksY) {
                //System.err.println("Height is too much!" + height + " > " + windowBlocksY);
                return -1;
            } else {
                y = windowBlocksY - height;
            }
        }

        x = getActualPositionX(x);
        y = getActualPositionY(y);

        // how many pixels

        FastStatistics solver = new FastStatistics();

        for (int dY = y; dY < y + (height * blockHeight); dY++) {
            for (int dX = x; dX < x + (width * blockWidth); dX++) {
                solver.addData(image.getGreyValue(dX, dY));
            }
        }

        return solver.getStandardDeviation();
    }


    /**
     * @param x         A Zero Indexed value
     * @param y         A Zero Indexed value
     * @param width
     * @param height
     * @param adjacency
     * @param shape
     * @return The difference of sums between two adjacent rectangles.
     */
    public float getTwoRectangleFeature(int x, int y, int width, int height, int adjacency, int shape) {

        if (adjacency == HORIZONTALLY_ADJACENT) {

            if (x + width + width > windowBlocksX) {
                if (width + width > windowBlocksX) {
                    //System.err.println("Width is too much!" + width + " > " + windowBlocksX);
                    return -1;
                } else {
                    x = windowBlocksX - (2 * width);
                }
            }

            if (y + height > windowBlocksY) {
                if (height > windowBlocksY) {
                    //System.err.println("Height is too much!" + height + " > " + windowBlocksY);
                    return -1;
                } else {
                    y = windowBlocksY - height;
                }
            }

            /**
             * 1-----2-----3
             * | 1st | 2nd |
             * 4-----5-----6
             */
            int val1 = integralImage[getActualPositionX(x)][getActualPositionY(y)];
            int val2 = integralImage[getActualPositionX(x + width)][getActualPositionY(y)];
            int val3 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y)];

            int val4 = integralImage[getActualPositionX(x)][getActualPositionY(y + height)];
            int val5 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height)];
            int val6 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y + height)];

            int firstSum = val5 - val2 - val4 + val1;
            int secondSum = val6 - val3 - val5 + val2;

            // average function
            float pixelsPerSquare = (width * blockWidth) * (height * blockHeight);

            return (shape == FIRST_SHAPE ? firstSum - secondSum : secondSum - firstSum) / (1 * pixelsPerSquare);

        } else {

            if (x + width > windowBlocksX) {
                if (width > windowBlocksX) {
                    //System.err.println("Width is too much!" + width + " > " + windowBlocksX);
                    return -1;
                } else {
                    x = windowBlocksX - width;
                }
            }

            if (y + height + height > windowBlocksY) {
                if (height + height > windowBlocksY) {
                    //System.err.println("Height is too much!" + height + " > " + windowBlocksY);
                    return -1;
                } else {
                    y = windowBlocksY - (2 * height);
                }
            }

            /**
             * 1-----2
             * | 1st |
             * 3-----4
             * | 2nd |
             * 5-----6
             */
            int val1 = integralImage[getActualPositionX(x)][getActualPositionY(y)];
            int val2 = integralImage[getActualPositionX(x + width)][getActualPositionY(y)];
            int val3 = integralImage[getActualPositionX(x)][getActualPositionY(y + height)];
            int val4 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height)];
            int val5 = integralImage[getActualPositionX(x)][getActualPositionY(y + height + height)];
            int val6 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height + height)];

            int firstSum = val4 - val2 - val3 + val1;
            int secondSum = val6 - val4 - val5 + val3;

            // average function
            float pixelsPerSquare = (width * blockWidth) * (height * blockHeight);

            return (shape == FIRST_SHAPE ? firstSum - secondSum : secondSum - firstSum) / (1 * pixelsPerSquare);

        }
    }

    public float getThreeRectangleFeature(int x, int y, int width, int height, int adjacency) {

        //System.out.println("x: " + x + ", y: " + y + ", width: " + width + ", height: " + height);

        if (adjacency == HORIZONTALLY_ADJACENT) {

            if ((width * 3) > getWindowBlocksX()) {
                width = getWindowBlocksX() / 3;
            }

            if (x + (3 * width) > windowBlocksX) {
                x = windowBlocksX - (3 * width);
            }

            if (y + height + 1 > windowBlocksY) {
                y = windowBlocksY - height;
            }

            //System.out.println("x: " + x + ", y: " + y + ", width: " + width + ", height: " + height);

            /**
             * 1-----2-----3-----4
             * | 1st | 2nd | 3rd |
             * 5-----6-----7-----8
             */

            int val1 = integralImage[getActualPositionX(x)][getActualPositionY(y)];
            int val2 = integralImage[getActualPositionX(x + width)][getActualPositionY(y)];
            int val3 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y)];
            int val4 = integralImage[getActualPositionX(x + width + width + width)][getActualPositionY(y)];

            int val5 = integralImage[getActualPositionX(x)][getActualPositionY(y + height)];
            int val6 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height)];
            int val7 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y + height)];
            int val8 = integralImage[getActualPositionX(x + width + width + width)][getActualPositionY(y + height)];

            int firstRegion = val6 - val2 - val5 + val1;
            int secondRegion = val7 - val3 - val6 + val2;
            int thirdRegion = val8 - val4 - val7 + val3;

            // average function
            float pixelsPerSquare = (width * blockWidth) * (height * blockHeight);

            return (firstRegion + thirdRegion - secondRegion) / pixelsPerSquare;

        } else {

            // System.out.println("VERTICALLY_ADJACENT");

            if ((height * 3) > windowBlocksY) {
                height = getWindowBlocksY() / 3;
            }

            if (x + width + 1 > windowBlocksX) {
                x = windowBlocksX - width;
            }

            if (y + (3 * height) + 1 > windowBlocksY) {
                y = windowBlocksY - (3 * height);
            }

            /**
             * 1-----2
             * | 1st |
             * 3-----4
             * | 2nd |
             * 5-----6
             * | 2nd |
             * 7-----8
             */
            int val1 = integralImage[getActualPositionX(x)][getActualPositionY(y)];
            int val2 = integralImage[getActualPositionX(x + width)][getActualPositionY(y)];

            int val3 = integralImage[getActualPositionX(x)][getActualPositionY(y + height)];
            int val4 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height)];

            int val5 = integralImage[getActualPositionX(x)][getActualPositionY(y + height + height)];
            int val6 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height + height)];

            int val7 = integralImage[getActualPositionX(x)][getActualPositionY(y + height + height + height)];
            int val8 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height + height + height)];

            int firstRegion = val4 - val2 - val3 + val1;
            int secondRegion = val6 - val4 - val5 + val3;
            int thirdRegion = val8 - val6 - val7 + val5;

            // average function
            float pixelsPerSquare = (width * blockWidth) * (height * blockHeight);

            return (firstRegion + thirdRegion - secondRegion) / (2 * pixelsPerSquare);

        }

    }

    public float getFourRectangleFeature(int x, int y, int width, int height, int shape) {


        try {

            if (x + (2 * width) + 1 > windowBlocksX) {
                if ((width * 2) > (windowBlocksX)) {
                    width = windowBlocksX / 2;
                }
                x = windowBlocksX - (2 * width);
            }

            if (y + (2 * height) + 1 > windowBlocksY) {
                if ((height * 2) > windowBlocksY) {
                    height = windowBlocksY / 2;
                }
                y = windowBlocksY - (2 * height);
            }

            /**
             * 1-----2-----3
             * | 1st | 2nd |
             * 4-----5-----6
             * | 2nd | 1st |
             * 7-----8-----9
             */

            int val1 = integralImage[getActualPositionX(x)][getActualPositionY(y)];
            int val2 = integralImage[getActualPositionX(x + width)][getActualPositionY(y)];
            int val3 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y)];

            int val4 = integralImage[getActualPositionX(x)][getActualPositionY(y + height)];
            int val5 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height)];
            int val6 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y + height)];

            int val7 = integralImage[getActualPositionX(x)][getActualPositionY(y + height + height)];
            int val8 = integralImage[getActualPositionX(x + width)][getActualPositionY(y + height + height)];
            int val9 = integralImage[getActualPositionX(x + width + width)][getActualPositionY(y + height + height)];

            int largeSquare = val9 - val3 - val7 + val1;

            // average function
            float pixelsPerSquare = (width * blockWidth) * (height * blockHeight);

            if (shape == FIRST_SHAPE) {

                int topLeft = val5 - val2 - val4 + val1;
                int bottomRight = val9 - val6 - val8 + val5;

                return (largeSquare - (2 * (topLeft + bottomRight))) / (2 * pixelsPerSquare);

            } else {

                int topRight = val6 - val3 - val5 + val2;
                int bottomLeft = val8 - val5 - val7 + val4;

                return (largeSquare - (2 * (topRight + bottomLeft))) / (2 * pixelsPerSquare);

            }

        } catch (ArrayIndexOutOfBoundsException err) {
            System.out.println("Array index out of bounds");
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("width: " + width);
            System.out.println("height: " + height);

            if (x + (2 * width) + 1 > windowBlocksX) {
                x = windowBlocksX - (2 * width);
            }

            if (y + (2 * height) + 1 > windowBlocksY) {
                y = windowBlocksY - (2 * height);
            }

            System.out.println("x: " + x);
            System.out.println("y: " + y);

            throw err;

        }

    }

    // More Getter Functions Below

    public int getWindowBlocksX() {
        return windowBlocksX;
    }

    /**
     * There are a certain number of blocks
     */
    public int getWindowBlocksY() {
        return windowBlocksY;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowX() {
        return windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public int getWidth() {
        return imageWidth;
    }

    public int getHeight() {
        return imageHeight;
    }

    public File getFile() {
        return image.getFile();
    }

    public String getFilename() {
        return getFile().getName();
    }

}
