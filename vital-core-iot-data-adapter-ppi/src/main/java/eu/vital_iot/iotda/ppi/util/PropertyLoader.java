package eu.vital_iot.iotda.ppi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * Loads properties from a file. The path to that file is specified by the
 * {@code vital.properties.file} system property.
 * 
 * @author k4t3r1n4
 *
 */
@Singleton(name = "loader")
public class PropertyLoader {

	/**
	 * The property whose value is the path to the properties file.
	 */
	private static final String PROPERTY = "vital.properties.file";

	/**
	 * The properties.
	 */
	private Map<String, String> properties = new HashMap<>();

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * Initialises this property loader.
	 */
	@PostConstruct
	private void init() {

		final String path = System.getProperty(PROPERTY);
		if (StringUtils.isBlank(path)) {
			logger.log(Level.SEVERE, "No value for vital-ppi-camden-footfall.properties.file.");
			return;
		}
		final File file = new File(path);
		final Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(file));
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to load properties from " + path + ".", ioe);
		}
		for (final String name : properties.stringPropertyNames()) {
			this.properties.put(name, properties.getProperty(name));
		}
	}

	/**
	 * Gets the value of the property with the specified name.
	 * 
	 * @param name
	 *            the name.
	 * @return the value of the property with the specified name.
	 */
	public String getProperty(String name) {
		return properties.get(name);
	}
}
