package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.service.ObservationService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ObservationAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ObservationService observationService;

	public Map get(String sensorUri, String observationType) throws Exception {

		ArrayNode observationNode = observationService.fetchLatestBySensorAndType(sensorUri, observationType);
		if (observationNode.size() > 0) {
			Map observation = objectMapper.convertValue(observationNode.get(0), Map.class);
			return observation;
		}

		return new HashMap();
	}

	public Collection<Map> fetchAll() throws Exception {

		ArrayNode observationArray = observationService.fetchAll();

		Collection<Map> observationList = new ArrayList<Map>();
		for (JsonNode sensorNode : observationArray) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			observationList.add(sensor);
		}
		return observationList;
	}

	public Collection<Map> fetchAllByType(String observationType) throws Exception {

		ArrayNode observationArray = observationService.fetchAllByType(observationType);

		Collection<Map> observationList = new ArrayList<Map>();
		for (JsonNode sensorNode : observationArray) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			observationList.add(sensor);
		}
		return observationList;
	}

	public Collection<Map> fetchAllBySensorAndType(String sensorUri, String observationType) throws Exception {

		ArrayNode observationArray = observationService.fetchAllBySensorAndType(sensorUri, observationType);

		Collection<Map> observationList = new ArrayList<Map>();
		for (JsonNode sensorNode : observationArray) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			observationList.add(sensor);
		}
		return observationList;
	}

}
