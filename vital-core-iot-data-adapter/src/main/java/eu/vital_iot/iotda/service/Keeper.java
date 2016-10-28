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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	 * The base URL to DMS.
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

		logger.log(Level.FINE, "Initialise.");

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

		logger.log(Level.FINE, "Initialised.");
	}

	/**
	 * Keeps.
	 */
	@Schedule(second = "0", minute = "*/30", hour = "*", persistent = false)
	public void keep() {

		logger.log(Level.INFO, "Keep.");

		for (final IoTSystem iotsystem : store.read("{\"enabled\": true }")) {
			final int period = iotsystem.getRefreshPeriod();
			final String last = iotsystem.getLastDataRefresh();
			logger.log(Level.FINE, "Data from " + iotsystem
					+ (last == null ? " has never been refreshed." : " was last refreshed at " + last + "."));
			final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			Date dlast = null;
			if (last != null) {
				try {
					dlast = FORMAT.parse(last);
				} catch (ParseException pe) {
					logger.log(Level.FINE, "Invalid date [ " + last + " ].");
				}
			}
			if (dlast == null || ((new Date().getTime() - dlast.getTime()) / 60000) > period) {
				pool.submit(() -> keepData(iotsystem));
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

		logger.log(Level.FINE, "Keep metadata [ iot-system: " + iotsystem + " ].");

		if (!iotsystem.isEnabled())
			return;
		final Date NOW = new Date();
		try {
			pullSystemMetadata(iotsystem);
			pullServiceMetadata(iotsystem);
			pullSensorMetadata(iotsystem);
			final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			iotsystem.setLastMetadataRefresh(FORMAT.format(NOW));
			store.update(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to keep metadata [ iot-system: " + iotsystem + " ].", ioe);
		}

		logger.log(Level.FINE, "Kept metadata [ iot-system: " + iotsystem + " ].");
	}

	/**
	 * Keeps data from the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to keep data from.
	 */
	public void keepData(IoTSystem iotsystem) {

		logger.log(Level.INFO, "Keep data [ iot-system: " + iotsystem + " ].");

		if (!iotsystem.isEnabled()) {
			logger.log(Level.FINE, iotsystem + " is disabled.");
			return;
		}

		final Date NOW = new Date();
		try {
			pullSensorData(iotsystem);
			final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			iotsystem.setLastDataRefresh(FORMAT.format(NOW));
			store.update(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to keep data [ iot-system: " + iotsystem + " ].", ioe);
		}

		logger.log(Level.INFO, "Kept data [ iot-system: " + iotsystem + " ].");
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

		logger.log(Level.FINE, "Pull system metadata [ iot-system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get system metadata [ iot-system: " + iotsystem + " ].");
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to pull system metadata [ iot-system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got system metadata [ iot-system: " + iotsystem + " ].");
			logger.log(Level.FINER, "System metadata: " + metadata + ".");
		}

		logger.log(Level.FINE, "Pulled system metadata [ iot-system: " + iotsystem + " ].");

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

		logger.log(Level.FINE, "Push system metadata to DMS [ iot-system: " + iotsystem + " ].");

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
				logger.log(Level.SEVERE, "Failed to push system metadata to DMS [ iot-system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed system metadata to DMS [ iot-system: " + iotsystem + " ].");
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

		logger.log(Level.FINE, "Pull service metadata [ iot-system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get service metadata [ iot-system: " + iotsystem + " ].");
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/service/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to pull service metadata [ iot-system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got service metadata [ iot-system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Service metadata: " + metadata + ".");
		}

		logger.log(Level.FINE, "Pulled service metadata [ iot-system: " + iotsystem + " ].");

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

		logger.log(Level.FINE, "Push service metadata to DMS [ iot-system: " + iotsystem + " ].");

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
				logger.log(Level.SEVERE, "Failed to push service metadata to DMS [ iot-system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed service metadata to DMS [ iot-system: " + iotsystem + " ].");
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

		logger.log(Level.FINE, "Pull sensor metadata [ iot-system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		String metadata = null;
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get sensor metadata [ iot-system: " + iotsystem + " ].");
			// FIXME: Get metadata for at most 50 sensors at a time.
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/sensor/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to pull sensor metadata [ iot-system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			metadata = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got sensor metadata [ iot-system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Sensor metadata: " + metadata + ".");
		}

		logger.log(Level.FINE, "Pulled sensor metadata [ iot-system: " + iotsystem + " ].");

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

		logger.log(Level.FINE, "Push sensor metadata to DMS [ iot-system: " + iotsystem + " ].");

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
				logger.log(Level.SEVERE, "Failed to push sensor metadata to DMS [ iot-system: " + iotsystem
						+ ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed sensor metadata to DMS [ iot-system: " + iotsystem + " ].");
	}

	/**
	 * Pulls data from all sensors managed by the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to pull data from its sensors.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	@SuppressWarnings("unchecked")
	private void pullSensorData(IoTSystem iotsystem) throws IOException {

		logger.log(Level.FINE, "Pull sensor data [ iot-system: " + iotsystem + " ].");

		final String url = getObservationsURL(iotsystem);
		logger.log(Level.FINE, "Get observations URL: " + url + ".");

		final ObjectMapper mapper = new ObjectMapper();

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get sensor metadata [ iot-system: " + iotsystem + " ].");
			final HttpPost post = new HttpPost(iotsystem.getPpi() + "/sensor/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			final String sresponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to get sensor metadata [ iot-system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			logger.log(Level.FINE, "Got sensor metadata [ iot-system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Sensor metadata: " + sresponse + ".");
			final List<Object> l = mapper.readValue(sresponse, new TypeReference<List<Object>>() {
			});
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
						final String property = (String) ((List<Object>) ((Map<String, Object>) oo).get("@type"))
								.get(0);
						pullSensorData(iotsystem, url, sensor, property);
					}
				} catch (JsonLdError jle) {
					logger.log(Level.SEVERE, "Failed to get observations URL [ iot-system: " + iotsystem + " ].", jle);
				}
			}
		}

		logger.log(Level.FINE, "Pulled sensor data [ iot-system: " + iotsystem + " ].");
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

		logger.log(Level.FINE, "Get observations URL [ iot-system: " + iotsystem + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get service metadata [ iot-system: " + iotsystem + " ].");
			HttpPost post = new HttpPost(iotsystem.getPpi() + "/service/metadata");
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			HttpEntity entity = new StringEntity(
					"{ \"type\": [ \"http://vital-iot.eu/ontology/ns/ObservationService\" ] }", StandardCharsets.UTF_8);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			String sresponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE, "Failed to get observations URL [ iot-system: " + iotsystem + ", status-code: "
						+ response.getStatusLine().getStatusCode() + " ].");
				return null;
			}
			logger.log(Level.FINE, "Got service metadata [ iot-system: " + iotsystem + " ].");
			logger.log(Level.FINER, "Service metadata: " + sresponse + ".");
			final ObjectMapper mapper = new ObjectMapper();
			List<Object> l = mapper.readValue(sresponse, new TypeReference<List<Object>>() {
			});
			String url = null;
			if (!l.isEmpty()) {
				try {
					Map<String, Object> m = (Map<String, Object>) l.get(0);
					final Map<String, Object> context = new HashMap<>();
					context.put("@context", m.get("@context"));
					final JsonLdOptions options = new JsonLdOptions();
					options.setExpandContext(context);
					m = (Map<String, Object>) JsonLdProcessor.expand(JsonUtils.fromString(sresponse), options).get(0);
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
					logger.log(Level.SEVERE, "Failed to get observations URL [ iot-system: " + iotsystem + " ].", jle);
				}
			}

			logger.log(Level.FINE, "Observations URL: " + url + ".");
			logger.log(Level.FINE, "Got observations URL [ iot-system: " + iotsystem + " ].");
			return url;
		}
	}

	/**
	 * Pulls data from the given sensor, which is managed by the given IoT
	 * system, for the given property using the given URL, which corresponds to
	 * the GetObservations operation of the IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param url
	 *            the GetObservations operation URL.
	 * @param sensor
	 *            the sensor.
	 * @param property
	 *            the property.
	 * @throws IOException
	 *             in case pulling fails.
	 */
	private void pullSensorData(IoTSystem iotsystem, String url, String sensor, String property) {

		logger.log(Level.FINE, "Pull sensor data [ iot-system: " + iotsystem + ", sensor: " + sensor + ", property: "
				+ property + " ].");

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (iotsystem.getAuthenticationInfo().username != null) {
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
					iotsystem.getAuthenticationInfo().username, iotsystem.getAuthenticationInfo().password));
			builder.setDefaultCredentialsProvider(provider);
		}
		try (final CloseableHttpClient client = builder.build()) {
			logger.log(Level.FINE, "Get sensor data [ iot-system: " + iotsystem + ", sensor: " + sensor + ", property: "
					+ property + " ].");
			final Date NOW = new Date();
			final Date from = store.lastAction(sensor);
			final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			final String sfrom = from == null ? null : DATE_FORMAT.format(from);
			final HttpPost post = new HttpPost(url);
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			final HttpEntity entity = new StringEntity("{ \"sensor\": [ \"" + sensor + "\" ], \"property\": \""
					+ property + "\"" + (sfrom == null ? "" : ", \"from\": \"" + sfrom + "\"") + "}",
					StandardCharsets.UTF_8);
			post.setEntity(entity);
			final HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.log(Level.SEVERE,
						"Failed to get sensor data [ iot-system: " + iotsystem + ", sensor: " + sensor + ", property: "
								+ property + ", status-code: " + response.getStatusLine().getStatusCode() + " ].");
				return;
			}
			final String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			logger.log(Level.FINE, "Got sensor data [ iot-system: " + iotsystem + ", sensor: " + sensor + ", property: "
					+ property + " ].");
			logger.log(Level.FINER, "Sensor data: " + data + ".");
			pushSensorDataToDMS(iotsystem, sensor, property, data);
			store.action(sensor, NOW);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to pull [ iot-system: " + iotsystem + ", sensor: " + sensor
					+ ", property: " + property + " ].", ioe);
		}

		logger.log(Level.FINE, "Pulled sensor data [ iot-system: " + iotsystem + ", sensor: " + sensor + ", property: "
				+ property + " ].");
	}

	/**
	 * Pushes the given data from the given sensor, which is managed by the
	 * given IoT system, for the given property to DMS.
	 * 
	 * @param iotsystem
	 *            the IoT system.
	 * @param url
	 *            the GetObservations operation URL.
	 * @param sensor
	 *            the sensor.
	 * @param property
	 *            the property.
	 * @throws IOException
	 *             in case pushing fails.
	 */
	private void pushSensorDataToDMS(IoTSystem iotsystem, String sensor, String property, String data)
			throws IOException {

		logger.log(Level.FINE, "Push sensor data to DMS [ iot-system: " + iotsystem + ", sensor: " + sensor
				+ ", property: " + property + " ].");

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
				logger.log(Level.SEVERE,
						"Failed to push sensor data to DMS [ iot-system: " + iotsystem + ", sensor: " + sensor
								+ ", property: " + property + ", status-code: "
								+ response.getStatusLine().getStatusCode() + " ].");
				return;
			}
		}
		logger.log(Level.FINE, "Pushed sensor data to DMS [ iot-system: " + iotsystem + ", sensor: " + sensor
				+ ", property: " + property + " ].");
	}

	/**
	 * Destroys this keeper.
	 */
	@PreDestroy
	public void destroy() {

		logger.log(Level.FINE, "Destroy.");

		// Shutdown the thread pool.
		if (pool != null)
			pool.shutdownNow();

		logger.log(Level.FINE, "Destroyed.");
	}
}
