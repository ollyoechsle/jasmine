package jasmine.imaging.core;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.StatisticsSolver;

import javax.swing.*;
import java.io.IOException;
import java.util.Vector;

/**
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
 * @author Olly Oechsle, University of Essex, Date: 03-Aug-2007
 * @version 1.0
 */
public abstract class JasmineCorrelationDialog extends JDialog {

    protected static String[] names = new String[]{"Red", "Green", "Blue", "Hue", "Saturation", "Lightness", "Grey", "VLine1Mean", "VLine1SD", "HLine1Mean", "HLine1SD", "Vline2Mean", "VLine2SD", "HLine2Mean", "HLine2SD", "Perim1Mean", "Perim1SD", "Perim2Mean", "Perim2SD", "Laplacian", "Variance", "Sobel", "Hline1Edges", "Vline1Edges"};

    public Jasmine j;

    protected StatisticsSolver expected;

    protected StatisticsSolver[] observed;

    public static float getValue(PixelLoader img, Pixel pixel, int featureIndex) {
        switch (featureIndex) {
            case 0:
                return (img.getRed(pixel.x, pixel.y));
            case 1:
                return (img.getGreen(pixel.x, pixel.y));
            case 2:
                return (img.getBlue(pixel.x, pixel.y));
            case 3:
                return (img.getHue(pixel.x, pixel.y));
            case 4:
                return (img.getSaturation(pixel.x, pixel.y));
            case 5:
                return (img.getLightness(pixel.x, pixel.y));
            case 6:
                return (img.getGreyValue(pixel.x, pixel.y));
            case 7:
                return (img.getVLine1().getStatistics(img, pixel.x, pixel.y).getMean());
            case 8:
                return (img.getVLine1().getStatistics(img, pixel.x, pixel.y).getStandardDeviation());
            case 9:
                return (img.getHLine1().getStatistics(img, pixel.x, pixel.y).getMean());
            case 10:
                return (img.getHLine1().getStatistics(img, pixel.x, pixel.y).getStandardDeviation());
            case 11:
                return (img.getVLine2().getStatistics(img, pixel.x, pixel.y).getMean());
            case 12:
                return (img.getVLine2().getStatistics(img, pixel.x, pixel.y).getStandardDeviation());
            case 13:
                return (img.getHLine2().getStatistics(img, pixel.x, pixel.y).getMean());
            case 14:
                return (img.getHLine2().getStatistics(img, pixel.x, pixel.y).getStandardDeviation());
            case 15:
                return (img.getPerimeter1().getStatistics(img, pixel.x, pixel.y).getMean());
            case 16:
                return (img.getPerimeter1().getStatistics(img, pixel.x, pixel.y).getStandardDeviation());
            case 17:
                return (img.getPerimeter2().getStatistics(img, pixel.x, pixel.y).getMean());
            case 18:
                return (img.getPerimeter2().getStatistics(img, pixel.x, pixel.y).getStandardDeviation());
            case 19:
                return img.getLaplacian(pixel.x, pixel.y);
            case 20:
                return img.get3x3Variance(pixel.x, pixel.y);
            case 21:
                return img.getHorizontalSobel(pixel.x, pixel.y);
            case 22:
                return (img.getHLine1().getEdgeCount(img, pixel.x, pixel.y));
            case 23:
                return (img.getVLine1().getEdgeCount(img, pixel.x, pixel.y));
        }
        return 0;
    }

    public JasmineCorrelationDialog(Jasmine j, int mode) {
        super(j);
        this.j = j;

        try {

            expected = new StatisticsSolver();

            observed = new StatisticsSolver[names.length];

            // initialise the array
            for (int i = 0; i < observed.length; i++) {
                observed[i] = new StatisticsSolver();
            }

            // Calculate correlations for this feature
            for (int i = 0; i < j.project.getImages().size(); i++) {
                JasmineImage image = j.project.getImages().elementAt(i);
                PixelLoader img = new PixelLoader(image.getBufferedImage());
                Vector<Pixel> pixels = image.getOverlayPixels(mode);

                if (pixels != null) {
                    for (int k = 0; k < pixels.size(); k++) {
                        if (k % 2 == 0) continue;
                        Pixel pixel = pixels.elementAt(k);
                        expected.addData(pixel.value);

                        for (int l = 0; l < observed.length; l++) {
                            observed[l].addData(getValue(img, pixel, l));
                        }
                    }
                }
            }

            processData();

            init();

        } catch (IOException e) {
            j.alert("Cannot load overlay pixels for one of the images.");
        }

    }

    public abstract void init();

    public abstract void processData();

}
