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
public class ShapeIntensity extends ParameterisedTerminal {

    int MIN_BLOCKS = 1;
    int MAX_BLOCKS = 4;

    private int blocksX, blocksY, x, y;

    public ShapeIntensity() {
        this(2,2,1,1);
    }

    public ShapeIntensity(int blocksX, int blocksY, int x, int y) {
        this.blocksX = blocksX;
        this.blocksY = blocksY;
        this.x = x;
        this.y = y;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getAverage(blocksX, blocksY, x, y);
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getAverage(" + blocksX + ", " + blocksY + ", " + x + "," + y + ")";
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public String getShortName() {
        return "Average(" + blocksX + ", " + blocksY + ", " + x + "," + y + ")";
    }

    public ParameterisedTerminal getRandom() {
        int bx = getRandom(MIN_BLOCKS, MAX_BLOCKS);
        int by = getRandom(MIN_BLOCKS, MAX_BLOCKS);
        int x = getRandom(0, bx-1);
        int y = getRandom(0, by-1);
        return new ShapeIntensity(bx, by, x, y);
    }

    public Vector<ParameterisedTerminal> getDefaults() {
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
        // no defaults
        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{blocksX, blocksY, x, y};
    }

}