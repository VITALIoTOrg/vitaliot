package eu.vital_iot.iotda.ppi.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	@Property(name = "vital_core_iot_data_adapter_ppi.base_url")
	private String ppi;

	/**
	 * Gets the metadata for the services provided by the IoT system.
	 * 
	 * @return the metadata for the services provided by the IoT system.
	 */
	@POST
	@Path("/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Object> metadata() {
		final List<Object> services = new ArrayList<>();
		Map<String, Object> service = new HashMap<>();
		service.put("@context", "http://vital-iot.eu/contexts/service.jsonld");
		service.put("id", ppi + "/service/monitoring");
		service.put("type", "vital:MonitoringService");
		List<Object> operations = new ArrayList<>();
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
		service = new HashMap<>();
		service.put("@context", "http://vital-iot.eu/contexts/service.jsonld");
		service.put("id", ppi + "/service/observation");
		service.put("type", "vital:ObservationService");
		operations = new ArrayList<>();
		operation = new HashMap<>();
		operation.put("type", "vital:GetObservations");
		operation.put("hrest:hasAddress", ppi + "/ppi/system/measurement");
		operation.put("hrest:hasMethod", "hrest:POST");
		operations.add(operation);
		service.put("operations", operations);
		services.add(service);
		return services;
	}
}