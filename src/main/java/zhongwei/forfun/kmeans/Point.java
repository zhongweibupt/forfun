/**
 * 
 */
package zhongwei.forfun.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* <p>Title: Point.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月8日
* @version 1.0
*/
public class Point {
	private List<Double> data;
	private int dimension;
	private String id;
	private String label;
	
	public Point(String id, List<Double> data) {
		this.id = id;
		this.data = new ArrayList<Double>(data);
		this.dimension = this.data.size();
		this.label = "";
	}
	
	public List<Double> getData() {
		return this.data;
	}
	
	public int getDimension() {
		return this.dimension;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void labelIt(String label) {
		this.label = label;
	}
	
	public Cluster chooseCluster(List<Cluster> clusters, Distance distance) {
		double minDist = Double.MAX_VALUE;
		Cluster best = null; 
		for(Cluster cluster : clusters) {
			double tmp = distance.getDistance(cluster.getCenter(), this);
			if(tmp < minDist) {
				best = cluster;
				minDist = tmp;
			}
		}
		
		return best;
	}
	
	@Override  
    public boolean equals(Object o) {  
        if(this == o) return true;  
        if(o == null || getClass() != o.getClass()) return false;  
  
        Point point = (Point) o;
        
        if(this.dimension != point.getDimension()) return false;
        
        for(int i = 0; i < this.dimension; i++)
        	if(Double.compare(this.getData().get(i), point.getData().get(i)) != 0)
        		return false;
  
        return true;  
    }
	
	@Override  
    public int hashCode() {  
        int result = 0;  
        long temp = 0;
        
        for(int i = 0; i < data.size(); i++) {
        	temp = data.get(i) != +0.0d ? Double.doubleToLongBits(data.get(i)) : 0L;  
            result = 31 * result + (int) (temp ^ (temp >>> 32));
        }
          
        return result;  
    }  
}
