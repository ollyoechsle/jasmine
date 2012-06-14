package jasmine.imaging.core.util.wizard.segmentation;


import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineFeatureSelectionDialog;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.core.segmentation.ClassIDMapping;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblem;
import jasmine.imaging.core.util.ImagePixel;
import jasmine.imaging.core.util.TerminalMetaData;
import jasmine.imaging.core.util.wizard.Wizard;
import jasmine.imaging.core.util.wizard.WizardPanel;
import jasmine.imaging.core.util.wizard.segmentation.WizardChooseGP;

import java.util.Vector;

/**
 * <p/>
 * Allows the user to start segmentation by following a series of steps.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Feb-2008
 * @version 1.0
 */
public class SegmentationWizard extends Wizard {

    protected int mode;

    protected Jasmine jasmine;

    protected WizardChooseGP chooseGP;
    protected WizardChooseClasses chooseClasses;
    protected WizardChooseNeutralClass chooseNeutralClass;
    protected WizardChooseTrainingSize chooseTrainingSize;

    Vector<ImagePixel> allPixels;

    public SegmentationWizard(Jasmine jasmine, int mode) {
        this.jasmine = jasmine;
        this.mode = mode;
        initialise(jasmine);
    }

    public int getDefaultStartPosition() {
        if (mode == JasmineClass.MASK) {
            return 2;
        }
        return 0;
    }

    public String getTitle() {
        if (mode == JasmineClass.MATERIAL) {
            return "Segmentation Wizard";
        } else {
            return "Background Subtraction Wizard";
        }
    }

    public Vector<WizardPanel> getPanels() {

        Vector<WizardPanel> wizardPanels = new Vector<WizardPanel>();

        try {
            if (mode == JasmineClass.MATERIAL) {
                allPixels = JasmineUtils.getAllMaterialPixels(jasmine.project);
            } else {	//POEY comment: for segmentation mode=MASK
                allPixels = JasmineUtils.getAllMaskPixels(jasmine.project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        chooseClasses = new WizardChooseClasses(this, mode);
        chooseNeutralClass = new WizardChooseNeutralClass(this, mode);
        chooseTrainingSize = new WizardChooseTrainingSize(this, allPixels.size());
        chooseGP = new WizardChooseGP(this, mode);

        wizardPanels.add(chooseClasses);
        wizardPanels.add(chooseNeutralClass);
        wizardPanels.add(chooseTrainingSize);
        wizardPanels.add(chooseGP);
        return wizardPanels;

    }

    //POEY comment: Background Subtraction Wizard window
    public void onFinish() {
   	
    	//POEY comment: get classed of images
        Vector<ClassIDMapping> classesToBeSolved = chooseClasses.getClassesToBeSolved();

        if (classesToBeSolved == null) return;

        //POEY comment: for segmentation, mode = 0
        //show a message on console: Jasmine Segmentation Problem MODE = 0
        JasmineSegmentationProblem problem = chooseGP.getProblem(mode);

        if (problem == null) return;

        problem.setClassesToBeSolved(classesToBeSolved);

        problem.setNeutralClassID(chooseNeutralClass.getNeutralClassID());

        if (mode == JasmineClass.MATERIAL) {
            problem.setTerminalMetaData((Vector<TerminalMetaData>) jasmine.project.getProperty(JasmineFeatureSelectionDialog.MATERIAL_FEATURE_SET_HANDLE));
        } else {
            problem.setTerminalMetaData((Vector<TerminalMetaData>) jasmine.project.getProperty(JasmineFeatureSelectionDialog.MASK_PIXEL_FEATURE_SET_HANDLE));
        }
        //POEY comment: JasmineSegmentationProblem.trainingProportion = the percentage of selected pixels/100
        JasmineSegmentationProblem.trainingProportion = chooseTrainingSize.getTrainingProportion();
        
        jasmine.runGPSegmentation(problem, chooseGP.chkAdvanced.isSelected(), mode);

        dispose();
    }

}
