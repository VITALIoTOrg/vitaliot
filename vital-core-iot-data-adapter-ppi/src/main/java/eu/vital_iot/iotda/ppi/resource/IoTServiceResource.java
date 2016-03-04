package eu.vital_iot.iotda.ppi.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.vital_iot.iotda.ppi.util.Property;

/**
 * The IoT service resource.
 * 
 * @author k4t3r1n4
 *
 */
@Path("/system/service")
public class IoTServiceResource {

	/**
	 * The base URL to the IoT data adapter PPI.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter-ppi.base-url")
	private String ppi;

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * Gets the metadata for the services provided by the IoT system that
	 * satisfy the given query.
	 * 
	 * @param query
	 *            the query.
	 * @return the metadata for the services provided by the IoT system that
	 *         satisfy the given query.
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> metadata(String query) {

		try {

			final List<Object> services = new ArrayList<>();

			final ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map;
			if (StringUtils.isBlank(query)) {
				map = new HashMap<>();
			} else {
				map = mapper.readValue(query, new TypeReference<HashMap<String, Object>>() {
				});
			}
			final List<String> ids = (List<String>) map.get("id");
			final List<String> types = (List<String>) map.get("type");

			if ((ids == null || (ids.contains(ppi + "/service/monitoring")))
					&& (types == null || types.contains("http://vital-iot.eu/ontology/ns/MonitoringService"))) {
				final Map<String, Object> service = new HashMap<>();
				service.put("@context", "http://vital-iot.eu/contexts/service.jsonld");
				service.put("id", ppi + "/service/monitoring");
				service.put("type", "vital:MonitoringService");
				final List<Object> operations = new ArrayList<>();
				Map<String, Object> operation = new HashMap<>();
				operation.put("type", "vital:GetSystemStatus");
				operation.put("hrest:hasAddress", ppi + "/ppi/system/status");
				operation.put("hrest:hasMethod", "hrest:POST");
				operations.add(operation);
				operation = new HashMap<>();
				operation.put("type", "vital:GetSensorStatus");
				operation.put("hrest:hasAddress", ppi + "/ppi/system/sensor/status");
				operation.put("hrest:hasMethod", "hrest:POST");
				operations.add(operation);
				service.put("operations", operations);
				services.add(service);
			}

			if ((ids == null || (ids.contains(ppi + "/service/observation")))
					&& (types == null || types.contains("http://vital-iot.eu/ontology/ns/ObservationService"))) {
				final Map<String, Object> service = new HashMap<>();
				service.put("@context", "http://vital-iot.eu/contexts/service.jsonld");
				service.put("id", ppi + "/service/observation");
				service.put("type", "vital:ObservationService");
				final List<Object> operations = new ArrayList<>();
				Map<String, Object> operation = new HashMap<>();
				operation.put("type", "vital:GetObservations");
				operation.put("hrest:hasAddress", ppi + "/ppi/system/measurement");
				operation.put("hrest:hasMethod", "hrest:POST");
				operations.add(operation);
				service.put("operations", operations);
				services.add(service);
			}

			return services;

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to get IoT service metadata.", e);
			return new ArrayList<>();
		}
	}
}