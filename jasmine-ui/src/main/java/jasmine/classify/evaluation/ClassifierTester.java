package jasmine.classify.evaluation;


import jasmine.classify.classifier.Classifier;
import jasmine.classify.data.Data;
import jasmine.classify.data.DataStatistics;

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
 * @author Olly Oechsle, University of Essex, Date: 01-Aug-2008
 * @version 1.0
 */
public class ClassifierTester {


    public static ClassifierTestResults testMulticlass(Vector<Integer> classes, Classifier classifier, Vector<Data> testData) {

        int N = 0;
        int TP = 0;
        int TN = 0;

        ClassConfusionMatrix m = new ClassConfusionMatrix(classes);
        //POEY comment: highestClassID = 10
        int[] mistakes = new int[DataStatistics.highestClassID+1];

        Vector<Data> falsePositives = new Vector<Data>(100);
        Vector<Data> falseNegatives = new Vector<Data>(100);
        //POEY comment: testData.size() = the number of objects
        for (int j = 0; j < testData.size(); j++) {
            Data data = testData.elementAt(j);

            int expected = data.classID;

            N++;

            //POEY comment: not sure that which classify function
            //maybe jasmine.classify.classifier.GPClassifier.java
            int output = classifier.classify(data);

            if (output < 0) output = 0;

            m.addConfusion(expected, output);
            
            if (output > 0) {
                if (output == expected) {
                    TP++;
                } else {
                    // False negative
                    falseNegatives.add(data);
                    mistakes[data.classID]++;
                }
            } else {
                if (expected == 0) {
                    TN++;
                } else {
                    // False Positive
                    falsePositives.add(data);
                    mistakes[data.classID]++;
                }
            }

        }

        float percentage = (TP + TN) / (float) N;
        ClassifierTestResults t = new ClassifierTestResults(-1, N, percentage, TP);
        for (int c = 1; c < mistakes.length; c++) {
            int output = mistakes[c];
            float confusionPercentage = output / (float) N;
            t.add(new ClassConfusion(c, output, confusionPercentage));
        }

        t.setFalsePositives(falsePositives);
        t.setFalseNegatives(falseNegatives);

        m.calculatePercentages();
        t.classConfusionMatrix = m;

        return t;

    }

    public static ClassifierTestResults testBinarySingleClass(Classifier classifier, Vector<Data> testData, int classID) {
        return testBinarySingleClass(classifier, testData, classID, true);
    }

    public static ClassifierTestResults testBinarySingleClass(Classifier classifier, Vector<Data> testData, int classID, boolean recordMistakes) {

        int N = 0;

        int TP = 0;
        int TN = 0;
        int FP = 0;
        int FN = 0;

        int[] outputs = new int[DataStatistics.highestClassID+1];

        Vector<Data> falsePositives = new Vector<Data>(100);
        Vector<Data> falseNegatives = new Vector<Data>(100);
        //POEY comment: the testData set is the trainData set
        //testData.size() = the number of objects
        for (int j = 0; j < testData.size(); j++) {
            Data data = testData.elementAt(j);

            int expected = data.classID;

            int output;

            // ignore data with different
            if (expected != classID) {
                if (recordMistakes && data.weight > 0) {	//not this case
                    output = classifier.classify(data);
                    if (output < 0) output = 0;
                    if (output > 0) {
                        FP++;
                        falsePositives.add(data);
                    }
                }
                continue;
            } else {
            	//jasmine.classify.classifier.NearestNeighbourClassifier.java
            	//POEY comment: output is predicted classID by the k-nearest neighbour method
                output = classifier.classify(data);
                //POEY comment: real classID starts at #1
                if (output < 0) output = 0;
            }

            N++;

            outputs[output]++;

            if (expected == 0) {

                if (output == expected) {
                    TN++;
                }
                
            } else {
                if (output == expected) {
                    TP++;
                } else {
                    FN++;
                    if (recordMistakes) falseNegatives.add(data);
                }
            }

        }

        float percentage = (TP) / (float) N;
        ClassifierTestResults t = new ClassifierTestResults(classID, N, percentage, TP);
        for (int c = 1; c < outputs.length; c++) {
            int numMistakes = outputs[c];
            if (c != classID && numMistakes > 0) {
                float confusionPercentage = numMistakes / (float) N;
                t.add(new ClassConfusion(c, numMistakes, confusionPercentage));
            }
        }

        t.setFalsePositives(falsePositives);
        t.setFalseNegatives(falseNegatives);

        return t;

    }

    public static ClassifierTestResults testBinary(Vector<Integer> classes, Classifier classifier, Vector<Data> testData, int classID) {

        int N = 0;

        int TP = 0;
        int TN = 0;
        int FP = 0;
        int FN = 0;

        if (!classes.contains(0)) classes.add(0);
        ClassConfusionMatrix m = new ClassConfusionMatrix(classes);

        int[] outputs = new int[DataStatistics.highestClassID+1];

        Vector<Data> falsePositives = new Vector<Data>(100);
        Vector<Data> falseNegatives = new Vector<Data>(100);

        for (int j = 0; j < testData.size(); j++) {
            Data data = testData.elementAt(j);

            int expected = data.classID;

            if (expected != classID) continue;

            N++;

            int output = classifier.classify(data);

            if (output < 0) {
                output = 0;
            }

            m.addConfusion(expected, output);

            outputs[output]++;

            if (expected == 0) {

                if (output == expected) {
                    TN++;
                } else {
                    FP++;
                    falsePositives.add(data);
                }
            } else {
                if (output == expected) {
                    TP++;
                } else {
                    FN++;
                    falseNegatives.add(data);
                }
            }

        }

        float percentage = (TP + TN) / (float) N;
        ClassifierTestResults t = new ClassifierTestResults(classID, N, percentage, TP);
        for (int c = 1; c < outputs.length; c++) {
            int numMistakes = outputs[c];
            if (c != classID && numMistakes > 0) {
                float confusionPercentage = numMistakes / (float) N;
                t.add(new ClassConfusion(c, numMistakes, confusionPercentage));
            }
        }

        t.setFalsePositives(falsePositives);
        t.setFalseNegatives(falseNegatives);
        m.calculatePercentages();
        t.classConfusionMatrix = m;

        return t;

    }



}
