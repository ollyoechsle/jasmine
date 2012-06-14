package jasmine.gp.training;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.SimpleImage;

import java.util.Vector;

/**
 *
 * <p>
 * Allows SXGP to choose what pixels will be used from the image.
 * It may wish to inspect all of them, but then there may be parts
 * of the image for which there is no or ambiguous truth.
 * </p>
 *
 * <p>
 * The PixelSelection essentially controls which pixels ECJ is permitted
 * to train on.
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
 * @author Olly Oechsle, University of Essex, Date: 18-Jan-2007
 * @version 1.0
 */
public abstract class PixelSelection {

    /**
     * The image to process
     */
    protected SimpleImage image;

    public PixelSelection(SimpleImage image) {
        this.image = image;
    }

    public SimpleImage getImage() {
        return image;
    }

    public abstract Vector<Pixel> getPixels();

    public abstract Pixel getNextPixel();

    public abstract Pixel getRandomPixel();

    public abstract boolean hasMorePixels();

    public abstract void reset();

}
