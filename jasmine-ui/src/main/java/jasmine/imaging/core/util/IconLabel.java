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
public class IconLabel extends JLabel {

    protected boolean selected;
    protected boolean showIcon;

    protected int fontHeight;

    protected Image icon, iconDark;

    public IconLabel() {
        fontHeight = getFontMetrics(getFont()).getHeight();
        try {
            icon = new ImageIcon(getClass().getResource("/star16.png")).getImage();
            iconDark = new ImageIcon(getClass().getResource("/star16_dark.png")).getImage();
        } catch (Exception e) {
            // don't worry
            icon = null;
        }
    }

    public IconLabel(String text) {
        this();
        setText(text);
    }



    protected void paintComponent(Graphics g) {

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (selected) {
            g.setColor(SystemColor.textHighlight);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        int rectStart = (getHeight() - 10) / 2;

        if (icon != null && showIcon) {
            g.drawImage(selected?icon:iconDark, 5, rectStart, null);
        }

        if (selected) {
            g.setColor(SystemColor.textHighlightText);
        } else {
            g.setColor(SystemColor.textText);
        }

        g.drawString(getText(), 20, rectStart+10);

    }

}