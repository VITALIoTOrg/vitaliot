package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vital.orchestrator.util.VitalClient;
import eu.vital.orchestrator.util.VitalConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anglen on 24/05/16.
 */
public class DmsAdapter {

	@Inject
	private Logger log;

	@Inject
	private VitalClient vitalClient;

	@Inject
	private VitalConfiguration configuration;

	@Inject
	ObjectMapper objectMapper;

	public Collection<Map> query(String resourceType, Map query, Boolean encodeKeys) throws Exception {

		String dmsUrl = configuration.getProperty("vital-core-orchestrator.dms", "http://localhost:8080/");

		if (resourceType.equalsIgnoreCase("system")) {
			dmsUrl += "/querySystem";
		} else if (resourceType.equalsIgnoreCase("service")) {
			dmsUrl += "/queryService";
		} else if (resourceType.equalsIgnoreCase("sensor")) {
			dmsUrl += "/querySensor";
		} else if (resourceType.equalsIgnoreCase("observation")) {
			dmsUrl += "/queryObservation";
		}

		dmsUrl += "?encodeKeys=" + (encodeKeys == null? "true" : encodeKeys);

		// Connect to this URL and fetch all sensors:
		JsonNode dmsResponse = vitalClient.doPost(dmsUrl, objectMapper.valueToTree(query));

		Collection<Map> queryResult = new ArrayList<>();

		if (dmsResponse.isObject()) {
			Map node = objectMapper.convertValue(dmsResponse, Map.class);
			queryResult.add(node);
		} else if (dmsResponse.isArray()) {
			for (JsonNode node : dmsResponse) {
				Map sensor = objectMapper.convertValue(node, Map.class);
				queryResult.add(sensor);
			}
		}
		return queryResult;
	}
}
