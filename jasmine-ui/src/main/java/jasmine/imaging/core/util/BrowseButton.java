package jasmine.imaging.core.util;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 19-Mar-2009
 * Time: 11:51:11
 * To change this template use File | Settings | File Templates.
 */
public class BrowseButton extends JButton {

    static ImageIcon browseIcon;

    public BrowseButton() {

        try {
            if (browseIcon == null) {
                browseIcon = new ImageIcon(getClass().getResource("/open16.png"));
            }
            setIcon(browseIcon);
        } catch (Exception e) {
            //System.err.println("Could load load icon: " + icon);
        }

    }



}
