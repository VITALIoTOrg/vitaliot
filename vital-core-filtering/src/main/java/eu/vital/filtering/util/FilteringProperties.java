package eu.vital.filtering.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jboss.resteasy.logging.Logger;

public class FilteringProperties {



	/* System Property to read configuration file location */
	private static final String VITAL_PROPERTIES_FILE = "vital.properties.file";

	/* Default configuration file location */
	private static final String PROPERTIES_FILENAME = "/vital-properties.xml";

	/* Configuration Parameter Names */
	public static final String DMS_ENDPOINT_ADDRESS = "vital-core-filtering.dms";
	public static final String DMS_ENDPOINT_PORT = "";
	public static final String FILTERING_ENDPOINT_ADDRESS = "vital-core-filtering.endpoint-address";
	public static final String FILTERING_ENDPOINT_PORT = "vital-core-filtering.endpoint-port";

	//private static final String PROPERTIES_FILENAME="/filtering.properties";
		
	private Properties properties;

	final static Logger logger=Logger.getLogger(FilteringProperties.class);

	public FilteringProperties() {
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


	public String getProperty(String property){
		return properties.getProperty(property);
	}

}
