package jasmine.imaging.commons;


import javax.swing.*;
import java.awt.*;

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
 * @author Olly Oechsle, University of Essex, Date: 06-Feb-2007
 * @version 1.0
 */
public abstract class Segmenter {

    static final int red = Color.RED.getRGB();

    public int getColor(int classID) {
        if (classID > 1) return red;
        return -1;
    }

    public int[][] segment(PixelLoader image) {
        int[][] array = new int[image.getWidth()][image.getHeight()];
        // first thing to do is to push the whole image into an editable array
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                array[x][y] = segment(image, x, y);
            }
        }
        return array;
    }

    /**
     * Segments the image and returns an array of pixels
     * @param image
     * @param bar
     * @return An array of pixels labelled with integer classes.
     */
    public int[][] segment(PixelLoader image, JProgressBar bar) {
        int[][] array = new int[image.getWidth()][image.getHeight()];
        // first thing to do is to push the whole image into an editable array
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                array[x][y] = segment(image, x, y);
            }
            if (bar != null) bar.setValue(y);
        }
        return array;
    }

    public abstract int segment(PixelLoader image, int x, int y);

}
