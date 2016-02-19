package eu.vital.filtering.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import eu.vital.filtering.query.FilteringQuery;
import eu.vital.filtering.query.QueryFactory;
import eu.vital.filtering.query.QueryTypeEnumeration;
import eu.vital.filtering.util.FilteringMetadataReader;
import eu.vital.filtering.util.FilteringProperties;


@Path("")
public class RestInterface {

	private static Logger logger=Logger.getLogger(RestInterface.class);
	private FilteringProperties props;
	private FilteringMetadataReader metadataReader;

	public RestInterface() {
		props=new FilteringProperties();
		metadataReader=new FilteringMetadataReader();
	}

	@POST
	@Path("/threshold")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public Response thresholdFiltering(@CookieParam("vitalAccessToken") String cookie, JSONObject input) {
		if(cookie==null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}else{

			String completeCookie="vitalAccessToken="+cookie;
			FilteringQuery query=QueryFactory.getQuery(QueryTypeEnumeration.THRESHOLD_QUERY);
			query.setInputJSON(input);
			if(query.isInputAppropriate()){
				query.SetCookie(completeCookie);
				query.executeQuery();
				return Response.status(Response.Status.OK).entity(query.getQueryResult()).build();
			}else
			{
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
	}

	@POST
	@Path("/resampling")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public Response resampleFiltering(@CookieParam("vitalAccessToken") String cookie, JSONObject input){

		if(cookie==null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}else{
			String completeCookie="vitalAccessToken="+cookie;
			FilteringQuery query=QueryFactory.getQuery(QueryTypeEnumeration.RESAMPLING_QUERY);
			query.setInputJSON(input);
			if(query.isInputAppropriate()){
				query.SetCookie(completeCookie);
				query.executeQuery();
				return Response.status(Response.Status.OK).entity(query.getQueryResult()).build();
			}else
			{
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
	}


	@GET
	@Path("/contexts/{name}")
	@Produces("application/ld+json")
	public String getContext(@PathParam("name") String name) throws IOException{
		InputStream fis=this.getClass().getResourceAsStream("/contexts/"+name);
		StringBuilder sb=new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String line;
		while((line=reader.readLine())!=null){
			sb.append(line);
		}

		return sb.toString();
	}

}
