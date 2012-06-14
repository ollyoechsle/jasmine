package jasmine.imaging.core;


import jasmine.imaging.commons.util.CSVReader;
import jasmine.imaging.core.util.TrainingObject;
import jasmine.imaging.shapes.SegmentedShape;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
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
 * @author Olly Oechsle, University of Essex, Date: 13-Dec-2006
 * @version 1.0
 */
public class Importer {

    public static Color getColor(int i)  {
        switch (i)  {
            case 0:
                return Color.RED;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.CYAN;
            case 5:
                return Color.MAGENTA;
            case 6:
                return Color.GRAY;
            case 7:
                return Color.PINK;
            case 8:
                return Color.ORANGE;
            case 9:
                return Color.WHITE;
            default:
                int r = (int) (Math.random() * 255);
                int g = (int) (Math.random() * 255);
                int b = (int) (Math.random() * 255);
                return new Color(r,g,b);
        }
    }

    public static Color getColor(String name)  {
        name = name.toLowerCase();
        if (name.equals("red")) return Color.RED;
        if (name.equals("yellow")) return Color.YELLOW;
        if (name.equals("pink")) return Color.PINK;
        if (name.equals("green")) return Color.GREEN;
        if (name.equals("orange")) return Color.ORANGE;
        if (name.equals("purple")) return Color.MAGENTA;
        if (name.equals("blue")) return Color.BLUE;
        if (name.equals("light blue")) return Color.CYAN;
        if (name.equals("white")) return Color.WHITE;
        if (name.equals("gray")) return Color.GRAY;
        if (name.equals("black")) return Color.BLACK;
                int r = (int) (Math.random() * 255);
                int g = (int) (Math.random() * 255);
                int b = (int) (Math.random() * 255);
                return new Color(r,g,b);
    }

    public static String importClasses(JasmineProject project, File f) throws IOException, RuntimeException {
        CSVReader reader = new CSVReader(f);
        StringBuffer message = new StringBuffer(100);
        while (reader.hasMoreLines()) {
            Vector<String> data = reader.getLine();
            int type = Integer.parseInt(data.elementAt(0));
            int id = Integer.parseInt(data.elementAt(1));
            String name = data.elementAt(2);
            if (!project.addClass(new JasmineClass(id, name, type, getColor(id), false))) {
                message.append("Class ID " + id + " clashes with existing classID; " + name + " was not added.\n");
            }
        }
        if (message.length() == 0) return null;
        return message.toString();
    }

    public static int importImages(JasmineProject project, File f) throws IOException, RuntimeException {
        CSVReader reader = new CSVReader(f);
        int imageCount = 0;
        while (reader.hasMoreLines()) {
            Vector<String> data = reader.getLine();
            String filename = data.elementAt(0);
            int classID = -1;
            if (data.size() > 0) {
                classID = Integer.parseInt(data.elementAt(1));
            }
            if (project.addImage(new JasmineImage(filename, classID, TrainingObject.TRAINING))) imageCount++;
        }
        return imageCount;
    }

    /**
     * Imports the shapes saved in a CSV file back into a project. Fails if the specified image(s) do not exist.
     * Images should therefore be imported before shapes.
     * @param project The project into which the shapes are imported.
     * @param f The CSV File containing the data
     * @return The number of shapes successfully imported
     */
    public static int importShapes(JasmineProject project, File f) throws IOException {

        CSVReader reader = new CSVReader(f);

        int shapesImported = 0;

        while (reader.hasMoreLines()) {

            Vector<String> data = reader.getLine();

            String shapeClassID = data.elementAt(0);
            String shapeFilename = data.elementAt(1);

            // find the image in the jasmine project
            JasmineImage image = JasmineUtils.getImageByFilename(project, shapeFilename);

            if (image != null) {

                SegmentedShape s = new SegmentedShape(-1);
                s.classID = Integer.parseInt(shapeClassID);

                Vector<String> pixelData = reader.getLine();
                Vector<String> edgeData = reader.getLine();

                for (int i = 0; i < pixelData.size(); i+=2) {
                    int x =  Integer.parseInt(pixelData.elementAt(i));
                    int y = Integer.parseInt(pixelData.elementAt(i + 1));
                    s.add(x, y, false);
                }

                for (int i = 0; i < edgeData.size(); i+=2) {
                    int x =  Integer.parseInt(edgeData.elementAt(i));
                    int y = Integer.parseInt(edgeData.elementAt(i + 1));
                    s.add(x, y, true);
                }

                // TODO: Get working
                //image.addShape(s);
                shapesImported++;
                
            } else {
                System.out.println("Could not find image: " + shapeFilename);
            }

        }

        return shapesImported;

    }

}
