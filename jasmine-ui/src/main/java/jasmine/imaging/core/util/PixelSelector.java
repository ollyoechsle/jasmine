package jasmine.imaging.core.util;


import jasmine.imaging.core.JasmineProject;
import jasmine.imaging.core.JasmineUtils;

import java.util.Vector;

/**
 * Convenience class
 */
public class PixelSelector {

    // pixel selection mode
    public static final int PIXEL_SELECTION_BY_CLASS = 0;
    public static final int PIXEL_SELECTION_BY_CLUSTER = 1;

    protected int selectionMode; // by class or by cluster?

    public PixelSelector(JasmineProject project) {
        this(PixelSelector.getPixelSelectionMode(project));
    }

    public PixelSelector(int selectionMode) {
        this.selectionMode = selectionMode;
    }

    private static final String PIXEL_SELECTION_MODE = "pixel_selection_mode";

    public static void setPixelSelectionMode(JasmineProject project, int mode) {
        project.addProperty(PIXEL_SELECTION_MODE, mode);
    }

    public static int getPixelSelectionMode(JasmineProject project) {
        if (project == null) return PIXEL_SELECTION_BY_CLASS;
        Integer mode = (Integer) project.getProperty(PIXEL_SELECTION_MODE);
        if (mode == null) {
            return PIXEL_SELECTION_BY_CLASS;
        } else {
            return mode;
        }
    }

    public Vector<ImagePixel> choosePixels(Vector<ImagePixel> allPixels, float totalProportion, JasmineProject project, int overlayType) {
        if (getPixelSelectionMode(project) == PIXEL_SELECTION_BY_CLASS) {
            System.out.println("Selecting pixels by class");
            return JasmineUtils.choosePixelsByClass(allPixels, overlayType, totalProportion, project);
        } else {
            System.out.println("Selecting pixels by cluster");
            return JasmineUtils.choosePixelsByCluster(allPixels, totalProportion, project, overlayType);
        }
    }

    public String toString() {
        switch(selectionMode) {
            case PIXEL_SELECTION_BY_CLASS:
                return "By Class";
            case PIXEL_SELECTION_BY_CLUSTER:
                return "By Cluster (Unsupervised)";
        }
        return "Unknown";
    }

}
