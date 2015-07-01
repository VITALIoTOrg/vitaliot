package eu.vital.management.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DocumentManagerTest {

	@Mock
	private Logger log;

	@InjectMocks
	private DocumentManager documentManager;

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		documentManager.produceElasticSearchTransportClient();
		documentManager.deleteIndex("test");
		documentManager.createIndex("test");
		mapper = new ObjectMapper();
	}

	@Test
	public void testIndexManagement() {
		assertTrue(documentManager.existsIndex("test"));
		assertTrue(documentManager.deleteIndex("test"));
		assertFalse(documentManager.existsIndex("test"));
	}

	@Test
	public void testCreate() throws JsonProcessingException, IOException {
		/*
		String id = documentManager.create("test", "system", readJsonNode("test-data/system.jsonld"));
		assertTrue(documentManager.documentExists("test", "system", id));
		id = documentManager.create("test", "sensor", readJsonNode("test-data/sensor1.jsonld"));
		assertTrue(documentManager.documentExists("test", "sensor", id));
		id = documentManager.create("test", "observation", readJsonNode("test-data/observation1.jsonld"));
		assertTrue(documentManager.documentExists("test", "observation", id));
		*/
	}

	@Test
	public void testUpdate() throws Exception {
		/*
		String id = documentManager.create("test", "sensor", readJsonNode("test-data/sensor1.jsonld"));
		documentManager.update("test", "sensor", id, readJsonNode("test-data/sensor2.jsonld"));
		assertEquals("vital2-I_TrS_10", documentManager.get("test", "sensor", id).get("name").asText());
		*/
	}

	@Test
	public void testGetList() throws Exception {
		/*
		String id1 = documentManager.create("test", "sensor", readJsonNode("test-data/sensor1.jsonld"));
		String id2 = documentManager.create("test", "sensor", readJsonNode("test-data/sensor2.jsonld"));
		assertEquals(2, documentManager.getList("test", "sensor").size());
		*/
	}

	@Test
	public void testGet() throws Exception {
		/*
		String id = documentManager.create("test", "sensor", readJsonNode("test-data/sensor1.jsonld"));
		assertEquals(readJsonNode("test-data/sensor1.jsonld"), documentManager.get("test", "sensor", id));
		*/
	}

	@Test
	public void testSearch() throws JsonProcessingException, IOException {
		/*
		String id1 = documentManager.create("test", "sensor", readJsonNode("test-data/sensor1.jsonld"));
		String id2 = documentManager.create("test", "sensor", readJsonNode("test-data/sensor2.jsonld"));
		assertEquals(2, documentManager.search("test", "sensor", "traffic").size());
		assertEquals(1, documentManager.search("test", "sensor", "reversecolor").size());
		*/
	}

	private JsonNode readJsonNode(String filePath) throws JsonProcessingException, IOException {
		return mapper.readTree(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
	}

}
