package jasmine.classify.classifier;


import jasmine.classify.data.Data;

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
public class NearestNeighbourClassifier extends Classifier {

    protected KNearestNeighbour knn;

    public NearestNeighbourClassifier(Vector<Data> trainingData) {

        knn = new KNearestNeighbour(2);

        for (int i = 0; i < trainingData.size(); i++) {
            Data data = trainingData.elementAt(i);
            //POEY comment:  getValuesDouble() return a calculated value of an object
            knn.add(data.getValuesDouble(), data.classID);
        }

    }

    public int classify(float[] values) {
    	//POEY comment: values.length = the number of functions    	
        double[] dv = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            dv[i] = values[i];
        }
        return knn.classify(dv);
    }

}
