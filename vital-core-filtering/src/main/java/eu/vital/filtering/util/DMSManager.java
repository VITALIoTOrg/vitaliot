package eu.vital.filtering.util;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import eu.vital.filtering.exception.ConnectionErrorException;
import eu.vital.filtering.inputJSON.Threshold_JSON_Object.INEQUALITY_TYPE;
import eu.vital.filtering.util.FilteringProperties;

public class DMSManager {

	private static Logger logger=Logger.getLogger(DMSManager.class);
	private String endpoint, cookie;
	private final String OBSERVED_BY="http://purl.oclc.org/NET/ssnx/ssn#observedBy";
	private final String OBSERVATION_PROPERTY="http://purl.oclc.org/NET/ssnx/ssn#observationProperty";
	private final String VALUE="http://vital-iot.eu/ontology/ns/value";
	private final String KEY_TIME="http://purl.oclc.org/NET/ssnx/ssn#observationResultTime";
	private final String TIME="http://www.w3.org/2006/time#inXSDDateTime";
	private final String LATITUDE_KEY="dul:hasLocation.geo:lat";
	private final String LONGITUDE_KEY="dul:hasLocation.geo:long";
	private final String HAS_VALUE="http://purl.oclc.org/NET/ssnx/ssn#hasValue";
	private final String OBSERVATION_RESULT="http://purl.oclc.org/NET/ssnx/ssn#observationResult";
	//TODO replace latitude and longitude keys with actual used in documents
	//	private final String LATITUDE_KEY="hasLastKnownLocation.geo:lat";
	//	private final String LONGITUDE_KEY="hasLastKnownLocation.geo:long";
	private DateFormat formatter;

	public enum DMS_Index{
		SYSTEM("/querySystem"),
		SERVICE("/queryService"),
		SENSOR("/querySensor"),
		OBSERVATION("/queryObservation");

		private final String address;       

		private DMS_Index(String s) {
			address = s;
		}

		public String getAddress() {
			return this.address;
		}

	}

	public DMSManager(DMS_Index index, String cookie){
		FilteringProperties props=new FilteringProperties();
		String DMS_Address=props.getProperty(FilteringProperties.DMS_ENDPOINT_ADDRESS);
		this.endpoint=DMS_Address+index.getAddress();
		this.cookie=cookie;
		formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
	}

	public LinkedList<JSONObject> getObservationsById(String id, String ObservationProperty, Date from, Date to, INEQUALITY_TYPE inequality, double value){
		
		
	JSONObject and_root=new JSONObject();
	JSONArray mainArray=new JSONArray();
	
	JSONObject IDObject = new JSONObject();
	JSONArray arrayId = new JSONArray();
	JSONObject ValueIDObject = new JSONObject();
	ValueIDObject.put("@value", id);
	arrayId.add(ValueIDObject);
	IDObject.put(OBSERVED_BY, arrayId);
	
	mainArray.add(IDObject);
	
	JSONObject PropertyObject = new JSONObject();
	JSONArray arrayPropertyId = new JSONArray();
	JSONObject ValuePropertyObject = new JSONObject();
	JSONArray arrayValue = new JSONArray();
	
	arrayValue.add(ObservationProperty);
	ValuePropertyObject.put("@type", arrayValue);
	
	arrayPropertyId.add(ValuePropertyObject);
	PropertyObject.put(OBSERVATION_PROPERTY, arrayPropertyId);
		
	mainArray.add(PropertyObject);
	
	String finalTimeInterval = null;
	
	if(from !=null && to!=null){
		String TimeInterval = "{\"$gt\":\" "+formatter.format(from)+"\",\"$lt\": \""+formatter.format(to)+"\"}";
			
		JSONObject cleaned = (JSONObject) JSONValue.parse(TimeInterval);
		
		JSONObject TimeIntervalObject = new JSONObject();
		TimeIntervalObject.put("@value", cleaned);
				
		JSONObject MatchTimeInterval = new JSONObject();
		MatchTimeInterval.put("$elemMatch", TimeIntervalObject);
								
		JSONObject XSDTimeObject = new JSONObject();
		XSDTimeObject.put(TIME, MatchTimeInterval);
		
		JSONObject MatchXSDTimeObject = new JSONObject();
		MatchXSDTimeObject.put("$elemMatch", XSDTimeObject);
		
		JSONObject TimeObject = new JSONObject();
		TimeObject.put(KEY_TIME, MatchXSDTimeObject);
		
		finalTimeInterval = TimeObject.toJSONString();
		//mainArray.add(TimeObject);
		
	}
	String ValueMeasurement = "{\"$"+inequality+"\":"+value +"}";
	JSONObject cleanedValue = (JSONObject) JSONValue.parse(ValueMeasurement);

	JSONObject valueObject = new JSONObject();
	valueObject.put("@value", cleanedValue);
	
	JSONObject MatchvalueObject = new JSONObject();
	MatchvalueObject.put("$elemMatch", valueObject);
	
	JSONObject ValueMeasured = new JSONObject();
	ValueMeasured.put(VALUE, MatchvalueObject);
	
	JSONObject MatchHasValue = new JSONObject();
	MatchHasValue.put("$elemMatch", ValueMeasured);
	
	JSONObject HasValue = new JSONObject();
	HasValue.put(HAS_VALUE, MatchHasValue);
	
	JSONObject MatchObservationResult = new JSONObject();
	MatchObservationResult.put("$elemMatch", HasValue);
	
	JSONObject ObservationResult = new JSONObject();
	ObservationResult.put(OBSERVATION_RESULT, MatchObservationResult);
	
	
	String finalQuery = IDObject.toJSONString()+PropertyObject.toJSONString()+finalTimeInterval+ObservationResult.toJSONString();

	logger.debug("query Sent: "+finalQuery);

	return queryDMS(endpoint, removeExtraQuotes(finalQuery), cookie);

}





	public LinkedList<JSONObject> getObservationsByPosition(MapSelectionSquare region, String observationProperty, Date from, Date to, INEQUALITY_TYPE inequality, double value){
		JSONObject and_root=new JSONObject();
		JSONArray and_terms=new JSONArray();

		JSONObject minLat=new JSONObject();
		minLat.put(LATITUDE_KEY, createDoubleInequality(Double.parseDouble(region.getMinLatitude()), INEQUALITY_TYPE.gt));
		and_terms.add(minLat);

		JSONObject maxLat=new JSONObject();
		maxLat.put(LATITUDE_KEY, createDoubleInequality(Double.parseDouble(region.getMaxLatitude()), INEQUALITY_TYPE.lt));
		and_terms.add(maxLat);

		JSONObject minLong=new JSONObject();
		minLong.put(LONGITUDE_KEY, createDoubleInequality(Double.parseDouble(region.getMinLongitude()), INEQUALITY_TYPE.gt));
		and_terms.add(minLong);

		JSONObject maxLong=new JSONObject();
		maxLong.put(LONGITUDE_KEY, createDoubleInequality(Double.parseDouble(region.getMaxLongitude()), INEQUALITY_TYPE.lt));
		and_terms.add(maxLong);

		JSONObject measured_value=new JSONObject();
		measured_value.put(VALUE, createDoubleInequality(value, inequality));
		and_terms.add(measured_value);

		if(from !=null && to!=null){
			JSONObject timeLowerBound=new JSONObject();
			timeLowerBound.put(TIME, createStringInequality(formatter.format(from), INEQUALITY_TYPE.gt));
			and_terms.add(timeLowerBound);

			JSONObject timeUpperBound=new JSONObject();
			timeUpperBound.put(TIME, createStringInequality(formatter.format(to), INEQUALITY_TYPE.lt));
			and_terms.add(timeUpperBound);
		}

		and_root.put("$and", and_terms);

		logger.debug("query Sent: "+removeExtraQuotes(and_root.toJSONString()));

		return queryDMS(endpoint, removeExtraQuotes(and_root.toJSONString()), cookie);
	}

	public LinkedList<JSONObject> getObservationsInTimeRange(String id, String ObservationProperty, Date from, Date to){
		JSONObject and_root=new JSONObject();
		JSONArray and_terms=new JSONArray();

		JSONObject source=new JSONObject();
		source.put(OBSERVED_BY, id);
		and_terms.add(source);

		JSONObject property=new JSONObject();
		property.put(OBSERVATION_PROPERTY, ObservationProperty);
		and_terms.add(property);

		JSONObject timeLowerBound=new JSONObject();
		timeLowerBound.put(TIME, createStringInequality(formatter.format(from), INEQUALITY_TYPE.gt));
		and_terms.add(timeLowerBound);

		JSONObject timeUpperBound=new JSONObject();
		timeUpperBound.put(TIME, createStringInequality(formatter.format(to), INEQUALITY_TYPE.lt));
		and_terms.add(timeUpperBound);


		and_root.put("$and", and_terms);

		logger.debug("query Sent: "+removeExtraQuotes(and_root.toJSONString()));

		return queryDMS(endpoint, removeExtraQuotes(and_root.toJSONString()), cookie);



	}


	private JSONObject createDoubleInequality(double value, INEQUALITY_TYPE inequality){
		JSONObject node=new JSONObject();
		node.put("$"+inequality, value);
		return node;
	}

	private JSONObject createStringInequality(String value, INEQUALITY_TYPE inequality){
		JSONObject node=new JSONObject();
		node.put("$"+inequality, value);
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


	private LinkedList<JSONObject> queryDMS(String DMS_endpoint, String postObject, String cookie){
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
		}
		catch(Exception e){
			logger.error(e.toString());
			throw new ConnectionErrorException("Error in connection with DMS");
		}finally{
			if(connectionDMS != null) {
				connectionDMS.disconnect(); 
			}
		}
	}


}
