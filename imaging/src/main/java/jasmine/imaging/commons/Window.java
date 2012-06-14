package jasmine.imaging.commons;

import java.io.Serializable;
import java.awt.*;

/**
 * Represents a window within an image, consisting of
 * width, height and position
 * @author Olly Oechsle, University of Essex, Date: 19-Mar-2008
 * @version 1.0
 */
public class Window implements Serializable {

    public int width, height, x, y;
    protected WindowClass c;

    public Window(int width, int height, int left, int top, WindowClass c)  {
        this.width = width;
        this.height = height;
        this.x = left;
        this.y = top;
        this.c = c;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public WindowClass getWindowClass() {
        return c;
    }

    public void setClass(WindowClass c) {
        this.c = c;
    }

    public void drawRect(Graphics g) {
        if (c != null) g.setColor(c.colour);
        g.drawRect(x,y,width,height);
    }

    public void fillRect(Graphics g) {
        if (c != null) g.setColor(c.colour);
        g.fillRect(x,y,width,height);
    }
    
}
