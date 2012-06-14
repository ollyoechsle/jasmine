package jasmine.imaging.core.visionsystem;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.shapes.SegmentedShape;
import jasmine.imaging.shapes.ShapePixel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 22-Apr-2009
 * Time: 15:05:51
 * To change this template use File | Settings | File Templates.
 */
public class VisionSystemGraphics {

    
    public static Pixel getLabelPosition(SegmentedShape object, int offsetX, int offsetY) {
        int objectWidth = object.maxX - object.minX;
        int objectHeight = object.maxY - object.minY;
        int x = object.minX + (objectWidth / 2) + offsetX;
        int y = object.minY + (objectHeight / 2) + offsetY;
        return new ShapePixel(x, y);
    }

    public static void drawShape(BufferedImage img, SegmentedShape o, Color c) {
        int color = c.getRGB();
        Vector<ShapePixel> edge = o.pixels;
        for (int i = 0; i < edge.size(); i++) {
            ShapePixel shapePixel = edge.elementAt(i);
            int x = shapePixel.x;
            int y = shapePixel.y;
            img.setRGB(x, y, color);
        }
    }

    public static void drawShapeOutline(BufferedImage img, SegmentedShape o, Color c) {
        int color = c.getRGB();
        Vector<ShapePixel> edge = o.edgePixels;
        for (int i = 0; i < edge.size(); i++) {
            ShapePixel shapePixel = edge.elementAt(i);
            int x = shapePixel.x;
            int y = shapePixel.y;
            img.setRGB(x, y, color);
        }
    }


    public static void drawLabel(Graphics2D g2, FontMetrics f, Pixel highest, String label, Color edgeColor, Color bgColor, Color textColor) {

        int lineHeight = 20;
        int padding = 3;
        Rectangle2D r = f.getStringBounds(label, g2);
        int halfStringWidth = (int) (r.getWidth() / 2);
        int halfStringHeight = (int) (r.getHeight() / 2);
        int stringWidth = halfStringWidth * 2;
        int stringHeight = halfStringHeight * 2;

        g2.setColor(bgColor);
        g2.fillRect(highest.x - halfStringWidth - padding, highest.y - lineHeight - stringHeight - padding - padding, stringWidth + padding + padding, stringHeight + padding + padding);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(textColor);
        g2.drawString(label, highest.x - halfStringWidth, highest.y - lineHeight - halfStringHeight);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setColor(edgeColor);
        g2.drawRect(highest.x - halfStringWidth - padding, highest.y - lineHeight - stringHeight - padding - padding, stringWidth + padding + padding, stringHeight + padding + padding);
        g2.drawLine(highest.x, highest.y, highest.x, highest.y - lineHeight);

    }

}
