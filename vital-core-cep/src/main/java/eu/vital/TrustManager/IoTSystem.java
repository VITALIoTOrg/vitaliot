package eu.vital.TrustManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;

/**
 * @author adminuser
 * 
 * This class stores all stuff related to an instance been monitored by Trust manager. For the time being it is prepared only for 
 * IoT systems.
 *
 */
public class IoTSystem {
	//name of the monitored instance (iotsystem)
	String name;
	
	//Array of 
	SLAparam[] slas;
	
	//randomizer used to generate values for metrics in the meanwhile sla and metric methods are implemented by iotsystem developers
	Random r = new Random();
	
	/**
	 * Class constructor
	 * @param name: name of the monitored instance (iotsystem) it is the vital id: EJ. http://vital-integration.atosresearch.eu:8280/vital-ppi-citybikes/velib
	 */
	public IoTSystem (String name){
		
		this.name = name;
	}
	
	
	
	/**
	 * method to direction values between the received info and the values understood by the app, and to SET VALUES USED TO 
	 * GENERATE RANDOM VALUES FOR METRICS.
	 * @param slasHM: informacion sobre SLAs recibida en el metodo start del rest
	 */
	public void initSLAs (HashMap<String,String[]> slasHM){
				
		slas= new SLAparam [slasHM.size()];
		int i =0;
		
		Iterator<Entry<String, String[]>> it = slasHM.entrySet().iterator();
		while (it.hasNext()){
			String a[] = it.next().getValue();
			String b[] = SLAconstants.getSLAHM().get(a[0]);
			String address;
			
			//translate direction of the SLA
			//received: 1, app uses: m. this means that the sla direction is: sla is fulfilled when metric is below max threshold
			//received: 2, app uses: d. this measn that the sla direction is: sla is fulfilled when metric is over min threshold
			//received: 3, app uses: b. this means that the sla direction is: sla is fulfilled whtn metric is between thresholds
			if (a[3].equals("1"))
				address = "m";
			else if (a[3].equals("2"))
				address = "d";
			else if (a[3].equals("3"))
				address = "b";
			else
				address = "m"; //TODO: control this error
			
			
			//Be carefull, two last values are used to simulate metrics
			slas[i] = new SLAparam(a[0],Integer.parseInt(a[1]),Integer.parseInt(a[2]),address,Integer.parseInt(b[1]),0.95);
			i++;
		}
	}
	
	
	/**
	 * method to generate random values for all slas of the iotsystem. values follow a gaussian distribution
	 */
	public void generateValues(){
		for (int j =0; j<slas.length; j++){
			slas[j].genVal(r.nextGaussian()*1.02);
		}
	}
	
	
	
	/**
	 * @return a String whit the last metric in BSON format, this is stored in mongodb to be used by the trust engine
	 */
	public String getDocument(){
		StringBuilder sb = new StringBuilder();
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		sb.append("{");
		sb.append("\'id\':\'" + this.name +"\',");
		sb.append("\'timeS\':\'"+ dateFormat.format(date) + "\',");
		sb.append("\'metric\':[");
		for (int j =0; j<slas.length; j++){
		 	
			if (j!=0)
				sb.append(",");
			sb.append(slas[j].getJSONVal());
		}
		sb.append("]}");
		return sb.toString();
		
		
	}

}