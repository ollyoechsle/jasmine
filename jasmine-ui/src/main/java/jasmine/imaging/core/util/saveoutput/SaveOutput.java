package jasmine.imaging.core.util.saveoutput;


import jasmine.imaging.commons.util.FileIO;
import jasmine.imaging.core.Jasmine;

import javax.swing.*;
import java.io.*;

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
 * @author Olly Oechsle, University of Essex, Date: 27-Jun-2007
 * @version 1.0
 */
public class SaveOutput extends PrintStream {

    static PrintStream oldStdout;
    static PrintStream oldStderr;
    static File f;

    SaveOutput(PrintStream ps) {
        super(ps);
    }

    /**
     * Starts copying the std out and std error to a file
     */
    public static void start() throws IOException { // Add a time stamp to the end of the file name.

        f = new File(new File(System.getProperty("user.dir")), "jasmine.out");

        // make sure the file is empty
        if (f.length() > 0) {
            FileIO.saveToFile("", f);
        }

        // Save old settings.
        oldStdout = System.out;
        oldStderr = System.err;

        // Start redirecting the output
        System.setOut(new PrintStream(new SpecialOutputStream(f, true)));
        System.setErr(new PrintStream(new SpecialOutputStream(f, true)));

        System.out.println("Started System.out redirection.");

    }

    static OutputConsole outputWindow = null;

    public static JDialog getOutputWindow(Jasmine j) {

        try {

            if (outputWindow == null) {
                outputWindow = new OutputConsole(j);
            }

            return outputWindow;

        } catch (IOException e) {
            j.alert("Cannot open output: " + e.toString());
            SaveOutput.stop();
            e.printStackTrace();
            return null;
        }

    }

    public static String getOutput() throws IOException {
        if (f == null) {
            start();
        }
        return FileIO.readFile(f);

    }

    // Restores the original settings.
    public static void stop() {
        System.out.println("Stopped System.out redirection.");
        System.setOut(oldStdout);
        System.setErr(oldStderr);
    }

}
