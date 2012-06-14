package jasmine.imaging.core.util.wizard;

import javax.swing.*;

/**
 * A JPanel which implements this interface can be used as part of a wizard.
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Feb-2008
 * @version 1.0
 */
public abstract class WizardPanel extends JPanel {
    public abstract String getTitle();
    public abstract String getDescription();
    public abstract boolean isOK();
}
