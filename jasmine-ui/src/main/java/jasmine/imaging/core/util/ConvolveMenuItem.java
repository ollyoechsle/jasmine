package jasmine.imaging.core.util;


import jasmine.imaging.commons.ConvolutionMatrix;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ImageFrame;
import jasmine.imaging.core.JasmineMenus;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Menu item for applying convolution previews to the image.
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
 * @author Olly Oechsle, University of Essex, Date: 30-May-2007
 * @version 1.0
 */
public class ConvolveMenuItem extends JMenuItem {

    protected int type;
    protected JasmineMenus menus;
    protected String name;

    public ConvolveMenuItem(JasmineMenus menus, String name, int type) {
        super(name);
        this.name = name;
        this.type = type;
        this.menus = menus;
        addActionListener(menus);
    }

    public void displayConvolvedImage() {
        PixelLoader loader = menus.j.getCurrentImage();
        BufferedImage img = loader.getConvolved(new ConvolutionMatrix(type));
        ImageFrame f = new ImageFrame(img);
        f.setTitle(name);
    }

}