package jasmine.imaging.core.util;


import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ImageFrame;
import jasmine.imaging.core.JasmineMenus;

import javax.swing.*;
import java.awt.image.BufferedImage;

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
 * @author Olly Oechsle, University of Essex, Date: 30-May-2007
 * @version 1.0
 */
public final class FilterMenuItem extends JMenuItem {

    protected int type;
    protected JasmineMenus parent;
    protected String name;

    public FilterMenuItem(JasmineMenus menus, String name, int type) {
        super(name);
        this.name = name;
        this.type = type;
        this.parent = menus;
        addActionListener(menus);
    }

    public void displayProcessedImage() {
        PixelLoader loader = parent.j.getCurrentImage();
        BufferedImage img = loader.getProcessedImage(type);
        ImageFrame f = new ImageFrame(img);
        f.setTitle("Haralick Processing: " + name);
    }

}
