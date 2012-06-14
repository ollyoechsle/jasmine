package jasmine.gp.multiclass;

import java.io.Serializable;
import java.util.Vector;

/**
 * The original Dynamic Range Selection Program Classification Map
 * Based on a description by Loveard and Ciesielski (2001).
 *
 * @author Olly Oechsle, University of Essex, Date: 27-Feb-2008
 */
public class BasicDRS extends PCM implements Serializable {

    public static final int TYPE = 1;

    private int defaultClass = -1;
    private double min = -25;
    private double max = 25;
    private int numSlots = 50;
    private int[] slots;

    public BasicDRS() {
        this(-25, 25, 50);
    }

    public BasicDRS(int numSlots) {
        this(-25, 25, numSlots);
    }

    /**
     * @param min      Minimum threshold for values
     * @param max      Maximum threshold for values
     * @param numSlots The number of slots, spread evenly along the range from min -> max
     */
    public BasicDRS(double min, double max, int numSlots) {
        this.min = min;
        this.max = max;
        this.numSlots = numSlots;
        slots = new int[numSlots + 1];
    }

//    public static void main(String[] args) {
//        BasicDRS pcm = new BasicDRS(-1, 1, 2);
//        pcm.addResult(0.058733942931631256, 0, 1);
//        pcm.addResult(7.992976540412017E-4, 0, 1);
//        pcm.addResult(-0.005842084876255984, 1, 1);
//        pcm.addResult(0.012396538309783935, 0, 1);
//        pcm.addResult(-0.00609350487878374, 1, 1);
//        pcm.addResult(0.01748269728360054, 0, 1);
//        pcm.addResult(0.02185092255098849, 0, 1);
//        pcm.addResult(0.04183126796901677, 0, 1);
//        pcm.addResult(0.03842552296854436, 0, 1);
//        pcm.addResult(0.05293584720394481, 0, 1);
//        pcm.addResult(0.013113027567570464, 0, 1);
//        pcm.addResult(0.06322947024417658, 0, 1);
//        pcm.addResult(0.004194309928198904, 0, 1);
//        pcm.addResult(0.010065431258248334, 0, 1);
//        pcm.addResult(0.0012619438373261379, 0, 1);
//        pcm.addResult(0.0029258637857278906, 0, 1);
//        pcm.addResult(0.009200038521638159, 0, 1);
//        pcm.addResult(0.009680921339820429, 0, 1);
//        pcm.addResult(-8.213909743737077E-4, 0, 1);
//        pcm.addResult(-0.012038379925195606, 0, 1);
//        pcm.addResult(0.03565906630879396, 0, 1);
//        pcm.addResult(-0.007627662397067469, 1, 1);
//        pcm.addResult(0.039459956429343065, 0, 1);
//        pcm.addResult(0.022196753505167123, 0, 1);
//        pcm.addResult(0.023675037442861056, 0, 1);
//        pcm.addResult(0.016264704912859966, 0, 1);
//        pcm.addResult(0.054845753643607845, 0, 1);
//        pcm.addResult(0.007154975216141498, 0, 1);
//        pcm.addResult(0.00845541259506769, 0, 1);
//        pcm.addResult(-0.0018097286138212272, 1, 1);
//        pcm.addResult(0.024654776650817008, 0, 1);
//        pcm.addResult(0.0440133738676344, 0, 1);
//        pcm.addResult(0.009154723324210216, 0, 1);
//        pcm.addResult(0.05028680415291395, 0, 1);
//        pcm.addResult(0.019844648934093462, 0, 1);
//        pcm.addResult(0.010619888661675916, 0, 1);
//        pcm.addResult(0.041266221991373496, 0, 1);
//        pcm.addResult(0.02235628635005862, 0, 1);
//        pcm.addResult(0.01456357913366599, 0, 1);
//        pcm.addResult(-0.004565019803823522, 0, 1);
//        pcm.addResult(0.0028605168747708578, 0, 1);
//        pcm.addResult(0.04003633398703671, 0, 1);
//        pcm.addResult(0.02806934805738737, 0, 1);
//        pcm.addResult(0.0018533112423314033, 0, 1);
//        pcm.addResult(0.0022701891911307438, 0, 1);
//        pcm.addResult(0.03291794193450604, 0, 1);
//        pcm.addResult(0.008233678943155602, 0, 1);
//        pcm.addResult(-0.004903438039760263, 0, 1);
//        pcm.addResult(0.03885934523884008, 0, 1);
//        pcm.addResult(0.007719006068158463, 0, 1);
//        pcm.addResult(0.00788557772570104, 0, 1);
//        pcm.addResult(-0.003097929605811329, 1, 1);
//        pcm.addResult(0.051506075937249304, 0, 1);
//        pcm.addResult(-0.0011987502982501452, 1, 1);
//        pcm.addResult(0.02117185717640497, 0, 1);
//        pcm.addResult(0.0453240312310058, 0, 1);
//        pcm.addResult(0.008353015976544505, 0, 1);
//        pcm.addResult(-0.0028015807557513865, 0, 1);
//        pcm.addResult(0.05080100726743486, 0, 1);
//        pcm.addResult(0.022387988285097495, 0, 1);
//        pcm.addResult(6.397351970737243E-4, 0, 1);
//        pcm.addResult(0.0032983454907220167, 0, 1);
//        pcm.addResult(0.035519717196650834, 0, 1);
//        pcm.addResult(0.010547095536319238, 0, 1);
//        pcm.addResult(0.0033478940201267255, 0, 1);
//        pcm.addResult(0.011106747707684009, 0, 1);
//        pcm.addResult(0.0016997999142429464, 0, 1);
//        pcm.addResult(0.029220318902642116, 0, 1);
//        pcm.addResult(0.021485616016750357, 0, 1);
//        pcm.addResult(0.05837063231479757, 0, 1);
//        pcm.addResult(0.006610946582228829, 0, 1);
//        pcm.addResult(0.04903400873277938, 0, 1);
//        pcm.addResult(0.019231372465178663, 0, 1);
//        pcm.addResult(-9.37143023706094E-5, 1, 1);
//        pcm.addResult(0.013608648527938562, 0, 1);
//        pcm.addResult(0.01193827710326568, 0, 1);
//        pcm.addResult(0.05755155935699885, 0, 1);
//        pcm.addResult(0.021624337531673372, 0, 1);
//        pcm.addResult(0.006336358857290437, 0, 1);
//        pcm.addResult(0.004742132419965742, 0, 1);
//        pcm.addResult(0.004781552575267588, 0, 1);
//        pcm.addResult(0.015742768709534193, 0, 1);
//        pcm.addResult(-0.002138150437232354, 1, 1);
//        pcm.addResult(0.01956752488643207, 0, 1);
//        pcm.addResult(0.043810257875149264, 0, 1);
//        pcm.addResult(0.027420818728103306, 0, 1);
//        pcm.addResult(0.022770185462890148, 0, 1);
//        pcm.addResult(0.048199183737385916, 0, 1);
//        pcm.addResult(0.04866673872857487, 0, 1);
//        pcm.addResult(0.00663067767329523, 0, 1);
//        pcm.addResult(0.041489954069481416, 0, 1);
//        pcm.addResult(0.035670714104565404, 0, 1);
//        pcm.addResult(0.019469221089492197, 0, 1);
//        pcm.addResult(0.028760102175901678, 0, 1);
//        pcm.addResult(-0.004270102983618582, 1, 1);
//        pcm.addResult(0.055347116491729784, 0, 1);
//        pcm.addResult(-0.005167934205127943, 1, 1);
//        pcm.addResult(0.004155991603338344, 0, 1);
//        pcm.addResult(0.023678394199660592, 0, 1);
//        pcm.addResult(-0.014934016393955132, 0, 1);
//        pcm.addResult(0.05307889263177437, 0, 1);
//        pcm.addResult(0.008797658905177926, 0, 1);
//        pcm.addResult(0.05094973571894878, 0, 1);
//        pcm.addResult(0.06193558718270124, 0, 1);
//        pcm.addResult(0.04215934831254653, 0, 1);
//        pcm.addResult(0.008700372780179716, 0, 1);
//        pcm.addResult(0.04556598133853372, 0, 1);
//        pcm.addResult(0.04819646800524305, 0, 1);
//        pcm.addResult(0.043189792600386884, 0, 1);
//        pcm.addResult(0.011943191604647813, 0, 1);
//        pcm.addResult(0.019147612618223727, 0, 1);
//        pcm.addResult(0.04658776087800964, 0, 1);
//        pcm.addResult(0.03265775822699616, 0, 1);
//        pcm.addResult(0.06127841333683359, 0, 1);
//        pcm.addResult(0.009314848841776503, 0, 1);
//        pcm.addResult(0.018544384234284145, 0, 1);
//        pcm.addResult(0.040821626523766585, 0, 1);
//        pcm.addResult(0.015551674931604885, 0, 1);
//        pcm.addResult(-5.255087032530938E-4, 0, 1);
//        pcm.addResult(0.006149109370800014, 0, 1);
//        pcm.addResult(0.05486120188266928, 0, 1);
//        pcm.addResult(0.02919199503282386, 0, 1);
//        pcm.addResult(0.048374238600307255, 0, 1);
//        pcm.addResult(0.04486678970354238, 0, 1);
//        pcm.addResult(0.05441871397094357, 0, 1);
//        pcm.addResult(0.01649620078248793, 0, 1);
//        pcm.addResult(-0.0024601550193736033, 0, 1);
//        pcm.addResult(0.011990000003873479, 0, 1);
//        pcm.addResult(0.005962473330434417, 0, 1);
//        pcm.addResult(-0.003420314004698567, 0, 1);
//        pcm.addResult(0.04281034348606068, 0, 1);
//        pcm.addResult(0.009018276363173143, 0, 1);
//        pcm.addResult(0.05098207778956064, 0, 1);
//        pcm.addResult(0.01893624450709183, 0, 1);
//        pcm.addResult(0.03843223826689594, 0, 1);
//        pcm.addResult(0.0263554620361628, 0, 1);
//        pcm.addResult(0.026631528123736613, 0, 1);
//        pcm.addResult(-0.0013546518487357077, 1, 1);
//        pcm.addResult(0.019202330369907337, 0, 1);
//        pcm.addResult(0.04194973488934829, 0, 1);
//        pcm.addResult(0.042159580350375495, 0, 1);
//        pcm.addResult(0.032398319005347904, 0, 1);
//        pcm.addResult(-0.0018351780470980319, 0, 1);
//        pcm.addResult(0.004296489168756182, 0, 1);
//        pcm.addResult(0.04017064549838013, 0, 1);
//        pcm.addResult(0.004256130530164997, 0, 1);
//        pcm.addResult(-0.0035061443753785586, 1, 1);
//        pcm.addResult(0.016760959359114737, 0, 1);
//        pcm.addResult(0.0023261186793455436, 0, 1);
//        pcm.addResult(0.018245084789830696, 0, 1);
//        pcm.addResult(-0.006378559712904757, 1, 1);
//        pcm.addResult(-0.00601808141407289, 1, 1);
//        pcm.addResult(-0.015796397601959583, 1, 1);
//        pcm.addResult(0.03548845604620849, 0, 1);
//        pcm.addResult(-0.0042038203900711035, 1, 1);
//        pcm.addResult(0.0049284180929002755, 0, 1);
//        pcm.addResult(0.05647368179317316, 0, 1);
//        pcm.addResult(0.015414379001708776, 0, 1);
//        pcm.addResult(0.01244923995922149, 0, 1);
//        pcm.addResult(0.005740277006535475, 0, 1);
//        pcm.addResult(0.009888257835189974, 1, 1);
//        pcm.addResult(5.804390396204045E-5, 0, 1);
//        pcm.addResult(0.024754654937345986, 0, 1);
//        pcm.addResult(0.015142659215615613, 0, 1);
//        pcm.addResult(0.0052149480567617035, 0, 1);
//        pcm.addResult(0.052430855120576186, 0, 1);
//        pcm.addResult(0.001827976530776555, 0, 1);
//        pcm.addResult(0.004418924607581986, 0, 1);
//        pcm.addResult(0.06221799347935829, 0, 1);
//        pcm.addResult(0.009326151668124746, 0, 1);
//        pcm.addResult(0.01823180421216599, 0, 1);
//        pcm.addResult(0.039717218797835324, 0, 1);
//        pcm.addResult(-0.015174679112153207, 1, 1);
//        pcm.addResult(0.04127787284642923, 0, 1);
//        pcm.addResult(-0.004296905190335338, 1, 1);
//        pcm.addResult(-8.114499086568638E-5, 1, 1);
//        pcm.addResult(-0.0032044877065973577, 1, 1);
//        pcm.addResult(0.006155605594402831, 0, 1);
//        pcm.addResult(0.02061202112385499, 0, 1);
//        pcm.addResult(0.039152554561058595, 0, 1);
//        pcm.addResult(-0.0019580712417071253, 0, 1);
//        pcm.addResult(0.02939367240443553, 0, 1);
//        pcm.addResult(0.00977399757084435, 0, 1);
//        pcm.addResult(0.05574411573595867, 0, 1);
//        pcm.addResult(4.739448830364739E-4, 0, 1);
//        pcm.addResult(0.0289709944534456, 0, 1);
//        pcm.addResult(-0.0010123965230559152, 0, 1);
//        pcm.addResult(0.02206651673030798, 0, 1);
//        pcm.addResult(0.004034439096995442, 0, 1);
//        pcm.addResult(0.0443691211784192, 0, 1);
//        pcm.addResult(0.0086977451145738, 0, 1);
//        pcm.addResult(0.016747018691417707, 0, 1);
//        pcm.addResult(-1.708183080126064E-4, 1, 1);
//        pcm.addResult(0.05368065924412779, 0, 1);
//        pcm.addResult(0.007592705050063573, 0, 1);
//        pcm.addResult(0.05070409549008456, 0, 1);
//        pcm.addResult(0.00708529514227704, 0, 1);
//        pcm.addResult(-0.023648256515429518, 1, 1);
//        pcm.addResult(0.00856047481475581, 0, 1);
//        pcm.addResult(0.016498564009115993, 0, 1);
//        pcm.calculateThresholds();
//        System.out.println(pcm.getHits());
//    }

    public BasicDRS(double MIN, double MAX, int[] slots) {
        this.min = MIN;
        this.max = MAX;
        this.slots = slots;
    }

    /**
     * Calculates the thresholds in some way so as to enable classification.
     */
    public void calculateThresholds() {

        int maxClassID = 0;
        for (int i = 0; i < classes.size(); i++) {
            int classID = classes.elementAt(i);
            if (classID > maxClassID) maxClassID = classID;
        }

        int slotCount[][] = new int[slots.length][maxClassID + 1];

        for (int i = 0; i < cachedResults.size(); i++) {
            CachedOutput cachedOutput = cachedResults.elementAt(i);
            // which slot does the output fit into?
            int slot = getSlotIndex(cachedOutput.rawOutput);
            slotCount[slot][cachedOutput.expectedClass]++;
        }

        for (int COUNT = 0; COUNT < slots.length; COUNT++) {
            int CL = defaultClass;
            int highest = 0;
            for (int j = 0; j < slotCount[COUNT].length; j++) {
                if (slotCount[COUNT][j] > highest) {
                    CL = j;
                    highest = slotCount[COUNT][j];
                }
            }
            slots[COUNT] = CL;
        }

    }

/*    protected int getSlotIndex(double raw) {
        int slot = (int) raw;
        if (slot < MIN) slot = MIN;
        if (slot > MAX) slot = MAX;
        int index =  slot - MIN;
        return index;
    }*/

    protected int getSlotIndex(double raw) {
    	//POEY comment: max = 25, min = -25
    	//numSlots = the number of classes * 7
    	//raw is a calculated value of a pixel
        if (raw > max) raw = max;
        else if (raw < min) raw = min;
        double RANGE = max - min;
        double adjusted = ((raw - min) / RANGE) * numSlots;
        return (int) adjusted;
    }

    /**
     * Given an output, returns the classification
     */
    public int getClassFromOutput(double raw) {
        return slots[getSlotIndex(raw)];
    }

    /**
     * Turns the map into java that allows it to be reinstantiated.
     */
    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("new BasicDRS(");
        buffer.append(min);
        buffer.append(",");
        buffer.append(max);
        buffer.append(",new int[]{");
        for (int i = 0; i < slots.length; i++) {
            buffer.append(slots[i]);
            if (i < slots.length - 1) buffer.append(",");
        }
        buffer.append("});");
        return buffer.toString();
    }

}
