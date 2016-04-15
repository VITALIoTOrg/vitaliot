/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



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
