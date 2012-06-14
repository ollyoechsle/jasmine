package jasmine.imaging.commons;


import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.ShapePixel;

import java.io.File;

/**
 *
 * <p>
 * Integral image allows you to make a precalculated image which can then be used
 * to very quickly establish the sum of pixels in any rectangular area.
 * </p>
 *
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
 * @author Olly Oechsle, University of Essex, Date: 20-Sep-2007
 * @version 1.1
 */
public class IntegralImage {

    protected String filename;
    protected PixelLoader image;
    protected int[][] sumIntegralImage;
    protected long[][] sumSquaredIntegralImage;
    protected int width, height;
    
    //POEY
    protected ExtraShapeData img;
    protected int[][] sumIntegralObject;
    protected long[][] sumSquaredIntegralObject;


//    public static void main(String[] args) throws Exception {
//        IntegralImage iiv = new IntegralImage(new File("/home/ooechs/Desktop/Screenshot-2.png"));
//        iiv.check(0,0,100,100);
//    }

    public IntegralImage(File image) throws Exception {
        this(new PixelLoader(image));
    }

    public IntegralImage(PixelLoader image) {
        this.filename = image.getFilename();
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        createIntegralImage();
    }
    
    //POEY
    public IntegralImage(PixelLoader image, ExtraShapeData img) {
        this.filename = image.getFilename();
        this.img = img;
        this.width = img.getWidth();
        this.height = img.getHeight();
        createIntegralObject();
    }

    public String getFilename() {
        return this.filename;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Allows access to the original PixelLoader object.
     */
    public PixelLoader getPixelLoader() {
        return image;
    }

    /**
     * Removes the pixel loader and saves some memory.
     */
    public void setPixelLoaderNull() {
        image = null;
    }

    private void createIntegralImage() {

        final int baseResolutionX = 1;
        final int baseResolutionY = 1;

        // Prepare an array to store the results in
        sumIntegralImage = new int[image.getWidth()  + 1][image.getHeight()  + 1];
        sumSquaredIntegralImage = new long[image.getWidth()  + 1][image.getHeight()  + 1];

        // The array is at a different position to the image
        // If the baseResolution is more than 1. A bit messy but
        // avoids unnecessary multiplications
        int arrX = 1;

        // Move through each column in the image
        for (int x = 0; x < image.getWidth(); x++) {

            int sumColumnTotal = 0;
            int sumSquaredColumnTotal = 0;
            int arrY = 1;

            // Move down the column
            for (int y = 0; y < image.getHeight(); y++) {

                // Find the sum of pixels in this column, to this particular Y value
                int g = image.getGreyValue(x, y);
                sumColumnTotal += g;
                sumSquaredColumnTotal += (g*g);
                                                                                
                // sum total = value of row to left + columnTotal
                sumIntegralImage[arrX][arrY] = (arrX == 0 ? 0 : sumIntegralImage[arrX - 1][arrY]) + sumColumnTotal;
                sumSquaredIntegralImage[arrX][arrY] = (arrX == 0 ? 0 : sumSquaredIntegralImage[arrX - 1][arrY]) + sumSquaredColumnTotal;

                // results Y index
                arrY++;

            }

            // results X index
            arrX++;

        }

    }
    
    //POEY
    private void createIntegralObject() {

        final int baseResolutionX = 1;
        final int baseResolutionY = 1;

        // Prepare an array to store the results in
        sumIntegralObject = new int[img.getWidth()  + 1][img.getHeight()  + 1];
        sumSquaredIntegralObject = new long[img.getWidth()  + 1][img.getHeight()  + 1];

        // The array is at a different position to the image
        // If the baseResolution is more than 1. A bit messy but
        // avoids unnecessary multiplications
        int arrX = 1;

        // Move through each column in the image
        for (int x = 0; x < img.getWidth(); x++) {

            int sumColumnTotal = 0;
            int sumSquaredColumnTotal = 0;
            int arrY = 1;

            // Move down the column
            for (int y = 0; y < img.getHeight(); y++) {

            	ShapePixel p = img.array[x][y];
                if (p != null) {
	                // Find the sum of pixels in this column, to this particular Y value
	                int g = image.getGreyValue(p.x, p.y);               	
                	sumColumnTotal += g;
	                sumSquaredColumnTotal += (g*g);
	                                                                                
	                // sum total = value of row to left + columnTotal
	                sumIntegralObject[arrX][arrY] = (arrX == 0 ? 0 : sumIntegralObject[arrX - 1][arrY]) + sumColumnTotal;
	                sumSquaredIntegralObject[arrX][arrY] = (arrX == 0 ? 0 : sumSquaredIntegralObject[arrX - 1][arrY]) + sumSquaredColumnTotal;
	
	                // results Y index
	                arrY++;
                }
            }

            // results X index
            arrX++;

        }

    }

    /**
     * Simple method to ensure that the integral image is producing the correct output.
     */
    protected void check(int x, int y, int width, int height) {

        FastStatistics stats = new FastStatistics();

        for (int dy = 0; dy < height; dy++)
        for (int dx = 0; dx < width; dx++) {
            stats.addData(image.getGreyValue(x + dx, y + dy));
        }

        double sum = stats.getTotal();

        double variance = stats.getVariance();

        if (getSum(x, y, x + width, y + height) == sum){
            System.out.println("SUM Check passed: " + sum);
        } else {
            System.out.println("SUM Check FAILED");
        }

        System.out.println("Mean: " + stats.getMean());

        double intVariance  = getVariance(x, y, x + width, y + height);

        if (intVariance  == variance) {
            System.out.println("Variance Check passed: " + variance);
        } else {
            System.out.println("Variance Check FAILED: " + intVariance + " != " + variance );
        }

    }

    /**
     * Gets the sum of intensities within a box bounded by two sets of coordinates - (x1,y1), (x2, y2)
     */
    public int getSum(int x1, int y1, int x2, int y2) {
        int rect1 = sumIntegralImage[x1][y1];
        int rect2 = sumIntegralImage[x2][y1];
        int rect3 = sumIntegralImage[x1][y2];
        int rect4 = sumIntegralImage[x2][y2];
        return rect4 - rect2 - rect3 + rect1;
    }

    public double getMean(int x1, int y1, int x2, int y2) {
        return getSum(x1,y1,x2,y2) / getArea(x1,y1,x2,y2);
    }

    public long getSquaredSum(int x1, int y1, int x2, int y2) {
        long rect1 = sumSquaredIntegralImage[x1][y1];
        long rect2 = sumSquaredIntegralImage[x2][y1];
        long rect3 = sumSquaredIntegralImage[x1][y2];
        long rect4 = sumSquaredIntegralImage[x2][y2];
        return rect4 - rect2 - rect3 + rect1;
    }

    public double getArea(int x1, int y1, int x2, int y2) {
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        return width * height;
    }

    public double getVariance(int x1, int y1, int x2, int y2) {

        // Once the integral image of pixel intensities and square of the pixel intensities,
        // local means and variances can be calculated very efficiently

        // variance is the average squared difference of a set of hist
        // from the expected value (mean)

        double area = getArea(x1, y1, x2, y2);

        // get the mean
        double mean = getSum(x1, y1, x2, y2) / area;

        double sumXSquared = getSquaredSum(x1, y1, x2, y2);

        return (sumXSquared / area) - (mean * mean);

    }

    public double getStdDeviation(int x1, int y1, int x2, int y2) {
        return Math.sqrt(getVariance(x1, y1, x2, y2));
    }

    protected HaarlikeFeatures haarFeatures;
    protected HaarlikeFeatures haarFeaturesVariance;

    public HaarlikeFeatures getHaarlikeFeatures() {
        if (haarFeatures == null) haarFeatures = new HaarlikeFeatures(this, HaarlikeFeatures.SUM);
        return haarFeatures;
    }

    public HaarlikeFeatures getHaarlikeFeaturesVariance() {
        if (haarFeaturesVariance == null) haarFeaturesVariance = new HaarlikeFeatures(this, HaarlikeFeatures.VARIANCE);
        return haarFeaturesVariance;
    }

}
