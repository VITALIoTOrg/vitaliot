package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.DocumentManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

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
		// Connect to ES and retrieve result
//		QueryBuilder query = QueryBuilders.termQuery("dul:isPartOf", systemJSON.get("id").asText());
//		ArrayNode documents = documentManager.search(SERVICE_TYPE, query);
//		return documents;
		return list();
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
