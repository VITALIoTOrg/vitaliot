package utils;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class ConfigReader {
	
	private static ConfigReader instance;
	private Properties config;
	
	public static final String SNMP_HOST = "vital-core-security-snmp-exposer.snmp-host";
	public static final String SNMP_PORT = "vital-core-security-snmp-exposer.snmp-port";
	
	private ConfigReader() {
        String fileName = System.getProperty("jboss.server.config.dir") + "/vital-properties.xml";
        File file = new File(fileName);

        config = new Properties();
		
		try {
			config.loadFromXML(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ConfigReader getInstance() {
		if (instance == null) {
			instance = new ConfigReader();
		}
		return instance;
	}
	
	public String get(String key) {
		return config.getProperty(key);
	}
}
