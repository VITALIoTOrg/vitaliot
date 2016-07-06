package eu.vital_iot.iotda.ppi.resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * The sensor resource.
 * 
 * @author k4t3r1n4
 *
 */
@Path("/system/sensor")
public class SensorResource {

	/**
	 * The status service.
	 */
	@EJB
	private StatusService statusService;

	/**
	 * The base URL to the IoT data adapter PPI.
	 */
	@Inject
	@Property(name = "vital_core_iot_data_adapter_ppi.base_url")
	private String ppi;

	/**
	 * Gets the metadata for the services provided by the IoT system.
	 * 
	 * @return the metadata for the services provided by the IoT system.
	 */
	@POST
	@Path("/metadata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> metadata() {
		final List<Object> sensors = new ArrayList<>();
		final Map<String, Object> sensor = new HashMap<>();
		sensor.put("@context", "http://vital-iot.eu/contexts/sensor.jsonld");
		sensor.put("id", ppi + "/sensor/monitoring");
		sensor.put("type", "vital:MonitoringSensor");
		sensor.put("name", "System Monitoring Sensor");
		sensor.put("description", "A virtual sensor that monitors the operational state of the IoT Data Adapter.");
		sensor.put("status", statusService.isIoTSystemRunning() ? "vital:Running" : "vital:Unavailable");
		final List<Object> properties = new ArrayList<>();
		final Map<String, Object> property = new HashMap<>();
		property.put("type", "vital:OperationalState");
		property.put("id", ppi + "/sensor/monitoring/status");
		properties.add(property);
		sensor.put("ssn:observes", properties);
		sensors.add(sensor);
		return sensors;
	}

	/**
	 * Gets the metadata for the services provided by the IoT system.
	 * 
	 * @param data
	 *            the data.
	 * @return the metadata for the services provided by the IoT system.
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> status(String data) {
		final List<Object> statuses = new ArrayList<>();
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final Map<String, Object> map = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {
			});
			final String id = ((List<String>) map.get("id")).get(0);
			if (!id.equals(ppi + "/sensor/monitoring"))
				return statuses;
		} catch (Exception e) {
			e.printStackTrace();
			return statuses;
		}
		final Map<String, Object> status = new HashMap<>();
		status.put("@context", "http://vital-iot.eu/contexts/measurement.jsonld");
		status.put("id", ppi + "/measurement/" + System.currentTimeMillis());
		status.put("type", "ssn:Observation");
		final Map<String, Object> property = new HashMap<>();
		property.put("type", "vital:OperationalState");
		status.put("ssn:observationProperty", property);
		final Map<String, Object> time = new HashMap<>();
		final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		time.put("time:inXSDDateTime", DATE_FORMAT.format(new Date()));
		status.put("ssn:observationResultTime", time);
		status.put("ssn:featureOfInterest", ppi + "/sensor/monitoring");
		final Map<String, Object> result = new HashMap<>();
		result.put("type", "ssn:SensorOutput");
		final Map<String, Object> value = new HashMap<>();
		value.put("type", "ssn:ObservationValue");
		value.put("value", statusService.isIoTSystemRunning() ? "vital:Running" : "vital:Unavailable");
		result.put("ssn:hasValue", value);
		status.put("ssn:observationResult", result);
		statuses.add(status);
		return statuses;
	}
}