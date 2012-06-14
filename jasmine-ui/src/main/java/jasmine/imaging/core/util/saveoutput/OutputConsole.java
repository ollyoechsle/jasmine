package jasmine.imaging.core.util.saveoutput;


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Olly
 * Date: 14-Sep-2007
 * Time: 10:43:40
 * To change this template use File | Settings | File Templates.
 */
public class OutputConsole extends JDialog {

    JTextArea area;

    public OutputConsole(JFrame owner) throws IOException {

        super(owner);
        setTitle("Console Output");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        area = new JTextArea(SaveOutput.getOutput());
        add(new JScrollPane(area));

        setSize(400, 200);
        setVisible(true);

    }

    public void refresh() {
        try {
            area.setText(SaveOutput.getOutput());
        } catch (Exception e) {
            area.setText(e.getMessage());
        }
    }

}
