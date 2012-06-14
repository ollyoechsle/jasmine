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
public class JavaDoc {

    protected String comment;
    protected String version;
    protected String author;

    public JavaDoc() {
        comment = "";
        version = null;
        author = null;
    }

    public void addLine(String line) {
        comment += " * " + line + "\n";
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toSource() {
        if (comment.length() == 0) return "";
        StringBuffer buffer = new StringBuffer();
        buffer.append("/**\n");
        buffer.append(comment);
        
        if (author != null) {
            buffer.append(" * @author ");
            buffer.append(author);
            buffer.append("\n");
        }

        if (version != null) {
            buffer.append(" * @version ");
            buffer.append(version);
            buffer.append("\n");
        }

        buffer.append(" */\n");
        return buffer.toString();
    }

}
