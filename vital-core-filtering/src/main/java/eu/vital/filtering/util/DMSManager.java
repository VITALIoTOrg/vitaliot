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
import org.json.simple.parser.JSONParser;

import eu.vital.filtering.exception.ConnectionErrorException;
import eu.vital.filtering.inputJSON.Threshold_JSON_Object.INEQUALITY_TYPE;
import eu.vital.filtering.util.FilteringProperties.FilteringProperty;

public class DMSManager {

	private static Logger logger=Logger.getLogger(DMSManager.class);
	private String endpoint, cookie;
	private final String OBSERVED_BY="ssn:observedBy";
	private final String OBSERVATION_PROPERTY="ssn:observationProperty.type";
	private final String VALUE="ssn:observationResult.ssn:hasValue.value";
	private final String TIME="ssn:observationResultTime.time:inXSDDateTime";
	private final String LATITUDE_KEY="dul:hasLocation.geo:lat";
	private final String LONGITUDE_KEY="dul:hasLocation.geo:long";
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
		FilteringProperties propertires=new FilteringProperties();
		String DMS_Address=propertires.getProperty(FilteringProperty.DMS_ENDPOINT_ADDRESS);
		this.endpoint=DMS_Address+index.getAddress();
		this.cookie=cookie;
		formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
	}


	public LinkedList<JSONObject> getObservationsById(String id, String ObservationProperty, Date from, Date to, INEQUALITY_TYPE inequality, double value){
		JSONObject and_root=new JSONObject();
		JSONArray and_terms=new JSONArray();

		JSONObject source=new JSONObject();
		source.put(OBSERVED_BY, id);
		and_terms.add(source);

		JSONObject property=new JSONObject();
		property.put(OBSERVATION_PROPERTY, ObservationProperty);
		and_terms.add(property);

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
