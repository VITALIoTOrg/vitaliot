package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.OrchestratorStorage;
import eu.vital.orchestrator.util.OntologyParser;
import eu.vital.orchestrator.util.VitalClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ObservationService {

	@Inject
	private Logger log;

	@Inject
	private OrchestratorStorage orchestratorStorage;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private SystemDAO systemDAO;

	@Inject
	private SensorDAO sensorDAO;

	@Inject
	private ServiceDAO serviceDAO;

	@Inject
	private VitalClient vitalClient;

	public ArrayNode fetchLatestBySensorAndType(String sensorURI, String observationType) throws Exception {

		try {
			// Connect to ES and retrieve result
			JsonNode sensor = sensorDAO.get(sensorURI);

			String systemURI = sensor.get("system").asText();
			ObjectNode system = systemDAO.get(systemURI);
			ArrayNode systemServices = serviceDAO.searchBySystem(system);

			// Find ObservationService service, get URL:
			String operationURL = OntologyParser.findOperationURL(
					"http://vital-iot.eu/ontology/ns/ObservationService",
					"http://vital-iot.eu/ontology/ns/GetObservations",
					systemServices);

			// Connect to this URL and fetch all sensors:
			ObjectNode operationInput = objectMapper.createObjectNode();
			ArrayNode sensorsArray = objectMapper.createArrayNode();
			sensorsArray.add(sensorURI);
			operationInput.put("sensor", sensorsArray);
			operationInput.put("property", observationType);
			JsonNode result = vitalClient.doPost(operationURL, operationInput);

			if (result.isArray()) {
				return (ArrayNode) result;
			} else {
				return objectMapper.createArrayNode().add(result);
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			return objectMapper.createArrayNode();
		}
	}

}
