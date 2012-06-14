package jasmine.gp.nodes.imaging.parameterised;


import jasmine.gp.params.NodeConstraints;
import jasmine.gp.problems.DataStack;
import jasmine.imaging.commons.HaarlikeFeatures;
import jasmine.imaging.commons.PixelLoader;

import java.util.Vector;

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
 * @author Olly Oechsle, University of Essex, Date: 22-Apr-2008
 * @version 1.0
 */
public class GenericHaarFeature extends ParameterisedTerminal {

    int MIN_D = -5;
    int MAX_D = 5;
    int MIN_SIZE = 1;
    int MAX_SIZE = 5;

    private int type;
    private int dx, dy, width, height;

    public GenericHaarFeature() {    	     	
        this(1,0,0,5,5);
    }

    public GenericHaarFeature(int type, int dx, int dy, int width, int height) {    	
        this.type = type;
        this.dx = dx;
        this.dy = dy;
        this.width = width;
        this.height = height;
    }

    public int[] getReturnTypes() {
        return new int[]{NodeConstraints.NUMBER, NodeConstraints.FLOATING_POINT_NUMBER, NodeConstraints.RANGE255, NodeConstraints.FEATURE};
    }

    public double execute(DataStack data) {
        PixelLoader image = data.getImage();

        int x = data.getX();
        int y = data.getY();

        switch (type) {
            case 1:
                data.value = image.getIntegralImage().getHaarlikeFeatures().getTwoRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT);
                break;
            case 2:
                data.value = image.getIntegralImage().getHaarlikeFeatures().getTwoRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.VERTICALLY_ADJACENT);
                break;
            case 3:
                data.value = image.getIntegralImage().getHaarlikeFeatures().getThreeRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT);
                break;
            case 4:
                data.value = image.getIntegralImage().getHaarlikeFeatures().getThreeRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.VERTICALLY_ADJACENT);
                break;
            case 5:
                data.value = image.getIntegralImage().getHaarlikeFeatures().getFourRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT);
                break;
            case 6:
                data.value = image.getIntegralImage().getHaarlikeFeatures().getFourRectangleFeature(x + dx, y + dy, width, height, HaarlikeFeatures.VERTICALLY_ADJACENT);
        }

        return debugger == null? data.value : debugger.record(data.value);

    }

    public String toJava() {
    	switch (type) {
            case 1:
                return "image.getIntegralImage().getHaarlikeFeatures().getTwoRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT)";
            case 2:
                return "image.getIntegralImage().getHaarlikeFeatures().getTwoRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeFeatures.VERTICALLY_ADJACENT)";
            case 3:
                return "image.getIntegralImage().getHaarlikeFeatures().getThreeRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT)";
            case 4:
                return "image.getIntegralImage().getHaarlikeFeatures().getThreeRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeFeatures.VERTICALLY_ADJACENT)";
            case 5:
                return "image.getIntegralImage().getHaarlikeFeatures().getFourRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeFeatures.HORIZONTALLY_ADJACENT)";
            case 6:
                return "image.getIntegralImage().getHaarlikeFeatures().getFourRectangleFeature(x + " + dx + ", y + " + dy + ", width, height, HaarlikeFeatures.VERTICALLY_ADJACENT)";
        }
        return "";
    }

    public String getShortName() {
    	//return "Perim";
    	
    	//POEY
        return "Haar";
    }

    public ParameterisedTerminal getRandom() {
        int dx = getRandom(MIN_D, MAX_D);
        int dy = getRandom(MIN_D, MAX_D);
        int width = getRandom(MIN_SIZE, MAX_SIZE);
        int height = getRandom(MIN_SIZE, MAX_SIZE);
        int type = getRandom(1,6);
        return new GenericHaarFeature(type, dx, dy, width, height);
    }

    public Vector<ParameterisedTerminal> getDefaults() {
        Vector<ParameterisedTerminal> defaults = new Vector<ParameterisedTerminal>();
        return defaults;
    }

    public Object[] getConstructorArgs() {
        return new Object[]{type, dx,dy,width,height};
    }

    public String toString() {
        return "Haar features";
    }


}