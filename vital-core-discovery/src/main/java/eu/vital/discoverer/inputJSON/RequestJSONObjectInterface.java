package eu.vital.discoverer.inputJSON;

import org.json.simple.JSONObject;

public interface RequestJSONObjectInterface {

	// Marker interface to identify all Java objects used to parse JSON Objects in POST requests
	public void setIncludedKeys(JSONObject inputObject);

}
