package jasmine.gp.nodes.imaging.parameterised;


import jasmine.gp.tree.Terminal;

import java.util.Vector;

/**
 * Interface for terminals requiring their own parameters
 */
public abstract class ParameterisedTerminal extends Terminal implements Comparable {

    public float score;

    public abstract ParameterisedTerminal getRandom();

    /**
     * Returns a random number between the min and max inclusive
     */
    protected int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min+1)) + min;
    }

    /**
     * Returns some useful defaults
     */
    public abstract Vector<ParameterisedTerminal> getDefaults();


/*    public static void main(String[] args) {
        int[] counts = new int[11];
        for (int i = 0; i < 100000; i++) {
            counts[getRandom(-5, 5)+5]++;
        }
        for (int i = 0; i < counts.length; i++) {
            int count = counts[i];
            System.out.println(i + ", " + count);
        }
    }*/

    public int compareTo(Object o) {
        float otherScore = ((ParameterisedTerminal) o).score;
        if (score > otherScore) return -1;
        if (score < otherScore) return +1;
        return 0;
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterisedTerminal that = (ParameterisedTerminal) o;

        Object[] thatArgs = that.getConstructorArgs();
        Object[] thisArgs = this.getConstructorArgs();

        if (thatArgs.length != thisArgs.length) return false;

        for (int i = 0; i < thisArgs.length; i++) {
            if (!thisArgs[i].equals(thatArgs[i])) return false;
        }

        return true;

    }

    public int hashCode() {
        return 0;
    }
}
