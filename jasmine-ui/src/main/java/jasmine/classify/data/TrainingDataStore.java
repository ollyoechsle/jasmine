package jasmine.classify.data;


import jasmine.classify.data.Data;

import java.util.Vector;
import java.util.Hashtable;

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
 * @author Olly Oechsle, University of Essex, Date: 14-Aug-2008
 * @version 1.0
 */
public class TrainingDataStore {

    Hashtable<Integer, Vector<Data>> store;

    public TrainingDataStore() {
        this.store = new Hashtable<Integer, Vector<Data>>(10);
    }

    public void put(int classID, Vector<Data> data)  {
        if (data == null) System.err.println("Data is null. NPE on its way");
        store.put(classID, data);
    }

    public Vector<Data> get(int classID) {
        return store.get(classID);
    }

}
