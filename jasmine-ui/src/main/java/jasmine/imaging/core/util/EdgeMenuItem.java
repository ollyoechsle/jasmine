package jasmine.imaging.core.util;


import jasmine.imaging.commons.ConvolutionMatrix;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ImageFrame;
import jasmine.imaging.core.JasmineMenus;

import javax.swing.*;
import java.awt.image.BufferedImage;
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
 * @author Olly Oechsle, University of Essex, Date: 22-Feb-2008
 * @version 1.0
 */
public class EdgeMenuItem extends JMenuItem {

    protected JasmineMenus menus;

    public EdgeMenuItem(JasmineMenus menus, String name) {
        super(name);
        this.menus = menus;
        addActionListener(menus);
    }

    public void displayConvolvedImage() {
        PixelLoader pl = menus.j.getCurrentImage();
        BufferedImage image = pl.getBufferedImage();

        Graphics g = image.getGraphics();
        g.setColor(Color.RED);

        ConvolutionMatrix HSOBEL = new ConvolutionMatrix(ConvolutionMatrix.HORIZONTAL_SOBEL);
        ConvolutionMatrix VSOBEL = new ConvolutionMatrix(ConvolutionMatrix.VERTICAL_SOBEL);

        for (int y = 0; y < image.getHeight(); y += 2)
            for (int x = 0; x < image.getWidth(); x += 2) {

                double gx = pl.getConvolved(x, y, HSOBEL);
                double gy = pl.getConvolved(x, y, VSOBEL);

                int G = Math.abs((int) gx) + Math.abs((int) gy);

                if (G > 200) {

                    double angle = Math.atan(gy / gx);

                    double lineLength = 3;
                    int x1 = (int) (Math.cos(angle) * lineLength);
                    int y1 = (int) (Math.sin(angle) * lineLength);

                    g.drawLine(x - x1, y - y1, x + x1, y + y1);
                }
            }
        ImageFrame f = new ImageFrame(image);
        f.setTitle("Edge Detection");
    }

}