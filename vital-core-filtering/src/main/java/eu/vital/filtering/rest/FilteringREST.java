package eu.vital.filtering.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import eu.vital.filtering.exception.ConnectionErrorExceptionHandler;
import eu.vital.filtering.exception.WrongInputExceptionHandler;
import eu.vital.filtering.exception.WrongJSONFormatExceptionHandler;

public class FilteringREST extends Application{
	
	private Set<Object> singletons = new HashSet<Object>();

	public FilteringREST() {
		singletons.add(new RestInterface());
		singletons.add(new PpiImplementation());
	}
	
	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(WrongInputExceptionHandler.class);
        classes.add(WrongJSONFormatExceptionHandler.class);
        classes.add(ConnectionErrorExceptionHandler.class);
        /* add your additional JAX-RS classes here */
        return classes;
    }

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
