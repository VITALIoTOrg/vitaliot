/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

import eu.vital.discoverer.query.DiscoverQuery;
import eu.vital.discoverer.query.QueryFactory;
import eu.vital.discoverer.query.QueryTypeEnum;
import eu.vital.discoverer.util.DiscoveryMetadataReader;
import eu.vital.discoverer.util.DiscoveryProperties;



@Path("")
public class RestInterface {

	/*
	 * Class to implement all the functions provided to users by Discoverer REST Interface
	 */

	private static Logger logger=Logger.getLogger(RestInterface.class);
	private DiscoveryProperties props;
	private DiscoveryMetadataReader metadataReader;

	public RestInterface() {
		props=new DiscoveryProperties();
		metadataReader=new DiscoveryMetadataReader();
	}

	@GET
	@Path("/ConnDMS")
	@Produces("application/ld+json")
	public String connectionTest() {

		String value="OFF";
		InetAddress DMS_URL;
		try {
			DMS_URL = InetAddress.getByName(props.getProperty(DiscoveryProperties.DMS_ENDPOINT_ADDRESS));

			final int DMS_Port = Integer.parseInt(props.getProperty(DiscoveryProperties.DMS_ENDPOINT_PORT));
			ConnDMS connection = new ConnDMS ();

			if (connection.canConnect(DMS_URL, DMS_Port)){
				value = "ON";
				logger.info("ConnDMS=ON");
			}

		} catch (UnknownHostException e) {
			value="OFF";
			logger.warn("ConnDMS=OFF");
		}

		return "{\n  \"@context\":\"http://vital-iot.org/contexts/service.jsonld\",\n  "
		+ "\"type\":\"ServiceDiscovery/ConnDMS\",\n  "
		+ "\"hrest:hasAddress\":\"BASE_URL/discoverer/ConnDMS\",\n  "
		+ "\"hrest:hasMethod\":\"hrest:GET\",\n  "
		+ "\"hrest:status\":\""+ value +"\"\n}";

	}

	@POST
	@Path("/ico")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public Response discoverICOs(@CookieParam("vitalAccessToken") String cookie, JSONObject input){
		if(cookie==null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}else{

			String completeCookie="vitalAccessToken="+cookie;
			DiscoverQuery query=QueryFactory.getQuery(QueryTypeEnum.ICOS_QUERY);
			if(query.checkJSONInput(input))
			{
				query.setInputJSON(input);
				query.setCookie(completeCookie);
				query.executeQuery();

				return Response.status(Response.Status.OK).entity(query.getQueryResult()).build();
			}
			else{
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
	}

	@POST
	@Path("/system")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public Response discoverServices(@CookieParam("vitalAccessToken") String cookie,JSONObject input){

		if(cookie==null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}else{

			String completeCookie="vitalAccessToken="+cookie;

			DiscoverQuery query=QueryFactory.getQuery(QueryTypeEnum.SYSTEMS_QUERY);
			if(query.checkJSONInput(input))
			{
				query.setInputJSON(input);
				query.setCookie(completeCookie);
				query.executeQuery();


				return Response.status(Response.Status.OK).entity(query.getQueryResult()).build();
			}
			else{
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
	}

	@POST
	@Path("/service")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/ld+json")
	public Response discoverSystems(@CookieParam("vitalAccessToken") String cookie,JSONObject input){
		if(cookie==null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}else{

			String completeCookie="vitalAccessToken="+cookie;

			DiscoverQuery query=QueryFactory.getQuery(QueryTypeEnum.SERVICES_QUERY);
			if(query.checkJSONInput(input))
			{
				query.setInputJSON(input);
				query.setCookie(completeCookie);
				query.executeQuery();


				return Response.status(Response.Status.OK).entity(query.getQueryResult()).build();
			}
			else{
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
