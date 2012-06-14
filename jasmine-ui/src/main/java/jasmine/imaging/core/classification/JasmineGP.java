package jasmine.imaging.core.classification;

import jasmine.gp.multiclass.BasicDRS;
import jasmine.gp.multiclass.BetterDRS;
import jasmine.gp.multiclass.EntropyThreshold;
import jasmine.gp.multiclass.PCM;
import jasmine.gp.multiclass.VarianceThreshold;
import jasmine.gp.nodes.*;
import jasmine.gp.nodes.ercs.*;
import jasmine.gp.nodes.imaging.object.*;
import jasmine.gp.nodes.imaging.parameterised.GenericHaarFeature;
import jasmine.gp.nodes.imaging.parameterised.GenericLineFeature;
import jasmine.gp.nodes.imaging.parameterised.GenericNxNFeature;
import jasmine.gp.nodes.imaging.parameterised.GenericPerimeterFeature;
import jasmine.gp.nodes.imaging.texture.Mean;
import jasmine.gp.nodes.imaging.texture.Range;
import jasmine.gp.nodes.imaging.texture.Variance;
import jasmine.gp.nodes.imaging.texture.object.*;
import jasmine.gp.nodes.logic.*;
import jasmine.gp.nodes.math.*;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.Problem;
import jasmine.gp.tree.Terminal;
import jasmine.imaging.core.segmentation.AdaptiveBinaryThreshold;
import jasmine.imaging.core.util.ObjectDescriptor;
import jasmine.imaging.core.util.ObjectDescriptorTerminal;
import jasmine.imaging.core.util.TerminalMetaData;
import jasmine.imaging.gp.nodes.AspectRatio;
import jasmine.imaging.gp.nodes.AverageDepth;
import jasmine.imaging.gp.nodes.AverageHollowSize;
import jasmine.imaging.gp.nodes.BalanceX;
import jasmine.imaging.gp.nodes.BalanceXEnds;
import jasmine.imaging.gp.nodes.BalanceXLeftVariance;
import jasmine.imaging.gp.nodes.BalanceXRightVariance;
import jasmine.imaging.gp.nodes.BalanceY;
import jasmine.imaging.gp.nodes.BalanceYEnds;
import jasmine.imaging.gp.nodes.BoundingArea;
import jasmine.imaging.gp.nodes.ClosestEndToCog;
import jasmine.imaging.gp.nodes.ClosestPixelToCog;
import jasmine.imaging.gp.nodes.Corners;
import jasmine.imaging.gp.nodes.CountHollows;
import jasmine.imaging.gp.nodes.Density;
import jasmine.imaging.gp.nodes.DistFromBottom;
import jasmine.imaging.gp.nodes.DistFromLeft;
import jasmine.imaging.gp.nodes.DistFromRight;
import jasmine.imaging.gp.nodes.DistFromTop;
import jasmine.imaging.gp.nodes.Ends;
import jasmine.imaging.gp.nodes.HorizontalSymmetry;
import jasmine.imaging.gp.nodes.IntensityAverage;
import jasmine.imaging.gp.nodes.IntensityStdDev;
import jasmine.imaging.gp.nodes.InverseHorizontalSymmetry;
import jasmine.imaging.gp.nodes.InverseVerticalSymmetry;
import jasmine.imaging.gp.nodes.IsCogOverHollow;
import jasmine.imaging.gp.nodes.Joints;
import jasmine.imaging.gp.nodes.MaxDepth;
import jasmine.imaging.gp.nodes.Rectangularity;
import jasmine.imaging.gp.nodes.Roughness;
import jasmine.imaging.gp.nodes.Roundness;
import jasmine.imaging.gp.nodes.ShapeIntensity;
import jasmine.imaging.gp.nodes.VerticalSymmetry;
import jasmine.imaging.shapes.SegmentedObject;

import java.util.Vector;

/**
 * Base class for object classification and sub object classification
 *
 * @author Olly Oechsle, University of Essex, Date: 18-Oct-2007
 * @version 1.0
 */
public abstract class JasmineGP extends Problem {

    public static int ISLAND_COUNT = 1;
    protected int drsType;
    protected int SLOT_COUNT;

    // extra data about the terminals
    protected Vector<TerminalMetaData> terminalMetaData = null;

    public JasmineGP(int drsType, int numClasses) {
        this.drsType = drsType;
        this.SLOT_COUNT = numClasses * 7;
    }

    public void setTerminalMetaData(Vector<TerminalMetaData> terminalMetaData) {
        this.terminalMetaData = terminalMetaData;
    }

    public PCM getPCM() {
        PCM pcm = null;
        switch (drsType) {
            case BetterDRS.TYPE:
                pcm = new BetterDRS(SLOT_COUNT);
                break;
            case BasicDRS.TYPE:
                pcm = new BasicDRS(SLOT_COUNT);
                break;
            case VarianceThreshold.TYPE:
                pcm = new VarianceThreshold();
                break;
            case EntropyThreshold.TYPE:
                pcm = new EntropyThreshold();
                break;
        }
        return pcm;
    }


    public void customiseParameters(GPParams params) {
        // do nothing   	
        params.setIslandCount(ISLAND_COUNT);
    }

    public void registerFunctions(GPParams params) {

        params.registerNode(new Add());
        params.registerNode(new Mul());
        params.registerNode(new Sub());
        params.registerNode(new Div());

        params.registerNode(new Mean());
        params.registerNode(new PercentDiff());

        params.registerNode(new Ln());
        params.registerNode(new Squared());
        params.registerNode(new Cubed());
        params.registerNode(new Sqrt());
        params.registerNode(new Exp());
        params.registerNode(new Hypot());

        params.registerNode(new Sin());
        params.registerNode(new Cos());
        params.registerNode(new Tan());

        params.registerNode(new Max());
        params.registerNode(new Min());

        params.registerNode(new More_FP());
        params.registerNode(new Less_FP());
        params.registerNode(new AND_FP());
        params.registerNode(new OR_FP());

        params.registerNode(new PercentageERC());
        params.registerNode(new CustomRangeERC(0, 255));
        params.registerNode(new CustomRangeERC(0, 25));

    }

    public static Vector<Terminal> getStandardTerminals(Vector<SegmentedObject> trainingData) {

        Vector<Terminal> featureList = new Vector<Terminal>();

        //POEY comment: colour functions (original functions created by Olly from segmentation)
        
        featureList.add(new NormalisedRedMean()); 
        featureList.add(new NormalisedRedStdDev()); 
 		featureList.add(new NormalisedGreenMean()); 
 		featureList.add(new NormalisedGreenStdDev());
		featureList.add(new NormalisedBlueMean());
		featureList.add(new NormalisedBlueStdDev());

		featureList.add(new C1C2C3Mean(0));
		featureList.add(new C1C2C3StdDev(0));
		featureList.add(new C1C2C3Mean(1));
		featureList.add(new C1C2C3StdDev(1));
        featureList.add(new C1C2C3Mean(2));
        featureList.add(new C1C2C3StdDev(2));
                       
        featureList.add(new L1L2L3Mean(0));
        featureList.add(new L1L2L3StdDev(0));
        featureList.add(new L1L2L3Mean(1));
        featureList.add(new L1L2L3StdDev(1));
        featureList.add(new L1L2L3Mean(2));
        featureList.add(new L1L2L3StdDev(2));
       
        featureList.add(new GreyValueMean());
        featureList.add(new GreyValueStdDev());
                      
        featureList.add(new HueMean());
        featureList.add(new HueStdDev());
        featureList.add(new SaturationMean());
		featureList.add(new SaturationStdDev());
        featureList.add(new LightnessMean());
		featureList.add(new LightnessStdDev());
                
        featureList.add(new RangeObject());
       	featureList.add(new VarianceObject());  
                  
        featureList.add(new AdaptiveBinaryThresholdObject());
                       
        // Texture	
        featureList.add(new GenericPerimeterFeatureMean());
        featureList.add(new GenericLineFeatureMean());
        //featureList.add(new GenericHaarFeatureMean()); //not yet
        //featureList.add(new GenericNxNFeatureMean());		//not yet

        
        
        //POEY comment: object functions
               
        // this applies to both objects and sub objects
        featureList.add(new Corners());
		featureList.add(new CountHollows());
		featureList.add(new BalanceX());
        featureList.add(new BalanceY());
        featureList.add(new Density());
        featureList.add(new AspectRatio());
        featureList.add(new Joints());
        featureList.add(new Ends());
        featureList.add(new Roundness());
        featureList.add(new Roughness());
        featureList.add(new ShapeIntensity());
        featureList.add(new BalanceXEnds());
        featureList.add(new BalanceXLeftVariance());
        featureList.add(new BalanceXRightVariance());
        featureList.add(new BalanceYEnds());
        featureList.add(new ClosestEndToCog());
        featureList.add(new ClosestPixelToCog());
        featureList.add(new HorizontalSymmetry());
        featureList.add(new VerticalSymmetry());
        featureList.add(new InverseHorizontalSymmetry());
        featureList.add(new InverseVerticalSymmetry());
        featureList.add(new BoundingArea());
        featureList.add(new Rectangularity());
        featureList.add(new IsCogOverHollow());
        featureList.add(new AverageHollowSize());
        featureList.add(new DistFromTop());
        featureList.add(new DistFromBottom());
        featureList.add(new DistFromLeft());
        featureList.add(new DistFromRight());
        featureList.add(new MaxDepth());
        featureList.add(new AverageDepth());

        //POEY comment: grey value
        featureList.add(new IntensityAverage());
        featureList.add(new IntensityStdDev());       
        

        
        if (trainingData != null) {

            // extra features - applies to objects only
            featureList.add(new ObjectDescriptorTerminal(1));
            featureList.add(new ObjectDescriptorTerminal(2));
            featureList.add(new ObjectDescriptorTerminal(3));
            featureList.add(new ObjectDescriptorTerminal(4));
            featureList.add(new ObjectDescriptorTerminal(5));
            featureList.add(new ObjectDescriptorTerminal(6));
            featureList.add(new ObjectDescriptorTerminal(7));

            Vector<Integer> classes = ObjectDescriptor.getAllSubObjectClassIDs(trainingData);
            for (int i = 0; i < classes.size(); i++) {
                Integer classID = classes.elementAt(i);
                featureList.add(new ObjectDescriptorTerminal(8, classID));
            }
        }
        
      //POEY 
        //texture          
        featureList.add(new GreyGLCM(0));        
        featureList.add(new GreyGLCM(1));
        featureList.add(new GreyGLCM(2));
        featureList.add(new GreyGLCM(3));
        featureList.add(new GreyGLCM(4));
        featureList.add(new GreyGLCM(5));
        featureList.add(new GreyGLCM(6));
        featureList.add(new GreyGLCM(7));
        featureList.add(new GreyGLCM(8));
   
        featureList.add(new HueGLCM(0));        
        featureList.add(new HueGLCM(1));
        featureList.add(new HueGLCM(2));
        featureList.add(new HueGLCM(3));
        featureList.add(new HueGLCM(4));
        featureList.add(new HueGLCM(5));
        featureList.add(new HueGLCM(6));
        featureList.add(new HueGLCM(7));
        featureList.add(new HueGLCM(8)); 
       
        featureList.add(new SaturationGLCM(0));        
        featureList.add(new SaturationGLCM(1));
        featureList.add(new SaturationGLCM(2));
        featureList.add(new SaturationGLCM(3));
        featureList.add(new SaturationGLCM(4));
        featureList.add(new SaturationGLCM(5));
        featureList.add(new SaturationGLCM(6));
        featureList.add(new SaturationGLCM(7));
        featureList.add(new SaturationGLCM(8)); 
        
        featureList.add(new LightnessGLCM(0));        
        featureList.add(new LightnessGLCM(1));
        featureList.add(new LightnessGLCM(2));
        featureList.add(new LightnessGLCM(3));
        featureList.add(new LightnessGLCM(4));
        featureList.add(new LightnessGLCM(5));
        featureList.add(new LightnessGLCM(6));
        featureList.add(new LightnessGLCM(7));
        featureList.add(new LightnessGLCM(8)); 
        
        featureList.add(new RedGLCM(0));        
        featureList.add(new RedGLCM(1));
        featureList.add(new RedGLCM(2));
        featureList.add(new RedGLCM(3));
        featureList.add(new RedGLCM(4));
        featureList.add(new RedGLCM(5));
        featureList.add(new RedGLCM(6));
        featureList.add(new RedGLCM(7));
        featureList.add(new RedGLCM(8));
          
        featureList.add(new GreenGLCM(0));        
        featureList.add(new GreenGLCM(1));
        featureList.add(new GreenGLCM(2));
        featureList.add(new GreenGLCM(3));
        featureList.add(new GreenGLCM(4));
        featureList.add(new GreenGLCM(5));
        featureList.add(new GreenGLCM(6));
        featureList.add(new GreenGLCM(7));
        featureList.add(new GreenGLCM(8));
      
        featureList.add(new BlueGLCM(0));        
        featureList.add(new BlueGLCM(1));
        featureList.add(new BlueGLCM(2));
        featureList.add(new BlueGLCM(3));
        featureList.add(new BlueGLCM(4));
        featureList.add(new BlueGLCM(5));
        featureList.add(new BlueGLCM(6));
        featureList.add(new BlueGLCM(7));
        featureList.add(new BlueGLCM(8));    
        
        featureList.add(new GreyGLRM(0));        
        featureList.add(new GreyGLRM(1));
        featureList.add(new GreyGLRM(2));
        featureList.add(new GreyGLRM(3));
        featureList.add(new GreyGLRM(4));
        featureList.add(new GreyGLRM(5));
        
        featureList.add(new HueGLRM(0));        
        featureList.add(new HueGLRM(1));
        featureList.add(new HueGLRM(2));
        featureList.add(new HueGLRM(3));
        featureList.add(new HueGLRM(4));
        featureList.add(new HueGLRM(5));
        
        featureList.add(new SaturationGLRM(0));        
        featureList.add(new SaturationGLRM(1));
        featureList.add(new SaturationGLRM(2));
        featureList.add(new SaturationGLRM(3));
        featureList.add(new SaturationGLRM(4));
        featureList.add(new SaturationGLRM(5));
        
        featureList.add(new LightnessGLRM(0));        
        featureList.add(new LightnessGLRM(1));
        featureList.add(new LightnessGLRM(2));
        featureList.add(new LightnessGLRM(3));
        featureList.add(new LightnessGLRM(4));
        featureList.add(new LightnessGLRM(5));
        
        featureList.add(new RedGLRM(0));        
        featureList.add(new RedGLRM(1));
        featureList.add(new RedGLRM(2));
        featureList.add(new RedGLRM(3));
        featureList.add(new RedGLRM(4));
        featureList.add(new RedGLRM(5));
        
        featureList.add(new GreenGLRM(0));        
        featureList.add(new GreenGLRM(1));
        featureList.add(new GreenGLRM(2));
        featureList.add(new GreenGLRM(3));
        featureList.add(new GreenGLRM(4));
        featureList.add(new GreenGLRM(5));
        
        featureList.add(new BlueGLRM(0));        
        featureList.add(new BlueGLRM(1));
        featureList.add(new BlueGLRM(2));
        featureList.add(new BlueGLRM(3));
        featureList.add(new BlueGLRM(4));
        featureList.add(new BlueGLRM(5));
     
        return featureList;

    }


}