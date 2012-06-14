package jasmine.imaging.core.util;


import jasmine.imaging.core.Jasmine;

import javax.swing.*;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class RecentProjectMenuItem extends JMenuItem implements ActionListener {

    private File f;
    private Jasmine j;

    public RecentProjectMenuItem(Jasmine j, File f) {
        super(f.getName());
        this.f = f;
        this.j = j;
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        j.openProject(f);
    }

}
