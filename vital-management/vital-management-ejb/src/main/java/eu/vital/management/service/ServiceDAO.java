package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.storage.DocumentManager;
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
	private DocumentManager documentManager;

	@Inject
	ObjectMapper objectMapper;


	public ArrayNode list() throws Exception {
		// Connect to ES and retrieve result
		ArrayNode documents = documentManager.getList(DocumentManager.DOCUMENT_TYPE.SERVICE.toString());
		return documents;
	}

	public ArrayNode searchBySystem(JsonNode systemJSON) throws Exception {
		// Connect to ES and retrieve resultdep-
		Bson query = eq("system", systemJSON.get("@id").asText());
		ArrayNode documents = documentManager.search(DocumentManager.DOCUMENT_TYPE.SERVICE.toString(), query);

		return documents;
	}

	public ObjectNode get(String uri) throws Exception {
		// Connect to ES and retrieve result
		ObjectNode document = documentManager.get(DocumentManager.DOCUMENT_TYPE.SERVICE.toString(), uri);
		return document;
	}

	public ObjectNode save(ObjectNode serviceData) throws Exception {
		String uri = serviceData.get("@id").asText();
		documentManager.update(DocumentManager.DOCUMENT_TYPE.SERVICE.toString(), uri, serviceData);
		return get(uri);
	}

}
