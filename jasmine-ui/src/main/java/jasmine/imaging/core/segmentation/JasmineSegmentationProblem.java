package jasmine.imaging.core.segmentation;

import jasmine.classify.EnsembleListener;
import jasmine.classify.IslandFusionClassifier;
import jasmine.classify.classifier.Classifier;
import jasmine.classify.classifier.ClassifierFusion;
import jasmine.classify.classifier.GPClassifier;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataSet;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.interfaces.GraphicalListener;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.nodes.DataValueTerminal;
import jasmine.gp.nodes.ercs.BoolERC;
import jasmine.gp.nodes.ercs.CustomRangeIntegerERC;
import jasmine.gp.nodes.ercs.PercentageERC;
import jasmine.gp.nodes.imaging.image.ImageHueMean;
import jasmine.gp.nodes.imaging.image.ImageLightnessMean;
import jasmine.gp.nodes.imaging.image.ImageSaturationMean;
import jasmine.gp.nodes.imaging.parameterised.GenericHaarFeature;
import jasmine.gp.nodes.imaging.parameterised.GenericLineFeature;
import jasmine.gp.nodes.imaging.parameterised.GenericNxNFeature;
import jasmine.gp.nodes.imaging.parameterised.GenericPerimeterFeature;
import jasmine.gp.nodes.imaging.texture.Mean;
import jasmine.gp.nodes.imaging.texture.Range;
import jasmine.gp.nodes.imaging.texture.Variance;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.ImagingProblem;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.core.util.EvolvedSegmenter;
import jasmine.imaging.core.util.EvolvedSegmenterEnsemble;
import jasmine.imaging.core.util.EvolvedSegmenterSingle;
import jasmine.imaging.core.util.ImagePixel;
import jasmine.imaging.core.util.PixelSelector;
import jasmine.imaging.core.util.TerminalMetaData;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.Vector;

/**
 * <p/>
 * Represents a segmentation problem that can be solved using Jasmine.
 * Common methods shared by the problems are here.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 18-Feb-2008
 * @version 1.0
 */
public abstract class JasmineSegmentationProblem extends ImagingProblem {

    public final boolean skip = true;
    public final double ALPHA = 1;
    public final double BETA = 2;
    // how many of the pixels to use? (0-1)
    public static float trainingProportion = 1;
    public static int ISLAND_COUNT = 1;
    int drsType = BetterDRS.TYPE;
    int SLOT_COUNT = 20;

    // the project that we're using
    protected JasmineProject project;

    // which classes are to be solved?
    protected Vector<ClassIDMapping> classesToBeSolved = null;

    // extra data about the terminals
    protected Vector<TerminalMetaData> terminalMetaData;

    //protected Vector<ImagePixel> pixels;
    protected Vector<Data> trainingData;

    /**
     * A classID that the classifier can mistakenly classify without
     * receiving a FP penalty (mind you it doesn't get a reward either).
     */
    protected int neutralClassID = -1;

    // normal segmentation or background subtraction?
    protected int mode;

    /**
     * Initialises the problem with the specified project file
     */
    public JasmineSegmentationProblem(JasmineProject project, int mode) {
        System.out.println("Jasmine Segmentation Problem MODE = " + mode);
        this.project = project;
        this.mode = mode;
    }

    /**
     * Sets a classID that the classifier can mistakenly classify without
     * receiving a FP penalty (mind you it doesn't get a reward either).
     */
    public void setNeutralClassID(int neutralClassID) {
        this.neutralClassID = neutralClassID;
    }

    /**
     * Sets extra data about the terminals
     *
     * @param terminalMetaData
     */
    public void setTerminalMetaData(Vector<TerminalMetaData> terminalMetaData) {
        this.terminalMetaData = terminalMetaData;
    }

    /**
     * Sets the classes to be solved
     */
    public void setClassesToBeSolved(Vector<ClassIDMapping> classesToBeSolved) {
        this.classesToBeSolved = classesToBeSolved;
    }

    public String getMethodSignature() {
        return "segment(PixelLoader image, int x, int y)";
    }

/*    public void setPixels(Vector<ImagePixel> pixels) {
        this.pixels = pixels;
    }

    public Vector<ImagePixel> getPixels() {
        return pixels;
    }*/

    public Vector<Data> getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(Vector<Data> trainingData) {
        this.trainingData = trainingData;
    }


    public void loadTrainingData(Evolve e) {
        if (trainingData != null) return;
        if (classesToBeSolved == null) {
            throw new RuntimeException("Classes to be solved is null");
        }
        try {
            System.out.print("Loading training data... ");
            PixelLoader.CACHE_RGB_HSL = true;
            PixelLoader.CACHING_OTHERS = false;
            // get the pixels out here
            Vector<ImagePixel> pixels = loadTrainingData(project, classesToBeSolved, mode);
            // get the feature set
            Vector<Terminal> set = JasmineUtils.getTerminalSet(getStandardTerminals(), terminalMetaData);
            // give a reference to DataValueTerminal, so it can work on images as well as generic datasets
            DataValueTerminal.imagingTerminals = set;
            // now get a dataset from the pixels
            //POEY comment: ds consists of calculated value of pixels and classID
            DataSet ds = JasmineUtils.generateDataSet(set, pixels);
            trainingData = ds.getAllTrainingData();
            // get the training images
            trainingImages = JasmineUtils.getTrainingImages(project, mode);
            // ensure the pixel loaders are not hogging memory
            PixelLoader.CACHE_RGB_HSL = false;
            PixelLoader.CACHING_OTHERS = false;
            System.out.println("Done");
        } catch (Exception err) {
            e.fatal(err.getMessage());
        }
    }

    public static Vector<ImagePixel> loadTrainingData(JasmineProject project, Vector<ClassIDMapping> classesToBeSolved, int mode) throws IOException {

        Vector<ImagePixel> pixels = JasmineUtils.getAllPixels(project, mode);
        Vector<ImagePixel> chosen = new PixelSelector(project).choosePixels(pixels, trainingProportion, project, mode);
        Vector<ImagePixel> finalSelection = new Vector<ImagePixel>();       

        for (int i = 0; i < chosen.size(); i++) {
            ImagePixel pixel = chosen.elementAt(i);
            // see if the pixel is one of the classes to be solved
            for (int k = 0; k < classesToBeSolved.size(); k++) {
                ClassIDMapping classToBeSolved = classesToBeSolved.elementAt(k);
                if (pixel.classID == classToBeSolved.getJasmineClassID()) {
                    pixel.classID = classToBeSolved.getNewClassID();
                    finalSelection.add(pixel);
                    break;
                }
            }
        }

        System.out.println("Final selection size: " + finalSelection.size());
        return finalSelection;

    }


    public static Vector<Terminal> getStandardTerminals() {
        Vector<Terminal> terminals = new Vector<Terminal>();

/*        terminals.add(new Red());
        terminals.add(new Green());
        terminals.add(new Blue());*/

        terminals.add(new NormalisedRed());
        terminals.add(new NormalisedGreen());
        terminals.add(new NormalisedBlue());

        //POEY comment: RGB
        terminals.add(new C1C2C3(0));
        terminals.add(new C1C2C3(1));
        terminals.add(new C1C2C3(2));

        //POEY comment: RGB
        terminals.add(new L1L2L3(0));
        terminals.add(new L1L2L3(1));
        terminals.add(new L1L2L3(2));

        terminals.add(new Hue());
        terminals.add(new Saturation());
        terminals.add(new Lightness());

        terminals.add(new GreyValue());

/*        terminals.add(new RedMean());
        terminals.add(new RedStdDev());
        terminals.add(new GreenMean());
        terminals.add(new GreenStdDev());
        terminals.add(new BlueMean());
        terminals.add(new BlueStdDev());*/

        // Image averages
        terminals.add(new ImageHueMean());
        terminals.add(new ImageSaturationMean());
        terminals.add(new ImageLightnessMean());

        //POEY comment: Image averages of grey colour
        terminals.add(new Mean());
        terminals.add(new Range());
        terminals.add(new Variance());
        //terminals.add(new Laplacian());

        //POEY comment: Binary colour
        terminals.add(new AdaptiveBinaryThreshold());

        // Texture
/*        terminals.add(new Perimeter1Mean());
        terminals.add(new Perimeter2Mean());

        terminals.add(new Perimeter1Min());
        terminals.add(new Perimeter2Min());

        terminals.add(new Perimeter1Max());
        terminals.add(new Perimeter2Max());

        terminals.add(new Perimeter1StdDev());
        terminals.add(new Perimeter2StdDev());*/

        terminals.add(new GenericPerimeterFeature());	//POEY comment: Grey colour
        terminals.add(new GenericLineFeature());		//POEY comment: Grey colour
        terminals.add(new GenericHaarFeature());		//POEY comment: Grey colour
        terminals.add(new GenericNxNFeature());

/*        terminals.add(new HLine1Mean());
        terminals.add(new HLine2Mean());
        terminals.add(new HLine1StdDev());
        terminals.add(new HLine2StdDev());
        terminals.add(new HLine1Edges());
        terminals.add(new HLine2Edges());

        terminals.add(new VLine1Mean());
        terminals.add(new VLine2Mean());
        terminals.add(new VLine1StdDev());
        terminals.add(new VLine2StdDev());
        terminals.add(new VLine1Edges());
        terminals.add(new VLine2Edges());*/

        return terminals;

    }


    public String getMethodSignature(Individual individual) {
        return "public int evaluate(PixelLoader image, int x, int y)";
    }



    /**
     * Ensures that DataValueTerminal is set up with instances of appropriate terminals so that it can
     * be executed as an evolved segmenter and properly converted into Java.
     */
     public void ensureIndividualHasTerminals(Individual ind) {
        if (ind != null) {
             DataValueTerminal.currentValues = null;
             DataStack ds = new DataStack();
             ds.setImage(trainingImages.elementAt(0));
             ind.execute(ds);
        }
     }


    /**
     * Loads the terminals which are then the same for all segmentation problems.
     */
    public void registerImagingTerminals(GPParams params) {

        Vector<Terminal> terminals = JasmineUtils.getTerminalSet(getStandardTerminals(), terminalMetaData);

        // register nodes without fitness or enabledness
        //POEY comment: give index to extraction functions
        for (int i = 0; i < terminals.size(); i++) {
            //params.registerNode(terminals.elementAt(i));
            params.registerNode(new DataValueTerminal(i));
        }

        params.registerNode(new CustomRangeIntegerERC(1, 20));
        params.registerNode(new CustomRangeIntegerERC(0, 255));
        params.registerNode(new PercentageERC());
        params.registerNode(new BoolERC());		//POEY comment: generate a boolean value

    }

    private BufferedImage out;

    public BufferedImage describe(GPActionListener listener, Individual ind, DataStack data, int index) {

        if (listener instanceof GraphicalListener) {

            PixelLoader.CACHE_RGB_HSL = false;
            PixelLoader.CACHING_OTHERS = false;

            int black = Color.BLACK.getRGB();

            // make a cache of the colours so we don't have to requery the vector all the time
            Integer[] colors;
            if (mode == JasmineClass.MATERIAL) {
	            if (classesToBeSolved != null) {
	                // if the classes are mapped somehow make sure the colours are still correct
	                colors = new Integer[classesToBeSolved.size() + 5];
	                for (int i = 0; i < classesToBeSolved.size(); i++) {
	                    ClassIDMapping classIDMapping = classesToBeSolved.elementAt(i);
	                    JasmineClass jasmineClass = project.getPixelClass(classIDMapping.jasmineClassID, mode);
	                    if (jasmineClass.classID ==6) {
	                        System.out.println("White");
	                    }
	                    if (JasmineUtils.isBackground(jasmineClass)) {
	                        colors[classIDMapping.newClassID] = null;
	                    } else {
	                        colors[classIDMapping.newClassID] = jasmineClass.color.getRGB();
	                    }
	                }
	            } else {
	                Vector<JasmineClass> classes = project.getPixelClasses(mode);
	                colors = new Integer[project.getObjectClasses().size() + classes.size() + 1];
	                // get the colours from the jasmine classes
	                for (int i = 0; i < classes.size(); i++) {
	                    JasmineClass jasmineClass = classes.elementAt(i);
	                    if (JasmineUtils.isBackground(jasmineClass)) {
	                        colors[jasmineClass.classID] = null;
	                    } else {
	                        colors[jasmineClass.classID] = jasmineClass.color.getRGB();
	                    }
	                }
	            }
            } else {
                System.out.println("Background subtraction mode");
                colors = new Integer[2];
                colors[0] = black;
                colors[1] = null;
            }

            data.setImage(trainingImages.elementAt(index));

            // get the first training image
            BufferedImage image = trainingImages.elementAt(index).getBufferedImage();

            int scale = 1;

            while (true) {

                if (image.getWidth() / scale > 320)
                    scale++;
                else break;

            }

            int scaledWidth = image.getWidth() / scale;
            int scaledHeight = image.getHeight() / scale;


            if (out == null || out.getWidth() != scaledWidth || out.getHeight() != scaledHeight)
                out = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);

            int overlayX;
            int overlayY = 0;

            for (int y = 0; y < image.getHeight(); y += scale) {
                overlayX = 0;
                for (int x = 0; x < image.getWidth(); x += scale) {

                    data.setX(x);
                    data.setY(y);

                    int value;

                        if (ind.getPCM() != null) {
                            value = ind.getPCM().getClassFromOutput(ind.execute(data));
                        } else {
                            value = (int) ind.execute(data);
                        }

                    Integer c;

                    if (value < 0 || value > (colors.length - 1)) {
                        c = black;
                    } else {
                        c = colors[value];
                    }

                    if (overlayX < scaledWidth && overlayY < scaledHeight) {
                        if (c == null) {
                            // background classes
                            out.setRGB(overlayX, overlayY, image.getRGB(x, y));
                        } else {
                            out.setRGB(overlayX, overlayY, c);
                        }
                    }

                    overlayX++;
                }
                overlayY++;
            }


            return out;

        }

        return null;

    }

    //POEY comment: save a segmentation solution file
    public void saveSegmenter(final Jasmine j, Evolve e, final File f) {
        final Individual[] inds = e.getBestIndividuals();
        //POEY comment: inds.length = 1
        for (int i = 0; i < inds.length; i++) {
            Individual ind = inds[i];
            ensureIndividualHasTerminals(ind);
        }

        if (inds.length > 1) {
            final EnsembleListener listener = new EnsembleListener(null);

            new Thread() {
                public void run() {
                    getSegmenter(inds, listener).save(f);
                    j.setStatusText("Saved segmenter: " + f.getName());
                }
            }.start();
            
        } else {
            getSegmenter(inds, null).save(f);
            j.setStatusText("Saved segmenter: " + f.getName());
        }


    }

    public EvolvedSegmenter getSegmenter(Individual[] inds,  EnsembleListener listener) {

        if (inds.length == 1) {
            System.out.println("Created single segmenter (ind length is 1)");
            return new EvolvedSegmenterSingle(inds[0]);
        } else {
            Vector<Integer> classIDs = JasmineUtils.getClasses(trainingData);

            Classifier c = IslandFusionClassifier.generateClassifier(trainingData, trainingData, classIDs, inds, listener);

            listener.dispose();

            // convert into an evolved segmenter
            if (c instanceof GPClassifier) {
                System.out.println("Created single segmenter");
                return new EvolvedSegmenterSingle(((GPClassifier) c).ind);
            } else {
                System.out.println("Created ensemble segmenter");
                return new EvolvedSegmenterEnsemble((ClassifierFusion) c);
            }
        }

    }

}
