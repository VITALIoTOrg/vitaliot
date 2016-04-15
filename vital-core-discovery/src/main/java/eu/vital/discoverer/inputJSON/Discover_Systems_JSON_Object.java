/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.inputJSON;

import org.json.simple.JSONObject;

public class Discover_Systems_JSON_Object implements RequestJSONObjectInterface{

	private String type, serviceArea;
	private boolean hasType, hasServiceArea;

	public String getType() {
		return type;
	}

	public void setType(String type){
		this.type=type;
	}

	public String getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(String serviceArea) {
		this.serviceArea = serviceArea;
	}

	public boolean hasType(){
		return this.hasType;
	}

	public boolean hasServiseArea(){
		return this.hasServiceArea;
	}

	public void setIncludedKeys(JSONObject inputObject) {
		this.hasServiceArea=inputObject.containsKey("serviceArea");
		this.hasType=inputObject.containsKey("type");
	}

}
