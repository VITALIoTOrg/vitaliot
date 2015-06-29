package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigReader {

	
	private static ConfigReader instance;
	private Properties config;
	
	public static final String IDP_HOST = "IDP_HOST";
	public static final String IDP_PORT = "IDP_PORT";
	public static final String SNMP_PORT = "SNMP_PORT";
	public static final String USER_ADM = "USER_ADM";
	public static final String PWD_ADM = "PWD_ADM";
	public static final String AUTH_TOKEN = "AUTH_TOKEN";
	
	
	private ConfigReader() {
		config = new Properties();
		InputStream is = this.getClass().getResourceAsStream("/config.properties");
		
		try {
			config.load(is);
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
