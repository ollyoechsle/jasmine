package jasmine.imaging.commons;

import java.io.File;

/**
 * <p>
 * Returns haar like statistics which are calculated using an integral image.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 07-Sep-2006
 * @version 1.0
 */
public class HaarlikeFeatures {
    
    public static int MODE = 0;

    public static final int HORIZONTALLY_ADJACENT = 1;
    public static final int VERTICALLY_ADJACENT = 2;

    public static final int FIRST_SHAPE = 1;
    public static final int SECOND_SHAPE = 2;

    public static final int SUM = 1;
    public static final int VARIANCE = 2;

    protected int mode;

    protected IntegralImage image;

//    public static void main(String[] args) throws Exception {
//        IntegralImage img = new IntegralImage(new File("/home/ooechs/Data/faces/test/face/test-face-0.bmp"));
//        img.getHaarlikeFeatures().getTwoRectangleFeature(0, 1, 20, 1, 2);
//
//
//        FastStatistics f = new FastStatistics();
//        for (int j = 0; j < 6; j++) {
//        long start = System.currentTimeMillis();
//
//        for (int i = 0; i < 1000; i++) {
//        for (int x = 0; x < 20; x++) {
//            for (int y = 0; y < 20; y++) {
//                for (int w = 0; w < 20; w++) {
//                    for (int h = 0; h < 20; h++) {
//                        img.getHaarlikeFeatures().getTwoRectangleFeature(x, y, w, h, 2);
//                    }
//                }
//            }
//        }
//        }
//        long end = System.currentTimeMillis();
//        long time = end - start;
//        if (j > 0) {
//        System.out.println("Time: " + time);
//            f.addData(time);
//        }
//        }
//        System.out.println("Mean: " + f.getMean());
//
//    }

    public HaarlikeFeatures(IntegralImage image, int mode) {
        this.image = image;
        this.mode = mode;
    }

    /**
     * @return The difference of sums between two adjacent rectangles.
     */
    public double getTwoRectangleFeature(int x, int y, int width, int height, int adjacency) {               

        int doubleWidth = width << 1;
        int doubleHeight = height << 1;

        if (adjacency == HORIZONTALLY_ADJACENT) {
            if (x + doubleWidth > image.width) return 0;
            if (y + height > image.height) return 0;
            if (x + doubleWidth < 0) return 0;
            if (y + height < 0) return 0;
        } else {
            if (x + width > image.width) return 0;
            if (y + doubleHeight > image.height) return 0;
            if (x + width < 0) return 0;
            if (y + doubleHeight < 0) return 0;
        }

        double firstSum = getSum(x, y, width, height);
        double secondSum = adjacency == HORIZONTALLY_ADJACENT? getSum(x,y,doubleWidth, height) : getSum(x, y, width, doubleHeight);

        if (mode == SUM) {
            // calculate the mean
            double area = width * height;
            return pd(firstSum, secondSum, area);
        } else {
            return pd(secondSum, firstSum, 1);
        }

    }

    public double pd(double firstSum, double secondSum, double area) {
        switch (MODE) {
            case 0:
                return (secondSum - firstSum) / area;
            case 1:
                return ((secondSum - firstSum) / firstSum) * 100;
            case 2:
                return secondSum - firstSum;

        }
        return 0;
    }



    public double getThreeRectangleFeature(int x, int y, int width, int height, int adjacency) {

        int tripleWidth = width * 3;
        int tripleHeight = height * 3;

        if (adjacency == HORIZONTALLY_ADJACENT) {
            if (x + tripleWidth > image.width) return 0;
            if (y + height > image.height) return 0;
            if (x + tripleWidth < 0) return 0;
            if (y + height < 0) return 0;
        } else {
            if (x + width > image.height) return 0;
            if (y + tripleHeight > image.height) return 0;
            if (x + width < 0) return 0;
            if (y + tripleHeight < 0) return 0;
        }

        double firstSum = adjacency == HORIZONTALLY_ADJACENT? getSum(x+width, y, width, height) : getSum(x, y + height, width, height);
        double secondSum = adjacency == HORIZONTALLY_ADJACENT? getSum(x,y,tripleWidth, height) : getSum(x, y, width, tripleHeight);

        if (mode == SUM) {
            // area is equivalent to two squares
            // calculate the mean
            double area = width * height * 2;
            return pd(firstSum, secondSum, area);
        } else {
            return pd(secondSum, firstSum, 1);
        }

    }    

    public double getFourRectangleFeature(int x, int y, int width, int height, int adjacency) {

        int doubleWidth = width << 1;
        int doubleHeight = height << 1;

        if (x + doubleWidth < 0) return 0;
        if (y + doubleHeight < 0) return 0;

        if (x + doubleWidth > image.width) return 0;
        if (y + doubleHeight > image.height) return 0;

        double largerSum = getSum(x,y,doubleWidth, doubleHeight);
        double firstSum = 0;

        if (adjacency == HORIZONTALLY_ADJACENT) {
            firstSum += getSum(x,y,width, height);
            firstSum += getSum(x+width,y+height,width,height);
        } else {
            firstSum += getSum(x+width,y,width, height);
            firstSum += getSum(x,y+height,width,height);
        }

        double secondSum = largerSum - firstSum;

        if (mode == SUM) {
            // area is equivalent to two squares
            // calculate the mean
            double area = width * height * 2;
            return pd(firstSum, secondSum, area);
        } else {
            return pd(secondSum, firstSum, 1);
        }

    }

    public double getSum(int x, int y, int width, int height) {
        try {
            if (mode == SUM) {
                return image.getSum(x, y, x + width, y + height);
            } else {
                return image.getStdDeviation(x, y, x + width, y + height);
            }
        } catch (Exception e) {
            return 0;
        }
    }


}

