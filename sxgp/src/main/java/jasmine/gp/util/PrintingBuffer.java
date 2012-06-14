package jasmine.gp.util;

/**
 * Redirects output from std out to a stringbuffer.
 *
 * @author Olly Oechsle, University of Essex, Date: 24-Apr-2007
 * @version 1.0
 */
public class PrintingBuffer {

    StringBuffer buffer;

    boolean displayInStdOut;

    public PrintingBuffer(boolean displayInStdOut) {
        buffer = new StringBuffer(4096);
        this.displayInStdOut = displayInStdOut;
    }

    public void append(String s) {
        buffer.append(s);
        buffer.append('\n');
        if (displayInStdOut) System.out.println(s);
    }

    public String toString() {
        return buffer.toString();
    }

}
