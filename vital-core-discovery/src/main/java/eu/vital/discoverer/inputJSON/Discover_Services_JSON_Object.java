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
