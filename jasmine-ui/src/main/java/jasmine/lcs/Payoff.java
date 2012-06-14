package jasmine.lcs;

/**
 * Represents payoff from the environment. Payoff may provide reinforcement and also
 * signal whether the environment requires resetting.
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public class Payoff {

    protected boolean finished;
    protected double amount;

    /**
     * Constructs the payoff object
     * @param amount How much payoff should the classifier system receive?
     * @param finished Are any more steps required?
     */
    public Payoff(double amount, boolean finished) {
        this.amount = amount;
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public double getAmount() {
        return amount;
    }

    public String toString() {
        return "£" + amount + ", Finished: " + finished;
    }
    
}
