/**
 * 
 */
package zhongwei.forfun.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
* <p>Title: Cluster.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月8日
* @version 1.0
*/
public class Cluster {
	private String id;
	private Point center;
	private List<Point> members = new LinkedList<Point>();
	
	public Cluster(String id, Point center, List<Point> members) {
		this.id = id;
		this.center = center;
		this.members = members;
	}
	
	public String getId() {  
        return id;  
    }  
  
    public Point getCenter() {  
        return center;  
    }
    
    public List<Point> getMembers() {  
        return members;  
    }
    
    public void setCenter(Point center) {  
        this.center = center;  
    }
	
	public void addMember(Point newPoint) {  
        this.members.add(newPoint);
    }
	
	public void clearMembers() {
		this.members.clear();
	}
	
	public void updateCenter() throws Exception {
		if(members.isEmpty()) {
			throw new IllegalStateException("Size of members in " + id + " is empty.");  
			//center = KMeans.chooseCenter(1);
			//return;
		}
		
		List<Double> newData = new ArrayList<Double>();
		
		for (int i = 0; i < center.getDimension(); i++) {  
            double tmp = 0.0;
			for(int j = 0; j < members.size(); j++) {
            	tmp += members.get(j).getData().get(i);
            }
			tmp /= members.size();
			newData.add(tmp);
        }  
		
        Point nc = new Point("center-" + id, newData);  
        center = nc;  
	}

}
