package jasmine.gp.problems;


import jasmine.gp.training.TrainingImage;

import java.util.Vector;

/**
 * A problem involving images. Identical to the standard problem class, except there
 * is a vector of training images.
 */
public abstract class ImagingProblem extends Problem {

    protected Vector<TrainingImage> trainingImages;

    /**
     * Gets the training data, which in this case consists of a set of images.
     */
    public Vector<TrainingImage> getImages() {
        return trainingImages;
    }

    public int getImageCount() {
        if (trainingImages != null) return trainingImages.size();
        return 0;
    }

    public String getImageName(int index) {
        if (trainingImages != null) {
            TrainingImage t = trainingImages.elementAt(index);
                String name = t.getName();
            if (name != null) return name;
        }
        return "Image " + index;
    }
    
}
