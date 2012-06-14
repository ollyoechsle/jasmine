package jasmine.imaging.core;


import jasmine.imaging.commons.GreyWorldAssumptionTransform;
import jasmine.imaging.commons.PixelLoader;

import java.io.File;

public class JasmineImagePreprocessor {

//    public static void main(String[] args) {
//        String filename = "hello.jpg.OVERLAY";
//        filename = filename.replaceAll("hello.jpg", "hello.png");
//        System.out.println(filename);
//    }

    boolean colourConstancy = true;

    public JasmineImagePreprocessor(JasmineProject project) {

    }

    public void restore(File imageFile) throws Exception {

        // before doing anything see if there is an original file
        File original = new File(imageFile.getParentFile(), imageFile.getName() + ".ORIGINAL");

        if (original.exists()) {
            new PixelLoader(original).saveAs(imageFile);
        }

    }

    public File ensureIsLossless(JasmineImage image, File imageFile) throws Exception {
        if (imageFile.getName().toLowerCase().endsWith(".jpg")) {
            PixelLoader pl = new PixelLoader(imageFile);
            String filename = imageFile.getName();
            filename = filename.substring(0, filename.indexOf(".")) + ".png";
            File f = new File(imageFile.getParentFile(), filename);            
            if (image.materialOverlayFilename != null) {
                // update the overlay
                String newOverlayFilename = image.materialOverlayFilename.replaceAll(imageFile.getName(), filename);
                OverlayData.renameMaterialOverlay(image, newOverlayFilename);
                newOverlayFilename = image.maskOverlayFilename.replaceAll(imageFile.getName(), filename);
                OverlayData.renameMaskOverlay(image, newOverlayFilename);
            }
            image.filename = filename;
            image.project.setChanged(true, "Converted image to lossless format: " + filename);
            pl.saveAs(f);
            return f;
        }
        return imageFile;
    }

    public void process(Jasmine j, JasmineImage image, File imageFile) throws Exception {

        imageFile = ensureIsLossless(image, imageFile);

        // before doing anything see if there is an original file
        File original = new File(imageFile.getParentFile(), imageFile.getName() + ".ORIGINAL");
        PixelLoader originalPL;

        if (!original.exists()) {
            originalPL = new PixelLoader(imageFile);
            originalPL.saveAs(original);
        } else {
            originalPL = new PixelLoader(original);
        }

        if (colourConstancy) {
            PixelLoader img = new GreyWorldAssumptionTransform().transform(originalPL);
            img.saveAs(imageFile);
        }

        j.imageBrowser.refresh();

    }

}
