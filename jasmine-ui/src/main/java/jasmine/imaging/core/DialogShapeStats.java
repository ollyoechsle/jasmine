package jasmine.imaging.core;


import jasmine.gp.problems.DataStack;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.commons.util.ScalingImagePanel;
import jasmine.imaging.shapes.ExtraShapeData;
import jasmine.imaging.shapes.RadiusChart;
import jasmine.imaging.shapes.SegmentedObject;
import jasmine.imaging.shapes.SegmentedShape;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
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
public class DialogShapeStats extends JDialog {

    Vector<Terminal> objectTerminals;
    Vector<Terminal> subObjectTerminals;
    Vector<Terminal> terminals;

    ScalingImagePanel image;
    JLabel classID;

    RadiusChart chart;

    Jasmine j;

    MyTableModel model;

    Object obj;

    public DialogShapeStats(Jasmine j) {
        super(j);

        this.j = j;

        objectTerminals = JasmineUtils.getTerminalsForObjects(j.project);
        subObjectTerminals = JasmineUtils.getTerminalsForSubObjects(j.project);

        classID = new JLabel("No Object" );
        image = new ScalingImagePanel();
        image.setPreferredSize(new Dimension(100,100));
        image.setMaximumSize(new Dimension(100,100));
        image.setMinimumSize(new Dimension(100,100));
        image.setBlackBackground(false);

        setTitle("Object Information");

        JPanel top = new JPanel(new BorderLayout(5,5));
        top.add(image, BorderLayout.WEST);
        top.add(classID, BorderLayout.CENTER);

        Container c = getContentPane();

        model = new MyTableModel();
        JTable t = new JTable(model);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onClose();
            }
        });

        //showGraph = new JCheckBox("Show Graph");
        //buttons.add(showGraph);
        buttons.add(close);

        c.add(top, BorderLayout.NORTH);
        c.add(new JScrollPane(t), BorderLayout.CENTER);
        c.add(buttons, BorderLayout.SOUTH);

        setSize(350, 350);
        setLocation(200, 200);
        setVisible(true);

    }

    public void hideStats() {
        obj = null;
        model.update();
    }

    Point chartLocation = null;

    public void displayStats(Object shape) {

        if (!isVisible()) return;

        if (shape == null) {
            obj = null;
            model.update();
            this.classID.setText("No Object");
            image.setImageNull();
            return;
        }

        ExtraShapeData esd;
        int classID = -1;
        String className = null;

        if (shape instanceof SegmentedShape) {
            esd = new ExtraShapeData((SegmentedShape) shape, j.getCurrentImage());
            classID = ((SegmentedShape) shape).classID;
            JasmineClass c = j.project.getSubObjectClass(classID);
            if (c != null) className = c.name;
            obj = esd;
            terminals = subObjectTerminals;
        } else {
            obj = shape;
            esd = ((SegmentedObject) shape).getOutlineData();
            classID = ((SegmentedObject) shape).getClassID();
            terminals = objectTerminals;
            JasmineClass c = j.project.getShapeClass(classID);
            if (c != null) className = c.name;
        }

        if (className == null) {
            this.classID.setText("Class: " + classID);
        } else {
            this.classID.setText("Class: " + className);
        }

        // so we can see the skeleton structure
        esd.skeletonise();
        image.setImage(esd.getImage(j.currentImage.getBufferedImage()));
        //image.setImage(esd.getImageDepth());


        /*Pixel cog = s.getCentreOfGravity();

        if (chart != null) {
            chartLocation = chart.getLocation();
            chart.dispose();
        }

        if (showGraph.isSelected()) {
            chart = new RadiusChart(s.getRadiuses());
            if (chartLocation != null) {
                chart.setLocation(chartLocation);
            }
        }*/

        model.update();

    }

    public void onClose() {
        j.menus.view_shape_stats.setSelected(false);
        if (chart != null) chart.setVisible(false);
        setVisible(false);
    }

    class MyTableModel extends AbstractTableModel {

        DecimalFormat f = new DecimalFormat("0.000");

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return terminals == null ? 0 : terminals.size();
        }

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Feature";
                case 1:
                    return "Value";
            }
            return "Unknown";
        }

        public Object getValueAt(int row, int col) {
            Terminal data = terminals.elementAt(row);
            switch (col) {
                case 0:
                    return data.getShortName();
                case 1:
                    DataStack ds = new DataStack();
                    JasmineUtils.setupDataStack(ds, obj);
                    return f.format(data.execute(ds));
            }
            return null;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public void update() {
            fireTableDataChanged();
        }
    }

}