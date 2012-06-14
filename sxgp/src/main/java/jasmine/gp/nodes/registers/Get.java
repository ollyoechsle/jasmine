package jasmine.gp.nodes.registers;

import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;

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
 * @author Olly Oechsle, University of Essex, Date: 23-Oct-2008
 * @version 1.0
 */
public class Get extends Terminal {

   protected int index;

   public Get(int index) {
        this.index = index;
   }

    /**
     * Any processing the node does takes place here
     */
    public double execute(DataStack data) {
        return data.registers.values[index];
    }

    /**
     * Strongly typed GP: What type does the node return?
     */
    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER};
    }

    /**
     * What would the java be if this node were written in it?
     */
    public String toJava() {
        return "register" + index;
    }

    public Object[] getConstructorArgs() {
        // make sure the index is remembered when this node is cloned.
        return new Object[]{index};
    }


}
