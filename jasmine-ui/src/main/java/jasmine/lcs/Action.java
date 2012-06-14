package jasmine.lcs;

/**
 * Represents an action that can be taken by the environment.
 * Essentially just wraps an integer, but can be extended to incorporate
 * more complicated actions and makes the code easier to read.
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public class Action {

    protected int id;

    public Action(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "Action: " + id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (id != action.id) return false;

        return true;
    }

    public int hashCode() {
        return id;
    }
    
}
