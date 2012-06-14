package jasmine.gp;

/**
 * Simple counter which makes sure that all the crossovers and mutations etc
 * are happening as they should.
 *
 * Note that in coevolution problems these values will be increased for both
 * population types, so if you have different parameters for each then the results
 * here may be somewhat different to what you expect.
 *
 * @author Olly Oechsle, University of Essex, Date: 22-Jan-2008
 * @version 1.0
 */
public class OperationCounter {

    public static int CROSSOVER_COUNT = 0;
    public static int FAILED_CROSSOVER_COUNT = 0;
    public static int FAILED_MUTATION_COUNT = 0;
    public static int REPRODUCTION_COUNT = 0;
    public static int POINT_MUTATION_COUNT = 0;
    public static int ERC_MUTATION_COUNT = 0;
    public static int ERC_JITTER_COUNT = 0;

    public static void reset() {
        CROSSOVER_COUNT = 0;
        FAILED_CROSSOVER_COUNT = 0;
        FAILED_MUTATION_COUNT = 0;
        REPRODUCTION_COUNT = 0;
        POINT_MUTATION_COUNT = 0;
        ERC_MUTATION_COUNT = 0;
        ERC_JITTER_COUNT = 0;
    }

    public static String getResults() {
        int MUTATION_COUNT = POINT_MUTATION_COUNT + ERC_MUTATION_COUNT + ERC_JITTER_COUNT;
        double totalOperations = CROSSOVER_COUNT + FAILED_CROSSOVER_COUNT + MUTATION_COUNT + FAILED_MUTATION_COUNT + REPRODUCTION_COUNT;
        double totalMutations = POINT_MUTATION_COUNT + ERC_MUTATION_COUNT + ERC_JITTER_COUNT;
        StringBuffer buffer = new StringBuffer();
        buffer.append("// Successful Crossover: " + (CROSSOVER_COUNT / totalOperations));
        buffer.append("\n// Failed Crossovers: " + (FAILED_CROSSOVER_COUNT / totalOperations));
        buffer.append("\n// Successful Mutation: " + (MUTATION_COUNT / totalOperations));
        buffer.append("\n// Failed Mutation: " + (FAILED_MUTATION_COUNT / totalOperations));
        buffer.append("\n// Reproduction: " + (REPRODUCTION_COUNT / totalOperations));
        buffer.append("\n");
        buffer.append("\n// Point Mutation: " + (POINT_MUTATION_COUNT / totalMutations));
        buffer.append("\n// ERC Mutation: " + (ERC_MUTATION_COUNT / totalMutations));
        buffer.append("\n// ERC Jitter: " + (ERC_JITTER_COUNT / totalMutations));
        return buffer.toString();
    }

//    public static void main(String[] args) {
//        GPParams params = new GPParams();
//        for (int i = 0; i <2000; i++) {
//        switch (params.getOperator()) {
//            case GPParams.CROSSOVER:
//                CROSSOVER_COUNT++;
//                break;
//            case GPParams.MUTATION:
//                POINT_MUTATION_COUNT++;
//                break;
//            case GPParams.REPRODUCTION:
//                REPRODUCTION_COUNT++;
//                break;
//        }
//
//
//        }
//
//        System.out.println(getResults());
//
//    }

}
