package eu.vital.filtering.query;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import eu.vital.filtering.util.FilteringProperties;

public abstract class FilteringQuery {

	final static Logger logger=Logger.getLogger(FilteringQuery.class);

	private final String PROPERTIES_FILENAME="vital-properties.xml";

	protected JSONObject inputJSON;
	protected LinkedList<JSONObject> result_in_JSON;
	protected FilteringProperties properties;
	protected String cookie;
	protected String DMS_ADDRESS, DMS_PORT;
	
	public FilteringQuery() {

		//this.properties=new FilteringProperties();
		FilteringProperties props=new FilteringProperties();

		//this.DMS_ADDRESS="http://"+props.getProperty(DiscoveryProperty.DMS_ENDPOINT_ADDRESS);
		this.DMS_ADDRESS=props.getProperty(FilteringProperties.DMS_ENDPOINT_ADDRESS);
		this.DMS_PORT=props.getProperty(FilteringProperties.DMS_ENDPOINT_PORT);

		result_in_JSON=new LinkedList<JSONObject>();
		inputJSON=null;

	}
	
	public void SetCookie(String cookie){
		this.cookie=cookie;
	}
	
	public abstract void setInputJSON(JSONObject inputJSON);
	
	public abstract void executeQuery();
	//returns a linked list containing json objects to return as response of the wen service
	public LinkedList<JSONObject> getQueryResult(){
		return this.result_in_JSON;
	}
	//Verifies if the received JSON object is compatible with the expected format
	public abstract boolean isInputAppropriate();
}
