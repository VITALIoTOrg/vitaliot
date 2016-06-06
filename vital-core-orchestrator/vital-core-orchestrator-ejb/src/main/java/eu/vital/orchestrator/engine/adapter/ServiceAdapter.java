package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vital.orchestrator.service.ServiceDAO;
import eu.vital.orchestrator.util.OntologyParser;
import eu.vital.orchestrator.util.VitalClient;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ServiceAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ServiceDAO serviceDAO;

	@Inject
	private VitalClient vitalClient;

	public Object execute(String serviceUri, String operationType, Map data) throws Exception {

		JsonNode service = serviceDAO.get(serviceUri);
		JsonNode operation = OntologyParser.findOperation(operationType, service);

		String url = OntologyParser.getOperationURL(operation);
		String method = OntologyParser.getOperationMethod(operation);

		JsonNode responseData;
		if (method.equals("GET")) {
			responseData = vitalClient.doGet(url);
		} else if (method.equals("POST")) {
			JsonNode requestData = objectMapper.valueToTree(data);
			responseData = vitalClient.doPost(url, requestData);
		} else {
			throw new UnsupportedOperationException("Mehod " + method + " not supported");
		}

		if (responseData.isArray()) {
			Collection<Map> result = new ArrayList<Map>();
			for (JsonNode node : responseData) {
				Map nodeAsMap = objectMapper.convertValue(node, Map.class);
				result.add(nodeAsMap);
			}
			return result;
		} else {
			Map nodeAsMap = objectMapper.convertValue(responseData, Map.class);
			return nodeAsMap;
		}
	}

}
