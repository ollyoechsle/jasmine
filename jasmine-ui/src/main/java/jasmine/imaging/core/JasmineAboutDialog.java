package jasmine.imaging.core;


import jasmine.gp.Evolve;

import javax.swing.*;
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
 * @author Olly Oechsle, University of Essex, Date: 26-Jun-2007
 * @version 1.0
 */
public class JasmineAboutDialog extends JDialog {

    public JasmineAboutDialog(Jasmine j) {

        super(j);                

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        setTitle(Jasmine.APP_NAME);

        JLabel name = new JLabel("<html><b>Jasmine: Machine Vision System Builder</b><br>By Olly Oechsle, University of Essex<br>2006-2009<br><a href='http://vase.essex.ac.uk/software/jasmine'>vase.essex.ac.uk/software/jasmine</a<br><br>GP Toolkit: " + Evolve.APP_NAME + "<br>Jasmine Version: " + Jasmine.VERSION + "<br>Java Version: " + System.getProperty("java.class.version") + "</html>");

        JLabel icon = new JLabel("");

        try {
            icon.setIcon(new ImageIcon(getClass().getResource("/vase-icon128.png")));
        } catch (Exception e) {
        }

        c.add(icon, BorderLayout.WEST);
        c.add(name, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton close = new JButton("OK");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttons.add(close);

        c.add(buttons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(430, 220);
        setLocationRelativeTo(j);
        setResizable(false);
        setVisible(true);

    }

}
