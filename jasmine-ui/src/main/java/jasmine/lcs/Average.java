package jasmine.lcs;

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
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public class Average {

    protected double total = 0;
    protected int instances = 0;

    /**
     * Adds a fresh number to be added to the average
     */
    public void add(double value)  {
        this.total += value;
        this.instances++;
    }

    /**
     * Adds a number to be added to the average
     * @param value The value to add
     * @param instances The number of instances that contributed to the total
     */
    public void add(double value, int instances) {
        this.total += value;
        this.instances+= instances;
    }

    /**
     * Gets the average value of all the numbers added.
     */
    public double getMean() {
        return total / instances;
    }

}
