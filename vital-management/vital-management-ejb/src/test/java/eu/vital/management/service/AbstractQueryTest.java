package eu.vital.management.service;

import eu.vital.management.storage.DocumentManager;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(MockitoJUnitRunner.class)
public class AbstractQueryTest {

	@Mock
	protected Logger log;


	@InjectMocks
	protected DocumentManager documentManager;

	/**
	 * Ideally, we would want a @BeforeClass annotation, but a static method makes dependency injection hard!
	 *
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		//mockStatic(ClientService.class);
		//Whitebox.setInternalState(QueryService.class, new NodeClientService());
		//EasyMock.expect(mockClientService.getClient()).andReturn(NodeBuilder.nodeBuilder().node().start().client());
		//delete and re-create
		documentManager.createIndex("test");
//		indexService.indexStringData(readString(new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/system.jsonld")))), "test", "system", "vital2-HiReply_1");
//		indexService.indexStringData(readString(new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/sensor1.jsonld")))), "test", "sensor", "vital2-I_TrS_2");
//		indexService.indexStringData(readString(new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/sensor2.jsonld")))), "test", "sensor", "vital2-I_TrS_3");
//		indexService.indexStringData(readString(new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/observation1.jsonld")))), "test", "measurement", "vital2-I_TrS_2_Speed");
//		indexService.indexStringData(readString(new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/observation2.jsonld")))), "test", "measurement", "vital2-I_TrS_2_Color");
	}

	/**
	 * copy of eu.vital.management.util.Utils.readString(BufferedReader)
	 */
	protected String readString(BufferedReader br) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null)
				sb.append(line).append("\n");
		} catch (IOException e) {
			log.log(Level.WARNING, "Cannot read from reader", e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				log.log(Level.WARNING, "Cannot close reader", e);
			}
		}
		return sb.toString();
	}

}
