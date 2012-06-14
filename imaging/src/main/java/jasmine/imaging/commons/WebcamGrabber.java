package jasmine.imaging.commons;



import javax.media.*;
import javax.media.util.BufferToImage;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FormatControl;
import javax.media.format.VideoFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.Collections;

public class WebcamGrabber {

    public JFrame window;
    private Player player;
    private Component visualComponent;

//    public static void main(String[] args) throws Exception {
//        new WebcamGrabber();
//    }

    public WebcamGrabber() throws Exception {
        this(true);
    }

    public WebcamGrabber(boolean display) throws Exception {
        try {

            //gets a list of devices how support the given videoformat
            MyCaptureDeviceInfo[] cams = autoDetect();

            if (cams.length == 0) {
                throw new Exception("No webcams detected - did you run jmfinit?");
            }

            Object selected = JOptionPane.showInputDialog(null,
                    "Select Video format",
                    "Capture format selection",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,        //  Icon icon,
                    cams, // videoFormats,
                    cams[0]);

            CaptureDeviceInfo device;

            if (selected != null) {
                device = ((MyCaptureDeviceInfo) selected).capDevInfo;
            } else {
                return;
            }

            MediaLocator ml = device.getLocator();

            player = Manager.createRealizedPlayer(ml);

            player.start();

            FormatControl formatControl = (FormatControl) player.getControl("javax.media.control.FormatControl");
            Format[] videoFormats = device.getFormats();

            MyVideoFormat[] myFormatList = new MyVideoFormat[videoFormats.length];
            for (int i = 0; i < videoFormats.length; i++) {
                myFormatList[i] = new MyVideoFormat((VideoFormat) videoFormats[i]);
            }

            Format currFormat = formatControl.getFormat();

            visualComponent = player.getVisualComponent();

            if (visualComponent != null) {

                if (display) {

                    window = new JFrame("Camera");
                    window.add(visualComponent);
                    window.setSize(320, 240);
                    window.setVisible(true);

                    window.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            player.stop();
                            window.dispose();
                        }
                    });

                }

                if (currFormat instanceof VideoFormat) {
                    VideoFormat currentFormat = (VideoFormat) currFormat;
                    Dimension imageSize = currentFormat.getSize();
                    if (display) window.setPreferredSize(imageSize);
                    else visualComponent.setPreferredSize(imageSize);
                } else {
                    System.err.println("Error : Cannot get current video format");
                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Component getVisualComponent() {
        return visualComponent;
    }

    public PixelLoader grab() {
        long start = System.currentTimeMillis();
        BufferedImage image = toBufferedImage(grabFrameImage());
        PixelLoader p = new PixelLoader(image, null);
        return p;
    }

    private Buffer grabFrameBuffer() {
        if (player != null) {
            FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
            if (fgc != null) {
                return (fgc.grabFrame());
            } else {
                System.err.println("Error : FrameGrabbingControl is null");
                return (null);
            }
        } else {
            System.err.println("Error : Player is null");
            return (null);
        }
    }

    private Image grabFrameImage() {
        Buffer buffer = grabFrameBuffer();
        if (buffer != null) {
            // Convert it to an image
            BufferToImage btoi = new BufferToImage((VideoFormat) buffer.getFormat());
            if (btoi != null) {
                Image image = btoi.createImage(buffer);
                if (image != null) {
                    return (image);
                } else {
                    System.err.println("Error : BufferToImage cannot convert buffer");
                    return (null);
                }
            } else {
                System.err.println("Error : cannot create BufferToImage instance");
                return (null);
            }
        } else {
            System.out.println("Error : Buffer grabbed is null");
            return (null);
        }
    }

    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = false;

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    class MyCaptureDeviceInfo implements Comparable {
        public CaptureDeviceInfo capDevInfo;

        public MyCaptureDeviceInfo(CaptureDeviceInfo devInfo) {
            capDevInfo = devInfo;
        }

        public int compareTo(Object o) {
            MyCaptureDeviceInfo other = (MyCaptureDeviceInfo) o;
            boolean thisV = this.capDevInfo.getName().startsWith("v");
            boolean otherV = other.capDevInfo.getName().startsWith("v");
            if (thisV && !otherV) return -1;
            if (!thisV && otherV) return +1;
            return 0;               
        }

        public String toString() {
            return (capDevInfo.getName());
        }
    }

    public MyCaptureDeviceInfo[] autoDetect() {
        Vector list = CaptureDeviceManager.getDeviceList(null);

        CaptureDeviceInfo devInfo = null;
        String name;
        Vector capDevices = new Vector();

        if (list != null) {

            for (int i = 0; i < list.size(); i++) {
                devInfo = (CaptureDeviceInfo) list.elementAt(i);
                name = devInfo.getName();

                //if (name.startsWith("vf")) {
                    System.out.println("DeviceManager List : " + name);
                    capDevices.addElement(new MyCaptureDeviceInfo(devInfo));
                //}
                System.out.println(name);
            }
        }

        Collections.sort(capDevices);

        MyCaptureDeviceInfo[] detected = new MyCaptureDeviceInfo[capDevices.size()];
        for (int i = 0; i < capDevices.size(); i++) {
            detected[i] = (MyCaptureDeviceInfo) capDevices.elementAt(i);
        }

        return (detected);
    }

}
