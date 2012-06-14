package jasmine.imaging.core.util;

import javax.swing.*;

/**
 * Base class which represents all the tabs in Jasmine.
 *
 * @author Olly Oechsle, University of Essex, Date: 21-Feb-2008
 * @version 1.0
 */
public class JasmineTab extends JPanel {

    int mode;

    public JasmineTab(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

}
