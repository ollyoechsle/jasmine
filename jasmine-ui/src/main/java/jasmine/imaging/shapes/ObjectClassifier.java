package jasmine.imaging.shapes;


import jasmine.gp.multiclass.ClassResults;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 20-Apr-2009
 * Time: 15:26:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class ObjectClassifier implements Serializable {

    public abstract int classify(SegmentedObject shape);
    
    public String test(JasmineProject project) {
        try {

            // get the shapes from the project
            Vector<SegmentedObject> objects = JasmineUtils.getLabelledObjects(project);

            ClassResults results = new ClassResults();           
            for (int i = 0; i < project.getObjectClasses().size(); i++) {
                JasmineClass jasmineClass = project.getObjectClasses().elementAt(i);
                results.addClass(jasmineClass.name, jasmineClass.classID);
            } 
            for (int i = 0; i < objects.size(); i++) {
                SegmentedObject shape = objects.elementAt(i);
                
                //POEY comment: classify the training set
                int classID = classify(shape);
                System.out.println(classID + " --- " + shape.getClassID());
                if (classID == shape.getClassID()) {
                    //results.addHit(classID);
                	//POEY
                	results.addHit(shape.getClassID());
                } else {
                    //results.addMiss(classID);
                	//POEY
                	results.addMiss(shape.getClassID());
                }

            }

            return results.toHTML();

        } catch (Exception err) {
            System.err.println("// Can't run on unseen data: " + err.getMessage());
            err.printStackTrace();
            return err.getMessage();
        }
    }


}
