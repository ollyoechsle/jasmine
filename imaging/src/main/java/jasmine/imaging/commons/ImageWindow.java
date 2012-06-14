package jasmine.imaging.commons;


public class ImageWindow {

    public int left, top, width, height;

    public int colour = Colours.UNKNOWN_COLOUR;

    public ImageWindow(Window w) {
        this.left = w.getX();
        this.top = w.getY();
        this.width = w.getWidth();
        this.height = w.getHeight();
    }

    public ImageWindow(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public int size() {
        return width * height;
    }

    public int getArea() {
        return size();
    }

    public boolean nearEdge(int imageWidth, int imageHeight) {

        int pLeft = (int) ((((double) left) / imageWidth) * 100);
        int pTop = (int) ((((double) top) / imageHeight) * 100);
        int pRight = (int) ((((double) (left + width)) / imageWidth) * 100);
        int pBottom = (int) ((((double)(top + height)) / imageHeight) * 100);

        // 10%
        double threshold = 15;

        if (pLeft < threshold) return true;
        if (pTop < threshold) return true;
        if (pBottom > (100 - threshold)) return true;
        if (pRight > (100 - threshold)) return true;

        return false;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public String toString() {
        return "Left: " + left + ", Top: " + top + ", Width: " + width + ", Height: " + height + ", Colour: " + Colours.colourToString(colour);
    }

}

