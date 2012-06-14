package jasmine.javasource;

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
 * @author Olly Oechsle, University of Essex, Date: 04-Jun-2007
 * @version 1.0
 */
public class JavaVariable {

    protected String name, type;

    public JavaVariable(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public String toSource() {
        return "protected " + type + " " + name + ";";
    }

}
