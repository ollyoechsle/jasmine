package jasmine.imaging.commons;


import jasmine.imaging.commons.util.Region;

import java.awt.*;

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
 * @author Olly Oechsle, University of Essex, Date: 23-Oct-2006
 * @version 1.0
 */
public interface Grid {

    public int getTotalHorizontalLines();

    public int getTotalVerticalLines();

    public void draw(Graphics g, Region r);

}
