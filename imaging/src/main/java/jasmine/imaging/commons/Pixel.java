package jasmine.imaging.commons;

import java.io.Serializable;

/**
 *
 * <p>
 * Represents a pixel, a coordinate pair and an optional value.
 * </p>
 *
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
 * @author Olly Oechsle, University of Essex, Date: 07-Sep-2006
 * @version 1.0
 */
public class Pixel implements Serializable {

    public int x;
    public int y;
    public int value;
    public boolean flag;

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pixel(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public void move(int dX, int dY) {
        this.x += dX;
        this.y += dY;
    }

    public String toCSV() {
        return x + "," + y + "," + value;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pixel pixel = (Pixel) o;

        if (x != pixel.x) return false;
        if (y != pixel.y) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = x;
        result = 31 * result + y;
        return result;
    }
}
