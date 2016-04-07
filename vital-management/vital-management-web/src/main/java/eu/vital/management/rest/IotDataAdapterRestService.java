package eu.vital.management.rest;

import eu.vital.management.rest.proxy.ProxyRestService;
import eu.vital.management.util.VitalConfiguration;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/iot-data-adapter/*", asyncSupported = true)
public class IotDataAdapterRestService extends ProxyRestService {

	@Inject
	Logger logger;

	@Inject
	VitalConfiguration vitalConfiguration;

	public URL getRemoteUrl() {
		try {
			return new URL(vitalConfiguration.getProperty("vital-management.iot-data-adapter", "http://localhost:8080/vital-core-iot-data-adapter/rest"));
		} catch (MalformedURLException e) {
			logger.warning("vital-management.iot-data-adapter is malformed" + vitalConfiguration.getProperty("vital-management.iot-data-adapter"));
			return null;
		}
	}
}
