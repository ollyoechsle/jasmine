package jasmine.imaging.core.util;


import jasmine.imaging.commons.PixelLoader;
import jasmine.imaging.commons.util.FileTree;
import jasmine.imaging.commons.util.ImageFilenameFilter;
import jasmine.imaging.commons.util.ImagePanel;
import jasmine.imaging.core.Jasmine;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.io.File;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
 * <p/>
 * //TODO: Need to be able to add multiple files.
 *
 * @author Panitnat Yimyam, University of Essex, Date: 10-July-2011
 * @version 1.0
 */
public class AddTestingImage extends JDialog implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ImagePanel p;
    protected JButton ok, done;
    protected Jasmine j;
    FileTree fileTree;

    public AddTestingImage(Jasmine j, File directory) {

        j.updateDefaultProjectLocation(directory);

        this.j = j;

        setTitle("Add Testing Image");

        try {
            setIconImage(new ImageIcon(getClass().getResource("/add.gif")).getImage());
        } catch (Exception e) {
        }


        p = new ImagePanel();
        p.setDisplayCentered(true);


        fileTree = new FileTree(directory, new ImageFilenameFilter()) {

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
             * Called when a file is selected in the tree
             */
            public void onSelectFile(File f) {
                TreePath[] paths = fileTree.getSelectionModel().getSelectionPaths();
                if (paths.length > 1) {
                    ok.setText("Add Images");
                } else {
                    ok.setText("Add Image");
                    currentTestingImage = f;
                    loadImage(f);
                }

            }

        };

        try {
            Icon customOpenIcon = new ImageIcon(getClass().getResource("/folder16.png"));
            Icon customClosedIcon = new ImageIcon(getClass().getResource("/folder16.png"));
            Icon customLeafIcon = new ImageIcon(getClass().getResource("/image16.png"));
            DefaultTreeCellRenderer renderer2 = new DefaultTreeCellRenderer();
            renderer2.setOpenIcon(customOpenIcon);
            renderer2.setClosedIcon(customClosedIcon);
            renderer2.setLeafIcon(customLeafIcon);
            fileTree.setCellRenderer(renderer2);
        } catch (Exception e) {
            //System.err.println("Could load load icon: " + icon);
        }

        JScrollPane scrollPane = new JScrollPane(fileTree);
        scrollPane.setPreferredSize(new Dimension(200, -1));

        JPanel main = new JPanel(new BorderLayout());
        main.add(new JScrollPane(p));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scrollPane, main);

        done = new JButton("Done");
        done.addActionListener(this);
        Dimension d = done.getPreferredSize();
        d.setSize(120, d.height);
        done.setPreferredSize(d);

        ok = new JButton("Add Image");
        ok.setEnabled(false);
        ok.addActionListener(this);
        ok.setPreferredSize(d);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(ok);
        buttons.add(done);

        Container c = getContentPane();
        c.add(splitPane, BorderLayout.CENTER);
        c.add(buttons, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {

            TreePath[] paths = fileTree.getSelectionModel().getSelectionPaths();
            if (paths.length > 1) {
                File[] files = new File[paths.length];
                for (int i = 0; i < paths.length; i++) {
                    TreePath path = paths[i];
                    files[i] = new File(path.getPathComponent(0).toString(), (String) path.getPathComponent(1).toString());
                }
                j.addTestingImage(files);
                
            } else {
                j.addTestingImage(new File[]{currentTestingImage});
            }
        } else {
            dispose();
        }
    }

    protected File currentTestingImage;

    protected void loadImage(File f) {
        if (f != null && !f.isDirectory()) {
            try {
                p.setImage(new PixelLoader(f));
                ok.setEnabled(true);
            } catch (Exception err) {
                // don't worry
            }
        } else {
            p.setImageNull();
        }
    }


}
