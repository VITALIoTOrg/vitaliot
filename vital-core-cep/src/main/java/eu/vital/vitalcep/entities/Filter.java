/* 
 *	CEP REST Interface
 *	Copyright (c) Atos S.A.
 *	Research & Innovation - Internet of Everything Lab
 *	All Rights Reserved.
 *	
 *	ATOS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package eu.vital.vitalcep.entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;
import eu.vital.vitalcep.entities.dolceHandler.statements.ExternalDolceStatement;

// TODO: Auto-generated Javadoc
/**
 * The Class Filter.
 * 
 * This Class is a dolce specification container
 * 
 */
public class Filter {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(Filter.class);

        /** The id. */
	private String id;
        
        /** The URI. */
	private String uri;
        
         /** The @context. */
	private String context;
        
        /** The name. */
	private String name;        
        
        /** The type. */
	private String type;
        
        /** The description. */
	private String description;
              
        /** The name. */
	private String observes ; // todo:observes
        
        /** The source. */
	private JSONArray source;
	//TODO: see how to builds a dolceSpecification from parts or proper class
	/** The dolceSpecification. */
	private DolceSpecification dolceSpecification;
        
      
	
	// constructor form json as String
	/**
	 * Instantiates a new filter.
	 *
	 * @param json the json in string format
	 */
	public Filter(String json) {
		JSONObject jo = new JSONObject(json);

		//JSONObject dsjo = jo.getJSONObject("filter");

		// get id
		id = jo.getString("id");
                
                // get uri
		uri = jo.getString("uri");
                
                // get name
		name = jo.getString("name");
                
                 // get name
		context = jo.getString("@context");

		// get observes
		//JSONArray obsJA = dsjo.getJSONArray("observer");
		//for (int i = 0; i < obsJA.length(); i++) {
		//		//observes.put(extDS.id, extDS);
                //    observer = dsjo.getString("name");
		//}

		// get description
		description = jo.getString("description");
                
                // get source
		source = new JSONArray(jo.getString("source"));
		
		// get type
		type = jo.getString("type");
                
		// get description
		dolceSpecification = new DolceSpecification(
                         jo.getString("dolceSpecification"));
                
		logger.debug("mod or new filter: \n" + json.toString());
		logger.debug("read: \n" + toString());
	}


	// return a JSONObject with the Filter
	/**
	 * Gets the Filter as json obj.
	 *
	 * @return the json obj
	 */
	public JSONObject getJsonObj() {

		
		JSONObject mainObject = new JSONObject();
		JSONObject dsObj = new JSONObject();

		dsObj.put("id", id);
                dsObj.put("uri", uri);
                dsObj.put("@context", context);
		dsObj.put("description", description);
		dsObj.put("dolceSpecification", dolceSpecification);
		dsObj.put("name", name);
               // dsObj.put("observers", observers);//TODO: observer complexity
                dsObj.put("source", source);
                dsObj.put("type", type);

		mainObject.put("filter", dsObj);

		return mainObject;
	}

}
