package eu.vital.TrustManager.params;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.vital.TrustManager.conf.ConfigReader;
import eu.vital.TrustManager.connectors.dms.DMSManager;
import eu.vital.TrustManager.connectors.ppi.PPIManager;
import eu.vital.TrustManager.security.Security;

/**
 * @author adminuser
 * 
 * This class stores all stuff related to an instance been monitored by Trust manager. For the time being it is prepared only for 
 * IoT systems.
 *
 */
public class IoTSystem {
	//name of the monitored instance (iotsystem)
	public String name;
	public String getSupportedSLAsURL=null;
	public String getSLAsURL=null;
	public JSONObject metricsJO=null;
	
	public String metricsRequestBody;
	
	//Array of 
	PerformanceMetric[] slas;
	
	//randomizer used to generate values for metrics in the meanwhile sla and metric methods are implemented by iotsystem developers
	Random r = new Random();
	
	Logger logger = Logger.getLogger(IoTSystem.class);
	
	
	HashMap<String,String[]> slasHM;
	
	/**
	 * Class constructor
	 * @param name: name of the monitored instance (iotsystem) it is the vital id: EJ. http://vital-integration.atosresearch.eu:8280/vital-ppi-citybikes/velib
	 */
	public IoTSystem (String name, HashMap<String,String[]> slasHM){
		this.name = name;
		this.slasHM = slasHM;
	}
		
	public boolean init(){
		
		//TRANSLATE VALUES RECEIVED	
		slas= new PerformanceMetric [slasHM.size()];
		int i =0;
		
		Iterator<Entry<String, String[]>> it = slasHM.entrySet().iterator();
		while (it.hasNext()){
			String a[] = it.next().getValue();
			String address;
			
			
			//received: 1, app uses: m. this means that the performance metric direction is: performance metric is fulfilled when metric is below max threshold
			//received: 2, app uses: d. this measn that the performance metric direction is: performance metric is fulfilled when metric is over min threshold
			//received: 3, app uses: b. this means that the performance metric direction is: performance metric is fulfilled whtn metric is between thresholds
			if (a[3].equals("1"))
				address = "m";
			else if (a[3].equals("2"))
				address = "d";
			else if (a[3].equals("3"))
				address = "b";
			else
				address = "m"; //TODO: control this error
			
			slas[i] = new PerformanceMetric(a[0],Integer.parseInt(a[1]),Integer.parseInt(a[2]),address);
			i++;
		}
	
		//GET IOT SYSTEM URLS AND DATA
		String cookie;
		String orchestratorPPIURL;
		String DMSURL;
		
		ConfigReader creader = ConfigReader.getInstance();
		orchestratorPPIURL = creader.get(ConfigReader.ORCHESTRATOR_URL);
    	DMSURL = creader.get(ConfigReader.DMS_URL);
		try{
            
			IoTSystem[] iots;
            		
			StringBuilder ck = new StringBuilder();
			Security slogin = new Security();
                               
			JSONArray aData =  new JSONArray();
                	               	
			Boolean token = slogin.login("elisa", "elisotas1",false,ck);
//                        credentials.put("username", req.getHeader("trustUser"));
//                        credentials.put("password", req.getHeader("querty1234"));
			if (!token){
				logger.error("Faliled at login: " + Response.Status.UNAUTHORIZED);
				return false;
			}else{
				cookie = ck.toString();    
                	
				//String body = "{\"@type\": \"http://vital-iot.eu/ontology/ns/IotSystem\"}";
				String body = "{}";
				
				DMSManager dms = new DMSManager(DMSURL,cookie);
					
				JSONArray ja = dms.getServices(body);
//				StringBuilder sb = new StringBuilder();
//				sb.append(ja.toString());
//				logger.debug(sb.toString());
				
				JSONObject jo=null;
				String id="";
				String type="";
				Boolean found = false;
				for (int h = 0; h<ja.length();h++){
					jo =ja.getJSONObject(h);
					id = null;
					type = null;
					try{
						id = jo.getString("id");
						type = jo.getString("type");
					}catch(Exception e){
					}
					
					if (id.contains(name) && type.equalsIgnoreCase("MonitoringService")){
						found = true;
						break;
					}
				}
				JSONArray opja;
				if (found){
					Object opjo = jo.get("operations");
					if (opjo instanceof JSONArray){
						opja = (JSONArray)opjo;
						
						boolean hasgetmetrics=false;
						boolean hasgetsupported=false;
						String supM= null;
						String getM= null;
						for (int j=0;j<opja.length();j++){
							String optype = ((JSONObject)(opja.getJSONObject(j))).getString("type");
							if (optype.equalsIgnoreCase("GetSupportedSLAParameters")){
								supM=opja.getJSONObject(j).getString("hrest:hasAddress");
								hasgetsupported=true;
							}
							if (optype.equalsIgnoreCase("GetSLAParameters")){
								getM=opja.getJSONObject(j).getString("hrest:hasAddress");
								hasgetmetrics=true;
							}
							if (hasgetsupported && hasgetmetrics){
								this.getSupportedSLAsURL =supM;
								this.getSLAsURL=getM;
								
								
								StringBuilder sb1 = new StringBuilder();
								Iterator its = slasHM.keySet().iterator();
								int s = slasHM.size();
								if (s>0){
									if(s==1){
										sb1.append("{\"metric\":\"");
										sb1.append(slasHM.get(its.next())[0]);
										sb1.append("\"}");
										metricsRequestBody = sb1.toString();
									}else{
										sb1.append("{\"metric\":[");
									
										while(its.hasNext()){
											sb1.append("\"");
											sb1.append(slasHM.get(its.next())[0]);
											sb1.append("\"");
											if (its.hasNext())
												sb1.append(",");
										}
										sb1.append("]}");
										metricsRequestBody =sb1.toString();
									}
								}
								else
									return false;
								
								
								return true;
							}
						}
						
					}
				
				}	
				
			}	
			    	
			return false;
			
		}catch(Exception e){
			logger.error(e,e);
			return false;
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
	
	public boolean getSystemMetricValues(){
		//GET IOT SYSTEM URLS AND DATA
		String cookie;
		String orchestratorPPIURL;
		String DMSURL;
		
		ConfigReader creader = ConfigReader.getInstance();
		orchestratorPPIURL = creader.get(ConfigReader.ORCHESTRATOR_URL);
    	DMSURL = creader.get(ConfigReader.DMS_URL);
		try{
            
			IoTSystem[] iots;
            		
			StringBuilder ck = new StringBuilder();
			Security slogin = new Security();
                               
			JSONArray aData =  new JSONArray();
                	               	
			Boolean token = slogin.login("elisa", "elisotas1",false,ck);
//		                        credentials.put("username", req.getHeader("trustUser"));
//		                        credentials.put("password", req.getHeader("querty1234"));
			if (!token){
				logger.error("Faliled at login: " + Response.Status.UNAUTHORIZED);
				return false;
			}else{
				cookie = ck.toString();
				
				PPIManager ppim = new PPIManager (cookie);
				
				
				JSONArray perMetricsJA =ppim.GetSLAParameters(getSLAsURL, metricsRequestBody);
				
				int l = slas.length;
				for (int z=0;z<perMetricsJA.length();z++){
					Double value;
					String param;
					
					//param = 
					param = perMetricsJA.getJSONObject(z).getJSONObject("ssn:observationProperty").getString("type");
					JSONObject ooo = perMetricsJA.getJSONObject(z).getJSONObject("ssn:observationResult");
					JSONObject ppp = ooo.getJSONObject("ssn:hasValue");
					value = (Double)ppp.get("value");
					//value = ooppp.floatValue(); //Float.parseFloat(perMetricsJA.getJSONObject(z).getJSONObject("ssn:observationResult").getJSONObject("ssn:hasValue").getString("value"));
				
					param = param.substring(param.indexOf(":")+1);
					
					for (int nn=0; nn<slas.length;nn++){
						PerformanceMetric pm = slas[nn];
						String nnaammee =pm.name;
						if (pm.name.toLowerCase().contains(param.toLowerCase())){
							pm.setValue(value);
						}
					}
				
				
				
				}
				
				
				return true;
			}
		}catch (Exception e){
			logger.error(e,e);
			return false;
		}
	}
	

}