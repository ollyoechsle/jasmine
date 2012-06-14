package jasmine.imaging.core;

import jasmine.gp.multiclass.ClassResults;
import jasmine.gp.util.Deployable;
import jasmine.imaging.commons.FastStatistics;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.Segmenter;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.util.ComponentPanel;
import jasmine.imaging.core.util.ImagePixel;
import jasmine.imaging.core.util.JasmineDeployer;
import jasmine.imaging.core.util.JasmineTab;
import jasmine.imaging.core.util.OptionComponentPanel;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.shapes.ObjectClassifier;
import jasmine.imaging.shapes.SubObjectClassifier;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

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
 * @author Olly Oechsle, University of Essex, Date: 22-Jun-2007
 * @version 1.0
 */
public class JasmineVisionSystemPanel extends JasmineTab implements ActionListener {

    protected JButton test, check;
    public ComponentPanel backgroundSubtracter, segmenter, objectClassifier, subobject_classifier;
    protected JLabel results;

    protected Jasmine j;

    protected JPanel top;

    protected  JComboBox processList;

    public JasmineVisionSystemPanel(final Jasmine j) {

        super(Jasmine.VISION_SYSTEM);

        Border etched = BorderFactory.createEtchedBorder();

        this.j = j;

        setLayout(new BorderLayout());

        test = new JButton("Test");
        test.addActionListener(this);

        check = new JButton("Check");
        check.addActionListener(this);

        //segmenter = new JTextField();
        //classifier = new JTextField();

        backgroundSubtracter = new ComponentPanel("Background Subtraction", ComponentPanel.OUT, "/cut16.png") {
            public void onMouseClicked() {
                browse(this);
            }

            public void onClear() {
                clear(this);
            }
        };


        segmenter = new ComponentPanel("Segmenter", ComponentPanel.OUT, "/edit16.png") {
            public void onMouseClicked() {
                browse(this);
            }

            public void onClear() {
                clear(this);
            }
        };


        objectClassifier = new ComponentPanel("Object Classifier", ComponentPanel.IN, "/object16.png") {
            public void onMouseClicked() {
                browse(this);
            }

            public void onClear() {
                clear(this);
            }
        };

        subobject_classifier = new ComponentPanel("SubObject Classifier", ComponentPanel.IN, "/sub_objects16.png") {
            public void onMouseClicked() {
                browse(this);
            }

            public void onClear() {
                clear(this);
            }
        };

        processList = new JComboBox(VisionSystem.processingNames);
        processList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (j.project != null) {
                    j.project.addProperty(VisionSystem.SHAPE_PROCESSOR_HANDLE, processList.getSelectedIndex());
                }
            }
        });
        processList.setMaximumSize(new Dimension(80, 20));

        top = new JPanel(new GridLayout(1, 2));
        top.setMinimumSize(new Dimension(100, 150));
        top.setPreferredSize(new Dimension(-1, 150));
        top.setBorder(BorderFactory.createTitledBorder(etched, "Components"));

        add(top, BorderLayout.NORTH);

        top.setLayout(new GridLayout(1, 3));
        backgroundSubtracter.setType(ComponentPanel.OUT);
        segmenter.setType(ComponentPanel.BOTH);
        objectClassifier.setType(ComponentPanel.IN);
        top.add(backgroundSubtracter);
        OptionComponentPanel oc = new OptionComponentPanel("Shape Processing", ComponentPanel.BOTH, "/draw24.png");
        oc.add(processList);
        top.add(oc);
        top.add(segmenter);
        top.add(objectClassifier);

        top.add(subobject_classifier);

        JPanel main = new JPanel(new BorderLayout());

        main.setBorder(BorderFactory.createTitledBorder(etched, "Results"));

        results = new JLabel("No results yet.", JLabel.CENTER);

        main.add(new JScrollPane(results), BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        enableTestbutton();

    }

    public void onProjectChanged(JasmineProject project) {
        if (project != null) {
            String existing_segmenter = (String) project.getProperty(VisionSystem.SEGMENTER_HANDLE);
            segmenter.setFile(existing_segmenter);//existing_segmenter == null ? "" : existing_segmenter);
            String existing_classifier = (String) project.getProperty(VisionSystem.OBJECT_CLASSIFIER_HANDLE);
            objectClassifier.setFile(existing_classifier);//(existing_classifier == null ? "" : existing_classifier);
            String existing_bg_subtactor = (String) project.getProperty(VisionSystem.BACKGROUND_SUBTRACTER_HANDLE);
            backgroundSubtracter.setFile(existing_bg_subtactor);
            String existing_subobject_classifier = (String) project.getProperty(VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE);
            subobject_classifier.setFile(existing_subobject_classifier);
            if (project.getProperty(VisionSystem.SHAPE_PROCESSOR_HANDLE) != null)
            processList.setSelectedIndex((Integer) project.getProperty(VisionSystem.SHAPE_PROCESSOR_HANDLE));
            else processList.setSelectedIndex(0);
        } else {
            backgroundSubtracter.setFileNull();
            segmenter.setFileNull();
            objectClassifier.setFileNull();
            subobject_classifier.setFileNull();
            processList.setSelectedIndex(0);
        }
        enableTestbutton();
    }

    public void enableTestbutton() {
        if (segmenter.getFile() != null || objectClassifier.getFile() != null) {
            test.setEnabled(true);
        } else {
            test.setEnabled(false);
        }
    }

    public void test() {
        try {

            StringBuffer buffer = new StringBuffer();
            buffer.append("<html>");
            boolean tested = false;

            if (backgroundSubtracter.getFile() != null) {
                buffer.append("<h2>Background Subtracter</h2>");
                Segmenter s = JasmineDeployer.getSegmenter(backgroundSubtracter.getFile());
                if (s != null) {
                    buffer.append(testBackgroundSubtracter(s));
                }
                tested = true;
            }

            if (segmenter.getFile() != null) {
                buffer.append("<h2>Segmenter</h2>");
                Segmenter s = JasmineDeployer.getSegmenter(segmenter.getFile());
                if (s != null) {
                    buffer.append(testSegmenter(s));
                }
                tested = true;
            }

            if (objectClassifier.getFile() != null) {
                buffer.append("<h2>Object Classifier</h2>");
                ObjectClassifier c = JasmineDeployer.getObjectClassifier(objectClassifier.getFile());
                if (c != null) {
                    buffer.append(c.test(j.project));
                }
                tested = true;
            }

            if (subobject_classifier.getFile() != null) {
                buffer.append("<h2>Sub-Object Classifier</h2>");
                SubObjectClassifier c = JasmineDeployer.getShapeClassifier(subobject_classifier.getFile());
                if (c != null) {
                    buffer.append(c.test(j.project));
                }
                tested = true;
            }

            if (!tested) {
                buffer.append("You haven't set up a segmenter or classifier for the vision system - you cannot test them until you choose one. To create a segmenter or classifier, evolve one or both using Tools > Run GP..., then click File > Save on the GP Evolution Window to save the program to disk.");
            }

            buffer.append("</html>");

            results.setText(buffer.toString());

        } catch (Exception err) {
            j.alert(err.toString());
            err.printStackTrace();
        }
    }

    public String testBackgroundSubtracter(Segmenter s) {
        try {

            Vector<ImagePixel> allPixels = JasmineUtils.getAllPixels(j.project, JasmineClass.MASK);
            PixelLoader.CACHING_OTHERS = false;

            ClassResults results = new ClassResults();
            for (int i = 0; i < j.project.getMaskClasses().size(); i++) {
                JasmineClass c = j.project.getMaskClasses().elementAt(i);
                results.addClass(c.name, c.classID);
            }

            //POEY
            ProgressDialog d = new ProgressDialog("Progress", "The data is being calculated.", allPixels.size());
            
            for (int i = 0; i < allPixels.size(); i++) {
                ImagePixel imagePixel = allPixels.elementAt(i);
                JasmineClass c = j.project.getMaskClass(imagePixel.classID);
                int expected = c.background?0:1;
                //POEY comment: for segment -> jasmine.imaging.core.util.EvolvedSegmentationSingle.java 
                int classID = s.segment(imagePixel.image, imagePixel.x, imagePixel.y);
                if (classID == expected) {
                    //results.addHit(c.classID);
                	//POEY
                	results.addHit(expected+1);
                } else {
                    //results.addMiss(c.classID);
                	//POEY
                	results.addMiss(expected+1);
                }
                
                //POEY
                d.setValue(i + 1);
            }
            //POEY
            d.dispose();

            return results.toHTML();

        } catch (Exception err) {
            System.err.println("// Can't test segmenter on data: " + err.getMessage());
            err.printStackTrace();
            return err.getMessage();
        }

    }

    public String testSegmenter(Segmenter s) {
        try {

            Vector<ImagePixel> allPixels = JasmineUtils.getAllPixels(j.project, JasmineClass.MATERIAL);
            PixelLoader.CACHING_OTHERS = false;

            ClassResults results = new ClassResults();
            for (int i = 0; i < j.project.getMaterialClasses().size(); i++) {
                JasmineClass c = j.project.getMaterialClasses().elementAt(i);
                results.addClass(c.name, c.classID);
            }

            for (int i = 0; i < allPixels.size(); i++) {
                ImagePixel imagePixel = allPixels.elementAt(i);
                int classID = s.segment(imagePixel.image, imagePixel.x, imagePixel.y);
                if (classID == imagePixel.classID) {
                    //results.addHit(classID);
                	//POEY
                	results.addHit(imagePixel.classID);
                } else {
                    //results.addMiss(classID);
                	//POEY
                	results.addMiss(imagePixel.classID);
                }
            }

            return results.toHTML();

        } catch (Exception err) {
            System.err.println("// Can't test segmenter on data: " + err.getMessage());
            err.printStackTrace();
            return err.getMessage();
        }

    }

    public float getTimeForSegmenter(Segmenter s, int iterations) {
        System.out.println(s.getClass().getCanonicalName());
        FastStatistics statistics = new FastStatistics();
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            testSegmenter(s);
            long time = System.currentTimeMillis() - start;
            statistics.addData(time);
            System.out.println(time);
        }
        return statistics.getMean();
    }

    public float getTimeForBackgroundSubtractor(Segmenter s, int iterations) {
        System.out.println(s.getClass().getCanonicalName());
        FastStatistics statistics = new FastStatistics();
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            testBackgroundSubtracter(s);
            long time = System.currentTimeMillis() - start;
            statistics.addData(time);
            System.out.println(time);
        }
        return statistics.getMean();
    }

    public void clear(ComponentPanel source) {
        source.setFileNull();
        if (source == segmenter) {
            j.project.addProperty(VisionSystem.SEGMENTER_HANDLE, null);
        }
        if (source == objectClassifier) {
            j.project.addProperty(VisionSystem.OBJECT_CLASSIFIER_HANDLE, null);
        }
        if (source == backgroundSubtracter) {
            j.project.addProperty(VisionSystem.BACKGROUND_SUBTRACTER_HANDLE, null);
        }
        if (source == subobject_classifier) {
            j.project.addProperty(VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE, null);
        }
    }

    protected JFileChooser chooser;

    public void browse(ComponentPanel source) {
        if (j == null) {
            System.err.println("j is null!");
        }

        if (j.project == null) {
            System.err.println("j.project is null");
        }

        if (chooser == null) {
            if (j.project.getFilename() != null) {
                chooser = new JFileChooser(j.project.getFilename().getParentFile());
            } else {
                chooser = new JFileChooser(j.project.getImageLocation());
            }
        }

        if (source == segmenter) {
            chooser.setDialogTitle("Load Segmenter");
        }

        if (source == backgroundSubtracter) {
            chooser.setDialogTitle("Load Background Segmenter");
        }

        if (source == objectClassifier) {
            chooser.setDialogTitle("Load Classifier");
        }

        if (source == subobject_classifier) {
            chooser.setDialogTitle("Load Sub-Object Classifier");
        }

        if (source.getFile() != null) {
            chooser.setSelectedFile(source.getFile());
        }

        chooser.setFileFilter(JasmineFilters.getClassAndFileFileFilter());

        int result = chooser.showOpenDialog(j);
        if (result == JFileChooser.APPROVE_OPTION) {
            source.setFile(chooser.getSelectedFile());
            if (source == segmenter) {
                j.project.addProperty(VisionSystem.SEGMENTER_HANDLE, chooser.getSelectedFile().getAbsolutePath());
            }

            if (source == objectClassifier) {
                j.project.addProperty(VisionSystem.OBJECT_CLASSIFIER_HANDLE, chooser.getSelectedFile().getAbsolutePath());
            }

            if (source == backgroundSubtracter) {
                j.project.addProperty(VisionSystem.BACKGROUND_SUBTRACTER_HANDLE, chooser.getSelectedFile().getAbsolutePath());
            }

            if (source == subobject_classifier) {
                j.project.addProperty(VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE, chooser.getSelectedFile().getAbsolutePath());
            }

        }
        enableTestbutton();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == test) {
            test();
        }


        if (e.getSource() == check) {

            check();
        }

    }

    public void check() {
        j.project.addProperty(VisionSystem.BACKGROUND_SUBTRACTER_HANDLE, backgroundSubtracter.getAbsolutePath());
        j.project.addProperty(VisionSystem.SEGMENTER_HANDLE, segmenter.getAbsolutePath());
        j.project.addProperty(VisionSystem.OBJECT_CLASSIFIER_HANDLE, objectClassifier.getAbsolutePath());
        j.project.addProperty(VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE, subobject_classifier.getAbsolutePath());

        VisionSystem vs;

        try {
            vs = VisionSystem.load(j.project);
            j.alert("All programs loaded successfully");
        } catch (Exception e) {
            j.alert(e.getMessage());
            return;
        }

        attemptToDeploy("Background Subtracter", VisionSystem.BACKGROUND_SUBTRACTER_HANDLE, vs.backgroundSubtracter, backgroundSubtracter.getFile());
        attemptToDeploy("Segmenter", VisionSystem.SEGMENTER_HANDLE, vs.segmenter, segmenter.getFile());
        attemptToDeploy("Object Classifier", VisionSystem.OBJECT_CLASSIFIER_HANDLE, vs.objectClassifier, objectClassifier.getFile());
        attemptToDeploy("Sub-Object Classifier", VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE, vs.subobjectClassifier, subobject_classifier.getFile());

    }

    public void attemptToDeploy(String name, String handle, Object s, File f) {
        if (s != null) {
            if (s instanceof Deployable) {
                if (j.confirm("The " + name + " uses an interpreted GP tree.\nIt may be possible to make the segmenter more efficient by compiling into Java byte-code.\nShall I try to do this for you?")) {
                    try {
                        File classFile = new JasmineDeployer(j).deploy((Deployable) s, f);

                        if (handle.equals(VisionSystem.SEGMENTER_HANDLE)) {
                            // success - update the project
                            segmenter.setFile(classFile);
                            j.project.addProperty(handle, segmenter.getFile().getAbsolutePath());
                            Segmenter s2 = JasmineDeployer.getSegmenter(classFile);
                            if (j.confirm("The segmenter was compiled successfully! Test to see the difference in speed?")) {
                                // run once to ensure pixel loaders are cached
                                j.setStatusText("Testing...");
                                j.showProgressBar(100);
                                testSegmenter(s2);
                                final int iterations = 20;
                                j.setProgressBarValue(33);
                                float time1 = getTimeForSegmenter((Segmenter) s, iterations);
                                j.setProgressBarValue(66);
                                float time2 = getTimeForSegmenter(s2, iterations);
                                j.alert("Old segmenter: " + time1 + " ms, new segmenter: " + time2 + "ms.");
                                j.setStatusText("Finished testing");
                                j.hideProgressBar();
                            }
                        }

                        if (handle.equals(VisionSystem.BACKGROUND_SUBTRACTER_HANDLE)) {
                            backgroundSubtracter.setFile(classFile);
                            j.project.addProperty(handle, backgroundSubtracter.getAbsolutePath());
                            Segmenter s2 = JasmineDeployer.getSegmenter(classFile);
                            if (j.confirm("The background subtracter was compiled successfully! Test to see the difference in speed?")) {
                                // run once to ensure pixel loaders are cached
                                j.setStatusText("Testing...");
                                j.showProgressBar(100);
                                testBackgroundSubtracter(s2);
                                final int iterations = 20;
                                j.setProgressBarValue(33);
                                float time1 = getTimeForBackgroundSubtractor((Segmenter) s, iterations);
                                j.setProgressBarValue(66);
                                float time2 = getTimeForBackgroundSubtractor(s2, iterations);
                                j.alert("Old segmenter: " + time1 + " ms, new segmenter: " + time2 + "ms.");
                                j.setStatusText("Finished testing");
                                j.hideProgressBar();
                            }
                        }


                        if (handle.equals(VisionSystem.OBJECT_CLASSIFIER_HANDLE)) {
                            objectClassifier.setFile(classFile);
                            j.project.addProperty(handle, objectClassifier.getAbsolutePath());
                            j.alert("The compiled object classifier was created successfully.");
                        }

                        if (handle.equals(VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE)) {
                            subobject_classifier.setFile(classFile);
                            j.project.addProperty(handle, subobject_classifier.getAbsolutePath());
                            j.alert("The compiled sub-object classifier was created successfully.");
                        }

                    } catch (Exception err) {
                        j.alert("Could not compile the program. Sorry.\n" + err.toString());
                        err.printStackTrace();
                    }
                }
            }
        }
    }

}
