package jasmine.imaging.core.util.wizard.segmentation;


import jasmine.classify.data.Data;
import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.VarianceThreshold;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.segmentation.ClassIDMapping;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblem;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblemDRSBetter;
import jasmine.imaging.core.util.JasmineSegmentationChooser;
import jasmine.imaging.core.util.PropertyValue;
import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;


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

    final String[] methods = new String[]{"Dynamic Range Selection (DRS)", "Improved Dynamic Range Selection (DRS2)", "Entropy Thresholding (Binary)", "Variance Thresholding (Binary)"};
    protected JComboBox strategy;
    protected JButton autochoose;
    protected JCheckBox chkEnsemble, chkAdvanced;

    protected SegmentationWizard wizard;

    public String getTitle() {
        return "Genetic Programming";
    }


    public String getDescription() {
        return "Please choose the GP representation that you'd like to use. If you're unsure, click the 'Choose For Me' button to have the system choose the appropriate one automatically.";
    }

    public boolean isOK() {
        // remember user preferences
        ensemblePref = chkEnsemble.isSelected();
        advancedPref = chkAdvanced.isSelected();
        return true;
    }

    static boolean ensemblePref = true;
    static boolean advancedPref = false;

    //POEY comment: Menu: Evolve > Background Subtracter > Background Subtraction Wizard > Genetic Programming
    public WizardChooseGP(SegmentationWizard wizard, int mode) {

        this.wizard = wizard;

        strategy = new JComboBox(methods);
        strategy.setSelectedIndex(DRS_BETTER);

        autochoose = new JButton("Choose for me");
        autochoose.setToolTipText("Intelligently selects the most appropriate technique");
        autochoose.addActionListener(this);
        autochoose.setEnabled(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        modePanel.add(new JLabel("Mode: "));
        modePanel.add(strategy);
        modePanel.add(autochoose);

        mainPanel.add(modePanel);

        chkEnsemble = new JCheckBox();
        chkEnsemble.setSelected(ensemblePref);
        mainPanel.add(new PropertyValue("Ensemble Classifier", chkEnsemble));
        
        //POEY comment: for segmentation, mode = MASK
        if (mode == JasmineClass.MASK) {
            // don't want things to be too slow!
            chkEnsemble.setSelected(false);           
        }

        chkAdvanced = new JCheckBox();
        chkAdvanced.setSelected(advancedPref);
        mainPanel.add(new PropertyValue("Advanced Settings", chkAdvanced));

        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

    }

    public void actionPerformed(ActionEvent e) {

    	//POEY comment: in case a user click the button "Choose for me", but I think now it does not work about mode input
        if (e.getSource() == autochoose) {	

            float oldTrainingProportion = JasmineSegmentationProblem.trainingProportion;
            // ensure it runs faster
            JasmineSegmentationProblem.trainingProportion = 0.1f;

            try {

                autochoose.setEnabled(false);
                autochoose.setText("Please wait...");

                wizard.alert("Jasmine will now evaluate a few individuals using each technique to decide the one that is best suited.\nThis may take a minute or so.");

                ProgressDialog d = new ProgressDialog("Autochoose GP", "Evaluating...", methods.length);

                Vector<ClassIDMapping> classesToBeSolved = wizard.chooseClasses.getClassesToBeSolved();

                if (classesToBeSolved == null) return;

                StringBuffer buffer = new StringBuffer();
                buffer.append("Technique, Mean Fitness, Time(ms)\n");

                Vector<Data> trainingData = null;

                String bestName = "";
                int bestIndex = 0;
                double bestFitness = Double.MAX_VALUE;

                // Run each of the three problems on the training data and choose the best
                for (int i = 0; i < methods.length; i++) {

                    JasmineSegmentationProblem problem = getProblem(i);	//POEY comment: i should be mode ???
                    problem.setClassesToBeSolved(classesToBeSolved);

                    //d.setMessage("Evaluating " + problem.getName());

                    if (trainingData == null) {
                        problem.loadTrainingData(null);
                        trainingData = problem.getTrainingData();
                    } else {
                        problem.setTrainingData(trainingData);
                    }

                    long start = System.currentTimeMillis();
                    double meanFitness = new JasmineSegmentationChooser(problem, 1).getMeanFitness();
                    long stop = System.currentTimeMillis();

                    if (meanFitness < bestFitness) {
                        bestFitness = meanFitness;
                        bestIndex = i;
                        bestName = problem.getName();
                    }

                    buffer.append(problem.getName());
                    buffer.append(", ");
                    buffer.append(meanFitness);
                    buffer.append(", ");
                    buffer.append(stop - start);
                    buffer.append("\n");

                    d.setValue(i + 1);
                    System.out.print(".");

                }

                buffer.append("Best: " + bestName);

                d.dispose();

                wizard.alert(buffer.toString());

                strategy.setSelectedIndex(bestIndex);

                autochoose.setText("Choose for me");
                autochoose.setEnabled(true);


                return;

            } catch (Exception err) {
                wizard.alert("Cannot autochoose: " + err.toString());
                err.printStackTrace();
            } finally {
                // restore
                JasmineSegmentationProblem.trainingProportion = oldTrainingProportion;
            }

        }


    }

    public JasmineSegmentationProblem getProblem(int mode) {
        return getProblem(strategy.getSelectedIndex(), mode);
    }

    private JasmineSegmentationProblem getProblem(int index, int mode) {    	
    	//POEY comment: index is an index of selected method from the combobox
    	//mode is a index of 4 methods
    	//for segmentation, chkEnsemble.isSelected(false)
        JasmineSegmentationProblem.ISLAND_COUNT = chkEnsemble.isSelected()? 7 : 1;
        switch (index) {
/*            case DRS_MEAN:
                return new JasmineSegmentationProblemDRSMean(wizard.jasmine.project);
            case SRS:
                return new JasmineSegmentationProblemSRS(wizard.jasmine.project);
            case DT:
                return new JasmineSegmentationProblemDT(wizard.jasmine.project);
            case DRS_MEDIAN:
                return new JasmineSegmentationProblemDRSMedian(wizard.jasmine.project);*/
            case DRS_BASIC:
                return new JasmineSegmentationProblemDRSBetter(wizard.jasmine.project, BasicDRS.TYPE, mode);
            case DRS_BETTER:
                return new JasmineSegmentationProblemDRSBetter(wizard.jasmine.project, BetterDRS.TYPE, mode);
            case DRS_ENTROPY:
                return new JasmineSegmentationProblemDRSBetter(wizard.jasmine.project, EntropyThreshold.TYPE, mode);
            case DRS_VARIANCE:
                return new JasmineSegmentationProblemDRSBetter(wizard.jasmine.project, VarianceThreshold.TYPE, mode);
                /*  case DRS_CONTROLLED:
              return new JasmineSegmentationProblemDRSControlled(wizard.jasmine.project);*/
            default:
                wizard.alert("Invalid segmentation mode: " + index);
                return null;
        }
    }

}
