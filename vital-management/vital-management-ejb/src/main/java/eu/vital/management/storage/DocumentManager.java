package eu.vital.management.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.service.ObservationService;
import eu.vital.management.service.SensorDAO;
import eu.vital.management.service.SystemDAO;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class DocumentManager implements Serializable {

	private static final long serialVersionUID = -6384206813479073234L;

	@Inject
	private Logger log;

	@Inject
	private ObjectMapper objectMapper;

	private Client esClient;

	private static final String MAPPING_LOCATION = "mappings";

	private static final Map<String, String> typeMap;

	static {
		typeMap = new HashMap<String, String>();
		typeMap.put(SensorDAO.SENSOR_TYPE, "sensor-mapping.json");
		typeMap.put(ObservationService.OBSERVATION_TYPE, "measurement-mapping.json");
		typeMap.put(SystemDAO.SYSTEM_TYPE, "system-mapping.json");
	}

	private static final String MAIN_INDEX = "vital-management";

	@PostConstruct
	public void produceElasticSearchTransportClient() {
		log.info("produceElasticSearchTransportClient");

		String url = System.getProperty("vital.elasticsearch.host");
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
				.setQuery(qb);
		return getResults(srb);
	}

	public ObjectNode get(String type, String documentId) throws Exception {
		//just the _source field (default) will do
		GetResponse getResponse = esClient.prepareGet(MAIN_INDEX, type, documentId).get();
		if (getResponse.isExists()) {
			ObjectNode document = (ObjectNode) objectMapper.readTree(getResponse.getSourceAsString());
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
			documentList.add(objectMapper.readTree(hit.getSourceAsString()));
		}
		return documentList;
	}

	/**
	 * Creates index with appropriate type mappings (hard-coded).
	 *
	 * @param indexName
	 */
	public boolean createIndex(String indexName) {
		CreateIndexRequestBuilder cirb = esClient.admin().indices().prepareCreate(indexName);
		//add all type mappings to CreateIndexRequestBuilder
		for (String type : typeMap.keySet()) {
			cirb.addMapping(type, getMappingForType(type));
		}
		CreateIndexResponse cir = null;
		try {
			cir = cirb.get();
			log.info("Index " + indexName + " was created!");
			return cir.isAcknowledged();
		} catch (ElasticsearchException ex) {
			log.log(Level.INFO, "Error when creating index " + indexName + ". " + ex.getDetailedMessage(), ex);
		}
		return false;
	}

	public boolean existsIndex(String indexName) {
		IndicesExistsResponse ier = null;
		try {
			ier = esClient.admin().indices().prepareExists(indexName).get();
		} catch (ElasticsearchException ex) {
			log.log(Level.SEVERE, "Error when checking whether index " + indexName + " exists. " + ex.getDetailedMessage(), ex);
		}
		return ier.isExists();
	}

	public boolean deleteIndex(String indexName) {
		if (!existsIndex(indexName)) {
			return false;
		}
		DeleteIndexResponse dir = null;
		try {
			dir = esClient.admin().indices().prepareDelete(indexName).get();
			log.info("Index " + indexName + " was deleted!");
		} catch (ElasticsearchException ex) {
			log.log(Level.SEVERE, "Error when deleting index " + indexName + ". " + ex.getDetailedMessage(), ex);
		}
		return dir.isAcknowledged();
	}

	/**
	 * @param type
	 * @return a String containing the mapping (in JSON format) for an ES type.
	 */
	private String getMappingForType(String type) {
		String url = MAPPING_LOCATION + "/" + typeMap.get(type);
		return readFileContents(url);
	}

	/**
	 * Reads the contents of a file in a String.
	 *
	 * @param url
	 * @return
	 */
	private String readFileContents(String url) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(url)));
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Error while reading " + url + ". Cannot fetchObservation file.", ex);
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error while reading " + url + ". Cannot open file.", ex);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

}
