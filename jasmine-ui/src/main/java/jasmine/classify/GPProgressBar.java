package jasmine.classify;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

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
 * @author Olly Oechsle, University of Essex, Date: 06-Aug-2008
 * @version 1.0
 */
public class GPProgressBar extends JPanel {

    protected double value;
    protected boolean enabled = true;
    int r, g, b;
    int w = -1;
    protected String text = null;

    public GPProgressBar(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setWidth(int w) {
        this.w = w;
    }

    public void setValue(double value) {
        this.value = value;
        repaint();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        repaint();
    }

    public void setText(String text) {
        this.text = text;
    }

    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        int border = 1;
        int height = (getHeight()) - border - border;

        int alpha = enabled ? 255 : 50;
        int backgroundAlpha = enabled ? 128 : 64;

        int width = (int) ((getWidth() - 1) * value);

        width = Math.max(width, 3);

        // draw the background
        gr.setColor(new Color(0, 0, 0, 20));
        gr.fillRect(0, border, getWidth() - 1, height - border - border);
        gr.setColor(new Color(0, 0, 0, backgroundAlpha));
        gr.drawRect(0, border, getWidth() - 1, height - border - border);

        if (value > 0) {
        // draw the bar
        gr.setColor(new Color(r, g, b, alpha));
        gr.fillRect(0, border, width, height - border - border);

        // and the highlight
        gr.setColor(new Color(255, 255, 255, 200));
        gr.drawLine(0, border + 1, width, border + 1);
        gr.drawLine(1, border + 2, 1, height - 1);
        }

        // and the border
        gr.setColor(new Color(0, 0, 0, alpha));
        gr.drawRect(0, border, width, height - border - border);


        // the text
        if (text != null) {
            gr.setColor(new Color(255,255,255, alpha));
            Rectangle2D r = gr.getFontMetrics().getStringBounds(text, gr);
            int x = (int) ((getWidth() - r.getWidth()) / 2);
            int y = (int) (((getHeight() - r.getHeight()) / 2) + r.getHeight());
            gr.drawString(text, x, y-1);
        }


    }

//    public static void main(String[] args) {
//        JFrame f = new JFrame();
//        Container c = f.getContentPane();
//        c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
//        //c.add(new GPStatusBox(null, null));
//        ClassStatusBox b1 = new ClassStatusBox(1, 500);
//        c.add(b1);
//        b1.update(450, 5);
//        c.add(new ClassStatusBox(2, 500));
//        c.add(new ClassStatusBox(3, 500));
//        f.setSize(600, 100);
//        f.setVisible(true);
//    }

    public Dimension getMinimumSize() {
        return new Dimension(100, 20);
    }

    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        d.height = 40;
        if (w != -1) {
            d.width = w;
        }
        return d;
    }

}
