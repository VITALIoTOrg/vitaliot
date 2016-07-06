package eu.vital.orchestrator.util;

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

	private Map<String, String> properties = new HashMap<>();

	@Inject
	private Logger log;

	@PostConstruct
	private void init() {
		final String path = System.getProperty(VITAL_PROPERTIES_FILE);
		if (path == null) {
			log.warning("Could not load configuration from " + VITAL_PROPERTIES_FILE);
			return;
		}
		try {
			log.warning("Loading configuration from " + path);
			final File file = new File(path);
			final Properties properties = new Properties();
			properties.loadFromXML(new FileInputStream(file));
			for (final String name : properties.stringPropertyNames()) {
				this.properties.put(name, properties.getProperty(name));
			}
		} catch (IOException e) {
			log.warning("Could not load configuration from " + VITAL_PROPERTIES_FILE);
		}
	}

	public String getProperty(String name) {
		return properties.get(name);
	}

	public String getProperty(String name, String defaultValue) {
		return properties.containsKey(name) ? properties.get(name) : defaultValue;
	}
}
