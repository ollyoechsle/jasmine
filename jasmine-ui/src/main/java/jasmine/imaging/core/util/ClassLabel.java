package jasmine.imaging.core.util;

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
 * @author Olly Oechsle, University of Essex, Date: 20-Apr-2007
 * @version 1.0
 */
public class ClassLabel extends JLabel {

    protected Color c;
    protected boolean selected;

    private int fontHeight;

//    public static void main(String[] args) {
//    }

    public ClassLabel() {
         fontHeight = getFontMetrics(getFont()).getHeight();
    }

    public ClassLabel(String text) {
        this();
        setText(text);
    }

    public void setClassColour(Color c) {
        this.c = c;
    }

    protected void paintComponent(Graphics g) {

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (selected) {
            g.setColor(SystemColor.textHighlight);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        int rectStart = (getHeight() - 10) / 2;

   /*     if (c != null) {
            g.setColor(c);
            g.fillRect(5,  rectStart,  10, 10);
        }*/

            int x = 5;
            int y = rectStart;
            int width = 10;
            int height = 10;
            if (c != null) {
                g.setColor(c);
                g.fillRect(x, y, width, height);
                g.setColor(new Color(1,1,1, 16));
                int halfHeight = height / 2;
                g.fillRect(x, y+halfHeight, width, height-halfHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width-1, height-1);
            }

/*
        if (selected) {
            g.setColor(Color.WHITE);
            if (c != null) {
                g.drawRect(5,  rectStart,  10, 10);
            }
        }
*/

        if (selected) {
            g.setColor(SystemColor.textHighlightText);
        } else {
            g.setColor(SystemColor.textText);
        }

        g.drawString(getText(), 20, rectStart+10);

    }

}
