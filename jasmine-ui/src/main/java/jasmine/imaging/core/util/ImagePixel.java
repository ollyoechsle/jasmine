package jasmine.imaging.core.util;

import jasmine.imaging.commons.PixelLoader;

/**
 * Stores a pixel and its associated pixel loader
 */
public class ImagePixel extends TrainingObject{

        public int x,  y, classID;
        public PixelLoader image;

        public ImagePixel(int x, int y, int classID, PixelLoader image) {
            super(classID, TrainingObject.TRAINING);
            this.x = x;
            this.y = y;
            this.classID = classID;
            this.image = image;
        }
    
}