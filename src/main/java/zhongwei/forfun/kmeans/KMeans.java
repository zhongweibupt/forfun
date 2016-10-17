/**
 * 
 */
package zhongwei.forfun.kmeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
* <p>Title: KMeans.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月8日
* @version 1.0
*/
public class KMeans {
	private int k;
	private int numIter = 10000;//default
	private int dimension;
	private List<Point> points;
	private List<Cluster> clusters;
	private Distance distance = new Distance() {  
        public double getDistance(Point p1, Point p2) {  
            //欧几里德距离  
        	double sum = 0;
        	for(int i = 0; i < p1.getDimension(); i++) {
        		sum += Math.pow(p1.getData().get(i) - p2.getData().get(i), 2);
        	}
            return Math.sqrt(sum);  
        }  
    };//default
        
    public KMeans(int k, int dimension, int num, Distance distance) {
    	this.k = k;
    	this.dimension = dimension;
    	this.numIter = num;
    	this.distance = distance;
    	this.points = new LinkedList<Point>();
    	this.clusters = new LinkedList<Cluster>();
    }
    
    public KMeans(int k, int dimension) {
    	this.k = k;
    	this.dimension = dimension;
    	this.points = new LinkedList<Point>();
    	this.clusters = new LinkedList<Cluster>();
    }
    
    public void init(String datafilePath) throws FileNotFoundException {
    	readCsv(datafilePath);
    	Set<Point> centers = randomCenter(k);
    	
    	List<Cluster> clusters = new ArrayList<Cluster>();
    	
    	int i = 0;
    	for(Point point : centers) {
    		List<Point> members = new LinkedList<Point>();
    		Cluster cluster = new Cluster("Cluster" + (i++), point, members);
    		clusters.add(cluster);
    	}
    	
    	this.clusters = clusters;
    }
    
    public void clustering() {
    	for(int i = 0; i < numIter; i++) {
    		updateCluster(this.clusters);
    		System.out.println("Running: " + i);
    	}
    }
    
    public void updateCluster(List<Cluster> clusters) {
    	for(Cluster cluster : clusters) {
    		cluster.clearMembers();
    	}
    	
    	for(Point point : this.points) {
    		Cluster cluster = point.chooseCluster(clusters, this.distance);
    		cluster.addMember(point);
    	}
    	
    	for(Cluster cluster : clusters) {
    		try {
				cluster.updateCenter();
			} catch (Exception e) {
				e.printStackTrace();
				cluster.setCenter((Point)randomCenter(1).toArray()[0]);
			}
    	}
    }
    
    public Set<Point> randomCenter(int k) {
    	 Set<Point> centers = new HashSet<Point>();  
         Random ran = new Random();  
         int roll = 0;  
         while (centers.size() < k) {  
             roll = ran.nextInt(points.size());  
             centers.add(points.get(roll));  
         }  
         return centers; 
    }
    
    private void readCsv(String datafilePath) throws FileNotFoundException {
    	this.points = new LinkedList<Point>();
    	this.clusters = new LinkedList<Cluster>();
    	
    	File file = new File(datafilePath);
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("[,\r]");
		
		int i = 0;
		while (scanner.hasNext()) {
			//if(i == 0) {
				//for(int j = 0; j <= dimension; j++)
					//scanner.next();
			//}
			String id = scanner.next();
			List<Double> data = new ArrayList<Double>();
			
			for(int j = 0; j < dimension; j++) {
				data.add(Double.valueOf(scanner.next()).doubleValue());
			}			
			Point point = new Point(id, data);
			this.points.add(point);
			i++;
	    }
    }
    
    public static void main(String args[]) throws FileNotFoundException {
    	KMeans k = new KMeans(10, 2);
    	k.init("C:\\Users\\zhwei\\Desktop\\zhongweibupt.github.io\\mtsp-ga\\ga-0.0.1\\data\\002.csv");
    	k.clustering();
    }

}
