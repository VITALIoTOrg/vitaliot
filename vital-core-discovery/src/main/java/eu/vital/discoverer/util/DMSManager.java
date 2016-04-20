/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.util;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.vital.discoverer.exception.ConnectionErrorException;

public class DMSManager {

	private static Logger logger=Logger.getLogger(DMSManager.class);
	private final String LATITUDE_KEY="hasLastKnownLocation.geo:lat";
	private final String LONGITUDE_KEY="hasLastKnownLocation.geo:long";
	private final String MOVEMENT_PATTER_KEY="hasMovementPattern.type";
	private final String PREDICTED="Predicted";

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

		//postObject.put(field, value);
		postObject.put("@"+field, value);

		return queryDMS(endpoint, postObject, cookie);

	}

public JSONObject getById(String id){

	LinkedList<JSONObject> id_search=getByField("id", id);
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
		current.put("id", s);
		array.add(current);
	}

	root.put("$or", array);

	return queryDMS(endpoint, root, cookie);
}


public LinkedList<JSONObject> searchInRegion(MapSelectionSquare area){

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

	root.put("$and", array);

	logger.debug("Geo Query sent: "+root.toJSONString());

	return queryDMS2(endpoint, removeExtraQuotes(root.toJSONString()), cookie);

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

	private LinkedList<JSONObject> queryDMS(String DMS_endpoint, JSONObject postObject, String cookie){
		HttpURLConnection connectionDMS = null;
		try{

			logger.debug("cookie sent: "+cookie);
			logger.debug("JSON object received: "+postObject.toJSONString());

			String postObjectString=postObject.toJSONString();
			byte[] postObjectByte=postObjectString.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postObjectByte.length;
			URL DMS_Url=new URL(DMS_endpoint);

			// prepare header
			connectionDMS = (HttpURLConnection) DMS_Url.openConnection();
			connectionDMS.setDoOutput(true);
			connectionDMS.setDoInput(true);
			connectionDMS.setConnectTimeout(5000);
			connectionDMS.setReadTimeout(5000);
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
			connectionDMS.setConnectTimeout(5000);
			connectionDMS.setReadTimeout(5000);
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
			if(HttpResult == HttpURLConnection.HTTP_OK){
				JSONParser parser=new JSONParser();
				JSONArray array=(JSONArray) parser.parse(new InputStreamReader(connectionDMS.getInputStream(),"utf-8"));

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
