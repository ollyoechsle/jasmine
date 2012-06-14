package jasmine.imaging.core.util;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 20-Feb-2009
 * Time: 09:31:50
 * To change this template use File | Settings | File Templates.
 */
public class OKCancelBar extends JPanel {

    public OKCancelBar(JButton ok, JButton cancel) {
        Dimension size = cancel.getPreferredSize();
        ok.setPreferredSize(size);
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createHorizontalGlue());
        add(ok);
        add(Box.createHorizontalStrut(3));
        add(cancel);
    }

}
