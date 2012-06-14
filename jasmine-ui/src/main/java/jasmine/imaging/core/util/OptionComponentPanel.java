package jasmine.imaging.core.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Vision system component
 */
public class OptionComponentPanel extends JPanel {

    int padding = 5;
    int olen = 30;
    int oh = 20;
    int ilen = 20;
    int ih = 40;

    public static final int IN = 1;
    public static final int OUT = 2;
    public static final int BOTH = 3;

    public JComponent component;

    public String getDetails() {
        return details;
    }

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

//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            System.out.println("Unable to load native look and feel");
//        }
//        JFrame f = new JFrame();
//        Container c = f.getContentPane();
//        OptionComponentPanel oc = new OptionComponentPanel("Shape Processing", OUT, "/draw24.png");
//        oc.add(new JButton("Hello"));
//        c.add(oc);
//        f.setSize(400, 200);
//        f.setVisible(true);
//    }

    public OptionComponentPanel(String name, int TYPE, String icon) {
        this.name = name;
        if (icon != null) {
            try {
                this.icon = javax.imageio.ImageIO.read(getClass().getResource(icon));
            } catch (Exception e) {
            }
        }
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel l = new JLabel(name);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(50));
        add(l);
        setType(TYPE);
    }

    public Component add(Component comp) {
        if (comp instanceof JComponent) ((JComponent) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
        return super.add(comp);
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
   

    }

}