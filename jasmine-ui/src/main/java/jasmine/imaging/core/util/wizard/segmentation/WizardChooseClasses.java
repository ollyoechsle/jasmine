package jasmine.imaging.core.util.wizard.segmentation;

import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineClassStatistics;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.core.segmentation.ClassIDMapping;
import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.io.IOException;

/**
 * Allows the user to choose what flavour of segmentation to use.
 *
 * @author Olly Oechsle, University of Essex, Date: 08-Aug-2007
 * @version 1.0
 */
public class WizardChooseClasses extends WizardPanel {

    protected SegmentationWizard wizard;
    protected JCheckBox[] classes;
    protected JTextField[] classLabels;

    public String getTitle() {
        return "Classes";
    }

    public String getDescription() {
        return "You may untick the boxes of classes you don't want to have solved, you may combine classes together by overriding their IDs";
    }

    public boolean isOK() {
        Vector<ClassIDMapping> classesToBeSolved = getClassesToBeSolved();
        return classesToBeSolved != null;
    }

    //POEY comment: Menu: Evolve > Background Subtracter > Background Subtraction Wizard > Classes
    public WizardChooseClasses(SegmentationWizard wizard, int mode) {

        try {

        this.wizard = wizard;

        //POEY comment: for segmentation, mode = 0 = MASK
        Vector<JasmineClass> pixelClasses = wizard.jasmine.project.getClasses(wizard.mode);

        JPanel classesPanel = new JPanel(new GridLayout(pixelClasses.size() + 1, 2));
        classesPanel.add(new JLabel("Class"));
        classesPanel.add(new JLabel("Label"));
        classesPanel.add(new JLabel("Instances"));

        Vector<JasmineClassStatistics> stats;
        if (wizard.mode == JasmineClass.MASK) {
        	//POEY comment: get classes of pixels, the number of selected pixels of all images for each type
        	//and the number of images
            stats = wizard.jasmine.project.getMaskStatistics();
        } else {
            stats = wizard.jasmine.project.getMaterialStatistics();
        }

        classes = new JCheckBox[pixelClasses.size()];
        classLabels = new JTextField[pixelClasses.size()];
        for (int i = 0; i < pixelClasses.size(); i++) {
            JasmineClass jasmineClass = pixelClasses.elementAt(i);
            //POEY comment: instances = the number of selected pixels of all images for each type
            int instances = stats.elementAt(i).instances;
            classes[i] = new JCheckBox(jasmineClass.name, true);
            if (instances == 0) classes[i].setSelected(false);
            classLabels[i] = new JTextField(String.valueOf(jasmineClass.classID));
            if (mode == JasmineClass.MASK) {
                classLabels[i].setText(JasmineUtils.isBackground(jasmineClass)? "0" : "1");
            }
            classesPanel.add(classes[i]);
            classesPanel.add(classLabels[i]);
            classesPanel.add(new JLabel(String.valueOf(instances)));
        }

        add(classesPanel, BorderLayout.CENTER);

        } catch (IOException e) {
            wizard.jasmine.alert("Cannot start WizardChooseClasses");
            e.printStackTrace();
            throw new RuntimeException("Error while getting pixel statistics");
        }

    }

    //POEY comment: get selected classes from the interface: Background Subtraction Wizard > Classes
    public Vector<ClassIDMapping> getClassesToBeSolved() {
        // Figure out which classes are to be solved  
    	//POEY comment: for segmentation mode=0=MASK
    	//pixelClasses.size() = classes.length    	
        Vector<JasmineClass> pixelClasses = wizard.jasmine.project.getClasses(wizard.mode);        
        Vector<ClassIDMapping> classesToBeSolved = new Vector<ClassIDMapping>(pixelClasses.size());
        for (int i = 0; i < classes.length; i++) {
            JCheckBox box = classes[i];           
            int classLabel = Integer.parseInt(classLabels[i].getText());
            if (box.isSelected()) classesToBeSolved.add(new ClassIDMapping(pixelClasses.elementAt(i).classID, classLabel));
        }

        if (classesToBeSolved.size() < 2) {
            wizard.jasmine.alert("Please select at least two classes.");
            return null;
        } else {
            return classesToBeSolved;
        }
    }
}
