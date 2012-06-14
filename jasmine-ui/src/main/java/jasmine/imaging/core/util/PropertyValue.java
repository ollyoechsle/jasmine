package jasmine.imaging.core.util;

import javax.swing.*;
import java.awt.*;

public class PropertyValue extends JPanel {

    public static final int lblWidth = 120;
    public static final int height = 50;

    public PropertyValue(String label, JComponent component) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        JLabel l = new JLabel(label + ":  ", JLabel.RIGHT);
        l.setPreferredSize(new Dimension(lblWidth, height));
        add(l);
        add(component);
        add(Box.createHorizontalGlue());
        add(Box.createHorizontalStrut(5));
        this.setPreferredSize(new Dimension(-1, height));
    }

   public PropertyValue(String label, JComponent component, JComponent other) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        JLabel l = new JLabel(label + ":  ", JLabel.RIGHT);
        l.setPreferredSize(new Dimension(lblWidth, height));
        add(l);
        add(component);
        add(Box.createHorizontalGlue());
        add(Box.createHorizontalStrut(5));
       add(other);
       add(Box.createHorizontalStrut(5));
        this.setPreferredSize(new Dimension(-1, height));
    }



}
