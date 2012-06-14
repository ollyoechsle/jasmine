package jasmine.imaging.commons;

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
 * @author Olly Oechsle, University of Essex, Date: 07-Sep-2006
 * @version 1.0
 */
public class Colours {

    public static final int UNKNOWN_COLOUR = 0;

    public static final int DARK = 1;
    public static final int GREY = 2;
    public static final int LIGHT = 9;

    public static final int RED = 3;
    public static final int ORANGE_YELLOW = 4;
    public static final int GREEN = 6;
    public static final int BLUE = 7;
    public static final int PURPLE = 8;


    /**
     * Returns names of the colours
     * SVG Compatible
     * @param colour
     */
    public static String colourToString(int colour) {
        switch (colour) {
            case DARK: return "black";
            case GREY: return "grey";
            case RED: return "red";
            case ORANGE_YELLOW: return "yellow";
            case GREEN: return "green";
            case BLUE: return "blue";
            case PURPLE: return "purple";
        }
        return "Unknown Colour";
    }


}
