package jasmine.imaging.core;


import jasmine.imaging.core.Importer;
import jasmine.imaging.core.util.OKCancelBar;
import jasmine.imaging.core.util.PropertyValue;

import javax.swing.*;
import java.awt.event.*;
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
 * @author Olly Oechsle, University of Essex, Date: 13-Feb-2007
 * @version 1.0
 */
public class DialogClassEntry extends JDialog implements ActionListener {

    JTextField id, name;
    JComboBox coloursList;
    JButton ok, cancel;
    JCheckBox background;
    private Jasmine jasmine;

    protected JasmineClass editClass;

    protected JColourPanel colourPreview;

    protected int type;


    public DialogClassEntry(Jasmine jasmine, JasmineClass editClass, int type) {
        super(jasmine);
        this.jasmine = jasmine;
        if (editClass == null) {
            setTitle("Add " + JasmineClass.getTypeName(type) + " Class");
        } else {
            setTitle("Edit " + JasmineClass.getTypeName(editClass.type) + " Class");
        }
        this.type = type;
        setModal(true);

        setIconImage(jasmine.getIconImage());

        coloursList = new JComboBox();
        coloursList.addItem("Gray");
        coloursList.addItem("Red");
        coloursList.addItem("Yellow");
        coloursList.addItem("Green");
        coloursList.addItem("Blue");
        coloursList.addItem("Light Blue");
        coloursList.addItem("Orange");
        coloursList.addItem("Purple");
        coloursList.addItem("Pink");
        coloursList.addItem("White");
        coloursList.addItem("Black");
        coloursList.addItem("Random");

        coloursList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                colourPreview.setColour(e.getItem().toString());
            }
        });

        colourPreview = new JColourPanel();
        colourPreview.setColour(coloursList.getItemAt(0).toString());

        id = new JTextField();
        name = new JTextField();
        background = new JCheckBox();

        if (editClass == null) {

            int nextID = jasmine.project.getNextClassID(type);

            id.setText(String.valueOf(nextID));

            if (nextID <= coloursList.getItemCount()) {
                coloursList.setSelectedIndex(nextID - 1);
            } else {
                // go to last in the list (random)
                coloursList.setSelectedIndex(coloursList.getItemCount() - 1);
            }

            if (nextID == 1) {
                name.setText("Background");
                background.setSelected(true);
            }

        }

        init(editClass);

        JPanel colourPanel = new JPanel();
        colourPanel.setLayout(new BoxLayout(colourPanel, BoxLayout.LINE_AXIS));
        colourPanel.add(coloursList);
        colourPanel.add(Box.createHorizontalStrut(5));
        colourPanel.add(colourPreview);
        colourPanel.add(Box.createHorizontalGlue());

/*        JPanel fields = new JPanel(new GridLayout(4, 1));
        fields.add(new JLabel("Class ID: "));
        fields.add(id);
        fields.add(new JLabel("Name:"));
        fields.add(name);
        fields.add(new JLabel("Colour:"));*/
        /*     fields.add(colourPanel);
        fields.add(new JLabel("Background:"));
        fields.add(background);*/

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.PAGE_AXIS));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Class ID", id));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Class Name", name));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Colour", colourPanel));
        fields.add(Box.createVerticalStrut(5));
        fields.add(new PropertyValue("Background", background));


        ok = new JButton("OK");
        cancel = new JButton("Cancel");
        ok.addActionListener(this);
        cancel.addActionListener(this);

        getContentPane().add(fields, BorderLayout.CENTER);
        getContentPane().add(new OKCancelBar(ok, cancel), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                name.requestFocus();
            }
        });

        JRootPane rootPane = this.getRootPane();
        InputMap iMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok");


        ActionMap aMap = rootPane.getActionMap();
        aMap.put("ok", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        setSize(360, 170);
        setLocationRelativeTo(jasmine);
        setResizable(false);
        setVisible(true);

    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.height, d.height);
    }

    class JColourPanel extends JPanel {

        Color c;

        public JColourPanel() {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    // go to last in the list (random)
                    coloursList.setSelectedIndex(coloursList.getItemCount() - 1);
                    setColour("random");
                }
            });
        }

        public void setColour(String colour) {
            setColour(Importer.getColor(colour));
        }

        public void setColour(Color c) {
            this.c = c;
            repaint();
        }


        public Color getColour() {
            return c;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 0;
            int y = 0;
            int width = getWidth();
            int height = getHeight();
            if (c != null) {
                g.setColor(c);
                g.fillRect(x, y, width, height);
                g.setColor(new Color(1, 1, 1, 16));
                g.fillOval(-width / 3, -height / 3, width * 2, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width - 1, height - 1);

/*                g.setColor(new Color(1,1,1, 128));
                // top
                g.drawLine(x, y, x + width, y);
                // left
                g.drawLine(x, y, x, y + height);
                g.setColor(new Color(0,0,0, 64));
                // bottom
                g.drawLine(x, y+height-1, x + width, y+height-1);
                // right
                g.drawLine(x+width-1, y, x+width-1, y + height);*/
            }
        }

    }

    public void init(JasmineClass editClass) {
        if (editClass != null) {

            this.editClass = editClass;

            id.setText(String.valueOf(editClass.classID));
            name.setText(editClass.name);
            try {
            coloursList.setSelectedIndex(editClass.classID - 1);
            } catch (Exception e) {}
            background.setSelected(editClass.background);
            colourPreview.setColour(editClass.color);

        }

    }

    public void onOK() {
        try {

            // create the colour
            Color color = colourPreview.getColour();
            if (color == null) {
                jasmine.alert("Cannot recognise this colour. Please choose another");
                return;
            } else {
                Vector<JasmineClass> existingClasses = jasmine.project.getClasses(type);
                for (int i = 0; i < existingClasses.size(); i++) {
                    JasmineClass jasmineClass = existingClasses.elementAt(i);
                    if (jasmineClass.color == color && (editClass == null || jasmineClass != editClass)) {
                        jasmine.alert("Cannot use this colour - in use");
                        return;
                    }
                }
            }

            int classID = Integer.parseInt(id.getText());

            if (!name.getText().trim().equals("")) {

                boolean success = false;

                if (editClass == null) {

                    // add new class
                    JasmineClass cl = new JasmineClass(classID, name.getText(), type, color, background.isSelected());

                    success = jasmine.project.addClass(cl);

                    //jasmine.mode == Jasmine.PIXEL_SEGMENTATION? jasmine.project.addPixelClass(cl) : jasmine.project.addShapeClass(cl);

                    if (!success) {
                        JOptionPane.showMessageDialog(this, "Cannot add class: this ID is already in use");
                    }

                    jasmine.classbox.currentClass = cl;

                } else {

                    // edit existing class

                    editClass.name = name.getText();
                    editClass.background = background.isSelected();
                    editClass.color = color;
                    jasmine.classbox.currentClass = editClass;
                    jasmine.project.setChanged(true, "Saved changes to class");

                    success = true;

                }

                if (success) {

                    jasmine.classbox.refreshThenshowSelectedClass();
                    setVisible(false);
                    dispose();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Please enter a name for the class");
            }
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric ID");
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == ok) {
            onOK();
        }

        if (e.getSource() == cancel) {
            setVisible(false);
            dispose();
        }

    }

}
