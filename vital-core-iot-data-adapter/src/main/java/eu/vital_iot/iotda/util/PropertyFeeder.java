package eu.vital_iot.iotda.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * Feeds the properties with values.
 * 
 * @author k4t3r1n4
 *
 */
public class PropertyFeeder {

	/**
	 * The property loader.
	 */
	@Inject
	private PropertyLoader loader;

	/**
	 * Injects the value of a property at the specified point.
	 * 
	 * @param injectionPoint
	 *            the injection point.
	 * @return the value of the property.
	 */
	@Produces
	@Property(name = "")
	public String getProperty(InjectionPoint injectionPoint) {

		final String name = injectionPoint.getAnnotated().getAnnotation(Property.class).name();
		final String value = loader.getProperty(name);

		if (value == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Unknown property: " + name);

		return value;
	}

	/**
	 * Injects the value of a property as an integer at the specified point.
	 * 
	 * @param injectionPoint
	 *            the injection point.
	 * @return the value of the property.
	 */
	@Produces
	@Property(name = "")
	public Integer getPropertyI(InjectionPoint injectionPoint) {

		String value = getProperty(injectionPoint);
		return value == null ? null : Integer.valueOf(value);
	}
}