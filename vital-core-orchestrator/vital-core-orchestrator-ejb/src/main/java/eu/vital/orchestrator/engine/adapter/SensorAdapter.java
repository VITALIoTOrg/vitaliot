package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.service.SensorDAO;
import org.bson.conversions.Bson;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class SensorAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private SensorDAO sensorDAO;

	public Collection<Map> getList() throws Exception {
		ArrayNode sensors = sensorDAO.list();

		Collection<Map> sensorList = new ArrayList<Map>();
		for (JsonNode sensorNode : sensors) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			sensorList.add(sensor);
		}
		return sensorList;
	}

	public Collection<Map> searchBySensorType(String sensorType) throws Exception {

		Bson query = eq("@type", sensorType);
		ArrayNode sensors = sensorDAO.search(query);

		Collection<Map> sensorList = new ArrayList<Map>();
		for (JsonNode sensorNode : sensors) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			sensorList.add(sensor);
		}
		return sensorList;
	}

	public Collection<Map> searchByObservationType(String observationType) throws Exception {

		Bson query = eq("http://purl\\u002eoclc\\u002eorg/NET/ssnx/ssn#observes.@type", observationType);
		ArrayNode sensors = sensorDAO.search(query);

		Collection<Map> sensorList = new ArrayList<Map>();
		for (JsonNode sensorNode : sensors) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			sensorList.add(sensor);
		}
		return sensorList;
	}

	public Map get(String uri) throws Exception {
		JsonNode sensorNode = sensorDAO.get(uri);
		Map sensor = objectMapper.convertValue(sensorNode, Map.class);
		return sensor;
	}
}
