package jasmine.imaging.commons;

//POEY
//to calculate statistic of grey data

public class FastStatisticsGrey {
	private int maxN = Integer.MIN_VALUE;
	private int minN = Integer.MAX_VALUE;
	private int maxNIndex;
	private int minNIndex;
	private int n = 0;
	private int[] data = new int[256];
	
  public final void addData(int x) {
  	data[x]++;
      if (data[x] > maxN){
      	maxN = data[x];
      	maxNIndex = x;
      }
      if (data[x] < minN){
      	minN = data[x];
      	minNIndex = x;
      }
      n++;
  }
	
  public int getN() {
      return n;
  }
  
  //return an index of array data which has max frequency
  public int getMaxNIndex() {
      return maxNIndex;
  }

}