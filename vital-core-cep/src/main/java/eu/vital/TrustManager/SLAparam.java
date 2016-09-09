package eu.vital.TrustManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author adminuser
 * class to manage the metric of each sla param and the fulfilment, this is needed by the trust engine
 *
 */
public class SLAparam {
	
	String name;
	double max;
	double min;
	double variance;
	String address;
	
	Value_gen valgen;
	int fulfilment=0;
	Double lastValue = 0.0;
	String lastValueTime;
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	Date date = new Date();
	
	/**
	 * Class constructor
	 * 
	 * @param name: of the sla param it is according to vital ontology
	 * @param max: max threshold
	 * @param min: min threshold
	 * @param address: addres to fulfil the sla
	 * @param variance: this is used to simulate the values of the metrics, this param is specific for each sla type, fixed in SLAContants class
	 * @param variacion: this is used as modifier of the variance, to get some more uncertainty of the values.
	 */
	public SLAparam (String name, int max, int min, String address, int variance, double variacion){
		this.name = name;
		this.address = address;
		
		
		if (address.equals("b")){
			this.max =  max;
			this.min =  min;
		}
		else if (address.equals("m"))
			this.max = max;
		else if (address.equals("d"))
			this.min = min;
		
		this.variance = variance*variacion;
		
		valgen = new Value_gen(this.max, this.min, this.variance, this.address);
	}
	
	
	/**
	 * method to generate the numeric value of the metric
	 * @param error, it is used to introduce error in the value, to be out of thresholds
	 * @return
	 */
	public double genVal (double error){
		lastValue = valgen.generate(error);
		lastValueTime=dateFormat.format(date);
		fulfilment =0;
		if (address.equals("m")){
			if (lastValue<=max)
				fulfilment = 1;
		}
		else if (address.equals("d")){
			if (lastValue >= min)
				fulfilment = 1;
		}
		else
			if ((lastValue>=min)&&(lastValue<=max))
				fulfilment=1;
		
		return lastValue;
	}
	
	
	/**
	 * @return a String with the information of the metric in BSON format to be inserted into the mongodb
	 */
	public String getJSONVal(){
		StringBuilder sb1 = new StringBuilder();
		sb1.append("{");
		sb1.append("\'metric_name\':\'" + this.name + "\',");
		sb1.append("\'max\':\'" + this.max + "\',");
		sb1.append("\'min\':\'" + this.min + "\',");
		sb1.append("\'variance\':\'" + this.variance + "\',");
		sb1.append("\'date\':\'" + this.lastValueTime + "\',");
		sb1.append("\'value\':\'" + this.lastValue + "\',");
		sb1.append("\'fulfilment\':\'" + this.fulfilment + "\'");
		sb1.append("}");
		
		return sb1.toString();
		
		
	}

}
