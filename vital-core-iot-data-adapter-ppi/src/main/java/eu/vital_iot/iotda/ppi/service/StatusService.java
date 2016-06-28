package eu.vital_iot.iotda.ppi.service;

import java.io.IOException;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import eu.vital_iot.iotda.ppi.util.Property;

/**
 * The status service.
 * 
 * @author k4t3r1n4
 *
 */
@Singleton
public class StatusService {

	/**
	 * The base URL to the IoT data adapter.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.base-url")
	private String iotda;

	/**
	 * Checks whether the IoT system is running.
	 * 
	 * @return whether the IoT system is running.
	 */
	public boolean isIoTSystemRunning() {
		final HttpClient client = HttpClients.createDefault();
		final HttpGet get = new HttpGet(iotda);
		final RequestConfig config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();
		get.setConfig(config);
		try {
			final HttpResponse response = client.execute(get);
			final int sc = response.getStatusLine().getStatusCode();
			return sc == HttpStatus.SC_OK;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
	}
}