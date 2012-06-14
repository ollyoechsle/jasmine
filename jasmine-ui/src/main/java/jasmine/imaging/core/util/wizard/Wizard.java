package jasmine.imaging.core.util.wizard;


import jasmine.imaging.core.util.wizard.WizardPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.Vector;
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
 *
 * @author Olly Oechsle, University of Essex, Date: 26-Feb-2008
 * @version 1.0
 */
public abstract class Wizard implements ActionListener {

    Vector<WizardPanel> panels;

    JLabel title;

    JButton previous, next, finish, cancel;

    JPanel center;

    private int position = 0;

    JDialog wizard;

    JPanel sidePanel;

    public abstract String getTitle();

    public abstract Vector<WizardPanel> getPanels();

    //POEY comment: Menu: Evolve > Background Subtracter > Background Subtraction Wizard
    //Menu: Evolve > Object Classifier > Object Classification Wizard
    public void initialise(JFrame owner) {

        wizard = new JDialog(owner);

        try {
            wizard.setIconImage(new ImageIcon(getClass().getResource("/wizard24.png")).getImage());
        } catch (Exception e) {
        }

        this.panels = getPanels();

        sidePanel = new WizardSidePanel(this);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        sidePanel.setBackground(new Color(0, 90, 155));
        sidePanel.setPreferredSize(new Dimension(200, -1));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        title = new JLabel();

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        center = new JPanel(new CardLayout());
        for (int i = 0; i < panels.size(); i++) {
            WizardPanel wizardPanel = panels.elementAt(i);
            center.add(wizardPanel, wizardPanel.getTitle());
        }

        mainPanel.add(center, BorderLayout.CENTER);

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        Dimension d = cancel.getPreferredSize();

        finish = new JButton("Finish");
        finish.addActionListener(this);
        finish.setPreferredSize(d);

        previous = new JButton("Back");
        previous.addActionListener(this);
        previous.setPreferredSize(d);

        next = new JButton("Next");
        next.addActionListener(this);
        next.setPreferredSize(d);

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        cancel.setPreferredSize(d);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(previous);
        buttons.add(next);
        buttons.add(finish);
        buttons.add(cancel);

        position = getDefaultStartPosition();
        displayPanelAt(position);

        wizard.setLayout(new BorderLayout());
        wizard.add(sidePanel, BorderLayout.WEST);
        wizard.add(mainPanel, BorderLayout.CENTER);
        wizard.add(buttons, BorderLayout.SOUTH);
        
        wizard.setTitle(getTitle());
        wizard.setSize(750, 400);
        wizard.setLocationRelativeTo(null);
        wizard.setVisible(true);        

    }

    public int getDefaultStartPosition() {
        return 0;
    }

    WizardPanel currentPanel;

    public void dispose() {
        wizard.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previous) {
            if (currentPanel.isOK()) {
                position--;
                displayPanelAt(position);
            }
        }
        if (e.getSource() == next) {
            if (currentPanel.isOK()) {
                position++;
                displayPanelAt(position);
            }
        }
        if (e.getSource() == cancel) {
            wizard.dispose();
        }
        if (e.getSource() == finish) {
            if (currentPanel.isOK()) {
                onFinish();
            }
        }
    }

    public abstract void onFinish();

    public void displayPanelAt(int position) {
        previous.setEnabled(position != 0);
        next.setEnabled(position < panels.size() - 1);
        finish.setEnabled(!next.isEnabled());
        CardLayout cl = (CardLayout)(center.getLayout());
        currentPanel = panels.elementAt(position);        
        cl.show(center, currentPanel.getTitle());
        title.setText("<html><body><b>" + currentPanel.getTitle() + "</b><br>" + currentPanel.getDescription() + "</body></html>");
        sidePanel.repaint();
    }

    public void alert(String message) {
        JOptionPane.showMessageDialog(wizard, message);
    }

}
