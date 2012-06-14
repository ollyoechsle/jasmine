package jasmine.gp.nodes.imaging.parameterised;


import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.ColourChannels;
import jasmine.imaging.commons.FastStatistics;
import jasmine.imaging.commons.StatisticsSolver;

import java.util.Vector;

/**
 * Returns the mean intensity of a region. See feature node for more info.
 *
 * @author Olly Oechsle, University of Essex, Date: 06-Nov-2007
 * @version 1.0
 */
public class GenericNxNFeature extends ParameterisedTerminal {

    public static final int MAX_RADIUS = 10;
    public static final int MIN_RADIUS = 3;

    protected int channel, size, stat;

    public GenericNxNFeature() {
        this(ColourChannels.HUE,  2, StatisticsSolver.MEAN);
    }

    public GenericNxNFeature(int channel, int size, int stat) {
        this.channel = channel;
        this.size = size;
        this.stat = stat;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255, NodeConstraints.FEATURE};
    }

    public double execute(DataStack ds) {

        FastStatistics s = ds.getImage().get3x3Stats(ds.getX(), ds.getY(), channel, size);       
        ds.value = s.getStatistic(stat);
        return debugger == null? ds.value : debugger.record(ds.value);

    }

    public String toJava() {
         return "image.get3x3Stats(x,y," + channel + "," + size + ").getStatistic(" + stat + ")";
    }

    public String getShortName() {
        return "nXn";
    }

    public ParameterisedTerminal getRandom() {
        int channel = getRandom(0,13);
        int size = getRandom(2,10);
        int stat = getRandom(1,6);
        return new GenericNxNFeature(channel, size, stat);
    }

    public Vector<ParameterisedTerminal> getDefaults() {
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
        int size = 5;
        //POEY comment: actually there are 0->16 channels ???
        for (int channel = 0; channel <= 13; channel++) {
            for (int stat = StatisticsSolver.RANGE; stat <= StatisticsSolver.VARIANCE; stat++) {
                defaults.add(new GenericNxNFeature(channel, size, stat));
            }
        }
        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{channel, size, stat};
    }

    public String toString() {
        return "NxN Features";
    }

}