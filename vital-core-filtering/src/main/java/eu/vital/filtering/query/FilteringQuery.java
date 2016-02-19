package eu.vital.filtering.query;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import eu.vital.filtering.util.FilteringProperties;

public abstract class FilteringQuery {

	final static Logger logger=Logger.getLogger(FilteringQuery.class);
	protected JSONObject inputJSON;
	protected LinkedList<JSONObject> result_in_JSON;
	protected FilteringProperties properties;
	protected String cookie;
	
	
	public FilteringQuery() {
		result_in_JSON=new LinkedList<JSONObject>();
		inputJSON=null;
		this.properties=new FilteringProperties();
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
