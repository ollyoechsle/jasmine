package jasmine.gp.interfaces;


import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A menu item for each image in the training set
 * allows the user to choose which image is previewed.
 */
class ImageMenuItem extends JMenuItem {

    int i;

    public ImageMenuItem(final GraphicalListener graphicalListener, String name, int i) {

        super(name);

        this.i = i;

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graphicalListener.previousFitness = -1;
                graphicalListener.index = ImageMenuItem.this.i;
            }
        });

    }

}
