package eu.vital.discoverer.util;

import java.text.DecimalFormat;

public class GPSUtils {

	private final static double R=6370;

	public static GPSPoint getDestinationPoint(GPSPoint start_point, double bearing_angle, double distance){
		double delta=distance/R;

		double src_lat=Math.toRadians(start_point.getLatitude());
		double src_long=Math.toRadians(start_point.getLongitude());
		double bearing =Math.toRadians(bearing_angle);

		double dst_lat= Math.asin( (Math.sin(src_lat)*Math.cos (delta)) + (Math.cos (src_lat)*Math.sin (delta) * Math.cos( bearing)) );
		double dst_long= src_long+Math.atan2( Math.sin(bearing)*Math.sin(delta)*Math.cos(src_lat), Math.cos(delta)-(Math.sin(src_lat)*Math.sin(dst_lat)) );

		double dst_lat_deg=Math.toDegrees(dst_lat);
		double dst_long_deg=Math.toDegrees(dst_long);

		double dst_lat_cut=Double.parseDouble(new DecimalFormat("#0.000000").format(dst_lat_deg));
		double dst_long_cut=Double.parseDouble(new DecimalFormat("#0.000000").format(dst_long_deg));

		GPSPoint destination= new GPSPoint(dst_lat_cut, dst_long_cut);


		return destination;

	}

	public static double angleBetweenPoints_V3(GPSPoint startPoint, GPSPoint endPoint){
		double bearing=0;

		double lat1=Math.toRadians(startPoint.getLatitude());
		double long1=Math.toRadians(startPoint.getLongitude());
		double lat2=Math.toRadians(endPoint.getLatitude());
		double long2=Math.toRadians(endPoint.getLongitude());


		double y=Math.sin(long2-long1)*Math.cos(lat2);
		double x=Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(long2-long1);

		bearing=Math.atan2(y, x);
		bearing=Math.toDegrees(bearing);
		bearing=(bearing+360)%360;

		bearing=Double.parseDouble(new DecimalFormat("#0.00").format(bearing));


		return bearing;
	}


}
