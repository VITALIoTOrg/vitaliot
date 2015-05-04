package utils;

public class SessionUtils {

	private static String adminAuthToken = "";
	
	public static synchronized String getAdminAuhtToken() {
		return adminAuthToken;
	}
	
	public static synchronized void setAdminAuthToken(String value) {
		adminAuthToken = value;
	}
	
	
}
