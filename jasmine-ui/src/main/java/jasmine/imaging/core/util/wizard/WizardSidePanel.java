package jasmine.imaging.core.util.wizard;


import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.Vector;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

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
 * @author Olly Oechsle, University of Essex, Date: 26-Feb-2008
 * @version 1.0
 */
public  class WizardSidePanel extends JPanel {

   protected BufferedImage image;
   protected Wizard w;

   public WizardSidePanel(Wizard w) {

       this.w = w;

       try {
           image = javax.imageio.ImageIO.read(getClass().getResource("/wizard.jpg"));
       } catch (IOException e) {
           image = null;
        }

   }

    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(), getHeight());
        if (image != null) {
            g.drawImage(image, 0, getHeight() - image.getHeight(), image.getWidth(), image.getHeight(), null);
        }

        int lineHeight = g.getFontMetrics().getHeight();

        int yMargin = 5;
        int xMargin = 10;
        int y = 10;
        int cellHeight = lineHeight + yMargin + yMargin;
        for (int i = 0; i < w.panels.size(); i++) {
            WizardPanel wizardPanel = w.panels.elementAt(i);
            g.setColor(Color.WHITE);
            if (wizardPanel == w.currentPanel) {
                g.fillRect(xMargin, y, getWidth() - xMargin - xMargin, cellHeight);
            } else {
                g.drawRect(xMargin, y, getWidth() - xMargin - xMargin, cellHeight);
            }
            g.setColor(wizardPanel == w.currentPanel? Color.BLACK : Color.WHITE);
            g.drawString(wizardPanel.getTitle(), xMargin + xMargin, y + lineHeight + yMargin-2);
            y += cellHeight + yMargin;
        }

    }



}