package jasmine.imaging.core;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;

import java.util.Vector;
import java.util.Hashtable;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.*;

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
 * @author Olly Oechsle, University of Essex, Date: 11-Dec-2006
 * @version 1.0
 */
public class JasmineProject implements Serializable {

    static final long serialVersionUID = 3L;

    private File imageLocation;// = new File("/home/ooechs/Desktop/clouds-9806/pix");

    //POEY
    private File testingImageLocation;
    
    public Vector<JasmineClass> classes;

    private Vector<JasmineImage> images;
    
    //POEY
    private Vector<JasmineTestingImage> testingImages;
    
    private File filename;
    private int cursor = 0;
    
    //POEY
    private int cursorTesting = 0;
    
    private boolean changed = false;
    private String projectName;

    //public JasmineProject(String projectName, File imageLocation) {
    public JasmineProject(String projectName, File imageLocation, File testingImageLocation) {
        this.projectName = projectName;
        this.imageLocation = imageLocation;

        //POEY
        this.testingImageLocation = testingImageLocation;
        
        classes = new Vector<JasmineClass>(10);
        images = new Vector<JasmineImage>(100);
        
        //POEY
        testingImages = new Vector<JasmineTestingImage>(100);
        
        properties = new Hashtable<String, Object>(10);
        // add two classes
        classes.add(new JasmineClass(1, "Background", JasmineClass.MASK,  Color.BLACK, true));
        classes.add(new JasmineClass(2, "Object", JasmineClass.MASK,  Color.WHITE, false));
    }

    public static JasmineProject load(File f) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream in = new ObjectInputStream(bis);
        JasmineProject project = (JasmineProject) in.readObject();
        project.changed = false;
        project.cursor = 0;
        
        //POEY
        project.cursorTesting = 0;
        
        in.close();
        return project;
    }

    public String getName() {
        return projectName;
    }

    public void setName(String projectName) {
        this.projectName = projectName;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }
    
    //POEY
    public void setCursorTesting(int cursor) {
        this.cursorTesting = cursor;
    }

    public File getImageLocation() {
        return imageLocation;
    }

    //POEY
    public File getTestingImageLocation() {
        return testingImageLocation;
    }
    
    public void setImageLocation(File imageLocation) {
        this.imageLocation = imageLocation;
    }

    //POEY
    public void setTestingImageLocation(File testingImageLocation) {
        this.testingImageLocation = testingImageLocation;       
    }
    
    //POEY comment: get a name of the project    
    public File getFilename() {
        return filename;
    }

    public void setFilename(File filename) {
        this.filename = filename;
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean removeClass(JasmineClass c) {
        return classes.remove(c);
    }

    public void setChanged(boolean changed, String reason) {
        System.out.println("Project Changed: " + reason);
        this.changed = changed;
    }

    /**
     * @return False if classID already exists
     */
    public boolean addClass(JasmineClass c) {
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == c.type && jasmineClass.classID == c.classID) return false;
        }
        classes.add(c);
        setChanged(true, "Added " + JasmineClass.getTypeName(c.type) + " class to project.");
        return true;
    }

    public boolean hasNextImage() {
        return cursor < images.size() - 1;
    }

    public boolean hasPrevImage() {
        return cursor > 0;
    }
    
    //POEY
    public boolean hasNextTestingImage() {
        return cursorTesting < testingImages.size() - 1;
    }

    //POEY
    public boolean hasPrevTestingImage() {
        return cursorTesting > 0;
    }

    //POEY
    public boolean checkFirstTestingImage() {
        return cursorTesting==0 ? true:false;
    }
    
    //POEY
    public boolean checkFirstImage() {
        return cursor==0 ? true:false;
    }

    public JasmineImage currentImage() {
        if (images.size() == 0) return null;
        // ensure cursor is not out of bounds.
        if (cursor > images.size() - 1) {
            cursor = images.size() - 1;
        }
        return images.elementAt(cursor);
    }
    
    //POEY
    public JasmineTestingImage currentTestingImage() {
        if (testingImages.size() == 0) return null;
        // ensure cursor is not out of bounds.
        if (cursorTesting > testingImages.size() - 1) {
        	cursorTesting = testingImages.size() - 1;
        }
        return testingImages.elementAt(cursorTesting);
    }

    public void moveNext() {
        if (hasNextImage()) cursor++;
    }

    public void movePrev() {
        if (hasPrevImage()) cursor--;
    }
    
    //POEY
    public void moveNextTesting() {
        if (hasNextTestingImage()) cursorTesting++;
    }
    //POEY
    public void movePrevTesting() {
        if (hasPrevTestingImage()) cursorTesting--;
    }

    public void clearClasses(int type) {
        Vector<JasmineClass> toRemove = new Vector<JasmineClass>(10);
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == type) {
                toRemove.add(jasmineClass);
            }
        }
        classes.removeAll(toRemove);
        setChanged(true, "Cleared " + toRemove.size() + " " + JasmineClass.getTypeName(type) + "classes");
    }

    public boolean addImage(JasmineImage img) {
        for (int i = 0; i < images.size(); i++) {
            JasmineImage image = images.elementAt(i);
            if (image.filename.equals(img.filename)) return false;
        }
        images.add(img);
        setChanged(true, "Added new image");

        // need to update the cursor (see currentImage())
        cursor = images.size() - 1;
        
        img.project = this;
        return true;
    }
    
    //POEY
    public boolean addTestingImage(JasmineTestingImage img) {
        for (int i = 0; i < testingImages.size(); i++) {
        	JasmineTestingImage image = testingImages.elementAt(i);
            if (image.filenameTesting.equals(img.filenameTesting)) return false;
        }
        testingImages.add(img);
        setChanged(true, "Added new testing image");

        // need to update the cursor (see currentImage())
        cursorTesting = testingImages.size() - 1;
        
        img.project = this;
        return true;
    }

    public int getNextClassID(int type) {
        int max = 0;
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == type && jasmineClass.classID > max) {
                max = jasmineClass.classID;
            }
        }
        return max + 1;
    }

    public Vector<JasmineClass> getPixelClasses(int mode) {
        if (mode == JasmineClass.MATERIAL) {	//POEY comment: in this case, mode = 1 ?       	
            return getMaterialClasses();
        } else {	
            return getMaskClasses();
        }
    }

    public Vector<JasmineClass> getMaskClasses() {
        Vector<JasmineClass> maskClasses = new Vector<JasmineClass>(10);
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == JasmineClass.MASK) maskClasses.add(jasmineClass);
        }
        return maskClasses;
    }

    public Vector<JasmineClass> getMaterialClasses() {
        Vector<JasmineClass> materialClasses = new Vector<JasmineClass>(10);        
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == JasmineClass.MATERIAL)	//POEY comment: for segmentation, not this case
            	materialClasses.add(jasmineClass);	
        }
        return materialClasses;
    }

    public Vector<JasmineClass> getObjectClasses() {
        Vector<JasmineClass> shapeClasses = new Vector<JasmineClass>(10);
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == JasmineClass.OBJECT) shapeClasses.add(jasmineClass);
        }
        return shapeClasses;
    }

    public Vector<JasmineClass> getSubObjectClasses() {
        Vector<JasmineClass> shapeClasses = new Vector<JasmineClass>(10);
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == JasmineClass.SUB_OBJECT) shapeClasses.add(jasmineClass);
        }
        return shapeClasses;
    }

    public Vector<JasmineClass> getClasses(int type) {
    	//POEY comment: for segmentation type=0
        Vector<JasmineClass> chosen = new Vector<JasmineClass>(10);
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == type) chosen.add(jasmineClass);
        }
        return chosen;
    }            

    // not sure this is used anymore - this was a very old idea for per-image classification, which isn't currently supported.
    public JasmineClass getClass(JasmineImage image) {
        return getMaterialClass(image.getClassID());
    }

    public JasmineClass getPixelClass(int id, int pixelMode) {
        if (pixelMode == JasmineClass.MATERIAL) return getMaterialClass(id);
        else return getMaskClass(id);
    }

    public JasmineClass getMaskClass(int id) {
        Vector<JasmineClass> maskClasses = getMaskClasses();
        for (int i = 0; i < maskClasses.size(); i++) {
            JasmineClass jasmineClass = maskClasses.elementAt(i);
            if (jasmineClass.classID == id) return jasmineClass;
        }
        return null;
    }

    public JasmineClass getMaterialClass(int id) {
        Vector<JasmineClass> materialClasses = getMaterialClasses();
        for (int i = 0; i < materialClasses.size(); i++) {
            JasmineClass jasmineClass = materialClasses.elementAt(i);
            if (jasmineClass.classID == id) return jasmineClass;
        }
        return null;
    }

    public JasmineClass getShapeClass(int id) {
        Vector<JasmineClass> objectClasses = getObjectClasses();
        for (int i = 0; i < objectClasses.size(); i++) {
            JasmineClass jasmineClass = objectClasses.elementAt(i);
            if (jasmineClass.classID == id) return jasmineClass;
        }
        return null;
    }

    public JasmineClass getSubObjectClass(int id) {
        Vector<JasmineClass> subObjectClasses = getSubObjectClasses();
        for (int i = 0; i < subObjectClasses.size(); i++) {
            JasmineClass jasmineClass = subObjectClasses.elementAt(i);
            if (jasmineClass.classID == id) return jasmineClass;
        }
        return null;
    }

    public Vector<JasmineImage> getImages() {    	
        return images;
    }
    
    //POEY
    public Vector<JasmineTestingImage> getTestingImages() {   
        return testingImages;
    }

    public JasmineClass getClassFromRGB(int rgb, int type)  {
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            if (jasmineClass.type == type && jasmineClass.matchRGB(rgb))
            return jasmineClass;
        }
        return null;
    }

    //POEY comment: save a name of the project
    public void save(File f) throws IOException {
        this.filename = f;
        FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);
        out.close();
        setChanged(false, "Saved changes");
    }

    protected Hashtable properties;

    public void addProperty(String property, Object value) {
        Object existing = properties.get(property);
        // don't update the project unnecessarily
        if (existing != null && existing.equals(value)) return;
        setChanged(true, "Updated property: " + property);
        if (value != null) {
            properties.put(property, value);
        } else {
            properties.remove(property);
        }
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }

    public Vector<JasmineClassStatistics> getSubObjectStatistics() throws IOException  {

        Vector<JasmineClass> subobjectClasses = getSubObjectClasses();

        Vector<JasmineClassStatistics> stats = new Vector<JasmineClassStatistics>(subobjectClasses.size());

        // go through each class
        for (int i = 0; i < subobjectClasses.size(); i++) {
            JasmineClass shapeClass = subobjectClasses.elementAt(i);

            int imageCount = 0;
            int instanceCount = 0;

            for (int j = 0; j < images.size(); j++) {
                JasmineImage jasmineImage = images.elementAt(j);

                Vector<SegmentedObject> shapes = jasmineImage.getObjects();

                int instancesInThisImage = 0;

                for (int k = 0; k < shapes.size(); k++) {
                    SegmentedObject shape = shapes.elementAt(k);

                    for (int l = 0; l < shape.subobjects.size(); l++) {
                        SegmentedShape segmentedShape = shape.subobjects.elementAt(l);
                        if (segmentedShape.classID == shapeClass.classID) {
                            instancesInThisImage++;
                        }
                    }
                    
                }

                if (instancesInThisImage > 0)  {
                    imageCount++;
                    instanceCount += instancesInThisImage;
                }

            }

            stats.add(new JasmineClassStatistics(shapeClass, instanceCount, imageCount));

        }

        return stats;

    }


    public Vector<JasmineClassStatistics> getObjectStatistics() throws IOException  {

        Vector<JasmineClass> shapeClasses = getObjectClasses();

        Vector<JasmineClassStatistics> stats = new Vector<JasmineClassStatistics>(shapeClasses.size());

        // go through each class
        for (int i = 0; i < shapeClasses.size(); i++) {
            JasmineClass shapeClass = shapeClasses.elementAt(i);

            int imageCount = 0;
            int instanceCount = 0;

            for (int j = 0; j < images.size(); j++) {
                JasmineImage jasmineImage = images.elementAt(j);

                Vector<SegmentedObject> shapes = jasmineImage.getObjects();

                int instancesInThisImage = 0;

                for (int k = 0; k < shapes.size(); k++) {
                    SegmentedObject shape = shapes.elementAt(k);
                    if (shape.getClassID() == shapeClass.classID) {
                        instancesInThisImage++;
                    }
                }

                if (instancesInThisImage > 0)  {
                    imageCount++;
                    instanceCount += instancesInThisImage;
                }

            }

            stats.add(new JasmineClassStatistics(shapeClass, instanceCount, imageCount));

        }

        return stats;

    }

    public Vector<JasmineClassStatistics> getMaskStatistics() throws IOException {

        Vector<JasmineClass> maskClasses = getMaskClasses();

        Vector<JasmineClassStatistics> stats = new Vector<JasmineClassStatistics>(maskClasses.size());

        // go through each class
        for (int j = 0; j < maskClasses.size(); j++) {
            JasmineClass pixelClass = maskClasses.elementAt(j);

            int imageCount = 0;
            int instanceCount = 0;

            for (int i = 0; i < images.size(); i++) {
                JasmineImage jasmineImage = images.elementAt(i);
                if (jasmineImage.maskOverlayFilename != null) {
                	//POEY comment: load selected pixels
                    Vector<Pixel> pixels = jasmineImage.getMaskOverlayPixels();
                    int pixelsInThisImage = 0;
                    for (int k = 0; k < pixels.size(); k++) {
                        Pixel overlayPixel = pixels.elementAt(k);
                        //POEY comment: value is classID of a pixel
                        if (overlayPixel.value == pixelClass.classID) pixelsInThisImage++;
                    }
                    
                    if (pixelsInThisImage > 0) {
                        imageCount++;
                        instanceCount += pixelsInThisImage;
                    }
                }
            }

			//POEY comment: instanceCount = the number of selected pixels of all images for each type
			//imageCount = the number of images
            stats.add(new JasmineClassStatistics(pixelClass,  instanceCount, imageCount));

        }

        return stats;

    }

    public Vector<JasmineClassStatistics> getMaterialStatistics() throws IOException {

        Vector<JasmineClass> pixelClasses = getMaterialClasses();

        Vector<JasmineClassStatistics> stats = new Vector<JasmineClassStatistics>(pixelClasses.size());

        // go through each class
        for (int j = 0; j < pixelClasses.size(); j++) {
            JasmineClass pixelClass = pixelClasses.elementAt(j);

            int imageCount = 0;
            int instanceCount = 0;

            for (int i = 0; i < images.size(); i++) {
                JasmineImage jasmineImage = images.elementAt(i);
                if (jasmineImage.materialOverlayFilename != null) {
                    Vector<Pixel> pixels = jasmineImage.getMaterialOverlayPixels();
                    int pixelsInThisImage = 0;
                    for (int k = 0; k < pixels.size(); k++) {
                        Pixel overlayPixel = pixels.elementAt(k);
                        if (overlayPixel.value == pixelClass.classID) pixelsInThisImage++;
                    }
                    if (pixelsInThisImage > 0) {
                        imageCount++;
                        instanceCount += pixelsInThisImage;
                    }
                }
            }

            stats.add(new JasmineClassStatistics(pixelClass,  instanceCount, imageCount));

        }

        return stats;

    }

    public void exportMaterialPixels() throws IOException {
        // go through each image
        for (int i = 0; i < images.size(); i++) {
            JasmineImage jasmineImage = images.elementAt(i);
            if (jasmineImage.materialOverlayFilename != null) {
                BufferedImage img = jasmineImage.getBufferedImage();
                Vector<Pixel> pixels = jasmineImage.getMaterialOverlayPixels();
                // for each pixel
                for (int j = 0; j < pixels.size(); j++) {
                    Pixel overlayPixel = pixels.elementAt(j);
                    Color c = new Color(img.getRGB(overlayPixel.x, overlayPixel.y));
                    String csv = jasmineImage.filename + ", "  + jasmineImage.getClassID() + ", " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue();
                    System.out.println(csv);
                }
            }
        }
    }

}
