package jasmine.imaging.gp.nodes;


import jasmine.gp.nodes.imaging.parameterised.ParameterisedTerminal;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.shapes.ExtraShapeData;

import java.util.Vector;

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
 * @author Olly Oechsle, University of Essex, Date: 22-Feb-2007
 * @version 1.0
 */
public class Roughness extends ParameterisedTerminal {

    int MIN_ROUGHNESS = 4;
    int MAX_ROUGHNESS = 12;

    private int roughness;

    public Roughness() {
        this(4);
    }

    public Roughness(int roughness) {
        this.roughness = roughness;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getRoughness(roughness);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getRoughness(" + roughness + ")";
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public String getShortName() {
        return "Rough" + roughness;
    }

    public ParameterisedTerminal getRandom() {
        int r = getRandom(MIN_ROUGHNESS, MAX_ROUGHNESS);
        return new Roughness(r);
    }

    public Vector<ParameterisedTerminal> getDefaults() {
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
        defaults.add(new Roughness(4));
        defaults.add(new Roughness(8));
        defaults.add(new Roughness(12));
        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{roughness};
    }

}
