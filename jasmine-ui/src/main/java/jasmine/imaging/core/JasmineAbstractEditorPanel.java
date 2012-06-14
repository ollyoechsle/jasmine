package jasmine.imaging.core;


import jasmine.imaging.commons.util.ImagePanel;
import jasmine.imaging.core.util.JasmineTab;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Common Functionality between the Segmentation image panel and the
 * Classification image panel
 *
 * @author Olly Oechsle, University of Essex, Date: 19-Jun-2007
 * @version 1.0
 */
public abstract class JasmineAbstractEditorPanel extends JPanel {

    protected Jasmine j;

    protected ImagePanel imagePanel;

    int prevX = -1;
    int prevY = -1;

    public JPanel getPanel() {
        JPanel panel = new JasmineTab(getMode());
        OverlayLayout overlay = new OverlayLayout(panel);
        panel.setLayout(overlay);
        panel.add(this);
        panel.add(imagePanel);
        return panel;
    }

    public abstract int getMode();

    public JasmineAbstractEditorPanel(final Jasmine j) {

        imagePanel = new ImagePanel() {
            public void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (!fade) {
                    graphics.setColor(new Color(0, 0, 0, 128));
                    graphics.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setDisplayCentered(true);

        this.j = j;

        setOpaque(false);

        setFocusable(true);

        // listen for ascii keys
        char[] c = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'j', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        for (int i = 0; i < c.length; i++) {
            final String keyPressed = String.valueOf(c[i]);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(c[i]), keyPressed);
            getActionMap().put(keyPressed, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    j.classbox.appendCharAndSearch(keyPressed);
                }
            });
        }


        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {
                if (imagePanel.image != null) {
                    int x = (e.getX() - imagePanel.getOffsetX()) / imagePanel.zoom;
                    int y = (e.getY() - imagePanel.getOffsetY()) / imagePanel.zoom;
                    //j.mousePosition.setText(x + ", " + y);
                } else {
                    //j.mousePosition.setText("");
                }
                onMouseMoved(e);
            }

            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                onMousePressed(e);
            }

            public void mouseReleased(MouseEvent e) {
                onMouseReleased(e);
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getUnitsToScroll() > 0) {
                    zoomOut();
                } else {
                    zoomIn();
                }
                repaint();
            }
        });

    }

    private boolean fade = true;

    public void toggleDisplayImage() {
        fade = !fade;
        imagePanel.repaint();
        this.repaint();
    }

    public BufferedImage getImage() {
        return imagePanel.image;
    }

    public void setImageNull() {
        imagePanel.setImageNull();
    }

    public void onMouseReleased(MouseEvent e) {
        prevX = -1;
        prevY = -1;
    }

    public void zoomIn() {
        imagePanel.zoomIn();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void zoomOut() {
        imagePanel.zoomOut();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public abstract void loadJasmineImage(JasmineImage image, BufferedImage img);

    public void onMouseMoved(MouseEvent e) {
    }

    public void onMouseDragged(MouseEvent e) {
    }

    public abstract void clear();

    public abstract void onMousePressed(MouseEvent e);

    public void paintComponent(Graphics g) {

        // draw the background image (or not)
/*            if (j.displayImage) {
            super.paintComponent(g);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }*/

        if (j.project == null) return;

        render(g);

    }


    public abstract void render(Graphics g);

}
