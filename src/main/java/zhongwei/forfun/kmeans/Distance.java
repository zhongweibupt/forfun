/**
 * 
 */
package zhongwei.forfun.kmeans;

/**
* <p>Title: Distance.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月8日
* @version 1.0
*/
public interface Distance {
	abstract public double getDistance(Point p1, Point p2);
}
