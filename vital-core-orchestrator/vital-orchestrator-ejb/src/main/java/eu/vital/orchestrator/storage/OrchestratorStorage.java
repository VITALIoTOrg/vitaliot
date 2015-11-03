package eu.vital.orchestrator.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class OrchestratorStorage implements Serializable {

	private static final long serialVersionUID = -6384206813479073234L;

	public enum DOCUMENT_TYPE {
		SYSTEM,
		SERVICE,
		SENSOR,
		OBSERVATION,
		CONFIGURATION,
		OPERATION,
		WORKFLOW,
		METASERVICE
	}

	@Inject
	private Logger log;

	@Inject
	private ObjectMapper objectMapper;

	private Client esClient;

	private final String MAIN_INDEX = "vital-orchestrator";

	@PostConstruct
	public void produceElasticSearchTransportClient() {
		log.info("produceElasticSearchTransportClient");

		//String url = System.getProperty("vital.elasticsearch.host");
		String url = "localhost";
		TransportClient client = new TransportClient();
		client.addTransportAddress(new InetSocketTransportAddress(url, 9300));
		this.esClient = client;
	}

	@PreDestroy
	public void disposeElasticSearchClient() {
		log.info("disposeElasticSearchClient");
		if (this.esClient != null) {
			this.esClient.close();
		}
	}

	/**
	 * ***************
	 * CRUD Functions:
	 * ****************
	 */

	public String create(String type, JsonNode document) {
		IndexRequestBuilder irb = esClient.prepareIndex(MAIN_INDEX, type).setSource(document.toString());
		IndexResponse response = irb.get();
		log.info("Document was indexed successfully in " + type + "/" + response.getId() + ".");
		return response.getId();
	}

	public void update(String type, String documentId, JsonNode document) {
		IndexRequestBuilder irb = esClient.prepareIndex(MAIN_INDEX, type, documentId).setSource(document.toString());
		IndexResponse response = irb.get();
		log.info("Document was indexed successfully in " + type + "/" + response.getId() + ".");
	}

	public void delete(String type, String documentId) throws Exception {
		esClient.prepareDelete(MAIN_INDEX, type, documentId)
				.execute()
				.actionGet();
	}

	/**
	 * ***************
	 * GET Functions:
	 * ****************
	 */

	public boolean documentExists(String type, JsonNode document) {
		return esClient.prepareGet(MAIN_INDEX, type, document.toString()).get().isExists();
	}

	public ArrayNode getList(String... types) throws Exception {
		SearchRequestBuilder srb = esClient.prepareSearch(MAIN_INDEX).setTypes(types).setSize(10000);
		return getResults(srb);
	}

	public ArrayNode search(String type, QueryBuilder qb) throws Exception {
		SearchRequestBuilder srb = esClient.prepareSearch(MAIN_INDEX)
				.setTypes(type)
				.setQuery(qb)
				.setFrom(0)
				.setSize(Integer.MAX_VALUE);
		return getResults(srb);
	}

	public ObjectNode get(String type, String documentId) throws Exception {
		//just the _source field (default) will do
		GetResponse getResponse = esClient.prepareGet(MAIN_INDEX, type, documentId).get();
		if (getResponse.isExists()) {
			ObjectNode document = (ObjectNode) objectMapper.readTree(getResponse.getSourceAsString());
			document.put("id", getResponse.getId());
			return document;
		} else {
			throw new Exception("Not Found " + type + " / " + documentId);
		}
	}

	private ArrayNode getResults(SearchRequestBuilder srb) throws Exception {
		SearchResponse response = srb.get();
		SearchHit[] hits = response.getHits().getHits();

		ArrayNode documentList = objectMapper.createArrayNode();
		for (SearchHit hit : hits) {
			ObjectNode node = (ObjectNode) objectMapper.readTree(hit.getSourceAsString());
			node.put("id", hit.getId());
			documentList.add(node);
		}
		return documentList;
	}

}
