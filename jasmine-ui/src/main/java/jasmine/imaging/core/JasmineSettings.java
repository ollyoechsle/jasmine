package jasmine.imaging.core;

import java.io.*;
import java.util.Hashtable;

/**
 * Stores settings
 */
public class JasmineSettings implements Serializable {    

    public static boolean VERBOSE = true;

    protected Hashtable<String, Object> properties;

    public void addProperty(String property, Object value) {
        if (VERBOSE) System.out.println("Updated property: " + property);
        properties.put(property, value);
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }

    public JasmineSettings() {
        properties = new Hashtable<String, Object>();
    }

    public static JasmineSettings load() {
        File f = getSaveLocation();
        if (f.exists()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream in = new ObjectInputStream(fis);
                JasmineSettings settings = (JasmineSettings) in.readObject();
                in.close();
                return settings;
            } catch (Exception e) {
                System.err.println("Could not load jasmine settings, recreating file. " + e.toString());
                return new JasmineSettings();
            }
        } else {
            return new JasmineSettings();
        }
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(getSaveLocation());
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(this);
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Could not save Jasmine settings");
        }
    }

    public static File getSaveLocation() {
        return new File(System.getProperty("user.home"), "jasmine.preferences");
    }

}
