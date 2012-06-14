package jasmine.imaging.core;


import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.util.Histogram;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

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
 * @author Olly Oechsle, University of Essex, Date: 19-Jun-2007
 * @version 1.0
 */
public class JasmineSegmentationPanel extends JasmineAbstractEditorPanel {

    protected int DEFAULT_SIZE = 30;

    // record where the mouse was clicked first
    int startX = -1;
    int startY = -1;

    // record where the mouse is
    int mouseX = 0;
    int mouseY = 0;

    // draw the overlay on a separate layer
    protected BufferedImage maskOverlay, materialOverlay;

    // how big is the paintbrush?
    protected int size;

    // has the overlay changed in any way?
    protected boolean maskOverlayChanged = false;
    protected boolean materialOverlayChanged = false;

    // the current mode
    public int mode = JasmineClass.MASK;

    public void setMode(int mode) {
        this.mode = mode;
        repaint();
        j.classbox.refresh();
        j.imageBrowser.refresh();

        if (mode == JasmineClass.MASK) {
            j.menus.file_save_overlay.setText("Save Mask");
            j.menus.edit_clear.setText("Clear Mask");
            if (materialOverlay != null) {
                j.menus.edit_clear.setEnabled(true);
            } else {
                j.menus.edit_clear.setEnabled(false);
            }
        }

        if (mode == JasmineClass.MATERIAL) {
            j.menus.file_save_overlay.setText("Save Overlay");
            j.menus.edit_clear.setText("Clear Overlay");
            if (maskOverlay != null) {
                j.menus.edit_clear.setEnabled(true);
            } else {
                j.menus.edit_clear.setEnabled(false);
            }
        }

        if (j.displayStats != null) {
            j.displayStats.mode = this.mode;
            j.displayStats.refresh();
        }

    }

    public JasmineSegmentationPanel(Jasmine j) {
        super(j);
        setCursorSize(DEFAULT_SIZE);
    }

    public int getMode() {
        return Jasmine.PIXEL_SEGMENTATION;
    }

    public void onMouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    public void onMouseDragged(MouseEvent e) {

        mouseX = e.getX();
        mouseY = e.getY();

        // dragging is only used during pixel segmentation markup
        if (j.segmentationMode == j.PAINT || SwingUtilities.isLeftMouseButton(e)) {
            // target mode only draws once
            if (j.segmentationMode != j.TARGET) {
                drawPixels(e);
            }
        }

        if (j.segmentationMode == j.LINE || j.segmentationMode == j.TARGET) {
            repaint();
        }

        if (imagePanel.image != null) {
            int x = (e.getX() - imagePanel.getOffsetX()) / imagePanel.zoom;
            int y = (e.getY() - imagePanel.getOffsetY()) / imagePanel.zoom;
            j.mousePosition.setText(x + ", " + y);
        } else {
            j.mousePosition.setText("");
        }

    }

    public void zoomIn() {
        super.zoomIn();
        setCursorSize(size);
    }

    public void zoomOut() {
        super.zoomOut();
        setCursorSize(size);
    }

    public void onMousePressed(MouseEvent e) {

        if ((j.segmentationMode == j.PAINT || j.segmentationMode == j.LINE) && j.classbox.getCurrentClass() == null) {
            j.alert("Cannot draw - no class selected");
            return;
        }

        // for the line
        startX = e.getX();
        startY = e.getY();

        // make sure some segmentation mode is specified
        if (j.segmentationMode == null) {
            j.segmentationMode = j.PAINT;
        }

        // painting mode
        if (j.segmentationMode == j.PAINT || j.segmentationMode == j.TARGET || SwingUtilities.isLeftMouseButton(e)) {
            drawPixels(e);
        }

    }

    public BufferedImage getCurrentOverlay() {
        if (mode == JasmineClass.MASK) {
            if (maskOverlay == null) {
                maskOverlay = new BufferedImage(imagePanel.image.getWidth(), imagePanel.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            }
            return maskOverlay;
        } else {
            if (materialOverlay == null) {
                materialOverlay = new BufferedImage(imagePanel.image.getWidth(), imagePanel.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            }
            return materialOverlay;
        }
    }

    public void onMouseReleased(MouseEvent e){

        super.onMouseReleased(e);
        
        if (j.segmentationMode == j.LINE) {
            int radius = size / 2;
            int sx = (startX - imagePanel.getOffsetX()) / imagePanel.zoom;
            int sy = (startY - imagePanel.getOffsetY()) / imagePanel.zoom;
            int ex = (e.getX() - imagePanel.getOffsetX()) / imagePanel.zoom;
            int ey = (e.getY() - imagePanel.getOffsetY()) / imagePanel.zoom;
            Graphics2D g = (Graphics2D) getCurrentOverlay().getGraphics();
            g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(j.classbox.getCurrentClass().color);
            g.drawLine(sx + radius, sy + radius, ex + radius, ey + radius);
            startX = -1;
            startY = -1;
            repaint();
            setOverlayChanged(true);
        }
        startX = -1;
        startY = -1;
    }

    public void setOverlayChanged(boolean changed) {
        if (mode == JasmineClass.MASK) maskOverlayChanged = changed;
        if (mode == JasmineClass.MATERIAL) materialOverlayChanged = changed;
    }

    public boolean overlayChanged() {
        if (mode == JasmineClass.MASK) return maskOverlayChanged;
        if (mode == JasmineClass.MATERIAL) return materialOverlayChanged;
        return false;
    }

    public boolean eitherOverlayChanged() {
        return (maskOverlayChanged || materialOverlayChanged);
    }

    public void drawPixels(MouseEvent e) {

        if (imagePanel.image == null) {
            System.err.println("Image is null");
            return;
        }

        if (j.classbox.getCurrentClass() == null) {
            System.err.println("Classbox is null");
            return;
        }

        setOverlayChanged(true);

        j.menus.file_save_overlay.setEnabled(true);
        j.menus.edit_clear.setEnabled(true);

        Graphics2D g = (Graphics2D) getCurrentOverlay().getGraphics();

        int radius = size / 2;

        int x = (e.getX() - imagePanel.getOffsetX()) / imagePanel.zoom;
        int y = (e.getY() - imagePanel.getOffsetY()) / imagePanel.zoom;

        if (j.segmentationMode == null) {
            throw new RuntimeException("Cannot draw while segmentation mode is null");
        }

        if ((j.segmentationMode == j.PAINT || j.segmentationMode == j.TARGET) && SwingUtilities.isLeftMouseButton(e)) {

            // PAINTING MODE
            g.setColor(j.classbox.getCurrentClass().color);
            g.fillOval(x, y, size, size);

            if (prevX != -1) {
                if (j.segmentationMode == j.PAINT) {
                    g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(prevX, prevY, x + radius, y + radius);
                }
            }

            repaint();

        }

        if (j.segmentationMode == j.ERASE || SwingUtilities.isRightMouseButton(e)) {
            // ERASING MODE
            int radiusSquared = radius * radius;
            for (int dY = -radius; dY <= radius; dY++) {
                for (int dX = -radius; dX <= radius; dX++) {
                    double r = (dX * dX) + (dY * dY);
                    if (r <= radiusSquared || (dX == 0 && dY == 0)) {
                        try {
                            getCurrentOverlay().setRGB(x + dX + radius, y + dY + radius, 0);
                        } catch (ArrayIndexOutOfBoundsException aib) {
                            // do nothing
                        }
                    }
                }
            }
            repaint();
        }

        if (j.segmentationMode == j.HISTOGRAM) {
            // get a count for each gray value and plot on a graph
            PixelLoader loader = j.getCurrentImage();
            Hashtable<Integer, Integer> count = new Hashtable<Integer, Integer>();
            int radiusSquared = radius * radius;
            for (int dY = -radius; dY < radius; dY++) {
                for (int dX = -radius; dX < radius; dX++) {
                    double r = (dX * dX) + (dY * dY);
                    if (r <= radiusSquared) {
                        int value = loader.getGreyValue(x + dX, y + dY);
                        Integer c = count.get(value);
                        if (c == null) {
                            c = 1;
                        } else {
                            c = c + 1;
                        }
                        count.put(value, c);
                    }
                }
            }

            new Histogram(count);

        }

        prevX = x + radius;
        prevY = y + radius;

    }


    public void setMaskOverlay(BufferedImage pixelOverlay) {
        this.maskOverlay = pixelOverlay;
        maskOverlayChanged = false;
    }

    public void clear() {
        if (mode == JasmineClass.MASK) {
            if (imagePanel.image != null) {
                maskOverlay = new BufferedImage(imagePanel.image.getWidth(), imagePanel.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                j.menus.file_save_overlay.setEnabled(true);
            } else {
                maskOverlay = null;
            }
            maskOverlayChanged = false;
        } else {
            if (imagePanel.image != null) {
                materialOverlay = new BufferedImage(imagePanel.image.getWidth(), imagePanel.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                j.menus.file_save_overlay.setEnabled(true);
            } else {
                materialOverlay = null;
            }
            materialOverlayChanged = false;
        }
    }

    public void setCursorSize(int size) {
        this.size = size;

        // create the cursor
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        int cursor_size = (size * imagePanel.zoom);
        BufferedImage cursorImage = new BufferedImage(cursor_size + 1, cursor_size + 1, BufferedImage.TYPE_INT_ARGB);
        //Graphics2D g = (Graphics2D) cursorImage.getGraphics();
        //g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //g.drawOval(0, 0, cursor_size, cursor_size);
        Point cursorHotSpot = new Point(0, 0);
        
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "My Cursor");
        setCursor(customCursor);

    }

    public void saveOverlays(JasmineImage image) {
           try {

               if (maskOverlayChanged) {
                    OverlayData od = new OverlayData(getCurrentOverlay(), j.project, j.project.currentImage(), JasmineClass.MASK);
                    od.save();
                   j.setStatusText("Saved overlay, " + od.size() + " pixels.");
               }
               if (materialOverlayChanged) {
                    OverlayData od = new OverlayData(getCurrentOverlay(), j.project, j.project.currentImage(), JasmineClass.MATERIAL);
                    od.save();
                   j.setStatusText("Saved overlay, " + od.size() + " pixels.");
               }
               j.menus.file_save_overlay.setEnabled(false);
               j.imageBrowser.refresh();

               if (j.displayStats != null && j.displayStats.isVisible()) {
                   j.displayStats.refresh();
               }

               setOverlayChanged(false);

           } catch (Exception e) {
               j.alert("Could not save segmentation overlay: " + e.toString());
               e.printStackTrace();
           }
       }


    public void saveOverlay(JasmineImage image) {
        try {

            OverlayData od = new OverlayData(getCurrentOverlay(), j.project, j.project.currentImage(), mode);

            od.save();

            if (od.size() > 0) {
                j.setStatusText("Saved overlay, " + od.size() + " pixels.");
            } else {
                j.setStatusText("Removed empty overlay file");
            }

            j.menus.file_save_overlay.setEnabled(false);
            j.imageBrowser.refresh();

            if (j.displayStats != null && j.displayStats.isVisible()) {
                j.displayStats.refresh();
            }

            setOverlayChanged(false);

        } catch (Exception e) {
            j.alert("Could not save segmentation overlay: " + e.toString());
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {

        if (imagePanel.image != null && getCurrentOverlay() != null) {
            Graphics2D g2 = (Graphics2D) g;
            Composite oldcomp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            imagePanel.drawImage(g, getCurrentOverlay());
            g2.setComposite(oldcomp);
            if (j.classbox.getCurrentClass() != null) {
                g.setColor(j.classbox.getCurrentClass().color);
            } else {
                g.setColor(Color.BLACK);
            }

            // draw the cursor
            int ovalSize = size * imagePanel.zoom;
            g.drawOval(mouseX, mouseY, ovalSize, ovalSize);

            if (j.segmentationMode == j.TARGET) {
                int mx = mouseX  + (ovalSize / 2);
                int my = mouseY  + (ovalSize / 2);
                // horzontal line
                g.drawLine(0, my, getWidth(), my);
                // vertical line
                g.drawLine(mx, 0, mx, getHeight());
            }

            // draw the line
            if (j.segmentationMode == j.LINE && startX != -1) {
                int size = ovalSize / 2;
                g.drawOval(startX, startY, ovalSize, ovalSize);
                g.drawLine(startX + size, startY + size, mouseX + size, mouseY + size);
            }

        }

    }


    public void loadJasmineImage(JasmineImage image, BufferedImage img) {

        maskOverlayChanged = false;
        materialOverlayChanged = false;

        if (image == null) {
            setImageNull();
            clear();
            repaint();
            return;
        }

        if (img != null) {

            imagePanel.setImage(img);
            clear();

            // save the width and height. This allows the overlay
            // to be reloaded with the correct dimensions.
            image.setWidth(img.getWidth());
            image.setHeight(img.getHeight());

                try {

                    if (image.materialOverlayFilename != null) {
                        materialOverlay = OverlayData.load(image, JasmineClass.MATERIAL);
                        if (materialOverlay == null) {
                            j.alert("Overlay: " + image.materialOverlayFilename + " not found.");
                            image.materialOverlayFilename = null;
                            j.menus.edit_clear.setEnabled(false);
                        }
                    }

                    if (image.maskOverlayFilename != null) {
                        maskOverlay = OverlayData.load(image, JasmineClass.MASK);
                        if (maskOverlay == null) {
                            j.alert("Overlay: " + image.maskOverlayFilename + " not found.");
                            image.maskOverlayFilename = null;
                            j.menus.edit_clear.setEnabled(false);
                        }
                    }

                    repaint();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Could not load segmentation overlay for this image: " + e);
                    e.printStackTrace();
                }
            } else {
                j.menus.edit_clear.setEnabled(false);
            }
            j.menus.edit_delete_shape.setEnabled(false);
        }
                    
    }

