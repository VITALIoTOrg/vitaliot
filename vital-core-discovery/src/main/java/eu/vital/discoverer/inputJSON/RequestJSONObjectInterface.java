/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.inputJSON;

import org.json.simple.JSONObject;

public interface RequestJSONObjectInterface {

	// Marker interface to identify all Java objects used to parse JSON Objects in POST requests
	public void setIncludedKeys(JSONObject inputObject);

}
