package jasmine.imaging.core.segmentation;


import jasmine.classify.data.Data;
import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.CachedOutput;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.PCM;
import jasmine.gp.multiclass.VarianceThreshold;
import jasmine.gp.nodes.*;
import jasmine.gp.nodes.math.Cos;
import jasmine.gp.params.GPParams;
import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.core.JasmineProject;

import java.io.File;
import java.util.Vector;

/**
 * <p/>
 * Learns segmentation. Classification is performed using Dynamic Range Selection
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 18-Jan-2007
 * @version 1.0
 */

public class JasmineSegmentationProblemDRSBetter extends JasmineSegmentationProblem {



 /*   public static void main(String[] args) throws Exception {
        JasmineProject project = JasmineProject.load(new File("/home/ooechs/Desktop/JasmineProjects/Fruit.jasmine"));
        JasmineSegmentationProblemDRSBetter p = new JasmineSegmentationProblemDRSBetter(project, BetterDRS.TYPE);
        //new GPStartDialog(null, p, new GraphicalListener());
        new GPStartDialog(null, p, new ConsoleListener(ConsoleListener.SILENT));
    }
*/
    public String getName() {
        switch (drsType) {
            case BetterDRS.TYPE:
                return "BetterDRS Segmentation";
            case BasicDRS.TYPE:
                return "BasicDRS Segmentation";
            case VarianceThreshold.TYPE:
                return "Variance Threshold Segmentation";
            case EntropyThreshold.TYPE:
                return "Entropy Threshold Segmentation";
        }
        return "Unknown";
    }

    public JasmineSegmentationProblemDRSBetter(File jasmineFile, int drsType, int mode) throws Exception {
        this(JasmineProject.load(jasmineFile), drsType, mode);
    }

    public JasmineSegmentationProblemDRSBetter(JasmineProject p, int drsType, int mode) {
        super(p, mode);
        SLOT_COUNT = project.getPixelClasses(mode).size() * 7;
        this.drsType = drsType;
    }

    public void initialise(Evolve e, GPParams params) {

        if (trainingData == null) loadTrainingData(e);

        // initialise the parameters
        //params.registerNode(new If());

/*        params.registerNode(new Less());
        params.registerNode(new More());
        params.registerNode(new Between());*/

        params.registerNode(new Cos());
        params.registerNode(new Mean());
        params.registerNode(new PercentDiff());

        params.registerNode(new Add());
        params.registerNode(new Mul());
        params.registerNode(new Sub());
        params.registerNode(new Div());

        //POEY comment: load all extraction functions, 2 integer values, a floating point value and a boolean value to terminals
        registerImagingTerminals(params);

        // set up additional parameters
        params.setReturnType(NodeConstraints.NUMBER);

    }

    public void customiseParameters(GPParams params) {
        // do nothing
        params.setIslandCount(ISLAND_COUNT);
    }

    private PCM pcm = null;

    /**
     * Builds the dynamic classification map
     *
     * @param data
     * @param ind
     * @return False, if the program doesn't use imaging.
     */
    public boolean buildProgramClassificationMap(DataStack data, Individual ind) {

        switch (drsType) {
            case BetterDRS.TYPE:
            	//POEY comment: SLOT_COUNT = the number of classes * 7, but it generate (the number of classes * 7)+1 slots
                pcm = new BetterDRS(SLOT_COUNT);
                break;
            case BasicDRS.TYPE:
            	//POEY comment: SLOT_COUNT = the number of classes * 7, but it generate (the number of classes * 7)+1 slots
            	
                pcm = new BasicDRS(SLOT_COUNT);
                break;
            case VarianceThreshold.TYPE:
                pcm = new VarianceThreshold();
                break;
            case EntropyThreshold.TYPE:
                pcm = new EntropyThreshold();
                break;
        }

/*        for (int i = 0; i < pixels.size(); i++) {
            ImagePixel imagePixel = pixels.elementAt(i);

            // set up the image on the stack
            data.setImage(imagePixel.image);

            // save this to the data stack. The nodes are able to access this stack.
            data.setX(imagePixel.x);
            data.setY(imagePixel.y);*/
        //POEY comment: trainingData.size() = the number of selected pixels
        for (int i = 0; i < trainingData.size(); i++) {

            Data data1 =  trainingData.elementAt(i);

            DataValueTerminal.currentValues  = data1.values;

            // run the individual and get the raw result
            //POEY comment: calculate the pixel's value by segmentation functions
            double result = ind.execute(data);

            // don't bother executing individuals which don't use imaging functions.
            if (!data.usesImaging) {
                return false;
            }

            pcm.addResult(result, data1.classID);
        }

        pcm.calculateThresholds();

        return true;

    }
    


    public void evaluate(Individual ind, DataStack data, Evolve e) {

        // build a special program classification map that fits the output
        // of this individual.
    	//POEY comment: create slots for BasicDRS or BetterDRS
    	//get calculated values of pixels and calculate threshold
        boolean usesImaging = buildProgramClassificationMap(data, ind);

        int FP = 0;
        int TP = 0;

        double N;

        if (!usesImaging) {
            ind.setWorstFitness();
            return;
        } else {	//every method goes in here
            if (drsType == BetterDRS.TYPE) {
                ((BetterDRS) pcm).fillInSlots();
            }
            
            Vector<CachedOutput> outputcache = pcm.getCachedResults();

            N = outputcache.size();
            //POEY comment: outputcache.size() = the number of final selected pixels
            for (int i = 0; i < outputcache.size(); i++) {

                CachedOutput cachedOutput = outputcache.elementAt(i);

                //POEY comment: in a case of BetterDRS, go to jasmine.gp.multiclass.BetterDRS.java
                //result is a class returned from slot
                //rawOutput is a calculated value of the pixel
                int result = pcm.getClassFromOutput(cachedOutput.rawOutput);

                // wrong answers are penalised
                if (result != cachedOutput.expectedClass) {
                	//POEY comment: neutralClassID = -1
                    if (result != neutralClassID) FP++;
                } else {
                    TP++;
                }

            }

        }

        // New fitness function
        double fitness = 1 - (TP / (N + FP));       

        ind.setKozaFitness(fitness);
        ind.setHits(TP);
        ind.setMistakes(FP);
        ind.setPCM(pcm);

        pcm.clearCachedResults();

    }

}
