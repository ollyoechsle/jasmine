package jasmine.imaging.core;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.core.util.TrainingObject;
import jasmine.imaging.shapes.SegmentedObject;

import javax.swing.*;

import com.bbn.openmap.dataAccess.image.geotiff.GeoTIFFFile;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.Vector;

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
 * @author Panitnat Yimyam, University of Essex, Date: 13-July-2010
 * @version 1.0
 */
public class JasmineTestingImage extends TrainingObject {

    //protected String filename;
    
    //POEY
    protected String filenameTesting;
    
    protected JasmineProject project;
    //protected String materialOverlayFilename = null;
    //protected String maskOverlayFilename = null;
    public Vector<SegmentedObject> objects;
    protected int width, height;

    // ClassID: in case the whole image is to be classified (for future versions of Jasmine)
    public JasmineTestingImage(String filename, int classID, int type) {
        super(classID, type);
        objects = new Vector<SegmentedObject>(10);
        this.filenameTesting = filename;
        
 /*       //POEY
        if(type==0) 	//training image
        	this.filename = filename;
        else if(type==1){ 	//testing image       	
        	this.filenameTesting = filename;
        	System.out.println("type = 1, filenameTesting = "+this.filenameTesting);
        }*/
    }

    public String getFilename() {
        return filenameTesting;
    }

    public JasmineProject getProject() {
        return project;
    }
/*
    public String getMaterialOverlayFilename() {
        return materialOverlayFilename;
    }

    public String getMaskOverlayFilename() {
        return materialOverlayFilename;
    }*/

    public void addObject(SegmentedObject s) {
        if (!objects.contains(s)) {
            objects.add(s);
            project.setChanged(true, "Added shape to " + filenameTesting);
        }
    }

    public Vector<SegmentedObject> getObjects() {
        return objects;
    }

    public void setObjects(Vector<SegmentedObject> objects) {
        this.objects = objects;
    }

    public void clearObjects() {
        if (objects.size() > 0) {
            objects.removeAllElements();
            project.setChanged(true, "Cleared objects from " + filenameTesting);
        }
    }

    public void removeObject(SegmentedObject object) {
        objects.removeElement(object);
        project.setChanged(true, "Removed object from " + filenameTesting);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    //POEY
    public BufferedImage getBufferedTestingImage() {
        try {
            if (project == null) {
                System.err.println("Project is null");
            }
            if (filenameTesting == null) {
                System.err.println("FilenameTesting is null");
            }
            
          //POEY
            if(filenameTesting.endsWith(".tif")){
    			String filenameTif = project.getTestingImageLocation().getAbsolutePath()+File.separator+filenameTesting;   			
        		GeoTIFFFile gtf = new GeoTIFFFile(filenameTif);                  
        		return gtf.getBufferedImage();
            }
            
            return javax.imageio.ImageIO.read(new File(project.getTestingImageLocation(), filenameTesting));
        } catch (javax.imageio.IIOException e1) {
            JOptionPane.showMessageDialog(null, "Could not load image!\n" + e1.getMessage());
        } catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "Could not load image!\n" + e2.getMessage());
        }
        return null;
    }
/*
    public Vector<Pixel> getOverlayPixels(int type) throws IOException {
        return OverlayData.loadData(this, type);
    }

    public Vector<Pixel> getMaterialOverlayPixels() throws IOException {
        return OverlayData.loadData(this, JasmineClass.MATERIAL);
    }

    public Vector<Pixel> getMaskOverlayPixels() throws IOException {
        return OverlayData.loadData(this, JasmineClass.MASK);
    }
*/
    public String toString() {
        // if the image has a classID - currently not supported.
 //       JasmineClass c = project == null? null : project.getClass(this);
        StringBuffer buffer = new StringBuffer();
 //       if (materialOverlayFilename != null) buffer.append("* ");
//        else  buffer.append("  ");

    	buffer.append(filenameTesting);
 
/*    	if (c == null) {
            if (classID != -1) {
                buffer.append(" - [NO CLASS: " + classID + "]");
            }
        } else {
            buffer.append(" - " + c.name);
        }        
        if (objects != null && objects.size() > 0) {
            buffer.append(" (");
            buffer.append(objects.size());
            buffer.append(")");
        }
*/
    	return buffer.toString();
    }

}

