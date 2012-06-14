package jasmine.lcs;


import java.util.Vector;

/**
 * A representation of the basic environment which changes its state and returns feedback to the
 * learning classifier system.
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public abstract class Environment {

    /**
     * Are multiple steps required to solve the problem?
     */
    public abstract boolean isMultiStep();

    /**
     * Returns the name of the environment
     */
    public abstract String getName();

    /**
     * Initialises (or resets) the environment
     */
    public abstract void initialise();

    /**
     * Returns the current state of the environment as an input vector
     */
    public abstract InputVector getInput();

    /**
     * Returns the number of inputs that make up each input vector
     */
    public abstract int getFeatureCount();

    /**
     * Updates the environment with a certain action
     * @param a The action to takeAction the environment with
     * @return Any payoff that is recieved as a result of updating the environment.
     */
    public abstract Payoff takeAction(Action a);

    /**
     * Gets all the available actions that may be performed on this environment. Note
     * that the Environment base class caches the result of this function so it only
     * needs to be called once. To clear the cache you can call the clearActionCache() function.
     */
    public abstract Vector<Action> getActions();

    // caches the actions so the getActions method doesn't have to be continuously evaluated.
    private Vector<Action> actionCache = null;

    /**
     * Gets a random action
     */
    public Action getRandomAction() {
        if (actionCache == null) {
            actionCache = getActions();
        }
        return actionCache.elementAt((int) (Math.random() * actionCache.size()));
    }

    /**
     * Clears the action cache, in case you need to update the actions.
     */
    protected void clearActionCache() {
        actionCache = null;
    }

    /**
     * Creates a new random condition which is compatible with the input
     * vectors in the environment.
     */
    public abstract Condition getRandomCondition();

    /**
     * Gets a condition which matches a given input vector
     */
    public abstract Condition getConditionToCover(InputVector i);

}
