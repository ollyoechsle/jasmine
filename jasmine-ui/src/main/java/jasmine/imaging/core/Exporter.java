package jasmine.imaging.core;


import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.FastStatistics;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.CSVWriter;
import jasmine.imaging.core.util.ImagePixel;
import jasmine.imaging.core.util.PixelSelector;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.ShapePixel;

import java.util.Vector;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
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
 * @author Olly Oechsle, University of Essex, Date: 13-Dec-2006
 * @version 1.0
 */
public class Exporter {

    public static void exportClasses(JasmineProject project, File f) throws IOException {

        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write("# Classes exported from " + project.getName() + "\n");
        out.write("# Type, ClassID, Name\n");

        Vector<JasmineClass> classes = project.getMaterialClasses();
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            out.write(JasmineClass.MATERIAL + ", ");
            out.write(String.valueOf(jasmineClass.classID));
            out.write(", ");
            out.write(jasmineClass.name);
            out.write("\n");
        }
        classes = project.getObjectClasses();
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            out.write(JasmineClass.OBJECT + ", ");
            out.write(String.valueOf(jasmineClass.classID));
            out.write(", ");
            out.write(jasmineClass.name);
            out.write("\n");
        }

        out.close();

    }

    private static String prefix = "testing";
    private static boolean includeNameOnFeatureExport = true;

    public static void exportShapesAsImages(JasmineProject project, File directory) throws IOException {

        // ensure we are saving to a directory
        if (!directory.isDirectory()) directory = directory.getParentFile();

        Vector<ExtraShapeData> shapes = JasmineUtils.getTrainingData(project);

        int white = Color.WHITE.getRGB();

        CSVWriter writer = new CSVWriter();

        int highestWidth = 0;
        int highestHeight = 0;

        for (int i = 0; i < shapes.size(); i++) {
            ExtraShapeData data = shapes.elementAt(i);

            if (data.getWidth() > highestWidth) highestWidth = data.getWidth();
            if (data.getHeight() > highestHeight) highestHeight = data.getHeight();

            int shape_width = (data.getShape().maxX - data.getShape().minX) + 1;
            int shape_height = (data.getShape().maxY - data.getShape().minY) + 1;

            BufferedImage image = new BufferedImage(shape_width, shape_height, BufferedImage.TYPE_INT_RGB);

            for (int k = 0; k < data.getShape().pixels.size(); k++) {
                ShapePixel p = data.getShape().pixels.elementAt(k);
                image.setRGB(p.x - data.getShape().minX, p.y - data.getShape().minY, white);
            }

            double aspectRatio = 1d;
            double image_height = shape_height;
            double image_width = shape_width; //image_height * aspectRatio;


            BufferedImage background = new BufferedImage((int) image_width, (int) image_height, BufferedImage.TYPE_INT_RGB);
            Graphics g = background.getGraphics();
            g.drawImage(image, (int) ((image_width - image.getWidth()) / 2), (int) ((image_height - image.getHeight()) / 2), null);
            PixelLoader pl = new PixelLoader(background);

            // find a decent filename
            int classID = data.getShape().classID;
            JasmineClass c = project.getShapeClass(classID);
            int counter = 1;
            String filename;
            while (true) {
                if (c != null) {
                    filename = c.name + "_" + counter + ".bmp";
                } else {
                    filename = "unknown_" + counter + ".bmp";
                }
                File f = new File(directory, filename);
                if (!f.exists()) break;
                counter++;
            }

            //PixelLoader pl = new PixelLoader(image);

            try {
                //String filename = prefix + i + ".bmp";
                pl.saveAs(new File(directory, filename));
                writer.addData(filename);
                writer.addData(data.getShape().classID);
                writer.newLine();
            } catch (Exception e) {
                System.err.println("Cannot save file for some reason.");
            }

        }

        writer.save(new File(directory, "classes.csv"));

    }

    public static void exportImages(JasmineProject project, File f) throws IOException {

        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write("# Images exported from " + project.getName() + "\n");
        out.write("# Filename, ClassID\n");
        Vector<JasmineImage> images = project.getImages();
        for (int i = 0; i < images.size(); i++) {
            JasmineImage image = images.elementAt(i);
            out.write(image.filename);
            out.write(", ");
            out.write(String.valueOf(image.getClassID()));
            out.write("\n");
        }
        out.close();

    }

    public static void exportShapes(JasmineProject project, File f) throws IOException {

        throw new RuntimeException("Sorry  - export shapes doesn't work at the moment.");

        // TODO: UPdate the format so that multi-sub-object shapes can be exported.

        /* BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write("# Shapes exported from " + project.getName() + "\n");
        out.write("# Line n: ClassID, Filename of image\n");
        out.write("# Line (n+1): x,y coordinates of shape pixels\n");
        out.write("# Line (n+2): x,y coordinates of edge pixels\n");

        // go through each image
        Vector<JasmineImage> images = project.getImages();
        for (int i = 0; i < images.size(); i++) {

            JasmineImage image = images.elementAt(i);
            Vector<SegmentedShape> shapes = image.getShapes();

            for (int j = 0; j < shapes.size(); j++) {
                SegmentedShape shape = shapes.elementAt(j);

                out.write(String.valueOf(shape.classID));
                out.write(", ");
                out.write(image.filename);
                out.write("\n");

                Vector<ShapePixel> pixels = shape.pixels;
                for (int k = 0; k < pixels.size(); k++) {
                    ShapePixel shapePixel = pixels.elementAt(k);
                    out.write(String.valueOf(shapePixel.x));
                    out.write(",");
                    out.write(String.valueOf(shapePixel.y));
                    out.write(",");
                }

                out.write("\n");

                pixels = shape.edgePixels;
                for (int k = 0; k < pixels.size(); k++) {
                    ShapePixel shapePixel = pixels.elementAt(k);
                    out.write(String.valueOf(shapePixel.x));
                    out.write(",");
                    out.write(String.valueOf(shapePixel.y));
                    out.write(",");
                }

                out.write("\n");

            }

        }

        out.close();*/

    }

    public static void exportNormalisationCoefficients(JasmineProject project, File file) throws IOException {

        // Get the shapes from the image
        Vector<ExtraShapeData> shapes = JasmineUtils.getTrainingData(project);

        // there are 20 different shape features in use
        int numFeatures = 20;

        // put the values into an array first (they need some processing)
        double[][] values = new double[shapes.size()][numFeatures];

        // Get the raw values and put them into the array
        for (int i = 0; i < shapes.size(); i++) {
            ExtraShapeData s = shapes.elementAt(i);
            values[i] = JasmineUtils.getShapeFeatures(s);
        }

        // Now the values can be written out in a semi-colon separated file
        // which can easily be imported by other programs, or the ML
        // implementation of your choice.
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        out.write("Corners,Hollows,BalanceX,BalanceY,Density,AspectRatio,Joints,Ends,Roundness,EndBalanceX,EndBalanceY,ClosestEndToCog,ClosestPixelToCog,HorizontalSymmetry,VerticalSymmetry,InvHorizontalSymmetry,InvVerticalSymmetry,Roughness4,Roughness,Roughness12,ClassID\n");

        // For each feature, calculate a normalisation coefficient
        for (int feature = 0; feature < numFeatures; feature++) {

            double normalisationValue = 0;

            for (int row = 0; row < values.length; row++) {
                double value = values[row][feature];
                normalisationValue += (value * value);
            }

            normalisationValue = Math.sqrt(normalisationValue);

            out.write(String.valueOf(normalisationValue));

            out.write(",");

        }

        out.close();


    }

    /**
     * Exports pixel features. Attempts to keep the class counts even.
     */
    public static void exportPixelFeatures(final Jasmine jasmine, final JasmineProject project, final Vector<Terminal> terminals, final int pixelMode) throws IOException {

        final Vector<ImagePixel> allPixels = JasmineUtils.getAllPixels(project, pixelMode);

        new DialogTrainingSize(jasmine, allPixels.size()) {
            public void onOK(float totalProportion, float trainingPercentage, File trainingFile, File testingFile) throws IOException {

                jasmine.showProgressBar(110);
                jasmine.setStatusText("Exporting...");

                int trainPixels = 0;
                int testPixels = 0;

                BufferedWriter trainingOut = new BufferedWriter(new FileWriter(trainingFile));

                BufferedWriter testingOut = null;
                if (testingFile != null) {
                    testingOut = new BufferedWriter(new FileWriter(testingFile));
                }

                Vector<ImagePixel> chosenPixels = new PixelSelector(project).choosePixels(allPixels, totalProportion, project, pixelMode);//(allPixels, totalProportion, project);

                jasmine.setProgressBarValue(10);

                // go through the chosen pixels
                for (int i = 0; i < chosenPixels.size(); i++) {
                    ImagePixel pixel = chosenPixels.elementAt(i);
                    float[] values;

                    if (terminals == null) {
                        values = JasmineUtils.getPixelFeatures(pixel.image, pixel.x, pixel.y);
                    } else {
                        values = JasmineUtils.getPixelFeatures(pixel.image, pixel.x, pixel.y, terminals);
                    }

                    BufferedWriter out;
                    if (testingOut == null || Math.random() <= trainingPercentage) {
                        out = trainingOut;
                        trainPixels++;
                    } else {
                        out = testingOut;
                        testPixels++;
                    }

                    // write the feature vector to CSV
                    for (int k = 0; k < values.length; k++) {
                        out.write(String.valueOf(values[k]));
                        out.write(",");
                    }

                    // class ID
                    out.write(String.valueOf(pixel.classID));
                    out.write("\n");

                    if (i % 250 == 0) {
                        jasmine.setProgressBarValue(10 + (100 * (int) (i / (float) chosenPixels.size())));
                    }

                }

                trainingOut.close();
                if (testingOut != null) {
                    testingOut.close();
                }

                StringBuffer message = new StringBuffer("Exported ");
                if (trainPixels > 0) {
                    message.append(trainPixels + " pixels to " + trainingFile.getName());
                }
                if (testPixels > 0) {
                    message.append(", " + testPixels + " pixels to " + testingFile.getName());
                }

                jasmine.setStatusText(message.toString());

                jasmine.hideProgressBar();


            }
        };

    }

    /**
     * Exports shape features
     */
    public static void exportShapeFeatures(final Jasmine jasmine, final JasmineProject project, File f, final Vector<Terminal> terminals) throws IOException {

        jasmine.setStatusText("Exporting...");

        BufferedWriter out = new BufferedWriter(new FileWriter(f));

        Vector<ExtraShapeData> objects = JasmineUtils.getLabelledSubObjects(project);

        jasmine.setProgressBarValue(10);

        // go through the chosen pixels
        for (int i = 0; i < objects.size(); i++) {
            ExtraShapeData extraShapeData = objects.elementAt(i);

            double[] values = JasmineUtils.getShapeFeatures(extraShapeData, terminals);

            // write the feature vector to CSV
            for (int k = 0; k < values.length; k++) {
                out.write(String.valueOf(values[k]));
                out.write(",");
            }

            // class ID
            out.write(String.valueOf(extraShapeData.getClassID()));
            out.write("\n");

        }

        out.close();

        StringBuffer message = new StringBuffer("Exported " + objects.size() + " shapes to " + f.getName());

        jasmine.setStatusText(message.toString());

        jasmine.hideProgressBar();

    }

    /**
     * Exports shape features
     */
    public static void exportObjectFeatures(final Jasmine jasmine, final JasmineProject project, File f, final Vector<Terminal> terminals) throws IOException {

        jasmine.setStatusText("Exporting...");

        BufferedWriter out = new BufferedWriter(new FileWriter(f));

        Vector<SegmentedObject> objects = JasmineUtils.getLabelledObjects(project);

        jasmine.setProgressBarValue(10);
        
        /*
        //POEY 
        //for normalization        
        float[][] dataNorm = new float[objects.size()][terminals.size()];
        int[] classID = new int[objects.size()];
        */

        // go through the chosen pixels
        for (int i = 0; i < objects.size(); i++) {
            SegmentedObject object = objects.elementAt(i);

            double[] values = JasmineUtils.getObjectFeatures(object, terminals);          
            
            // write the feature vector to CSV
            for (int k = 0; k < values.length; k++) {
                out.write(String.valueOf(values[k]));
                out.write(",");
                
                //POEY
                //dataNorm[i][k] = (float)values[k];
                
            }

            // class ID
            out.write(String.valueOf(object.getClassID()));
            out.write("\n");
            
            //POEY
            //classID[i] = object.getClassID();

        }

        /*
        //POEY
        //get normalized data
        for(int j = 0; j < dataNorm[0].length; j++) {
        	FastStatistics solver = new FastStatistics();
        	for(int i = 0; i < dataNorm.length; i++){
        		solver.addData(dataNorm[i][j]);
        	}
        	for(int i = 0; i < dataNorm.length; i++){
        		dataNorm[i][j] = solver.getNorm(dataNorm[i][j]);
        	}        	
        }        
        //attend normalized data to the file
        out.write("\n\n\nNormalized data\n");
        for (int i = 0; i < dataNorm.length; i++) {
        	for (int j = 0; j < dataNorm[i].length; j++) {
        		out.write(String.valueOf(dataNorm[i][j]));
        		out.write(",");
        	}
        	out.write(String.valueOf(classID[i]));
            out.write("\n");
        }
        */
        
        
        out.close();

        StringBuffer message = new StringBuffer("Exported " + objects.size() + " shapes to " + f.getName());

        jasmine.setStatusText(message.toString());

        jasmine.hideProgressBar();

    }

    //POEY
    /**
     * Exports shape features of testing images
     */
    public static void exportTestingObjectFeatures(final Jasmine jasmine, final JasmineProject project, File f, final Vector<Terminal> terminals) throws IOException {

        jasmine.setStatusText("Exporting...");

        BufferedWriter out = new BufferedWriter(new FileWriter(f));

        Vector<SegmentedObject> objects = JasmineUtils.getLabelledTestingObjects(jasmine,project);

        jasmine.setProgressBarValue(10);
       /* 
      //POEY        
        float[][] dataNorm = new float[objects.size()][terminals.size()];
        int[] classID = new int[objects.size()];
*/
        // go through the chosen pixels
        for (int i = 0; i < objects.size(); i++) {
            SegmentedObject object = objects.elementAt(i);

            double[] values = JasmineUtils.getObjectFeatures(object, terminals);

            // write the feature vector to CSV
            for (int k = 0; k < values.length; k++) {
                out.write(String.valueOf(values[k]));
                out.write(",");
                
                //POEY
                //dataNorm[i][k] = (float)values[k];
                
            }

            // class ID
            out.write(String.valueOf(object.getClassID()));
            out.write("\n");
            
            //POEY
            //classID[i] = object.getClassID();

        }
/*        
        //POEY
        //get normalized data
        for(int j = 0; j < dataNorm[0].length; j++) {
        	FastStatistics solver = new FastStatistics();
        	for(int i = 0; i < dataNorm.length; i++){
        		solver.addData(dataNorm[i][j]);
        	}
        	for(int i = 0; i < dataNorm.length; i++){
        		dataNorm[i][j] = solver.getNorm(dataNorm[i][j]);
        	}        	
        }        
        //attend normalized data to the file
        out.write("\n\n\nNormalized data\n");
        for (int i = 0; i < dataNorm.length; i++) {
        	for (int j = 0; j < dataNorm[i].length; j++) {
        		out.write(String.valueOf(dataNorm[i][j]));
        		out.write(",");
        	}
        	out.write(String.valueOf(classID[i]));
            out.write("\n");
        }
*/
        out.close();

        StringBuffer message = new StringBuffer("Exported " + objects.size() + " shapes to " + f.getName());

        jasmine.setStatusText(message.toString());

        jasmine.hideProgressBar();

    }




}
