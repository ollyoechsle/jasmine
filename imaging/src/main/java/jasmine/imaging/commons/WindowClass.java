package jasmine.imaging.commons;

import java.awt.*;
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
 * @author Olly Oechsle, University of Essex, Date: 19-Mar-2008
 * @version 1.0
 */
public class WindowClass implements Serializable {

    protected String name;
    protected int classID;
    protected Color colour;

    public WindowClass(String name, int classID, Color color) {
        this.name = name;
        this.classID = classID;
        this.colour = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public Color getColor() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public String toString() {
        return name;
    }

}
