package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vital.orchestrator.util.VitalClient;

import javax.inject.Inject;
import java.util.Map;

public class HttpAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private VitalClient vitalClient;

	public Map doGet(String url) throws Exception {
		JsonNode output = vitalClient.doGet(url);
		return objectMapper.convertValue(output, Map.class);
	}

	public Map doPost(String url, Map data) throws Exception {
		JsonNode inputData = objectMapper.valueToTree(data);
		JsonNode output = vitalClient.doPost(url, inputData);
		return objectMapper.convertValue(output, Map.class);
	}

}