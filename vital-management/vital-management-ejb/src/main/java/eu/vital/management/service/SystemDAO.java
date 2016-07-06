package eu.vital.management.service;

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
public class SystemDAO {

	@Inject
	private Logger log;

	@Inject
	private DocumentManager documentManager;

	@Inject
	ObjectMapper objectMapper;

	@Inject
	ServiceDAO serviceDAO;

	@Inject
	VitalClient vitalClient;

	public ArrayNode list() throws Exception {
		// Connect to ES and retrieve result
		ArrayNode documents = documentManager.getList(DocumentManager.DOCUMENT_TYPE.SYSTEM.toString());
		return documents;
	}

	public ArrayNode search(Bson query) throws Exception {
		// Connect to ES and retrieve result
		ArrayNode document = documentManager.search(DocumentManager.DOCUMENT_TYPE.SYSTEM.toString(), query);
		return document;
	}

	public ObjectNode get(String systemId) throws Exception {
		// Connect to ES and retrieve result
		ObjectNode document = documentManager.get(DocumentManager.DOCUMENT_TYPE.SYSTEM.toString(), systemId);
		return document;
	}

	public ObjectNode save(ObjectNode systemData) throws Exception {
		String systemId = systemData.get("@id").asText();
		documentManager.update(DocumentManager.DOCUMENT_TYPE.SYSTEM.toString(), systemId, systemData);
		return get(systemId);
	}

	public ObjectNode fetchStatus(String systemId) throws Exception {
		// Connect to ES and retrieve result

		ObjectNode system = get(systemId);
		ArrayNode systemServices = serviceDAO.searchBySystem(system);
		// Find ObservationService service, get URL:
		String operationURL = OntologyParser.findOperationURL(
				"http://vital-iot.eu/ontology/ns/MonitoringService",
				"http://vital-iot.eu/ontology/ns/GetSystemStatus",
				systemServices);

		// Connect to this URL and fetch all sensors:
		ObjectNode operationInput = objectMapper.createObjectNode();
		ObjectNode result = (ObjectNode) vitalClient.doPost(operationURL, operationInput);

		return result;
	}
}
