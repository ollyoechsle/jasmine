package jasmine.imaging.core.segmentation;


import jasmine.gp.training.PixelSelection;
import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.JasmineImage;

import java.io.IOException;
import java.util.Vector;

/**
 *
 * <p>
 * An implementation of Pixel selection that only permits pixels
 * contained in the overlay of a Jasmine Image to be considered.
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
 * @author Olly Oechsle, University of Essex, Date: 12-Jan-2007
 * @version 1.0
 */
public class JasminePixelSelection extends PixelSelection {

    private Vector<Pixel> overlayPixels;
    private int cursor;
    private int size;

    public JasminePixelSelection(JasmineImage image, int mode) throws IOException {
        super(new PixelLoader(image.getBufferedImage(), null));
        overlayPixels = image.getOverlayPixels(mode);
        size = overlayPixels.size();
        cursor = 0;
    }

    public Vector<Pixel> getPixels() {
        return overlayPixels;
    }

    public Pixel getRandomPixel() {
        return overlayPixels.elementAt((int) (Math.random() * overlayPixels.size()));
    }

    public Pixel getNextPixel() {
        Pixel p = overlayPixels.elementAt(cursor);
        cursor++;
        return p;
    }

    public boolean hasMorePixels() {
        return cursor < size;
    }

    public void reset() {
        cursor = 0;
    }

}
