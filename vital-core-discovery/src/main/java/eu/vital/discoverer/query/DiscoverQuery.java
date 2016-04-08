/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.query;

import java.util.LinkedList;

import org.jboss.resteasy.logging.Logger;
import org.json.simple.JSONObject;

import eu.vital.discoverer.util.DiscoveryProperties;


public abstract class DiscoverQuery {

	final static Logger logger=Logger.getLogger(DiscoverQuery.class);

	private final String PROPERTIES_FILENAME="vital-properties.xml";

	protected QueryTypeEnum queryType;
	protected JSONObject inputJSON;
	protected String sparqlQuery;
	protected LinkedList<JSONObject> result_in_JSON;
	protected String DMS_ADDRESS, DMS_PORT;
	protected String cookie;

	public DiscoverQuery() {

		DiscoveryProperties props=new DiscoveryProperties();

		//this.DMS_ADDRESS="http://"+props.getProperty(DiscoveryProperty.DMS_ENDPOINT_ADDRESS);
		this.DMS_ADDRESS=props.getProperty(DiscoveryProperties.DMS_ENDPOINT_ADDRESS);
		this.DMS_PORT=props.getProperty(DiscoveryProperties.DMS_ENDPOINT_PORT);
		result_in_JSON=new LinkedList<JSONObject>();
	}

	public abstract void setInputJSON(JSONObject inputJSON);

	public void setCookie(String cookie){
		this.cookie=cookie;
	}

	// Executes the query according to the JSON object received in input
	public abstract void executeQuery();
	//returns a linked list containing json objects to return as response of the wen service
	public LinkedList<JSONObject> getQueryResult(){
		return this.result_in_JSON;
	}
	//Verifies if the received JSON object is compatible with the expected format
	public abstract boolean checkJSONInput(JSONObject object);

	protected LinkedList<JSONObject> combineResults(LinkedList<JSONObject> resultList, LinkedList<JSONObject> partialResult){
		if(resultList!=null){
			resultList.retainAll(partialResult);
		}else{
			resultList=partialResult;
		}
		return resultList;
	}

}
