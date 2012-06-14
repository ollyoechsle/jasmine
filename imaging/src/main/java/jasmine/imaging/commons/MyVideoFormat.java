package jasmine.imaging.commons;

import javax.media.format.VideoFormat;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: Olly
* Date: 01-Oct-2007
* Time: 12:00:01
* To change this template use File | Settings | File Templates.
*/
class MyVideoFormat {
    public VideoFormat format;

    public MyVideoFormat(VideoFormat format) {
        this.format = format;
    }

    public String toString() {
        Dimension dim = format.getSize();
        return (format.getEncoding() + " [ " + dim.width + " x " + dim.height + " ]");
    }
}
