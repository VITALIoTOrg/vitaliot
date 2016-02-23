package eu.vital_iot.iotda.ppi.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Identifies properties.
 * 
 * @author k4t3r1n4
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR })
public @interface Property {

	/**
	 * Gets the name of this property.
	 * 
	 * @return the name of this property.
	 */
	@Nonbinding
	String name();
}