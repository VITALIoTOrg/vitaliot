package eu.vital_iot.iotda.util;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * The resources.
 * 
 * @author k4t3r1n4
 *
 */
public class Resources {

	/**
	 * Produces the logger.
	 * 
	 * @param injectionPoint
	 *            the injection point.
	 * @return the logger to inject.
	 */
	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
		return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
}