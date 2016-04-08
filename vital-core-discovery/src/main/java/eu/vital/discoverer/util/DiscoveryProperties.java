/**
 * @Author: Riccardo Petrolo <riccardo>
 * @Date: 2016-03-30T17:37:24+02:00
 * @Email: riccardo.petrolo@inria.fr
 * @Last modified by:   riccardo
 * @Last modified time: 2016-03-30T18:26:51+02:00
 */

package eu.vital.discoverer.util;

import org.jboss.resteasy.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DiscoveryProperties {

	/* System Property to read configuration file location */
	private static final String VITAL_PROPERTIES_FILE = "vital.properties.file";

	/* Default configuration file location */
	private static final String PROPERTIES_FILENAME = "/vital-properties.xml";

	/* Configuration Parameter Names */
	public static final String DMS_ENDPOINT_ADDRESS = "vital-core-discovery.dms";
	public static final String DMS_ENDPOINT_PORT = "";
	public static final String DISCOVERER_ENDPOINT_ADDRESS = "vital-core-discovery.endpoint-address";
	public static final String DISCOVERER_ENDPOINT_PORT = "vital-core-discovery.endpoint-port";

	private Properties properties;

	final static Logger logger = Logger.getLogger(DiscoveryProperties.class);

	public DiscoveryProperties() {
		properties = new Properties();

		String fileName = System.getProperty(VITAL_PROPERTIES_FILE);
		File propertyFile = new File(fileName);

		if (propertyFile.exists()) {
			// 1. Read from Vital configuration file
			logger.debug("Local configuration file found");
			try {
				properties.loadFromXML(new FileInputStream(propertyFile));
				logger.info("Contained Keys\n" + properties.keySet());
			} catch (IOException e) {
				logger.error("Unable to load Properties", e);
			}

		} else {
			// 2. Fallback to default configuration file
			logger.info("Loading default configuration file");
			try {
				properties.loadFromXML(this.getClass().getResourceAsStream(PROPERTIES_FILENAME));
			} catch (IOException e) {
				logger.error("Unable to load Properties", e);
			}
		}
	}

	public String getProperty(String property) {
		return properties.getProperty(property);
	}

}
