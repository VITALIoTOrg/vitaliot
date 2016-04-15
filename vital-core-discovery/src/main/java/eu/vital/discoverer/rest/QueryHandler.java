/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.rest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONObject;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;

public class QueryHandler {

	private static final String dms_endpoint="http://vmvital02.deri.ie:8000/sparql";
	private static Logger logger=Logger.getLogger(QueryHandler.class);

	public static JSONObject queryAllSystem(){

		String query ="PREFIX vital:<http://vital-iot.eu/ontology/ns/>"
				+ "SELECT * WHERE { ?s ?p vital:IotSystem }";
//		+ "SELECT * WHERE { ?s ?p ?o }";
		try{

			//build an object to perform the request
			ClientRequest request = new ClientRequest(dms_endpoint);
			request.accept("application/json");
			logger.info("Systems queryed\n");
			//put options in the JSON object for the request
			JSONObject obj = new JSONObject();
			obj.put("query", query);
			obj.put("returnType", "RDF");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			String input =  out.toString();

			request.body("application/json", input);
			//Perform request
			ClientResponse<String> response = request.post(String.class);
			//analyze results
			StringBuilder resultBuilder=new StringBuilder();
			ResultSet results = ResultSetFactory.fromJSON(new ByteArrayInputStream(response.getEntity().getBytes()));
			LinkedList<JSONObject> resultList=new LinkedList<JSONObject>();
			while (results.hasNext()) {
			      QuerySolution solution = results.next();
			      JSONObject sol=new JSONObject();
			      sol.put("IoTSystem",solution.get("?s").asResource().toString() );
			      resultList.add(sol);
			}

			JSONObject resp=new JSONObject();
			resp.put("@context", "http://vital-iot.org/contexts/system.jsonld");
			resp.put("systems", resultList);
			return resp;


		}
		catch(Exception e){
			return new JSONObject();
		}

	}

}
