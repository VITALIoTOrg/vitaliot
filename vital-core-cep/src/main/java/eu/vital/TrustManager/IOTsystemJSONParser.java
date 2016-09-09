package eu.vital.TrustManager;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.vital.TrustManager.taskExecutor.Tasker;
import eu.vital.vitalcep.connectors.iotda.IOTDAManager;
import eu.vital.vitalcep.security.Security;

public class IOTsystemJSONParser {//implements Tasker{

	public String name;
	static IOTsystemJSONParser slar = null;
	private String dmsURL;
	private String iotdaURL;
	private ConcurrentHashMap<String, IoTSystem>iotshm;
	
	static Logger logger = Logger.getLogger("IOTSystemCreator Class");
	
	/**
	 * @param iotshm: hashmap where parsed infor is stored, this is the returned info
	 * @param body: body in json format received in start method of the rest api
	 * @return true if the method has parsed the body without problems.
	 */
	public static boolean extractIOTsystems(ConcurrentHashMap<String, IoTSystem>iotshm, String body){
		
		String iotsName;
		
		JSONObject jo = new JSONObject(body);
		
		JSONArray iotsArray = jo.getJSONArray("IoTSystems");
		
		try{
			for (int i = 0 ; i < iotsArray.length(); i++){
				JSONObject iotjo = iotsArray.getJSONObject(i);
				
				iotsName = iotjo.getString("id"); //IoTSystem id
					
				JSONArray slajoarray = iotjo.getJSONArray("sla_params");
						
				String slaid;
				String slah;
				String slal;
				String sladir;
				HashMap<String, String[]> slaHM = new HashMap<>();;
				for (int j=0; j<slajoarray.length(); j++){
					JSONObject slajo = slajoarray.getJSONObject(j);
					slaid = slajo.getString("id");
					slah = slajo.getString("upthreshold");
					slal = slajo.getString("downthreshold");
					sladir = slajo.getString("address");
					String[] slaA = {slaid,slah,slal,sladir};
					slaHM.put(slaA[0], slaA);
				}
				IoTSystem iots = new IoTSystem(iotsName);
				iots.initSLAs(slaHM);
				iotshm.put(iots.name, iots);
				
			}
		}catch (JSONException e){
			logger.error (e,e);
			return false;
		}
		
		return true;
	}
	
	
//	public boolean getIoTSystems(){
//	
//		String cookie;
//    	
//		try{
//            
//			IoTSystem[] iots;
//            		
//			StringBuilder ck = new StringBuilder();
//			Security slogin = new Security();
//                               
//			JSONArray aData =  new JSONArray();
//                	               	
//			Boolean token = slogin.login("elisa", "elisotas1",false,ck);
////                        credentials.put("username", req.getHeader("trustUser"));
////                        credentials.put("password", req.getHeader("querty1234"));
//			if (!token){
//				logger.error("Faliled at login: " + Response.Status.UNAUTHORIZED);
//				return false;
//			}else{
//				cookie = ck.toString();    
//                	
//				//String body = "{\"@type\": \"http://vital-iot.eu/ontology/ns/IotSystem\"}";
//				String body = "{}";
//				try {
//					
//					//DMSManager oDMS = new DMSManager(dmsURL,cookie);
//					IOTDAManager iotda = new IOTDAManager(iotdaURL, cookie);
//                         
//					aData = iotda.getRegisteredIoTsystems(body);
//                    		
//					iots = new IoTSystem[aData.length()];
//                    		
//					for (int n=0 ; n<aData.length();n++){
//						JSONObject pp = aData.getJSONObject(n);
//						String systemS = pp.opt("ppi").toString();
//						logger.debug("system: " +  systemS);
//                    			
//						IoTSystem is =new IoTSystem(systemS);
//						is.initSLAs(null);
//						iotshm.put(systemS, is);
//                    			
//                    			//Look for SLAs
//					}
//					
//                         
//				} catch (Exception ex) {
//					logger.error(ex,ex);
//					return false;
//				}
//
//				logger.debug("System fetched: " + Response.Status.OK);
//				if (iotshm.size()>0)
//					return true;
//				else
//					return false;
//			}
//			    	
//		}catch(Exception e){
//			logger.error(e,e);
//			return false;
//		}
//        
//	}
//
//
//	@Override
//	public boolean doTask() {
//		this.getIoTSystems();
//		return true;
//	}
}