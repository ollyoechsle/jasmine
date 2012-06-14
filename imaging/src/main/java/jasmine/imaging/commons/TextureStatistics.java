package jasmine.imaging.commons;

import java.util.ArrayList;

//POEY
public class TextureStatistics {
	
	private float mean = 0.0f;
	private float variance = 0.0f;
	
	public float mean(float[][] glcm, ArrayList<Integer> colour){
		int level = 0;
		for (int x = 0; x < glcm.length; x++) {
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	level = colour.get(y)+1;
	            	mean += level*glcm[x][y];
				}
            }
        }
		return mean;
	}
	
	public float variance(float[][] glcm, ArrayList<Integer> colour){
		int level = 0;
		for (int x = 0; x < glcm.length; x++) {
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	level = colour.get(y)+1;
	            	variance += (level-mean)*(level-mean)*glcm[x][y];
				}
            }
        }
		return variance;
	}
	
	public float uniformity(float[][] glcm){
		float value = 0.0f;
		for (int x = 0; x < glcm.length; x++) {
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	value += glcm[x][y]*glcm[x][y];
				}
            }
        }
		return value;
	}
	
	public float entropy(float[][] glcm){
		float value = 0.0f;
		for (int x = 0; x < glcm.length; x++) {
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	value += glcm[x][y]*Math.log(glcm[x][y]);
				}
            }
        }
		return (-1)*value;
	}
	
	public float max(float[][] glcm){
		float max = 0;
		for (int x = 0; x < glcm.length; x++) {
			for (int y = 0; y < glcm[x].length; y++) {
            	if(glcm[x][y]>max)
            		max = glcm[x][y];
            }
        }
		return max;
	}
	
	public float correlation(float[][] glcm, ArrayList<Integer> colour){
		int levelx=0, levely=0;
		float value = 0.0f;
		for (int x = 0; x < glcm.length; x++) {
			levelx = colour.get(x)+1;
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	levely = colour.get(y)+1;
	            	value += (levelx-mean)*(levely-mean)*glcm[x][y]/(variance*variance);
				}
            }
        }
		return value;
	}
	
	public float homogeneity(float[][] glcm, ArrayList<Integer> colour){
		int levelx=0, levely=0;
		float value = 0.0f;
		for (int x = 0; x < glcm.length; x++) {
			levelx = colour.get(x)+1;
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	levely = colour.get(y)+1;
	            	value += glcm[x][y]/(1+((levelx-levely)*(levelx-levely)));
				}
            }
        }
		return value;
	}
	
	public float ineria(float[][] glcm, ArrayList<Integer> colour){
		int levelx=0, levely=0;
		float value = 0.0f;
		for (int x = 0; x < glcm.length; x++) {
			levelx = colour.get(x)+1;
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	levely = colour.get(y)+1;
	            	value += (float)glcm[x][y]*((levelx-levely)*(levelx-levely));
				}
            }
        }
		return value;
	}
	
	public float clusterShade(float[][] glcm, ArrayList<Integer> colour){
		int levelx=0, levely=0;
		float value = 0.0f;
		for (int x = 0; x < glcm.length; x++) {
			levelx = colour.get(x)+1;
			for (int y = 0; y < glcm[x].length; y++) {
				if(glcm[x][y]>0){
	            	levely = colour.get(y)+1;
	            	value += glcm[x][y]*Math.pow(levelx+levely-(2*mean), 3);
				}
            }
        }
		return value;
	}
	
	public float shortRun(int[][] glrm){
		float value = 0.0f;
		int sum = 0;
		for (int x = 0; x < glrm.length; x++) {
			for (int y = 0; y < glrm[x].length; y++) {
				if(glrm[x][y]>0){
                	sum += glrm[x][y];
                	value += (float)glrm[x][y]/((y+1)*(y+1));
				}
            }
        }
		return value/sum;
	}
	
	public float longRun(int[][] glrm){
		int value = 0;
		int sum = 0;
		for (int x = 0; x < glrm.length; x++) {
			for (int y = 0; y < glrm[x].length; y++) {
				if(glrm[x][y]>0){
                	sum += glrm[x][y];
                	value += ((y+1)*(y+1))*glrm[x][y];
				}
            }
        }
		return (float)value/sum;
	}
	
	public float greyLevelNonUniformity(int[][] glrm){
		int value;
		float[] line = new float[glrm.length];
		int sum = 0;
		
		for (int x = 0; x < glrm.length; x++) {
			for (int y = 0; y < glrm[x].length; y++) {
                	sum += glrm[x][y];
            }
        }
		
		for (int x = 0; x < glrm.length; x++) {
			value = 0;
			for (int y = 0; y < glrm[x].length; y++) {
            	value += glrm[x][y];
            }
			line[x] = (float)value*value/sum;
        }
		value = 0;
		for (int x = 0; x < glrm.length; x++) {
			value += line[x];
		}
		return value;
	}
	
	public float runLengthNonUniformity(int[][] glrm){
		int value;
		float[] line = new float[glrm[0].length];
		int sum = 0;
		
		for (int x = 0; x < glrm.length; x++) {
			for (int y = 0; y < glrm[x].length; y++) {
            	sum += glrm[x][y];
            }
        }
		
		for (int y = 0; y < glrm[0].length; y++) {
			value = 0;
			for (int x = 0; x < glrm.length; x++){
            	value += glrm[x][y];
            }
			line[y] = (float)value*value/sum;
        }
		value = 0;
		for (int y = 0; y < glrm[0].length; y++) {
			value += line[y];
		}
		return value;
	}
	
	public float runPercent(int[][] glrm){
		int value = 0;
		int sum = 0;
		for (int x = 0; x < glrm.length; x++) {
			for (int y = 0; y < glrm[x].length; y++) {
				if(glrm[x][y]>0){
                	sum += glrm[x][y];
                	value += (y+1)*glrm[x][y];
				}
            }
        }
		return (float)sum/value;
	}
	
	public float runEntropy(int[][] glrm){
		float value = 0.0f;
		int sum = 0;
		for (int x = 0; x < glrm.length; x++) {
			for (int y = 0; y < glrm[x].length; y++) {
				if(glrm[x][y]>0){
                	sum += glrm[x][y];
                	value += glrm[x][y]*Math.log(glrm[x][y]);
				}
            }
        }
		return value/sum;
	}

}
