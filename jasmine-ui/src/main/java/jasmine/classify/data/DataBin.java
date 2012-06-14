package jasmine.classify.data;


import java.util.Vector;
import java.util.Random;

public class DataBin {

    Vector<Data> data;
    int classID;
    int size = 0;

    public DataBin(int classID) {
        this.classID = classID;
        data = new Vector<Data>(50);
    }

    public void add(Data d) {
        if (d.getLabel() != classID) throw new RuntimeException("Wrong data in bin " + classID);
        data.add(d);
        if (data.size() > size) size = data.size();
    }

    public int size() {
        return size;
    }

    public Data popData(Random r) {
        if (data.size() == 0) return null;
        Data d = data.elementAt((int) (data.size() * r.nextDouble()));
        data.remove(d);
        return d;
    }

}