package jasmine.imaging.core.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.File;

/**
 * Vision system component
 */
public class ComponentPanel extends JPanel {

    int padding = 5;
    int olen = 30;
    int oh = 20;
    int ilen = 20;
    int ih = 40;

    public static final int IN = 1;
    public static final int OUT = 2;
    public static final int BOTH = 3;

    public File component;

    public File getFile() {
        return component;
    }


    public void setFile(String filepath) {
        if (filepath == null || filepath.equals("")) setFileNull();
        else setFile(new File(filepath));
    }

    public void setFileNull() {
        this.component = null;
        this.details = "[Not loaded]";
        repaint();
    }

    public void setFile(File component) {
        this.component = component;
        if (component != null) {
            this.details = component.getName();
        } else {
            this.details = "[Not loaded]";
        }
        repaint();
    }

    public String getAbsolutePath() {
        if (component == null) return "";
        return component.getAbsolutePath();
    }

    public String getDetails() {
        return details;
    }

//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            System.out.println("Unable to load native look and feel");
//        }
//        JFrame f = new JFrame();
//        Container c = f.getContentPane();
//        c.setLayout(new GridLayout(1, 3));
//        c.add(new ComponentPanel("Segmenter", OUT, "/draw24.png"));
//        c.add(new ComponentPanel("Classifier", BOTH, "/shape24.png"));
//        c.add(new ComponentPanel("Post Processor", IN, "/process24.png"));
//        f.setSize(400, 200);
//        f.setVisible(true);
//    }

    protected boolean in, out;
    protected String name;
    protected String details = "[Not loaded]";
    protected BufferedImage icon;
    protected BufferedImage browse;
    protected boolean mouseDown;

    public void setType(int TYPE) {
        switch (TYPE) {
            case IN:
                this.in = true;
                this.out = false;
                break;
            case OUT:
                this.in = false;
                this.out = true;
                break;
            case BOTH:
                this.in = true;
                this.out = true;
        }
    }

    public ComponentPanel(String name, int TYPE, String icon) {
        this.name = name;
        if (icon != null) {
            try {
                this.icon = javax.imageio.ImageIO.read(getClass().getResource(icon));
                this.browse = javax.imageio.ImageIO.read(getClass().getResource("/open24.png"));
            } catch (Exception e) {
            }
            ;
        }
        setType(TYPE);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    onClear();
                } else {
                    mouseDown = true;
                    repaint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    mouseDown = false;
                    repaint();
                    onMouseClicked();
                }
            }
        });
    }

    public void onMouseClicked() {
    }

    public void onClear() {
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
        Polygon p = new Polygon();

        int halfHeight = getHeight() / 2;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        p.addPoint(padding, padding);

        int x2 = out ? getWidth() - padding - olen : getWidth() - padding;

        p.addPoint(x2, padding);

        if (out) {
            // outwards point
            p.addPoint(x2, halfHeight - oh);
            p.addPoint(x2 + olen, halfHeight - oh);

            p.addPoint(x2 + olen, halfHeight + oh);
            p.addPoint(x2, halfHeight + oh);
            // end outwards point
        }

        p.addPoint(x2, getHeight() - padding);

        p.addPoint(padding, getHeight() - padding);

        if (in) {
            // inwards point
            p.addPoint(padding, halfHeight + ih);
            p.addPoint(padding + ilen, halfHeight + ih);

            p.addPoint(padding + ilen, halfHeight - ih);
            p.addPoint(padding, halfHeight - ih);
            // end inwards point
        }

        p.addPoint(padding, padding);

        if (component == null) {
            g.setColor(new Color(20, 90, 150, 32));
        } else {
            g.setColor(new Color(20, 90, 150, 64));
        }

        if (mouseDown) {
            g.setColor(Color.WHITE);
        }

        g2.fillPolygon(p);
        g.setColor(new Color(20, 90, 150));
        Stroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);
        g2.drawPolygon(p);

        if (out) {
            g2.drawLine(x2 + olen, halfHeight, getWidth(), halfHeight);
        }

        if (in) {
            g2.drawLine(0, halfHeight, padding + ilen, halfHeight);
        }

        Rectangle2D bounds = g2.getFontMetrics().getStringBounds(name, g2);
        int width = getWidth();
        int txtPad = padding;
        if (out) {
            width -= olen;
        }
        if (in) {
            width -= ilen;
            txtPad += ilen;
        }

        int sx = (int) ((width - bounds.getWidth()) / 2);
        int sy = (int) ((getHeight() - bounds.getHeight()) / 2);
        g2.drawString(name, sx + txtPad, sy + (int) bounds.getHeight());

        int ix = (int) ((bounds.getWidth() - icon.getWidth()) / 2);
        if (component == null) {
            g2.drawImage(browse, sx + ix + txtPad, sy - icon.getHeight(), null);
        } else {
            if (icon != null) {
                g2.drawImage(icon, sx + ix + txtPad, sy - icon.getHeight(), null);
            }
        }

        if (details != null) {
            Rectangle2D bounds2 = g2.getFontMetrics().getStringBounds(details, g2);
            sx = (int) ((width - bounds2.getWidth()) / 2);
            g2.drawString(details, sx + txtPad, sy + (int) (bounds.getHeight() * 2));
        }


    }

}
