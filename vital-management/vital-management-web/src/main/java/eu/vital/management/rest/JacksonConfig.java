package eu.vital.management.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonConfig implements ContextResolver<ObjectMapper> {

	@Inject
	ObjectMapper objectMapper;

	@Override
	public ObjectMapper getContext(final Class<?> type) {
		return objectMapper;
	}

}
