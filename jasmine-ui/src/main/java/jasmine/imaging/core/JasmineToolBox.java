package jasmine.imaging.core;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
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
 * @author Olly Oechsle, University of Essex, Date: 30-May-2007
 * @version 1.0
 */
public class JasmineToolBox extends JToolBar implements ChangeListener, ActionListener {

    JSlider size;
    JLabel lblSize;

    JPanel pixelMarkupPanel;
    JPanel shapeMarkupPanel;

    protected JButton segment, select_shape, clear_shapes, classify_others, filterBySize, defineClass;
    protected ToggleToolboxButton classifyMode, segmentationMode;

    
    protected JButton test, check;

    protected Jasmine j;

    protected Vector<ToolboxButton> buttons;

    public JasmineToolBox(final Jasmine j) {

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        this.j = j;

        setOrientation(JToolBar.HORIZONTAL);

        size = new JSlider(JSlider.HORIZONTAL, 1, 50, j.segmentationPanel.DEFAULT_SIZE);
        lblSize = new JLabel(String.valueOf(j.segmentationPanel.DEFAULT_SIZE));
        //size.setBorder(BorderFactory.createTitledBorder("Brush Size"));
        size.setMajorTickSpacing(10);
        size.setMinorTickSpacing(2);
        size.setPaintTicks(true);
        size.setPaintLabels(false);
        size.setValue(30);
        size.addChangeListener(this);
        size.setPreferredSize(new Dimension(100, 50));
        size.setToolTipText("Change the brush size");

        buttons = new Vector<ToolboxButton>();

        j.PAINT = new ToolboxButton(buttons, "Paint", "Paint class overlays", "paint.png", Jasmine.PIXEL_SEGMENTATION);
        j.LINE = new ToolboxButton(buttons, "Lines", "Draw class overlay lines", "edit.png", Jasmine.PIXEL_SEGMENTATION);
        j.TARGET = new ToolboxButton(buttons, "Target", "Target specific pixels", "target.png", Jasmine.PIXEL_SEGMENTATION);
        j.ERASE = new ToolboxButton(buttons, "Erase", "Erase parts of the overlay", "clear.png", Jasmine.PIXEL_SEGMENTATION);
        j.HISTOGRAM = new ToolboxButton(buttons, "Histogram", "View Histogram", "distribution.png", Jasmine.PIXEL_SEGMENTATION);
        segmentationMode = new ToggleToolboxButton(buttons, "Mask", "Materials", "Allows you to paint the object/sub object mask", "Allows you to define materials", "objects.png", "sub_objects.png", Jasmine.PIXEL_SEGMENTATION);

        segment = new ToolboxButton(buttons, "Segment", "Segment", "segment.png", Jasmine.OBJECT_CLASSIFICATION);
        select_shape = new ToolboxButton(buttons, "Select", "Select Shape", "pointer.png", Jasmine.OBJECT_CLASSIFICATION);
        clear_shapes = new ToolboxButton(buttons, "Clear", "Clear All Shapes", "clear.png", Jasmine.OBJECT_CLASSIFICATION);
        classify_others = new ToolboxButton(buttons, "Label Others", "Labels all other objects in the scene that haven't been labelled yet with the current class.", "classify.png", Jasmine.OBJECT_CLASSIFICATION);
        classifyMode = new ToggleToolboxButton(buttons, "Objects", "SubObjects", "Selects objects in the scene", "Selects sub objects", "objects.png", "sub_objects.png", Jasmine.OBJECT_CLASSIFICATION);
        filterBySize = new ToolboxButton(buttons, "Filter", "Filter objects and sub-objects by size", "filter.png", Jasmine.OBJECT_CLASSIFICATION);
        
        //POEY
        defineClass = new ToolboxButton(buttons, "Define a Class", "Segment an object and define a class to it automatically", "segment.png", Jasmine.OBJECT_CLASSIFICATION);

        check = new ToolboxButton(buttons, "Check", "Tests whether the components work properly", "measure.png", Jasmine.VISION_SYSTEM);
        test = new ToolboxButton(buttons, " Test ", "Tests the components on the project training data", "ok.png", Jasmine.VISION_SYSTEM);

        j.PAINT.setSelected(true);

        showButtons(j.mode);

    }

    /**
     * Shows and hides buttons as appropriate, given the new mode.
     *
     * @param mode
     */
    public void showButtons(int mode) {
        removeAll();
        for (int i = 0; i < buttons.size(); i++) {
            ToolboxButton toolboxButton = buttons.elementAt(i);
            toolboxButton.updateVisibility(mode);
        }
        if (mode == Jasmine.PIXEL_SEGMENTATION) {
            addSeparator();
            add(new JLabel(" Brush Size: "));
            add(size);
            add(Box.createHorizontalStrut(5));
            add(lblSize);
        }
        add(Box.createHorizontalGlue());
        repaint();
    }

    public void actionPerformed(ActionEvent e) {

        ToolboxButton me = (ToolboxButton) e.getSource();

        // unselect the buttons
        for (int i = 0; i < buttons.size(); i++) {
            ToolboxButton otherButton = buttons.elementAt(i);
            if (otherButton.mode == me.mode) {
                otherButton.setSelected(false);
            }
        }
        me.setSelected(true);

        if (e.getSource() == segment) {
            j.segmentCurrentImage((JButton) e.getSource());
            me.setSelected(false);
        }

        if (e.getSource() == select_shape) {
            select_shape.setSelected(select_shape.isSelected());
        }

        if (e.getSource() == classify_others) {
            j.classificationPanel.classifyOthers();
            me.setSelected(false);
        }

        if (e.getSource() == clear_shapes) {
            j.classificationPanel.clear();
            me.setSelected(false);
        }

        if (e.getSource() == test) {
            j.visionSystemPanel.test();
        }

        if (e.getSource() == check) {
            j.visionSystemPanel.check();
        }

        if (e.getSource() == filterBySize) {
            DialogObjectSize.show(j);
        }
        
        //POEY
        if (e.getSource() == defineClass) {
            j.classificationPanel.defineClass((JButton) e.getSource());
            me.setSelected(false);                       
        }

        if (!(me instanceof ToggleToolboxButton)) {
            if (me.mode == Jasmine.PIXEL_SEGMENTATION) {
                j.segmentationMode = me;
            } else {
                j.classificationMode = me;
            }
        }

        if (e.getSource() == classifyMode) {
            classifyMode.toggle();
            if (classifyMode.state) {
                j.classificationPanel.setMode(JasmineClassificationPanel.SELECT_OBJECTS);             
            } else {
                j.classificationPanel.setMode(JasmineClassificationPanel.SELECT_SUBOBJECTS);
            }
        }

        if (e.getSource() == segmentationMode) {
            segmentationMode.toggle();
            if (segmentationMode.state) {
                j.segmentationPanel.setMode(JasmineClass.MASK);
            } else {
                j.segmentationPanel.setMode(JasmineClass.MATERIAL);
            }
        }

    }

    public void setEnabled(boolean enabled) {
        for (int i = 0; i < buttons.size(); i++) {
            ToolboxButton button = buttons.elementAt(i);
            button.setEnabled(enabled);
        }
        size.setEnabled(enabled);
        lblSize.setEnabled(enabled);
    }

    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        if (!slider.getValueIsAdjusting()) {
            j.segmentationPanel.setCursorSize(slider.getValue());
        }
        lblSize.setText(String.valueOf(slider.getValue()));
    }

    class ToolboxButton extends JButton {

        protected int mode;
        protected String icon;

        public ToolboxButton(Vector<ToolboxButton> buttons, String text, String tooltip, String icon, int mode) {

            this.icon = icon;
            buttons.add(this);
            setToolTipText(tooltip);
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setPreferredSize(new Dimension(80, 50));
            setMinimumSize(new Dimension(80, 50));
            setText(text);
            Font f = getFont();
            f.getSize();
            setFont(new Font(f.getName(), f.getStyle(), 10));
            try {
                setIcon(new ImageIcon(getClass().getResource("/" + icon)));
            } catch (Exception e) {
                //System.err.println("Could load load icon: " + icon);
            }
            addActionListener(JasmineToolBox.this);
            putClientProperty("JButton.buttonType", "text");
            this.mode = mode;

        }

        public void updateVisibility(int newMode) {
            if (newMode == this.mode) {
                JasmineToolBox.this.add(this);
            }
        }

        public String toString() {
            return icon;
        }

    }

    class ToggleToolboxButton extends ToolboxButton {

        ImageIcon icon1, icon2;
        String text1, text2;
        String tooltip1, tooltip2;
        boolean state = true;

        ToggleToolboxButton(Vector<ToolboxButton> buttons, String text1, String text2, String tooltip1, String tooltip2, String icon1, String icon2, int mode) {
            super(buttons, text1, tooltip1, icon1, mode);
            this.text1 = text1;
            this.text2 = text2;
            this.tooltip2 = tooltip2;
            this.tooltip1 = tooltip1;
            try {
                this.icon1 = (ImageIcon) getIcon();
                this.icon2 = (new ImageIcon(getClass().getResource("/" + icon2)));
            } catch (Exception e) {
                //System.err.println("Could load load icon: " + icon);
            }
        }

        public void toggle() {
            state = !state;
            if (state) {
                setIcon(icon1);
                setText(text1);
                setToolTipText(tooltip1);
            } else {
                setIcon(icon2);
                setText(text2);
                setToolTipText(tooltip2);
            }
        }

    }


}