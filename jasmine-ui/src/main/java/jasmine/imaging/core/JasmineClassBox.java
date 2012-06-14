package jasmine.imaging.core;


import jasmine.imaging.core.util.ClassLabel;
import jasmine.imaging.core.util.ToolButton;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
 *
 * @author Olly Oechsle, University of Essex, Date: 30-May-2007
 * @version 1.0
 */
public class JasmineClassBox extends JDialog implements ListSelectionListener, ActionListener {

        protected JasmineClass currentClass;
        protected ClassListModel model;
        protected JList list;
        protected JTextField searchBox;

        protected ToolButton add, edit, delete;

        protected Jasmine j;

        public JasmineClassBox(final Jasmine j) {

            super(j);

            this.j = j;

            try {
                setIconImage(new ImageIcon(getClass().getResource("/class16.png")).getImage());
            } catch (Exception e) {
                
            }

            setTitle("Classes");

            JToolBar bar = new JToolBar();
            bar.setLayout(new BoxLayout(bar, BoxLayout.LINE_AXIS));
            add = new ToolButton(this, "Add Class", "add_class.png");
            edit = new ToolButton(this, "Edit Class", "edit_class.png");
            delete = new ToolButton(this, "Delete Class", "delete_class.png");
            bar.add(add);
            bar.add(edit);
            bar.add(delete);

            searchBox = new JTextField();
            searchBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     searchFor(searchBox.getText());
                }
            });

            bar.addSeparator();
            bar.add(searchBox);
            bar.add(Box.createHorizontalGlue());

            Container c = getContentPane();
            model = new ClassListModel(null);
            list = new JList(model);
            list.addListSelectionListener(this);
            list.setCellRenderer(new CustomCellRenderer());


            JScrollPane pane = new JScrollPane(list);

            c.add(bar, BorderLayout.NORTH);
            c.add(pane, BorderLayout.CENTER);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    j.menus.view_classbox.setSelected(false);
                    setVisible(false);
                }
            });

        }

        public void appendCharAndSearch(String c) {
            searchBox.setText(searchBox.getText() + c);
            if (model.classes != null) searchFor(searchBox.getText());
        }

        public JasmineClass getCurrentClass() {
            if (currentClass == null) {
                if (model.classes != null && model.classes.size() > 0) {
                    currentClass = model.classes.elementAt(0);
                    list.setSelectedIndex(0);
                }
            }
            return currentClass;
        }

        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == add) {
                j.addClass();
            }
            
            if (e.getSource() == delete) {
                JasmineClass c = getCurrentClass();
                if (c != null) {
                    j.project.setChanged(true, "Deleted class: " + c.name);
                    j.project.removeClass(c);
                    model.classes.remove(c);
                    refresh();
                } else {
                    j.alert("Cannot delete class: none selected");
                }
            }

            if (e.getSource() == edit) {
                j.editClass();
            }

        }

        public void next() {
            int index = list.getSelectedIndex();
            if (index == -1) {
                list.setSelectedIndex(0);
            } else {
                int newIndex = index + 1;
                if (newIndex >= model.classes.size()) {
                    newIndex = 0;
                }
                list.setSelectedIndex(newIndex);
            }

        }

        class ClassListModel extends AbstractListModel {

            Vector<JasmineClass> classes;

            public ClassListModel(Vector<JasmineClass> classes) {
                this.classes = classes;
                if (classes != null) {
                    System.out.println("List index: " +  list.getSelectedIndex());
                }
            }

            public int getSize() {
                return classes == null || classes.size() == 0 ? 1 : classes.size();
            }

            public Object getElementAt(int index) {
                if (classes == null || classes.size() == 0) {
                    return "No Classes";
                }
                if (index < classes.size()) {
                    return classes.elementAt(index);
                } else {
                    return null;
                }
            }

            public void update() {
                fireIntervalRemoved(this, 0, getSize());
            }

        }

        public void searchFor(String className) {
            if (className.length() == 0) return;
            int index = 0;
            int matches = 0;
            for (int i = 0; i < model.classes.size(); i++) {
                JasmineClass jasmineClass = model.classes.elementAt(i);
                if (jasmineClass.name.toLowerCase().startsWith(className)) {
                    index = i;
                    matches++;
                }
            }
            if (matches == 1) {
                list.ensureIndexIsVisible(index);
                list.setSelectedIndex(index);
                searchBox.setText("");
            }
        }


        class CustomCellRenderer extends ClassLabel implements ListCellRenderer {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                setText(value.toString());

                if (value instanceof JasmineClass) {
                    c = ((JasmineClass) value).color;
                } else {
                    c = null;
                }

                this.selected = isSelected;

                return this;

            }

        }

        public void init(Vector<JasmineClass> classes, boolean unselect) {

            setTitle(JasmineClass.getTypeName(j.getClassMode()) + " Classes");

            if (classes != null) {
                model.classes = classes;
                model.update();
                if (classes.size() == 1) {
                    list.setSelectedIndex(0);
                }
            }

            if (unselect) {
                currentClass = null;
            } else {
                for (int i = 0; i < classes.size(); i++) {
                    JasmineClass jasmineClass = classes.elementAt(i);
                    if (jasmineClass.equals(currentClass)) {
                        list.setSelectedIndex(i);
                        list.ensureIndexIsVisible(i);
                        break;
                    }
                }
            }
        }

        public void refreshThenshowSelectedClass() {
          if (j.project != null) {
                init(j.project.getClasses(j.getClassMode()), false);
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                if (list.getSelectedIndex() >= 0) {
                    if (list.getSelectedValue() instanceof JasmineClass) {
                        currentClass = (JasmineClass) list.getSelectedValue();
                        searchBox.setText("");
                    }
                }
            }
        }

        public void refresh() {
            if (j.project != null) {
                init(j.project.getClasses(j.getClassMode()), true);
                //init(j.mode == Jasmine.PIXEL_SEGMENTATION ? j.project.getMaterialClasses() : j.project.getObjectClasses());
            }
        }

    }