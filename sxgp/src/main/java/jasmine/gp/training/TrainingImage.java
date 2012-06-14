package jasmine.gp.training;


import jasmine.imaging.commons.PixelLoader;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * <p>
 * Training Image is a simple image but also has a PixelSelection
 * object bound to it.
 * </p>
 *
 * <p>
 * This is useful if we want to perform image segmentation
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
public class TrainingImage extends PixelLoader {

    protected PixelSelection selection;
    public int classID;

    public TrainingImage(BufferedImage image) {
        this(image, null);
    }

    public TrainingImage(BufferedImage image, PixelSelection selection) {
        super(image);
        this.selection = selection;
    }

    public TrainingImage(BufferedImage image, int classID) {
        super(image);
        this.classID = classID;
    }

    public PixelSelection getSelection() {
        selection.reset();
        return selection;
    }

    public void setSelection(PixelSelection selection) {
        this.selection = selection;
    }

    private String name = null;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        File f = getFile();
        if (f != null) {
            return f.getName();
        }
        return name;
    }

}
