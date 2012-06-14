package jasmine.imaging.core;


import jasmine.imaging.commons.ConvolutionMatrix;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.util.ConvolveMenuItem;
import jasmine.imaging.core.util.EdgeMenuItem;
import jasmine.imaging.core.util.FilterMenuItem;
import jasmine.imaging.core.util.RecentProjectMenuItem;
import jasmine.imaging.core.util.RecentProjects;
import jasmine.imaging.core.util.saveoutput.SaveOutput;
import jasmine.imaging.core.util.wizard.classification.ClassificationWizard;
import jasmine.imaging.core.util.wizard.segmentation.SegmentationWizard;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * @author Olly Oechsle, University of Essex, Date: 21-Jun-2007
 * @version 1.0
 */
public class JasmineMenus extends JMenuBar implements ActionListener {

    // MENUS
    JMenuItem file_add_images;
    JMenuItem file_add_images_from_camera;
    JMenuItem file_save_overlay;
    JMenuItem file_load_overlay;

    JMenuItem file_new_project;
    JMenuItem file_open_project;
    JMenuItem file_save_project;
    JMenuItem file_save_project_as;
    JMenuItem file_close_project;
    
    //POEY
    JMenuItem clear_recent_project;

    JMenuItem filter_preprocess;
    JMenuItem filter_restore;

    JMenuItem file_export, file_import;

    JMenuItem file_import_images;
    JMenuItem file_export_images;

    JMenuItem file_import_classes;
    JMenuItem file_export_classes;

    JMenuItem file_import_shapes;
    JMenuItem file_export_shapes;
    JMenuItem file_export_shapes_as_images;
    JMenuItem file_export_normalisation_coefficients;

    JMenuItem file_export_pixels;

    JMenuItem file_exit;

    protected JMenuItem edit_clear, edit_delete_shape;
    protected JCheckBoxMenuItem view_magnifier;
    protected JCheckBoxMenuItem view_classbox;
    protected JCheckBoxMenuItem view_toolbox;
    protected JCheckBoxMenuItem view_image_browser;
    
    //POEY
    protected JCheckBoxMenuItem view_testing_image_browser;
    
    protected JCheckBoxMenuItem view_shape_stats;
    protected JCheckBoxMenuItem view_webcam;
    protected JMenuItem view_arrange_windows;
    protected JMenuItem view_zoomin, view_zoomout;

    JMenuItem classes_add;
    JMenuItem classes_clear;
    JMenuItem classes_next;
    JMenuItem classes_edit;

    JMenuItem tools_getstats;
    JMenuItem tools_correlate;
    JMenuItem tools_feature_similarity;
    JMenuItem tools_feature_distribution;
    JMenuItem tools_feature_selection;

    JMenuItem images_next;
    JMenuItem images_prev;
    JMenuItem edit_delete_image;
    JMenuItem clear_images;
    JMenuItem clear_testing_images;
    JMenuItem clear_image_overlays;

    JMenuItem run_gp_background_subtraction;
    JMenuItem run_gp_segmentation;
    JMenuItem run_gp_object_classification;
    JMenuItem run_gp_sub_object_classification;

    JMenuItem test_background_subtracter;
    JMenuItem test_segmenter;
    JMenuItem test_classification;
    
    //POEY
    JMenuItem test_classification_testing;
    JMenuItem test_classification_testing_all;
    
    JMenuItem eval_classification;

    JMenuItem about, debug, tutorial;

    private JCheckBoxMenuItem view_image;
    private JMenuItem allshapes_clear;
    private JMenuItem edit_project;

    JMenu file, edit, modeMenu, view, classMenu, imageMenu, tools, run;

    public Jasmine j;

    class JasmineMenuItem extends JMenuItem {

        public JasmineMenuItem(String name) {
            this(name, null);
        }

        public JasmineMenuItem(String name, String icon) {
            super(name);
            addActionListener(JasmineMenus.this);
            if (icon != null) {
               try {
                    setIcon(new ImageIcon(getClass().getResource(icon)));
                } catch (Exception e) {
                }
            }
        }

    }

    public JasmineMenus(Jasmine j) {

        this.j = j;

        file = new JMenu("File");
        file.setMnemonic('f');

        file_new_project = new JasmineMenuItem("New Project...", "/new16.png");
        file_new_project.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        file_open_project = new JasmineMenuItem("Open Project...", "/open16.png");
        file_open_project.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        final JMenu mnuRecent = new JMenu("Recent Projects");
        final RecentProjects recent = (RecentProjects) j.settings.getProperty(Jasmine.SETTING_RECENT_PROJECTS);
        if (recent != null && recent.size() > 0) {
            for (int i = 0; i < recent.projects.size(); i++) {
                File project = recent.projects.elementAt(i);
                if (project.exists()) {
                    mnuRecent.add(new RecentProjectMenuItem(j, project));
                }
            }
            mnuRecent.addSeparator();
            JMenuItem clear = new JMenuItem("Clear");
            clear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    recent.projects.clear();
                    mnuRecent.removeAll();
                }
            });
            mnuRecent.add(clear);
        }
        
        clear_recent_project = new JasmineMenuItem("Clear Recent Project...", "/clear16.png");
        clear_recent_project.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recent.projects.clear();
                mnuRecent.removeAll();
            }
        });
        

        file_save_project = new JasmineMenuItem("Save Project", "/save16.png");
        file_save_project.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        file_save_project_as = new JasmineMenuItem("Save Project As...");
        file_save_project.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        file_close_project = new JasmineMenuItem("Close Project");

        file_add_images = new JasmineMenuItem("Add Images...", "/add16.png");
        file_add_images.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));

        file_add_images_from_camera = new JasmineMenuItem("Capture Images...", "/webcam16.png");
        file_add_images_from_camera.setEnabled(false);
        file_add_images_from_camera.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        file_save_overlay = new JasmineMenuItem("Save Overlay");
        file_save_overlay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK));
        file_save_overlay.setEnabled(false);

        file_load_overlay = new JasmineMenuItem("Load Overlay");
        file_load_overlay.setEnabled(false);

        file_import_classes = new JasmineMenuItem("Classes...");

        file_import_images = new JasmineMenuItem("Images...");

        file_import_shapes = new JasmineMenuItem("Shapes...");

        file_export_classes = new JasmineMenuItem("Classes...");

        file_export_images = new JasmineMenuItem("Images...");

        file_export_shapes = new JasmineMenuItem("Shapes...");

        file_export_pixels = new JasmineMenuItem("Pixel Features...");

        file_export_shapes_as_images = new JasmineMenuItem("Shape Images...");

        file_export_normalisation_coefficients = new JasmineMenuItem("Normalisation Coefficients...");

        file_exit = new JasmineMenuItem("Quit Jasmine");
        file_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));

        file.add(file_new_project);
        file.add(file_open_project);

        if (mnuRecent != null) 
        file.add(mnuRecent);
        
        file.add(clear_recent_project);

        file.add(file_save_project);
        file.add(file_save_project_as);
        file.add(file_close_project);

        file.addSeparator();

        file.add(file_add_images);
        file.add(file_add_images_from_camera);

        file.addSeparator();

        file.add(file_save_overlay);
        file.add(file_load_overlay);

        file_import = new JMenu("Import");

        file_import.add(file_import_classes);
        file_import.add(file_import_images);
        file_import.add(file_import_shapes);

        file.add(file_import);

        file_export = new JMenu("Export");
        try {
            file_export.setIcon(new ImageIcon(getClass().getResource( "/dataset16.png")));
        } catch (Exception e) {}

        file_export.add(file_export_classes);
        file_export.add(file_export_images);
        file_export.add(file_export_shapes);
        file_export.add(file_export_pixels);
        file_export.add(file_export_shapes_as_images);
        file_export.add(file_export_normalisation_coefficients);

        file.add(file_export);

        file.addSeparator();

        file.add(file_exit);

        add(file);

        // EDIT MENU

        edit = new JMenu("Edit");
        edit.setMnemonic('e');

        edit_clear = new JasmineMenuItem("Clear", "/clear16.png");
        edit_clear.setEnabled(false);

        edit_delete_shape = new JasmineMenuItem("Delete Shape");
        edit_delete_shape.setEnabled(false);

        edit_delete_image = new JasmineMenuItem("Delete Image", "/delete_image16.png");

        //mcs: As modeMenu is commented out, I am commenting its menu items too!
        //modeMenu = new JMenu("Preset Solutions");

        //modeMenu.add(new ModeMenuItem(j, "Pasta", new PastaSegmenter(), new PastaClassifier(), true));
        //modeMenu.add(new ModeMenuItem(j, "Skin Lesions", new MelanomaSegmenter(), new MelanomaClassifier()));
        //modeMenu.add(new ModeMenuItem(j, "ANPR (OCR)", new ANPRSegmenter(), new VotingClassifier()));
        //modeMenu.add(new ModeMenuItem(j, "Gestures", new GestureSegmenter(), new GestureClassifier2()));
        //modeMenu.add(new ModeMenuItem(j, "Alphabet", new AlphabetSegmenter(), new AlphabetClassifier()));
        //modeMenu.add(new ModeMenuItem(j, "Number Plates", new NumberplateSegmenter(), new NumberplateClassifier()));
        //modeMenu.add(new ModeMenuItem(j, "Red Eye", new RedEyeSegmenter(), new RedEyeClassifier()));

        edit_project = new JasmineMenuItem("Project Settings...");

        edit.add(edit_delete_shape);
        edit.add(edit_delete_image);
        edit.add(edit_clear);
        //edit.add(modeMenu);
        edit.addSeparator();
        edit.add(edit_project);

        add(edit);

        // VIEW MENU

        view = new JMenu("View");
        view.setMnemonic('v');

        view_classbox = new JCheckBoxMenuItem("Classes Window");
        try {
            view_classbox.setIcon(new ImageIcon(getClass().getResource("/class16.png")));
        } catch (Exception e) {}
        view_classbox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        view_classbox.addActionListener(this);

        view_image_browser = new JCheckBoxMenuItem("Image Browser");
        try {
            view_image_browser.setIcon(new ImageIcon(getClass().getResource("/image16.png")));
        } catch (Exception e) {}
        view_image_browser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        view_image_browser.addActionListener(this);

        view_zoomin = new JasmineMenuItem("Zoom In", "/zoom_in16.png");
        //view_zoomin.setAccelerator(KeyStroke.getKeyStroke('+'));
        view_zoomin.addActionListener(this);
        view_zoomout = new JasmineMenuItem("Zoom Out", "/zoom_out16.png");
        //view_zoomin.setAccelerator(KeyStroke.getKeyStroke('-'));
        view_zoomout.addActionListener(this);

        view_shape_stats = new JCheckBoxMenuItem("Object Information");
        view_shape_stats.addActionListener(this);

        view_image = new JCheckBoxMenuItem("Darker Image");
        view_image.setSelected(!j.displayImage);
        view_image.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        view_image.addActionListener(this);
        view_image.setToolTipText("Shows a darker version of the image which may help you to see the class overlay more clearly");

        view_webcam = new JCheckBoxMenuItem("Camera...");
        try {
            view_webcam.setIcon(new ImageIcon(getClass().getResource("/webcam16.png")));
        } catch (Exception e) {}

        view_webcam.setSelected(false);
        view_webcam.addActionListener(this);

        view_arrange_windows = new JasmineMenuItem("Arrange Windows");
        view_arrange_windows.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        view.add(view_image_browser);
        view.add(view_classbox);
        view.add(view_shape_stats);
        view.add(view_webcam);
        view.addSeparator();
        view.add(view_zoomin);
        view.add(view_zoomout);
        view.addSeparator();
        view.add(view_image);
        view.addSeparator();
        view.add(view_arrange_windows);

        add(view);

        // FILTER MENU

        JMenu filter = new JMenu("Filter");

        filter_preprocess = new JMenuItem("Preprocess");
        filter_preprocess.addActionListener(this);
        filter_restore = new JMenuItem("Restore");
        filter_restore.addActionListener(this);

        filter.add(filter_preprocess);
        filter.add(filter_restore);
        filter.addSeparator();

        ConvolveMenuItem convolve_hsobel = new ConvolveMenuItem(this, "Sobel (Horizontal)", ConvolutionMatrix.HORIZONTAL_SOBEL);
        filter.add(convolve_hsobel);

        ConvolveMenuItem convolve_vsobel = new ConvolveMenuItem(this, "Sobel (Vertical)", ConvolutionMatrix.VERTICAL_SOBEL);
        filter.add(convolve_vsobel);

        EdgeMenuItem edge = new EdgeMenuItem(this, "Edge Detection");
        filter.add(edge);

        ConvolveMenuItem convolve_laplacian = new ConvolveMenuItem(this, "Laplacian", ConvolutionMatrix.LAPLACIAN);
        filter.add(convolve_laplacian);

        ConvolveMenuItem convolve_gaussian = new ConvolveMenuItem(this, "Gaussian Blur", ConvolutionMatrix.GAUSSIAN);
        filter.add(convolve_gaussian);

        ConvolveMenuItem convolve_average = new ConvolveMenuItem(this, "Average", ConvolutionMatrix.MEAN);
        filter.add(convolve_average);

        ConvolveMenuItem convolve_m10 = new ConvolveMenuItem(this, "m10 (Emboss Top)", ConvolutionMatrix.M10);
        filter.add(convolve_m10);

        ConvolveMenuItem convolve_m01 = new ConvolveMenuItem(this, "m01 (Emboss Left)", ConvolutionMatrix.M01);
        filter.add(convolve_m01);

        filter.addSeparator();

        FilterMenuItem filter_contrast = new FilterMenuItem(this, "Contrast", PixelLoader.HARALICK_CONTRAST);
        filter.add(filter_contrast);

        FilterMenuItem filter_dissimilarity = new FilterMenuItem(this, "Dissimilarity", PixelLoader.HARALICK_DISSIMILARITY);
        filter.add(filter_dissimilarity);

        FilterMenuItem filter_uniformity = new FilterMenuItem(this, "Uniformity", PixelLoader.HARALICK_UNIFORMITY);
        filter.add(filter_uniformity);

        FilterMenuItem filter_entropy = new FilterMenuItem(this, "Entropy", PixelLoader.HARALICK_ENTROPY);
        filter.add(filter_entropy);

        FilterMenuItem filter_maximumprobability = new FilterMenuItem(this, "Maximum Probability", PixelLoader.HARALICK_MAXIMUM_PROBABILITY);
        filter.add(filter_maximumprobability);

        filter.addSeparator();

        FilterMenuItem variance = new FilterMenuItem(this, "Variance", PixelLoader.VARIANCE);
        filter.add(variance);


        tools = new JMenu("Tools");



        tools_getstats = new JasmineMenuItem("Class Statistics...", "/statistics16.png");

        tools_correlate = new JasmineMenuItem("Feature Correlations...");

        tools_feature_similarity = new JasmineMenuItem("Feature Similarity...");

        tools_feature_distribution = new JasmineMenuItem("Feature Distribution...", "distribution16.png");

        tools_feature_selection = new JasmineMenuItem("Feature Selection/Extraction", "/filter16.png");

        tools.add(tools_getstats);
        tools.add(tools_feature_selection);
        tools.addSeparator();
        tools.add(tools_correlate);
        tools.add(tools_feature_similarity);
        tools.add(tools_feature_distribution);
        tools.add(filter);

        // CLASS MENU

        classMenu = new JMenu("Classes");
        classMenu.setMnemonic('c');

        classes_add = new JasmineMenuItem("Add Class...", "/add16.png");
        classes_add.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        classes_edit = new JasmineMenuItem("Edit Class", "/edit16.png");

        classes_next = new JasmineMenuItem("Next Class", "/forward_arrow16.png");
        classes_next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, ActionEvent.CTRL_MASK));

        classes_clear = new JasmineMenuItem("Remove All Classes", "/delete_class16.png");

        allshapes_clear = new JasmineMenuItem("Remove All Shapes from All Images");

        classMenu.add(classes_add);
        classMenu.add(classes_edit);
        classMenu.add(classes_next);
        classMenu.addSeparator();
        classMenu.add(classes_clear);
        classMenu.add(allshapes_clear);


        add(classMenu);

        // IMAGE MENU

        imageMenu = new JMenu("Images");
        imageMenu.setMnemonic('i');

        images_next = new JasmineMenuItem("Next Image", "/forward_arrow16.png");

        images_prev = new JasmineMenuItem("Previous Image", "/back_arrow16.png");

        clear_images = new JasmineMenuItem("Clear All Training Images", "/delete_image16.png");
        
        //POEY
        clear_testing_images = new JasmineMenuItem("Clear All Testing Images", "/delete_image16.png");

        clear_image_overlays = new JasmineMenuItem("Clear Overlays", "/clear16.png");

        imageMenu.add(images_prev);
        imageMenu.add(images_next);
        imageMenu.addSeparator();
        imageMenu.add(clear_images);
        imageMenu.add(clear_image_overlays);
        
        //POEY
        imageMenu.add(clear_testing_images);

        add(imageMenu);

        add(tools);

        // RUN MENU

        run = new JMenu("Evolve");

        run_gp_background_subtraction = new JasmineMenuItem("Background Subtracter...", "/cut16.png");

        run_gp_segmentation = new JasmineMenuItem("Material/Colour Segmenter...", "/colours16.png");

        run_gp_object_classification = new JasmineMenuItem("Object Classifier...", "/object16.png");

        run_gp_sub_object_classification = new JasmineMenuItem("Sub-Object Classifier...", "/sub_objects16.png");

        test_background_subtracter = new JasmineMenuItem("Test Background Subtracter");
        test_segmenter = new JasmineMenuItem("Test Segmenter");
        test_classification = new JasmineMenuItem("Test Vision System", "/ok16.png");
        test_classification.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        
        //POEY
        test_classification_testing = new JasmineMenuItem("Test Vision System (Testing Set: individual)", "/ok16.png");
        test_classification_testing_all = new JasmineMenuItem("Test Vision System (Testing Set: all)", "/ok16.png");
        
        eval_classification = new JasmineMenuItem("Evaluate Classifier");

        run.add(run_gp_background_subtraction);
        run.add(run_gp_segmentation);
        run.add(run_gp_object_classification);
        run.add(run_gp_sub_object_classification);

        tools.addSeparator();
        tools.add(test_background_subtracter);
        tools.add(test_segmenter);
        tools.add(eval_classification);
        tools.add(test_classification);
        
        //POEY
        tools.add(test_classification_testing);
        tools.add(test_classification_testing_all);

        add(run);

        JMenu help = new JMenu("Help");

        about = new JasmineMenuItem("About", "/vase16.png");

        tutorial = new JasmineMenuItem("Tutorial");

        debug = new JasmineMenuItem("View Console", "/console16.png");

        help.add(about);
        help.add(tutorial);
        help.addSeparator();
        help.add(debug);

        add(help);

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == view_classbox) j.classbox.setVisible(!j.classbox.isVisible());
        if (e.getSource() == view_image_browser) j.imageBrowser.setVisible(!j.imageBrowser.isVisible());
        
        //POEY
        if (e.getSource() == view_testing_image_browser) j.testingImageBrowser.setVisible(!j.testingImageBrowser.isVisible());

        if (e.getSource() == view_shape_stats) {
            j.viewShapeStats();
            return;
        }

        if (e.getSource() == view_webcam) {
            j.showWebcam();
            return;
        }

        if (e.getSource() == view_zoomin) {
            j.getCurrentPanel().zoomIn();
        }

        if (e.getSource() == view_zoomout) {
            j.getCurrentPanel().zoomOut();
        }

        if (e.getSource() == view_image) {
            j.toggleDisplayImage();
            return;
        }

        if (e.getSource() instanceof JCheckBoxMenuItem) {
            updateViewMenus();
            return;
        }

        if (e.getSource() instanceof ConvolveMenuItem) {
            ((ConvolveMenuItem) e.getSource()).displayConvolvedImage();
            return;
        }

        if (e.getSource() instanceof FilterMenuItem) {
            ((FilterMenuItem) e.getSource()).displayProcessedImage();
            return;
        }

        if (e.getSource() instanceof EdgeMenuItem) {
            ((EdgeMenuItem) e.getSource()).displayConvolvedImage();
            return;
        }

        if (e.getSource() == file_new_project) {
            j.newProject();
            return;
        }

        if (e.getSource() == file_open_project) {
            j.openProject();
            return;
        }

        if (e.getSource() == file_save_project) {
            j.saveProject();
            return;
        }

        if (e.getSource() == file_save_project_as) {
            j.saveProjectAs();
            return;
        }

        if (e.getSource() == file_close_project) {
            j.closeProject();
            return;
        }

        if (e.getSource() == file_add_images_from_camera) {
            j.captureFromWebcam();
            return;
        }

        if (e.getSource() == file_add_images) {
            j.addImage();
            return;
        }

        if (e.getSource() == file_load_overlay) {
            j.loadOverlay();
            return;
        }

        if (e.getSource() == file_save_overlay) {
            j.saveOverlay();
            return;
        }

        if (e.getSource() == file_import_classes) {
            j.importClasses();
            return;
        }

        if (e.getSource() == file_import_images) {
            j.importImages();
            return;
        }

        if (e.getSource() == file_import_shapes) {
            j.importShapes();
            return;
        }

        if (e.getSource() == file_export_classes) {
            j.exportClasses();
            return;
        }

        if (e.getSource() == file_export_images) {
            j.exportImages();
            return;
        }

        if (e.getSource() == file_export_shapes) {
            j.exportShapes();
            return;
        }

        if (e.getSource() == file_export_shapes_as_images) {
            j.exportShapesAsImages();
        }

        if (e.getSource() == file_export_normalisation_coefficients) {
            j.exportNormalisationCoefficients();
            return;
        }

        if (e.getSource() == file_export_pixels) {
            j.exportPixels();
            return;
        }

        if (e.getSource() == file_exit) {
            j.exit();
            return;
        }

        if (e.getSource() == edit_clear) {
            j.clear();
            return;
        }

        if (e.getSource() == edit_delete_shape) {
            j.classificationPanel.deleteSelectedShape();
            return;
        }

        if (e.getSource() == edit_project) {
            j.editProjectSettings();
            return;
        }

        if (e.getSource() == classes_add) {
            j.addClass();
            return;
        }

        if (e.getSource() == classes_edit) {
            j.editClass();
            return;
        }

        if (e.getSource() == classes_next) {
            j.classbox.next();
            return;
        }

        if (e.getSource() == allshapes_clear) {
            j.clearAllShapes();
            return;
        }

        if (e.getSource() == classes_clear) {
            j.clearAllClasses();
            return;
        }

        if (e.getSource() == view_arrange_windows) {
            j.arrangeWindows();
            System.out.println("Rearranging menus");
        }

        if (e.getSource() == tools_getstats) {
            j.displayClassStats();
            return;
        }

        if (e.getSource() == tools_correlate) {
            j.displayCorrelation();
            return;
        }

        if (e.getSource() == tools_feature_similarity) {
            j.displayFeatureSimilarity();
            return;
        }


        if (e.getSource() == tools_feature_selection) {
            j.openFeatureSelectionDialog();
        }

        if (e.getSource() == tools_feature_distribution) {
            j.displayFeatureDistributions();
        }

        if (e.getSource() == images_next) {
            j.nextImage();
            return;
        }

        if (e.getSource() == images_prev) {
            j.prevImage();
            return;
        }

        if (e.getSource() == edit_delete_image) {
            if (j.imageBrowser != null) {
                j.imageBrowser.deleteSelected();
            } else {
                j.alert("Cannot delete: image browser not open");
            }            
            return;
        }

        if (e.getSource() == clear_images) {
            j.clearAllImages();
            return;
        }
        
        //POEY
        if (e.getSource() == clear_testing_images) {
            j.clearAllTestingImages();
            return;
        }

        if (e.getSource() == clear_image_overlays) {
            j.clearImageOverlays();
            return;
        }

        if (e.getSource() == run_gp_background_subtraction) {
            j.ensureOverlaySavedOK();
            if (!JasmineFeatureSelectionDialog.hasDoneFeatureSelection(j.project, JasmineClass.MASK)) {
                 if (j.confirm("Would you like to select appropriate features first? Click no to proceed using the default ones.")) {
                     j.openFeatureSelectionDialog(JasmineClass.MASK, true);
                     return;
                 }
            }
            new SegmentationWizard(j, JasmineClass.MASK);
            return;
        }


        if (e.getSource() == run_gp_segmentation) {
            j.ensureOverlaySavedOK();
                if (!JasmineFeatureSelectionDialog.hasDoneFeatureSelection(j.project, JasmineClass.MATERIAL)) {
                     if (j.confirm("Would you like to select appropriate features first? Click no to proceed using the default ones.")) {
                         j.openFeatureSelectionDialog(JasmineClass.MATERIAL, false);
                         return;
                     }
                }
            new SegmentationWizard(j, JasmineClass.MATERIAL);
            return;
        }

        if (e.getSource() == run_gp_object_classification) {
            if (JasmineUtils.countLabelledObjects(j.project) == 0) {
                j.alert("No objects have been labelled yet.");
            } else {
                if (!JasmineFeatureSelectionDialog.hasDoneFeatureSelection(j.project, JasmineClass.OBJECT)) {
                     if (j.confirm("Would you like to select appropriate features first? Click no to proceed using the default ones.")) {
                         j.openFeatureSelectionDialog(JasmineClass.OBJECT, false);
                         return;
                     }
                }
                new ClassificationWizard(j, JasmineClass.OBJECT);
            }
            return;
        }

        if (e.getSource() == run_gp_sub_object_classification) {
            if (JasmineUtils.countLabelledSubObjects(j.project) == 0) {
                j.alert("No sub-objects have been labelled yet.");
            } else {
                if (!JasmineFeatureSelectionDialog.hasDoneFeatureSelection(j.project, JasmineClass.SUB_OBJECT)) {
                     if (j.confirm("Would you like to select appropriate features first? Click no to proceed using the default ones.")) {
                         j.openFeatureSelectionDialog(JasmineClass.SUB_OBJECT, false);
                         return;
                     }
                }
                new ClassificationWizard(j, JasmineClass.SUB_OBJECT);
            }
            return;
        }

        if (e.getSource() == test_background_subtracter)  {
            j.testBackgroundSegmenterOnImage();
            return;
        }

        if (e.getSource() == test_segmenter) {
            j.testSegmenterOnImage();
            return;
        }

        if (e.getSource() == test_classification) {
            j.testClassificationOnImage();
            return;
        }
        
        //POEY
        if (e.getSource() == test_classification_testing) {
            j.testClassificationOnTestingImage();
            return;
        }
        
      //POEY
        if (e.getSource() == test_classification_testing_all) {
            j.testClassificationOnTestingImageAll();
            return;
        }

        if (e.getSource() == eval_classification) {
            j.evaluateClassifier();
            return;
        }

        if (e.getSource() == about) {
            j.showAbout();
            return;
        }

        if (e.getSource() == tutorial) {
            openURL("http://vase.essex.ac.uk/software/jasmine/tutorial.shtml");
        }

        if (e.getSource() == debug) {
            SaveOutput.getOutputWindow(j);
        }

        if (e.getSource() == filter_preprocess) {
            j.preprocess();
        }

        if (e.getSource() == filter_restore) {
            j.restore();
        }

    }

    public void openURL(String url) {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {

            j.alert("Please visit: " + url);
        }

        try {

            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        }
        catch (Exception e) {

            System.err.println(e.getMessage());
        }
    }

    public void updateViewMenus() {
        view_classbox.setSelected(j.classbox.isVisible());
        view_image_browser.setSelected(j.imageBrowser.isVisible());               
        view_shape_stats.setSelected(j.shapeStats != null && j.shapeStats.isVisible());
    }

    public void updateSaveMenu() {
        file_save_project.setEnabled(j.project != null && j.project.getFilename() != null);
    }

    public void enableMenus() {
        if (j.project != null) {
            j.toolbox.setEnabled(true);
/*            images_next.setEnabled(project.hasNextImage());
            images_prev.setEnabled(project.hasPrevImage());
            zoomIn.setEnabled(true);
            zoomOut.setEnabled(true);*/
            updateSaveMenu();
            file_save_project_as.setEnabled(true);
            file_close_project.setEnabled(true);
            file_add_images.setEnabled(true);
            file_load_overlay.setEnabled(true);
            file_import.setEnabled(true);
            file_export.setEnabled(true);
            edit.setEnabled(true);
            view.setEnabled(true);
            tools.setEnabled(true);
            classMenu.setEnabled(true);
            imageMenu.setEnabled(true);
            run.setEnabled(true);

        } else {
            j.toolbox.setEnabled(false);
/*            zoomIn.setEnabled(false);
            zoomOut.setEnabled(false);*/
            file_save_project.setEnabled(false);
            file_save_project_as.setEnabled(false);
            file_close_project.setEnabled(false);
            file_add_images.setEnabled(false);
            file_import.setEnabled(false);
            file_export.setEnabled(false);
            file_load_overlay.setEnabled(false);
            file_save_overlay.setEnabled(false);
            edit.setEnabled(false);
            view.setEnabled(false);
            classMenu.setEnabled(false);
            imageMenu.setEnabled(false);
/*          images_next.setEnabled(false);
            images_prev.setEnabled(false);*/
            run.setEnabled(false);
            tools.setEnabled(false);
        }
/*        next.setEnabled(images_next.isEnabled());
        prev.setEnabled(images_prev.isEnabled());
        addclass.setEnabled(classMenu.isEnabled());*/
    }

}
