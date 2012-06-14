package jasmine.imaging.core.util.wizard.classification;


import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.VarianceThreshold;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.classification.JasmineGPObject;
import jasmine.imaging.core.classification.JasmineGPSubObject;
import jasmine.imaging.core.classification.JasmineICSObject;
import jasmine.imaging.core.classification.JasmineICSSubObject;
import jasmine.imaging.core.util.PropertyValue;
import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;


/**
 * Allows the user to choose what flavour of segmentation to use.
 *
 * @author Olly Oechsle, University of Essex, Date: 08-Aug-2007
 * @version 1.0
 */
public class WizardChooseGP extends WizardPanel implements ActionListener {

    public static final int DRS_BASIC = 0;
    public static final int DRS_BETTER = 1;
    public static final int DRS_ENTROPY = 2;
    public static final int DRS_VARIANCE = 3;
    public static final int ICS = 4;

    final String[] methods = new String[]{"Basic Dynamic Range Selection (DRS)", "Better Dynamic Range Selection (DRS)", "Entropy Thresholding (Binary)", "Variance Thresholding (Binary)", "Intelligent Classification System (ICS)"};
    protected JComboBox strategy;
    protected JButton autochoose;
    protected JCheckBox chkEnsemble, chkAdvanced;

    protected ClassificationWizard wizard;

    public String getTitle() {
        return "Genetic Programming";
    }

    public String getDescription() {
        //return "Please choose the GP representation you'd like to use. If you're unsure, click the 'Choose For Me' button to have the system choose the appropriate one automatically.";
        //POEY
    	return "Please choose the GP representation that you'd like to use.";
    }

    public boolean isOK() {
        // remember user preferences
        ensemblePref = chkEnsemble.isSelected();
        advancedPref = chkAdvanced.isSelected();        
        return true;
    }

    static boolean ensemblePref = true;
    static boolean advancedPref = false;

    public WizardChooseGP(ClassificationWizard wizard) {

        this.wizard = wizard;

        strategy = new JComboBox(methods);
        strategy.setSelectedIndex(ICS);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        modePanel.add(new JLabel("Mode: "));
        modePanel.add(strategy);

        mainPanel.add(modePanel);

        chkEnsemble = new JCheckBox();
        chkEnsemble.setSelected(false);
        //mainPanel.add(new PropertyValue("Ensemble Classifier", chkEnsemble));

        chkAdvanced = new JCheckBox();
        chkAdvanced.setSelected(advancedPref);
        mainPanel.add(new PropertyValue("Advanced Settings", chkAdvanced));

        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

    }

    public void actionPerformed(ActionEvent e) {
    }

    public Object getProblem(int type) {
        return getProblem(strategy.getSelectedIndex(), type);
    }

    private Object getProblem(int index, int type) {

        if (index == ICS) {
            if (type == JasmineClass.OBJECT)
                return new JasmineICSObject(wizard.jasmine.project);
            else
                return new JasmineICSSubObject(wizard.jasmine.project);
        }

        JasmineGPObject.ISLAND_COUNT = chkEnsemble.isSelected() ? 7 : 1;
        int drsType = BetterDRS.TYPE;
        switch (index) {
            case DRS_BASIC:
                drsType = BasicDRS.TYPE;
                break;
            case DRS_ENTROPY:
                drsType = EntropyThreshold.TYPE;
                break;
            case DRS_VARIANCE:
                drsType = VarianceThreshold.TYPE;
                break;
        }

        if (type == JasmineClass.OBJECT)
            return new JasmineGPObject(wizard.jasmine.project, drsType);
        else
            return new JasmineGPSubObject(wizard.jasmine.project, drsType);
    }

}