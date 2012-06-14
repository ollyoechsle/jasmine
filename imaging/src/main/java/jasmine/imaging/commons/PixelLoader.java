package jasmine.imaging.commons;


import jasmine.imaging.commons.util.Median;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

//POEY
import com.bbn.openmap.dataAccess.image.geotiff.GeoTIFFFile;

/**
 * Loads Pixels from an image file.
 *
 * @version 1.4 Option to turn off caching, fixed some bugs.
 */
public class PixelLoader implements Cloneable, SimpleImage {

    public static boolean CACHING_OTHERS = true;
    public static boolean CACHE_RGB_HSL = true;

    protected BufferedImage img;

    protected File file = null;

    protected int width, height;

    public boolean loadedOK = true;

    /**
     * Private constructor for the pixel loader
     */
    private PixelLoader() {
        loadedOK = true;
    }

    public void setFile(File f) {
        this.file = f;
    }

    /**
     * Load a blank PixelLoader image that can subsequently
     * be manipulated.
     */
    public PixelLoader(int width, int height) {
        this.file = null;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.width = width;
        this.height = height;
        loadedOK = true;
    }

    public PixelLoader(String filename) throws Exception {
        this(new File(filename));
    }

    public PixelLoader(InputStream stream) {

        try {

            this.file = null;

            // Use the Java ImageIO library to open the file as a buffered image.
            img = javax.imageio.ImageIO.read(stream);

            width = img.getWidth();
            height = img.getHeight();


        } catch (javax.imageio.IIOException e1) {

            e1.printStackTrace();

            loadedOK = false;

        } catch (Exception e) {

            e.printStackTrace();

            loadedOK = false;

        }

        if (img == null) {
            throw new RuntimeException("No Buffered Image in Pixel Loader. Image may not have loaded OK. Try a different file type?");
        }

    }

    public PixelLoader(File imageFile) throws Exception {

        // ensure that the file actually exists before proceeding
        if (!imageFile.exists()) throw new Exception("File does not exist: " + imageFile.getAbsolutePath());

        try {

            this.file = imageFile;
            
            // Use the Java ImageIO library to open the file as a buffered image.
            img = javax.imageio.ImageIO.read(imageFile);

            if(this.file.getName().endsWith(".tif")){
    			String filename = this.file.getAbsolutePath();
        		GeoTIFFFile gtf = new GeoTIFFFile(filename);                  
        		img = gtf.getBufferedImage();
            }
            
            width = img.getWidth();
            height = img.getHeight();


        } catch (javax.imageio.IIOException e1) {

            e1.printStackTrace();

            loadedOK = false;

        } catch (Exception e) {

            e.printStackTrace();

            loadedOK = false;

        }

        if (img == null) {
            throw new RuntimeException("No Buffered Image in Pixel Loader. Image may not have loaded OK. Try a different file type?");
        }

    }

    public PixelLoader(BufferedImage img) {
        this(img, null);
    }

    public PixelLoader(BufferedImage img, File f) {
    	this.img = img;
        width = img.getWidth();
        height = img.getHeight();
        loadedOK = true;
        this.file = f;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected IntegralImage integralImage;

    public IntegralImage getIntegralImage() {
        if (integralImage == null) {       	
            integralImage = new IntegralImage(this);
        }
        return integralImage;
    }

    protected Perimeter perimeter1;
    protected Perimeter perimeter2;

    public static int feature1Size = 2;
    public static int feature2Size = 5;

    public Perimeter getPerimeter1() {
        if (perimeter1 == null) {
            perimeter1 = new Perimeter(feature1Size);
        }
        return perimeter1;
    }

    public Perimeter getPerimeter2() {
        if (perimeter2 == null) {
            perimeter2 = new Perimeter(feature2Size);
        }
        return perimeter2;
    }

    protected Line hLine1, hLine2;
    protected Line vLine1, vLine2;

    public Line getHLine1() {
        if (hLine1 == null) {
            hLine1 = new Line(feature1Size, Line.HORIZONTAL);
        }
        return hLine1;
    }

    public Line getHLine2() {
        if (hLine2 == null) {
            hLine2 = new Line(feature2Size, Line.HORIZONTAL);
        }
        return hLine2;
    }

    public Line getVLine1() {
        if (vLine1 == null) {
            vLine1 = new Line(feature1Size, Line.VERTICAL);
        }
        return vLine1;
    }

    public Line getVLine2() {
        if (vLine2 == null) {
            vLine2 = new Line(feature2Size, Line.VERTICAL);
        }
        return vLine2;
    }

    // it is okay to cache these
    private int lightnessMean = -1;
    private double lightnessStdDeviation = -1;
    private int hueMean = -1;
    private double hueStdDeviation = -1;
    private int satMean = -1;
    private double satStdDeviation = -1;
    private double greyStdDeviation = -1;
    
    //POEY 
    private double normalisedRedMean = -1;
    private double normalisedRedStdDeviation = -1;
    private double normalisedGreenMean = -1;
    private double normalisedGreenStdDeviation = -1;
    private double normalisedBlueMean = -1;
    private double normalisedBlueStdDeviation = -1;
    private double C1C2C3Mean = -1;
    private double C1C2C3StdDeviation = -1;
    private double L1L2L3Mean = -1;
    private double L1L2L3StdDeviation = -1;
    private double greyValueMean = -1;
    private double greyValueStdDeviation = -1;
    private double imageMean = -1;
    private double imageStdDeviation = -1;
    private double imageRange = -1;
    private double imageRangeStdDeviation = -1;
    private double imageVariance = -1;
    private double imageVarianceStdDeviation = -1;
    private int imageBinary = -1;
    
    public int getLightnessMean() {
        if (lightnessMean == -1) calculateImageStatistics();
        return lightnessMean;
    }

    public double getLightnessStdDeviation() {
        if (lightnessStdDeviation == -1) calculateImageStatistics();
        return lightnessStdDeviation;
    }

    public int getHueMean() {
        if (hueMean == -1) calculateImageStatistics();
        return hueMean;
    }

    public double getHueStdDeviation() {
        if (hueStdDeviation == -1) calculateImageStatistics();
        return hueStdDeviation;
    }

    public int getSatMean() {
        if (satMean == -1) calculateImageStatistics();
        return satMean;
    }

    public double getSatStdDeviation() {
        if (satStdDeviation == -1) calculateImageStatistics();
        return satStdDeviation;
    }

    //POEY
    //mean of normalised RGB
    public double getNormalisedRedMean() {
        if (normalisedRedMean == -1) calculateImageStatisticsRGB();
        return normalisedRedMean;
    }
    
    public double getNormalisedRedStdDeviation() {
        if (normalisedRedStdDeviation == -1) calculateImageStatistics();
        return normalisedRedStdDeviation;
    }
    
    public double getNormalisedGreenMean() {
        if (normalisedGreenMean == -1) calculateImageStatisticsRGB();
        return normalisedGreenMean;
    }
    
    public double getNormalisedGreenStdDeviation() {
        if (normalisedGreenStdDeviation == -1) calculateImageStatistics();
        return normalisedGreenStdDeviation;
    }
    
    public double getNormalisedBlueMean() {
        if (normalisedBlueMean == -1) calculateImageStatisticsRGB();
        return normalisedBlueMean;
    }
    
    public double getNormalisedBlueStdDeviation() {
        if (normalisedBlueStdDeviation == -1) calculateImageStatistics();
        return normalisedBlueStdDeviation;
    }
    
    //POEY
    //mean of C1C2C3
    public double getC1C2C3Mean(int i) {
        calculateImageStatisticsC1C2C3(i);
        return C1C2C3Mean;
    }
    
    //POEY
    //mean of L1L2L3
    public double getL1L2L3Mean(int i) {
        calculateImageStatisticsL1L2L3(i);
        return L1L2L3Mean;
    }
    
//    //POEY
//    //mean of Grey values
//    public double getGreyValueMean() {
//        if (greyValueMean == -1) calculateImageStatisticsGrey();
//        return greyValueMean;
//    }
    
    //POEY
    public double get3x3MeanImage() {
        if (imageMean == -1) calculateImageStatisticsMean();
        return imageMean;
    }
    
    //POEY
    public double get3x3RangeImage() {
        if (imageRange == -1) calculateImageStatisticsRange();
        return imageRange;
    }
    
    //POEY
    public double get3x3VarianceImage() {
        if (imageVariance == -1) calculateImageStatisticsVariance();
        return imageVariance;
    }
    
    //POEY
    public double getAdaptiveBinaryThresholdOneValue() {
        if (imageBinary == -1) calculateImageStatisticsBinary();
        return imageBinary;
    }
    
    
    
    /**
     * Gets the standard deviation of greyness of the whole image.
     * Optimised. Was 51ms, now 7ms.
     */
    public double getStdDeviation() {
        if (greyStdDeviation == -1) {
            FastStatistics s = new FastStatistics();
            for (int y = 0; y < getHeight(); y += 1) {
                for (int x = 0; x < getWidth(); x += 1) {
                    s.addData(getGreyValue(x, y));
                }
            }
            greyStdDeviation = s.getStandardDeviation();
        }
        return greyStdDeviation;
    }

    /**
     * Optimised.
     * Was 461ms, now 17ms (!)
     */
    private void calculateImageStatistics() {

        FastStatistics hueSolver = new FastStatistics();
        FastStatistics saturationSolver = new FastStatistics();
        FastStatistics lightnessSolver = new FastStatistics();

        if (CACHE_RGB_HSL) {
            if (hueCache == null) createHSLCache();

            int c = 0;
            for (int y = 0; y < height; y += 1) {
                for (int x = 0; x < width; x += 1) {

                    hueSolver.addData(hueCache[c]);
                    saturationSolver.addData(satCache[c]);
                    lightnessSolver.addData(lightnessCache[c]);

                    c++;
                }
            }
        } else {
            for (int y = 0; y < height; y += 1) {
                for (int x = 0; x < width; x += 1) {
                    hueSolver.addData(getHue(x, y));
                    saturationSolver.addData(getSaturation(x, y));
                    lightnessSolver.addData(getLightness(x, y));

                }
            }
        }


        lightnessMean = (int) lightnessSolver.getMean();
        lightnessStdDeviation = (int) lightnessSolver.getStandardDeviation();

        hueMean = (int) hueSolver.getMean();      
        hueStdDeviation = (int) hueSolver.getStandardDeviation();

        satMean = (int) saturationSolver.getMean();
        satStdDeviation = (int) saturationSolver.getStandardDeviation();
        
    }
    
    //POEY
    private void calculateImageStatisticsRGB() {

        FastStatistics normalisedRedSolver = new FastStatistics();
        FastStatistics normalisedGreenSolver = new FastStatistics();
        FastStatistics normalisedBlueSolver = new FastStatistics();

        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	            normalisedRedSolver.addData((float)getNormalisedRed(x, y));
				normalisedGreenSolver.addData((float)getNormalisedGreen(x, y));
				normalisedBlueSolver.addData((float)getNormalisedBlue(x, y));
	        }
        }

	    normalisedRedMean = (double) normalisedRedSolver.getMean();
	    normalisedRedStdDeviation = (double) normalisedRedSolver.getStandardDeviation();
	    normalisedGreenMean = (double) normalisedGreenSolver.getMean();
	    normalisedGreenStdDeviation = (double) normalisedGreenSolver.getStandardDeviation();
	    normalisedBlueMean = (double) normalisedBlueSolver.getMean();
	    normalisedBlueStdDeviation = (double) normalisedBlueSolver.getStandardDeviation();

    }
    
    //POEY
    private void calculateImageStatisticsC1C2C3(int i) {
        FastStatistics C1C2C3Solver = new FastStatistics();

        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	        	C1C2C3Solver.addData((float)getC1C2C3(x,y,i));
	        }
        }

        C1C2C3Mean = (double) C1C2C3Solver.getMean();
        C1C2C3StdDeviation = (double) C1C2C3Solver.getStandardDeviation();
    }
    
    //POEY
    private void calculateImageStatisticsL1L2L3(int i) {
        FastStatistics L1L2L3Solver = new FastStatistics();
        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	        	L1L2L3Solver.addData((float)getL1L2L3(x,y,i));
	        }
        }
        L1L2L3Mean = (double) L1L2L3Solver.getMean();
        L1L2L3StdDeviation = (double) L1L2L3Solver.getStandardDeviation();
    }
    
//    //POEY
//    private void calculateImageStatisticsGrey() {
//        FastStatistics greyValueSolver = new FastStatistics();
//        for (int y = 0; y < height; y += 1) {
//	        for (int x = 0; x < width; x += 1) {
//	        	greyValueSolver.addData((float)getGreyValue(x,y));
//	        }
//        }
//        greyValueMean = (double) greyValueSolver.getMean();
//        greyValueStdDeviation = (double) greyValueSolver.getStandardDeviation();
//    }
    
    //POEY
    private void calculateImageStatisticsMean() {
        FastStatistics imageMeanSolver = new FastStatistics();
        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	        	imageMeanSolver.addData((float)get3x3Mean(x,y));
	        }
        }
        imageMean = (double) imageMeanSolver.getMean();
        imageStdDeviation = (double) imageMeanSolver.getStandardDeviation();
    }
    
    //POEY
    private void calculateImageStatisticsRange() {
        FastStatistics imageRangeSolver = new FastStatistics();
        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	        	imageRangeSolver.addData((float)get3x3Range(x,y));
	        }
        }
        imageRange = (double) imageRangeSolver.getMean();
        imageRangeStdDeviation = (double) imageRangeSolver.getStandardDeviation();
    }
    
    //POEY
    private void calculateImageStatisticsVariance() {
        FastStatistics imageVarianceSolver = new FastStatistics();
        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	        	imageVarianceSolver.addData((float)get3x3Variance(x,y));
	        }
        }
        imageVariance = (double) imageVarianceSolver.getMean();
        imageVarianceStdDeviation = (double) imageVarianceSolver.getStandardDeviation();
    }
    
    //POEY
    private void calculateImageStatisticsBinary() {
    	FastStatisticsGrey imageBinarySolver = new FastStatisticsGrey();
        for (int y = 0; y < height; y += 1) {
	        for (int x = 0; x < width; x += 1) {
	        	imageBinarySolver.addData(getAdaptiveBinaryThreshold(x,y));
	        }
        }
        imageBinary = imageBinarySolver.getMaxNIndex();
    }

    public PixelLoader getSubImage(ImageWindow area) {
        PixelLoader newImage = new PixelLoader();
        newImage.file = this.file;
        newImage.img = this.img.getSubimage(area.left, area.top, area.width, area.height);
        newImage.width = area.width;
        newImage.height = area.height;
        return newImage;
    }

    /**
     * Gets the buffered image which the PixelLoader is wrapping.
     */
    public BufferedImage getBufferedImage() {
        return img;
    }

    /**
     * Returns statistics about a particular channel.
     */
    public FastStatistics getStatistics(int channel) {

        FastStatistics solver = new FastStatistics();

        if (CACHE_RGB_HSL) {
            if (greyColourCache == null) createRGBCache();

            int numPixels = width * height;

            switch (channel) {
                case ColourChannels.GREYSCALE:
                    for (int c = 0; c < numPixels; c++) {
                        solver.addData(greyColourCache[c]);
                    }
                    break;
                case ColourChannels.RED:
                    for (int c = 0; c < numPixels; c++) {
                        solver.addData(redColourCache[c]);
                    }
                    break;
                case ColourChannels.GREEN:
                    for (int c = 0; c < numPixels; c++) {
                        solver.addData(greenColourCache[c]);
                    }
                    break;
                case ColourChannels.BLUE:
                    for (int c = 0; c < numPixels; c++) {
                        solver.addData(blueColourCache[c]);
                    }
                    break;
            }
        } else {
            for (int y = 0; y < height; y += 1) {
                for (int x = 0; x < width; x += 1) {
                    switch (channel) {
                        case ColourChannels.GREYSCALE:
                            solver.addData(getGreyValue(x, y));
                            break;
                        case ColourChannels.RED:
                            solver.addData(getRed(x, y));
                            break;
                        case ColourChannels.GREEN:
                            solver.addData(getGreen(x, y));
                            break;
                        case ColourChannels.BLUE:
                            solver.addData(getBlue(x, y));
                            break;
                    }
                }
            }
        }

        return solver;

    }


    private int rMax, rMin, gMax, gMin, bMax, bMin;

    private int[] greyColourCache;
    private int[] redColourCache;
    private int[] greenColourCache;
    private int[] blueColourCache;
    private float[][] c1c2c3Cache;
    private float[][] normalisedRGBCache;
    private float[][] l1l2l3Cache;

    /**
     * Gets the colour at the point X, Y, as a java.awt.Color object
     */
    public Color getColor(int x, int y) {
        return new Color(img.getRGB(x, y));
    }

    /**
     * Gets the raw RGB color at the point X, Y.
     */
    public int getRGB(int x, int y) {
        return img.getRGB(x, y);
    }

    public void setRGB(int x, int y, int rgb) {
        img.setRGB(x, y, rgb);
        hueCache = null;
        greyColourCache = null;
    }

    /**
     * Gets the grayscale value of the pixel
     */
    public int getGreyValue(int x, int y) throws RuntimeException {
        // this is called many times for each pixel. Hence we'll do the maths once only.
        if (CACHE_RGB_HSL) {
            if (greyColourCache == null) createRGBCache();
            int c = (y * width) + x;
            return greyColourCache[c];
        } else {
            // get a colour object, which saves us having to shift bits and other stuff.
            int rgb = img.getRGB(x, y);

            // extract the colours
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            return (int) ((red * rx) + (green * gx) + (blue * bx));
        }
    }


    public int getRed(int x, int y) throws RuntimeException {
        if (CACHE_RGB_HSL) {
            // this is called many times for each pixel. Hence we'll do the maths once only.
            if (redColourCache == null) createRGBCache();
            int c = (y * width) + x;
            return redColourCache[c];
        } else {
            // get a colour object, which saves us having to shift bits and other stuff.
            int rgb = img.getRGB(x, y);
            // extract the colours
            return (rgb >> 16) & 0xFF;
        }
    }

    public int getGreen(int x, int y) throws RuntimeException {
        // this is called many times for each pixel. Hence we'll do the maths once only.
        if (CACHE_RGB_HSL) {
            if (greenColourCache == null) createRGBCache();
            int c = (y * width) + x;
            return greenColourCache[c];
        } else {
            // get a colour object, which saves us having to shift bits and other stuff.
            int rgb = img.getRGB(x, y);
            // extract the colours
            return (rgb >> 8) & 0xFF;
        }
    }


    public int getBlue(int x, int y) throws RuntimeException {
        // this is called many times for each pixel. Hence we'll do the maths once only.
        if (CACHE_RGB_HSL) {
            if (blueColourCache == null) createRGBCache();
            int c = (y * width) + x;
            return blueColourCache[c];
        } else {
            int rgb = img.getRGB(x, y);
            return rgb & 0xFF;
        }
    }

    public static final int red = 0;
    public static final int green = 1;
    public static final int blue = 2;

    public double getNormalisedRed(int x, int y) {
    	//POEY comment: for segmentation CACHING_OTHERS and CACHE_RGB_HSL are false
        if (CACHING_OTHERS) {   
            if (normalisedRGBCache == null) createRGBCache();
            int c = (y * width) + x;
            return normalisedRGBCache[c][red];
        } else { 
            int red, green, blue;

            if (CACHE_RGB_HSL) {   
                if (redColourCache == null) createRGBCache();
                int c = (y * width) + x;
                red = redColourCache[c];
                green = greenColourCache[c];
                blue = blueColourCache[c];
            } else {
                int rgb = img.getRGB(x, y);
                // extract the colours
                red = (rgb >> 16) & 0xFF;
                green = (rgb >> 8) & 0xFF;
                blue = rgb & 0xFF;
            }

            double sum = red + green + blue;
            return red / sum;

        }
    }

    public double getNormalisedGreen(int x, int y) {
        if (CACHING_OTHERS) {
            if (normalisedRGBCache == null) createRGBCache();
            int c = (y * width) + x;
            return normalisedRGBCache[c][green];
        } else {

            int red, green, blue;

            if (CACHE_RGB_HSL) {
                if (redColourCache == null) createRGBCache();
                int c = (y * width) + x;
                red = redColourCache[c];
                green = greenColourCache[c];
                blue = blueColourCache[c];
            } else {
                int rgb = img.getRGB(x, y);
                // extract the colours
                red = (rgb >> 16) & 0xFF;
                green = (rgb >> 8) & 0xFF;
                blue = rgb & 0xFF;
            }

            double sum = red + green + blue;
            return green / sum;
        }

    }

    public double getNormalisedBlue(int x, int y) {
        if (CACHING_OTHERS) {
            if (normalisedRGBCache == null) createRGBCache();
            int c = (y * width) + x;
            return normalisedRGBCache[c][blue];
        } else {
            int red, green, blue;

            if (CACHE_RGB_HSL) {
                if (redColourCache == null) createRGBCache();
                int c = (y * width) + x;
                red = redColourCache[c];
                green = greenColourCache[c];
                blue = blueColourCache[c];
            } else {
                int rgb = img.getRGB(x, y);
                // extract the colours
                red = (rgb >> 16) & 0xFF;
                green = (rgb >> 8) & 0xFF;
                blue = rgb & 0xFF;
            }

            double sum = red + green + blue;
            return blue / sum;

        }
    }

    /**
     * Gevers and Smeulders, 1999
     * Returns illumination and highlight invariant colour values
     * i should be : 0 | 1 | 2
     */
    public double getC1C2C3(int x, int y, int i) {
        if (CACHING_OTHERS) {
            if (c1c2c3Cache == null) createRGBCache();
            int c = (y * width) + x;
            return c1c2c3Cache[c][i];
        } else {
            // get a colour objPect, which saves us having to shift bits and other stuff.
            int red, green, blue;

            if (CACHE_RGB_HSL) {
                if (redColourCache == null) createRGBCache();
                int c = (y * width) + x;
                red = redColourCache[c];
                green = greenColourCache[c];
                blue = blueColourCache[c];
            } else {
                int rgb = img.getRGB(x, y);
                // extract the colours
                red = (rgb >> 16) & 0xFF;
                green = (rgb >> 8) & 0xFF;
                blue = rgb & 0xFF;
            }

            switch (i) {
                case 0:
                    int maxGB = Math.max(green, blue);
                    return Math.atan(maxGB != 0 ? red / maxGB : 0);
                case 1:
                    int maxRB = Math.max(red, blue);
                    return Math.atan(maxRB != 0 ? green / maxRB : 0);
                default:
                    int maxRG = Math.max(red, green);
                    return Math.atan(maxRG != 0 ? blue / maxRG : 0);
            }
        }
    }

    /**
     * Gevers and Smeulders, 1999
     * Returns illumination and highlight invariant colour values
     * i should be : 0 | 1 | 2
     */
    public double getL1L2L3(int x, int y, int i) {
        if (CACHING_OTHERS) {
            if (l1l2l3Cache == null) createRGBCache();
            int c = (y * width) + x;
            return l1l2l3Cache[c][i];
        } else {

            int red, green, blue;

            if (CACHE_RGB_HSL) {
                if (redColourCache == null) createRGBCache();
                int c = (y * width) + x;
                red = redColourCache[c];
                green = greenColourCache[c];
                blue = blueColourCache[c];
            } else {
                int rgb = img.getRGB(x, y);
                // extract the colours
                red = (rgb >> 16) & 0xFF;
                green = (rgb >> 8) & 0xFF;
                blue = rgb & 0xFF;
            }

            double rMinusGSquared = (red - green) ^ 2;
            double rMinusBSquared = (red - blue) ^ 2;
            double gMinusBSquared = (green - blue) ^ 2;

            double M = rMinusGSquared + rMinusBSquared + gMinusBSquared;

            //POEY
            if(M==0)
            	M = 0.1;
            
            switch (i) {
                case 0:
                    return rMinusGSquared / M;
                case 1:
                    return rMinusBSquared / M;
                default:
                    return gMinusBSquared / M;
            }
        }
    }

    public void normalise() {

        if (CACHE_RGB_HSL && redColourCache == null) {
            createRGBCache();
            if (hueCache == null) {
                createHSLCache();
            }
        }

        double rRange = rMax - rMin;
        double gRange = gMax - gMin;
        double bRange = bMax - bMin;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                double red = ((getRed(x, y) - rMin) / (rRange)) * 255.0;
                double green = ((getGreen(x, y) - gMin) / (gRange)) * 255.0;
                double blue = ((getBlue(x, y) - bMin) / (bRange)) * 255.0;
                img.setRGB(x, y, new Color((int) red, (int) green, (int) blue).getRGB());
            }
        }

        clearCaches();

    }


    public int getAdaptiveBinaryThreshold(int x, int y) {

        int minRange = 255 / 5;

        FastStatistics f = get3x3Stats(x, y, ColourChannels.GREYSCALE, 2);
        int minimum = (int) f.getMin();
        int maximum = (int) f.getMax();
        int range = maximum - minimum;

        int T = 0;
        if (range > minRange) {
            // print is visible in the neighbourhood
            // set the threshold to be the average
            T = (minimum + maximum) / 2;
        } else {
            // seems to be mainly white
            // set the threshold higher
            T = maximum - (minRange / 2);
        }

        if (getGreyValue(x, y) > T) {
            return 255;
        } else {
            return 0;
        }

    }

    final double rx = 0.299;
    final double gx = 0.587;
    final double bx = 0.114;


    public void clearCaches() {
        greyColourCache = null;
        redColourCache = null;
        greenColourCache = null;
        blueColourCache = null;
        c1c2c3Cache = null;
        l1l2l3Cache = null;
        meanCache = null;
        varianceCache = null;
        hueCache = null;
        satCache = null;
        lightnessCache = null;
        normalisedRGBCache = null;
        vsobelCache = null;
        hsobelCache = null;
        laplacianCache = null;
    }
    /**
     * Optimised.
     * Before: 140ms on Lenna.png; after: 82 ms.
     */
    private void createRGBCache() {

        rMax = 0;
        gMax = 0;
        bMax = 0;
        rMin = 255;
        gMin = 255;
        bMin = 255;


        int numPixels = height * width;

        greyColourCache = new int[numPixels];//img.getHeight()][img.getWidth()];
        redColourCache = new int[numPixels];//new int[img.getHeight()][img.getWidth()];
        greenColourCache = new int[numPixels];//new int[img.getHeight()][img.getWidth()];
        blueColourCache = new int[numPixels];//new int[img.getHeight()][img.getWidth()];
        if (CACHING_OTHERS) {
            c1c2c3Cache = new float[numPixels][3];
            l1l2l3Cache = new float[numPixels][3];
            normalisedRGBCache = new float[numPixels][3];
        }

        int c = 0;

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                // get a colour object, which saves us having to shift bits and other stuff.
                int rgb = img.getRGB(x, y);

                // extract the colours
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                if (red > rMax) {
                    rMax = red;
                }
                if (red < rMin) {
                    rMin = red;
                }
                if (green > gMax) {
                    gMax = green;
                }
                if (green < gMin) {
                    gMin = green;
                }
                if (blue > bMax) {
                    bMax = blue;
                }
                if (blue < bMin) {
                    bMin = blue;
                }

                greyColourCache[c] = (int) ((red * rx) + (green * gx) + (blue * bx));
                redColourCache[c] = red;
                greenColourCache[c] = green;
                blueColourCache[c] = blue;

                if (CACHING_OTHERS) {
                    // avoid divide by zero
                    red++;
                    green++;
                    blue++;

                    int maxGB = Math.max(green, blue);
                    int maxRB = Math.max(red, blue);
                    int maxRG = Math.max(red, green);

                    c1c2c3Cache[c][0] = (float) Math.atan(maxGB != 0 ? red / maxGB : 0);
                    c1c2c3Cache[c][1] = (float) Math.atan(maxRB != 0 ? green / maxRB : 0);
                    c1c2c3Cache[c][2] = (float) Math.atan(maxRG != 0 ? blue / maxRG : 0);

                    float sum = red + green + blue;

                    normalisedRGBCache[c][0] = red / sum;
                    normalisedRGBCache[c][1] = green / sum;
                    normalisedRGBCache[c][2] = blue / sum;

                    float rMinusGSquared = (red - green) ^ 2;
                    float rMinusBSquared = (red - blue) ^ 2;
                    float gMinusBSquared = (green - blue) ^ 2;

                    float M = rMinusGSquared + rMinusBSquared + gMinusBSquared;
                    l1l2l3Cache[c][0] = rMinusGSquared / M;
                    l1l2l3Cache[c][1] = rMinusBSquared / M;
                    l1l2l3Cache[c][2] = gMinusBSquared / M;
                }

                c++;

            }

        }
    }

    private int[] hueCache;
    private int[] satCache;
    private int[] lightnessCache;   

    public int getHue(int x, int y) {
        if (CACHE_RGB_HSL) {
            if (hueCache == null) createHSLCache();
            int c = (y * width) + x;
            return hueCache[c];
        } else {
        	//POEY comment: for segmentation and classification, CACHE_RGB_HSL = false
        	return ColourConvertor.RGB2HSL(img.getRGB(x, y))[0];
        }
    }

    public int getSaturation(int x, int y) {
        if (CACHE_RGB_HSL) {
            if (satCache == null) createHSLCache();
            int c = (y * width) + x;
            return satCache[c];
        } else {
            return ColourConvertor.RGB2HSL(img.getRGB(x, y))[1];
        }
    }

    public int getLightness(int x, int y) {
        if (CACHE_RGB_HSL) {
            if (lightnessCache == null) createHSLCache();
            int c = (y * width) + x;
            return lightnessCache[c];
        } else {
            return ColourConvertor.RGB2HSL(img.getRGB(x, y))[2];
        }
    }

    /**
     * Optimised.
     * Previous: 168ms; After: 141ms
     */
    private void createHSLCache() {

        int numPixels = img.getHeight() * width;

        hueCache = new int[numPixels];
        satCache = new int[numPixels];
        lightnessCache = new int[numPixels];

        int c = 0;

        for (int yPos = 0; yPos < img.getHeight(); yPos++) {
            for (int xPos = 0; xPos < img.getWidth(); xPos++) {

                int[] hsl = ColourConvertor.RGB2HSL(img.getRGB(xPos, yPos));

                hueCache[c] = hsl[0];
                satCache[c] = hsl[1];
                lightnessCache[c] = hsl[2];

                c++;

            }

        }

    }

    private static ConvolutionMatrix verticalSobel;
    private static ConvolutionMatrix horizontalSobel;
    private static ConvolutionMatrix laplacian;

    private int[][] vsobelCache;
    private int[][] hsobelCache;
    private int[][] laplacianCache;

    public int getVerticalSobel(int x, int y) {
        if (verticalSobel == null) verticalSobel = new ConvolutionMatrix(ConvolutionMatrix.VERTICAL_SOBEL);

        if (CACHING_OTHERS) {
            if (vsobelCache == null) {
                vsobelCache = new int[getWidth()][getHeight()];
                for (int dY = 0; dY < getHeight(); dY++)
                    for (int dX = 0; dX < getWidth(); dX++) {
                        vsobelCache[dX][dY] = -1;
                    }
            }
            if (vsobelCache[x][y] == -1) {
                vsobelCache[x][y] = getConvolved(x, y, verticalSobel);
            }
            return vsobelCache[x][y];
        } else {
            return getConvolved(x, y, verticalSobel);
        }
    }

    public int getEdgeMagnitude(int x, int y) {
        return Math.abs(getHorizontalSobel(x, y)) + Math.abs(getVerticalSobel(x, y));
    }

    public int getHorizontalSobel(int x, int y) {
        if (horizontalSobel == null) horizontalSobel = new ConvolutionMatrix(ConvolutionMatrix.HORIZONTAL_SOBEL);
        if (CACHING_OTHERS) {
            if (hsobelCache == null) {
                hsobelCache = new int[getWidth()][getHeight()];
                for (int dY = 0; dY < getHeight(); dY++)
                    for (int dX = 0; dX < getWidth(); dX++) {
                        hsobelCache[dX][dY] = -1;
                    }

            }
            if (hsobelCache[x][y] == -1) {
                hsobelCache[x][y] = getConvolved(x, y, horizontalSobel);
            }
            return hsobelCache[x][y];
        } else {
            return getConvolved(x, y, horizontalSobel);
        }
    }

    public int getLaplacian(int x, int y) {
        if (laplacian == null) laplacian = new ConvolutionMatrix(ConvolutionMatrix.LAPLACIAN);
        if (CACHING_OTHERS) {
            if (laplacianCache == null) {
                laplacianCache = new int[getWidth()][getHeight()];
                for (int dY = 0; dY < getHeight(); dY++)
                    for (int dX = 0; dX < getWidth(); dX++) {
                        laplacianCache[dX][dY] = -1;
                    }

            }
            if (laplacianCache[x][y] == -1) {
                laplacianCache[x][y] = getConvolved(x, y, laplacian);
            }
            return laplacianCache[x][y];
        } else {
            return getConvolved(x, y, laplacian);
        }
    }

    public float[][] varianceCache;

//    public static void main(String[] args) throws Exception {
//        PixelLoader image = new PixelLoader("/home/ooechs/Desktop/Lenna.png");
//        FastStatistics s = new FastStatistics();
//        for (int i = 0; i <= 20; i++) {
//            long start = System.currentTimeMillis();
//            image.get3x3Mean(10, 10);
//            image.meanCache = null;
//            long end = System.currentTimeMillis();
//            long time = end - start;
//            if (i > 0) s.addData(time);
//            System.out.println(time);
//        }
//        System.out.println(s.getMean() + " | " + s.getStandardDeviation());
//    }

    /**
     * Gets the variance in grey value about a 3x3 window, centering on
     * a specific point at (x, y). Pixels about the edge are ignored. Results cached for performance.
     * Optimised. Was 248ms, now 87ms.
     */
    public float get3x3Variance(int x, int y) {
        if (CACHING_OTHERS) {
            if (varianceCache == null) {

                FastStatistics solver = new FastStatistics();

                varianceCache = new float[getHeight()][getWidth()];
                for (int ny = size; ny < getHeight() - size; ny++)
                    for (int nx = size; nx < getWidth() - size; nx++) {

                        solver.reset();

                        for (int dy = -size; dy <= size; dy++) {
                            int newY = ny + dy;
                            for (int dx = -size; dx <= size; dx++) {
                                solver.addData(getGreyValue(nx + dx, newY));
                            }
                        }

                        varianceCache[ny][nx] = solver.getVariance();

                    }
            }
            return varianceCache[y][x];
        } else {
            if (x <= size || y <= size || x >= (width - size) || y >= (height - size)) return 0;
            FastStatistics solver = new FastStatistics();
            for (int dy = -size; dy <= size; dy++) {
                int newY = y + dy;
                for (int dx = -size; dx <= size; dx++) {
                    solver.addData(getGreyValue(x + dx, newY));
                }
            }
            return solver.getVariance();
        }
    }

    /**
     * The size of the nxn caches.
     * Size 2 creates 5x5 ranges.
     */
    public static int size = 1;

    private int[] rangeCache;

    /**
     * Optimised.
     * Took 206 ms to run on Lenna.png, now takes 43ms.
     */
    public int get3x3Range(int x, int y) {
        if (CACHING_OTHERS) {      	
            if (rangeCache == null) {

                int pixelCount = width * height;
                rangeCache = new int[pixelCount];

                if (greyColourCache == null) createRGBCache();

                // precalculate the number of elements to be averaged
                int windowSize = (size * 2) + 1;

                for (int ny = size; ny < getHeight() - size; ny++) {
                    for (int nx = size; nx < getWidth() - size; nx++) {

                        // calculate the mean without using the statistics solver
                        int high = 0;
                        int low = 256;

                        // start in top left corner of the window
                        int C = ((ny - size) * width) + (nx - size);

                        for (int dy = 0; dy < windowSize; dy++) {
                            int c = C;
                            for (int dx = 0; dx < windowSize; dx++) {
                                int value = greyColourCache[c];
                                if (value > high) high = value;
                                if (value < low) low = value;
                                c++;
                            }
                            C += width;
                        }

                        int p = (ny * width) + nx;
                        rangeCache[p] = high - low;
                    }
                }
            }
            int c = (y * width) + x;
            return rangeCache[c];
        } else {
            if (x <= size || y <= size || x >= (width - size) || y >= (height - size)) return 0;
            FastStatistics solver = new FastStatistics();
            for (int dy = -size; dy <= size; dy++) {
                int newY = y + dy;
                for (int dx = -size; dx <= size; dx++) {               	
                    solver.addData(getGreyValue(x + dx, newY));
                }
            }
            return (int) solver.getRange();
        }
    }

    private float[] meanCache;


    public float get3x3TruncatedMedian(int x, int y, int steps) {

        int min = 255;
        int max = 0;
        int arraySize = ((size * 2) + 1);
        arraySize *= arraySize;
        int[] values = new int[arraySize];
        double mean = 0;

        // find minimum, median and maximum intensity hist
        int c = 0;
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                if (dx == 0 && dy == 0) continue;
                int g = getGreyValue(x + dx, y + dy);
                values[c] = g;
                mean += g;
                if (g > max) max = g;
                if (g < min) min = g;
                c++;
            }
        }

        int median = Median.find(values, 0, c - 1);
        mean /= c;

        max = 255 - 32;
        min = 32;

        for (int i = 0; i < steps; i++) {

            // deduce HOW MUCH the local intensity distribution should be truncated by
            // lets assume that it will be a quartile
            int t = (max - min) / 4;

            // decide from which end local intensity distribution should be truncated
            // since the order is usually mean-median-mode
            if (mean < median) {
                // truncate from the left side
                min = median - t;
            } else {
                // truncate from the right side
                max = median + t;
            }

            min = median - t;
            max = median + t;

            // reset mean and counter
            mean = 0;
            c = 0;

            for (int dy = -size; dy <= size; dy++) {
                for (int dx = -size; dx <= size; dx++) {
                    if (dx == 0 && dy == 0) continue;
                    int g = getGreyValue(x + dx, y + dy);
                    if (g >= min && g <= max) {
                        values[c] = g;
                        mean += g;
                        c++;
                    }

                }
            }

            median = Median.find(values, 0, c - 1);
            mean /= c;

        }

        // calculate the median of the truncated distribution
        return median;

    }

    /**
     * Optimised.
     * Was 246ms to run on Lenna.png, now is 57ms.
     */
    public float get3x3Gaussian(int x, int y) {

        int total = 0;

        total += getGreyValue(x - 1, y - 1);
        total += getGreyValue(x, y - 1) * 2;
        total += getGreyValue(x + 1, y - 1);

        total += getGreyValue(x - 1, y) * 2;
        total += getGreyValue(x, y) * 4;
        total += getGreyValue(x + 1, y) * 2;

        total += getGreyValue(x - 1, y + 1);
        total += getGreyValue(x, y + 1) * 2;
        total += getGreyValue(x + 1, y + 1);

        return total / 16;

    }

    public int get3x3Median(int x, int y) {
        int arraySize = ((size * 2) + 1);
        arraySize *= arraySize;
        int[] values = new int[arraySize];

        int c = 0;
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                if (dx == 0 && dy == 0) continue;
                int g = getGreyValue(x + dx, y + dy);
                values[c] = g;
                c++;
            }
        }

        return Median.find(values, 0, values.length - 1);
    }


    /**
     * Optimised.
     * Was 246ms to run on Lenna.png, now is 57ms.
     */
    public float get3x3Mean(int x, int y) {

        if (CACHING_OTHERS) {       	
            if (meanCache == null) {

                if (greyColourCache == null) createRGBCache();

                // precalculate the number of elements to be averaged
                int windowSize = (size * 2) + 1;
                final int count = windowSize * windowSize;

                int pixelCount = width * height;

                meanCache = new float[pixelCount];


                for (int ny = size; ny < getHeight() - size; ny++) {
                    for (int nx = size; nx < getWidth() - size; nx++) {

                        // calculate the mean without using the statistics solver
                        float total = 0;

                        // start in top left corner of the window
                        int C = ((ny - size) * width) + (nx - size);

                        for (int dy = 0; dy < windowSize; dy++) {
                            int c = C;
                            for (int dx = 0; dx < windowSize; dx++) {
                                total += greyColourCache[c];
                                c++;
                            }
                            C += width;
                        }

                        int p = (ny * width) + nx;
                        meanCache[p] = total / count;

                    }
                }
            }

            int c = (y * width) + x;
            return meanCache[c];
        } else {
            if (x <= size || y <= size || x >= (width - size) || y >= (height - size)) return 0;
            FastStatistics solver = new FastStatistics();
            for (int dy = -size; dy <= size; dy++) {
                int newY = y + dy;
                for (int dx = -size; dx <= size; dx++) {
                    solver.addData(getGreyValue(x + dx, newY));
                }
            }
            return solver.getMean();
        }

    }

    public static final int HARALICK_CONTRAST = 1;
    public static final int HARALICK_DISSIMILARITY = 2;
    public static final int HARALICK_UNIFORMITY = 3;
    public static final int HARALICK_MAXIMUM_PROBABILITY = 4;
    public static final int HARALICK_ENTROPY = 5;
    public static final int VARIANCE = 6;

    public BufferedImage getProcessedImage(final int haralickType) {

        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        double min = Double.MAX_VALUE;
        double max = 0;

        double[][] saved = new double[img.getWidth()][img.getHeight()];

        // run once to find min and max
        int border = HARALICK_SIZE + 1;
        for (int yPos = border; yPos < img.getHeight() - border; yPos++) {
            for (int xPos = border; xPos < img.getWidth() - border; xPos++) {


                double c = 0;

                switch (haralickType) {
                    case HARALICK_CONTRAST:
                        c = getContrast(xPos, yPos);
                        break;
                    case HARALICK_DISSIMILARITY:
                        c = getDissimilarity(xPos, yPos);
                        break;
                    case HARALICK_UNIFORMITY:
                        c = getUniformity(xPos, yPos);
                        break;
                    case HARALICK_ENTROPY:
                        c = getEntropy(xPos, yPos);
                        break;
                    case HARALICK_MAXIMUM_PROBABILITY:
                        c = getMaximumProbability(xPos, yPos);
                        break;
                    case VARIANCE:
                        c = get3x3Variance(xPos, yPos);
                }


                if (c < min) min = c;
                else if (c > max) max = c;

                saved[xPos][yPos] = c;

            }
        }

        // find the difference between min and max. This will be the scaling factor.
        double range = max - min;

        for (int yPos = border; yPos < img.getHeight() - border; yPos++) {
            for (int xPos = border; xPos < img.getWidth() - border; xPos++) {

                // histogram stretch to emphasize contrast
                int value = (int) (((saved[xPos][yPos] - min) / range) * 255);

                // drawPixels on the image
                image.setRGB(xPos, yPos, new Color(value, value, value).getRGB());

            }
        }

        return image;

    }

    public static final int HARALICK_SIZE = 8;

    /**
     * Gets the contrast from the gray level co-occurence matrix
     */
    public double getContrast(int x, int y) {

        double[][] glcm = getHorizontalGLCM(x, y, HARALICK_SIZE);

        double total = 0;

        for (int dy = 0; dy < GLCM_SIZE; dy++)
            for (int dx = 0; dx < GLCM_SIZE; dx++) {
                total += glcm[dx][dy] * ((dx - dy) ^ 2);
            }

        return total;

    }

    /**
     * Gets the dissimilarity. Similar to contrast but uses
     * a non exponential scale.
     */
    public double getDissimilarity(int x, int y) {

        double[][] glcm = getHorizontalGLCM(x, y, HARALICK_SIZE);

        double total = 0;

        for (int dy = 0; dy < GLCM_SIZE; dy++)
            for (int dx = 0; dx < GLCM_SIZE; dx++) {
                total += glcm[dx][dy] * (Math.abs(dx - dy));
            }

        return total;

    }

    /**
     * Gets the uniformity
     */
    public double getUniformity(int x, int y) {

        double[][] glcm = getHorizontalGLCM(x, y, HARALICK_SIZE);

        double total = 0;

        for (int dy = 0; dy < GLCM_SIZE; dy++)
            for (int dx = 0; dx < GLCM_SIZE; dx++) {
                total += glcm[dx][dy] * glcm[dx][dy];
            }

        return total;

    }

    /**
     * Gets the entropy
     */
    public double getEntropy(int x, int y) {

        double[][] glcm = getHorizontalGLCM(x, y, HARALICK_SIZE);

        double total = 0;

        for (int dy = 0; dy < GLCM_SIZE; dy++)
            for (int dx = 0; dx < GLCM_SIZE; dx++) {
                total += glcm[dx][dy] * (-Math.log1p(glcm[dx][dy]));
            }

        return total;

    }

    /**
     * Gets the maximum probability
     */
    public double getMaximumProbability(int x, int y) {

        double[][] glcm = getHorizontalGLCM(x, y, HARALICK_SIZE);

        double max = 0;

        for (int dy = 0; dy < GLCM_SIZE; dy++)
            for (int dx = 0; dx < GLCM_SIZE; dx++) {
                if (glcm[dx][dy] > max) {
                    max = glcm[dx][dy];
                }
            }

        return max;

    }

    final int GLCM_SIZE = 16;
    final int GLCM_QUANTISATION = 256 / GLCM_SIZE;

    public int quantize(int value) {
        if (value > 240) value = 240;
        value -= 210;
        if (value < 0) value = 0;
        return value / GLCM_QUANTISATION;
    }


    /**
     * Produces a Gray Level Co-occurence Matrix for a window
     * centered at (x,y) with a width and height of twice half size + 1
     * The GLCM is based on a 4 bit image quantization resulting in a
     * 16x16 array of 256 co-occurence values.
     */
    public double[][] getHorizontalGLCM(final int x, final int y, final int halfSize) {
        double[][] glcm = new double[GLCM_SIZE][GLCM_SIZE];
        //int hist = 0;
        for (int dy = -halfSize; dy <= halfSize; dy++) {
            for (int dx = -halfSize; dx <= halfSize; dx++) {
                int xPos = x + dx;
                int yPos = y + dy;
                // find the reference pixel
                int referenceValue = quantize(getGreyValue(xPos, yPos));
                // and its neighbour (to the immediate right)
                int neighbourValue = quantize(getGreyValue(xPos + 1, yPos));
                // increment the glcm
                glcm[referenceValue][neighbourValue]++;
                // increment the opposite side too (for symmetry)
                glcm[neighbourValue][referenceValue]++;
                //hist+= 2;
            }
        }

        // normalise hist
/*        for (int i = 0; i < GLCM_SIZE; i++) {
            for (int j = 0; j < GLCM_SIZE; j++) {
                glcm[i][j] /= hist;
            }
        }*/

        return glcm;

    }

    public FastStatistics get3x3Stats(int x, int y, int channel) {
        return get3x3Stats(x, y, channel, 1);
    }

    public FastStatistics get3x3Stats(int x, int y, int channel, int size) {

        FastStatistics solver = new FastStatistics();
        
        if (y - size < 0) return solver;
        if (y + size > (getHeight() - 1)) return solver;
        if (x - size < 0) return solver;
        if (x + size > (getWidth() - 1)) return solver;

        //POEY comment: for segmentation CACHING_OTHERS = false       
        if (CACHING_OTHERS) {        	
            switch (channel) {
                case ColourChannels.HUE:
                case ColourChannels.SATURATION:
                case ColourChannels.LIGHTNESS:
                    if (hueCache == null) createHSLCache();
                default:
                    if (redColourCache == null) createRGBCache();
                    break;
            }

            int C = ((y - size) * width) + (x - size);
            int windowSize = (size * 2) + 1;

            for (int dy = 0; dy < windowSize; dy++) {
                int c = C;
                for (int dx = 0; dx < windowSize; dx++) {
                    switch (channel) {
                        case ColourChannels.NORMALISED_RED:
                            solver.addData(normalisedRGBCache[c][0]);
                            break;
                        case ColourChannels.NORMALISED_GREEN:
                            solver.addData(normalisedRGBCache[c][1]);
                            break;
                        case ColourChannels.NORMALISED_BLUE:
                            solver.addData(normalisedRGBCache[c][2]);
                            break;
                        case ColourChannels.C1:
                            solver.addData(c1c2c3Cache[c][0]);
                            break;
                        case ColourChannels.C2:
                            solver.addData(c1c2c3Cache[c][1]);
                            break;
                        case ColourChannels.C3:
                            solver.addData(c1c2c3Cache[c][2]);
                            break;
                        case ColourChannels.L1:
                            solver.addData(l1l2l3Cache[c][0]);
                            break;
                        case ColourChannels.L2:
                            solver.addData(l1l2l3Cache[c][1]);
                            break;
                        case ColourChannels.L3:
                            solver.addData(l1l2l3Cache[c][2]);
                            break;
                        case ColourChannels.HUE:
                            solver.addData(hueCache[c]);
                            break;
                        case ColourChannels.SATURATION:
                            solver.addData(satCache[c]);
                            break;
                        case ColourChannels.LIGHTNESS:
                            solver.addData(lightnessCache[c]);
                            break;
                        case ColourChannels.GREYSCALE:
                            solver.addData(greyColourCache[c]);
                            break;
                        case ColourChannels.RED:
                            solver.addData((float) redColourCache[c]);
                            break;
                        case ColourChannels.GREEN:
                            solver.addData((float) greenColourCache[c]);
                            break;
                        case ColourChannels.BLUE:
                            solver.addData((float) blueColourCache[c]);
                            break;
                        case ColourChannels.EDGE:
                            try {
                            solver.addData((float) getEdgeMagnitude(x+dx, y+dy));
                            } catch (Exception e) {}
                            break;
                    }
                    c++;
                }
                C += width;
            }
        } else { 
            for (int dy = -size; dy <= size; dy++) {
                int newY = y + dy;
                for (int dx = -size; dx <= size; dx++) {
                	//POEY comment: for segmentation by AdaotuveBinaryThreshold, channel = GREYSCALE
                	//for segmentation by GenericN*NFeature, calculate 14 channels
                	//compare channel by name not index
                    switch (channel) {
                        case ColourChannels.NORMALISED_RED:	
                            solver.addData((float) getNormalisedRed(x + dx, newY));
                            break;
                        case ColourChannels.NORMALISED_GREEN:	
                            solver.addData((float) getNormalisedGreen(x + dx, newY));                            
                            break;
                        case ColourChannels.NORMALISED_BLUE:	
                            solver.addData((float) getNormalisedBlue(x + dx, newY));  
                            break;
                        case ColourChannels.C1:	
                            solver.addData((float) getC1C2C3(x + dx, newY, 0));   
                            break;
                        case ColourChannels.C2:	
                            solver.addData((float) getC1C2C3(x + dx, newY, 1));   
                            break;
                        case ColourChannels.C3:	
                            solver.addData((float) getC1C2C3(x + dx, newY, 2));   
                            break;
                        case ColourChannels.L1:	
                            solver.addData((float) getL1L2L3(x + dx, newY, 0));   
                            break;
                        case ColourChannels.L2:	
                            solver.addData((float) getL1L2L3(x + dx, newY, 1));   
                            break;
                        case ColourChannels.L3:	
                            solver.addData((float) getL1L2L3(x + dx, newY, 2));  
                            break;
                        case ColourChannels.HUE:	
                            solver.addData(getHue(x, y));   
                            break;
                        case ColourChannels.SATURATION:	
                            solver.addData(getSaturation(x + dx, newY));   
                            break;
                        case ColourChannels.LIGHTNESS:	
                            solver.addData(getLightness(x + dx, newY));   
                            break;
                        case ColourChannels.GREYSCALE:	            	
                            solver.addData(getGreyValue(x + dx, newY)); 
                            break;
                        case ColourChannels.RED:	
                            solver.addData(getRed(x + dx, newY));  
                            break;
                        case ColourChannels.GREEN:	
                            solver.addData(getGreen(x + dx, newY));   
                            break;
                        case ColourChannels.BLUE:	
                            solver.addData(getBlue(x + dx, newY));  
                            break;
                        case ColourChannels.EDGE:	
                            solver.addData((float) getEdgeMagnitude(x+dx, newY));  
                            break;
                    }
                }
            }
        }

/*        for (int dy = -size; dy <= size; dy++) {
    int newY = y + dy;
    for (int dx = -size; dx <= size; dx++) {
        int newX = x + dx;
        switch (channel) {
            case ColourChannels.RED:
                solver.addData((float) getRed(newX, newY));
                break;
            case ColourChannels.GREEN:
                solver.addData((float) getGreen(newX, newY));
                break;
            case ColourChannels.BLUE:
                solver.addData((float) getBlue(newX, newY));
                break;
            case ColourChannels.HUE:
                solver.addData(getHue(newX, newY));
                break;
            case ColourChannels.SATURATION:
                solver.addData(getSaturation(newX, newY));
                break;
            case ColourChannels.LIGHTNESS:
                solver.addData(getLightness(newX, newY));
                break;
            case ColourChannels.GREYSCALE:
                solver.addData(getGreyValue(newX, newY));
                break;
        }
    }
}*/
        return solver;
    }

    /**
     * Speed improvements, 19th Feb 2008
     * Now runs 39% faster.
     */
    public int getConvolved(int x, int y, ConvolutionMatrix matrix) {
        double sum = 0;
        int size = matrix.getWidth() / 2;

        if (y - size < 0) return 0;
        if (y + size > (getHeight() - 1)) return 0;
        if (x - size < 0) return 0;
        if (x + size > (getWidth() - 1)) return 0;

        for (int dy = -size; dy <= size; dy++) {
            int newY = y + dy;
            for (int dx = -size; dx <= size; dx++) {
                int newX = x + dx;
                sum += getGreyValue(newX, newY) * matrix.getWeight(dx + size, dy + size);
            }
        }

        return (int) (sum / matrix.getTotal());
    }

    public int[] getPrecomputedColours() {
        // Precache the colours
        int[] colours = new int[256];
        for (int i = 0; i <= 255; i++) {
            colours[i] = new Color(i, i, i).getRGB();
        }
        return colours;
    }

    /**
     * Speed improvements, 19th Feb 2008
     * Now runs 39% faster.
     */
    public BufferedImage getConvolved(ConvolutionMatrix matrix) {

        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        int[] colours = getPrecomputedColours();

        for (int yPos = 0; yPos < img.getHeight(); yPos++) {
            for (int xPos = 0; xPos < img.getWidth(); xPos++) {

                int c = getConvolved(xPos, yPos, matrix);
                if (c > 255) c = 255;
                if (c < 0) c = 0;
                if (c != 0) image.setRGB(xPos, yPos, colours[c]);

            }
        }

        return image;

    }

    public void saveAs(String filename) throws Exception {
        saveAs(new File(filename));
    }

    public void saveAs(File imageFile) throws Exception {
        try {
            String type = "bmp";
            if (imageFile.getName().endsWith(".png")) {
                type = "png";
            }
            if (imageFile.getName().endsWith(".jpg")) {
                type = "jpg";
            }
            
            //POEY
            if (imageFile.getName().endsWith(".tif")) {
                type = ".tif";
            }
            
            // Use the Java ImageIO library to shave the file as a PNG image
            javax.imageio.ImageIO.write(img, type, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
    }

    /**
     * Gets the file from which the image was loaded.
     */
    public File getFile() {
        return file;
    }

    public String getFilename() {
        if (file != null) {
            return file.getName();
        } else {
            return "no-name";
        }
    }

}

