package jasmine.imaging.commons;


import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 16-Mar-2009
 * Time: 15:03:49
 * To change this template use File | Settings | File Templates.
 */
public class GreyWorldAssumptionTransform extends ImageTransformer {

        FastStatistics avr, avg, avb;

        public GreyWorldAssumptionTransform() {
            avr = new FastStatistics();
            avg = new FastStatistics();
            avb = new FastStatistics();
        }

        public void init(PixelLoader image, int x, int y) {
            avr.addData(image.getRed(x, y));
            avg.addData(image.getGreen(x, y));
            avb.addData(image.getBlue(x, y));
        }

        int avR, avG, avB;
        public void afterInit() {
            avR = (int) avr.getMean();
            avG = (int) avg.getMean();
            avB = (int) avb.getMean();
        }

        /**
         * Returns the RGB value of the new colour.
         */
        protected int transform(PixelLoader image, int x, int y) {
            int r = (int) (image.getRed(x, y) - (avR) + 128);
            if (r > 255) r = 255;
            if (r < 0) r = 0;
            int g = (int) (image.getGreen(x, y) - (avG) + 128);
            if (g > 255) g = 255;
            if (g < 0) g = 0;
            int b = (int) (image.getBlue(x, y) - (avB) + 128);
            if (b > 255) b = 255;
            if (b < 0) b = 0;
            return new Color(r, g, b).getRGB();
        }
    }
