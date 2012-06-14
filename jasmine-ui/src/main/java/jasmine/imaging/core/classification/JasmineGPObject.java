package jasmine.imaging.core.classification;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.multiclass.*;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;
import jasmine.imaging.shapes.SegmentedObject;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 20-Apr-2009
 * Time: 14:03:37
 * To change this template use File | Settings | File Templates.
 */
public class JasmineGPObject extends JasmineGP {

    protected JasmineProject p;
    protected Vector<SegmentedObject> trainingData;



    public JasmineGPObject(JasmineProject p, int drsType) {
        super(drsType, p.getObjectClasses().size());
        this.p = p;
        trainingData = JasmineUtils.getLabelledObjects(p);
    }

    public String getName() {
        return "Object Classification Problem";
    }

    public void initialise(Evolve e, GPParams params) {

        // Register the functions
        registerFunctions(params);

        Vector<Terminal> set = JasmineUtils.getTerminalSet(getStandardTerminals(trainingData), terminalMetaData);
        for (int i = 0; i < set.size(); i++) {
            Terminal feature = set.elementAt(i);
            params.registerNode(feature);
        }

    }

    /*
    * Builds the dynamic classification map
    */
    private PCM buildProgramClassificationMap(DataStack data, Individual ind) {

        PCM pcm = getPCM();

        for (int i = 0; i < trainingData.size(); i++) {

            // get the data
            SegmentedObject d = trainingData.elementAt(i);

            // Put it onto the stack
            JasmineUtils.setupDataStack(data, d);

            // run the individual
            double result = ind.execute(data);

            // don't bother executing individuals which don't use the imaging functions.
            if (!data.usesImaging) {
                return null;
            }

            // add the result to the pcm
            pcm.addResult(result, d.getClassID());

        }

        pcm.calculateThresholds();


        return pcm;

    }



    public void evaluate(Individual ind, DataStack data, Evolve e) {
     	
        PCM pcm = buildProgramClassificationMap(data, ind);

        if (pcm == null) {
            ind.setWorstFitness();
            return;
        }

        if (pcm instanceof BetterDRS) {
            ((BetterDRS) pcm).fillInSlots();
        }

        int hits = 0;
        float mistakes = 0;

        Vector<CachedOutput> cachedOutput = pcm.getCachedResults();

        for (int i = 0; i < cachedOutput.size(); i++) {

            CachedOutput output = cachedOutput.elementAt(i);
            int classID = pcm.getClassFromOutput(output.rawOutput);

            SegmentedObject d = trainingData.elementAt(i);
            float difficulty = 1;

            if (classID > -1) {
                if (d.getClassID() == classID) {
                    hits += difficulty;
                } else {
                    mistakes += difficulty;
                }
            } else {
                // the point falls into no man's land
                mistakes += (difficulty * 0.5);
            }

        }

        // minimise the mistakes
        float fitness = mistakes;

        // maximise fitness
        ind.setKozaFitness(fitness);
        ind.setHits(hits);

        if (fitness == 0) {
            e.stopFlag = true;
        }

        ind.setPCM(pcm);

    }



    /**
     * Produces a class results object which summarises the ability of the classifier
     * on a per-class basis.
     */
    public ClassResults describe(GPActionListener gpActionListener, Individual ind, DataStack data, int index) {

        ClassResults results = new ClassResults();

        for (int i = 0; i < trainingData.size(); i++) {

            // get the data
            SegmentedObject d = trainingData.elementAt(i);

            // Put it onto the stack
            JasmineUtils.setupDataStack(data, d);

            // run the individual
            int classID = ind.getPCM().getClassFromOutput(ind.execute(data));

            if (results.getClassResult(classID) == null) {
                // if not added, add class to the class results data structure
                String className = p.getShapeClass(classID).name ;
                results.addClass(className, classID);
            }

            if (d.getClassID() != classID) {
                results.addMiss(classID);
            } else {
                results.addHit(classID);
            }

        }

        return results;

    }


}
