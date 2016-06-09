

package eu.vital.discoverer.query;

import java.io.IOException;
import java.util.LinkedList;

import org.jboss.resteasy.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.vital.discoverer.exception.DiscoveryApplicationException;
import eu.vital.discoverer.inputJSON.Discover_Services_JSON_Object;
import eu.vital.discoverer.util.DMSManager;
import eu.vital.discoverer.util.DMSManager.DMS_Index;

public class ServicesQuery extends DiscoverQuery{

	final static Logger logger=Logger.getLogger(ServicesQuery.class);
	private Discover_Services_JSON_Object inputObject;
	private final String TYPE_KEY="type";
	private final String SYSTEM_KEY="system";

	public ServicesQuery() {
		super();
	}

	@Override
	public void setInputJSON(JSONObject inputJSON) {
		this.inputJSON=inputJSON;

		ObjectMapper mapper = new ObjectMapper();
		try {
			inputObject = mapper.readValue(inputJSON.toString(), Discover_Services_JSON_Object.class);
		} catch (JsonParseException e) {
			logger.error("Error unmarshaling input JSON object",e);
			throw new DiscoveryApplicationException();

		} catch (JsonMappingException e) {
			logger.error("Error mapping input JSON object",e);
			throw new DiscoveryApplicationException();

		} catch (IOException e) {
			logger.error("IO Error on input JSON object",e);
			throw new DiscoveryApplicationException();
		}
		inputObject.setIncludedKeys(inputJSON);
	}


	@Override
	public void executeQuery() {

		DMSManager serviceManager=new DMSManager(DMS_Index.SERVICE,this.cookie);
		DMSManager systemManager=new DMSManager(DMS_Index.SYSTEM, cookie);
		LinkedList<JSONObject> result = null;
		if(this.inputObject.hasType()){
			result=this.combineResults(result, serviceManager.getByField("@"+TYPE_KEY, inputObject.getType()));
		}

		if(this.inputObject.hasSystem()){
			// Retrieve system description and service list
			JSONObject system=systemManager.getById(inputObject.getSystem());

			if(system==null){
				return;
			}
			JSONArray serviceList=(JSONArray)system.get("services");
			// extract service ids
			LinkedList<String> serviceIds=new LinkedList<String>();
			for(int i=0;i<serviceList.size();i++){
				serviceIds.addLast(serviceList.get(i).toString());
			}
			result=this.combineResults(result, serviceManager.getByIdList(serviceIds));
		}
		this.result_in_JSON=result;
	}


	@Override
	public boolean checkJSONInput(JSONObject object) {
		if(object.keySet().size()==0)
			return false;
		else
			return true;
	}
}
