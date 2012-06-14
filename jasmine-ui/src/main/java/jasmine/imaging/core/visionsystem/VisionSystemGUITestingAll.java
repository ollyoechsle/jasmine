package jasmine.imaging.core.visionsystem;


import jasmine.imaging.commons.AccuracyStatistics;
import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.ImagePanel;
import jasmine.imaging.commons.util.ProgressDialog;
import jasmine.imaging.core.Jasmine;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Panitnat
 * Date: 19-July-2011
 * To change this template use File | Settings | File Templates.
 */

public class VisionSystemGUITestingAll extends JFrame implements VisionSystemListener, ActionListener {

    protected VisionSystem vs;
    protected ImagePanel imagePanel;
    protected JLabel statusText;
    protected JButton capture;
    protected Jasmine j;

    public VisionSystemGUITestingAll(Jasmine j, VisionSystem vs) {

        this.j = j;
     
        //POEY
        //setTitle(vs.name + " Vision System, " + j.currentTestingImage);
        
        this.vs = vs;

        //Container c = getContentPane();
/*
        if (j != null) {
            JToolBar bar = new JToolBar();
            capture = new JButton("Capture");
            capture.addActionListener(this);
            try {
                capture.setIcon(new ImageIcon(getClass().getResource("/webcam16.png")));
            } catch (Exception e) {
                //System.err.println("Could load load icon: " + icon);
            }
            bar.add(capture);
            c.add(bar, BorderLayout.NORTH);
        }

        imagePanel = new ImagePanel();
        imagePanel.setDisplayCentered(true);
        c.add(imagePanel, BorderLayout.CENTER);
        statusText = new JLabel("Working...");
        c.add(statusText, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
*/
    }

    public void actionPerformed(ActionEvent e) {
        /*try {
            if (j.webcam == null) {
                j.showWebcam();
            }
            final PixelLoader image = j.webcam.grab();
            processImage(image);
        } catch (Exception err) {
            j.alert(err.getMessage());
            err.printStackTrace();
        }*/
    }


    private ProgressDialog d;

    public void onStart() {
         if (d == null) {
        d = new ProgressDialog("Working", "Please wait...", 100);
         }
    }

    public void onSegmentationProgress(int progress) {
        if (d != null) {
            d.setValue(progress);
        }
    }

    public void onFinishedSegmentation(Vector<SegmentedObject> objects) {
        if (d != null) {
            d.dispose();
        }
    }

  public void onFinished(Vector<SegmentedObject> objects) {
    if (d != null) {
            d.dispose();
        }
    }    

    //POEY
  	public void processImageWrite(final PixelLoader image, final BufferedWriter bw, final int[] total, final int[] correct) throws Exception {
    	
        //imagePanel.setImage(image);

        new Thread() {
            public void run() {

                try {
                    //vs.addVisionSystemListener(VisionSystemGUITesting.this);
                    Vector<SegmentedObject> objects = vs.processImage(image);
                    //BufferedImage img = image.getBufferedImage();
                    //Graphics2D g = (Graphics2D) img.getGraphics();
                    //FontMetrics f = g.getFontMetrics();

                    for (int i = 0; i < objects.size(); i++) {

                        SegmentedObject segmentedObject = objects.elementAt(i);

                        // don't show objects marked as background
                        if (vs.isBackground(segmentedObject)) continue;

                        if (vs.subobjectClassifier != null) {
                        	//for sub objects
                            for (int j = 0; j < segmentedObject.subobjects.size(); j++) {
                                SegmentedShape segmentedShape = segmentedObject.subobjects.elementAt(j);
                                // draw the shape
                                //VisionSystemGraphics.drawShapeOutline(img, segmentedShape, Color.WHITE);
                                String name = vs.getName(segmentedShape);
                                if (name != null) {
                                    //Pixel labelPosition = VisionSystemGraphics.getLabelPosition(segmentedShape, 0, 0);
                                    //VisionSystemGraphics.drawLabel(g, f, labelPosition, name, Color.BLACK, Color.BLACK, Color.WHITE);
                                	
                                	//POEY                                	
                                    System.out.print(", " + name);
                                    bw.write("," + name);
                                    //write 1 at the end of the line if the class result is correct
                                    if(image.getFilename().toLowerCase().startsWith(name.toLowerCase())){
                                    	bw.write(",1");
                                    	correct[0]++;
                                    	total[0]++;
                                    }                                   	
                                    else{
                                    	bw.write(",0");
                                    	total[0]++;
                                    }
                                }
                            }
                        }

                        String name = vs.getName(segmentedObject);
                        if (name != null) {
                            //Pixel labelPosition = VisionSystemGraphics.getLabelPosition(segmentedObject.outline, 0, 0);
                            //VisionSystemGraphics.drawLabel(g, f, labelPosition, name, Color.BLACK, Color.BLACK, Color.WHITE);
                            
                            //POEY
                            System.out.print(", " + name);
                            bw.write("," + name);
                            //write 1 at the end of the line if the class result is correct
                            if(image.getFilename().toLowerCase().startsWith(name.toLowerCase())){
                            	bw.write(",1"); 
                            	correct[0]++;
                            	total[0]++;
                            }
                            else{
                            	bw.write(",0");
                            	total[0]++;
                            }
                        }

                    }
                    //statusText.setText("Found " + objects.size() + " object(s).");
                    //imagePanel.repaint();
                    System.out.println();
                    bw.write("\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        //}.start();	//parallel running
            
        //POEY
        }.run();
    }

}


