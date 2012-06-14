package jasmine.gp.interfaces;


import jasmine.imaging.commons.util.ImagePanel;
import jasmine.imaging.commons.util.ScalingImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
     * Displays a live preview of segmenters effect on a particular image.
 */
class ImageWindow extends JDialog {

    ImagePanel image;
    JLabel lblMessage;
    private GraphicalListener graphicalListener;
    boolean setSize = true;
    JButton refresh;

    //POEY comment: a window for showing a gained segmentation result image
    public ImageWindow(final GraphicalListener graphicalListener) {
        super(graphicalListener.window);
        this.graphicalListener = graphicalListener;
        setTitle("Live Results");
        image = new ScalingImagePanel();
        refresh = new JButton("Refresh");
        lblMessage = new JLabel();
        JPanel bottom = new JPanel(new GridLayout(1, 2));
        bottom.add(lblMessage);
        bottom.add(refresh);

        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refreshing");               
                graphicalListener.displayOutput(graphicalListener.currentGeneration, true);
            }
        });

        //JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        getContentPane().add(image, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
        setLocation((int) graphicalListener.window.getLocation().getX(), 50);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

    }

    public void setImage(BufferedImage img) {
        if (setSize) {
            setSize(img.getWidth(), img.getHeight() + 50);
            setSize = false;
        }
        //image.setIcon(new ImageIcon(img));
        image.setImage(img);
    }

    public void onClose() {
        graphicalListener.displayOutput = false;
        graphicalListener.window.mnuDisplayImage.setSelected(false);
        dispose();
    }

}
