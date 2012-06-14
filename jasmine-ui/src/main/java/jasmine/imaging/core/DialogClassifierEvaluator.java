package jasmine.imaging.core;


import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SubObjectClassifier;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

/**
 * Evaluates a shape classifier on test data defined in a Jasmine project. Essentially
 * it runs the classifier against every item of training data and returns how many were
 * correct and how many were wrong.
 *
 * @author Olly Oechsle, University of Essex, Date: 19-Apr-2007
 * @version 1.0
 */
public class DialogClassifierEvaluator extends JDialog {

    JButton buttonCancel;
    JLabel classifierName;
    JLabel total;
    JLabel tp;
    JLabel fp;
    JProgressBar progressBar;

    public DialogClassifierEvaluator(Jasmine jasmine) {

        setTitle("Evaluate Classifier");
        init();
        start(jasmine);
        setLocation(400, 400);
        setSize(320, 160);
        setVisible(true);

    }

    public void init() {

        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(progressBar, BorderLayout.NORTH);

        JPanel results = new JPanel();
        results.setLayout(new GridLayout(4, 2));

        tp = new JLabel();
        fp = new JLabel();
        total = new JLabel();

        classifierName = new JLabel();
        results.add(new JLabel("Classifier:"));
        results.add(classifierName);

        results.add(new JLabel("Total Shapes: "));
        results.add(total);

        results.add(new JLabel("Correct: "));
        results.add(tp);

        results.add(new JLabel("Incorrect:"));
        results.add(fp);

        c.add(results, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));


        buttonCancel = new JButton("Close");

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        
        bottom.add(buttonCancel);

        c.add(bottom, BorderLayout.SOUTH);
    }

    public void onCancel() {
        dispose();
    }


    public void start(Jasmine jasmine) {
        // get all the shapes
        Vector<SegmentedObject> shapes = new Vector<SegmentedObject>(100);
        for (int i = 0; i < jasmine.project.getImages().size(); i++) {
            JasmineImage image = jasmine.project.getImages().elementAt(i);
            shapes.addAll(image.getObjects());
        }

        if (shapes.size() == 0) {
            jasmine.alert("Can't evaluate - you need to mark up the images/shapes you want to test first.");
            dispose();
        }

        SubObjectClassifier classifier = jasmine.getShapeClassifier();

        String className = classifier.getClass().getName();
        classifierName.setText(className.substring(className.lastIndexOf(".") + 1));

        progressBar.setMaximum(shapes.size());
        progressBar.setVisible(true);
        int TP = 0;
        int FP = 0;
        total.setText(String.valueOf(shapes.size()));



        for (int i = 0; i < shapes.size(); i++) {
            SegmentedObject shape = shapes.elementAt(i);
            progressBar.setValue(i + 1);

            System.out.print(i + ", ");

            int classID = -1; //classifier.classify(new ExtraShapeData(shape));

            if (classID == shape.getClassID()) {
                TP++;
                System.out.println("1");
            } else {
                FP++;
                System.out.println("0");
            }

            tp.setText(String.valueOf(TP));
            fp.setText(String.valueOf(FP));

        }

        double percentage = (TP / (double) shapes.size()) * 100;

        DecimalFormat f = new DecimalFormat("0.0");

        tp.setText(String.valueOf(TP) + " (" + f.format(percentage) + "%)");

        progressBar.setVisible(false);

    }



}
