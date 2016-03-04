package eu.vital_iot.iotda.ppi.resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.vital_iot.iotda.ppi.service.StatusService;
import eu.vital_iot.iotda.ppi.util.Property;

/**
 * The measurement resource.
 * 
 * @author k4t3r1n4
 *
 */
@Path("/system/measurement")
public class MeasurementResource {

	/**
	 * The status service.
	 */
	@EJB
	private StatusService statusService;

	/**
	 * The base URL to the IoT data adapter PPI.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter-ppi.base-url")
	private String ppi;

	/**
	 * The base URL to the IoT data adapter.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.base-url")
	private String iotda;

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * Gets observations from the monitoring sensor.
	 * 
	 * @param data
	 *            the data.
	 * @return observations from the monitoring sensor.
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> metadata(String data) {
		
		try {

			final List<Object> measurements = new ArrayList<>();
			final ObjectMapper mapper = new ObjectMapper();
			final Map<String, Object> map = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {
			});
			final String sensor = ((List<String>) map.get("sensor")).get(0);
			if (!sensor.equals(ppi + "/sensor/monitoring")) {
				logger.log(Level.INFO, "Unknown sensor " + sensor + ".");
				return new ArrayList<>();
			}
			final String theproperty = (String) map.get("property");
			if (!theproperty.equals("http://vital-iot.eu/ontology/ns/OperationalState")) {
				logger.log(Level.INFO, "Unknown property " + theproperty + ".");
				return new ArrayList<>();
			}

			Map<String, Object> measurement = new HashMap<>();
			measurement.put("@context", "http://vital-iot.eu/contexts/measurement.jsonld");
			measurement.put("id", ppi + "/measurement/" + System.currentTimeMillis());
			measurement.put("type", "ssn:Observation");
			Map<String, Object> property = new HashMap<>();
			property.put("type", "vital:OperationalState");
			measurement.put("ssn:observationProperty", property);
			Map<String, Object> time = new HashMap<>();
			DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			time.put("time:inXSDDateTime", DATE_FORMAT.format(new Date()));
			measurement.put("ssn:observationResultTime", time);
			measurement.put("ssn:featureOfInterest", iotda);
			Map<String, Object> result = new HashMap<>();
			result.put("type", "ssn:SensorOutput");
			Map<String, Object> value = new HashMap<>();
			value.put("type", "ssn:ObservationValue");
			value.put("value", statusService.isIoTSystemRunning() ? "vital:Running" : "vital:Unavailable");
			result.put("ssn:hasValue", value);
			measurement.put("ssn:observationResult", result);
			measurements.add(measurement);
			measurement = new HashMap<>();
			measurement.put("@context", "http://vital-iot.eu/contexts/measurement.jsonld");
			measurement.put("id", ppi + "/measurement/" + System.currentTimeMillis());
			measurement.put("type", "ssn:Observation");
			property = new HashMap<>();
			property.put("type", "vital:OperationalState");
			measurement.put("ssn:observationProperty", property);
			time = new HashMap<>();
			time.put("time:inXSDDateTime", DATE_FORMAT.format(new Date()));
			measurement.put("ssn:observationResultTime", time);
			measurement.put("ssn:featureOfInterest", iotda + "/sensor/monitoring");
			result = new HashMap<>();
			result.put("type", "ssn:SensorOutput");
			value = new HashMap<>();
			value.put("type", "ssn:ObservationValue");
			value.put("value", statusService.isIoTSystemRunning() ? "vital:Running" : "vital:Unavailable");
			result.put("ssn:hasValue", value);
			measurement.put("ssn:observationResult", result);
			measurements.add(measurement);
			return measurements;

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to get measurements.", e);
			return new ArrayList<>();
		}
	}
}