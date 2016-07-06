package eu.vital.filtering.rest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.vital.filtering.util.FilteringMetadataReader;
import eu.vital.filtering.util.FilteringProperties;
import eu.vital.filtering.util.OntologyUtils;
//import eu.vital.filtering.util.FilteringProperties.FilteringProperty;



@Path("/ppi")
public class PpiImplementation {

	private static Logger logger=Logger.getLogger(PpiImplementation.class);
	private FilteringProperties props;
	private FilteringMetadataReader metadataReader;
	private String FILTERING_BASE_ADDRESS;

	public PpiImplementation() {
		props=new FilteringProperties();
		metadataReader=new FilteringMetadataReader();
		FILTERING_BASE_ADDRESS=props.getProperty(FilteringProperties.FILTERING_ENDPOINT_ADDRESS)+":"+props.getProperty(FilteringProperties.FILTERING_ENDPOINT_PORT);

		//FILTERING_BASE_ADDRESS=props.getProperty(FilteringProperty.FILTERING_ENDPOINT_ADDRESS)+":"+props.getProperty(FilteringProperty.FILTERING_ENDPOINT_PORT);
	}

	@POST
	@Path("/metadata")
	@Produces("application/ld+json")
	public String serviceMetadata() throws IOException{

		String desc=metadataReader.getMetadata(FilteringMetadataReader.MetadataSelector.SYSTEM_METADATA);
		desc=desc.replaceAll("IP_ADDRESS", FILTERING_BASE_ADDRESS);
		return desc;
	}
	
	@POST
	@Path("/service/metadata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public String getServiceMetadata(JSONObject body) throws IOException, ParseException{
		String desc=metadataReader.getMetadata(FilteringMetadataReader.MetadataSelector.SERVICE_METADATA);
		desc=desc.replaceAll("IP_ADDRESS", FILTERING_BASE_ADDRESS);
		
		// return all if no parameter specified
		if(body.isEmpty()){	
			return desc;
		}
		// group services by id and by type
		LinkedList<JSONObject> resultById=new LinkedList<JSONObject>();
		LinkedList<JSONObject> resultByType=new LinkedList<JSONObject>();
		JSONParser parser=new JSONParser();
		ArrayList<JSONObject> metadata=(ArrayList<JSONObject>) parser.parse(desc);
		HashMap<String, JSONObject> mapById=new HashMap<String, JSONObject>();
		HashMap<String, JSONObject> mapByType=new HashMap<String, JSONObject>();
		
		for(JSONObject obj:metadata){
			mapById.put((String)obj.get("id"), obj);
			mapByType.put((String)obj.get("type"), obj);
		}
		
		// select services by received ids
		if(body.containsKey("id")){
			ArrayList<String> idList=(ArrayList<String>) body.get("id");
			for(String id:idList){
				if(mapById.containsKey(id)){
					resultById.addLast(mapById.get(id));
				}
			}
		}
		
		// select services by received types
		if(body.containsKey("type")){
			ArrayList<String> typeList=(ArrayList<String>) body.get("type");
			for(String type:typeList){
				if(mapByType.containsKey(type)){
					resultByType.addLast(mapByType.get(type));
				}
			}
		}
		
		// merge results
		LinkedList<JSONObject> result=resultById;
		resultByType.removeAll(resultById);
		result.addAll(resultByType);
		
		return result.toString();		
		

	}

	@POST
	@Path("/sensor/metadata")
	@Produces("application/ld+json")
	public String getSeensorMetadata() throws IOException{

		String desc=metadataReader.getMetadata(FilteringMetadataReader.MetadataSelector.SENSOR_METADATA);
		desc=desc.replaceAll("IP_ADDRESS", FILTERING_BASE_ADDRESS);
		return desc;

	}

	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public String getSystemStatus() throws IOException{

		DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		String desc=metadataReader.getMetadata(FilteringMetadataReader.MetadataSelector.STATUS_METADATA);
		desc=desc.replaceAll("IP_ADDRESS", FILTERING_BASE_ADDRESS);
		desc=desc.replaceAll("IDENTIFIER", Long.toString(new Date().getTime()));
		desc=desc.replaceAll("EXECUTION_TIME", formatter.format(new Date()));
		return desc;
	}


	@POST
	@Path("service/observation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public String getObservations(JSONObject params) throws IOException{
		if(checkObservationParams(params)){
			DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			String desc=metadataReader.getMetadata(FilteringMetadataReader.MetadataSelector.STATUS_METADATA);
			desc=desc.replaceAll("IP_ADDRESS", FILTERING_BASE_ADDRESS);
			desc=desc.replaceAll("IDENTIFIER", Long.toString(new Date().getTime()));
			desc=desc.replaceAll("EXECUTION_TIME", formatter.format(new Date()));
			return desc;
		}
		else
			return "[]";
	}


	private boolean checkObservationParams(JSONObject params){
		boolean propertyCheck=false,sensorCheck=false;


		if(params.containsKey("property")){
			String property=(String)params.get("property");
			if(OntologyUtils.equals(property, "vital:OperationalState"))
				propertyCheck=true;
		}

		if(params.containsKey("sensor")){
			Object sensorObject=params.get("sensor");
			logger.info("check instance type");
			logger.debug(sensorObject.getClass());
			ArrayList<String> sensorArray=(ArrayList<String>) sensorObject;
			String sensor=sensorArray.get(0);
			if(sensor.equals("http://"+FILTERING_BASE_ADDRESS+"/filtering/ppi/sensor/monitoring"))
				sensorCheck=true;
		}

		return propertyCheck && sensorCheck;
	}
}
