package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.OrchestratorStorage;
import eu.vital.orchestrator.util.OntologyParser;
import eu.vital.orchestrator.util.VitalClient;
import org.bson.conversions.Bson;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
public class SensorDAO {

	@Inject
	private Logger log;

	@Inject
	private OrchestratorStorage orchestratorStorage;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	SystemDAO systemDAO;

	@Inject
	ServiceDAO serviceDAO;

	@Inject
	VitalClient vitalClient;

	public ArrayNode list() throws Exception {
		// Connect to ES and retrieve result
		return orchestratorStorage.getList(OrchestratorStorage.DOCUMENT_TYPE.SENSOR.toString());
	}

	public ArrayNode search(Bson qb) throws Exception {
		return orchestratorStorage.search(OrchestratorStorage.DOCUMENT_TYPE.SENSOR.toString(), qb);
	}

	public ObjectNode get(String uri) throws Exception {
		// Connect to ES and retrieve result
		ObjectNode document = orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.SENSOR.toString(), uri);
		return document;
	}

	public JsonNode save(JsonNode ico) throws Exception {
		String uri = ico.get("@id").asText();
		orchestratorStorage.update(OrchestratorStorage.DOCUMENT_TYPE.SENSOR.toString(), uri, ico);
		return get(uri);
	}

	public ObjectNode fetchStatus(String sensorId) throws Exception {
		// Connect to ES and retrieve result

		ObjectNode system = get(sensorId);
		ArrayNode systemServices = serviceDAO.searchBySystem(system);
		// Find ObservationService service, get URL:
		String operationURL = OntologyParser.findOperationURL(
				"http://vital-iot.eu/ontology/ns/MonitoringService",
				"http://vital-iot.eu/ontology/ns/GetSensorStatus",
				systemServices);

		// Connect to this URL and fetch all sensors:
		ObjectNode operationInput = objectMapper.createObjectNode();
		ArrayNode ids = objectMapper.createArrayNode();
		ids.add(sensorId);
		operationInput.put("id", ids);

		ObjectNode result = (ObjectNode) vitalClient.doPost(operationURL, operationInput);

		return result;
	}

}
