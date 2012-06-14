package jasmine.imaging.core.util;

import java.io.Serializable;

/**
 * Training type
 */
public class TrainingObject implements Serializable {

    public static final int TRAINING = 0;
    public static final int TESTING = 1;

    protected int type = 0;
    protected int classID = 0;

    public TrainingObject(int classID, int type) {
        this.classID = classID;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }
    

}
