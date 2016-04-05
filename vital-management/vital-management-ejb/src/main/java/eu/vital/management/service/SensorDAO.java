package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.storage.DocumentManager;
import eu.vital.management.util.OntologyParser;
import eu.vital.management.util.VitalClient;
import org.bson.conversions.Bson;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
public class SensorDAO {

	@Inject
	private Logger log;

	@Inject
	private DocumentManager documentManager;

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
		return documentManager.getList(DocumentManager.DOCUMENT_TYPE.SENSOR.toString());
	}

	public ObjectNode get(String uri) throws Exception {
		// Connect to ES and retrieve result
		ObjectNode document = documentManager.get(DocumentManager.DOCUMENT_TYPE.SENSOR.toString(), uri);
		return document;
	}

	public ArrayNode search(Bson query) throws Exception {
		// Connect to ES and retrieve result
		ArrayNode document = documentManager.search(DocumentManager.DOCUMENT_TYPE.SENSOR.toString(), query);
		return document;
	}

	public JsonNode save(JsonNode ico) throws Exception {
		String uri = ico.get("@id").asText();
		documentManager.update(DocumentManager.DOCUMENT_TYPE.SENSOR.toString(), uri, ico);
		return get(uri);
	}

	public ObjectNode fetchStatus(String sensorId) throws Exception {
		// Connect to ES and retrieve result

		ObjectNode sensor = get(sensorId);
		ObjectNode system = systemDAO.get(sensor.get("system").asText());

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

		ArrayNode result = (ArrayNode) vitalClient.doPost(operationURL, operationInput);

		return (ObjectNode) result.get(0);
	}

}
