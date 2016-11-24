package eu.vital_iot.iotda.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import eu.vital_iot.iotda.common.IoTSystem;
import eu.vital_iot.iotda.util.Property;

/**
 * This class represents keepers for IoT systems.
 * 
 * @author k4t3r1n4
 *
 * @see IoTSystem
 */
@Startup
@Singleton
public class Keeper {

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * The store.
	 */
	@EJB
	private Store store;

	/**
	 * The number of workers.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.keeper.workers")
	private int nWorkers;

	/**
	 * The thread pool.
	 */
	private ExecutorService pool;

	/**
	 * The base URL to VITAL DMS.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.vital-core-dms.base-url")
	private String dms;

	/**
	 * The name of the cookie that contains the SSO token.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.sso-token")
	private String cookieName;

	/**
	 * The security guard.
	 */
	@Inject
	private Guard guard;

	/**
	 * The SSL connection socket factory.
	 */
	private SSLConnectionSocketFactory factory;

	/**
	 * Initialises this keeper.
	 */
	@PostConstruct
	public void init() {

		logger.log(Level.INFO, "Initialise.");

		// Start the thread pool.
		pool = Executors.newFixedThreadPool(nWorkers);

		try {
			final SSLContext context = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(final X509Certificate[] chain, final String authType)
						throws CertificateException {
					return true;
				}
			}).build();
			factory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
			logger.log(Level.SEVERE, "Failed to build the SSL connection socket factory.", e);
		}

		logger.log(Level.INFO, "Initialised.");
	}

	/**
	 * Keeps.
	 */
	@Schedule(second = "0", minute = "*/10", hour = "*", persistent = false)
	public void keep() {

		logger.log(Level.INFO, "Keep.");

		for (final IoTSystem iotsystem : store.read("{\"enabled\": true }")) {
			final int period = iotsystem.getRefreshPeriod();
			final String last = iotsystem.getLastDataRefresh();
			if (last == null) {
				logger.log(Level.FINE, "Data from system " + iotsystem + " has never been refreshed.");
			} else {
				logger.log(Level.FINE, "Data from system " + iotsystem + " was last refreshed at " + last + ".");
			}
			final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date dlast = null;
			if (last != null) {
				try {
					dlast = FORMAT.parse(last);
				} catch (ParseException pe) {
					logger.log(Level.SEVERE, "Invalid date [ " + last + " ].", pe);
				}
			}
			if (dlast == null || ((new Date().getTime() - dlast.getTime()) / 60000) > period) {
				// Keep data for at most one day.
				final Date from = dlast == null || new Date().getTime() - dlast.getTime() > period * 60000
						? new Date(new Date().getTime() - period * 60000) : dlast;
				pool.submit(() -> keepData(iotsystem, from));
			}
		}

		logger.log(Level.INFO, "Kept.");
	}

	/**
	 * Keeps metadata for the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to keep metadata for.
	 */
	public void keepMetadata(IoTSystem iotsystem) {

		logger.log(Level.INFO, "Keep metadata [ system: " + iotsystem + " ].");

		if (!iotsystem.isEnabled()) {
			logger.log(Level.FINE, "System " + iotsystem + " is disabled.");
			return;
		}

		final Date NOW = new Date();
		try {
			pullSystemMetadata(iotsystem);
			pullServiceMetadata(iotsystem);
			pullSensorMetadata(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to keep metadata [ system: " + iotsystem + " ].", ioe);
		} finally {
			final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			iotsystem.setLastMetadataRefresh(FORMAT.format(NOW));
			store.update(iotsystem);
		}

		logger.log(Level.INFO, "Kept metadata [ system: " + iotsystem + " ].");
	}

	/**
	 * Keeps data from the given IoT system since the given date and time.
	 * 
	 * @param iotsystem
	 *            the IoT system to keep data from.
	 */
	public void keepData(IoTSystem iotsystem, Date from) {

		final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		logger.log(Level.INFO, "Keep data [ system: " + iotsystem + ", from: " + FORMAT.format(from) + " ].");

		if (!iotsystem.isEnabled()) {
			logger.log(Level.FINE, "System " + iotsystem + " is disabled.");
			return;
		}

		final Date to = new Date();
		try {
			pullSensorData(iotsystem, from, to);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE,
					"Failed to keep data [ system: " + iotsystem + ", from: " + FORMAT.format(from) + " ].", ioe);
		} finally {
			iotsystem.setLastDataRefresh(FORMAT.format(to));
			store.update(iotsystem);
		}

		logger.log(Level.INFO, "Kept data [ system: " + iotsystem + ", from: " + FORMAT.format(from) + " ].");
	}

	/**
	 * Pulls metadata about the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to pull metadata for.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	private void pullSystemMetadata(IoTSystem iotsystem) throws IOException {

		logger.log(Level.FINE, "Pull system metadata [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get system metadata [ system: " + iotsystem + " ].");
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to pull system metadata [ system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got system metadata [ system: " + iotsystem + " ].");
			logger.log(Level.FINER, "System metadata: " + metadata + ".");
		}

		logger.log(Level.FINE, "Pulled system metadata [ system: " + iotsystem + " ].");

		pushSystemMetadataToDMS(iotsystem, metadata);
	}

	/**
	 * Pushes the given metadata about the given IoT system to DMS.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param metadata
	 *            the metadata about the IoT system.
	 * @throws IOException
	 *             in case pushing fails.
	 */
	private void pushSystemMetadataToDMS(IoTSystem iotsystem, String metadata) throws IOException {

		logger.log(Level.FINE, "Push system metadata to DMS [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (factory != null) {
			builder = builder.setSSLSocketFactory(factory);
		}
		try (final CloseableHttpClient client = builder.build()) {
			final HttpPost post = new HttpPost(dms + "/insertSystem");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			post.setHeader("Cookie", cookieName + "=" + guard.systemLogin());
			final HttpEntity entity = new StringEntity(metadata, StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
				logger.log(Level.SEVERE, "Failed to push system metadata to DMS [ system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed system metadata to DMS [ system: " + iotsystem + " ].");
	}

	/**
	 * Pulls metadata about all IoT services provided by the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to pull metadata about its IoT services.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	private void pullServiceMetadata(IoTSystem iotsystem) throws IOException {

		logger.log(Level.FINE, "Pull service metadata [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get service metadata [ system: " + iotsystem + " ].");
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/service/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to pull service metadata [ system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got service metadata [ system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Service metadata: " + metadata + ".");
		}

		logger.log(Level.FINE, "Pulled service metadata [ system: " + iotsystem + " ].");

		pushServiceMetadataToDMS(iotsystem, metadata);
	}

	/**
	 * Pushes the given metadata about the IoT services provided by the given
	 * IoT system to DMS.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param metadata
	 *            the metadata about the IoT services provided by the IoT
	 *            system.
	 * @throws IOException
	 *             in case pushing fails.
	 */
	private void pushServiceMetadataToDMS(IoTSystem iotsystem, String metadata) throws IOException {

		logger.log(Level.FINE, "Push service metadata to DMS [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (factory != null) {
			builder = builder.setSSLSocketFactory(factory);
		}
		try (final CloseableHttpClient client = builder.build()) {
			final HttpPost post = new HttpPost(dms + "/insertService");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			post.setHeader("Cookie", cookieName + "=" + guard.systemLogin());
			final HttpEntity entity = new StringEntity(metadata, StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
				logger.log(Level.SEVERE, "Failed to push service metadata to DMS [ system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed service metadata to DMS [ system: " + iotsystem + " ].");
	}

	/**
	 * Pulls metadata about all sensors managed by the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to pull metadata about its sensors.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	private void pullSensorMetadata(IoTSystem iotsystem) throws IOException {

		logger.log(Level.FINE, "Pull sensor metadata [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get sensor metadata [ system: " + iotsystem + " ].");
			// FIXME: Get metadata for at most 50 sensors at a time.
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/sensor/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to pull sensor metadata [ system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got sensor metadata [ system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Sensor metadata: " + metadata + ".");
		}

		logger.log(Level.FINE, "Pulled sensor metadata [ system: " + iotsystem + " ].");

		pushSensorMetadataToDMS(iotsystem, metadata);
	}

	/**
	 * Pushes the given metadata about the sensors of the given IoT system to
	 * DMS.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param metadata
	 *            the metadata about the sensors of the IoT system.
	 * @throws IOException
	 *             in case pushing fails.
	 */
	private void pushSensorMetadataToDMS(IoTSystem iotsystem, String metadata) throws IOException {

		logger.log(Level.FINE, "Push sensor metadata to DMS [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (factory != null) {
			builder = builder.setSSLSocketFactory(factory);
		}
		try (final CloseableHttpClient client = builder.build()) {
			final HttpPost post = new HttpPost(dms + "/insertSensor");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			post.setHeader("Cookie", cookieName + "=" + guard.systemLogin());
			final HttpEntity entity = new StringEntity(metadata, StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
				logger.log(Level.SEVERE, "Failed to push sensor metadata to DMS [ system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed sensor metadata to DMS [ system: " + iotsystem + " ].");
	}

	/**
	 * Pulls data that were collected in the given time interval from all
	 * sensors managed by the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to pull data from its sensors.
	 * @param from
	 *            the beginning of the time interval.
	 * @param to
	 *            the end of the time interval.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	@SuppressWarnings("unchecked")
	private void pullSensorData(IoTSystem iotsystem, Date from, Date to) throws IOException {

		final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		logger.log(Level.FINE, "Pull sensor data [ system: " + iotsystem + ", from: " + FORMAT.format(from) + ", to: "
				+ FORMAT.format(to) + " ].");

		// Get observations URL.
		final String url = getObservationsURL(iotsystem);
		logger.log(Level.FINE, "Get observations URL for system " + iotsystem + " is " + url + ".");

		final ObjectMapper mapper = new ObjectMapper();

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}

		// Get sensor metadata.
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get sensor metadata [ system: " + iotsystem + " ].");
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/sensor/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to get sensor metadata [ system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			logger.log(Level.FINE, "Got sensor metadata [ system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Sensor metadata: " + metadata + ".");
		}

		// Process sensor metadata.
		final List<Object> l = mapper.readValue(metadata, new TypeReference<List<Object>>() {
		});
		final Map<String, List<String>> map = new HashMap<>();
		for (final Object o : l) {
			try {
				Map<String, Object> s = (Map<String, Object>) o;
				final Map<String, Object> context = new HashMap<>();
				context.put("@context", s.get("@context"));
				final JsonLdOptions options = new JsonLdOptions();
				options.setExpandContext(context);
				s = (Map<String, Object>) JsonLdProcessor.expand(o, options).get(0);
				final String sensor = (String) s.get("@id");
				final List<Object> ll = (List<Object>) s.get("http://purl.oclc.org/NET/ssnx/ssn#observes");
				for (final Object oo : ll) {
					final String property = (String) ((List<Object>) ((Map<String, Object>) oo).get("@type")).get(0);
					if (!map.containsKey(property)) {
						map.put(property, new ArrayList<>());
					}
					map.get(property).add(sensor);
				}
			} catch (JsonLdError jle) {
				logger.log(Level.SEVERE, "Failed to process sensor metadata [ system: " + iotsystem + " ].", jle);
			}
		}

		// Get sensor data.
		for (final Map.Entry<String, List<String>> entry : map.entrySet()) {
			final String property = entry.getKey();
			final List<String> sensors = entry.getValue();
			pullSensorData(iotsystem, property, sensors, url, from, to);
		}

		logger.log(Level.FINE, "Pulled sensor data [ system: " + iotsystem + ", from: " + FORMAT.format(from) + ", to: "
				+ FORMAT.format(to) + " ].");
	}

	/**
	 * Gets the URL of the GetObservations operation supported by the
	 * ObservationService of the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @return the URL of the GetObservations operation supported by the
	 *         ObservationService of the given IoT system, or {@code null}, if
	 *         no such operation is supported.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @throws IOException
	 *             in case getting fails.
	 */
	@SuppressWarnings("unchecked")
	private String getObservationsURL(IoTSystem iotsystem) throws IOException {

		logger.log(Level.FINE, "Get observations URL [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}

		// Get service metadata.
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get service metadata [ system: " + iotsystem + " ].");
			HttpPost post = new HttpPost(iotsystem.getPpi() + "/service/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			HttpEntity entity = new StringEntity(
					"{ \"type\": [ \"http://vital-iot.eu/ontology/ns/ObservationService\" ] }", StandardCharsets.UTF_8);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to get observations URL [ system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return null;
			}
			logger.log(Level.FINE, "Got service metadata [ system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Service metadata: " + metadata + ".");
		}

		// Process service metadata.
		final ObjectMapper mapper = new ObjectMapper();
		List<Object> l = mapper.readValue(metadata, new TypeReference<List<Object>>() {
		});
		String url = null;
		if (!l.isEmpty()) {
			try {
				Map<String, Object> m = (Map<String, Object>) l.get(0);
				final Map<String, Object> context = new HashMap<>();
				context.put("@context", m.get("@context"));
				final JsonLdOptions options = new JsonLdOptions();
				options.setExpandContext(context);
				m = (Map<String, Object>) JsonLdProcessor.expand(JsonUtils.fromString(metadata), options).get(0);
				l = (List<Object>) m.get("http://iserve.kmi.open.ac.uk/ns/msm#hasOperation");
				for (final Object o : l) {
					final Map<String, Object> s = (Map<String, Object>) o;
					final String type = (String) ((List<Object>) s.get("@type")).get(0);
					if (type.equals("http://vital-iot.eu/ontology/ns/GetObservations")) {
						url = (String) ((Map<String, Object>) ((List<Object>) s
								.get("http://www.wsmo.org/ns/hrests#hasAddress")).get(0)).get("@value");
						break;
					}
				}
			} catch (JsonLdError jle) {
				logger.log(Level.SEVERE, "Failed to get observations URL [ system: " + iotsystem + " ].", jle);
			}

			logger.log(Level.FINE, "Observations URL: " + url + ".");
			logger.log(Level.FINE, "Got observations URL [ system: " + iotsystem + " ].");
			return url;
		}

		return null;
	}

	/**
	 * Pulls data collected in the given time interval from the given sensors,
	 * which are managed by the given IoT system, for the given property using
	 * the given URL, which corresponds to the GetObservations operation of the
	 * IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param property
	 *            the property.
	 * @param sensor
	 *            the sensor.
	 * @param url
	 *            the GetObservations operation URL.
	 * @param from
	 *            the beginning of the time interval.
	 * @param to
	 *            the end of the time interval.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	private void pullSensorData(IoTSystem iotsystem, String property, List<String> sensors, String url, Date from,
			Date to) {

		final int BATCH_SIZE = 50;

		logger.log(Level.FINE, "Pull sensor data [ system: " + iotsystem + ", property: " + property + ", sensors: "
				+ sensors.size() + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}

		final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		final String sfrom = DATE_FORMAT.format(from);
		try (final CloseableHttpClient client = builder.build()) {
			for (int start = 0; start < sensors.size(); start += BATCH_SIZE) {
				int end = start + BATCH_SIZE;
				if (end > sensors.size()) {
					end = sensors.size();
				}
				logger.log(Level.FINE, "Get sensor data [ system: " + iotsystem + ", property: " + property
						+ ", sensors: " + sensors.size() + ", start: " + start + ", end: " + end + " ].");
				final HttpPost post = new HttpPost(url);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				// NOTE: Let's not use to for now.
				final String query = "{ \"sensor\": [ "
						+ sensors.subList(start, end).stream().map(s -> "\"" + s + "\"")
								.collect(Collectors.joining(","))
						+ " ], \"property\": \"" + property + "\", \"from\": \"" + sfrom + "\" }";
				final HttpEntity entity = new StringEntity(query, StandardCharsets.UTF_8);
				post.setEntity(entity);
				final HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					logger.log(Level.SEVERE,
							"Failed to get sensor data [ system: " + iotsystem + ", property: " + property
									+ ", sensors: " + sensors.size() + ", start: " + start + ", end: " + end
									+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
					continue;
				}
				final String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				logger.log(Level.FINE, "Got sensor data [ system: " + iotsystem + ", property: " + property
						+ ", sensors: " + sensors.size() + ", start: " + start + ", end: " + end + " ].");
				logger.log(Level.FINER, "Sensor data: " + data + ".");
				pushSensorDataToDMS(iotsystem, data);
			}
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to pull sensor data [ system: " + iotsystem + ", property: " + property
					+ ", sensors: " + sensors.size() + " ].", ioe);
		}

		logger.log(Level.FINE, "Pulled sensor data [ system: " + iotsystem + ", property: " + property + ", sensors: "
				+ sensors.size() + " ].");
	}

	/**
	 * Pushes the given data from the given IoT system to DMS.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param data
	 *            the data.
	 * @throws IOException
	 *             in case pushing fails.
	 */
	private void pushSensorDataToDMS(IoTSystem iotsystem, String data) throws IOException {

		logger.log(Level.FINE, "Push sensor data to DMS [ system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (factory != null) {
			builder = builder.setSSLSocketFactory(factory);
		}
		try (final CloseableHttpClient client = builder.build()) {
			final HttpPost post = new HttpPost(dms + "/insertObservation");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			post.setHeader("Cookie", cookieName + "=" + guard.systemLogin());
			final HttpEntity entity = new StringEntity(data, StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
				logger.log(Level.SEVERE, "Failed to push sensor data to DMS [ system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed sensor data to DMS [ system: " + iotsystem + " ].");
	}

	/**
	 * Destroys this keeper.
	 */
	@PreDestroy
	public void destroy() {

		logger.log(Level.INFO, "Destroy.");

		// Shutdown the thread pool.
		if (pool != null) {
			pool.shutdownNow();
		}

		logger.log(Level.INFO, "Destroyed.");
	}
}