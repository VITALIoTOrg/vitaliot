package eu.vital_iot.iotda.common;

import java.io.Serializable;

/**
 * This class represents IoT systems.
 * 
 * @author k4t3r1n4
 *
 */
@SuppressWarnings("serial")
public class IoTSystem implements Serializable {

	/**
	 * The ID of this IoT system.
	 */
	private String id;

	/**
	 * The URI of this IoT system.
	 */
	private String uri;

	/**
	 * The name of this IoT system.
	 */
	private String name;

	/**
	 * The base URL of the PPI implementation provided by this IoT system.
	 */
	private String ppi;

	/**
	 * The refresh period (in minutes) of the data stemming from this IoT
	 * system.
	 */
	private int refreshPeriod;

	/**
	 * The authentication information required in order to access this IoT
	 * system.
	 */
	private AuthenticationInfo authenticationInfo = new AuthenticationInfo();

	/**
	 * Whether this IoT system is enabled.
	 */
	private boolean enabled = true;

	/**
	 * The date and time when data from this IoT system were last refreshed.
	 */
	private String lastDataRefresh;

	/**
	 * The date and time when metadata about this IoT system were last
	 * refreshed.
	 */
	private String lastMetadataRefresh;

	/**
	 * Gets the ID of this IoT system.
	 * 
	 * @return the ID of this IoT system.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID of this IoT system.
	 * 
	 * @param id
	 *            the ID of this IoT system.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the URI of this IoT system.
	 * 
	 * @return the URI of this IoT system.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the URI of this IoT system.
	 * 
	 * @param uri
	 *            the URI of this IoT system.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the name of this IoT system.
	 * 
	 * @return the name of this IoT system.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this IoT system.
	 * 
	 * @param name
	 *            the name of this IoT system.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the base URL of the PPI implementation provided by this IoT system.
	 * 
	 * @return the base URL of the PPI implementation provided by this IoT
	 *         system.
	 */
	public String getPpi() {
		return ppi;
	}

	/**
	 * Sets the base URL of the PPI implementation provided by this IoT system.
	 * 
	 * @param ppi
	 *            the base URL of the PPI implementation provided by this IoT
	 *            system.
	 */
	public void setPpi(String ppi) {
		this.ppi = ppi;
	}

	/**
	 * Gets the refresh period (in minutes) of the data stemming from this IoT
	 * system.
	 * 
	 * @return the refresh period (in minutes) of the data stemming from this
	 *         IoT system.
	 */
	public int getRefreshPeriod() {
		return refreshPeriod;
	}

	/**
	 * Sets the refresh period (in minutes) of the data stemming from this IoT
	 * system.
	 * 
	 * @param refreshPeriod
	 *            the refresh period (in minutes) of the data stemming from this
	 *            IoT system.
	 */
	public void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * Gets the authentication information required in order to access this IoT
	 * system.
	 * 
	 * @return the authentication information required in order to access this
	 *         IoT system.
	 */
	public AuthenticationInfo getAuthenticationInfo() {
		return authenticationInfo;
	}

	/**
	 * Sets the authentication information required in order to access this IoT
	 * system.
	 * 
	 * @param authenticationInfo
	 *            the authentication information required in order to access
	 *            this IoT system.
	 */
	public void setAuthenticationInfo(AuthenticationInfo authenticationInfo) {
		this.authenticationInfo = authenticationInfo;
	}

	/**
	 * Gets whether this IoT system is enabled.
	 * 
	 * @return whether this IoT system is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets whether this IoT system is enabled.
	 * 
	 * @param enabled
	 *            whether this IoT system is enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the date and time when data from this IoT system were last
	 * refreshed.
	 * 
	 * @return the date and time when data from this IoT system were last
	 *         refreshed.
	 */
	public String getLastDataRefresh() {
		return lastDataRefresh;
	}

	/**
	 * Sets the date and time when data from this IoT system were last
	 * refreshed.
	 * 
	 * @param lastDataRefresh
	 *            the date and time when data from this IoT system were last
	 *            refreshed.
	 */
	public void setLastDataRefresh(String lastDataRefresh) {
		this.lastDataRefresh = lastDataRefresh;
	}

	/**
	 * Gets the date and time when metadata about this IoT system were last
	 * refreshed.
	 * 
	 * @return the date when metadata about this IoT system were last refreshed.
	 */
	public String getLastMetadataRefresh() {
		return lastMetadataRefresh;
	}

	/**
	 * Sets the date and time when metadata about this IoT system were last
	 * refreshed.
	 * 
	 * @param lastMetadataRefresh
	 *            the date and time when metadata about this IoT system were
	 *            last refreshed.
	 */
	public void setLastMetadataRefresh(String lastMetadataRefresh) {
		this.lastMetadataRefresh = lastMetadataRefresh;
	}

	/**
	 * Returns the string representation of this IoT system.
	 */
	@Override
	public String toString() {
		return "iot-system { id: " + id + "}";
	}

	/**
	 * This class represents information required for basic authentication.
	 * 
	 * @author k4t3r1n4
	 *
	 */
	public static class AuthenticationInfo {

		/**
		 * The username.
		 */
		public String username;

		/**
		 * The password.
		 */
		public String password;
	}
}