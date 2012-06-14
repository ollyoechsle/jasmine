package jasmine.imaging.core;

import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.core.visionsystem.VisionSystemGraphics;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;
import jasmine.imaging.shapes.ShapePixel;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

//POEY


/**
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version, provided that any use properly credits the author. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at
 * http://www.gnu.org
 * </p>
 * 
 * @author Olly Oechsle, University of Essex, Date: 19-Jun-2007
 * @version 1.0
 */
public class JasmineClassificationPanel extends JasmineAbstractEditorPanel {

	public static final int CREATE_SHAPES = 1;
	public static final int SELECT_SHAPES = 2;

	public static final int SELECT_OBJECTS = 4;
	public static final int SELECT_SUBOBJECTS = 5;

	public int minObjectSize = 1;
	public int minSubObjectSize = 1;

	protected BufferedImage shapeOverlay;
	public Vector<SegmentedObject> objects;

	public SegmentedObject selectedObject;
	public SegmentedShape selectedSubObject;

	public int mode = SELECT_OBJECTS;

	public JasmineClassificationPanel(Jasmine j) {
		super(j);
	}

	public int getMode() {
		return Jasmine.OBJECT_CLASSIFICATION;
	}

	public void onMousePressed(MouseEvent e) {
		switch (mode) {
		case SELECT_OBJECTS:
			selectedObject = selectObject(e);
			if (selectedObject != null) {
				if (SwingUtilities.isRightMouseButton(e)) {
					selectedObject.setClassID(-1);
				} else {
					if (j.classbox.getCurrentClass() != null) {
						if (selectedObject.getClassID() == -1) {
							selectedObject.setClassID(j.classbox.getCurrentClass().classID);
							j.imageBrowser.refresh();  
						}
					}
				}
				j.imageBrowser.refresh();
			}
			break;
		case SELECT_SUBOBJECTS:
			selectedSubObject = selectSubObject(e);
			if (selectedSubObject != null) {
				if (SwingUtilities.isRightMouseButton(e)) {
					selectedSubObject.classID = -1;
				} else {
					if (j.classbox.getCurrentClass() != null) {
						if (selectedSubObject.classID == -1) {
							// ONLY SET CLASSid IF NOT ALREADY ASSIGNED
							selectedSubObject.classID = (j.classbox.getCurrentClass().classID);
							j.imageBrowser.refresh();
						}
					}
				}
				j.imageBrowser.refresh();
			}
			break;
		}
		repaint();
	}

	// POEY
	public void defineClass(final JButton b) {

		try {	
			VisionSystem vs;
			vs = VisionSystem.load(j.project);
			if(j.project.getImages().size()==0){
	    		j.alert("No training images. You'll need to add at least one to your project first.");      	
	    	}
	    	else if(j.classbox.model.classes.size() == 0){
	    		j.alert("No class to be defined");
	    	}
	    	else if(vs.backgroundSubtracter == null){
	    		j.alert("Cannot segment: background subtracter is not set up");
	    	}
	    	else { 
	    		if(!j.firstImage()) 
		    		j.project.setCursor(0);
	    		
				//show a progress bar
            	ProgressDialog d = new ProgressDialog("Class Definition Progress", "Please wait...", j.project.getImages().size());
            	
            	//loop for all images in the training window
				for(int k=0; k<j.project.getImages().size(); k++, j.nextImage()) {
					//Segmentation process
					j.classificationPanel.clear();
					b.setEnabled(false);
					j.currentImage = j.project.currentImage();
					PixelLoader ploader = new PixelLoader(j.currentImage.getBufferedImage());
					if (j.currentImage != null) {
						j.currentImage.clearObjects();
						j.currentImage.setObjects(vs.getObjects(ploader));
						j.classificationPanel.objects = j.currentImage.getObjects();
						j.classificationPanel.repaint();
						j.imageBrowser.refresh();			
					}
					
					//class declaration process
					if(objects != null && j.classbox.model.classes.size() > 0){
						for(int i=0; i<objects.size(); i++){
							selectedObject = selectObject(objects.elementAt(i));
							if (selectedObject != null) {
								for (int l = 0; l < j.classbox.model.classes.size(); l++) {
									if (j.currentImage.getFilename().toLowerCase().startsWith(j.classbox.model.classes.get(l).toString().toLowerCase())) {
										selectedObject.setClassID(l+1);	//classID starts at 1
										System.out.println(j.project.currentImage().getFilename()+ " : class " + j.classbox.model.classes.get(l).toString());
									}
								}
								j.imageBrowser.refresh();
							}
						}
					}
					
					//show a progress bar : but doesn't work now
		            d.setValue(k + 1);
				}
				//dispose the progress bar
	    		d.dispose();
	    		
				repaint();
			}   		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	

	// POEY
	public SegmentedObject selectObject(SegmentedObject segmentedObject) {
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
					j.menus.edit_delete_shape.setEnabled(true);
					if (j.shapeStats != null) {
						try {
							j.shapeStats.displayStats(segmentedObject);
						} catch (RuntimeException rte) {
							j.alert("Cannot get shape statistics: " + rte.getMessage());
						}
					}
					return segmentedObject;
				}
			}
		//}

		if (j.shapeStats != null)
			j.shapeStats.hideStats();
		j.menus.edit_delete_shape.setEnabled(false);
		return null;

	}
	
	public SegmentedObject selectObject(MouseEvent e) {

		// the current objects are here
		if (objects == null)
			return null;

		int x = (e.getX() - imagePanel.getOffsetX()) / imagePanel.zoom;
		int y = (e.getY() - imagePanel.getOffsetY()) / imagePanel.zoom;
		
		for (int i = 0; i < objects.size(); i++) {

			SegmentedObject segmentedObject = objects.elementAt(i);
			SegmentedShape segmentedShape = segmentedObject.outline;

			// if (x >= segmentedShape.minX && x <= segmentedShape.maxX && y >=
			// segmentedShape.minY && y <= segmentedShape.maxY) {
			for (int l = 0; l < segmentedShape.getMass(); l++) {
				ShapePixel shapePixel = segmentedShape.pixels.elementAt(l);
				if (shapePixel.x == x && shapePixel.y == y) {
					j.menus.edit_delete_shape.setEnabled(true);
					if (j.shapeStats != null) {
						try {
							j.shapeStats.displayStats(segmentedObject);
						} catch (RuntimeException rte) {
							j.alert("Cannot get shape statistics: " + rte.getMessage());
						}
					}
					return segmentedObject;
				}
			}
			// }
		}

		if (j.shapeStats != null)
			j.shapeStats.hideStats();
		j.menus.edit_delete_shape.setEnabled(false);
		return null;

	}

	public SegmentedShape selectSubObject(MouseEvent e) {

		// the current objects are here
		if (objects == null)
			return null;

		int x = (e.getX() - imagePanel.getOffsetX()) / imagePanel.zoom;
		int y = (e.getY() - imagePanel.getOffsetY()) / imagePanel.zoom;

		for (int i = 0; i < objects.size(); i++) {

			SegmentedObject segmentedObject = objects.elementAt(i);

			for (int k = 0; k < segmentedObject.subobjects.size(); k++) {
				SegmentedShape segmentedShape = segmentedObject.subobjects
						.elementAt(k);

				// if (x >= segmentedShape.minX && x <= segmentedShape.maxX && y
				// >= segmentedShape.minY && y <= segmentedShape.maxY) {
				for (int l = 0; l < segmentedShape.getMass(); l++) {
					ShapePixel shapePixel = segmentedShape.pixels.elementAt(l);
					if (shapePixel.x == x && shapePixel.y == y) {
						j.menus.edit_delete_shape.setEnabled(true);
						if (j.shapeStats != null) {
							try {
								j.shapeStats.displayStats(segmentedShape);
							} catch (RuntimeException rte) {
								j.alert("Cannot get shape statistics: "
										+ rte.getMessage());
							}
						}
						System.out.println("Found sub obect");
						selectedObject = segmentedObject;
						return segmentedShape;
					}
				}

			}
			// }
		}

		if (j.shapeStats != null)
			j.shapeStats.hideStats();
		j.menus.edit_delete_shape.setEnabled(false);
		selectedObject = null;
		return null;

	}

	public void deleteSelectedShape() {
		switch (mode) {
		case SELECT_OBJECTS:
			if (selectedObject != null) {
				j.currentImage.removeObject(selectedObject);
				objects.remove(selectedObject);
				selectedObject = null;
				repaint();
			}
			break;
		case SELECT_SUBOBJECTS:
			if (selectedObject != null && selectedSubObject != null) {
				selectedObject.remove(selectedSubObject);
				repaint();
			}
			break;
		}

	}

	public void setMode(int mode) {
		this.mode = mode;
		repaint();
		j.classbox.refresh();

	}

	private int white = Color.WHITE.getRGB();
	private int black = Color.BLACK.getRGB();

	public void render(Graphics g) {

		if (objects != null && imagePanel.image != null) {

			System.out.println("Repainting JCP: objectsize: " + minObjectSize);

			if (shapeOverlay == null) {
				shapeOverlay = new BufferedImage(imagePanel.image.getWidth(),
						imagePanel.image.getHeight(),
						BufferedImage.TYPE_INT_ARGB);
			}

			// draw every object onto the shape overlay
			for (int k = 0; k < objects.size(); k++) {
				SegmentedObject object = objects.elementAt(k);

				if (object.outline.getMass() <= minObjectSize)
					continue;

				// go through each subobject and paint it with the right class
				for (int i = 0; i < object.subobjects.size(); i++) {
					SegmentedShape shape = object.subobjects.elementAt(i);

					JasmineClass c = j.project
							.getMaterialClass(shape.originalValue);

					int color;

					if (c == null || c.background) {
						// color = Color.GRAY.getRGB();
						continue;
					} else {
						color = c.color.getRGB();
					}

					for (int j = 0; j < shape.pixels.size(); j++) {
						ShapePixel shapePixel = shape.pixels.elementAt(j);
						int x = shapePixel.x;
						int y = shapePixel.y;
						shapeOverlay.setRGB(x, y, color);
					}

					// highlight the selected sub object
					if (mode == SELECT_SUBOBJECTS) {
						if (shape == selectedSubObject) {

							Vector<ShapePixel> edge = shape.edgePixels;
							for (int j = 0; j < edge.size(); j++) {
								ShapePixel shapePixel = edge.elementAt(j);
								int x = shapePixel.x;
								int y = shapePixel.y;
								shapeOverlay.setRGB(x, y, black);
							}
						}
					}

				}

				// draw a black border around the whole object
				int color = black;
				// highlighted objects in white
				if (mode == SELECT_OBJECTS && object.equals(selectedObject))
					color = white;

				Vector<ShapePixel> edge = object.outline.edgePixels;
				for (int i = 0; i < edge.size(); i++) {
					ShapePixel shapePixel = edge.elementAt(i);
					int x = shapePixel.x;
					int y = shapePixel.y;
					shapeOverlay.setRGB(x, y, color);

				}

			}

			// Graphics2D g2 = (Graphics2D) g;
			// Composite oldcomp = g2.getComposite();
			// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			// 0.5f));
			imagePanel.drawImage(g, shapeOverlay);
			// g2.setComposite(oldcomp);

			// draw the label overlay on top of that
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			FontMetrics f = g2.getFontMetrics();

			if (mode == SELECT_OBJECTS) {
				for (int i = 0; i < objects.size(); i++) {
					SegmentedObject object = objects.elementAt(i);

					if (object.outline.getMass() <= minObjectSize)
						continue;

					Color edgeColor = Color.BLACK;
					Color bgColor = Color.BLACK;
					Color textColour = Color.WHITE;
					if (mode == SELECT_OBJECTS && object.equals(selectedObject)) {
						edgeColor = Color.WHITE;
					}

					int objectWidth = object.outline.maxX - object.outline.minX;
					int objectHeight = object.outline.maxY
							- object.outline.minY;
					int x = ((object.outline.minX + (objectWidth / 2)) * imagePanel.zoom)
							+ imagePanel.getOffsetX();
					int y = ((object.outline.minY + (objectHeight / 2)) * imagePanel.zoom)
							+ imagePanel.getOffsetY();
					ShapePixel highest = new ShapePixel(x, y);
					if (object.getClassID() > -1) {

						JasmineClass c = j.project.getShapeClass(object
								.getClassID());

						// POEY comment: show object's area and its class
						if (c != null) {
							VisionSystemGraphics.drawLabel(g2, f, highest,
									c.name, edgeColor, bgColor, textColour);
						}

					}
				}
			}

			if (mode == SELECT_SUBOBJECTS) {
				for (int i = 0; i < objects.size(); i++) {
					SegmentedObject object = objects.elementAt(i);

					for (int k = 0; k < object.subobjects.size(); k++) {
						SegmentedShape subobject = object.subobjects
								.elementAt(k);

						if (subobject.classID > -1
								&& subobject.getMass() > minSubObjectSize) {

							Color edgeColor = Color.BLACK;
							Color bgColor = Color.BLACK;
							Color textColour = Color.WHITE;
							if (subobject.equals(selectedSubObject)) {
								edgeColor = Color.WHITE;
							}

							int objectWidth = subobject.maxX - subobject.minX;
							int objectHeight = subobject.maxY - subobject.minY;
							int x = subobject.minX + (objectWidth / 2)
									+ imagePanel.getOffsetX();
							int y = subobject.minY + (objectHeight / 2)
									+ imagePanel.getOffsetY();
							ShapePixel highest = new ShapePixel(x, y);

							JasmineClass c = j.project
									.getSubObjectClass(subobject.classID);

							if (c != null) {
								VisionSystemGraphics.drawLabel(g2, f, highest,
										c.name, edgeColor, bgColor, textColour);
							}

						}
					}
				}
			}
		}

	}

	public void clear() {
		objects.clear();
		selectedObject = null;
		if (imagePanel.image != null) {
			shapeOverlay = new BufferedImage(imagePanel.image.getWidth(),
					imagePanel.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		} else {
			shapeOverlay = null;
		}
		if (j.currentImage != null)
			j.currentImage.clearObjects();
		repaint();
	}

	public void loadJasmineImage(JasmineImage image, BufferedImage img) {

		if (image == null) {
			setImageNull();
			clear();
		} else {

			if (img != null) {

				System.out.println("Loading jasmine image");

				if (j.project.getProperty(VisionSystem.OBJECT_SIZE_HANDLE) != null) {
					minObjectSize = (Integer) j.project
							.getProperty(VisionSystem.OBJECT_SIZE_HANDLE);
				}
				if (j.project.getProperty(VisionSystem.SUB_OBJECT_SIZE_HANDLE) != null) {
					minSubObjectSize = (Integer) j.project
							.getProperty(VisionSystem.SUB_OBJECT_SIZE_HANDLE);
				}

				if (objects != null)
					objects = new Vector<SegmentedObject>();
				selectedObject = null;
				shapeOverlay = null;

				imagePanel.setImage(img);
				j.menus.edit_clear.setEnabled(true);

				// save the width and height. This allows the overlay
				// to be reloaded with the correct dimensions.
				image.setWidth(img.getWidth());
				image.setHeight(img.getHeight());

				selectedObject = null;
				j.menus.edit_delete_shape.setEnabled(false);
				objects = image.objects;

				repaint();

			}
		}
	}

	public void classifyOthers() {
		JasmineClass currentClass = j.classbox.getCurrentClass();
		if (objects != null && currentClass != null) {
			for (int i = 0; i < objects.size(); i++) {
				SegmentedObject selected = objects.elementAt(i);
				if (mode == JasmineClass.OBJECT) {
					// classify other objects
					if (selected.getClassID() == -1
							&& selected.outline.getMass() > minObjectSize) {
						selected.setClassID(currentClass.classID);
					}
				} else {
					// classify other sub objects
					for (int k = 0; k < selected.subobjects.size(); k++) {
						SegmentedShape segmentedShape = selected.subobjects
								.elementAt(k);
						if (segmentedShape.classID == -1
								&& segmentedShape.getMass() > minSubObjectSize) {
							segmentedShape.classID = currentClass.classID;
						}
					}
				}
			}
			repaint();
		}
	}

}
