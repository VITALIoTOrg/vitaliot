package util;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by anglen on 02/03/16.
 */
@ApplicationScoped
public class VitalConfiguration {

	private static final String VITAL_PROPERTIES_FILE = "vital.properties.file";

	private static Map<String, String> properties = null;

	private static void loadProperties() {
		final String path = System.getProperty(VITAL_PROPERTIES_FILE);
		if (path == null) {
			System.err.println("Could not load configuration from " + VITAL_PROPERTIES_FILE);
			return;
		}
		try {
			System.out.println("Loading configuration from " + path);
			final File file = new File(path);
			final Properties _properties = new Properties();
			_properties.loadFromXML(new FileInputStream(file));

			properties = new HashMap<>();
			for (final String name : _properties.stringPropertyNames()) {
				properties.put(name, _properties.getProperty(name));
			}
		} catch (IOException e) {
			properties = null;
			System.err.println("Could not load configuration from " + VITAL_PROPERTIES_FILE);
		}

	}

	public static String getProperty(String name) {
		if (properties == null) {
			loadProperties();
		}
		return properties.get(name);
	}

	public static String getProperty(String name, String defaultValue) {
		if (properties == null) {
			loadProperties();
		}
		return properties.containsKey(name) ? properties.get(name) : defaultValue;
	}
}
