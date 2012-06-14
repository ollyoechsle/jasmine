package jasmine.classify.evaluation;

import java.util.Vector;
import java.text.DecimalFormat;

/**
* Represents a class confusion, or false positive, where a class
* was incorrectly recognised as another one.
*
* @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
* @version 1.0
*/
public class ClassConfusionMatrix {

    float[] totals;
    float[][] matrix;

    Vector<Integer> classes;

    public ClassConfusionMatrix(Vector<Integer> classes) {
        this.classes = classes;
        this.matrix = new float[classes.size()][classes.size()];
    }

    public int getClassIndex(int ID) {
        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);
            if (classID == ID) return i;
        }
        return -1;
    }

    public int getClassFromIndex(int i)  {
        return classes.elementAt(i);
    }

    public void addConfusion(int expectedClass, int classifiedClass) {
        try {
        matrix[getClassIndex(expectedClass)][getClassIndex(classifiedClass)]++;
        } catch (Exception e)  {
            System.err.println("Expected: " + expectedClass + ", classified: " + classifiedClass);
            e.printStackTrace();
        }
    }

    public void calculatePercentages() {
        totals = new float[classes.size()];
        for (int i = 0; i < matrix.length; i++) {
            float[] floats = matrix[i];
            float total = 0;
            for (int j = 0; j < floats.length; j++) {
                total += floats[j];
            }
            totals[i] = total;
            for (int j = 0; j < floats.length; j++) {
                //floats[j] /= total;
            }
        }
    }

    public float getPercentage(int classID) {
        int index = getClassIndex(classID);
        return matrix[index][index];
    }

    public void print() {

        DecimalFormat f = new DecimalFormat("0.000");

        System.out.print("---,");
        //POEY comment: print classID
        for (int i = 0; i < classes.size(); i++) {
            Integer classID = classes.elementAt(i);
            System.out.print(classID);
            if (i < classes.size() - 1) {
                System.out.print(",");
            }
        }
        System.out.println();
        for (int i = 0; i < classes.size(); i++) {
            Integer expectedClass = classes.elementAt(i);
            //POEY comment: print classID
            System.out.print(expectedClass);
            System.out.print(",");
            int index = getClassIndex(expectedClass);
            float[] values = matrix[index];
            for (int j = 0; j < values.length; j++) {
                float confusion = values[j];
                //POEY comment: print the number of objects in this classID
                System.out.print(f.format(confusion));
                System.out.print(",");
            }
            //POEY comment: print the number of objects in this classID
            System.out.print(getPercentage(expectedClass));
            System.out.print(",");
            //POEY comment: print the number of objects in this classID
            System.out.print(totals[i]);
            System.out.println();
        }
    }

}
