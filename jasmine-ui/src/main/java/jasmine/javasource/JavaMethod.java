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
public class JavaMethod {

    protected String comment;
    protected String signature;
    protected String source;

    public JavaMethod() {
        this.signature = null;
        this.source = "";
    }

    public JavaMethod(String signature) {
        this.signature = signature;
        this.source = "";
    }

    public JavaMethod(String signature, String source) {
        this.signature = signature;
        this.source = source;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void addLine(String line) {
        if (!source.endsWith("\n"))  {
            source += "\n";
        }
        if (!line.startsWith("\t")) {
            source += "\t";
        }
        source += line;

    }

    public String toSource() {
        StringBuffer buffer = new StringBuffer();
        if (comment != null) {
            buffer.append("/**\n");
            buffer.append(" * ");
            buffer.append(comment);
            buffer.append("\n */\n");
        }
        if (signature != null) {
            buffer.append(signature);
            buffer.append(" {\n");
        }
        buffer.append(source);
        if (signature != null) {
            buffer.append("\n}\n");
        }
        return buffer.toString();
    }

}
