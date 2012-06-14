package jasmine.imaging.commons;

import java.util.ArrayList;

//POEY
//no use
public class AccuracyStatistics {
/*	
	private int[] n;
	private int[] classID;
	
	public AccuracyStatistics(int numClass){
		n = new int[numClass];
		classID = new int[numClass];
	}
	
	public void addData(int i, int correct){
		//in case classID = -1
		if(i<0)	
			i = 0;
		n[i]++;
		classID[i] += correct;
	}

	public float getAccuracy(int i){
		if(getTotal(i)>0)
			return getCorrectSum(i)/getTotal(i)*100;
		else
			return -1;
	}
	
	public float getAverageAccuracy(){
		float correct = 0.0f;
		int num = 0;
		for(int i=0; i<classID.length; i++){
			if(getTotal(i)>0){
				correct += (float)getAccuracy(i);
				num++;
			}
		}
		return correct/num;
	}
	
	public int getTotal(int i){
		return n[i];
	}
	
	public int getCorrectSum(int i){
		return classID[i];
	}
*/	
	
	private int total;	
	private String name;
	private int correct;
	public ArrayList<AccuracyStatistics> classes;
	
	public AccuracyStatistics() {
        classes = new ArrayList<AccuracyStatistics>(10);
    }
	
	public void addData(String name)  {
		int check = 0;
		if(classes.size()>0){
			 for (int i = 0; i < classes.size(); i++) {
				 if(classes.get(i).name== name){
					 check = 1;					 
					 break;
				 }
			 }
			 if(check==0){
				 classes.add(new AccuracyStatistics(name));
			 }
		}
		else{
			 classes.add(new AccuracyStatistics(name));
		}
			
    }
	
	public AccuracyStatistics(String name) {
        this.name = name;
        this.correct = 0;
        this.total = 0;
    }
	
	public void addHit(String name) {
		AccuracyStatistics c = getClassResult(name);
        if (c != null) {
        	System.out.println("c != null");
            c.correct++;
            c.total++;           
        }
    }
	
	public void addMiss(String name) {
		AccuracyStatistics c = getClassResult(name);
        if (c != null) {
            c.total++;           
        }
    }
	
	public int getHit(String name) {
		AccuracyStatistics c = getClassResult(name);
        if (c != null) {
            return c.correct;          
        }
        return -1;
    }
	
	public int getTotal(String name) {
		AccuracyStatistics c = getClassResult(name);
        if (c != null) {
            return c.total;          
        }
        return -1;
    }
	
	public int getAccuracy(String name) {
		AccuracyStatistics c = getClassResult(name);
        if (c != null) {
            return c.correct/c.total*100;          
        }
        return -1;
    }
	
	public String getName() {
        return name;
    }
	
	
	public AccuracyStatistics getClassResult(String name) {
        for (int i = 0; i < classes.size(); i++) {
        	AccuracyStatistics classResult = classes.get(i);
            if (classResult.name == name) return classResult;
        }
        return null;
    }
    
}
