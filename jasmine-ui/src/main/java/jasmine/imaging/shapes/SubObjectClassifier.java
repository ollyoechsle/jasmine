package jasmine.imaging.shapes;


import jasmine.gp.multiclass.ClassResults;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.core.JasmineClass;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;

import java.util.Vector;

/**
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version,
 * provided that any use properly credits the author.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details at http://www.gnu.org
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 08-Feb-2007
 * @version 1.0
 */
public abstract class SubObjectClassifier {

    public int classify(SegmentedShape shape, PixelLoader image) {
        return classify(new ExtraShapeData(shape, image));
    }

    public abstract int classify(ExtraShapeData shape);

    public int TP = 0;
    public int FP = 0;
    public double percentage;
    public boolean[] results;

    public String test(JasmineProject project) {
        try {

            // get the shapes from the project
            Vector<ExtraShapeData> shapes = JasmineUtils.getLabelledSubObjects(project);

            ClassResults results = new ClassResults();
            for (int i = 0; i < project.getSubObjectClasses().size(); i++) {
                JasmineClass jasmineClass = project.getSubObjectClasses().elementAt(i);
                results.addClass(jasmineClass.name, jasmineClass.classID);
            }

            for (int i = 0; i < shapes.size(); i++) {
                ExtraShapeData shape = shapes.elementAt(i);

                int classID = classify(shape);
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
            return err.getMessage();
        }
    }

}
