package eu.vital_iot.iotda.ppi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 * Loads properties from a file. The path to that file is specified by the
 * {@code vital-core-iot-data-adapter-ppi.properties} system property.
 * 
 * @author k4t3r1n4
 *
 */
@Singleton(name = "loader")
public class PropertyLoader {

	/**
	 * The property whose value is the path to the properties file.
	 */
	private static final String PROPERTY_FILE_PATH_PROPERTY = "vital-core-iot-data-adapter-ppi.properties";

	/**
	 * The properties.
	 */
	private Map<String, String> properties = new HashMap<>();

	/**
	 * Initialises this property loader.
	 * 
	 * @throws IOException
	 *             if the properties failed to be loaded from the file.
	 */
	@PostConstruct
	private void init() throws IOException {

		final String path = System.getProperty(PROPERTY_FILE_PATH_PROPERTY);
		final File file = new File(path);
		final Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		for (final String name : properties.stringPropertyNames())
			this.properties.put(name, properties.getProperty(name));
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