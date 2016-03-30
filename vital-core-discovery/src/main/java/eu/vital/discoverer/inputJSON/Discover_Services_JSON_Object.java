/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-02-26T09:52:37+01:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:09+02:00
*/



package eu.vital.discoverer.inputJSON;

import org.json.simple.JSONObject;

public class Discover_Services_JSON_Object implements RequestJSONObjectInterface{

	private String type, system;
	private boolean hasType, hasSystem;

	public void setType(String type){
		this.type=type;
	}

	public String getType(){
		return this.type;
	}

	public void setSystem(String system){
		this.system=system;
	}

	public String getSystem(){
		return this.system;
	}

	public void setIncludedKeys(JSONObject input){
		this.hasType=input.containsKey("type");
		this.hasSystem=input.containsKey("system");
	}

	public boolean hasType(){
		return this.hasType;
	}

	public boolean hasSystem(){
		return this.hasSystem;
	}

}
