package jasmine.imaging.core;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents Jasmine Classes
 * @author Olly Oechsle, University of Essex, Date: 11-Dec-2006
 * @version 1.0
 */
public class JasmineClass implements Serializable, Comparable {

    public static final int MASK = 0;
    public static final int MATERIAL = 1;
    public static final int OBJECT = JasmineClassificationPanel.SELECT_OBJECTS;
    public static final int SUB_OBJECT = JasmineClassificationPanel.SELECT_SUBOBJECTS;
    public static final int IMAGE = 3; // Not used
    
    public int type;
    public int classID;
    public String name;
    public Color color;
    public boolean background;

    public JasmineClass(int id, String name, int type, Color c, boolean background) {
        this.classID = id;
        this.name = name;
        this.type = type;
        this.color = c;
        this.background = background;
    }

    public int compareTo(Object o) {
        return name.compareTo(((JasmineClass) o).name);
    }

    public boolean matchRGB(int rgb) {
        return color.getRGB() == rgb;        
    }

    public static String getTypeName(int type) {
        switch (type) {
            case MASK: return "Mask";
            case MATERIAL: return "Material";
            case OBJECT: return "Object";
            case SUB_OBJECT: return "Sub Object";
        }
        return "Other";
    }

    public String toString() {
        if (!background) {
            return name;
        } else {
            return name + " (background)";
        }
    }

}
