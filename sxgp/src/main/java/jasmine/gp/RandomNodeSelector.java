package jasmine.gp;

import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeUtils;

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
 * @author Olly Oechsle, University of Essex, Date: 15-Dec-2008
 * @version 1.0
 */
public class RandomNodeSelector implements Selector {

    public Node select(Node tree, int returnType) {   	
        return TreeUtils.getRandomSubtree(tree, returnType);
    }
    
}
