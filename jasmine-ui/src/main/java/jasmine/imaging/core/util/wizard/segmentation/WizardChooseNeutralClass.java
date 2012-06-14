package jasmine.imaging.core.util.wizard.segmentation;


import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;

/**
 * Allows the user to choose what flavour of segmentation to use.
 *
 * @author Olly Oechsle, University of Essex, Date: 08-Aug-2007
 * @version 1.0
 */
public class WizardChooseNeutralClass extends WizardPanel {

    protected SegmentationWizard wizard;
    protected JTextField neutralClassID;

    public String getTitle() {
        return "Neutral Class";
    }

    public String getDescription() {
        return "If you want, you can assign a class for which mistakes are allowable. That is, if a classifier returns this classID by mistake, it won't be punished for it (but won't be rewarded either)";
    }

    public boolean isOK() {
        try {
            getNeutralClassID();
            return true;
        } catch (NumberFormatException e) {
            wizard.alert("Please enter a number (-1 if you don't want to use the neutral class feature)");
            return false;
        }
    }

  //POEY comment: Menu: Evolve > Background Subtracter > Background Subtraction Wizard > Neutral Class
    public WizardChooseNeutralClass(SegmentationWizard wizard, int mode) {

        this.wizard = wizard;

        neutralClassID = new JTextField("-1");
        if (mode == JasmineClass.MASK) {
            //neutralClassID.setText("0");
        }

        JPanel classesPanel = new JPanel(new GridLayout(1, 2));
        classesPanel.add(new JLabel("Neutral Class"));
        classesPanel.add(neutralClassID);

        add(classesPanel, BorderLayout.CENTER);

    }

    public int getNeutralClassID() {
        return Integer.parseInt(neutralClassID.getText());
    }

}
