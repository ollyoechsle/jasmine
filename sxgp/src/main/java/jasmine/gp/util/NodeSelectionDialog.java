package jasmine.gp.util;


import jasmine.gp.params.GPParams;
import jasmine.gp.params.NodeConstraints;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Allows you to choose which nodes should be used by the Genetic Programming
 * system. Appears as part of the GPStartDialog object.
 *
 * @see jasmine.gp.util.GPStartDialog
 * @author Olly Oechsle, University of Essex, Date: 26-Jun-2007
 * @version 1.0
 */
public class NodeSelectionDialog extends JDialog implements ActionListener {

    protected JButton ok;

    protected Vector<NodeConstraints> nodes;

    protected Vector<JCheckBox> checkboxes;

    public NodeSelectionDialog(JFrame owner, GPParams p) {

        super(owner);

        setTitle("Advanced Node Selection");

        nodes = p.getNodes();

        Container c = getContentPane();

        ok = new JButton("OK");
        ok.addActionListener(this);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        bottom.add(ok);

        c.add(new JScrollPane(new JTable(new MyTableModel())), BorderLayout.CENTER);
        c.add(bottom, BorderLayout.SOUTH);

        setSize(550, 300);
        setVisible(true);

    }


    public void actionPerformed(ActionEvent e) {

        dispose();

    }

    class MyTableModel extends AbstractTableModel {

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return nodes.size();
        }

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Type";
                case 1:
                    return "Feature";
                case 2:
                    return "Fitness";
                case 3:
                    return "Enabled";
            }
            return "Unknown";
        }

        public Object getValueAt(int row, int col) {
            NodeConstraints data = nodes.elementAt(row);
            switch (col) {
                case 0:
                    return NodeConstraints.typeNames[data.getType()];
                case 1:
                    return data.toString();
                case 2:
                    return data.getFitness();
                case 3:
                    return data.isEnabled();
            }
            return null;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
        * Don't need to implement this method unless your table's
        * editable.
        */
        public boolean isCellEditable(int row, int col) {
            // NOTE: New node selection thingy means that you can't change the
            // enabled status of node constraints at runtime. It will have no effect
            // as the node caches are made only once.
            return col == 2;
        }

        /*
        * Don't need to implement this method unless your table's
        * data can change.
        */
        public void setValueAt(Object value, int row, int col) {
            NodeConstraints node = nodes.elementAt(row);
            switch (col) {
                case 2:
                    node.setFitness((Double) value);
                    fireTableCellUpdated(row, col);
                    break;
                case 3:
                    node.setEnabled((Boolean) value);
                    fireTableCellUpdated(row, col);
                    break;
            }

        }

        public void update() {
            fireTableDataChanged();
        }

    }

}
