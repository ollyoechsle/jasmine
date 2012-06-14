package jasmine.classify.data;

import java.util.Vector;

/**
 * Provides various statistics about certain kinds of data.
 * Also establishes classID mappings.
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class DataStatistics {

    public static int highestClassID = 10;
    
    protected int[] classCounts;
    protected Vector<Integer> classes;
    protected int dataCount;
    protected int numFeatures;

    public static ClassIDMapping classIDMapping = new ClassIDMapping();
    
    //POEY comment: count pixels/objects and pixels/objects in each class
    public DataStatistics(Vector<Data> data) {

        classes = new Vector<Integer>(10);
        classCounts = new int[150];

        boolean canMaintainClassIDs = true;
        // figure out whether a classID mapping is needed
        try {

            Vector<Integer> classIDs = new Vector<Integer>();
            //POEY comment: data.size() = the number of pixels/objects
            for (int i = 0; i < data.size(); i++) {
                Data data1 =  data.elementAt(i);
                int classID = Integer.parseInt(data1.className);
                if (!classIDs.contains(classID)) {
                    classIDs.add(classID);
                }
            }

            // check the classIDs
            // if there are 5 classes, they must be: 1,2,3,4,5
            for (int i = 0; i < classIDs.size(); i++) {
                if (!classIDs.contains(i + 1)) {
                    canMaintainClassIDs = false;
                    break;
                }
            }

        } catch (NumberFormatException e) {
            // non numeric classID - cannot treat the classIDs properly.
            canMaintainClassIDs = false;
            //System.out.println("ClassIDs cannot be maintained because one of the class names is not an number");
        }

        // Loop through the data and figure out a classID mapping
        for (int i = 0; i < data.size(); i++) {
            Data data1 =  data.elementAt(i);
            classIDMapping.add(data1, canMaintainClassIDs);
        }

        for (int i = 0; i < data.size(); i++) {
            Data d =  data.elementAt(i);
            if (!classes.contains(d.classID)) {
                classes.add(d.classID);
                this.numFeatures = d.values.length;
                // record the highest class ID.
                if (d.classID > highestClassID) {
                    highestClassID = d.classID;
                }
            }
            classCounts[d.classID]++;
        }

        dataCount = data.size();
        
    }

    public static void reset() {
        classIDMapping.reset();
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public int getClassCount() {
        return classes.size();
    }

    public int getClassCount(int classID) {
        return classCounts[classID];
    }

    public float getClassPercentageOfTotal(int classID) {
        return classCounts[classID] / (float) dataCount;
    }

    public int getDataCount() {
        return dataCount;
    }

    public Vector<Integer> getClassIDs() {
        return classes;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Class, Instances, Frequency\n");
        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);
            buffer.append(classID);
            buffer.append(", ");
            buffer.append(getClassCount(classID));
            buffer.append(", ");
            buffer.append(getClassPercentageOfTotal(classID));
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public String getSummary() {
        return dataCount + " instances, " + classes.size() + " classes";
    }
    
}
