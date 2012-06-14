package jasmine.imaging.core;


import jasmine.classify.data.Data;
import jasmine.classify.data.DataStatistics;
import jasmine.classify.featureselection.AttributeFilter;
import jasmine.classify.featureselection.IGAttributeFilter;
import jasmine.classify.featureselection.LDAAttributeFilter;
import jasmine.classify.featureselection.PCMAttributeFilter;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.nodes.imaging.parameterised.ParameterisedTerminal;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.classification.JasmineGP;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblem;
import jasmine.imaging.core.util.IconLabel;
import jasmine.imaging.core.util.ImagePixel;
import jasmine.imaging.core.util.OKCancelBar;
import jasmine.imaging.core.util.PixelSelector;
import jasmine.imaging.core.util.TerminalMetaData;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.SegmentedObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Collections;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;

/**
 * <p/>
 * The job of this panel is to display all the features which are available to the
 * segmenter and offer means of either excluding them, or assigning them a fitness
 * which affects their probability of being used.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 21-Feb-2008
 * @version 1.0
 */
public class JasmineFeatureSelectionDialog extends JDialog implements ActionListener {


    public static final String FILTER_TYPE_HANDLE = "filter_type";
    public static final String MASK_PIXEL_FEATURE_SET_HANDLE = "mask-features";
    public static final String MATERIAL_FEATURE_SET_HANDLE = "pixel-features";
    public static final String OBJECT_FEATURE_SET_HANDLE = "object-features";
    public static final String SUB_OBJECT_FEATURE_SET_HANDLE = "sub-object-features";

    protected Vector<TerminalMetaData> terminalMetaData;

    protected Jasmine jasmine;

    protected JComboBox filterList, pixelSelector;

    JTable table;

    MyTableModel model;

    JTextField subsetSize;

    protected JButton lda, reset, disableAll, generateDataset, generateTestingDataset, extractFeatures;
    protected JButton save, close;

    protected int mode;
    protected static boolean bgSub = false;
    //protected Vector trainingData;



    public JasmineFeatureSelectionDialog(final Jasmine jasmine, int mode, boolean bgSub) {

        super(jasmine);

        try {
                        setIconImage(new ImageIcon(getClass().getResource("/filter16.png")).getImage());
                    } catch (Exception e) {

                    }

        this.bgSub = bgSub;
        setTitle(JasmineClass.getTypeName(mode) + " Feature Selection/Extraction");
        this.mode = mode;
        this.jasmine = jasmine;
        setLayout(new BorderLayout());
        model = new MyTableModel();

        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));

        extractFeatures = new ToolboxButton(toolbar, " Extract ", "If possible, finds suitable parameters for parameterised terminals", "filter.png", Jasmine.FEATURE_EXTRACTION);
        lda = new ToolboxButton(toolbar, "Evaluate", "Assign feature scores using the chosen filter", "measure.png", Jasmine.FEATURE_EXTRACTION);
        reset = new ToolboxButton(toolbar, " Reset ", "Reset feature values", "undo.png", Jasmine.FEATURE_EXTRACTION);
        disableAll = new ToolboxButton(toolbar, " Disable All ", "Disable all features", "clear.png", Jasmine.FEATURE_EXTRACTION);
        generateDataset = new ToolboxButton(toolbar, " Generate Dataset ", "Generate a training dataset", "dataset.png", Jasmine.FEATURE_EXTRACTION);
        
        //POEY
        generateTestingDataset = new ToolboxButton(toolbar, " Generate Testing Dataset ", "Generate a testing dataset", "dataset.png", Jasmine.FEATURE_EXTRACTION);

        toolbar.addSeparator();

        toolbar.add(new JLabel("  Filter: "));
        Vector<AttributeFilter> filters = new Vector<AttributeFilter>();
        filters.add(new LDAAttributeFilter());
        filters.add(new PCMAttributeFilter(BetterDRS.TYPE));
        filters.add(new IGAttributeFilter());
        filterList = new JComboBox(filters);
        filterList.setMaximumSize(new Dimension(100, 20));
        toolbar.add(filterList);

        subsetSize = new JTextField("20");
        toolbar.add(new JLabel("  Subset size: "));
        toolbar.add(subsetSize);
        subsetSize.setMinimumSize(new Dimension(30, 20));
        subsetSize.setMaximumSize(new Dimension(70, 20));

        Vector<PixelSelector> pixelChoosers = new Vector<PixelSelector>();
        pixelChoosers.add(new PixelSelector(PixelSelector.PIXEL_SELECTION_BY_CLASS));
        pixelChoosers.add(new PixelSelector(PixelSelector.PIXEL_SELECTION_BY_CLUSTER));
        pixelSelector = new JComboBox(pixelChoosers);

        if (mode == JasmineClass.MATERIAL || mode == JasmineClass.MASK) {

            pixelSelector.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // update the project immediately on change
                    PixelSelector.setPixelSelectionMode(jasmine.project, pixelSelector.getSelectedIndex());
                }
            });
            pixelSelector.setMinimumSize(new Dimension(50, 20));
            pixelSelector.setMaximumSize(new Dimension(50, 20));

            toolbar.add(new JLabel("  Selection: "));
            toolbar.add(pixelSelector);
        }

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(1).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setMaxWidth(70);
        JScrollPane scrollPane = new JScrollPane(table);

        toolbar.add(Box.createHorizontalGlue());

        add(scrollPane, BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);

        save = new JButton("Save");
        close = new JButton("Close");
        save.addActionListener(this);
        close.addActionListener(this);

        add(new OKCancelBar(save, close), BorderLayout.SOUTH);
        update(jasmine.project);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == lda) {
            filter();
        }

        if (e.getSource() == generateDataset) {
            generateDataSet(jasmine.project);
        }
        
        if (e.getSource() == generateTestingDataset) {
            generateTestingDataSet(jasmine.project);
        }

        if (e.getSource() == disableAll) {
            disableAll();
        }

        if (e.getSource() == reset) {
            reset(jasmine.project, false, null);
        }

        if (e.getSource() == extractFeatures) {
            doFeatureExtraction(jasmine.project);
        }

        if (e.getSource() == close) {
            dispose();
        }

        if (e.getSource() == save) {
            jasmine.project.addProperty(getFeatureSetHandle(mode), terminalMetaData);
            jasmine.project.setChanged(true, "Updated feature set");
        }

    }

    public static String getFeatureSetHandle(int mode) {
        switch (mode) {
            case JasmineClass.MASK:
                return MASK_PIXEL_FEATURE_SET_HANDLE;
            case JasmineClass.MATERIAL:
                return MATERIAL_FEATURE_SET_HANDLE;
            case JasmineClass.OBJECT:
                return OBJECT_FEATURE_SET_HANDLE;
            case JasmineClass.SUB_OBJECT:
                return SUB_OBJECT_FEATURE_SET_HANDLE;
        }
        throw new RuntimeException("Unknown mode: " + mode);
    }

    public static boolean hasDoneFeatureSelection(JasmineProject project, int mode) {
        return project.getProperty(getFeatureSetHandle(mode)) != null;
    }

    public void update(JasmineProject project) {
        terminalMetaData = (Vector<TerminalMetaData>) project.getProperty(getFeatureSetHandle(mode));
        pixelSelector.setSelectedIndex(PixelSelector.getPixelSelectionMode(jasmine.project));
        Integer property = (Integer) project.getProperty(FILTER_TYPE_HANDLE);
        if (property != null) {
            filterList.setSelectedIndex(property);
        } else {
            filterList.setSelectedIndex(0);
        }
        if (terminalMetaData == null || terminalMetaData.size() == 0) {
            reset(project, false, null);
        } else {
            model.update();
        }

    }

    public void disableAll() {
        for (int i = 0; i < terminalMetaData.size(); i++) {
            TerminalMetaData metaData = terminalMetaData.elementAt(i);
            metaData.setEnabled(false);
        }
        model.update();
    }

    public void generateDataSet(JasmineProject project) {

        // get the chosen terminals
        Vector<Terminal> terminals = new Vector<Terminal>(terminalMetaData.size());
        Vector<Terminal> st = getTerminals();
        for (int i = 0; i < terminalMetaData.size(); i++) {
            TerminalMetaData metaData = terminalMetaData.elementAt(i);
            if (metaData.isEnabled()) {
                terminals.add(metaData.getTerminal(st));
            }
        }

        switch (mode) {
            case JasmineClass.MASK:
            case JasmineClass.MATERIAL:
                PixelLoader.CACHE_RGB_HSL = true;
                PixelLoader.CACHING_OTHERS = false;
                jasmine.exportPixels(terminals);
                break;
            case JasmineClass.OBJECT:
                jasmine.exportObjectFeatures(terminals);
                break;
            case JasmineClass.SUB_OBJECT:
                jasmine.exportShapeFeatures(terminals);
                break;
        }

    }
    
    //POEY
    public void generateTestingDataSet(JasmineProject project) {

        // get the chosen terminals
        Vector<Terminal> terminals = new Vector<Terminal>(terminalMetaData.size());
        Vector<Terminal> st = getTerminals();
        for (int i = 0; i < terminalMetaData.size(); i++) {
            TerminalMetaData metaData = terminalMetaData.elementAt(i);
            if (metaData.isEnabled()) {
                terminals.add(metaData.getTerminal(st));
            }
        }

        switch (mode) {
            case JasmineClass.OBJECT:            	
                jasmine.exportTestingObjectFeatures(terminals);
        }

    }


    public void doFeatureExtraction(final JasmineProject project) {

        final ProgressDialog d = new ProgressDialog("Feature Extraction", "Please wait", 10);

        new Thread() {
            public void run() {
                reset(project, true, d);
            }
        }.start();
    }

    public Vector getTrainingData() {
        switch (mode) {
            case JasmineClass.MASK:
                try {              	
                    return JasmineUtils.getAllMaskPixels(jasmine.project);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot get the pixels");
                }
            case JasmineClass.MATERIAL:	//POEY comment: for segmentation
                try {                 	
                    return JasmineUtils.getAllMaterialPixels(jasmine.project);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot get the pixels");
                }
            case JasmineClass.OBJECT:	//POEY comment: for general classification
                return JasmineUtils.getLabelledObjects(jasmine.project);
            case JasmineClass.SUB_OBJECT:
                return JasmineUtils.getLabelledSubObjects(jasmine.project);
        }
        return null;
    }

    public void reset(JasmineProject project, boolean doFeatureExtraction, ProgressDialog d) {
        terminalMetaData = new Vector<TerminalMetaData>();
        //POEY comment: get feature extraction functions
        Vector<Terminal> terminals = getTerminals();

        // look through the terminals for parameterised ones
        Vector<ParameterisedTerminal> parameterisedTerminals = new Vector<ParameterisedTerminal>(10);
        for (int i = 0; i < terminals.size(); i++) {
            Terminal terminal = terminals.elementAt(i);
            if (terminal instanceof ParameterisedTerminal) {  
            	//POEY comment: for segmentation, there are Perimeter Features, Line Features, Haar features, NxN Features
            	//for classification, there are above functions plus getRoughness and getAverage
                parameterisedTerminals.add((ParameterisedTerminal) terminal);
            }
        }

        if (parameterisedTerminals.size() > 0) {
        	//POEY comment: Extract button
            if (doFeatureExtraction) {
                terminals.removeAll(parameterisedTerminals);
                //POEY comment: add top 21 scores of parameterised functions with random parameters 
                terminals.addAll(featureExtract(parameterisedTerminals, d));
            } else { //POEY comment: Reset button
                terminals.removeAll(parameterisedTerminals);
                for (int i = 0; i < parameterisedTerminals.size(); i++) {
                    ParameterisedTerminal parameterisedTerminal = parameterisedTerminals.elementAt(i);
                    //POEY comment: use default parameters for parameterisedTerminal  
                    terminals.addAll(parameterisedTerminal.getDefaults());
                }
            }
        }

        for (int i = 0; i < terminals.size(); i++) {
            Terminal terminal = terminals.elementAt(i);
            terminalMetaData.add(new TerminalMetaData(terminal));
        }
        model.update();
    }


    public Vector<Terminal> getTerminals() {
        switch (mode) {
            case JasmineClass.MASK:
            case JasmineClass.MATERIAL:
                return JasmineSegmentationProblem.getStandardTerminals();
            case JasmineClass.OBJECT:
                return JasmineGP.getStandardTerminals(getTrainingData());
            case JasmineClass.SUB_OBJECT:
                return JasmineGP.getStandardTerminals(null);
        }
        return null;
    }
    
    public void filter() {

        if (filterList.getSelectedItem() == null) {
            jasmine.alert("Please select a filter method from the list first");
            return;
        }

        Thread t = new Thread() {

            public void run() {

                try {

                    PixelLoader.CACHE_RGB_HSL = false;
                    PixelLoader.CACHING_OTHERS = false;

                    //POEY comment: terminalMetaData contains feature extraction functions
                    ProgressDialog d = new ProgressDialog("Feature Selection", "Fitness values for features are being calculated.", terminalMetaData.size());
                  
                    jasmine.setStatusText("Running Filter...");
                    
                    //POEY comment: terminals contain feature extraction functions
                    Vector<Terminal> terminals = getTerminals();

                    long start = System.currentTimeMillis();

                    // get some training data
                    //POEY comment: 
                    //For segmentation: every selected pixel
                    //For object classification: every segmented object
                    Vector trainingData = getTrainingData();

                    AttributeFilter filter = (AttributeFilter) filterList.getSelectedItem();

                    //POEY comment: loop for each feature extraction function
                    for (int j = 0; j < terminalMetaData.size(); j++) {
                    	
                    	//POEY comment: to print functions' name
                    	//System.err.println("\nfunction name: "+terminalMetaData.elementAt(j).getName());
                    	
                        TerminalMetaData metaData = terminalMetaData.elementAt(j);

                        //POEY comment: t contains feature extraction function
                        Terminal t = metaData.getTerminal(terminals);

                        // and get the fitness
                        //POEY comment: evaluateTerminal is to set pixels into data stack, get its class, calculate pixels' value from function
                        //get the value of (the number of pixels predicted accurately / the total number of selected pixels of its class)
                        metaData.setFitness(evaluateTerminal(t, filter, trainingData, mode));

                        model.update();

                        d.setValue(j + 1);

                    }

                    long time = System.currentTimeMillis() - start;

                    d.dispose();

                    Collections.sort(terminalMetaData);


                    int size;
                    try {
                    	//POEY comment: the number of feature functions selected 
                        size = Integer.parseInt(subsetSize.getText().trim());
                    } catch (Exception e) {
                        size = -1;
                        subsetSize.setText("-1");
                    }

                    if (size > 0) {
                        for (int i = 0; i < terminalMetaData.size(); i++) {
                            TerminalMetaData metaData = terminalMetaData.elementAt(i);
                            if (i < size) {
                                metaData.setEnabled(true);
                                System.out.println(metaData.getFitness());
                            } else {
                                metaData.setEnabled(false);
                            }
                        }
                    }

                    model.update();

                    jasmine.setStatusText("Ran filter on " + terminalMetaData.size() + " features in " + time + " ms.");

                    jasmine.project.addProperty(FILTER_TYPE_HANDLE, filterList.getSelectedIndex());

                } catch (Exception e) {
                    jasmine.alert("Cannot filter: " + e.toString());
                    e.printStackTrace();
                }

            }

        };

        t.start();

    }

    public static float evaluateTerminal(Terminal t, AttributeFilter filter, Vector trainingData, int mode) {

        Vector<Data> td = new Vector<Data>(100);

        DataStack data = new DataStack();
      
        //POEY comment: for segmentation, trainingData.size()= the number of selected pixels
        //for classification, trainingData.size()= the number of segmented objects
        for (int k = 0; k < trainingData.size(); k++) {

            int classID = 0;
            switch (mode) {
                case JasmineClass.MASK:
                case JasmineClass.MATERIAL:	//POEY comment: for segmentation
                    ImagePixel pixel = (ImagePixel) trainingData.elementAt(k);

                    //POEY comment: return classID of background or object (1 or 2)
                    classID = pixel.getClassID();
                    
                    //POEY comment: bgSub = false for segmentation
                    if (bgSub) {
                        JasmineClass c = Jasmine.currentInstance.project.getPixelClass(pixel.getClassID(), mode);
                        if (c == null) {
                            classID = 0;
                        } else {
                            classID = c.background? 0 : 1;
                        }
                    }
                    
                    //POEY comment: set data to DataStack
                    data.setImage(pixel.image);
                    data.setX(pixel.x);
                    data.setY(pixel.y);
                    break;
                case JasmineClass.OBJECT:	//POEY comment: for classification
                    SegmentedObject object = (SegmentedObject) trainingData.elementAt(k);
                    classID = object.getClassID();                                     
                    //POEY comment: set pixels of segmented area and perimeter of its area
                    JasmineUtils.setupDataStack(data, object);
                                     
                    break;
                case JasmineClass.SUB_OBJECT:
                    ExtraShapeData shape = (ExtraShapeData) trainingData.elementAt(k);
                    classID = shape.getClassID();
                    JasmineUtils.setupDataStack(data, shape);
                    break;
            }
            
            //POEY comment: get output from each feature extraction function
            //for segmentation, it generates an output value for every pixel
            //if a function returns a mean value, so every pixel has the same output value
            //for classification, an object obtains a output value (a mean value)
            float output = (float) t.execute(data);
            //lda.add(new double[]{output}, point.value);
         
            
            //POEY comment: match calculated value of a pixel with its class
            td.add(new Data(new float[]{output}, String.valueOf(classID)));

        }

        //POEY comment: count pixels/objects and pixels/objects in each class
        DataStatistics stats = new DataStatistics(td);
        
        //POEY comment: abstract class of filters for score calculation  
        //such as jasmine.classify.featureselection.LDAAttributeFilter.java
        return filter.getScore(0, td, stats.getClassCount());

    }

    public Vector<ParameterisedTerminal> featureExtract(Vector<ParameterisedTerminal> terminals, ProgressDialog d) {

        PixelLoader.CACHE_RGB_HSL = true;

        try {

            //Vector<ImagePixel> pixels = JasmineUtils.getAllPixels(jasmine.project);
        	//POEY comment: get selected pixels and their class
            Vector trainingData = getTrainingData();
            
            //POEY comment: a filter for ranking functions 
            AttributeFilter filter = (AttributeFilter) filterList.getSelectedItem();

            Vector<ParameterisedTerminal> best = new Vector<ParameterisedTerminal>();

            int tries = 50;
            //POEY comment: for classification
            if (mode == JasmineClass.OBJECT || mode == JasmineClass.SUB_OBJECT) {
                tries = 5000;
            }
            int c = 0;

            d.setMax(terminals.size() * tries);
            
            //POEY comment: the number of Parameterised functions (4 functions)
            for (int i = 0; i < terminals.size(); i++) {

                Vector<ParameterisedTerminal> evaluated = new Vector<ParameterisedTerminal>();

                ParameterisedTerminal parameterisedTerminal = terminals.elementAt(i);

                d.setMessage("Feature extraction, processing " + parameterisedTerminal.toString());

                for (int j = 0; j < tries; j++) {

                    // how many attempts per terminal? 100?
                	//POEY comment: get random parameters for parameterisedTerminal
                    ParameterisedTerminal t = parameterisedTerminal.getRandom();

                    if (!evaluated.contains(t)) {

                        // evaluate it
                    	//POEY comment: use the same evaluation functions for all features
                        //t.score = evaluateTerminal(t, filter, trainingData, mode);
                    	
                    	//POEY
                    	t.score = evaluateTerminal(t, filter, trainingData, mode);

                        evaluated.add(t);

                        c++;

                        d.setValue(c);

                        System.out.println(t.score + ", " + t.toJava() + ", " + t.toString());

                    }


                }

                Collections.sort(evaluated);

                System.out.println("Best: ");

                // helps ensure terminals added are unique
                Vector<String> alreadyAdded = new Vector<String>();

                for (int e = 0; e < evaluated.size(); e++) {
                    String java = evaluated.elementAt(e).toJava();
                    if (!alreadyAdded.contains(java)) {
                        alreadyAdded.add(java);
                        best.add(evaluated.elementAt(e));
                        System.out.println(evaluated.elementAt(e).score + ", " + evaluated.elementAt(e).toJava());
                        if (alreadyAdded.size() > (terminals.size() * 5)) break;	//POEY comment: top 21 scores
                    }

                }


            }

            d.dispose();

            return best;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

// This renderer extends a component. It is used each time a

    // cell must be displayed.

    public class MyTableCellRenderer extends IconLabel implements TableCellRenderer {

        public MyTableCellRenderer() {
            fontHeight = getFontMetrics(getFont()).getHeight();
            try {
                icon = new ImageIcon(getClass().getResource("/feature.png")).getImage();
                iconDark = icon;
            } catch (Exception e) {
                // don't worry
                icon = null;
            }
        }


        // This method is called each time a cell in a column
        // using this renderer needs to be rendered.
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at
            // (rowIndex, vColIndex)

            if (isSelected) {
                // cell (and perhaps other cells) are selected
                selected = true;
            }

            if (hasFocus) {
                // this cell is the anchor and the table has the focus
            }

            showIcon = true;

            // Configure the component with the specified value
            setText(value.toString());

            // Set tool tip if desired
            setToolTipText((String) value);

            // Since the renderer is a component, return itself
            return this;
        }

        // The following methods override the defaults for performance reasons
        public void validate() {
        }

        public void revalidate() {
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }


    class MyTableModel extends AbstractTableModel {

        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            int rc = terminalMetaData == null ? 0 : terminalMetaData.size();
            return rc;
        }

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Feature";
                case 1:
                    return "Score";
                case 2:
                    return "Enabled";
            }
            return "Unknown";
        }

        public Object getValueAt(int row, int col) {
            TerminalMetaData data = terminalMetaData.elementAt(row);
            switch (col) {
                case 0:
                    return data.getName();
                case 1:
                    return data.getFitness();
                case 2:
                    return data.isEnabled();
            }
            return null;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
        * Don't need to implement this method unless your table's
        * editable.
        */
        public boolean isCellEditable(int row, int col) {
            return col != 0;
        }

        /*
        * Don't need to implement this method unless your table's
        * data can change.
        */
        public void setValueAt(Object value, int row, int col) {
            TerminalMetaData data = terminalMetaData.elementAt(row);
            switch (col) {
                case 1:
                    data.setFitness((Double) value);
                    fireTableCellUpdated(row, col);
                    break;
                case 2:
                    data.setEnabled((Boolean) value);
                    fireTableCellUpdated(row, col);
                    break;
            }

        }

        public void update() {
            fireTableDataChanged();
        }


    }

    class ToolboxButton extends JButton {

        protected int mode;
        protected String icon;

        public ToolboxButton(JToolBar bar, String text, String tooltip, String icon, int mode) {

            this.icon = icon;
            bar.add(this);
            setToolTipText(tooltip);
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setPreferredSize(new Dimension(80, 50));
            setMinimumSize(new Dimension(80, 50));
            setText(text);
            Font f = getFont();
            f.getSize();
            setFont(new Font(f.getName(), f.getStyle(), 10));
            try {
                setIcon(new ImageIcon(getClass().getResource("/" + icon)));
            } catch (Exception e) {
                //System.err.println("Could load load icon: " + icon);
            }
            addActionListener(JasmineFeatureSelectionDialog.this);
            putClientProperty("JButton.buttonType", "text");
            this.mode = mode;

        }

        public String toString() {
            return icon;
        }

    }

}

