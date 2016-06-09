
package eu.vital.discoverer.query;

import java.io.IOException;
import java.util.LinkedList;
import org.jboss.resteasy.logging.Logger;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vital.discoverer.exception.DiscoveryApplicationException;
import eu.vital.discoverer.inputJSON.Discover_Systems_JSON_Object;
import eu.vital.discoverer.util.DMSManager;
import eu.vital.discoverer.util.DMSManager.DMS_Index;

public class SystemsQuery extends DiscoverQuery {

	final static Logger logger=Logger.getLogger(SystemsQuery.class);
	private final String TYPE="type";
	private final String SERVICE_AREA="http://vital-iot.eu/ontology/ns/serviceArea";
	private final String SERVICE_AREA_id="id";

	private Discover_Systems_JSON_Object inputObject;

	public SystemsQuery() {
		super();
	}

	@Override
	public void setInputJSON(JSONObject inputJSON) {
		this.inputJSON=inputJSON;

		ObjectMapper mapper = new ObjectMapper();
		try {
			inputObject = mapper.readValue(inputJSON.toString(), Discover_Systems_JSON_Object.class);
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
		DMSManager manager=new DMSManager(DMS_Index.SYSTEM, this.cookie);
		LinkedList<JSONObject> result = null;
		if(this.inputObject.hasType()){
			result=this.combineResults(result, manager.getByField("@"+TYPE, this.inputObject.getType()));
			//result=this.combineResults(result, manager.getByField(TYPE, this.inputObject.getType()));
		}
		if(this.inputObject.hasServiseArea()){
			//result=this.combineResults(result, manager.getByField(SERVICE_AREA+"@"+SERVICE_AREA_id, this.inputObject.getServiceArea()));
			result=this.combineResults(result, manager.getBySubField(SERVICE_AREA,"@"+SERVICE_AREA_id, this.inputObject.getServiceArea()));
			//result=this.combineResults(result, manager.getByField(SERVICE_AREA, this.inputObject.getServiceArea()));
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
