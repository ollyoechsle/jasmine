package jasmine.classify.data;

import java.util.Hashtable;
import java.io.Serializable;

/**
 * Sorts out classIDs for data so that no classIDs are zero and
 * copes with both numeric and string based classIDs.
 *
 * @author Olly Oechsle, University of Essex, Date: 01-Sep-2008
 * @version 1.0
 */
public class ClassIDMapping implements Serializable {

    // gets classID from name
    public Hashtable<String, Integer> classFromNameMappings;
    // gets name from classID
    public Hashtable<Integer, String> nameFromClassMappings;
    private int counter = 1;

    public ClassIDMapping() {
        reset();
    }

    public void add(Data d, boolean maintain) {

        if (maintain) {
            // preserve the classIDs according to the classname 
            int classID = Integer.parseInt(d.className);
            d.classID = classID;
            if (classFromNameMappings.get(d.getClassName()) == null) {
                classFromNameMappings.put(d.getClassName(), classID);
                nameFromClassMappings.put(classID, d.getClassName());
            }
        } else {
            // assign classIDs in whatever way you want.
            Integer classID = classFromNameMappings.get(d.getClassName());
            if (classID == null) {
                classID = counter;
                counter++;
                classFromNameMappings.put(d.getClassName(), classID);
                nameFromClassMappings.put(classID, d.getClassName());
            }
            d.classID = classID;
        }
    }

    public String getName(int classID) {
        return nameFromClassMappings.get(classID);
    }

    public void reset() {
        classFromNameMappings = new Hashtable<String, Integer>(10);
        nameFromClassMappings = new Hashtable<Integer, String>(10);
        counter = 1;
    }

}
