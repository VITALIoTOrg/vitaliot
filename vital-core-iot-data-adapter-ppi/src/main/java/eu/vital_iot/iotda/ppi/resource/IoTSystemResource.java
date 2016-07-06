package eu.vital_iot.iotda.ppi.resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.vital_iot.iotda.ppi.service.StatusService;
import eu.vital_iot.iotda.ppi.util.Property;

/**
 * The IoT system resource.
 * 
 * @author k4t3r1n4
 *
 */
@Path("/system")
public class IoTSystemResource {

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
	@SuppressWarnings("unused")
	@Inject
	private Logger logger;

	/**
	 * Gets the metadata for the IoT system.
	 * 
	 * @return the metadata for the IoT system.
	 */
	@POST
	@Path("/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> metadata() {
		final Map<String, Object> map = new HashMap<>();
		map.put("@context", "http://vital-iot.eu/contexts/system.jsonld");
		map.put("id", iotda);
		map.put("type", "vital:VitalSystem");
		map.put("name", "IoT Data Adapter");
		map.put("description", "The VITAL IoT Data Adapter.");
		final List<String> sensors = new ArrayList<>();
		sensors.add(ppi + "/sensor/monitoring");
		map.put("sensors", sensors);
		final List<String> services = new ArrayList<>();
		services.add(ppi + "/service/monitoring");
		map.put("services", services);
		map.put("status", statusService.isIoTSystemRunning() ? "vital:Running" : "vital:Unavailable");
		return map;
	}

	/**
	 * Gets the current status of the IoT system.
	 * 
	 * @return the current status of the IoT system.
	 */
	@POST
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> status() {
		final Map<String, Object> map = new HashMap<>();
		map.put("@context", "http://vital-iot.eu/contexts/measurement.jsonld");
		map.put("id", ppi + "/measurement/" + System.currentTimeMillis());
		map.put("type", "ssn:Observation");
		final Map<String, Object> property = new HashMap<>();
		property.put("type", "vital:OperationalState");
		map.put("ssn:observationProperty", property);
		final Map<String, Object> time = new HashMap<>();
		final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		time.put("time:inXSDDateTime", DATE_FORMAT.format(new Date()));
		map.put("ssn:observationResultTime", time);
		map.put("ssn:featureOfInterest", iotda);
		final Map<String, Object> result = new HashMap<>();
		result.put("type", "ssn:SensorOutput");
		final Map<String, Object> value = new HashMap<>();
		value.put("type", "ssn:ObservationValue");
		value.put("value", statusService.isIoTSystemRunning() ? "vital:Running" : "vital:Unavailable");
		result.put("ssn:hasValue", value);
		map.put("ssn:observationResult", result);
		return map;
	}
}