package jasmine.imaging.core;


import jasmine.imaging.commons.Pixel;
import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.Segmenter;
import jasmine.imaging.commons.util.ImagePanel;
import jasmine.imaging.core.visionsystem.VisionSystem;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.Grouper;
import jasmine.imaging.shapes.SegmentedShape;
import jasmine.imaging.shapes.ShapePixel;
import jasmine.imaging.shapes.SubObjectClassifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
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
 * @author Olly Oechsle, University of Essex, Date: 08-Feb-2007
 * @version 1.0
 */
public class DialogRun extends JDialog {

    public static final int MODE_SEGMENT_AND_CLASSIFY = 1;
    public static final int MODE_SEGMENT_ONLY = 2;

    protected int mode;

    protected DisplayPanel imagePanel;

    protected Vector<ExtraShapeData> shapes = null;

    protected JasmineProject project;

    protected JProgressBar status;

    protected Jasmine jasmine;

    protected boolean stop = true;

    protected JTextField time;

    protected boolean keydown = false;

    protected String filename;

      
    protected String SEGMENTER_HANDLE;

    public DialogRun(final Jasmine jasmine, JasmineProject project, final JasmineImage image, int mode, String SEGMENTER_HANDLE) {
        this(jasmine, project, image.getBufferedImage(), image.filename, mode, SEGMENTER_HANDLE);
    }

    public DialogRun(final Jasmine jasmine, JasmineProject project, final BufferedImage image, String filename, int mode, String SEGMENTER_HANDLE) {

        this.SEGMENTER_HANDLE = SEGMENTER_HANDLE;
        int pixelMode;
        if (SEGMENTER_HANDLE.equals(VisionSystem.SEGMENTER_HANDLE)) {
            pixelMode = JasmineClass.MATERIAL;
        } else {
            pixelMode = JasmineClass.MASK;
        }

        if (image == null) return;

        this.mode = mode;

        setTitle(filename);

        this.project = project;
        this.jasmine = jasmine;

        Container c = getContentPane();
        imagePanel = new DisplayPanel();
        imagePanel.setDisplayCentered(true);
        c.add(imagePanel, BorderLayout.CENTER);

        imagePanel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                keydown = true;
            }

            public void mouseReleased(MouseEvent e) {
                if (letter.size() > 0) {
                    letter.add(new Pixel(-1, -1));
                }
                keydown = false;
            }

        });

        if (jasmine != null && jasmine.webcam != null) {
            JToolBar toolbar = new JToolBar();
            final JButton btnCapture = new JButton("Test on Webcam");
            btnCapture.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (stop) {
                        stop = false;
                        Thread t = new Thread() {
                            public void run() {
                                while (!stop) {
                                    try {
                                        int t = Integer.parseInt(time.getText());
                                        sleep(t);
                                    } catch (Exception e) {
                                        // do nothing
                                    }
                                    test(jasmine.webcam.grab());
                                }
                            }
                        };
                        t.start();
                        btnCapture.setText("Stop");
                    } else {
                        stop = true;
                        btnCapture.setText("Test on Webcam");
                    }
                }
            });


            toolbar.add(btnCapture);
            time = new JTextField("400");
            toolbar.add(new JLabel("Interval: "));
            toolbar.add(time);

            c.add(toolbar, BorderLayout.NORTH);
        }

        status = new JProgressBar();
        status.setMaximum(image.getHeight());
        status.setMinimum(0);
        status.setValue(0);
        c.add(status, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stop = true;
                dispose();
            }
        });

        setSize(image.getWidth() + 50, image.getHeight() + 70);
        if (jasmine != null) {
            setLocation((int) jasmine.getLocation().getX(), (int) jasmine.getLocation().getY() + 100);
        }
        setVisible(true);

        if (!dontShowStatusBar) {
            status.setVisible(true);
        } else {
            status.setVisible(false);
        }

        // first put the jasmine classes into an array indexed by their classID. This is
        // faster than repeated calls to the getMaterialClass() method on the project.
        pixelClasses = new JasmineClass[100];
        Vector<JasmineClass> classes = project.getPixelClasses(pixelMode);
        for (int i = 0; i < classes.size(); i++) {
            JasmineClass jasmineClass = classes.elementAt(i);
            pixelClasses[jasmineClass.classID] = jasmineClass;
        }

        test(new PixelLoader(image));

    }

    JasmineClass[] pixelClasses;

    boolean dontShowStatusBar = false;

    int pixels[][];

    // caches of the segmenter and classifier
    private Segmenter s;
    private SubObjectClassifier classifier;

    //long time = 500;

    public void test(final PixelLoader image) {

        PixelLoader.CACHE_RGB_HSL = true;
        PixelLoader.CACHING_OTHERS = true;

        Thread t = new Thread() {
            public void run() {

                long start = System.currentTimeMillis();

                if (s == null) {
                    if (jasmine != null) {
                        s = jasmine.getSegmenter(SEGMENTER_HANDLE);
                    }
                }

                if (s == null) {
                    if (jasmine != null) {
                        jasmine.alert("No segmenter set up.");
                    } else {
                        System.err.println("No segmenter set up");
                    }
                    return;
                }

                pixels = s.segment(image, !dontShowStatusBar ? status : null);

                if (SEGMENTER_HANDLE.equals(VisionSystem.SEGMENTER_HANDLE)) {
                    // go through and remove background pixels
                    for (int i = 0; i < pixels.length; i++) {
                        int[] classID = pixels[i];
                        for (int j = 0; j < classID.length; j++) {
                            JasmineClass c = pixelClasses[classID[j]];
                            if (c == null || c.background) classID[j] = 0;
                        }
                    }
                } else {
                     // go through and remove background pixels
                    // find the first object class
                    JasmineClass objectClass = null;
                    for (int i = 0; i < pixelClasses.length; i++) {
                        JasmineClass pixelClass = pixelClasses[i];
                        if (pixelClass != null && !pixelClass.background) {
                            objectClass = pixelClass;
                            break;
                        }
                    }
                    for (int i = 0; i < pixels.length; i++) {
                        int[] classID = pixels[i];
                        for (int j = 0; j < classID.length; j++) {
                            if (classID[j] != 0) classID[j] = objectClass.classID;
                        }
                    }                
                }

                // get the shapes
                // now process the shapes
                if (classifier == null) {

                    if (jasmine != null) {
                        classifier = jasmine.getShapeClassifier();
                    }
                }

                Vector<SegmentedShape> shapeData = new Grouper().getShapes(pixels);

                shapes = new Vector<ExtraShapeData>(shapeData.size());

                // build extrashape data objects
                for (int i = 0; i < shapeData.size(); i++) {
                    SegmentedShape segmentedShape = shapeData.elementAt(i);
                    if (segmentedShape.getMass() > 50) {
                        ExtraShapeData esd = new ExtraShapeData(segmentedShape, image);
                        shapes.add(esd);

                    }
                }


                if (classifier != null && mode != MODE_SEGMENT_ONLY) {


                    for (int i = 0; i < shapes.size(); i++) {
                        ExtraShapeData segmentedShape = shapes.elementAt(i);
                        try {
                            segmentedShape.setClassID(classifier.classify(segmentedShape));
                        } catch (Exception e) {
                            System.out.println("We all make mistakes");
                            segmentedShape.setClassID(-1);
                        }
                    }

                }

                if (!dontShowStatusBar) {
                    status.setValue(status.getMaximum());
                    status.setVisible(false);
                }

                imagePanel.createImage(image.getBufferedImage());
                imagePanel.repaint();

                //time = System.currentTimeMillis() - start;

                //System.out.println(time);

            }
        };

        t.start();

    }

    public Vector<Pixel> letter = new Vector<Pixel>(10);

    int white = Color.WHITE.getRGB();

    class DisplayPanel extends ImagePanel {

        int palmsInARow = 0;
        int fistsInARow = 0;

        public DisplayPanel() {
            setDisplayCentered(true);
        }

        public void createImage(BufferedImage image) {


            if (image == null) return;

            Graphics2D draw = (Graphics2D) image.getGraphics();

            //draw.setColor(Color.BLACK);
            //draw.fillRect(0, 0, getWidth(), getHeight());

            if (setDisplayCentered) {
                offsetX = (getWidth() - (image.getWidth() * zoom)) / 2;
                offsetY = (getHeight() - (image.getHeight() * zoom)) / 2;
            }



            if (shapes != null && shapes.size() > 0) {

                for (int j = 0; j < shapes.size(); j++) {

                    ExtraShapeData s = shapes.elementAt(j);

                    // ignore small shapes
                    if (s.getMass() < 50) continue;
                    if (mode == MODE_SEGMENT_AND_CLASSIFY) {
                        if (s.getClassID() == -1) continue;
                    } else {
                        if (s.getShape().originalValue == -1) continue;
                    }


                    JasmineClass c;

                    if (mode == MODE_SEGMENT_AND_CLASSIFY) {
                        c = project.getShapeClass(s.getClassID());
                    } else {
                        c = project.getMaterialClass(s.getShape().originalValue);
                    }

                    if (c != null) {
                        white = c.color.getRGB();

                    }

                    /*                   if (c != null) {
                        // drawing hack
                        if (c.classID == 4) {
                            Pixel furthestPixel = s.getFurthestPixelFromCentre();
                            if (furthestPixel != null) {
                                int x = furthestPixel.x;// + offsetX;
                                int y = furthestPixel.y;// + offsetY;
                                draw.setColor(Color.GREEN);
                                draw.drawOval(x, y, 10, 10);
                                if (keydown) {
                                    letter.add(new Pixel(x, y));
                                }
                            }
                        }

                        if (c.classID == 3) {
                            palmsInARow++;
                            // palm
                            if (palmsInARow > 4) {
                                //letter.clear();
                            }
                        } else {
                            palmsInARow = 0;
                        }

                    }*/


                    String name;

                    if (c == null) {
                        name = "Class " + s.getClassID();
                    } else {
                        name = c.name;
                        if (c.background) continue;
                    }

                    // draw edge around it
                    for (int k = 0; k < s.getShape().edgePixels.size(); k++) {
                        ShapePixel shapePixel = s.getShape().edgePixels.elementAt(k);
                        int x = shapePixel.x;// + offsetX;
                        int y = shapePixel.y;// + offsetY;
                        image.setRGB(x, y, white);
                        //draw.drawLine(x, y, x + 1, y);
                    }

                    //Graphics2D g2d = (Graphics2D) g;
                    draw.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    //Pixel p = s.getCentreOfGravity();
                    Pixel p = new Pixel(s.boundingWidth / 2, s.boundingHeight / 2);
                    int xPos = s.getShape().minX + p.x;
                    int yPos = s.getShape().minY;
                    draw.setColor(Color.BLACK);
                    draw.setFont(new Font("Garuda", Font.PLAIN, 12));
                    FontMetrics f = draw.getFontMetrics();
                    Rectangle2D r = f.getStringBounds(name, draw);
                    int lineposy = yPos - (int) r.getHeight() / 2;
                    draw.drawLine(xPos, lineposy, xPos, yPos + p.y);

                    draw.fillRect(xPos, yPos + 2 - ((int) r.getHeight()), (int) r.getWidth() + 4, (int) r.getHeight());
                    draw.setColor(Color.WHITE);
                    draw.drawString(name, xPos + 2, yPos);

                    draw.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                }


            }

            // TODO: Hack for gesture drawing
/*            draw.setColor(Color.BLUE);
            draw.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            try {
            if (jasmine.getShapeClassifier() instanceof GestureClassifier2 && letter.size() > 0) {
                Pixel lastPoint = letter.elementAt(0);
                for (int i = 1; i < letter.size(); i++) {
                    Pixel point = letter.elementAt(i);
                    if (point.x == -1 && point.y == -1) {
                        lastPoint = letter.elementAt(i + 1);
                        i++;
                        continue;
                    }
                    draw.drawLine(lastPoint.x, lastPoint.y, point.x, point.y);
                    lastPoint = point;
                }
            }
            } catch (Exception e) {
                // do nothing
            }*/

            //((Graphics2D) g).setStroke(new BasicStroke(1f));

            // hack to save video frames
/*
            count++;
            try {
                String c = String.valueOf(count);
                if (count < 10) c = "000" + count;
                else if (count < 100) c = "00" + count;
                else if (count < 1000) c = "0" + count;
                javax.imageio.ImageIO.write(image, "jpg", new File("/home/ooechs/tmp/video" + c + ".jpg"));
            } catch (Exception e) {

            }
*/

            setImage(image);


        }

        public void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (image != null)
                drawImage(g, image);

        }
    }

    int count = 0;


}
