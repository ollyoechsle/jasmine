package jasmine.imaging.core;


import jasmine.classify.ICSListener;
import jasmine.classify.ICSListenerGraphical;
import jasmine.classify.classifier.Classifier;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.GraphicalListener;
import jasmine.gp.multiclass.ClassResult;
import jasmine.gp.tree.Terminal;
import jasmine.gp.util.GPStartDialog;
import jasmine.imaging.commons.AccuracyStatistics;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.Segmenter;
import jasmine.imaging.commons.WebcamGrabber;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.classification.JasmineGP;
import jasmine.imaging.core.classification.JasmineGPObject;
import jasmine.imaging.core.classification.JasmineICS;
import jasmine.imaging.core.classification.JasmineICSObject;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblem;
import jasmine.imaging.core.util.AddImage;
import jasmine.imaging.core.util.AddTestingImage;
import jasmine.imaging.core.util.EvolvedGPObjectClassifier;
import jasmine.imaging.core.util.EvolvedGPSubObjectClassifier;
import jasmine.imaging.core.util.EvolvedICSObjectClassifier;
import jasmine.imaging.core.util.EvolvedICSSubObjectClassifier;
import jasmine.imaging.core.util.JasmineDeployer;
import jasmine.imaging.core.util.JasmineTab;
import jasmine.imaging.core.util.ModeMenuItem;
import jasmine.imaging.core.util.RecentProjects;
import jasmine.imaging.core.util.TrainingObject;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.core.visionsystem.VisionSystemGUI;
import jasmine.imaging.core.visionsystem.VisionSystemGUITesting;
import jasmine.imaging.core.visionsystem.VisionSystemGUITestingAll;
import jasmine.imaging.core.visionsystem.VisionSystemListener;
import jasmine.imaging.gp.problems.SubGenerationalProblem;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SubObjectClassifier;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Random;

/**
 * Jasmine - A - SegMented - Image - Notation - Environment
 * J - A - S - M - I - N - E
 * <p/>
 * Jasmine is a GUI to load a series of images and notate them graphically with classes that you choose.
 * Jasmine allows you to notate different kinds of information; you may "draw" classes onto images for
 * segmentation problems, and then do further work with the results.
 * Jasmine is a test interface only and suffers from a number of bugs and is missing many features, some
 * of which are summarised below.
 * <p/>
 * BUG LIST
 * 1. FIXED --- Can't load one project after another
 * 2. FIXED --- Shape and pixel classes are confused
 * 3. FIXED --- Closing toolbox doesn't change the menu's ticked state
 * 4. FIXED --- Unique class counter doesn't work when adding shape classes
 * 5. FIXED --- If no pixel classes on project load, class window appears as a square
 * 6. FIXED --- When saving project, extension is .jpg, when it should be .jasmine
 * 7. FIXED --- Switching to shape mode doesn't update the classbox
 * 8. FIXED --- Can't click on shapes to set their class in shape mode
 * 9. FIXED --- Save overlay makes overlay disappear.
 * 10.FIXED --- Shape Selection doesn't work in zoom mode.
 * 11.FIXED --- Remove all images causes NPE
 * 13.FIXED --- Cursor goes odd on large sizes (and doesn't work on Windows)
 * <p/>
 * <p/>
 * DESIRABLE ADDITIONAL FEATURES
 * 1. DONE --- Class window should be a JList, not a series of buttons.
 * 2. DONE --- Delete selected shape
 * 3. DONE --- Modify class
 * 5. DONE --- Remove all classes works only for pixel classes
 * 6. DONE --- No remove single class option.
 * 7. DONE --- Save shapes with project for GP
 * 8. DONE --- Compile and run java code automatically.
 * 9. DONE --- Add overlay from existing file
 * 10. DONE --- Choose how many generations should the GP run for (and other GP settings)
 * 11. DONE --- Add multiple images at once
 * 12. DONE --- If overlay exists for an image, add it automatically.
 * 13. DONE --- Edit project settings - namely change directory
 * 14. DONE --- New project ensures that directory exists first.
 * 16. DONE --- Test on webcam capture, add images direct from webcam
 * 17. DONE --- Scroll bars on large images
 * 18. DONE --- Full export and import functions
 * 19. DONE - Right click for erase in paint mode
 * 20. DONE - Can now draw lines as well as freehand painting.
 *
 * @author Olly Oechsle, University of Essex, Date: 11-Dec-2006 - 24-March-2009
 * @version 0.99.4
 */
public class Jasmine extends JFrame implements ItemListener, VisionSystemListener {

    public static final String APP_NAME = "Jasmine Vision System Builder";
    public static final String VERSION = "1.3.41";
    public static String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator") + "JasmineProjects";
    public static final File DEFAULT_PROJECT_LOCATION_FILE = new File(DEFAULT_PROJECT_LOCATION);
    public static final String ONLINE_JAR_FILE_LOCATION = "http://vase.essex.ac.uk/software/jasmine/lib/";

    public static final String SETTING_RECENT_PROJECTS = "setting_recent_projects";
    public static final String SETTING_DEFAULT_PROJECT_LOCATION = "setting_project_location";

    protected boolean displayImage = true;

    // the modes of operation
    public static final int PIXEL_SEGMENTATION = 0;
    public static final int OBJECT_CLASSIFICATION = 1;
    public static final int FEATURE_EXTRACTION = 2;
    public static final int VISION_SYSTEM = 3;
    public static final int OTHER = 4;

    public static Random getRandom() {
        return new Random(2357);
    }
    

    /**
     * What general mode are we in - pixel segmentation or shape classification
     */
    public int mode = PIXEL_SEGMENTATION;

    /**
     * Within pixel classification we may be painting, erasing or viewing histograms.
     */
    public JButton segmentationMode = null;
    public JButton classificationMode = null;

    public JButton PAINT, LINE, ERASE, TARGET, HISTOGRAM;

    // DIALOGS AND FRAMES
    public JasmineSegmentationPanel segmentationPanel;
    public JasmineClassificationPanel classificationPanel;
    public JasmineAbstractEditorPanel currentPanel;

    public JasmineClassBox classbox;
    public JasmineToolBox toolbox;
    public JasmineImageBrowser imageBrowser;
    
    //POEY
    public JasmineTestingImageBrowser testingImageBrowser;
    
    public DialogDisplayStats displayStats;
    public DialogShapeStats shapeStats;
    public WebcamGrabber webcam;

    // saves settings
    protected JasmineSettings settings;

    // OTHER SWING ITEMS:
    public JLabel mousePosition, status;
    //public JComboBox classList;
    public JButton next, prev, addclass, zoomIn, zoomOut, capture;
    private JProgressBar progressBar;
    public static JFileChooser fc;

    // THE IMAGE WE ARE USING AT THE MOMENT
    public JasmineImage currentImage;
    
    //POEY
    public JasmineTestingImage currentTestingImage;

    // THE PROJECT WE ARE WORKING ON
    public JasmineProject project = null;

    public ModeMenuItem currentlySelected = null;

    protected JTabbedPane tabs;

    protected JasmineMenus menus;

    public JasmineVisionSystemPanel visionSystemPanel;
    //public JasmineFeatureSelectionPanel featureSelectionPanel;

    public static Jasmine currentInstance;

    /**
     * Loads the GUI and initialises the Jasmine program.
     */
    public Jasmine(String[] args) {
               
        // set the title and general size of the frame
        super(APP_NAME);
        this.setSize(800, 600);
        //this.setLocation(50, 50);
        currentInstance = this;

        settings = JasmineSettings.load();

        updateDefaultProjectLocation(null);

        try {
            setIconImage(new ImageIcon(getClass().getResource("/vase16.png")).getImage());
        } catch (Exception e) {
        }

        System.out.println("Loaded Jasmine OK");

        // use the system look and feel where possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel");
        }

        // initialise the file chooser
        fc = new JFileChooser(DEFAULT_PROJECT_LOCATION_FILE);
        
        // create the dialogs and position them
        imageBrowser = new JasmineImageBrowser(this);
        imageBrowser.setSize(220, 240);

        //POEY
        testingImageBrowser = new JasmineTestingImageBrowser(this);
        testingImageBrowser.setSize(220, 240);

        classbox = new JasmineClassBox(this);
        classbox.setSize(220, 240);

        // set up the main panel
        segmentationPanel = new JasmineSegmentationPanel(this);

        //featureSelectionPanel = new JasmineFeatureSelectionPanel(this);

        classificationPanel = new JasmineClassificationPanel(this);

        visionSystemPanel = new JasmineVisionSystemPanel(this);

        status = new JLabel();
        status.setPreferredSize(new Dimension(400, 20));
        setStatusText(null);

        mousePosition = new JLabel();
        mousePosition.setPreferredSize(new Dimension(100, 20));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setVisible(false);

        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
        statusBar.add(Box.createHorizontalStrut(5));
        statusBar.add(status);
        statusBar.add(mousePosition);
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(progressBar);
        statusBar.add(Box.createHorizontalStrut(5));
        statusBar.setPreferredSize(new Dimension(800, 20));

        tabs = new JTabbedPane();
        ImageIcon sicon = new ImageIcon(getClass().getResource("/pencil16.png"));
        ImageIcon cicon = new ImageIcon(getClass().getResource("/classify16.png"));
        ImageIcon vicon = new ImageIcon(getClass().getResource("/settings16.png"));

        tabs.addTab("Segmentation", sicon, new JScrollPane(segmentationPanel.getPanel()));
        //tabs.addTab("Feature Selection", new JScrollPane(featureSelectionPanel));
        tabs.addTab("Classification", cicon, new JScrollPane(classificationPanel.getPanel()));
        tabs.addTab("Vision System", vicon, visionSystemPanel);

        tabs.setEnabled(false);

        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Component tab = tabs.getSelectedComponent();
                if (tab instanceof JScrollPane) {
                    tab = ((JScrollPane) tab).getViewport().getView();
                }
                if (tab instanceof JasmineTab) {
                    setMode(((JasmineTab) tab).getMode());
                }
            }
        });

        toolbox = new JasmineToolBox(this);

        Container c = getContentPane();

        c.add(toolbox, BorderLayout.NORTH);
        c.add(tabs, BorderLayout.CENTER);
        c.add(statusBar, BorderLayout.SOUTH);

        // CREATE THE MENUS HERE
        menus = new JasmineMenus(this);
        setJMenuBar(menus);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        menus.enableMenus();
        this.setVisible(true);
        arrangeWindows();

        // parse through the args
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-p")) {
                System.out.println("\nOpening project: " + args[i + 1]);
                openProject(new File(DEFAULT_PROJECT_LOCATION_FILE, args[i + 1]));
                i++;
            }
            if (arg.equals("-s")) {
                System.out.println("Setting random seed: " + args[i + 1]);
                Evolve.seed = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

 

    }

    int gap = 15;

    public void arrangeWindows() {

        int x = (int) this.getLocation().getX() + this.getWidth() + gap;
        int y = (int) this.getLocation().getY();

        imageBrowser.setLocation(x, y);

        //POEY
        int xx = x + imageBrowser.getWidth() + gap;
        testingImageBrowser.setLocation(xx, y);
 
        y += imageBrowser.getHeight() + gap;
        
        classbox.setLocation(x, y);
    }


    /**
     * Sets the mode, either classification or segmentation
     *
     * @param mode
     */
    public void setMode(int mode) {
        this.mode = mode;
        if (mode == PIXEL_SEGMENTATION) {
            this.currentPanel = this.segmentationPanel;
            this.segmentationPanel.setCursorSize(toolbox.size.getValue());
        } else {
            this.currentPanel = this.classificationPanel;
        }
        if (mode > OBJECT_CLASSIFICATION) {
            mousePosition.setVisible(false);
        } else {
            mousePosition.setVisible(true);
        }
        /*if (mode == FEATURE_EXTRACTION) {
            featureSelectionPanel.update(project);
        }*/
        this.currentPanel.repaint();
        this.classbox.refresh();
        this.toolbox.showButtons(mode);
    }

    /**
     * Sets the status text
     *
     * @param message A message to put, or null to automatically set the message.
     */
    public void setStatusText(final String message) {
        if (message != null) System.out.println(message);
        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                if (message == null) {
                    if (project == null) {
                        status.setText(" Please create or open a project");
                    } else {
                        if (project.getImages().size() == 0) {
                            status.setText(" Click File > Add Images... to add images to the project");
                        } else {
                            if (mode == PIXEL_SEGMENTATION) {
                                if (project.getPixelClasses(segmentationPanel.mode).size() == 0) {
                                    status.setText(" Click Classes > Add Class to add pixel classes");
                                }
                            }
                            if (mode == OBJECT_CLASSIFICATION) {
                                if (project.getObjectClasses().size() == 0) {
                                    status.setText(" Click Classes > Add Class to add shape classes");
                                }
                            }
                        }
                    }
                } else {
                    status.setText(" " + message);
                }
            }
        });
    }

    public void hideProgressBar() {
        progressBar.setValue(0);
        progressBar.setVisible(false);
    }

    public void showProgressBar(final int max) {
        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                progressBar.setVisible(true);
                progressBar.setMaximum(max);
            }
        });
    }

    public void setProgressBarValue(final int n) {
        progressBar.setValue(n);
    }


    public void captureFromWebcam() {
        if (webcam != null) {
            PixelLoader p = webcam.grab();
            int i = 1;
            while (true) {
                File f = new File(project.getImageLocation(), "webcam" + i + ".png");
                if (!f.exists()) {
                    try {
                        p.saveAs(f);
                        JasmineImage image = new JasmineImage(f.getName(), -1, TrainingObject.TRAINING);
                        project.addImage(image);
                        loadJasmineImage(image);
                        imageBrowser.refresh();
                    } catch (Exception err) {
                        alert("Could not save captured image: " + err.toString());
                        err.printStackTrace();
                    }
                    break;
                }
                i++;
            }
        } else {
            alert("No cameras in use.\nSelect View > Camera to start using one.");
        }
    }


    public void addImage() {
        new AddImage(this, project.getImageLocation());
    }
    
    //POEY
    public void addTestingImage() {
        new AddTestingImage(this, project.getTestingImageLocation());
    }

    public void addImage(File[] files) {

        for (File file1 : files) {

            try {

                JasmineImage image = new JasmineImage(file1.getName(), -1, TrainingObject.TRAINING);
                if (project.getImages().contains(image)) {
                    // TODO: Add equals and hashcode to Jasmine Image class.
                    return;
                }

                // check if there's a material overlay
                File overlay = new File(project.getImageLocation(), OverlayData.getOverlayFilename(image, JasmineClass.MATERIAL));
                if (overlay.exists()) {
                    image.materialOverlayFilename = overlay.getAbsolutePath();
                }

                // check if there's a mask overlay
                overlay = new File(project.getImageLocation(), OverlayData.getOverlayFilename(image, JasmineClass.MASK));
                if (overlay.exists()) {
                    image.maskOverlayFilename = overlay.getAbsolutePath();
                }

                project.addImage(image);
                loadJasmineImage(image);

            } catch (Exception e) {

                alert("Could not add image " + file1.getName() + "\n" + e.getMessage());
                e.printStackTrace();
            }

        }

        menus.enableMenus();
        imageBrowser.refresh();

    }

    //POEY
    public void addTestingImage(File[] files) {

        for (File file1 : files) {

            try {
            	//POEY?
                JasmineTestingImage image = new JasmineTestingImage(file1.getName(), -1, TrainingObject.TESTING);
                if (project.getTestingImages().contains(image)) {              	
                    // TODO: Add equals and hashcode to Jasmine Image class.
                    return;
                }
/*
                // check if there's a material overlay
                File overlay = new File(project.getTestingImageLocation(), OverlayData.getOverlayFilename(image, JasmineClass.MATERIAL));
                if (overlay.exists()) {
                    image.materialOverlayFilename = overlay.getAbsolutePath();
                }

                // check if there's a mask overlay
                overlay = new File(project.getTestingImageLocation(), OverlayData.getOverlayFilename(image, JasmineClass.MASK));
                if (overlay.exists()) {
                    image.maskOverlayFilename = overlay.getAbsolutePath();
                }
*/
                project.addTestingImage(image);
                loadJasmineTestingImage(image);

            } catch (Exception e) {

                alert("Could not add image " + file1.getName() + "\n" + e.getMessage());
                e.printStackTrace();
            }

        }

        menus.enableMenus();
        testingImageBrowser.refresh();

    }

    public void viewShapeStats() {
        if (shapeStats == null) {
            shapeStats = new DialogShapeStats(this);
            if (getClassMode() == JasmineClass.OBJECT) {
                shapeStats.displayStats(classificationPanel.selectedObject);
            } else {
                shapeStats.displayStats(classificationPanel.selectedSubObject);
            }
        } else {
            shapeStats.setVisible(!this.shapeStats.isVisible());
        }
    }

    public void showWebcam() {
        if (webcam == null) {
            try {
                webcam = new WebcamGrabber();
                Point p = this.getLocation();
                int x = (int) p.getX() + gap + getWidth();
                int y = (int) p.getY() + gap + getHeight();
                webcam.window.setLocation(x, y);
                menus.view_webcam.setSelected(true);
                menus.file_add_images_from_camera.setEnabled(true);
            } catch (Exception err) {
                err.printStackTrace();
                alert(err.toString());
            }
        } else {
            webcam.window.setVisible(true);
            webcam.window.requestFocus();
        }

    }

    public void hideWebcam() {
        webcam.window.dispose();
        menus.file_add_images_from_camera.setEnabled(false);
        webcam = null;
        menus.view_webcam.setSelected(false);
    }


    public void loadOverlay() {
        if (currentImage != null) {
            if (segmentationPanel.mode == JasmineClass.MATERIAL) {
                if (project.getMaterialClasses().size() == 0) {
                    alert("Can't import material overlay - you haven't specified any material classes yet");
                } else {
                    fc.setCurrentDirectory(project.getImageLocation());
                    fc.setFileFilter(JasmineFilters.getOverlayFilter());

                    int returnVal = fc.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        currentImage.materialOverlayFilename = fc.getSelectedFile().getName();
                        project.setChanged(true, "New overlay loaded.");
                        imageBrowser.refresh();
                        loadJasmineImage(currentImage);
                    }
                }
            } else {
                if (project.getMaskClasses().size() == 0) {
                    alert("Can't import mask overlay - you haven't specified any classes yet");
                } else {
                    fc.setCurrentDirectory(project.getImageLocation());
                    fc.setFileFilter(JasmineFilters.getMaskFilter());

                    int returnVal = fc.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        currentImage.maskOverlayFilename = fc.getSelectedFile().getName();
                        project.setChanged(true, "New mask loaded.");
                        imageBrowser.refresh();
                        loadJasmineImage(currentImage);
                    }
                }
            }
        } else {
            alert("Can't load overlay - no image selected");
        }
    }

    public void saveOverlay() {
        segmentationPanel.saveOverlay(project.currentImage());
    }

    public void importClasses() {
        File file = getImportFilename("Import Classes");
        if (file == null) return;
        try {
            String message = Importer.importClasses(project, file);
            classbox.init(project.getMaterialClasses(), true);
            imageBrowser.refresh();
            // update the information on the current image
            if (currentPanel.getImage() != null) setImageInfo(project.currentImage());
            if (message != null) {
                alert(message);
            }
        } catch (IOException e1) {
            alert("Cannot load file: " + e1.getMessage());
        } catch (RuntimeException e2) {
            alert("Unexpected field in CSV file - did you load the right one? " + e2.getMessage());
        }
    }

    public void importImages() {
        File file = getImportFilename("Import Images");
        if (file == null) return;

        try {
            int importCount = Importer.importImages(project, file);
            menus.enableMenus();
            imageBrowser.refresh();
            loadJasmineImage(project.currentImage());
            alert("Imported " + importCount + " images.");
            setStatusText("Imported " + importCount + " images.");
        } catch (IOException e1) {
            alert("Cannot load file: " + e1.getMessage());
        } catch (RuntimeException e2) {
            alert("Unexpected field in CSV file - did you load the right one? " + e2.getMessage());
        }
    }

    public void importShapes() {
        File file = getImportFilename("Import Shapes");
        if (file == null) return;

        try {
            int importCount = Importer.importShapes(project, file);
            imageBrowser.refresh();
            loadJasmineImage(project.currentImage());
            alert("Imported " + importCount + " shapes.");
            setStatusText("Imported " + importCount + " shapes.");
        } catch (IOException e1) {
            alert("Cannot load file: " + e1.getMessage());
        } catch (Exception e2) {
            alert("Unexpected field in CSV file - did you load the right one? " + e2.getMessage());
        }
    }

    public void exportClasses() {
        File file = getExportFilename("Export Classes", project.getName() + "_classes.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportClasses(project, file);
            setStatusText("Exported classes to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }

    public void exportImages() {
        File file = getExportFilename("Export Images", project.getName() + "_images.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportImages(project, file);
            setStatusText("Exported images to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }

    public void exportShapes() {
        File file = getExportFilename("Export Shapes", project.getName() + "_shapes.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportShapes(project, file);
            setStatusText("Exported shapes to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }

    public void exportShapesAsImages() {
        File file = getExportDirectory("Export Shapes as Images");
        if (file == null) return;
        try {
            Exporter.exportShapesAsImages(project, file);
            setStatusText("Exported shape images to " + file.getName() + "/");
        } catch (IOException e1) {
            alert("Cannot save files: " + e1.getMessage());
        }
    }

    public void exportShapeFeatures(Vector<Terminal> terminals) {
        File file = getExportFilename("Export Sub-Object Features", project.getName() + "_sub_objects.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportShapeFeatures(this, project, file, terminals);
            setStatusText("Exported shape features to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }

    public void exportObjectFeatures(Vector<Terminal> terminals) {
        File file = getExportFilename("Export Object Features", project.getName() + "_objects.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportObjectFeatures(this, project, file, terminals);
            setStatusText("Exported shape features to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }
    
    //POEY
    public void exportTestingObjectFeatures(Vector<Terminal> terminals) {
        File file = getExportFilename("Export Testing Object Features", project.getName() + "_testingobjects.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportTestingObjectFeatures(this, project, file, terminals);
            setStatusText("Exported shape features of the testing set to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }

    public void exportNormalisationCoefficients() {
        File file = getExportFilename("Export Normalisation Coefficients", project.getName() + "_norms.csv", ".csv");
        if (file == null) return;
        try {
            Exporter.exportNormalisationCoefficients(project, file);
            setStatusText("Exported normalisation coefficients to " + file.getName());
        } catch (IOException e1) {
            alert("Cannot save file: " + e1.getMessage());
        }
    }

    public void exportPixels() {
        exportPixels(null);
    }

    public void exportPixels(Vector<Terminal> terminals) {
        //File file = getExportFilename("Export Pixel Features", project.getName() + "_pixel_features.csv", ".csv");
        //if (file == null) return;
        try {
            //boolean testing = !confirm("Do you want this to be training data (uses approximately 5% of all the data)");
            Exporter.exportPixelFeatures(this, project, terminals, JasmineClass.MATERIAL);
        } catch (IOException err) {
            alert("Cannot export pixels: " + err.getMessage());
            err.printStackTrace();
        }
    }

    public void clear() {
        currentPanel.clear();
        currentPanel.repaint();
    }

    private File getImportFilename(String title) {
        fc.setDialogTitle(title);
        fc.setFileFilter(JasmineFilters.getCSVFilter());

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    // changes the file chooser directory to the project directory, but only
    // once, in case the user needs to work on another directory a lot.
    private boolean setSelectedFile = false;

    public File getExportFilename(String title, String suggestedFilename, String extension) {
        fc.setDialogTitle(title);
        fc.setFileFilter(JasmineFilters.getCSVFilter());
        if (!setSelectedFile) {
            fc.setSelectedFile(new File(DEFAULT_PROJECT_LOCATION_FILE, suggestedFilename));
            setSelectedFile = true;
        }
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (!fc.getSelectedFile().getName().endsWith(extension)) {
                return new File(fc.getSelectedFile().getParentFile(), fc.getSelectedFile().getName() + extension);
            }
            return fc.getSelectedFile();
        }
        return null;
    }

    private File getExportDirectory(String title) {
        fc.setDialogTitle(title);
        fc.setFileFilter(null);
        fc.setSelectedFile(DEFAULT_PROJECT_LOCATION_FILE);
        int savedMode = fc.getFileSelectionMode();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        fc.setFileSelectionMode(savedMode);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    public int getClassMode() {
        switch (mode) {
            case PIXEL_SEGMENTATION:
                return segmentationPanel.mode;
            case OBJECT_CLASSIFICATION:
                return classificationPanel.mode;
        }
        return -1;
    }

    public void addClass() {
        new DialogClassEntry(this, null, getClassMode());
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (mode == PIXEL_SEGMENTATION) {
                JasmineClass c = (JasmineClass) e.getItem();
                currentImage.setClassID(c.classID);
                alert("Class of " + currentImage.filename + " is now " + c.name);
                imageBrowser.refresh();
                setImageInfo(currentImage);
            } else {
                JasmineClass c = (JasmineClass) e.getItem();
                currentImage.setClassID(c.classID);
                alert("Class of selected shape is now " + c.name);
            }
        }
    }

    public void editClass() {
        if (classbox.getCurrentClass() == null) {
            alert("No class selected");
        } else {
            new DialogClassEntry(this, classbox.getCurrentClass(), -1);
        }
    }

    public void clearAllShapes() {
        if (confirm("Are you sure you want to clear all shapes?")) {
            for (int i = 0; i < project.getImages().size(); i++) {
                JasmineImage jasmineImage = project.getImages().elementAt(i);
                jasmineImage.clearObjects();
                imageBrowser.refresh();
                loadJasmineImage(jasmineImage);
            }
        }
    }

    public void clearAllClasses() {
        if (confirm("Are you sure you want to clear all " + JasmineClass.getTypeName(getClassMode()) + " classes?")) {

            int mode = getClassMode();

            Vector<JasmineClass> toRemove = new Vector<JasmineClass>(10);
            for (int i = 0; i < project.classes.size(); i++) {
                JasmineClass jasmineClass = project.classes.elementAt(i);
                if (jasmineClass.type == mode) toRemove.add(jasmineClass);
            }
            project.classes.removeAll(toRemove);
            project.setChanged(true, "Removed " + toRemove.size() + " classes");
            classbox.refresh();
            imageBrowser.refresh();
            // update the information on the current image
            if (currentPanel.getImage() != null) setImageInfo(project.currentImage());
        }
    }

    public void displayClassStats() {
        if (displayStats == null) {
            displayStats = new DialogDisplayStats(this, project, getClassMode());
        } else {
            if (displayStats.mode != mode) {
                displayStats.dispose();
                displayStats = new DialogDisplayStats(this, project, getClassMode());
            } else {
                displayStats.setVisible(true);
                displayStats.requestFocus();
            }
        }
    }

    public void nextImage() {
        if (ensureOverlaySavedOK()) {
            project.moveNext();
            loadJasmineImage(project.currentImage());
            menus.enableMenus();
        }
    }

    public void prevImage() {
        if (ensureOverlaySavedOK()) {
            project.movePrev();
            loadJasmineImage(project.currentImage());
            menus.enableMenus();
        }
    }
    
    //POEY
    public void nextTestingImage() {
        project.moveNextTesting();
        loadJasmineTestingImage(project.currentTestingImage());
    }
    
    
    //POEY
    public boolean firstTestingImage() {    	
    	return project.checkFirstTestingImage();
    }
    
    //POEY
    public boolean firstImage() {    	
    	return project.checkFirstImage();
    }

    public void deleteImage(Object[] selected) {
        if (currentImage == null) {
            alert("No image selected");
        } else {
            String message;
            if (selected.length == 1) {
                message = "Are you sure you want to remove this image from the project?";
            } else {
                message = "Are you sure you want to remove these " + selected.length + " images from the project?";
            }
            if (confirm(message)) {
                for (Object selectedImage : selected) {
                    if (selectedImage instanceof JasmineImage) {
                        project.getImages().remove(selectedImage);
                    }
                }
                //project.getImages().remove(currentImage);
                loadJasmineImage(project.currentImage());
                imageBrowser.refresh();
                menus.enableMenus();
            }
        }
    }
    
    //POEY
    public void deleteTestingImage(Object[] selected) {
        if (currentTestingImage == null) {
            alert("No image selected");
        } else {
            String message;
            if (selected.length == 1) {
                message = "Are you sure you want to remove this image from the project?";
            } else {
                message = "Are you sure you want to remove these " + selected.length + " images from the project?";
            }
            if (confirm(message)) {
                for (Object selectedImage : selected) {
                    if (selectedImage instanceof JasmineTestingImage) {
                        project.getTestingImages().remove(selectedImage);
                    }
                }
                //project.getImages().remove(currentImage);
                loadJasmineTestingImage(project.currentTestingImage());
                testingImageBrowser.refresh();
                menus.enableMenus();
            }
        }
    }

    public void clearAllImages() {
        if (confirm("Are you sure you want to clear all images?")) {
            project.getImages().clear();
            loadJasmineImage(null);
            imageBrowser.refresh();
            menus.enableMenus();
        }
    }
    
    //POEY
    public void clearAllTestingImages() {
        if (confirm("Are you sure you want to clear all testing images?")) {
            project.getTestingImages().clear();
            loadJasmineTestingImage(null);
            testingImageBrowser.refresh();
            menus.enableMenus();
        }
    }

    public void clearImageOverlays() {
        if (confirm("Are you sure you want to clear all image overlays?")) {
            Vector<JasmineImage> images = project.getImages();
            for (int i = 0; i < images.size(); i++) {
                JasmineImage jasmineImage = images.elementAt(i);
                jasmineImage.materialOverlayFilename = null;
            }
            imageBrowser.refresh();
        }
    }

    public void runGPSegmentation(final JasmineSegmentationProblem problem, boolean startDialog, final int mode) {

        ensureOverlaySavedOK();

        GraphicalListener g = new GraphicalListener() {

            JFileChooser chooser;

            public void saveIndividual(Individual ind) {

                if (chooser == null) {
                    chooser = new JFileChooser(DEFAULT_PROJECT_LOCATION);
                }

                try {
                    chooser.setDialogTitle("Save Segmenter");
                    chooser.setFileFilter(JasmineFilters.getClassAndFileFileFilter());
                    String filename;
                    if (mode == JasmineClass.MATERIAL) {
                        filename = project.getName() + "-segmenter.solution";
                    } else {
                        filename = project.getName() + "-background-subtracter.solution";
                    }
                    if (project.getFilename() != null) {
                        chooser.setSelectedFile(new File(project.getFilename().getParent(), filename));
                    } else {
                        chooser.setSelectedFile(new File(DEFAULT_PROJECT_LOCATION, filename));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int r = chooser.showSaveDialog(window);
                if (r == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) { 
                    	//POEY comment: save a segmentation solution file
                        problem.saveSegmenter(Jasmine.this, e, f);
                    }
                }

            }

        };

        if (startDialog) {	//POEY comment: go to this case when a user chooses a listbox Advanced Settings       	
        	//POEY comment: GP parameter setting
            new GPStartDialog(this, problem, g).setLocation(50, 50);
        } else {       	
            new Evolve(problem, g).start();
        }

    }

    public void runICSClassification(final JasmineICS p, boolean startDialog) {

        ICSListener l = new ICSListenerGraphical() {

             JFileChooser chooser;

            public void saveIndividual() {

                if (getBestIndividual() == null) return;
                if (chooser == null) {
                    chooser = new JFileChooser(DEFAULT_PROJECT_LOCATION);
                }

                try {
                    chooser.setFileFilter(JasmineFilters.getClassAndFileFileFilter());
                    String filename;
                    if (p instanceof JasmineICSObject) {
                        chooser.setDialogTitle("Save Object Classifier");
                        filename = project.getName() + "-object-classifier.solution";
                    } else {
                        chooser.setDialogTitle("Save Sub-Object Classifier");
                        filename = project.getName() + "-subobject-classifier.solution";
                    }
                    if (project.getFilename() != null) {
                        chooser.setSelectedFile(new File(project.getFilename().getParentFile(), filename));
                    } else {
                        chooser.setSelectedFile(new File(DEFAULT_PROJECT_LOCATION, filename));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int r = chooser.showSaveDialog(window);
                if (r == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        Classifier c = getBestIndividual();
                        p.ensureTerminalMetaDataKnowsTerminals(c);
                        if (p instanceof JasmineICSObject) {
                            new EvolvedICSObjectClassifier(c).save(f);
                        } else {
                            new EvolvedICSSubObjectClassifier(c).save(f);
                        }
                    }
                }

            }

        };

        p.addListener(l);

        new Thread() {
            public void run() {
                p.run();
            }
        }.start();


    }

    public void runGPClassification(JasmineGP p, boolean startDialog) {

        GraphicalListener g = new GraphicalListener() {

            JFileChooser chooser;

            public void saveIndividual() {

                if (getBestIndividual() == null) return;
                if (chooser == null) {
                    chooser = new JFileChooser(DEFAULT_PROJECT_LOCATION);
                }

                try {
                    chooser.setFileFilter(JasmineFilters.getClassAndFileFileFilter());
                    chooser.setDialogTitle("Save Sub-Object Classifier");
                    String filename;
                    if (p instanceof JasmineGPObject) {
                        filename = project.getName() + "-object-classifier.solution";
                    } else {
                        filename = project.getName() + "-subobject-classifier.solution";
                    }
                    if (project.getFilename() != null) {
                        chooser.setSelectedFile(new File(project.getFilename().getParent(), filename));
                    } else {
                        chooser.setSelectedFile(new File(DEFAULT_PROJECT_LOCATION, filename));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int r = chooser.showSaveDialog(window);
                if (r == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        if (p instanceof JasmineGPObject) {
                            new EvolvedGPObjectClassifier(getBestIndividual()).save(f);
                        } else {
                            new EvolvedGPSubObjectClassifier(getBestIndividual()).save(f);
                        }
                    }
                }

            }

        };

        if (startDialog) {
            new GPStartDialog(this, p, g).setLocation(50, 50);
        } else {
            new Evolve(p, g).start();
        }

    }

    public void runSGGPShapeClassification() {
        SubGenerationalProblem p = new SubGenerationalProblem(project, null, false);
        new GPStartDialog(this, p, new GraphicalListener());
    }

    public void testClassificationOnImage()  {
        if (currentImage != null) {
            //new DialogRun(this, project, currentImage, DialogRun.MODE_SEGMENT_AND_CLASSIFY);
            try {
                VisionSystemGUI g = new VisionSystemGUI(this, VisionSystem.load(project));
                g.processImage(getCurrentImage());
            } catch (Exception e) {
                alert(e.toString());
                e.printStackTrace();
            }
        } else {
            alert("No image selected. You'll need to add at least one to your project first.");
        }
    }
    
    //POEY
    public void testClassificationOnTestingImage()  {
    	if(project.getTestingImages().size()!=0) {
    		try {
                VisionSystemGUITesting g = new VisionSystemGUITesting(this, VisionSystem.load(project));
                g.processImage(getCurrentTestingImage());
            } catch (Exception e) {
                alert(e.toString());
                e.printStackTrace();
            }
	    } else {
	        alert("No testing images. You'll need to add at least one to your project first.");
	    }
    }
    
    //POEY write testing results to a file
    public void testClassificationOnTestingImageAll()  {
    	if(project.getTestingImages().size()==0){
    		alert("No testing images. You'll need to add at least one to your project first.");      	
    	}
    	else {
            try {
            	
            	if(!firstTestingImage()) 
            		project.setCursorTesting(0);

            	//save results to a file
            	File file = getExportFilename("Export Testing Results", project.getName() + "_testing_result.csv", ".csv");
                if (file == null) return;
                try {              	
                    //Exporter.exportObjectFeatures(this, project, file, terminals);
                	BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    setStatusText("Exported testing results to " + file.getName());
                    
                    //Show a progress bar
                	ProgressDialog d = new ProgressDialog("Classification Progress", "Please wait...", project.getTestingImages().size());
                	int[] total = new int[1], correct = new int[1];
    	    		for(int i=0;i<project.getTestingImages().size();i++,nextTestingImage()) {	    			
    		            try {
    		                VisionSystemGUITestingAll g = new VisionSystemGUITestingAll(this, VisionSystem.load(project));
    		                System.out.print(project.currentTestingImage().getFilename());
    		                bw.write(project.currentTestingImage().getFilename());
    		                g.processImageWrite(getCurrentTestingImage(),bw,total,correct);    		                
    		                //Show a progress bar : but doesn't work now
    			            d.setValue(i + 1);
    		            } catch (Exception e) {
    		                alert(e.toString());
    		                e.printStackTrace();
    		            }		            
    		            
    	    		} 
    	    		System.out.println("Total="+total[0]+" Correct="+correct[0]+" Accuracy result="+((float)correct[0]/total[0]*100)+"%");
    	    		bw.write("\nTotal,"+total[0]+",Correct,"+correct[0]+"\nAccuracy result,"+((float)correct[0]/total[0]*100)+"%\n");
    	    		bw.close();
    	    		//Dispose the progress bar
    	    		d.dispose();
    	    		
	    			} catch (FileNotFoundException e1) {
	    				// TODO Auto-generated catch block
	    				e1.printStackTrace();
	    			}  		        
	    			                     
                } catch (IOException e1) {
                    alert("Cannot save file: " + e1.getMessage());
                }
            	
            	
	    }
    }

    public void testSegmenterOnImage() {
        if (currentImage != null) {
            new DialogRun(this, project, currentImage, DialogRun.MODE_SEGMENT_ONLY, VisionSystem.SEGMENTER_HANDLE);
        } else {
            alert("No image selected. You'll need to add at least one to your project first.");
        }
    }

    public void testBackgroundSegmenterOnImage() {
        if (currentImage != null) {
            new DialogRun(this, project, currentImage, DialogRun.MODE_SEGMENT_ONLY, VisionSystem.BACKGROUND_SUBTRACTER_HANDLE);
        } else {
            alert("No image selected. You'll need to add at least one to your project first.");
        }
    }

    public void evaluateClassifier() {
        if (project != null) {
            // tests the classifier on all SHAPE instances in the project
            new DialogClassifierEvaluator(this);
        } else {
            alert("Please create or open a project first");
        }
    }

    public void showWindows() {
        classbox.setVisible(true);
        toolbox.setVisible(true);
        imageBrowser.setVisible(true);
        
        //POEY
        testingImageBrowser.setVisible(true);
        
        arrangeWindows();
        menus.updateViewMenus();
    }

    public void newProject() {
        if (ensureProjectSavedOK()) {
            new NewProject(this, null);
        }
    }

    public void editProjectSettings() {
        new NewProject(this, project);
    }

    public void toggleDisplayImage() {
        currentPanel.toggleDisplayImage();
    }

    public JasmineAbstractEditorPanel getCurrentPanel() {
        return currentPanel;
    }

    public void setTitle() {
        if (project == null) {
            setTitle(APP_NAME);
        } else {
            setTitle(project.getName() + " - " + APP_NAME);
        }

    }

    //public void newProject(String projectName, File imageLocation) {
    public void newProject(String projectName, File imageLocation, File testingImageLocation) {
        //project = new JasmineProject(projectName, imageLocation);
    	
    	//POEY
    	project = new JasmineProject(projectName, imageLocation, testingImageLocation);
    	
        project.setImageLocation(imageLocation);
        
        //POEY
        project.setTestingImageLocation(testingImageLocation);
        
        menus.enableMenus();
        setTitle();
        showWindows();
        setStatusText(null);
        visionSystemPanel.onProjectChanged(project);
        classbox.refresh();
        segmentationPanel.setMode(JasmineClass.MASK);
    }

    public void editProject(String projectName, File imageLocation, File testingImageLocation) {
        project.setName(projectName);
        project.setImageLocation(imageLocation);
        
        //POEY
        project.setTestingImageLocation(testingImageLocation);
        
        setTitle();
        setStatusText(null);
    }

    public void closeProject() {

        if (ensureOverlaySavedOK()) {
            if (ensureProjectSavedOK()) {

                project = null;
                currentImage = null;
                
                //POEY
                currentTestingImage = null;
                
                classbox.setVisible(false);
                toolbox.setEnabled(false);
                imageBrowser.setVisible(false);
                
                //POEY
                testingImageBrowser.setVisible(false);
                
                if (shapeStats != null) shapeStats.setVisible(false);
                loadJasmineImage(null);
                
                //POEY
                loadJasmineTestingImage(null);
                
                setTitle();
                setStatusText(null);
                visionSystemPanel.onProjectChanged(project);

                tabs.setEnabled(false);

            }
        }

    }

    public void openProject() {

        if (project != null) {
            if (!ensureProjectSavedOK()) return;
            closeProject();
        }

        project = null;
        currentImage = null;
        
        //POEY
        currentTestingImage = null;

        fc.setDialogTitle("Open Project");
        fc.setFileFilter(JasmineFilters.getJasmineFilter());

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //POEY comment: file is a path of the project
            openProject(file);
        }

    }

    public void addRecentProject(File file) {
            RecentProjects projects = (RecentProjects) settings.getProperty(SETTING_RECENT_PROJECTS);
            if (projects == null) {
                projects = new RecentProjects();
                settings.addProperty(SETTING_RECENT_PROJECTS, projects);
            }
            projects.add(file);
            settings.save();
    }

    public void openProject(File file) {
    	
        setStatusText("Please wait, loading project...");

        tabs.setEnabled(true);

        try {

            JasmineProject p = JasmineProject.load(file);

            // save to recent projects
            addRecentProject(file);
            updateDefaultProjectLocation(file);

            // check that the directory where the images should be still exists
            File f = p.getImageLocation();
            if (!f.exists()) {
                alert("The image location for this project does not exist.\nThis may be because you've loaded this project on a different computer.\nPress OK to select a new folder.");
                while (true) {
                    fc.setDialogTitle("Choose Image Location");
                    fc.setFileFilter(JasmineFilters.getImageFilter());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION && fc.getSelectedFile().isDirectory()) {
                        p.setImageLocation(fc.getSelectedFile());
                    } else {
                        // don't load project.
                        setStatusText("Could not load the project.");
                        return;
                    }

                    // check that all the images exist
                    Vector<JasmineImage> failures = new Vector<JasmineImage>();                   
                    for (int i = 0; i < p.getImages().size(); i++) {
                        JasmineImage jasmineImage = p.getImages().elementAt(i);
                        File image = new File(p.getImageLocation(), jasmineImage.filename);
                        if (!image.exists()) {
                            failures.add(jasmineImage);
                        }
                    }

                    if (failures.size() > 0) {
                        if (confirm(failures.size() + " ( of " + p.getImages().size() + " ) images could not be found in this folder. Proceed?\nClick Yes to open the project without these files, or click No to choose another folder.")) {
                            // remove images
                            p.getImages().removeAll(failures);
                            break;
                        }
                    } else {
                        alert("Success! All training images found.");
                        break;
                    }

                }
            }

            //POEY not be solved
            File ft = p.getTestingImageLocation();
            if (!ft.exists()) {
                alert("The testing image location for this project does not exist.\nThis may be because you've loaded this project on a different computer.\nPress OK to select a new folder.");
                while (true) {
                    fc.setDialogTitle("Choose Testing Image Location");
                    fc.setFileFilter(JasmineFilters.getImageFilter());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION && fc.getSelectedFile().isDirectory()) {
                        p.setTestingImageLocation(fc.getSelectedFile());
                    } else {
                        // don't load project.
                        setStatusText("Could not load the project.");
                        return;
                    }

                    // check that all the images exist
                    Vector<JasmineTestingImage> failurest = new Vector<JasmineTestingImage>();
                    for (int i = 0; i < p.getTestingImages().size(); i++) {
                    	JasmineTestingImage jasmineTestingImage = p.getTestingImages().elementAt(i);                       
                        File image = new File(p.getTestingImageLocation(), jasmineTestingImage.filenameTesting);
                        if (!image.exists()) {
                            failurest.add(jasmineTestingImage);
                        }
                    }
                    if (failurest.size() > 0) {
                        if (confirm(failurest.size() + " ( of " + p.getTestingImages().size() + " ) testing images could not be found in this folder. Proceed?\nClick Yes to open the project without these files, or click No to choose another folder.")) {
                            // remove images
                            p.getTestingImages().removeAll(failurest);
                            break;
                        }
                    } else {
                        alert("Success! All testing images found. "+ft);
                        break;
                    }

                }
            }

            // everything is OK
            project = p;
            toolbox.setEnabled(true);
            imageBrowser.refresh();
            
            //POEY
            testingImageBrowser.refresh();
            
            loadJasmineImage(project.currentImage());
            
            //POEY
            loadJasmineTestingImage(project.currentTestingImage());
            
            classbox.refresh();
            menus.enableMenus();
            setTitle();
            showWindows();
            setStatusText(null);
            visionSystemPanel.onProjectChanged(project);
            //if (featureSelectionDialog != null) featureSelectionDialog.update(project);

            setStatusText("Loaded project.");

        } catch (IOException e1) {
            JOptionPane.showMessageDialog(this, "Cannot open project.\nThe file may be from an older version of Jasmine\n" + e1.toString(), "Open Project", JOptionPane.ERROR_MESSAGE);
            //e1.printStackTrace();
        } catch (ClassNotFoundException e2) {
            JOptionPane.showMessageDialog(this, "Cannot open project.\nClass Not Found", "Open Project", JOptionPane.ERROR_MESSAGE);
            e2.printStackTrace();
        }


    }

    public void openFeatureSelectionDialog() {
        openFeatureSelectionDialog(getClassMode(), false);
    }

    public void openFeatureSelectionDialog(int mode, boolean backgroundSub) {
        new JasmineFeatureSelectionDialog(this, mode, backgroundSub);
    }

    public void saveProject() {
        try {
            File existing = project.getFilename();
            if (existing != null) {
                long start = System.currentTimeMillis();
                project.save(project.getFilename());
                long time = System.currentTimeMillis() - start;
                setStatusText("Saved project OK, " + time + "ms");
            } else {
                saveProjectAs();
            }
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(this, "Cannot save project: " + e1.getMessage(), "Save Project", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveProjectAs() {
        fc.setDialogTitle("Save Project As");
        fc.setFileFilter(JasmineFilters.getJasmineFilter());
        if (project.getFilename() != null) {
            fc.setSelectedFile(project.getFilename());
        } else {
            fc.setSelectedFile(new File(DEFAULT_PROJECT_LOCATION_FILE, project.getName() + ".jasmine"));
        }
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                if (!file.getName().endsWith(".jasmine")) {
                    file = new File(file.getParent(), file.getName() + ".jasmine");
                }
                long start = System.currentTimeMillis();
                if (project != null) project.save(file);
                long time = System.currentTimeMillis() - start;
                menus.updateSaveMenu();
                setStatusText("Saved project as " + file.getName() + "," + time + "ms");
                addRecentProject(file);
                updateDefaultProjectLocation(file);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(this, "Cannot save project: " + e1.getMessage(), "Save Project", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void updateDefaultProjectLocation(File file) {
        if (file != null) {
            settings.addProperty(SETTING_DEFAULT_PROJECT_LOCATION, file.getParentFile().getAbsolutePath());
            DEFAULT_PROJECT_LOCATION = file.getParentFile().getAbsolutePath();
        } else {
            DEFAULT_PROJECT_LOCATION = (String) settings.getProperty(SETTING_DEFAULT_PROJECT_LOCATION);
        }
        //alert("DEFAult PROJECT LOCATIOn: " + DEFAULT_PROJECT_LOCATION);
    }

    public boolean ensureOverlaySavedOK() {
        if (project == null) return true;
        if (segmentationPanel.eitherOverlayChanged()) {
            int response = JOptionPane.showConfirmDialog(this, "Save segmentation overlay on " + currentImage.filename + " first?", "Save Overlay", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.OK_OPTION) {
                segmentationPanel.saveOverlays(project.currentImage());
                return true;
            }
            if (response == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    public boolean ensureProjectSavedOK() {
        if (!ensureOverlaySavedOK()) return false;
        if (project != null && (project.isChanged() || project.getFilename() == null)) {
            int response;

            if (project.getFilename() == null) {
                response = JOptionPane.showConfirmDialog(this, "Project has not been saved yet. Save Project?", "Save Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            } else {
                response = JOptionPane.showConfirmDialog(this, "Project has changed. Save changes?", "Save Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            }

            if (response == JOptionPane.OK_OPTION) {
                File existing = project.getFilename();
                if (existing == null) {
                    saveProjectAs();
                } else {
                    saveProject();
                }
            }
            if (response == JOptionPane.CANCEL_OPTION) return false;
        }
        return true;
    }

    public void exit() {
        if (ensureProjectSavedOK()) {
            System.exit(0);
        }
    }

    public void alert(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public boolean confirm(String message) {
        int retVal = JOptionPane.showConfirmDialog(this, message, APP_NAME, JOptionPane.YES_NO_OPTION);
        return retVal == JOptionPane.YES_OPTION;
    }

    /**
     * @param image
     */
    public void loadJasmineImage(JasmineImage image) {
        setImageInfo(image);
        if (currentPanel == null) {
            // initialise the panel
            currentPanel = segmentationPanel;
        }
        // share the buffered image (saves memory)
        if (image != null) {
            BufferedImage img = image.getBufferedImage();
            segmentationPanel.loadJasmineImage(image, img);
            classificationPanel.loadJasmineImage(image, img);
        } else {
            segmentationPanel.setImageNull();
            classificationPanel.setImageNull();
            currentPanel.repaint();
        }
        currentImage = image;

    }
    
    //POEY
    public void loadJasmineTestingImage(JasmineTestingImage image) {
        // share the buffered image (saves memory)
        if (image != null) {       	
            image.getBufferedTestingImage();
        }
        currentTestingImage = image;
    }

    public void setImageInfo(JasmineImage image) {
        if (image == null) {
            status.setText("No Image Selected");
        } else {
            status.setText(image.toString());
        }
    }

    public PixelLoader getCurrentImage() {
        PixelLoader pl = new PixelLoader(currentImage.getBufferedImage(), null);
        pl.setFile(new File(project.getImageLocation(), currentImage.filename));
        return pl;
    }
    
    //POEY
    public PixelLoader getCurrentTestingImage() {
        PixelLoader pl = new PixelLoader(currentTestingImage.getBufferedTestingImage(), null);
        pl.setFile(new File(project.getTestingImageLocation(), currentTestingImage.filenameTesting));        
        return pl;
    }

    public void segmentCurrentImage(final JButton b) {

        try {
        final VisionSystem vs = VisionSystem.load(project);
        vs.addVisionSystemListener(this);

        Thread t = new Thread() {
            public void run() {
                try {
                    classificationPanel.clear();
                    b.setEnabled(false);
                    if (currentImage != null) {
                        currentImage.clearObjects();
                        currentImage.setObjects(vs.getObjects(getCurrentImage()));
                        classificationPanel.objects = currentImage.getObjects();                        
                        classificationPanel.repaint();
                        imageBrowser.refresh();
                    } else {
                        alert("No image selected");
                    }
                } catch (Exception err) {
                    alert(err.getMessage());
                    err.printStackTrace();
                } finally {
                    b.setEnabled(true);
                }
            }
        };

        t.start();
        } catch (Exception e) {
            alert("Cannot load the vision system: " + e.toString());
        }

    }

    public void preprocess() {

        final ProgressDialog d = new ProgressDialog("Processing Images", "Please wait while the images are processed", project.getImages().size());

        new Thread() {
            public void run() {
                try {
                    JasmineImagePreprocessor p = new JasmineImagePreprocessor(project);
                    for (int i = 0; i < project.getImages().size(); i++) {
                        JasmineImage jasmineImage = project.getImages().elementAt(i);
                        p.process(Jasmine.this, jasmineImage, new File(project.getImageLocation(), jasmineImage.filename));
                        d.setValue(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    alert("Cannot preprocess: " + e);
                }
                segmentationPanel.repaint();
                d.dispose();
                loadJasmineImage(project.currentImage());
            }
        }.start();

    }

    public void restore() {
        final ProgressDialog d = new ProgressDialog("Restoring Images", "Please wait while the images are restored", project.getImages().size());

        new Thread() {
            public void run() {
                try {
                    JasmineImagePreprocessor p = new JasmineImagePreprocessor(project);
                    for (int i = 0; i < project.getImages().size(); i++) {
                        JasmineImage jasmineImage = project.getImages().elementAt(i);
                        p.restore(new File(project.getImageLocation(), jasmineImage.filename));
                        d.setValue(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    alert("Cannot restore: " + e);
                }
                d.dispose();
                loadJasmineImage(project.currentImage());
            }
        }.start();
    }

    public void displayCorrelation() {
        new JasmineCorrelationGraph(this, segmentationPanel.mode);
    }

    public void displayFeatureSimilarity() {
        new JasmineFeatureSimilarity(this, segmentationPanel.mode);
    }

    public void displayFeatureDistributions() {
        new JasmineFeatureDistributions(this);
    }

    public void showAbout() {
        new JasmineAboutDialog(this);
    }

    public SubObjectClassifier getShapeClassifier() {
        try {
            return JasmineDeployer.getShapeClassifier(project);
        } catch (Exception e) {
            alert("Cannot instantiate shape classifier: " + e.toString());
            return null;
        }
    }

    public Segmenter getSegmenter(String handle) {
        try {
            return JasmineDeployer.getSegmenter(project, handle);
        } catch (Exception e) {
            alert("Cannot instantiate segmenter" + e.toString());
            return null;
        }
    }

    private ProgressDialog d;

    public void onStart() {
        d = new ProgressDialog("Segmenting", "Please wait...", 100);
    }
    
    //POEY
    public void onProcess() {
        d = new ProgressDialog("Processing", "Please wait...", 100);
    }

    public void onSegmentationProgress(int progress) {
        if (d != null) {
            d.setValue(progress);
        }
    }

    public void onFinishedSegmentation(Vector<SegmentedObject> objects) {
        if (d != null) {
            d.dispose();
        }
    }

    public void onFinished(Vector<SegmentedObject> objects) {

    }

    public static void main(String[] args){
        new Jasmine(args);
    }

}
