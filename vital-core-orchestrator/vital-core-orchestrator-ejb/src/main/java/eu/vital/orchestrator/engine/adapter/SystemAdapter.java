package eu.vital.orchestrator.engine.adapter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.service.SystemDAO;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SystemAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private SystemDAO systemDAO;

	public Collection<Map> getList() throws Exception {
		ArrayNode systems = systemDAO.list();
		Collection<Map> systemList = new ArrayList<Map>();
		for (JsonNode systemNode : systems) {
			Map sensor = objectMapper.convertValue(systemNode, Map.class);
			systemList.add(sensor);
		}
		return systemList;
	}

	public Map get(String systemUri) throws Exception {
		JsonNode systemJSON = systemDAO.get(systemUri);
		Map system = objectMapper.convertValue(systemJSON, Map.class);
		return system;
	}
}