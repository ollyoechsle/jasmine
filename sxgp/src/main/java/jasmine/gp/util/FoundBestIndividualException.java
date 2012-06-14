package jasmine.gp.util;

import jasmine.gp.Individual;

/**
 * <p>
 * Thrown if the ERC optimiser finds the best individual. Instead of checking
 * for fitness after an individual has been through the ERC optimiser, it is
 * easier to throw an exception, so the eventuality can be handled in one place.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 08-Jan-2008
 * @version 1.0
 */
public class FoundBestIndividualException extends Exception {

    protected Individual ind;

    public FoundBestIndividualException(Individual ind) {
        this.ind = ind;
    }

    public Individual getInd() {
        return ind;
    }

}
