package eu.vital.management.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class VitalClient {

	@Inject
	Logger logger;

	@Inject
	ObjectMapper objectMapper;

	public JsonNode doGet(String url) throws Exception {
		Client client = ClientBuilder.newClient();
		client.register(new BasicAuthenticator("silo", "phier0Sa"));
		logger.info("GET REQUEST: " + url);
		JsonNode jsonNode = client.target(url)
				.request(MediaType.APPLICATION_JSON)
						//.header("Authorization", "Basic c2lsbzpwaGllcjBTYQ==")
				.accept("*")
				.get(JsonNode.class);
		client.close();
		logger.info("GET RESPONSE: " + url + " SUCCESS");
		if (jsonNode == null) {
			return null;
		}
		logger.info("Compact: " + jsonNode.toString());
		jsonNode = expand(jsonNode);
		logger.info("Expanded: " + jsonNode.toString());

		return jsonNode;
	}

	public JsonNode doPost(String url, JsonNode data) throws Exception {
		Client client = ClientBuilder.newClient();
		client.register(new BasicAuthenticator("silo", "phier0Sa"));
		logger.info("POST REQUEST: " + url + " " + data.toString());
		JsonNode jsonNode = client.target(url)
				.request(MediaType.APPLICATION_JSON)
						//.header("Authorization", "Basic c2lsbzpwaGllcjBTYQ==")
				.accept("*")
				.post(Entity.json(data), JsonNode.class);
		client.close();
		logger.info("POST RESPONSE: " + url + " SUCCESS");
		if (jsonNode == null) {
			return null;
		}
		logger.info("Compact: " + jsonNode.toString());
		jsonNode = expand(jsonNode);
		logger.info("Expanded: " + jsonNode.toString());

		return jsonNode;
	}


	private JsonNode expand(JsonNode jsonLD) throws Exception {
		boolean isArray = jsonLD.isArray();
		boolean isObject = jsonLD.isObject();

		if (isArray) {
			return expandArray((ArrayNode) jsonLD);
		}
		if (isObject) {
			return expandObject((ObjectNode) jsonLD);
		}
		return jsonLD;
	}

	private ArrayNode expandArray(ArrayNode jsonLD) throws Exception {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		for (JsonNode objectNode : jsonLD) {
			ObjectNode expandedObjectNode = expandObject((ObjectNode) objectNode);
			arrayNode.add(expandedObjectNode);
		}
		return arrayNode;
	}

	private ObjectNode expandObject(ObjectNode jsonLD) throws Exception {
		if (!jsonLD.has("@context")) {
			// It is JSON not JSON-LD
			return jsonLD;
		}

		Map context = new HashMap();
		JsonLdOptions options = new JsonLdOptions();

		Object jsonObject = JsonUtils.fromString(jsonLD.toString());
		List<Object> result = JsonLdProcessor.expand(jsonObject, options);
		Object compactJSON = JsonLdProcessor.compact(result, context, options);

		// Convert expanded
		String compactJSONString = JsonUtils.toString(compactJSON);
		ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(compactJSONString);

		return jsonNode;
	}

}
