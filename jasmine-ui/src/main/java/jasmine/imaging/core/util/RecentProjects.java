package jasmine.imaging.core.util;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 24-Mar-2009
 * Time: 14:33:25
 * To change this template use File | Settings | File Templates.
 */
public class RecentProjects implements Serializable {

    public Vector<File> projects;

    public RecentProjects() {
        projects = new Vector<File>();
    }

    public void add(File f) {
        if (!projects.contains(f)) {
            projects.add(f);
        }
    }

    public int size() {
        return projects.size();
    }

}
