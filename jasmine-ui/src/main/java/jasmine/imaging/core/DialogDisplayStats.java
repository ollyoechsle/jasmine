package jasmine.imaging.core;


import jasmine.classify.GPProgressBar;
import jasmine.imaging.core.util.ClassLabel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;
import java.util.Vector;

public class DialogDisplayStats extends JDialog {

    private Container contentPane;

    private JasmineProject project;

    int mode;

    Jasmine j;

    public DialogDisplayStats(Jasmine jasmine, JasmineProject project, int mode) {

        super(jasmine);

        this.j = jasmine;

        this.mode = mode;

        try {
            setIconImage(new ImageIcon(getClass().getResource("/statistics16.png")).getImage());
        } catch (Exception e) {
        }

        init();

        setTitle(JasmineClass.getTypeName(mode) + " Statistics");

        this.project = project;
        createForm(mode);

        setVisible(true);

    }

    JButton buttonRefresh;

    public void init() {
        JButton buttonCancel = new JButton("Close");
        buttonRefresh = new JButton("Refresh");

        contentPane = getContentPane();
        getRootPane().setDefaultButton(buttonCancel);

        contentPane.setLayout(new BorderLayout());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        bottom.add(buttonRefresh);
        bottom.add(buttonCancel);

        contentPane.add(bottom, BorderLayout.SOUTH);

        buttonRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

    }


    protected JPanel displayPane;
    protected Vector<GPProgressBar> pixelCounts;
    protected Vector<JLabel> imageCounts;
    protected JLabel lblinstanceTotal;
    protected JLabel lblimageTotal;
    JTabbedPane tabs;


    public void createForm(int mode) {
        try {



            Vector<JasmineClassStatistics> stats = null;
            switch (mode) {
                case JasmineClass.MASK:
                    stats = project.getMaskStatistics();
                    break;
                case JasmineClass.MATERIAL:
                    stats = project.getMaterialStatistics();
                    break;
                case JasmineClass.OBJECT:
                    stats = project.getObjectStatistics();
                    break;
                case JasmineClass.SUB_OBJECT:
                    stats = project.getSubObjectStatistics();
                    break;
            }

            int height = 120 + (stats.size() * 20);
            if (displayPane == null) {
                setSize(420, Math.min(height, 600));
                Point p = j.getLocation();
                int x = (int) p.getX() + j.getWidth() - getWidth();
                int y = (int) p.getY() + j.gap + j.getHeight();
                setLocation(x, y);
            }

            if (displayPane == null) {
                displayPane = new JPanel(new GridLayout(stats.size() + 2, 3));
            } else {
                displayPane.removeAll();
            }

            pixelCounts = new Vector<GPProgressBar>();
            imageCounts = new Vector<JLabel>();

            displayPane.add(new JLabel("Class"));
            displayPane.add(new JLabel("Instances"));
            displayPane.add(new JLabel("Images"));

            for (int i = 0; i < stats.size(); i++) {
                JasmineClassStatistics stat = stats.elementAt(i);

                ClassLabel label = new ClassLabel(stat.c.name);
                label.setClassColour(stat.c.color);

                GPProgressBar b = new GPProgressBar(20, 120, 200);
                pixelCounts.add(b);
                JLabel ic = new JLabel();
                imageCounts.add(ic);

                displayPane.add(label);
                displayPane.add(b);
                displayPane.add(ic);

            }

            displayPane.add(new JLabel("<html><b>Total</b></html>"));
            lblinstanceTotal = new JLabel();
            lblimageTotal = new JLabel();
            displayPane.add(lblinstanceTotal);
            displayPane.add(lblimageTotal);

            if (tabs == null) {
                tabs = new JTabbedPane();
                tabs.add(JasmineClass.getTypeName(mode) + " Statistics", displayPane);
                contentPane.add(tabs, BorderLayout.CENTER);
            }

            refresh(stats);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "IO Exception caught while getting stas. " + e.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Project has been reset. Please close and reopen the window");
        }
    }

    public void refresh() {
        if (!buttonRefresh.isEnabled()) return;
        try {
            buttonRefresh.setEnabled(false);
            Vector<JasmineClassStatistics> stats = null;
            switch (mode) {
                case JasmineClass.MASK:
                    stats = project.getMaskStatistics();
                    break;
                case JasmineClass.MATERIAL:
                    stats = project.getMaterialStatistics();
                    break;
                case JasmineClass.OBJECT:
                    stats = project.getObjectStatistics();
                    break;
                case JasmineClass.SUB_OBJECT:
                    stats = project.getSubObjectStatistics();
                    break;
            }
            refresh(stats);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buttonRefresh.setEnabled(true);
        }
    }

    public void refresh(Vector<JasmineClassStatistics> stats) {

        setTitle(JasmineClass.getTypeName(mode) + " Statistics");

        if (stats.size() != pixelCounts.size()) {
            System.out.println("Classes changed, recreating form");
            contentPane.remove(tabs);
            createForm(mode);
        } else {
            System.out.println("Class count unchanged, leaving form alone");
        }

        int instanceTotal = 0;
        int highest = 0;
        int imageTotal = 0;

        for (int i = 0; i < stats.size(); i++) {
            JasmineClassStatistics stat = stats.elementAt(i);
            if (stat.instances > highest) {
                highest = stat.instances;
            }
        }

        for (int i = 0; i < stats.size(); i++) {
            JasmineClassStatistics stat = stats.elementAt(i);

            ClassLabel label = new ClassLabel(stat.c.name);
            label.setClassColour(stat.c.color);

            GPProgressBar b = pixelCounts.elementAt(i);
            JLabel ic = imageCounts.elementAt(i);
            if (stat.instances > 0) {
                b.setValue(stat.instances / (double) highest);
                b.setText(String.valueOf(stat.instances));
                ic.setText("   " + stat.images);
            } else {
                b.setValue(0);
                b.setText("No instances");
                ic.setText("   0");
            }

            instanceTotal += stat.instances;
            imageTotal += stat.images;

        }

        lblinstanceTotal.setText(String.valueOf(instanceTotal));
        lblimageTotal.setText("   " + String.valueOf(imageTotal));

    }


    private void onCancel() {
        dispose();
    }


}
