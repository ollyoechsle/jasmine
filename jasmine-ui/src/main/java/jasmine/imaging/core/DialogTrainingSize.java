package jasmine.imaging.core;


import jasmine.imaging.core.util.BrowseButton;
import jasmine.imaging.core.util.OKCancelBar;
import jasmine.imaging.core.util.PropertyValue;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;
import java.io.File;

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
 * @author Olly Oechsle, University of Essex, Date: 13-Feb-2007
 * @version 1.0
 */
public class DialogTrainingSize extends JDialog implements ActionListener, ChangeListener {

    public static String TRAIN_PROPORTION_HANDLE = "training_proportion";

    protected JSlider totalSize, trainingProportion;
    protected JButton ok, cancel;
    protected JLabel lblTotalSize;
    protected JLabel lblTrainingProportion;
    protected static SelectFilePanel training, testing;

    private Jasmine jasmine;

    int totalPixels = 0;

//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            System.out.println("Unable to load native look and feel");
//        }
//        new DialogTrainingSize(null, 1000);
//    }

    public DialogTrainingSize(Jasmine jasmine, int trainingSize) {

        super(jasmine);
        this.jasmine = jasmine;

        try {
            setIconImage(new ImageIcon(getClass().getResource("/measure16.png")).getImage());
        } catch (Exception e) {
        }

        setTitle("Choose Dataset Size");

        setModal(true);

        totalPixels = trainingSize;

        if (jasmine != null) {
            setIconImage(jasmine.getIconImage());
        }

        int userPreferredSize = 5;
        if (jasmine != null && jasmine.project.getProperty(TRAIN_PROPORTION_HANDLE) != null) {
            userPreferredSize = (Integer) jasmine.project.getProperty(TRAIN_PROPORTION_HANDLE);
        }

        totalSize = new JSlider(1, 100, userPreferredSize);
        totalSize.addChangeListener(this);

        trainingProportion = new JSlider(1, 100, 50);
        trainingProportion.addChangeListener(this);

        lblTrainingProportion = new JLabel("50%");
        lblTotalSize = new JLabel("0");

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Total Data", totalSize, lblTotalSize));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Training %", trainingProportion, lblTrainingProportion));
        fields.add(Box.createVerticalStrut(5));

        if (training == null) training = new SelectFilePanel(jasmine, "Training");
        fields.add(training);
        fields.add(Box.createVerticalStrut(5));

        if (testing == null) testing = new SelectFilePanel(jasmine, "Testing");
        fields.add(testing);
        fields.add(Box.createVerticalStrut(15));        fields.add(Box.createVerticalGlue());

        updateLabels();

        ok = new JButton("OK");
        cancel = new JButton("Cancel");

        ok.addActionListener(this);
        cancel.addActionListener(this);

        getContentPane().add(fields, BorderLayout.CENTER);
        getContentPane().add(new OKCancelBar(ok, cancel), BorderLayout.SOUTH);

        int num = (int) ((totalSize.getValue() / 100f) * totalPixels);
        lblTotalSize.setText(String.valueOf(num));
        lblTrainingProportion.setText(trainingProportion.getValue() + "%");

        setSize(470, 200);
        setLocationRelativeTo(jasmine);
        //setResizable(false);
        setVisible(true);



    }


    public void onOK(float totalProportion, float trainingPercentage, File training, File testing) throws IOException {
        // do nothing - this method will be overridden.

    }

    public synchronized void stateChanged(ChangeEvent e) {
        int num = (int) ((totalSize.getValue() / 100f) * totalPixels);
        if (e.getSource() == totalSize) {
            lblTotalSize.setText(String.valueOf(num));
        } else {
            lblTrainingProportion.setText(trainingProportion.getValue() + "%");
        }
        updateLabels();
    }

    public void updateLabels() {
        //System.out.println("Update labels");
        int num = (int) ((totalSize.getValue() / 100f) * totalPixels);
        float proportion = trainingProportion.getValue()/100f;
        int trainingPixels = (int) (num * proportion);
        int testingPixels = num - trainingPixels;
        training.setText("Training (" + trainingPixels + ")");
        testing.setText("Testing (" + testingPixels + ")");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            setVisible(false);
            try {
                jasmine.project.addProperty(TRAIN_PROPORTION_HANDLE, totalSize.getValue());
                File trainingFile = training.getFile();
                File testingFile = testing.getFile();
                if (trainingFile == null) {
                    jasmine.alert("Please choose a filename. You may leave the testing filename blank, but you must choose a training filename");
                    return;
                }
                onOK(totalSize.getValue() / 100f, trainingProportion.getValue() / 100f, trainingFile, testingFile);
            } catch (Exception err) {
                jasmine.alert("IO Exception while saving data: " + err.getMessage());
                err.printStackTrace();
            }
            dispose();
        }
        if (e.getSource() == cancel) {
            setVisible(false);
            dispose();
        }
    }

    class SelectFilePanel extends JPanel implements ActionListener {

        public JLabel label;
        public JTextField file;
        public JButton btn;

        public Jasmine j;

        public SelectFilePanel(Jasmine j, String name) {
            this.j = j;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            label = new JLabel(name + ":  ", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(PropertyValue.lblWidth, PropertyValue.height));
            add(label);
            file = new JTextField("");
            file.setPreferredSize(new Dimension(PropertyValue.lblWidth, PropertyValue.height));
            add(file);
            btn = new BrowseButton();
            btn.setPreferredSize(new Dimension(25, 25));
            add(Box.createHorizontalStrut(5));
            add(btn);
            add(Box.createHorizontalStrut(5));
            btn.addActionListener(this);
            add(Box.createHorizontalGlue());
            this.setPreferredSize(new Dimension(-1, PropertyValue.height));
        }

        public void setText(String text) {
            label.setText(text + ":  ");
        }

        public void actionPerformed(ActionEvent e) {
            if (j != null) {
                String suggestedFilename = "";
                if (this == training) {
                    suggestedFilename = jasmine.project.getName() + "_training.csv";
                } else {
                    suggestedFilename = jasmine.project.getName() + "_testing.csv";
                }
                File f = j.getExportFilename("Select file", suggestedFilename, ".csv");
                if (f != null) {
                    file.setText(f.getAbsolutePath());
                }
            }
        }

        public File getFile() {
            if (file.getText() == null || file.getText().length() == 0) return null;
            return new File(file.getText());
        }

    }

}