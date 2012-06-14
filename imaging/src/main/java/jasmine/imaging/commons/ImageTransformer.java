package jasmine.imaging.commons;


import java.awt.image.BufferedImage;

/**
 * Takes one image, transforms it in some way, and then returns the
 * transformed copy
 *
 * @author Olly Oechsle, University of Essex, Date: 09-Oct-2008
 * @version 1.0
 */
public abstract class ImageTransformer {

    public PixelLoader transform(PixelLoader image) {

        init(image);

        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                newImage.setRGB(x, y, transform(image, x, y));
            }
        }

        return new PixelLoader(newImage, null);

    }

    
    public void init(PixelLoader image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                init(image, x, y);
            }
        }
        afterInit();
    }

    public void init(PixelLoader image, int x, int y) {

    }

    public void afterInit() {
        
    }

    /**
     * Returns the RGB value of the new colour.
     */
    protected abstract int transform(PixelLoader image, int x, int y);

}
