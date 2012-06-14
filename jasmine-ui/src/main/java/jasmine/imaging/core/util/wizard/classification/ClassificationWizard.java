package jasmine.imaging.core.util.wizard.classification;


import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineFeatureSelectionDialog;
import jasmine.imaging.core.classification.JasmineGP;
import jasmine.imaging.core.classification.JasmineICS;
import jasmine.imaging.core.util.TerminalMetaData;
import jasmine.imaging.core.util.wizard.Wizard;
import jasmine.imaging.core.util.wizard.WizardPanel;

import java.util.Vector;

/**
 * <p/>
 * Allows the user to start classification by following a series of steps.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Feb-2008
 * @version 1.0
 */
public class ClassificationWizard extends Wizard {

    protected Jasmine jasmine;

    protected WizardChooseGP chooseGP;

    // object or sub object?
    protected int mode;

    public ClassificationWizard(Jasmine jasmine, int mode) {
        this.mode = mode;
        this.jasmine = jasmine;
        initialise(jasmine);
    }

    public String getTitle() {
        return JasmineClass.getTypeName(mode) + " Classification Wizard";
    }

    public Vector<WizardPanel> getPanels() {
        Vector<WizardPanel> wizardPanels = new Vector<WizardPanel>();
        chooseGP = new WizardChooseGP(this);
        wizardPanels.add(chooseGP);
        return wizardPanels;
    }

    public void onFinish() {
        Object p = chooseGP.getProblem(mode);

        //POEY comment: user chose a method which is not ICS
        if (p instanceof JasmineGP) {

            JasmineGP problem = (JasmineGP) p;

            problem.setTerminalMetaData((Vector<TerminalMetaData>) jasmine.project.getProperty(JasmineFeatureSelectionDialog.getFeatureSetHandle(mode)));

            jasmine.runGPClassification(problem, chooseGP.chkAdvanced.isSelected());

        }

        //POEY comment: user chose ICS
        if (p instanceof JasmineICS) {

            JasmineICS superclassifier = (JasmineICS) p;

            superclassifier.setTerminalMetaData((Vector<TerminalMetaData>) jasmine.project.getProperty(JasmineFeatureSelectionDialog.getFeatureSetHandle(mode)));

            jasmine.runICSClassification(superclassifier, chooseGP.chkAdvanced.isSelected());

        }

        dispose();

    }

}