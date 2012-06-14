package jasmine.gp.multiclass;

import java.io.Serializable;
import java.util.Vector;

/**
 * My version of DRS
 *
 * @author Olly Oechsle, University of Essex, Date: 27-Feb-2008
 */
public class BetterDRS extends PCM implements Serializable {

    public static final int TYPE = 2;

    private int defaultClass = -1;
    private int numSlots = 50;
    private int[] slots;
    private double MIN = Double.MAX_VALUE, MAX = Double.MIN_VALUE;

    public BetterDRS() {
        this(50);
    }

    public BetterDRS(int numSlots) {   	
        this(numSlots, -1);
    }

    public BetterDRS(int numSlots, int defaultClass) {
        this.defaultClass = defaultClass;
        this.numSlots = numSlots;
        slots = new int[numSlots + 1];       
    }

//    public static void main(String[] args) {
//        BetterDRS drs = new BetterDRS(3);
//        drs.addResult(1, 5);
//        drs.addResult(1, 5);
//        drs.addResult(2, 5);
//        drs.addResult(2, 7);
//        drs.addResult(2, 5);
//        drs.addResult(2, 8);
//        drs.addResult(3, 8);
//
//        drs.calculateThresholds();
//        System.out.println(drs.getHits());
//        for (int i = 1; i <= 3; i++) {
//            int classID = drs.getClassFromOutput(i);
//            float confidence1 = drs.getConfidence(5, i);
//            float confidence2 = drs.getConfidence(8, i);
//            float confidence3 = drs.getConfidence(7, i);
//            System.out.println(i + ", " + classID + ", " + confidence1 + ", " + confidence2 + ", " + confidence3);
//        }
//
//    }

    public BetterDRS(double MIN, double MAX, int[] slots) {
        this.MIN = MIN;
        this.MAX = MAX;
        this.slots = slots;
        this.numSlots = slots.length - 1;
    }

    public BetterDRS(double MIN, double MAX, int[] slots, int[][] slotCount) {
        this.MIN = MIN;
        this.MAX = MAX;
        this.slots = slots;
        this.numSlots = slots.length - 1;
        this.slotCount = slotCount;
    }

    /**
     * Gives a result to the program classification map. This information can be used
     * to calculate good values for the threshold using the calculateThresholds method.
     *
     * @param output  The raw output from the program
     * @param classID The class that we expect to see
     */
    public void addResult(double output, int classID) {
        addResult(output, classID, 1);
    }

    /**
     * Gives a result to the program classification map. This information can be used
     * to calculate good values for the threshold using the calculateThresholds method.
     *
     * @param output  The raw output from the program
     * @param classID The class that we expect to see
     * @param weight  The weight associated with this piece of data
     */
    //POEY comment: to define MAX and MIN calculated values of pixels
    public void addResult(double output, int classID, double weight) {
        super.addResult(output, classID, weight);
        if (output > MAX) MAX = output;
        if (output < MIN) MIN = output;       
    }

    protected int slotCount[][];

    /**
     * Calculates the thresholds in some way so as to enable classification.
     */
    public void calculateThresholds() {

        //Vector<Integer> classes = discoverClasses();

        int maxClassID = 0;
        //POEY comment: classID starts at #0
        for (int i = 0; i < classes.size(); i++) {
            int classID = classes.elementAt(i);
            if (classID > maxClassID) maxClassID = classID;
        }

        //POEY comment: for segmentation, slots.length = (the number of classes * 7)+1
        //for classification, slots.length = 51
        slotCount = new int[slots.length][maxClassID + 1];

        //POEY comment: each index of cachedResults contains a value of pixel/object, real classID and weight=1
        for (int i = 0; i < cachedResults.size(); i++) {
            CachedOutput cachedOutput = cachedResults.elementAt(i);
            // which slot does the output fit into?
            //POEY comment: rawOutput is a value of a pixel/object
            int slot = getSlotIndex(cachedOutput.rawOutput);
            if (cachedOutput.expectedClass >= 0) {
                slotCount[slot][cachedOutput.expectedClass]++;
            } else {
                System.err.println("DRS is ignoring negative classID: " + cachedOutput.expectedClass);
            }
        }

        for (int SLOT = 0; SLOT < slots.length; SLOT++) {
            int CL = defaultClass;
            int highest = 0;
            for (int classID = 0; classID < slotCount[SLOT].length; classID++) {
                if (slotCount[SLOT][classID] > highest) {
                    CL = classID;
                    highest = slotCount[SLOT][classID];
                }
            }
            //POEY comment: store a classID which is the most frequently
            slots[SLOT] = CL;
        }

    }

    /**
     * Fills in gaps in the program classification map that removes some of the blank slots that
     * would otherwise return -1, which is guaranteed to be wrong. By filling in the PCM it may make
     * the DRS classifier slightly more general and reliable.
     */
    public void fillInSlots() {

       int before = defaultClass;

        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            if (slot == defaultClass) {

                // see whether this slot can be "filled in" to make the DRS translator more reliable

                // see what comes after this blank slot
                int after = defaultClass;

                for (int j = i; j < slots.length; j++) {
                    if (slots[j] != defaultClass) {
                        after = slots[j];
                        break;
                    }
                }

                // if both before and after are the same,
                // then fill in this one too.
                if (before == after) {
                    slots[i] = before;
                    // ensure that confidence is not zero
                    slotCount[i][before] = 1;
                }
                //if it is not in the if case above, so slots[i] = -1	//defaultClass

            } else {
                // remember what came before
                before = slot;
            }           
        }
           
    }

    public float getUnallocatedSlotPercentage() {
        float unallocated = 0;
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            if (slot == defaultClass) {
                unallocated++;
            }
        }
        return unallocated / slots.length;
    }

    public int getSlotIndex(double raw) {
    	//POEY comment: raw is a calculated value of a pixel
    	//MAX is a maximum calculated value of a pixel/object
    	//MIN is a minimum calculated value of a pixel/object
    	//for segmentation, numSlots = 14; for classification, numSlots = 50    	
        if (raw > MAX) raw = MAX;
        else if (raw < MIN) raw = MIN;
        double RANGE = MAX - MIN;
        double adjusted = ((raw - MIN) / RANGE) * numSlots;          
        return (int) adjusted;
    }

    /**
     * Given an output, returns the classification
     */
    public int getClassFromOutput(double raw) {   	
        return slots[getSlotIndex(raw)];
    }

    /**
     * Returns the DRS classifier's confidence that it relates to
     * a particular classID.
     * Warning - Will not work on individuals initialised using the toJava
     * method code.
     */
    public float getConfidence(int classID, double raw) {
        // first get the slot index
        int SLOT = getSlotIndex(raw);
        float qty = 0;
        int total = 0;
        for (int j = 0; j < slotCount[SLOT].length; j++) {
            if (j == classID) {
                qty = slotCount[SLOT][j];
            }
            total += slotCount[SLOT][j];
        }
        // avoid divide by zero error
        if (qty == 0) return 0;
        return qty / total;
    }

    private String slotCountToJava() {

        StringBuffer buffer = new StringBuffer();
        buffer.append("new int[][]{");
        //POEY comment: slotCount[slotIndex][classID]
        for (int i = 0; i < slotCount.length; i++) {
            int[] ints = slotCount[i];
            buffer.append("new int[]{");
            for (int j = 0; j < ints.length; j++) {
                buffer.append(ints[j]);
                if (j < ints.length) buffer.append(",");
            }
            buffer.append("}");
            if (i < slotCount.length) buffer.append(",");
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Turns the map into java that allows it to be reinstantiated.
     */
    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("new BetterDRS(");
        buffer.append(MIN);
        buffer.append(",");
        buffer.append(MAX);
        buffer.append(",new int[]{");
        for (int i = 0; i < slots.length; i++) {
            buffer.append(slots[i]);
            if (i < slots.length - 1) buffer.append(",");
        }
        buffer.append("},");
        buffer.append(slotCountToJava());
        buffer.append(")");
        return buffer.toString();
    }

}
