package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.storage.DocumentManager;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
public class ServiceDAO {

	public static final String SERVICE_TYPE = "service";

	@Inject
	private Logger log;

	@Inject
	private DocumentManager documentManager;

	@Inject
	ObjectMapper objectMapper;


	public ArrayNode list() throws Exception {
		// Connect to ES and retrieve result
		ArrayNode documents = documentManager.getList(SERVICE_TYPE);
		return documents;
	}

	public ArrayNode searchBySystem(JsonNode systemJSON) throws Exception {
		// Connect to ES and retrieve resultdep-
		QueryBuilder query = QueryBuilders.termQuery("system", systemJSON.get("@id").asText());
		ArrayNode documents = documentManager.search(SERVICE_TYPE, query);

		return documents;
	}

	public ObjectNode get(String uri) throws Exception {
		// Connect to ES and retrieve result
		ObjectNode document = documentManager.get(SERVICE_TYPE, uri);
		return document;
	}

	public ObjectNode save(ObjectNode serviceData) throws Exception {
		String uri = serviceData.get("@id").asText();
		documentManager.update(SERVICE_TYPE, uri, serviceData);
		return get(uri);
	}

}
