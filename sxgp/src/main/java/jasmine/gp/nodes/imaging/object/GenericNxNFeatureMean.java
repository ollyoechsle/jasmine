//package jasmine.gp.nodes.imaging.object;
//
//import jasmine.gp.nodes.imaging.parameterised.ParameterisedTerminal;
//import jasmine.gp.params.NodeConstraints;
//import jasmine.gp.problems.DataStack;
//import jasmine.imaging.commons.ColourChannels;
//import jasmine.imaging.commons.FastStatistics;
//import jasmine.imaging.commons.StatisticsSolver;
//import jasmine.imaging.shapes.ExtraShapeData;
//
//import java.util.Vector;
//
////POEY
//public class GenericNxNFeatureMean extends ParameterisedTerminal {
//
//    public static final int MAX_RADIUS = 10;
//    public static final int MIN_RADIUS = 3;
//
//    protected int channel, size, stat;
//
//    public GenericNxNFeatureMean() {
//        this(ColourChannels.HUE,  2, StatisticsSolver.MEAN);
//    }
//
//    public GenericNxNFeatureMean(int channel, int size, int stat) {
//        this.channel = channel;
//        this.size = size;
//        this.stat = stat;
//    }
//
//    public int[] getReturnTypes() {
//        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
//    }
//
//    public double execute(DataStack ds) {
//    	ds.usesImaging = true;
//        FastStatistics s = ((ExtraShapeData) ds.getData()).get3x3StatsMean(channel, size);       
//        ds.value = s.getStatistic(stat);
//        return debugger == null? ds.value : debugger.record(ds.value);
//
//    }
//
//    public String toJava() {
//         return "object.get3x3StatsMean(" + channel + "," + size + ").getStatistic(" + stat + ")";
//    }
//
//    public String getShortName() {
//        return "nXnm";
//    }
//
//    public ParameterisedTerminal getRandom() {
//        int channel = getRandom(0,13);
//        int size = getRandom(2,10);
//        int stat = getRandom(1,6);
//        return new GenericNxNFeatureMean(channel, size, stat);
//    }
//
//    public Vector<ParameterisedTerminal> getDefaults() {
//        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
//        int size = 5;
//        //POEY comment: actually there are 0->16 channels ???
//        for (int channel = 0; channel <= 13; channel++) {
//            for (int stat = StatisticsSolver.RANGE; stat <= StatisticsSolver.VARIANCE; stat++) {
//                defaults.add(new GenericNxNFeatureMean(channel, size, stat));
//            }
//        }
//        return defaults;
//    }
//
//    public Object[] getConstructorArgs() {
//        return new Object[]{channel, size, stat};
//    }
//
//    public String toString() {
//        return "NxN Mean Features";
//    }
//
//}
