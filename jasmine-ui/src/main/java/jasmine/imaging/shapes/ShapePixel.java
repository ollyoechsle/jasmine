package jasmine.imaging.shapes;


import jasmine.imaging.commons.Pixel;

import java.io.Serializable;

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
 * @author Olly Oechsle, University of Essex, Date: 01-Feb-2007
 * @version 1.0
 */
public class ShapePixel extends Pixel implements Serializable {

    boolean isEdge = false;
    boolean alreadyChecked = false;
    public int neighbours = 8;

    boolean insideEdge = true;

    public ShapePixel(int x, int y) {
        super(x, y);
        this.isEdge = false;
    }

    public ShapePixel(int x, int y, boolean isEdge) {
        super(x, y);
        this.isEdge = isEdge;
    }   

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Returns whether a pixel pair is diagonally adjacent
     */
    public boolean isDiagonallyAdjacent(ShapePixel other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        return (dx == 1 && dy == 1);
    }

}
