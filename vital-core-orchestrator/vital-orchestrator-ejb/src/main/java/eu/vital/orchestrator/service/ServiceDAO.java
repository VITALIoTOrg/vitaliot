package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.OrchestratorStorage;
import org.bson.conversions.Bson;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

@Stateless
public class ServiceDAO {

	@Inject
	private Logger log;

	@Inject
	private OrchestratorStorage orchestratorStorage;

	@Inject
	ObjectMapper objectMapper;

	public ArrayNode list() throws Exception {
		// Connect to ES and retrieve result
		ArrayNode documents = orchestratorStorage.getList(OrchestratorStorage.DOCUMENT_TYPE.SERVICE.toString());
		return documents;
	}

	public ArrayNode searchBySystem(JsonNode systemJSON) throws Exception {
		// Connect to ES and retrieve result
		Bson query = eq("system", systemJSON.get("@id").asText());
		ArrayNode documents = orchestratorStorage.search(OrchestratorStorage.DOCUMENT_TYPE.SERVICE.toString(), query);
		return documents;
	}

	public ObjectNode get(String uri) throws Exception {
		// Connect to ES and retrieve result
		ObjectNode document = orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.SERVICE.toString(), uri);
		return document;
	}

	public ObjectNode save(ObjectNode serviceData) throws Exception {
		String uri = serviceData.get("@id").asText();
		orchestratorStorage.update(OrchestratorStorage.DOCUMENT_TYPE.SERVICE.toString(), uri, serviceData);
		return get(uri);
	}

}
