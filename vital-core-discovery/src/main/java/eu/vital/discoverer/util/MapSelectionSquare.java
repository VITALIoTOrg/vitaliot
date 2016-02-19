package eu.vital.discoverer.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * This class is used to provide support for coordinate evaluation in a selection
 * process on a map.Starting from a center and a distance computes the coordinates 
 * for the four main cardinal directions (north, south, east, west). 
 * These points are then used to obtain boundary limits for the square area under 
 * analysis. 
 * 
 * @author Salvatore Guzzo Bonifacio <salvatore.guzzo_bonifacio@inria.fr>
 *
 */
public class MapSelectionSquare {
	
	private GPSPoint center, north, south, east, west;
	private double distance;
	private final double R=6370;
	private DecimalFormat formatter;
	
	/**
	 * Create a new object to compute boundaries in a map region for selection purposes.
	 *  
	 * @param center Central point coordinates
	 * @param distance square side length
	 */
	public MapSelectionSquare(GPSPoint center, double distance){
		
		this.center=center;
		this.distance=distance;
		this.north=getDestinationPoint(center, 0d, distance/2);
		this.east=getDestinationPoint(center, 90d, distance/2);
		this.south=getDestinationPoint(center, 180d, distance/2);
		this.west=getDestinationPoint(center, 270d, distance/2);
		formatter=new DecimalFormat("#.######");
		formatter.setRoundingMode(RoundingMode.HALF_DOWN);
	}
	
	/**
	 * The maximum latitude for selected area with 6 digit precision
	 * @return Maximum Latitude
	 */
	public String getMaxLatitude(){
		return formatter.format(this.north.getLatitude());
	}
	/**
	 * The minimum latitude for selected area with 6 digit precision
	 * @return Minimum Latitude
	 */
	public String getMinLatitude(){
		return formatter.format(this.south.getLatitude());
	}
	
	/**
	 * The maximum longitude for selected area with 6 digit precision
	 * @return Maximum Longitude
	 */
	public String getMaxLongitude(){
		return formatter.format(this.east.getLongitude());
	}
	
	/**
	 * The minimum longitude for selected area with 6 digit precision
	 * @return Minimum Longitude
	 */
	public String getMinLongitude(){
		return formatter.format(this.west.getLongitude());
	}
	
	/**
	 * Computes if specified point is inside the selection area
	 * @param point GPSPoint to test
	 * @return true if inside selection area, false otherwise
	 */
	public boolean isPointInSelection(GPSPoint point){
		boolean inside=true;
		//above maximum latitude
		if(point.getLatitude()>this.north.getLatitude())
			inside=false;
		//below minimun latitude
		if(point.getLatitude()<this.south.getLatitude())
			inside=false;
		//above maximum longitude
		if(point.getLongitude()>this.east.getLongitude())
			inside=false;
		//below mininum longitude
		if(point.getLongitude()<this.west.getLongitude())
			inside=false;
		return inside;
	}
	
	
	/**
	 * Produce a GPSPoint object residing at a distance "distance" from start_point 
	 * along the radial direction with an angle bearing_angle. 
	 *  
	 * @param start_point the center of the square region
	 * @param bearing_angle the angle from start point for the radial direction. (increases clockwise from north)
	 * @param distance the required distance in Km 
	 * @return the destination point
	 */
	private GPSPoint getDestinationPoint(GPSPoint start_point, double bearing_angle, double distance){
		double delta=distance/R;
		
		double src_lat=Math.toRadians(start_point.getLatitude());
		double src_long=Math.toRadians(start_point.getLongitude());
		double bearing =Math.toRadians(bearing_angle);
		
		double dst_lat= Math.asin( (Math.sin(src_lat)*Math.cos (delta)) + (Math.cos (src_lat)*Math.sin (delta) * Math.cos( bearing)) );
		double dst_long= src_long+Math.atan2( Math.sin(bearing)*Math.sin(delta)*Math.cos(src_lat), Math.cos(delta)-(Math.sin(src_lat)*Math.sin(dst_lat)) );
		
		double dst_lat_deg=Math.toDegrees(dst_lat);
		double dst_long_deg=Math.toDegrees(dst_long);
		
		GPSPoint destination= new GPSPoint(dst_lat_deg, dst_long_deg);
		
		return destination;
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Minimum latitude: "+getMinLatitude()+"\n"
				+ "Maximum latitude: "+getMaxLatitude()+"\n"
				+ "Minimum longitude: "+getMinLongitude()+"\n"
				+ "Maximum longitude: "+getMaxLongitude()+"\n";
				
				
	}
	
	
}
