package jasmine.imaging.core.util.saveoutput;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class SpecialOutputStream extends FileOutputStream {

    public SpecialOutputStream(File f, boolean append) throws IOException {
        super(f, append);
    }

    public void write(byte b[], int off, int len) throws IOException {
        super.write(b, off, len);    //To change body of overridden methods use File | Settings | File Templates.
        if (SaveOutput.outputWindow != null){
            SaveOutput.outputWindow.refresh();
        }
    }

}