package jasmine.imaging.core;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.util.CSVReader;

import java.awt.image.BufferedImage;
import java.util.Vector;
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
 * @author Olly Oechsle, University of Essex, Date: 13-Dec-2006
 * @version 1.0
 */
public class OverlayData{

    protected Vector<Pixel> data;
    protected JasmineImage image;
    protected JasmineProject project;
    protected int type;

    public OverlayData(BufferedImage overlayImage, JasmineProject project, JasmineImage image, int type) {

        this.type = type;
        this.project = project;
        this.image = image;

        data = new Vector<Pixel>(200);
        for (int y = 0; y < overlayImage.getHeight(); y++)
        for (int x = 0; x < overlayImage.getWidth(); x++) {
            int color = overlayImage.getRGB(x, y);
            if (color != 0) {
                // not transparent - find matching class
                JasmineClass c = project.getClassFromRGB(color, type);
                if (c != null) {
                    data.add(new Pixel(x, y, c.classID));
                }
            }
        }

        //System.out.println("Created overlay data: " + data.size() + "pixels");

    }

    public int size() {
        return data.size();
    }

    public static void renameMaterialOverlay(JasmineImage image, String newOverlayFilename) {
        String filename = getOverlayFilename(image, JasmineClass.MATERIAL);
        File f = new File(image.project.getImageLocation(), filename);
        System.out.println("Renaming " + image.materialOverlayFilename + " to " + newOverlayFilename);
        if (f.exists()) {
            f.renameTo(new File(image.project.getImageLocation(), newOverlayFilename));
        } else {
            System.err.println("Cannot rename " + image.materialOverlayFilename + ": file does not exist");
        }
        image.materialOverlayFilename = newOverlayFilename;
    }

   public static void renameMaskOverlay(JasmineImage image, String newOverlayFilename) {
        String filename = getOverlayFilename(image, JasmineClass.MASK);
        File f = new File(image.project.getImageLocation(), filename);
        System.out.println("Renaming " + image.maskOverlayFilename + " to " + newOverlayFilename);
        if (f.exists()) {
            f.renameTo(new File(image.project.getImageLocation(), newOverlayFilename));
        } else {
            System.err.println("Cannot rename " + image.maskOverlayFilename + ": file does not exist");
        }
        image.maskOverlayFilename = newOverlayFilename;
    }

    public static BufferedImage load(JasmineImage image, int pixelMode) throws IOException {
        String filename = getOverlayFilename(image, pixelMode);
        File f = new File(image.project.getImageLocation(), filename);
        if (f.exists()) {
            BufferedImage overlay = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            CSVReader reader = new CSVReader(f);
            while (reader.hasMoreLines()) {
                reader.getLine();
                int x = reader.getInt(0);
                int y = reader.getInt(1);
                int classID = reader.getInt(2);
                JasmineClass c = image.project.getPixelClass(classID, pixelMode);
                if (c != null && x < image.getWidth() && y < image.getHeight())
                overlay.setRGB(x, y, c.color.getRGB());
            }
            return overlay;
        } else {
            return null;
        }
    }

    public static Vector<Pixel> loadData(JasmineImage image, int pixelMode) throws IOException {
        String filename = getOverlayFilename(image, pixelMode);
        File f = new File(image.project.getImageLocation(), filename);
        Vector<Pixel> overlay = new Vector<Pixel>(500);
        if (f.exists()) {
        	//POEY comment: read a file in order to load selected pixels into lines
            CSVReader reader = new CSVReader(f);
            while (reader.hasMoreLines()) {
                reader.getLine();
                int x = reader.getInt(0);
                int y = reader.getInt(1);
                int classID = reader.getInt(2);
                JasmineClass c = image.project.getPixelClass(classID, pixelMode);
                if (c != null) overlay.add(new Pixel(x, y, classID));
            }
            return overlay;
        } else {
            return null;
        }
    }

    /**
     * Reads an overlay from a file. Without the need to involve a Jasmine project or image.
     */
    public static Vector<Pixel> loadData(File f) throws IOException {
        Vector<Pixel> overlay = new Vector<Pixel>(500);
        if (f.exists()) {
            CSVReader reader = new CSVReader(f);
            while (reader.hasMoreLines()) {
                reader.getLine();
                int x = reader.getInt(0);
                int y = reader.getInt(1);
                int id = reader.getInt(2);
                overlay.add(new Pixel(x, y, id));
            }
            return overlay;
        } else {
            return null;
        }
    }

    public void save() throws IOException {
        
        String filename = getOverlayFilename(image, type);
        File location = new File(project.getImageLocation(), filename);

        BufferedWriter out = new BufferedWriter(new FileWriter(location));
        for (int i = 0; i < data.size(); i++) {
            Pixel overlayPixel = data.elementAt(i);
            out.write(overlayPixel.toCSV() + "\n");
        }
        out.close();

        if (data.size() ==  0) {
            System.out.println("Deleting overlay file");
            if (location.exists()) location.delete();
            filename = null;
        }

        if (type == JasmineClass.MATERIAL) {
            if (filename != null) System.out.println("Saved material overlay to: " + location.getAbsolutePath());
            if (image.materialOverlayFilename == null ||!image.materialOverlayFilename.equals(filename)) project.setChanged(true, "Material overlay changed");
            image.materialOverlayFilename = filename;
        } else {
            if (filename != null) System.out.println("Saved mask to: " + location.getAbsolutePath());
            if (image.maskOverlayFilename == null || !image.maskOverlayFilename.equals(filename)) project.setChanged(true, "Mask overlay changed");
            image.maskOverlayFilename = filename;
        }
        
    }

    public static String getOverlayFilename(JasmineImage i, int type) {
        if (type == JasmineClass.MATERIAL)
        return i.filename + ".overlay";
        else	//POEY comment: for segmentation type=MASK
        return i.filename + ".mask";
    }




}
