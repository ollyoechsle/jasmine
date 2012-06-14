package jasmine.imaging.core;


import jasmine.imaging.core.util.OKCancelBar;
import jasmine.imaging.core.util.PropertyValue;
import jasmine.imaging.core.visionsystem.VisionSystem;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;

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
 * @author Olly Oechsle, University of Essex, Date: 13-Feb-2007
 * @version 1.0
 */
public class DialogObjectSize extends JDialog implements ActionListener, ChangeListener {

    protected JSlider objectSize, subObjectSize;
    protected JButton save, close;
    protected JLabel lblObjectSize;
    protected JLabel lblSubObjectSize;

    private Jasmine jasmine;

//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            System.out.println("Unable to load native look and feel");
//        }
//        new DialogTrainingSize(null, 1000);
//    }

    static DialogObjectSize currentInstance;

    public static void show(Jasmine j) {
        if (currentInstance == null) {
            new DialogObjectSize(j);
        } else {
            currentInstance.setVisible(true);
            currentInstance.requestFocus();
        }
    }

    public DialogObjectSize(Jasmine jasmine) {

        super(jasmine);
        this.jasmine = jasmine;

        currentInstance = this;

        try {
            setIconImage(new ImageIcon(getClass().getResource("/measure16.png")).getImage());
        } catch (Exception e) {
        }

        setTitle("Object Size Filter");

        setModal(true);

        if (jasmine != null) {
            setIconImage(jasmine.getIconImage());
        }

        int objectSize = 1;
        int subObjectSize = 1;

        if (jasmine != null && jasmine.project.getProperty(VisionSystem.OBJECT_SIZE_HANDLE) != null) {
            objectSize = (Integer) jasmine.project.getProperty(VisionSystem.OBJECT_SIZE_HANDLE);
        }
        if (jasmine != null && jasmine.project.getProperty(VisionSystem.SUB_OBJECT_SIZE_HANDLE) != null) {
            subObjectSize = (Integer) jasmine.project.getProperty(VisionSystem.SUB_OBJECT_SIZE_HANDLE);
        }

        this.objectSize = new JSlider(1, 1000, objectSize);
        this.objectSize.addChangeListener(this);

        this.subObjectSize = new JSlider(1, 1000, subObjectSize);
        this.subObjectSize.addChangeListener(this);

        this.lblObjectSize = new JLabel();
        this.lblSubObjectSize = new JLabel();

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Object Size", this.objectSize, lblObjectSize));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Sub Object Size %", this.subObjectSize, lblSubObjectSize));
        fields.add(Box.createVerticalStrut(5));

        save = new JButton("Save");
        close = new JButton("Close");

        save.addActionListener(this);
        close.addActionListener(this);

        getContentPane().add(fields, BorderLayout.CENTER);
        getContentPane().add(new OKCancelBar(save, close), BorderLayout.SOUTH);

        updateLabels();

        setSize(300, 120);
        setLocationRelativeTo(jasmine);
        setResizable(false);
        setVisible(true);

    }


    public void onClose() throws IOException {
        dispose();
    }

    public synchronized void stateChanged(ChangeEvent e) {
        System.out.println("Changed");
        jasmine.classificationPanel.minObjectSize = objectSize.getValue();
        jasmine.classificationPanel.minSubObjectSize = subObjectSize.getValue();
        updateLabels();
        jasmine.classificationPanel.repaint();
    }

    public void updateLabels() {
        lblObjectSize.setText(String.valueOf(objectSize.getValue()));
        lblSubObjectSize.setText(String.valueOf(subObjectSize.getValue()));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            setVisible(false);
            jasmine.project.addProperty(VisionSystem.OBJECT_SIZE_HANDLE, objectSize.getValue());
            jasmine.project.addProperty(VisionSystem.SUB_OBJECT_SIZE_HANDLE, subObjectSize.getValue());
        }
        if (e.getSource() == close) {
            setVisible(false);
            currentInstance = null;
            dispose();

        }
    }


}