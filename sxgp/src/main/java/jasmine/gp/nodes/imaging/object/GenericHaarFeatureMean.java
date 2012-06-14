//package jasmine.gp.nodes.imaging.object;
//
//import jasmine.gp.nodes.imaging.parameterised.ParameterisedTerminal;
//import jasmine.gp.params.NodeConstraints;
//import jasmine.gp.problems.DataStack;
//import jasmine.imaging.commons.HaarlikeFeatures;
//import jasmine.imaging.commons.PixelLoader;
//import jasmine.imaging.shapes.ExtraShapeData;
//
//import java.util.Vector;
//
////POEY
//public class GenericHaarFeatureMean extends ParameterisedTerminal {
//
//    int MIN_D = -5;
//    int MAX_D = 5;
//    int MIN_SIZE = 1;
//    int MAX_SIZE = 5;
//
//    private int type;
//    private int dx, dy, width, height;
//
//    public GenericHaarFeatureMean() {    	     	
//        this(1,0,0,5,5);
//    }
//
//    public GenericHaarFeatureMean(int type, int dx, int dy, int width, int height) {    	
//        this.type = type;
//        this.dx = dx;
//        this.dy = dy;
//        this.width = width;
//        this.height = height;
//    }
//
//    public int[] getReturnTypes() {
//        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255};
//    }
//
//    public double execute(DataStack data) {
//        PixelLoader image = data.getImage();
//        ExtraShapeData img =  (ExtraShapeData)data.getData();
//
//        int x = data.getX();
//        int y = data.getY();
//
//        switch (type) {
//            case 1:
//                data.value = img.getIntegralImage(image, img).getHaarlikeFeatures().getTwoRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT); 
//                break;
//            case 2:
//                data.value = img.getIntegralImage(image, img).getHaarlikeFeatures().getTwoRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.VERTICALLY_ADJACENT);
//                break;
//            case 3:
//                data.value = img.getIntegralImage(image, img).getHaarlikeFeatures().getThreeRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT);
//                break;
//            case 4:
//                data.value = img.getIntegralImage(image, img).getHaarlikeFeatures().getThreeRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.VERTICALLY_ADJACENT);
//                break;
//            case 5:
//                data.value = img.getIntegralImage(image, img).getHaarlikeFeatures().getFourRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT);
//                break;
//            case 6:
//                data.value = img.getIntegralImage(image, img).getHaarlikeFeatures().getFourRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.VERTICALLY_ADJACENT);
//        }
//
//        return debugger == null? data.value : debugger.record(data.value);
//
//    }
//
//    public String toJava() {
//switch (type) {
//            case 1:
//                return "object.getIntegralImage().getHaarlikeFeaturesMean().getTwoRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeMeanFeatures.HORIZONTALLY_ADJACENT)";
//            case 2:
//                return "object.getIntegralImage().getHaarlikeFeaturesMean().getTwoRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeMeanFeatures.VERTICALLY_ADJACENT)";
//            case 3:
//                return "object.getIntegralImage().getHaarlikeFeaturesMean().getThreeRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeMeanFeatures.HORIZONTALLY_ADJACENT)";
//            case 4:
//                return "object.getIntegralImage().getHaarlikeFeaturesMean().getThreeRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeMeanFeatures.VERTICALLY_ADJACENT)";
//            case 5:
//                return "object.getIntegralImage().getHaarlikeFeaturesMean().getFourRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeMeanFeatures.HORIZONTALLY_ADJACENT)";
//            case 6:
//                return "object.getIntegralImage().getHaarlikeFeaturesMean().getFourRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeMeanFeatures.VERTICALLY_ADJACENT)";
//        }
//        return "";
//    }
//
//    public String getShortName() {
//        return "Perim";
//    }
//
//    public ParameterisedTerminal getRandom() {
//        int dx = getRandom(MIN_D, MAX_D);
//        int dy = getRandom(MIN_D, MAX_D);
//        int width = getRandom(MIN_SIZE, MAX_SIZE);
//        int height = getRandom(MIN_SIZE, MAX_SIZE);
//        int type = getRandom(1,6);
//        return new GenericHaarFeatureMean(type, dx, dy, width, height);
//    }
//
//    public Vector<ParameterisedTerminal> getDefaults() {
//        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
//        return defaults;
//    }
//
//    public Object[] getConstructorArgs() {
//        return new Object[]{type, dx,dy,width,height};
//    }
//
//    public String toString() {
//        return "Haar Mean Features";
//    }
//
//
//}
