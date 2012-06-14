package jasmine.classify.featureselection;

/**
 * Totals up values for use by the Entropy class
 * The table is two dimensional, rows are occupied by different attribute values,
 * and columns are occupied by different outcomes
 * <table>
 * <tr>
 * <td></td><td>Poor</td><td>Rich</td>
 * </tr>
 * <tr>
 * <td>Female</td><td>14423</td><td>1769</td>
 * </tr>
 * <tr>
 * <td>Male</td><td>22732</td><td>9918</td>
 * </tr>
 * </table>
 *
 * Which you could enter using the following Java:
 *
 * <pre>
 * Table t1 = new Table(2,2);
 * t1.add(FEMALE, POOR, 14423);
 * t1.add(FEMALE, RICH, 1769);
 * t1.add(MALE, POOR, 22732);
 * t1.add(MALE, RICH, 9918);
 * </pre>
 *
 * Calculate the Information Gain with
 * new InformationGain().getInformationGain(t1.table);
 */
public class Table {

    public int[][] table;

    public Table(int numAttributeValues, int numOutcomes) {
        table = new int[numAttributeValues][numOutcomes];
    }

    public void add(int attributeValue, int[] outcomes) {
        table[attributeValue] = outcomes;
    }

    public void add(int attributeValue, int outcome) {
        table[attributeValue][outcome]++;
    }

    public void add(int attributeValue, int outcome, int n) {
        table[attributeValue][outcome]+=n;
    }

}
