package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.service.ObservationService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ObservationAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ObservationService observationService;

	public Map get(String sensorUri, String observationType) throws Exception {

		ArrayNode observationNode = observationService.fetchObservation(sensorUri, observationType);
		if (observationNode.size() > 0) {
			Map observation = objectMapper.convertValue(observationNode.get(0), Map.class);
			return observation;
		}

		return new HashMap();
	}

}
