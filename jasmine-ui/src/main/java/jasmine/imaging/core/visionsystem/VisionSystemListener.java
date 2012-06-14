package jasmine.imaging.core.visionsystem;


import jasmine.imaging.shapes.SegmentedObject;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ooechs
 * Date: 22-Apr-2009
 * Time: 14:56:41
 * To change this template use File | Settings | File Templates.
 */
public interface VisionSystemListener {

    public void onStart();

    public void onSegmentationProgress(int progress);

    public void onFinishedSegmentation(Vector<SegmentedObject> objects);

    public void onFinished(Vector<SegmentedObject> objects);

}
