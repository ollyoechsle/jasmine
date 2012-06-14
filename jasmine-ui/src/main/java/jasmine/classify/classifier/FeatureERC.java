package jasmine.classify.classifier;

import jasmine.gp.Evolve;
import jasmine.gp.nodes.ercs.BasicERC;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;

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
 * @author Olly Oechsle, University of Essex, Date: 12-Dec-2008
 * @version 1.0
 */
public final class FeatureERC extends BasicERC {

    public static float[] values;
    public static int numFeatures = 0;

    public double initialise() {
    	System.err.println("FeatureERC");
        return (Evolve.getRandomNumber() * max);
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        int i = (int) getValue();
        if (i >= numFeatures) i = numFeatures - 1;
        data.value = values[i];
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "feature[" + (int) getValue() + "]";
    }

    public String getShortName() {
        return "f" + (int) getValue();
    }

}
