package jasmine.imaging.core;


import jasmine.imaging.core.Jasmine;
import jasmine.imaging.core.JasmineTestingImage;
import jasmine.imaging.core.util.IconLabel;
import jasmine.imaging.core.util.ToolButton;
import jasmine.imaging.shapes.SegmentedObject;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * @author Panitnat Yimyam, University of Essex, Date: 09-July-2010
 * @version 1.0
 */
public class JasmineTestingImageBrowser extends JDialog implements ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 1L;

	protected JList list;

    protected MyListModel data;

    protected Jasmine j;

    protected ToolButton add, delete;//, capture, open;

    public JasmineTestingImageBrowser(final Jasmine j) {

        super(j);

        this.j = j;

        try {
            setIconImage(new ImageIcon(getClass().getResource("/image16.png")).getImage());
        } catch (Exception e) {
        }

        JToolBar bar = new JToolBar();
        add = new ToolButton(this, "Add Testing Image from File", "add_image.png");
        //capture = new ToolButton(this, "Capture from webcam", "webcam_add.png");
        delete = new ToolButton(this, "Delete Selected Images", "delete_image.png");
        //open = new ToolButton(this, "Open the image folder in Explorer/Finder", "image_folder.png");
        bar.add(add);
        //bar.add(capture);
        bar.add(delete);
        //bar.add(open);

        setTitle("Testing Image Browser");
        setSize(320, 200);
        Container c = getContentPane();
        data = new MyListModel();

        list = new JList(data);
        JScrollPane scrollPane = new JScrollPane(list);
        list.setCellRenderer(new CustomCellRenderer());

        c.add(bar, BorderLayout.NORTH);
        c.add(scrollPane, BorderLayout.CENTER);

        list.addListSelectionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                j.menus.view_testing_image_browser.setSelected(false);
                setVisible(false);
            }
        });
    }

    class CustomCellRenderer extends IconLabel implements ListCellRenderer {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        	JasmineTestingImage image = (JasmineTestingImage) value;
/*
            if (j.segmentationPanel.mode == JasmineClass.MATERIAL && image.materialOverlayFilename != null) {
                showIcon = true;
            } else {
                if (j.segmentationPanel.mode == JasmineClass.MASK && image.maskOverlayFilename != null) {
                    showIcon = true;
                } else {
                    showIcon = false;
                }
            }*/

            Vector<SegmentedObject> objects = image.getObjects();
            if (objects.size() == 0) {
                setText(image.filenameTesting);
            } else {
                int objectCount = 0;
                int subObjectCount = 0;
                for (int i = 0; i < objects.size(); i++) {
                    SegmentedObject segmentedObject = objects.elementAt(i);
                    if (segmentedObject.getClassID() != -1) objectCount++;
                    subObjectCount += segmentedObject.countLabelledSubObjects();
                }
                if (objectCount == 0 && subObjectCount == 0) {
                    setText(image.filenameTesting);
                } else {
                    if (subObjectCount == 0) {
                        setText(image.filenameTesting + " [" + objectCount + "]");
                    } else {
                        setText(image.filenameTesting + " [" + objectCount + "] [" + subObjectCount + "]");
                    }
                }
            }

            this.selected = isSelected;
            
            if (isSelected) {
                setBackground(SystemColor.textHighlight);
                setForeground(SystemColor.textHighlightText);
            } else {
                setBackground(Color.WHITE);
                setForeground(SystemColor.textText);
            }

            return this;

        }

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == add) {
            j.addTestingImage();
        }

/*        if (e.getSource() == capture) {
            j.captureFromWebcam();
        }
*/
        if (e.getSource() == delete) {
            deleteSelected();
        }
/*
        if (e.getSource() == open) {
            try {
                if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
                    // open in explorer
                    Runtime.getRuntime().exec("explorer.exe " + j.project.getTestingImageLocation().getAbsolutePath());
                } else {
                    // for mac
                    Runtime.getRuntime().exec("open " + j.project.getTestingImageLocation().getAbsolutePath());
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }*/

    }

    public void deleteSelected() {
        Object[] selected = list.getSelectedValues();
        j.deleteTestingImage(selected);
    }

    class MyListModel extends AbstractListModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		//POEY
		public int getSize() {
            return j.project == null ? 0 : j.project.getTestingImages().size();
        }

        public Object getElementAt(int index) {
            if (index < 0 || index >= j.project.getTestingImages().size()) return null;
            return j.project.getTestingImages().elementAt(index);
        }
       
        //POEY
        public void updateTesting() {
            fireIntervalRemoved(this, 0, getSize());
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {

            if (list.getSelectedIndex() >= 0) {

            	JasmineTestingImage image = (JasmineTestingImage) list.getSelectedValue();

                if (image != j.currentTestingImage) {

                    //if (j.ensureOverlaySavedOK()) {

                        j.project.setCursorTesting(list.getSelectedIndex());

                        j.loadJasmineTestingImage(j.project.currentTestingImage());

                        j.menus.enableMenus();

                    //}
                }
            }
        }
    }
    
    //POEY
    public void refresh() {
        data.updateTesting();
        showSelected();
    }

    public void showSelected() {

        if (j.project != null) {

            int index = 0;

            for (int i = 0; i < j.project.getTestingImages().size(); i++) {
            	JasmineTestingImage jasmineImage = j.project.getTestingImages().elementAt(i);
                if (jasmineImage == j.currentTestingImage) {
                    index = i;
                    break;
                }
            }

            list.ensureIndexIsVisible(index);
            list.setSelectedIndex(index);

        }

    }
/*	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}*/

}