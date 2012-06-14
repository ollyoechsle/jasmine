package jasmine.imaging.core.util;


import javax.swing.*;
import java.util.Vector;
import java.awt.event.ActionListener;
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
 * @author Olly Oechsle, University of Essex, Date: 21-Jun-2007
 * @version 1.0
 */
public class ToolButton extends JButton {

    public ToolButton(ActionListener listener, String tooltip, String icon) {

        setToolTipText(tooltip);

        //setIcon(new ImageIcon(Jasmine.ICON_DIRECTORY + icon));
        try {
            setIcon(new ImageIcon(getClass().getResource("/" + icon)));
        } catch (Exception e) {
            System.err.println("Could not load icon: " + icon);
            setText(tooltip);
        }
        addActionListener(listener);
        putClientProperty("JButton.buttonType", "text");

    }

}