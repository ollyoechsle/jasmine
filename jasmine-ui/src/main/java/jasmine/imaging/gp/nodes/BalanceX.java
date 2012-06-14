package jasmine.imaging.gp.nodes;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.shapes.ExtraShapeData;

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
 * @author Olly Oechsle, University of Essex, Date: 16-Feb-2007
 * @version 1.0
 */
public class BalanceX extends Terminal {

    public int getReturnType() {
        return NodeConstraints.NUMBER;
    }

    public double execute(DataStack data) {
        data.usesImaging = true;
        data.value = ((ExtraShapeData) data.getData()).getBalanceX();
        return debugger == null? data.value : debugger.record(data.value);
    }

    public String toJava() {
        return "shape.getBalanceX()";
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FEATURE};
    }

    public String getShortName() {
        return "BalanceX";
    }

}