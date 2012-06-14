package jasmine.imaging.core.util.wizard.segmentation;


import jasmine.imaging.core.DialogTrainingSize;
import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Vector;
import java.awt.*;

/**
 * Allows the user to choose what flavour of segmentation to use.
 *
 * @author Olly Oechsle, University of Essex, Date: 08-Aug-2007
 * @version 1.0
 */
public class WizardChooseTrainingSize extends WizardPanel implements ChangeListener {

    protected SegmentationWizard wizard;
    int totalPixels;
    protected JSlider trainingProportion;
    protected JLabel trainingSize;

    public String getTitle() {
        return "Training Size";
    }

    public String getDescription() {
        return "Please choose how many of the training pixels you'd like to use; using fewer pixels will decrease training time.";
    }

    public boolean isOK() {
        if (trainingProportion.getValue() > 0) {
            return true;
        } else {
            wizard.alert("Please choose a non-zero training size");
            return false;
        }
    }

    //POEY comment: Menu: Evolve > Background Subtracter > Background Subtraction Wizard > Training Size
    public WizardChooseTrainingSize(SegmentationWizard wizard, int totalPixels) {

        this.totalPixels = totalPixels;
        this.wizard = wizard;

        // choose the percentage that gives about 5000 pixels training data
        int userPreferredSize = 5;
        if (totalPixels < 5000) userPreferredSize = 100; 	//POEY comment: use all selected pixels (100%)
        else {	//POEY comment: userPreferredSize (%)
            userPreferredSize = new Double(((5000/(double) totalPixels) * 100)).intValue();
            
            //POEY
            if(userPreferredSize<=0){	
            	System.err.println("Sorry, you chose to many pixels.");
            	System.exit(1);
            }
        }

        if (wizard.jasmine.project.getProperty(DialogTrainingSize.TRAIN_PROPORTION_HANDLE) != null) {
            userPreferredSize = (Integer) wizard.jasmine.project.getProperty(DialogTrainingSize.TRAIN_PROPORTION_HANDLE);
        }

        trainingProportion = new JSlider(1, 100, userPreferredSize);
        trainingProportion.addChangeListener(this);

        JPanel classesPanel = new JPanel();
        classesPanel.setLayout(new BoxLayout(classesPanel, BoxLayout.LINE_AXIS));
        classesPanel.add(new JLabel("Training Samples"));
        classesPanel.add(trainingProportion);
        trainingSize = new JLabel("");
        classesPanel.add(trainingSize);

        stateChanged(null);

        add(classesPanel, BorderLayout.CENTER);

    }


    public synchronized void stateChanged(ChangeEvent e) {
        int percent = trainingProportion.getValue();
        int num = (int) ((trainingProportion.getValue() / 100f) * totalPixels);
        trainingSize.setText(" " + num + " samples (" + percent + "%)");
    }

    public float getTrainingProportion() {
        wizard.jasmine.project.addProperty(DialogTrainingSize.TRAIN_PROPORTION_HANDLE, trainingProportion.getValue());
        return trainingProportion.getValue() / 100f;
    }

}