package eu.vital.orchestrator.engine.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.service.SensorDAO;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SensorAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private SensorDAO sensorDAO;

	public Collection<Map> getList() throws Exception {
		ArrayNode sensors = sensorDAO.list();

		Collection<Map> sensorList = new ArrayList<Map>();
		for (JsonNode sensorNode : sensors) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			sensorList.add(sensor);
		}
		return sensorList;
	}

	public Collection<Map> searchBySensorType(String sensorType) throws Exception {

		QueryBuilder query = QueryBuilders.matchPhraseQuery("@type", sensorType);
		ArrayNode sensors = sensorDAO.search(query);

		Collection<Map> sensorList = new ArrayList<Map>();
		for (JsonNode sensorNode : sensors) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			sensorList.add(sensor);
		}
		return sensorList;
	}

	public Collection<Map> searchByObservationType(String observationType) throws Exception {
		QueryBuilder query = QueryBuilders
				.boolQuery()
				.must(QueryBuilders.matchPhraseQuery(
						"http://purl.oclc.org/NET/ssnx/ssn#observes.@type", observationType));
		ArrayNode sensors = sensorDAO.search(query);

		Collection<Map> sensorList = new ArrayList<Map>();
		for (JsonNode sensorNode : sensors) {
			Map sensor = objectMapper.convertValue(sensorNode, Map.class);
			sensorList.add(sensor);
		}
		return sensorList;
	}

	public Map get(String uri) throws Exception {
		JsonNode sensorNode = sensorDAO.get(uri);
		Map sensor = objectMapper.convertValue(sensorNode, Map.class);
		return sensor;
	}
}
