package core;

import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import util.DMSPermission;

@ApplicationPath("/")
public class DMSApp extends Application {

	static Timer timer;
	final static boolean isSecurityEnabled = true;

	public DMSApp() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println("Re-authenticating DMS");
				DMSPermission.securityDMSAuth();
			}
		}, 5000, (15 * 60 * 1000));
	}
}