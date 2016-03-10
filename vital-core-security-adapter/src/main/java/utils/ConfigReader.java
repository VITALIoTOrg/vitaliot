package utils;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class ConfigReader {
	
	private static ConfigReader instance;
	private Properties config;
	
	public static final String IDP_HOST = "vital-core-security-adapter.idp-host";
	public static final String IDP_PORT = "vital-core-security-adapter.idp-port";
    public static final String IDP_PATH = "vital-core-security-adapter.idp-path";
	public static final String PROXY_HOST = "vital-core-security-adapter.proxy-host";
	public static final String PROXY_PORT = "vital-core-security-adapter.proxy-port";
	public static final String SSO_TOKEN = "vital-core-security-adapter.sso-token";
	public static final String ALT_TOKEN = "vital-core-security-adapter.alt-token";
	
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
