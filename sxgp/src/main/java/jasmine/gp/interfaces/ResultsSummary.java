package jasmine.gp.interfaces;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Gives a digest of the results of the milestones in the evolution process.
 */
class ResultsSummary extends JDialog {

    JTextArea area;
    MainWindow owner;

    public ResultsSummary(final MainWindow owner, Vector<GenerationResult> results) {

        super(owner);
        this.owner = owner;
        this.data = results;

        table = new JTable(new MyTableModel());
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton ok = new JButton("Close");

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResultsSummary.this.dispose();
                owner.mnuGpSummary.setSelected(false);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ResultsSummary.this.dispose();
            }
        });

        buttons.add(ok);

        getContentPane().add(buttons, BorderLayout.SOUTH);

        setTitle("GP Summary");
        setSize(360, 200);
        Point p = owner.getLocation();
        setLocation((int) p.getX(), -220 +(int) p.getY());
        setVisible(true);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                dispose();
            }

        });

    }


    private Vector<GenerationResult> data;
    private JTable table;


    public void refresh() {
        ((MyTableModel) table.getModel()).refresh();
    }

    class MyTableModel extends AbstractTableModel {

        private String[] columnNames = new String[]{"Time(mS)", "Generation", "Fitness", "Hits", "Size", "Action"};


        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            GenerationResult r = data.elementAt(row);
            switch (col) {
                case 0:
                    return r.time;
                case 1:
                    return r.generation;
                case 2:
                    if (r.individual != null) {
                        return r.individual.getKozaFitness();
                    } else {
                        return "NA";
                    }
                case 3:
                    if (r.individual != null) {
                        return r.individual.getHits();
                    } else {
                        return "NA";
                    }
                case 4:
                    if (r.individual != null) {
                        return r.individual.getTreeSize();
                    } else {
                        return "NA";
                    }
                case 5:
                    return "Save";
            }
            return "";
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public void refresh() {
            fireTableDataChanged();
        }

    }



    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                GenerationResult r = data.elementAt(row);
                owner.graphicalListener.saveIndividual(r.individual);
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }


}
