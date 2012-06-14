package jasmine.classify.classifier;


import jasmine.imaging.commons.FastStatistics;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>
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
 * @author Olly Oechsle, University of Essex, Date: 22-Aug-2008
 * @version 1.0
 */
public abstract class ParameterStatistics {

    public static Hashtable<String, FastStatistics> stats;
    protected String name;
    public Vector<Integer> values;
    public ParameterStatistics next = null;

    public ParameterStatistics(String name) {
        this.name = name;

        values = new Vector<Integer>(5);
    }

    public static void clearStats() {
        stats = new Hashtable<String, FastStatistics>();
    }

    public abstract void applyToProblem(ProblemSettings s, int value);

    /**
     * Adds a possible value, for instance if this is for
     * tournament size then you could add 2 and 7.
     * @param value
     */
    public void addValue(int value) {
        values.add(value);
    }
    
    public void addStatistic(int parameterValue, float result) {
        if (stats == null) clearStats();
        String key = name + " = " + parameterValue;
        FastStatistics f = stats.get(key);
        if (f == null) {
            f = new FastStatistics();
            stats.put(key, f);
        }
        f.addData(result);
    }

    public static void printResults(Hashtable<String, FastStatistics> stats) {
        Enumeration<String> values = stats.keys();
        while (values.hasMoreElements()) {
            String key = values.nextElement();
            FastStatistics f = stats.get(key);
            System.out.println(key + ": mean=" + f.getMean() + ", min=" + f.getMin() + ", max=" + f.getMax() + ", sd=" + f.getStandardDeviation() + ", n=" + f.getN());
        }
    }

}
