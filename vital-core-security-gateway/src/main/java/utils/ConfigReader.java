package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	
	private static ConfigReader instance;
	private Properties config;
	
	public static final String PROXY_HOST = "PROXY_HOST";
	public static final String SECURITY_HOST = "SECURITY_HOST";
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	
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
