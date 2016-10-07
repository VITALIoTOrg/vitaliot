package eu.vital.TrustManager.manager;

import java.util.Random;

/**
 * @author adminuser
 * This class is used to  generate metric values according to sla direcction, thresholds, the variance set for the sla, and an error indicator.
 *
 */
public class Value_gen {
	Random r;
	double max;
	double min;
	double variance;
	String addres;
	
	public Value_gen (double max, double min, double variance, String address){
		r = new Random();
		
		this.max = max;
		this.min = min;
		this.variance = variance;
		this.addres = address;
	}
	
	public Double generate (double error){
		Double value=0.0;
		if (addres.equals("b"))
			value = ((Math.abs(r.nextGaussian())*((max+min)/2))+(variance*error));
		else if (addres.equals("m"))
			value = ((Math.abs(r.nextGaussian())*(max-variance))+(variance*error));
		else if (addres.equals("d"))
			value = ((Math.abs(r.nextGaussian())*(min+variance))-(variance*error));
				
		return value;
	}
}
