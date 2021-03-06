package eu.vital.discoverer.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import eu.vital.discoverer.exception.ConnectionErrorException;
import eu.vital.discoverer.exception.ConnectionErrorExceptionHandler;
import eu.vital.discoverer.exception.DiscoveryApplicationExceptionHandler;
import eu.vital.discoverer.exception.WrongInputExceptionHandler;
import eu.vital.discoverer.rest.RestInterface;

public class DiscovererREST  extends Application {

	private Set<Object> singletons = new HashSet<Object>();

	public DiscovererREST() {
		singletons.add(new RestInterface());
		singletons.add(new PpiImplementation());
	}

	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(WrongInputExceptionHandler.class);
        classes.add(DiscoveryApplicationExceptionHandler.class);
        classes.add(ConnectionErrorExceptionHandler.class);
        /* add your additional JAX-RS classes here */
        return classes;
    }

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
