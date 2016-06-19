package eu.vital.discoverer.util;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.vital.discoverer.exception.ConnectionErrorException;

public class DMSManager {

	private static Logger logger=Logger.getLogger(DMSManager.class);
	private final String LATITUDE_KEY="hasLastKnownLocation.geo:lat";
	private final String LONGITUDE_KEY="hasLastKnownLocation.geo:long";
	private final String MOVEMENT_PATTER_KEY="hasMovementPattern.type";
	private final String PREDICTED="Predicted";
	private final String KnownLocation_KEY="http://vital-iot.eu/ontology/ns/hasLastKnownLocation";
	private final String KnownLocation_lat_KEY="http://www.w3.org/2003/01/geo/wgs84_pos#lat";
	private final String KnownLocation_long_KEY="http://www.w3.org/2003/01/geo/wgs84_pos#long";

	private String endpoint, cookie;

	public enum DMS_Index{
		SYSTEM("/querySystem"),
		SERVICE("/queryService"),
		SENSOR("/querySensor"),
		OBSERVATION("/queryObsdervation");

		private final String address;

	    private DMS_Index(String s) {
	        address = s;
	    }

	    public String getAddress() {
	       return this.address;
	    }

	}

	public DMSManager(DMS_Index index, String cookie){
		DiscoveryProperties props=new DiscoveryProperties();
		String DMS_Address=props.getProperty(DiscoveryProperties.DMS_ENDPOINT_ADDRESS);
		String DMS_Port=props.getProperty(DiscoveryProperties.DMS_ENDPOINT_PORT);
		this.endpoint=DMS_Address+index.getAddress();
		this.cookie=cookie;
	}

	public LinkedList<JSONObject> getByType(String type){

		JSONObject postObject=new JSONObject();

		JSONObject obj=new JSONObject();
		obj.put("$exists", "true");
		postObject.put("type", obj);
		logger.debug("postObject: "+postObject.toJSONString());
		return queryDMS(endpoint, postObject, cookie);

	}


public LinkedList<JSONObject> getByField(String field, String value){

		JSONObject postObject=new JSONObject();
		
		//forcing @ character 
		//postObject.put("@"+field, value);
		postObject.put(field, value);
		
		return queryDMS(endpoint, postObject, cookie);

	}

public LinkedList<JSONObject> getByObser(String field, String value){

	//JSONObject postObject=new JSONObject();
	
	//String jsonstring = "{ \""+field+"\": { $elemMatch : {"+"\"@type\" " +":"+" \""+value+"\"} }";
	//String jsonstring = "{ \""+field+"\": { $elemMatch: { \"@type\": \""+value+"\"} } }";
	
	//logger.debug("Creata stringa: "+jsonstring);
	
	JSONObject rootQuery=new JSONObject();
	JSONObject root=new JSONObject();
	JSONArray array=new JSONArray();

//	JSONObject elemMatch=new JSONObject();
//	elemMatch.put("$elemMatch", "");
//	array.add(elemMatch);

//	JSONObject ty=new JSONObject();
//	ty.put("@"+"type", value);
//	array.add(ty);
	
	JSONObject tyOb=new JSONObject();
	tyOb.put("@"+"type", value);
	array.add(tyOb);
	rootQuery.put("$elemMatch",array);
	
	root.put(field,rootQuery);
	


	//root.put(field, array);

	logger.debug("Array: "+root.toJSONString());

	return queryDMS2(endpoint, removeExtraQuotesandBrackets(root.toJSONString()), cookie);
			
}


public LinkedList<JSONObject> getBySubField(String field, String subfield, String value){

	String jsonstring = "{ \""+field+"\": { \""+subfield+"\": \""+value+"\"} }";
	
	JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonstring);
	
	return queryDMS(endpoint, jsonObject, cookie);

}

public JSONObject getById(String id){

	LinkedList<JSONObject> id_search=getByField("@id", id);
	if(id_search.isEmpty()){
		return null;
	}
	else return id_search.getFirst();
}

public LinkedList<JSONObject> getByIdList(LinkedList<String> ids){

	JSONObject root=new JSONObject();
	JSONArray array=new JSONArray();

	for(String s: ids){
		JSONObject current=new JSONObject();
		current.put("@id", s);
		array.add(current);
	}

	root.put("$or", array);

	return queryDMS(endpoint, root, cookie);
}


public LinkedList<JSONObject> searchInRegion(MapSelectionSquare area){

	String ValuesLatitude = "{ \"$gt\": "+Double.parseDouble(area.getMinLatitude())+",\"$lt\":"+Double.parseDouble(area.getMaxLatitude())+"}";
	String ValuesLongitude = "{ \"$gt\": "+Double.parseDouble(area.getMinLongitude())+",\"$lt\":"+Double.parseDouble(area.getMaxLongitude())+"}";
	
	JSONObject IntervalLatitude = new JSONObject();
	IntervalLatitude.put("@value", JSONValue.parse(ValuesLatitude));
		
	JSONArray ArrayIntervalLatitude = new JSONArray();
	ArrayIntervalLatitude.add(IntervalLatitude);
	
	JSONObject MatchIntervalLatitude = new JSONObject();
	MatchIntervalLatitude.put("$elemMatch", ArrayIntervalLatitude);
	
	//JSONObject LatitudesObject = new JSONObject();
	//LatitudesObject.put(KnownLocation_lat_KEY, MatchIntervalLatitude);
	
	
	JSONObject IntervalLongitude = new JSONObject();
	IntervalLongitude.put("@value", JSONValue.parse(ValuesLongitude));
	
		
	JSONArray ArrayIntervalLongitude = new JSONArray();
	ArrayIntervalLongitude.add(IntervalLongitude);
	
	JSONObject MatchIntervalLongitude = new JSONObject();
	MatchIntervalLongitude.put("$elemMatch", ArrayIntervalLongitude);
	
	
	//JSONObject LongitudesObject = new JSONObject();
	//LongitudesObject.put(KnownLocation_long_KEY, MatchIntervalLongitude);
	
	
	String HasPosition = "{\""+KnownLocation_lat_KEY+"\":"+MatchIntervalLatitude+",\""+KnownLocation_long_KEY+"\""+MatchIntervalLongitude+"}";
	
	
	//{ " http://www.w3.org/2003/01/geo/wgs84_pos#lat ": {"$elemMatch":[{"@value":{"$gt":-52.686066,"$lt":-47.313934}}]},"http://www.w3.org/2003/01/geo/wgs84_pos#long"}
	logger.debug("HasPosition: "+HasPosition);
		
	//JSONArray ArrayPositions = new JSONArray();
	//ArrayPositions.add(LatitudesObject);
	//ArrayPositions.add(LongitudesObject);
	
	
	JSONObject MatchPositions = new JSONObject();
	MatchPositions.put("$elemMatch", JSONValue.parse(HasPosition));
	
	logger.debug("MatchPositions: "+MatchPositions.toString());
	
	
	JSONObject HasLocation = new JSONObject();
	HasLocation.put(KnownLocation_KEY, MatchPositions);


	logger.debug("HasLocation: "+HasLocation.toString());

	return queryDMS2(endpoint, removeExtraBrackets(HasLocation.toJSONString()), cookie);

}

public LinkedList<JSONObject> searchPredictedInRegion(MapSelectionSquare area){

	JSONObject root=new JSONObject();
	JSONArray array=new JSONArray();

	JSONObject minLat=new JSONObject();
	minLat.put(LATITUDE_KEY, createGT(Double.parseDouble(area.getMinLatitude())));
	array.add(minLat);

	JSONObject maxLat=new JSONObject();
	maxLat.put(LATITUDE_KEY, createLT(Double.parseDouble(area.getMaxLatitude())));
	array.add(maxLat);

	JSONObject minLong=new JSONObject();
	minLong.put(LONGITUDE_KEY, createGT(Double.parseDouble(area.getMinLongitude())));
	array.add(minLong);

	JSONObject maxLong=new JSONObject();
	maxLong.put(LONGITUDE_KEY, createLT(Double.parseDouble(area.getMaxLongitude())));
	array.add(maxLong);

	JSONObject movementPattern=new JSONObject();
	movementPattern.put(MOVEMENT_PATTER_KEY, PREDICTED);
	array.add(movementPattern);

	root.put("$and", array);

	return queryDMS2(endpoint, removeExtraQuotes(root.toJSONString()), cookie);

}

private JSONObject createGT(double value){
	JSONObject node=new JSONObject();
	node.put("$gt", value);
	return node;
}

private JSONObject createLT(double value){
	JSONObject node=new JSONObject();
	node.put("$lt", value);
	return node;
}

private String removeExtraQuotes(String input){

	StringTokenizer st=new StringTokenizer(input,"\"");
	StringBuilder sb=new StringBuilder();

	if(st.hasMoreTokens())

		sb.append(st.nextToken());


	while(st.hasMoreTokens()){

		String token=st.nextToken();
		if(token.charAt(0) == '$'){
			sb.append(" "+token+" ");
		}
		else{
			sb.append("\""+token+"\"");
		}

		if(st.hasMoreTokens()){
			sb.append(st.nextToken());
		}

	}

	return sb.toString();

}

private String removeExtraQuotesandBrackets(String input){
	
	String noBrackets = input.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\,","");

	StringTokenizer st=new StringTokenizer(noBrackets,"\"");
	StringBuilder sb=new StringBuilder();

	if(st.hasMoreTokens())

		sb.append(st.nextToken());


	while(st.hasMoreTokens()){

		String token=st.nextToken();
		if(token.charAt(0) == '$'){
			sb.append(" "+token+" ");
		}
		else{
			sb.append("\""+token+"\"");
		}

		if(st.hasMoreTokens()){
			sb.append(st.nextToken());
		}

	}

	return sb.toString();

}

private String removeExtraBrackets(String input){
	
	//String noBrackets = input.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\,","");
	String noBrackets = input.replaceAll("\\[", "").replaceAll("\\]","");
	return noBrackets;

}

private String CleanElemMatch(String input){
	
	String noBrackets = input.replaceAll("}]}]}]", "]");

	return noBrackets;

}

	private LinkedList<JSONObject> queryDMS(String DMS_endpoint, JSONObject postObject, String cookie){
		HttpURLConnection connectionDMS = null;
		try{

			logger.debug("cookie sent: "+cookie);
			logger.debug("JSON string received: "+postObject.toString());
			//logger.debug("JSON object received: "+postObject.toJSONString());
			//logger.debug("JSON string : "+"@"+postObject.toString());
			
			
			//postObject = "@"+String(postObject);
			String postObjectString=postObject.toJSONString();
			byte[] postObjectByte=postObjectString.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postObjectByte.length;
			URL DMS_Url=new URL(DMS_endpoint);

			// prepare header
			connectionDMS = (HttpURLConnection) DMS_Url.openConnection();
			connectionDMS.setDoOutput(true);
			connectionDMS.setDoInput(true);
			//connectionDMS.setConnectTimeout(5000);
			//connectionDMS.setReadTimeout(5000);
			connectionDMS.setConnectTimeout(50000);
			connectionDMS.setReadTimeout(50000);
			connectionDMS.setRequestProperty("Content-Type", "application/json");
			connectionDMS.setRequestProperty("Accept", "application/json");
			connectionDMS.setRequestProperty("charset", "utf-8");
			connectionDMS.setRequestProperty("Cookie", cookie);
			connectionDMS.setRequestMethod("POST");
			connectionDMS.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			
			DataOutputStream wr=new DataOutputStream(connectionDMS.getOutputStream());
			wr.write(postObjectByte);
			wr.flush();

			int HttpResult = connectionDMS.getResponseCode();
	
			if(HttpResult == HttpURLConnection.HTTP_ACCEPTED){
			
				JSONParser parser=new JSONParser();
				JSONArray array=(JSONArray) parser.parse(new InputStreamReader(connectionDMS.getInputStream(),"utf-8"));
				//JSONArray array=(JSONArray) parser.parse(new InputStreamReader(connectionDMS.getInputStream()));

				logger.debug("Reveiced response"+array.toJSONString());

				LinkedList<JSONObject> result=new LinkedList<JSONObject>();

				Iterator i= array.iterator();

				while(i.hasNext()){
					Object current=i.next();
					if(current instanceof JSONObject)
					result.add((JSONObject) current);
				}

				return result;

			}else{
				logger.debug("HTTP is not accepted "+ HttpResult);
				//TODO: throw exception for internal error
				return new LinkedList<JSONObject>();
			}
		}catch(Exception e){
			//TODO: throw exception for internal error
			logger.error(e.toString());
//			return new LinkedList<JSONObject>();
			throw new ConnectionErrorException("Error in connection with DMS");
		}finally{
			if(connectionDMS != null) {
				connectionDMS.disconnect();
			}
		}
	}
	
	private LinkedList<JSONObject> queryDMS2(String DMS_endpoint, String postObject, String cookie){
		HttpURLConnection connectionDMS = null;
		try{

			logger.debug("cookie sent: "+cookie);
			logger.debug("JSON object received: "+postObject);

			String postObjectString=postObject;
			byte[] postObjectByte=postObjectString.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postObjectByte.length;
			URL DMS_Url=new URL(DMS_endpoint);

			// prepare header
			connectionDMS = (HttpURLConnection) DMS_Url.openConnection();
			connectionDMS.setDoOutput(true);
			connectionDMS.setDoInput(true);
			//connectionDMS.setConnectTimeout(5000);
			//connectionDMS.setReadTimeout(5000);
			connectionDMS.setConnectTimeout(50000);
			connectionDMS.setReadTimeout(50000);
			connectionDMS.setRequestProperty("Content-Type", "application/json");
			connectionDMS.setRequestProperty("Accept", "application/json");
			connectionDMS.setRequestProperty("charset", "utf-8");
			connectionDMS.setRequestProperty("Cookie", cookie);
			connectionDMS.setRequestMethod("POST");
			connectionDMS.setRequestProperty("Content-Length", Integer.toString(postDataLength));

			DataOutputStream wr=new DataOutputStream(connectionDMS.getOutputStream());
			wr.write(postObjectByte);
			wr.flush();
			int HttpResult = connectionDMS.getResponseCode();
			if(HttpResult == HttpURLConnection.HTTP_ACCEPTED){
				JSONParser parser=new JSONParser();
				JSONArray array=(JSONArray) parser.parse(new InputStreamReader(connectionDMS.getInputStream(),"utf-8"));

				//logger.debug("Reveiced response"+array.toJSONString());

				LinkedList<JSONObject> result=new LinkedList<JSONObject>();

				Iterator i= array.iterator();

				while(i.hasNext()){
					Object current=i.next();
					if(current instanceof JSONObject)
					result.add((JSONObject) current);
				}

				return result;

			}else{
				return new LinkedList<JSONObject>();
			}
		}catch(Exception e){
			logger.error(e.toString());
			throw new ConnectionErrorException("Error in connection with DMS");
		}finally{
			if(connectionDMS != null) {
				connectionDMS.disconnect();
			}
		}
	}



}
