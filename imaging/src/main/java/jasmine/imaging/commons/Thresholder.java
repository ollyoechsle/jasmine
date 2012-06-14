package jasmine.imaging.commons;


import java.awt.image.BufferedImage;
import java.awt.*;


/**
 * Base class for thresholders
 */
public abstract class Thresholder {

    protected int[] hist;
    protected int N;

    public Thresholder(int max) {
        this.hist = new int[max + 1];
    }

    public void addData(int value) {
    	//POEY comment: value is a slot number
        this.hist[value]++;
        N++;
    }

    public BufferedImage threshold(PixelLoader image, int threshold) {

        int white = Color.WHITE.getRGB();
        int black = Color.BLACK.getRGB();

        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int value = image.getGreyValue(x, y);
                int rgb = value < threshold? black : white;
                img.setRGB(x, y, rgb);
            }
        }

        return img;

    }

    /**
     * Returns the probability of a pixel in the image having a grey level i
     */
    public double getNormalised(int i) {
    	//POEY comment: hist[i] contains frequency of slot[i]
        return hist[i] / (double) N;
    }

    public abstract int getOptimalThreshold();

}
