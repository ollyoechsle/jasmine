package jasmine.imaging.core.util;



import jasmine.gp.util.Deployer;
import jasmine.imaging.commons.Segmenter;
import jasmine.imaging.commons.util.FileIO;
import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.shapes.ObjectClassifier;
import jasmine.imaging.shapes.SubObjectClassifier;

import javax.swing.*;
import java.io.*;

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
 * @author Olly Oechsle, University of Essex, Date: 21-Jun-2007
 * @version 1.0
 */
public class JasmineDeployer extends Deployer {

    protected Jasmine j;

    public JasmineDeployer(Jasmine j) {
        this.j = j;
    }

    public File[] getJars() {
        File f = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "lib");
        if (!f.exists()) {
            j.alert("Making JAR directory: " + f.getAbsolutePath());
            j.setStatusText("Making JAR directory");
            f.mkdirs();
        }

        File[] jars = new File[]{new File(f, "ecj-imaging.jar"), new File(f, "jasmine.jar"), new File(f, "sx-gp-2008.jar")};

        for (int i = 0; i < jars.length; i++) {
            File jarFile = jars[i];

            if (!jarFile.exists()) {
                if (!j.confirm("A file needs to be downloaded to your computer: " + jarFile.getAbsolutePath() + ". Download?")) {
                    return null;
                }
                // the jar file must be downloaded from the website for javac to work
                j.setStatusText("Downloading JAR file");
                FileIO.saveFromWeb(Jasmine.ONLINE_JAR_FILE_LOCATION + jarFile.getName(), jarFile);
                j.alert("Downloaded JAR file needed for compilation to: " + jarFile.getAbsolutePath());
                j.setStatusText("Download complete");
            }

        }
        return jars;
    }

    public boolean confirm(String s) {
            int response = JOptionPane.showConfirmDialog(j, s);
            return response == JOptionPane.YES_OPTION;
    }

    public void setStatusText(String s) {
        j.setStatusText(s);
    }

    public void alert(String s) {
        j.alert(s);
    }

    public static SubObjectClassifier getShapeClassifier(JasmineProject project) throws Exception {
        String subobject_class = (String) project.getProperty(VisionSystem.SUB_OBJECT_CLASSIFIER_HANDLE);
        if (subobject_class != null && subobject_class.trim().length() > 0) {
            if (new File(subobject_class).exists()) {
                return getShapeClassifier(new File(subobject_class));
            } else {
                return (SubObjectClassifier) Class.forName(subobject_class).newInstance();
            }
        }
        return null;
    }

    public static SubObjectClassifier getShapeClassifier(File classFile) throws Exception {

        Object o = instantiateClass(classFile);

        if (o != null && o instanceof SubObjectClassifier) {

            return (SubObjectClassifier) o;

        } else {
            throw new RuntimeException("Cannot instantiate class - it isn't a Shape Classifier");
        }

    }

    public static ObjectClassifier getObjectClassifier(File classFile) throws Exception {
        Object o = instantiateClass(classFile);
        if (o != null && o instanceof ObjectClassifier) {
            return (ObjectClassifier) o;
        } else {
            throw new RuntimeException("Cannot instantiate class - it isn't an object Classifier");
        }
    }

    public static ObjectClassifier getObjectClassifier(JasmineProject project) throws Exception {
        String object_class = (String) project.getProperty(VisionSystem.OBJECT_CLASSIFIER_HANDLE);
        if (object_class != null && object_class.trim().length() > 0) {
            File f = new File(object_class);
            if (f.exists()) {
                return getObjectClassifier(new File(object_class));
            } else {
                return (ObjectClassifier) Class.forName(object_class).newInstance();
            }
        }
        return null;
    }

    public static Segmenter getSegmenter(JasmineProject project, String handle) throws Exception {
        String segmenter_class = (String) project.getProperty(handle);
        if (segmenter_class != null && segmenter_class.trim().length() > 0) {
            File f = new File(segmenter_class);
            if (f.exists()) {
                return getSegmenter(new File(segmenter_class));
            } else {
                return (Segmenter) Class.forName(segmenter_class).newInstance();
            }
        }
        return null;
    }

    public static Segmenter getSegmenter(File classFile) throws Exception {
        Object o = instantiateClass(classFile);
        if (o != null && o instanceof Segmenter) {
            return (Segmenter) o;
        } else {
            throw new RuntimeException("Class file is not a Segmenter");
        }

    }

}
