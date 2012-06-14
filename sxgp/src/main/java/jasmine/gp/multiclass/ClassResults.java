package jasmine.gp.multiclass;

import jasmine.imaging.shapes.ObjectClassifier;

import java.util.ArrayList;
import java.util.Vector;
import java.text.DecimalFormat;

/**
 * Holds data about the results of a multiclass experiment.
*
* @author Olly Oechsle, University of Essex, Date: 09-Feb-2007
* @version 1.0
*/
public class ClassResults {

    int total = 0;
    int hits = 0;

    public Vector<ClassResult> classes;

    public ClassResults() {
        classes = new Vector<ClassResult>(10);
    }

    public void addClass(String name, int classID)  {
        classes.add(new ClassResult(name, classID));
    }

    public void addHit(int classID) {
        ClassResult c = getClassResult(classID);
        if (c != null) {
            c.registerHit();
            hits++;
            total++;           
        }
    }

    public int getHits() {
        return hits;
    }

    public int getTotal() {
        return total;
    }

    public void addMiss(int classID) {
        ClassResult c = getClassResult(classID);
        if (c != null) {
            c.registerMiss();
            total++;
        }
    }

    public int getMistakes() {
        return total - hits;
    }

    public String getPercentage() {
        if (total == 0) return "-";
        if (ClassResult.format == null);
        ClassResult.format = new DecimalFormat("0.00");
        return ClassResult.format.format(100*hits/(float) total);
    }


    public ClassResult getClassResult(int classID) {
        for (int i = 0; i < classes.size(); i++) {
            ClassResult classResult = classes.elementAt(i);
            if (classResult.classID == classID) return classResult;
        }
        return null;
    }

    public String toHTML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table border='0'>");
        buffer.append("<tr><th width='100'>Class</th><th width='80'>n</th><th width='80'>Hits</th><th width='80'>Mistakes</th><th width='80'>%</th></tr>");
        for (int i = 0; i < classes.size(); i++) {
            ClassResult classResult = classes.elementAt(i);            
            buffer.append("<tr>");
            buffer.append("<td>");
            buffer.append(classResult.name);
            buffer.append("</td><td>");
            buffer.append(classResult.getTotal());
            buffer.append("</td><td>");
            buffer.append(classResult.getHits());
            buffer.append("</td><td>");
            buffer.append(classResult.getMistakes());  
            buffer.append("</td></td>");
            buffer.append(classResult.getPercentage());
            buffer.append("</td></tr>");
        }
        buffer.append("<tr><th>Total</th><td>");
        buffer.append(getTotal());
        buffer.append("</td><td>");
        buffer.append(getHits());
        buffer.append("</td><td>");
        buffer.append(getMistakes());
        buffer.append("</td><td>");
        buffer.append(getPercentage());
        buffer.append("</table>");
        return buffer.toString();        
    }

}
