package jasmine.imaging.commons;

import jasmine.imaging.commons.util.Gaussian;
import jasmine.imaging.commons.util.Pythag;

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
 * @author Olly Oechsle, University of Essex, Date: 19-Jan-2007
 * @version 1.0
 */
public class ConvolutionMatrix {

    public static final int LAPLACIAN = 1;
    public static final int MEAN = 2;
    public static final int GAUSSIAN = 3;
    public static final int M10 = 4;
    public static final int M11 = 5;
    public static final int M01 = 6;
    public static final int M02 = 7;
    public static final int HORIZONTAL_SOBEL = 8;
    public static final int VERTICAL_SOBEL = 9;
    public static final int HORIZONTAL_SOBEL5 = 10;
    public static final int VERTICAL_SOBEL5 = 11;    

    protected double[][] weights;
    protected int width;
    protected int height;
    protected double total;

    public ConvolutionMatrix(int type) {
        switch (type)  {

             case HORIZONTAL_SOBEL:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = -1; weights[1][0] = -2; weights[2][0] = -1;
                weights[0][1] = 0; weights[1][1] = 0; weights[2][1] = 0;
                weights[0][2] = 1; weights[1][2] = 2; weights[2][2] = 1;
                total = 1;
                break;
            case VERTICAL_SOBEL:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = 1; weights[1][0] = 0; weights[2][0] = -1;
                weights[0][1] = 2; weights[1][1] = 0; weights[2][1] = -2;
                weights[0][2] = 1; weights[1][2] = 0; weights[2][2] = -1;
                total = 1;
                break;
             case VERTICAL_SOBEL5:
                this.width = 5;
                this.height = 5;
                this.weights = new double[][]{
                        new double[]{-1,-4,-6,-4,-1},
                        new double[]{-2,-8,-12,-8,-2},
                        new double[]{0,0,0,0,0},
                        new double[]{2,8,12,8,2},
                        new double[]{1,4,6,4,1}
                };
                total = 1;
                break;
            case HORIZONTAL_SOBEL5:
                this.width = 5;
                this.height = 5;
                this.weights = new double[][]{
                        new double[]{1,2,0,-2,-1},
                        new double[]{4,8,0,-8,-4},
                        new double[]{6,12,0,-12,-6},
                        new double[]{4,8,0,-8,-4},
                        new double[]{1,2,0,-2,-1}
                };
                total = 1;
                break;
            case LAPLACIAN:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = 0; weights[1][0] = 1; weights[2][0] = 0;
                weights[0][1] = 1; weights[1][1] = -4; weights[2][1] = 1;
                weights[0][2] = 0; weights[1][2] = 1; weights[2][2] = 0;
                total = 1;
                break;
            case MEAN:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                double value = 1/9d;
                weights[0][0] = value; weights[1][0] = value; weights[2][0] = value;
                weights[0][1] = value; weights[1][1] = value; weights[2][1] = value;
                weights[0][2] = value; weights[1][2] = value; weights[2][2] = value;
                total = 1;
                break;
            case GAUSSIAN:
                this.width = 9;
                this.height = 9;
                this.weights = new double[width][height];
                Gaussian g = new Gaussian(1.4, 0);
                for (int y = -1; y <= 2; y++)
                for (int x = -2; x <= 2; x++) {
                    double dist = Pythag.distance(x, y);
                    double gValue = (g.getY(dist) * 15);
                    total += gValue;
                    weights[x + 2][y + 2] = gValue;
                }
            case M10:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = -1; weights[1][0] = -1; weights[2][0] = -1;
                weights[0][1] = 0; weights[1][1] = 0; weights[2][1] = 0;
                weights[0][2] = 1; weights[1][2] = 1; weights[2][2] = 1;
                total = 1;
                break;
            case M11:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = 1; weights[1][0] = 0; weights[2][0] = -1;
                weights[0][1] = 0; weights[1][1] = 0; weights[2][1] = 0;
                weights[0][2] = -1; weights[1][2] = 0; weights[2][2] = 1;
                total = 1;
                break;
            case M01:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = -1; weights[1][0] = 0; weights[2][0] = 1;
                weights[0][1] = -1; weights[1][1] = 0; weights[2][1] = 1;
                weights[0][2] = -1; weights[1][2] = 0; weights[2][2] = 1;
                total = 1;
                break;
            case M02:
                this.width = 3;
                this.height = 3;
                this.weights = new double[width][height];
                weights[0][0] = 1; weights[1][0] = 0; weights[2][0] = 1;
                weights[0][1] = 1; weights[1][1] = 0; weights[2][1] = 1;
                weights[0][2] = 1; weights[1][2] = 0; weights[2][2] = 1;
                total = 1;
                break;

        }
    }

    public double getTotal() {
        return total;
    }

    public void setValue(int x, int y, double value)  {
        weights[x][y] = value;
    }

    public double getWeight(int x, int y) {
        return weights[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
