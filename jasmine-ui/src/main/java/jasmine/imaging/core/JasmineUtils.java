package jasmine.imaging.core;


import jasmine.classify.data.Data;
import jasmine.classify.data.DataSet;
import jasmine.classify.data.MemoryDataSet;
import jasmine.gp.problems.DataStack;
import jasmine.gp.training.PixelSelection;
import jasmine.gp.training.TrainingImage;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.ColourChannels;
import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.classification.JasmineGP;
import jasmine.imaging.core.segmentation.JasminePixelSelection;
import jasmine.imaging.core.segmentation.JasmineSegmentationProblem;
import jasmine.imaging.core.util.ImagePixel;
import jasmine.imaging.core.util.TerminalMetaData;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;
import jasmine.imaging.shapes.ShapePixel;
import jasmine.kmeans.Cluster;
import jasmine.kmeans.DataPoint;
import jasmine.kmeans.KMeansClusterer;

import java.util.Vector;
import java.util.Collections;
import java.util.Random;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;

/**
 * Useful utility functions associated with Jasmine.
 *
 * @author Olly Oechsle, University of Essex, Date: 22-May-2007
 * @version 1.0
 */
public class JasmineUtils {

    public static final int MIN_SHAPE_SIZE = 50;

    public static String getPixelFeaturesCSVHeader() {
        return "Red, Green, Blue, Hue, Sat, Lightness, Grey, HueMean, SatMean, LightnessMean, RedMean, GreenMean, BlueMean, RedStdDev, GreenStdDev, BlueStdDev, RedRange, GreenRange, BlueRange, RedHigh, GreenHigh, BlueHigh,RedMean, GreenMean, BlueMean, RedStdDev, GreenStdDev, BlueStdDev, RedRange, GreenRange, BlueRange, RedHigh, GreenHigh, BlueHigh,ImageSat,ImageHue,ImageLight";
    }

    /**
     * Gets the labelled segmented objects in the project - the ones that can be used for training
     */
    public static Vector<SegmentedObject> getLabelledObjects(JasmineProject p) {
        Vector<SegmentedObject> trainingObjects = new Vector<SegmentedObject>(100);
        Vector<JasmineImage> images = p.getImages();
        //POEY comment: images.size() =  the number of images       
        for (int i = 0; i < images.size(); i++) {
            JasmineImage jasmineImage = images.elementAt(i);
            Vector<SegmentedObject> objects = jasmineImage.objects;
            //POEY comment: objects.size() =  the number of objects in an image 
            for (int k = 0; k < objects.size(); k++) {
                SegmentedObject segmentedObject = objects.elementAt(k);
                if (segmentedObject.getClassID() > -1) {
                    trainingObjects.add(segmentedObject);
                }
            }
        }
        return trainingObjects;
    }
    
    //POEY
    public static Vector<SegmentedObject> getLabelledTestingObjects(Jasmine j, JasmineProject p) {
        Vector<SegmentedObject> testingObjects = new Vector<SegmentedObject>(100);
        Vector<JasmineTestingImage> images = p.getTestingImages();
        
        JasmineUtils ju = new JasmineUtils();
        ju.defineClassTestingSet(j,p);
                
        for (int i = 0; i < images.size(); i++) {
        	ju.jasmineTestingImage = images.elementAt(i);
	        for (int k = 0; k < ju.jasmineTestingImage.objects.size(); k++) {
	            SegmentedObject segmentedObject = ju.jasmineTestingImage.objects.elementAt(k);
	            if (segmentedObject.getClassID() > -1) {
	                testingObjects.add(segmentedObject);
	            }
	        }
        }
        
        return testingObjects;
    }
    
    public JasmineTestingImage jasmineTestingImage;    
    
    // POEY
	private void defineClassTestingSet(Jasmine j, JasmineProject project) {

		try {	
			VisionSystem vs;
			vs = VisionSystem.load(project);
			if(j.project.getTestingImages().size()==0){
	    		j.alert("No testing images. You'll need to add at least one to your project first.");      	
	    	}
	    	else if(j.classbox.model.classes.size() == 0){
	    		j.alert("No class to be defined");
	    	}
	    	else if(vs.backgroundSubtracter == null){
	    		j.alert("Cannot segment: background subtracter is not set up");
	    	}
	    	else { 
	    		if(!j.firstTestingImage())
	    			j.project.setCursorTesting(0);
	    			
				//show a progress bar
            	ProgressDialog d = new ProgressDialog("Class Definition Progress", "Please wait...", j.project.getTestingImages().size());
            	
            	SegmentedObject selectedObject;
            	
            	//loop for all images the in the testing window
				for(int k=0; k<j.project.getTestingImages().size(); k++, j.nextTestingImage()) {
					//Segmentation process
					jasmineTestingImage = j.project.currentTestingImage();
					PixelLoader ploader = new PixelLoader(jasmineTestingImage.getBufferedTestingImage());
					if (jasmineTestingImage != null) {
						jasmineTestingImage.clearObjects();
						jasmineTestingImage.setObjects(vs.getObjects(ploader));
						jasmineTestingImage.objects = jasmineTestingImage.getObjects();			
					}
					
					//class declaration process					
					if(jasmineTestingImage.objects != null && j.classbox.model.classes.size() > 0){
						for(int i=0; i<jasmineTestingImage.objects.size(); i++){							
							selectedObject = selectObject(jasmineTestingImage.objects.elementAt(i));
							if (selectedObject != null) {
								for (int l = 0; l < j.classbox.model.classes.size(); l++) {
									if (jasmineTestingImage.getFilename().toLowerCase().startsWith(j.classbox.model.classes.get(l).toString().toLowerCase())) {
										selectedObject.setClassID(l+1);	//classID starts at 1
										System.out.println(j.project.currentTestingImage().getFilename()+ " : class " + j.classbox.model.classes.get(l).toString());
									}
								}
							}
						}
					}
					
					//show a progress bar : but doesn't work now
		            d.setValue(k + 1);
				}
				//dispose the progress bar
	    		d.dispose();
	    		
				//repaint();
			}   		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	// POEY
	private SegmentedObject selectObject(SegmentedObject segmentedObject) {
		/*// the current objects are here
		if (objects == null)
			return null;
*/
		// POEY
		// objects.size() should be 1
		int x = 0, y = 0;	
		/*for (int i = 0; i < objects.size(); i++) {
			SegmentedObject segmentedOneObject = objects.elementAt(i);*/
			SegmentedShape segmentedShape = segmentedObject.outline;
			// select a pixel of a segmented object
			ShapePixel shapeOnePixel = segmentedShape.pixels.elementAt(1); //I don't know why I choose element at 1
			x = shapeOnePixel.x;
			y = shapeOnePixel.y;
		//}

		//for (int i = 0; i < objects.size(); i++) {
			//SegmentedObject segmentedObject = objects.elementAt(i);
			//SegmentedShape segmentedShape = segmentedObject.outline;
			for (int l = 0; l < segmentedShape.getMass(); l++) {
				ShapePixel shapePixel = segmentedShape.pixels.elementAt(l);
				if (shapePixel.x == x && shapePixel.y == y) {
					/*j.menus.edit_delete_shape.setEnabled(true);
					if (j.shapeStats != null) {
						try {
							j.shapeStats.displayStats(segmentedObject);
						} catch (RuntimeException rte) {
							j.alert("Cannot get shape statistics: " + rte.getMessage());
						}
					}*/
					return segmentedObject;
				}
			}
		//}
/*
		if (j.shapeStats != null)
			j.shapeStats.hideStats();
		j.menus.edit_delete_shape.setEnabled(false);
		*/
		return null;

	}

    public static int countLabelledObjects(JasmineProject p) {
        return getLabelledObjects(p).size();
    }


    public static Vector<ExtraShapeData> getLabelledSubObjects(JasmineProject p) {
        Vector<ExtraShapeData> trainingObjects = new Vector<ExtraShapeData>(100);
        Vector<JasmineImage> images = p.getImages();
        for (int i = 0; i < images.size(); i++) {
            JasmineImage jasmineImage = images.elementAt(i);
            PixelLoader image = new PixelLoader(jasmineImage.getBufferedImage(), new File(p.getImageLocation(), jasmineImage.getFilename()));
            Vector<SegmentedObject> objects = jasmineImage.objects;
            for (int k = 0; k < objects.size(); k++) {
                SegmentedObject segmentedObject = objects.elementAt(k);
                for (int j = 0; j < segmentedObject.subobjects.size(); j++) {
                    SegmentedShape segmentedShape =  segmentedObject.subobjects.elementAt(j);
                    if (segmentedShape.classID > -1) {
                        trainingObjects.add(new ExtraShapeData(segmentedShape, image));
                    }
                }

            }
        }
        return trainingObjects;
    }

    public static int countLabelledSubObjects(JasmineProject p) {
        int count = 0;
        Vector<JasmineImage> images = p.getImages();
        for (int i = 0; i < images.size(); i++) {
            JasmineImage jasmineImage = images.elementAt(i);
            Vector<SegmentedObject> objects = jasmineImage.objects;
            for (int k = 0; k < objects.size(); k++) {
                SegmentedObject segmentedObject = objects.elementAt(k);
                for (int j = 0; j < segmentedObject.subobjects.size(); j++) {
                    SegmentedShape segmentedShape =  segmentedObject.subobjects.elementAt(j);
                    if (segmentedShape.classID > -1) {
                        count++;
                    }
                }

            }
        }
        return count;
    }

    public static boolean isBackground(JasmineClass c) {
        return c.background;
    }




    public static DataSet generateDataSet(Vector<Terminal> terminals, Vector<ImagePixel> images) {
        // go through each image
        DataStack stack = new DataStack();
        MemoryDataSet ds = new MemoryDataSet("Jasmine Dataset");
    
        //POEY comment: image.size() = the number of pixels
        for (int i = 0; i < images.size(); i++) {
            ImagePixel imagePixel = images.elementAt(i);
            
            //POEY comment: put pixel into stack
            stack.setImage(imagePixel.image);
            stack.setX(imagePixel.x);
            stack.setY(imagePixel.y);
            float[] row = new float[terminals.size()];
            
            //POEY comment: terminals.size() = the number of feature extraction function 
            for (int j = 0; j < terminals.size(); j++) {
                Terminal terminal = terminals.elementAt(j);
                row[j] = (float) terminal.execute(stack);
            }
            ds.addTrainingData(row, imagePixel.classID);           
        }
        return ds;
    }

    public static float[] generateObjectFeatureVector(Vector<Terminal> terminals, Object o) {
        DataStack stack = new DataStack();
            int classID = 0;
            if (o instanceof SegmentedObject) {
                SegmentedObject obj = (SegmentedObject) o;
                setupDataStack(stack, obj);
                classID = obj.getClassID();
            } else {
                ExtraShapeData esd = (ExtraShapeData) o;
                setupDataStack(stack, esd);
                classID = esd.getClassID();
            }
            float[] row = new float[terminals.size()];
            for (int j = 0; j < terminals.size(); j++) {
                Terminal terminal = terminals.elementAt(j);
                row[j] = (float) terminal.execute(stack);
            }
        return row;
}

    public static DataSet generateObjectDataSet(Vector<Terminal> terminals, Vector objects) {
        // go through each image
        DataStack stack = new DataStack();
        MemoryDataSet ds = new MemoryDataSet("Jasmine Dataset");
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.elementAt(i);
            int classID = 0;
            if (o instanceof SegmentedObject) {
                SegmentedObject obj = (SegmentedObject) o;
                setupDataStack(stack, obj);
                classID = obj.getClassID();
            } else {
                ExtraShapeData esd = (ExtraShapeData) o;
                setupDataStack(stack, esd);
                classID = esd.getClassID();
            }
            float[] row = new float[terminals.size()];
            for (int j = 0; j < terminals.size(); j++) {
                Terminal terminal = terminals.elementAt(j);
                row[j] = (float) terminal.execute(stack);
            }
            ds.addTrainingData(row, classID);
        }
        return ds;
    }

    public static void setupDataStack(DataStack ds, Object o) {
        if (o instanceof SegmentedObject) {
            setupDataStack(ds, (SegmentedObject) o);
            return;
        }
        if (o instanceof ExtraShapeData) {
            setupDataStack(ds, (ExtraShapeData) o);
            return;
        }
        return;
    }

    public static void setupDataStack(DataStack ds, SegmentedObject object) {
        ds.setData(object.getOutlineData());
        ds.setData2(object);
        ds.setImage(object.getImage());
    }

    public static void setupDataStack(DataStack ds, ExtraShapeData shape) {
        ds.setData(shape);
        ds.setImage(shape.getImage());
    }

    public static Vector<TrainingImage> getTrainingImages(JasmineProject project, int type) throws IOException {
        // produce the training data
        Vector<TrainingImage> trainingData = new Vector<TrainingImage>(10);

        // now get its images
        for (int i = 0; i < project.getImages().size(); i++) {

            JasmineImage image = project.getImages().elementAt(i);

            if (type == JasmineClass.MASK && image.getMaskOverlayPixels() == null) continue;
            if (type == JasmineClass.MATERIAL && image.getMaterialOverlayPixels() == null) continue;

            BufferedImage img = image.getBufferedImage();

            try {

                PixelSelection selection = new JasminePixelSelection(image, type);

                if (selection.getPixels().size() > 0) {
                    TrainingImage ti = new TrainingImage(img, selection);
                    ti.setName(image.filename);
                    trainingData.add(ti);
                }

            } catch (Exception err) {
                System.err.println("Cant get pixel selection for image: " + i);
            }
        }
        return trainingData;
    }

     public static Vector<ImagePixel> getAllPixels(JasmineProject project, int type) throws IOException {
        if (type == JasmineClass.MASK) return getAllMaskPixels(project);
         return getAllMaterialPixels(project);
    }

    public static Vector<ImagePixel> getAllMaskPixels(JasmineProject project) throws IOException {
        final Vector<ImagePixel> allPixels = new Vector<ImagePixel>(10000); 
        // go through all the images in the project and collect all the pixels in one place
        for (int i = 0; i < project.getImages().size(); i++) {           	
            JasmineImage image = project.getImages().elementAt(i);            
            PixelLoader pl = new PixelLoader(image.getBufferedImage(), null);           
            Vector<Pixel> pixels = image.getMaskOverlayPixels();
            if (pixels == null) continue;
            for (int j = 0; j < pixels.size(); j++) {           	
                Pixel pixel = pixels.elementAt(j);
                allPixels.add(new ImagePixel(pixel.x, pixel.y, pixel.value, pl));    
            }
        }
        return allPixels;
    }

    public static Vector<ImagePixel> getAllMaterialPixels(JasmineProject project) throws IOException {
        final Vector<ImagePixel> allPixels = new Vector<ImagePixel>(10000);
        // go through all the images in the project and collect all the pixels in one place        
        for (int i = 0; i < project.getImages().size(); i++) {       	
            JasmineImage image = project.getImages().elementAt(i);
            PixelLoader pl = new PixelLoader(image.getBufferedImage(), null);
            Vector<Pixel> pixels = image.getMaterialOverlayPixels();
            if (pixels == null) continue;
            for (int j = 0; j < pixels.size(); j++) {            	
                Pixel pixel = pixels.elementAt(j);
                allPixels.add(new ImagePixel(pixel.x, pixel.y, pixel.value, pl));
            }
        }
        return allPixels;
    }

    public static Vector<Terminal> getTerminalsForObjects(JasmineProject project) {
        Vector<TerminalMetaData> terminalMetaData = (Vector<TerminalMetaData>) project.getProperty(JasmineFeatureSelectionDialog.OBJECT_FEATURE_SET_HANDLE);
        Vector<Terminal> terminals = getTerminalSet(JasmineGP.getStandardTerminals(getLabelledObjects(project)), terminalMetaData);
        return terminals;
    }

    public static Vector<Terminal> getTerminalsForSubObjects(JasmineProject project) {
        Vector<TerminalMetaData> terminalMetaData = (Vector<TerminalMetaData>) project.getProperty(JasmineFeatureSelectionDialog.SUB_OBJECT_FEATURE_SET_HANDLE);
        Vector<Terminal> terminals = getTerminalSet(JasmineGP.getStandardTerminals(null), terminalMetaData);
        return terminals;
    }

    /**
     * Ignores classes and uses a k means clusterer to cluster image pixels (using the project function set)
     * An equal number of pixels is chosen from each cluster.
     * Based on an idea by Roberts and Claridge
     */
    public static Vector<ImagePixel> choosePixelsByCluster(Vector<ImagePixel> allPixels, float totalProportion, JasmineProject project, int mode) {

        // first job is to cluster all the pixels together
        Vector<TerminalMetaData> terminalMetaData = null;
        if (mode == JasmineClass.MATERIAL) {
        terminalMetaData = (Vector<TerminalMetaData>) project.getProperty(JasmineFeatureSelectionDialog.MATERIAL_FEATURE_SET_HANDLE);
        } else {
        terminalMetaData = (Vector<TerminalMetaData>) project.getProperty(JasmineFeatureSelectionDialog.MASK_PIXEL_FEATURE_SET_HANDLE);
        }

        //POEY comment: terminals contain extraction functions
        Vector<Terminal> terminals = getTerminalSet(JasmineSegmentationProblem.getStandardTerminals(), terminalMetaData);

        Random r = Jasmine.getRandom();

        // calculate the number of clusters
        //POEY comment: numClusters = the number of pixels' classes * 2
        int numClusters = getNumClasses(allPixels) * 2;

        // Start a K means clusterer
        KMeansClusterer clusterer = new KMeansClusterer(numClusters);

        // Go through all the pixels in the image
        DataStack ds = new DataStack() ;        
        for (int i = 0; i < allPixels.size(); i++) {
            ImagePixel imagePixel = allPixels.elementAt(i);
            ds.setX(imagePixel.x);
            ds.setY(imagePixel.y);
            ds.setImage(imagePixel.image);
            double[] values = new double[terminals.size()];
            //POEY comment: terminals.size()= the number of selected extraction functions
            for (int j = 0; j < terminals.size(); j++) {
                Terminal terminal = terminals.elementAt(j);
                values[j] = terminal.execute(ds);
            }
            DataPoint d = new DataPoint(values);
            d.setID(i);
            clusterer.add(d);
        }
        
        //POEY comment: to cluster selected pixels
        clusterer.run();
        
        Vector<Cluster> clusters = clusterer.getCentroids();

        // put the datapoints in each cluster into a random order
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.elementAt(i);
            Collections.shuffle(cluster.getMembers(), r);
        }

        // this will store those pixels that will become training data
        Vector<ImagePixel> chosenPixels = new Vector<ImagePixel>(1000);

        float target = allPixels.size() * totalProportion;
        int pixelsPerCluster = (int) (target / clusters.size());
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.elementAt(i);
            Vector<DataPoint> clusterPixels = cluster.getMembers();

            for (int j = 0; j < clusterPixels.size() && j < pixelsPerCluster; j++) {
                DataPoint p = clusterPixels.elementAt(j);
                chosenPixels.add(allPixels.elementAt(p.getID()));
            }
        }

        return chosenPixels;

    }

    public static int getNumClasses(Vector<ImagePixel> pixels) {
        Vector<Integer> classes = new Vector<Integer>(10);
        for (int i = 0; i < pixels.size(); i++) {
            ImagePixel imagePixel = pixels.elementAt(i);
            if (!classes.contains(imagePixel.classID)) classes.add(imagePixel.classID);
        }
        return classes.size();
    }

   public static Vector<Integer> getClasses(Vector<Data> data) {
        Vector<Integer> classes = new Vector<Integer>(10);
        for (int i = 0; i < data.size(); i++) {
            Data d = data.elementAt(i);
            if (!classes.contains(d.classID)) classes.add(d.classID);
        }
        return classes;
    }

    public static Vector<Terminal> getTerminalSet(Vector<Terminal> standardTerminals, Vector<TerminalMetaData> terminalMetaData) {
        if (terminalMetaData != null) {
            Vector<Terminal> terminalSet = new Vector<Terminal>();
            // register nodes using their terminal meta data - fitness and enabled          
            for (int i = 0; i < terminalMetaData.size(); i++) {
                TerminalMetaData metaData = terminalMetaData.elementAt(i);
                Terminal t = metaData.getTerminal(null);
                //POEY comment: t is a feature extraction function
                if (t != null) {
                    //params.registerNode(t, metaData.isEnabled(), metaData.getFitness());              	
                    if (metaData.isEnabled()) {
                        terminalSet.add(t);
                    }
                }
            }
            return terminalSet;
        } else {
            return standardTerminals;
        }
    }

    public static Vector<ImagePixel> choosePixelsByClass(Vector<ImagePixel> allPixels, int pixelMode, float totalProportion, JasmineProject project) {
        // figure out how many pixels per class we need
    	//POEY comment: allPixels.size() = all selected pixels
    	//totalProportion(%) = the percentage of those wanted selected pixels
    	//target (pixels) = the number of those wanted selected pixels
        float target = allPixels.size() * totalProportion;

        Vector<JasmineClass> classes;
        //POEY comment: for segmentation, pixelMode = 0
        classes = project.getPixelClasses(pixelMode);
        int pixelsPerClass = (int) (target / classes.size());

        Random r = Jasmine.getRandom();

        // this will store those pixels that will become training data
        Vector<ImagePixel> chosenPixels = new Vector<ImagePixel>(1000);

        // go through each class
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass c = classes.elementAt(i);

            // get only the pixels in this class
            Vector<ImagePixel> inClass = new Vector<ImagePixel>(1000);
            for (int j = 0; j < allPixels.size(); j++) {
                ImagePixel pixel = allPixels.elementAt(j);
                if (pixel.classID == c.classID) inClass.add(pixel);
            }

            // add the pixels to the list
            if (inClass.size() <= pixelsPerClass) {            	
                chosenPixels.addAll(inClass);
            } else {
                // add them in a random order
                Collections.shuffle(inClass, r);
                for (int j = 0; j < pixelsPerClass; j++) {
                    chosenPixels.add(inClass.elementAt(j));
                }
            }

        }

        return chosenPixels;
    }

    public static float[] getPixelFeatures(PixelLoader image, Pixel pixel) {
        return getPixelFeatures(image, pixel.x, pixel.y);
    }

    public static float[] getPixelFeatures(PixelLoader image, int x, int y, Vector<Terminal> terminalMetaData) {
        float[] values = new float[terminalMetaData.size()];
        DataStack data = new DataStack();
        data.setX(x);
        data.setY(y);
        data.setImage(image);
        for (int i = 0; i < terminalMetaData.size(); i++) {
            Terminal t = terminalMetaData.elementAt(i);
            float output = (float) t.execute(data);
            values[i] = output;
        }
        return values;
    }

    /**
     * Updated to reflect new colour Channels, February 2009.
     */
    public static float[] getPixelFeatures(PixelLoader image, int x, int y) {
        float[] values = new float[37];

        values[0] = (float) image.getNormalisedRed(x, y);
        values[1] = (float) image.getNormalisedGreen(x, y);
        values[2] = (float) image.getNormalisedBlue(x, y);

        values[3] = image.getHue(x, y);
        values[4] = image.getSaturation(x, y);
        values[5] = image.getLightness(x, y);

        values[6] = image.getGreyValue(x, y);

        values[7] = image.getHueMean();
        values[8] = image.getSatMean();
        values[9] = image.getLightnessMean();

        values[10] = image.get3x3Stats(x, y, ColourChannels.NORMALISED_RED).getMean();
        values[11] = image.get3x3Stats(x, y, ColourChannels.NORMALISED_GREEN).getMean();
        values[12] = image.get3x3Stats(x, y, ColourChannels.NORMALISED_BLUE).getMean();

        values[13] = image.get3x3Stats(x, y, ColourChannels.NORMALISED_RED).getStandardDeviation();
        values[14] = image.get3x3Stats(x, y, ColourChannels.NORMALISED_GREEN).getStandardDeviation();
        values[15] = image.get3x3Stats(x, y, ColourChannels.NORMALISED_BLUE).getStandardDeviation();

        values[16] = image.get3x3Stats(x, y, ColourChannels.HUE).getMean();
        values[17] = image.get3x3Stats(x, y, ColourChannels.SATURATION).getMean();
        values[18] = image.get3x3Stats(x, y, ColourChannels.LIGHTNESS).getMean();

        values[19] = image.get3x3Stats(x, y, ColourChannels.HUE).getStandardDeviation();
        values[20] = image.get3x3Stats(x, y, ColourChannels.SATURATION).getStandardDeviation();
        values[21] = image.get3x3Stats(x, y, ColourChannels.LIGHTNESS).getStandardDeviation();

        values[22] = image.get3x3Stats(x, y, ColourChannels.C1).getMean();
        values[23] = image.get3x3Stats(x, y, ColourChannels.C2).getMean();
        values[24] = image.get3x3Stats(x, y, ColourChannels.C3).getMean();

        values[25] = image.get3x3Stats(x, y, ColourChannels.C1).getStandardDeviation();
        values[26] = image.get3x3Stats(x, y, ColourChannels.C2).getStandardDeviation();
        values[27] = image.get3x3Stats(x, y, ColourChannels.C3).getStandardDeviation();

        values[28] = image.get3x3Stats(x, y, ColourChannels.L1).getMean();
        values[29] = image.get3x3Stats(x, y, ColourChannels.L2).getMean();
        values[30] = image.get3x3Stats(x, y, ColourChannels.L3).getMean();

        values[31] = image.get3x3Stats(x, y, ColourChannels.L1).getStandardDeviation();
        values[32] = image.get3x3Stats(x, y, ColourChannels.L2).getStandardDeviation();
        values[33] = image.get3x3Stats(x, y, ColourChannels.L3).getStandardDeviation();

        values[34] = (float) image.getHueStdDeviation();
        values[35] = (float) image.getSatStdDeviation();
        values[36] = (float) image.getLightnessStdDeviation();

        return values;
    }

    public static double[] getShapeFeatures(ExtraShapeData s, Vector<Terminal> terminals) {
        double[] feature = new double[terminals.size()];
        DataStack ds = new DataStack();
        setupDataStack(ds, s);
        for (int i = 0; i < terminals.size(); i++) {
            Terminal terminal = terminals.elementAt(i);
            feature[i] = terminal.execute(ds);
        }
        return feature;
    }

    public static double[] getObjectFeatures(SegmentedObject o, Vector<Terminal> terminals) {
        double[] feature = new double[terminals.size()];
        DataStack ds = new DataStack();
        setupDataStack(ds, o);
        for (int i = 0; i < terminals.size(); i++) {
            Terminal terminal = terminals.elementAt(i);
            feature[i] = terminal.execute(ds);
        }
        return feature;
    }

    public static double[] getShapeFeatures(ExtraShapeData s) {
        double[] feature = new double[20];
        feature[0] = s.countCorners();
        feature[1] = s.countHollows();
        feature[2] = s.getBalanceX();
        feature[3] = s.getBalanceY();
        feature[4] = s.getDensity();
        feature[5] = s.getAspectRatio();
        feature[6] = s.getJoints();
        feature[7] = s.getEnds();
        feature[8] = s.getRoundness();
        feature[9] = s.getEndBalanceX();
        feature[10] = s.getEndBalanceY();
        feature[11] = s.getClosestEndToCog();
        feature[12] = s.getClosestPixelToCog();
        feature[13] = s.getHorizontalSymmetry();
        feature[14] = s.getVerticalSymmetry();
        feature[15] = s.getInverseHorizontalSymmetry();
        feature[16] = s.getInverseVerticalSymmetry();
        feature[17] = s.getRoughness(4);
        feature[18] = s.getRoughness(8);
        feature[19] = s.getRoughness(12);
        return feature;
    }

    public static int countObjects(JasmineProject project) {

        if (project == null) {
            throw new RuntimeException("Can't count shapes - project is null.");
        }

        int shapeCount = 0;

        // now get its images
        for (int i = 0; i < project.getImages().size(); i++) {

            JasmineImage image = project.getImages().elementAt(i);

            if (image.getObjects().size() > 0) {
                for (int j = 0; j < image.getObjects().size(); j++) {
                    SegmentedObject shape = image.getObjects().elementAt(j);

                    if (shape.outline.getMass() >= MIN_SHAPE_SIZE) shapeCount++;
                }
            }

        }

        return shapeCount;

    }

    /**
     * Gets all the segmented shapes out of a given project instance.
     */
    public static Vector<ExtraShapeData> getTrainingData(JasmineProject project) {

        if (project == null) {
            throw new RuntimeException("Can't get training data - project is null.");
        }

        Vector<ExtraShapeData> trainingData = new Vector<ExtraShapeData>(100);

        // now get its images
        for (int i = 0; i < project.getImages().size(); i++) {

            JasmineImage image = project.getImages().elementAt(i);

            if (image.getObjects().size() > 0) {
                for (int j = 0; j < image.getObjects().size(); j++) {
                    SegmentedObject shape = image.getObjects().elementAt(j);

                    if (shape.outline.getMass() >= MIN_SHAPE_SIZE) {
                        try {
                            // TODO: This is wrong
                            trainingData.add(shape.getOutlineData());
                        } catch (Exception e) {
                            System.err.println("Cannot create shape object - perimeters are null");
                        }
                    }
                }
            }

        }

        return trainingData;
    }

    public static Vector<Pixel> getTrainingPixels(JasmineProject project, int type) {
        Vector<Pixel> pixels = new Vector<Pixel>(1000);
        try {
            Vector<JasmineImage> images = project.getImages();
            for (int i = 0; i < images.size(); i++) {
                JasmineImage jasmineImage = images.elementAt(i);
                if (jasmineImage.materialOverlayFilename != null) {
                    Vector<Pixel> overlayPixels = jasmineImage.getOverlayPixels(type);
                    for (int k = 0; k < overlayPixels.size(); k++) {
                        Pixel overlayPixel = overlayPixels.elementAt(k);
                        pixels.add(overlayPixel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pixels;
    }

    public static Vector<Integer> getDistinctClasses(Vector<ExtraShapeData> trainingData) {
        Vector<Integer> distinctClasses = new Vector<Integer>(100);

        for (int i = 0; i < trainingData.size(); i++) {
            SegmentedShape shape = trainingData.elementAt(i).getShape();

            if (!distinctClasses.contains(shape.classID)) {
                distinctClasses.add(shape.classID);
            }

        }

        return distinctClasses;

    }

    public static JasmineImage getImageByFilename(JasmineProject project, String filename) {
        for (int i = 0; i < project.getImages().size(); i++) {
            JasmineImage image = project.getImages().elementAt(i);
            if (image.filename.equals(filename)) return image;
        }
        return null;
    }


}
