package core;

import java.util.Timer;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.impl.cookie.BasicClientCookie;

import api.VitalObservation;
import api.VitalSensor;
import api.VitalService;
import api.VitalSystem;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import util.DMSUtils;
import util.DMSPermission;

@Path("/")
public class DMS {

	// private final static Logger logger = LoggerFactory.getLogger(DMS.class);

	final static int responseSuccess = 200;
	final static int responseUnauthorize = 401;
	final static int responseBadServer = 500;

	static Timer timer;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Object status() {
		// DMSPermission.securityDMSAuth();// Temporary blocked for testing
		return Response.status(Response.Status.ACCEPTED)
				.entity("{\"message\" : \"Welcome to VITAL DMS.\"}").build();
	}

	@GET
	@Path("/dmsauth")
	@Produces(MediaType.APPLICATION_JSON)
	public Object DMSAuth() {
		DMSPermission.securityDMSAuth();// Temporary blocked for
		return Response.status(Response.Status.ACCEPTED)
				.entity("{\"message\" : \"DMS Re-authenticated.\"}").build();
	}

	@POST
	@Path("/insertSystem")
	@Produces(MediaType.APPLICATION_JSON)
	public Object insertSystem(String data) {

		try {

			if (VitalSystem.insertSystem(data.trim())) {

				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Data pushed.\"}").build();
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Error is inserting data.\"}")
						.build();
			}

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception occured.\"}").build();

		}

	}

	@POST
	@Path("/insertService")
	@Produces(MediaType.APPLICATION_JSON)
	public Object insertService(String data) {

		try {
			if (VitalService.insertService(data.trim())) {

				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Data pushed.\"}").build();
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Error is inserting data.\"}")
						.build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception occured.\"}").build();

		}

	}

	@POST
	@Path("/insertSensor")
	@Produces(MediaType.APPLICATION_JSON)
	public Object insertSensor(String data) {

		try {
			if (VitalSensor.insertSensor(data.trim())) {

				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Data pushed.\"}").build();
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Error is inserting data.\"}")
						.build();
			}

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception occured.\"}").build();

		}

	}

	@POST
	@Path("/insertObservation")
	@Produces(MediaType.APPLICATION_JSON)
	public Object insertObservation(String data) {

		try {
			if (VitalObservation.insertObservation(data.trim())) {

				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Data pushed.\"}").build();
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity("{\"message\" : \"Error is inserting data.\"}")
						.build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception occured.\"}").build();

		}

	}

	@POST
	@Path("/querySystem")
	@Produces(MediaType.APPLICATION_JSON)
	public Object querySystem(String data,
			@CookieParam("vitalAccessToken") Cookie cookie) {
		DBObject query = DMSUtils
				.encodeKeys((DBObject) JSON.parse(data.trim()));
		org.apache.http.cookie.Cookie c = null;
		if (cookie != null)
			c = new BasicClientCookie(cookie.getName(), cookie.getValue());
		try {
			if (DMSApp.isSecurityEnabled) {
				int code = DMSPermission.checkPermission(c);
				// System.out.println("Code: " + code);
				if (code == DMSPermission.successfulPermission) {
					DBObject perm = DMSUtils.encodeKeys(DMSPermission
							.getPermission());

					DBObject filteredQuery = DMSPermission.permissionFilter(
							perm, query);
					return Response.status(Response.Status.ACCEPTED)
							.entity(VitalSystem.querySystem(filteredQuery))
							.build();
				} else if (code == DMSPermission.accessTokenNotFound) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else if (code == DMSPermission.unsuccessful) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("{\"message\" : \"Internal Server error.\"}")
							.build();
				}
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity(VitalSystem.querySystem(query)).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception - Internal Server error.\"}")
					.build();
		}

	}

	@POST
	@Path("/queryService")
	@Produces(MediaType.APPLICATION_JSON)
	public Object queryService(String data,
			@CookieParam("vitalAccessToken") Cookie cookie) {
		DBObject query = DMSUtils
				.encodeKeys((DBObject) JSON.parse(data.trim()));
		org.apache.http.cookie.Cookie c = null;
		if (cookie != null)
			c = new BasicClientCookie(cookie.getName(), cookie.getValue());
		try {
			if (DMSApp.isSecurityEnabled) {
				int code = DMSPermission.checkPermission(c);
				// System.out.println("Code: " + code);
				if (code == DMSPermission.successfulPermission) {
					DBObject perm = DMSUtils.encodeKeys(DMSPermission
							.getPermission());

					DBObject filteredQuery = DMSPermission.permissionFilter(
							perm, query);
					return Response.status(Response.Status.ACCEPTED)
							.entity(VitalService.queryService(filteredQuery))
							.build();
				} else if (code == DMSPermission.accessTokenNotFound) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else if (code == DMSPermission.unsuccessful) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("{\"message\" : \"Internal Server error.\"}")
							.build();
				}
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity(VitalService.queryService(query)).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception - Internal Server error.\"}")
					.build();
		}

	}

	@POST
	@Path("/querySensor")
	@Produces(MediaType.APPLICATION_JSON)
	public Object querySensor(String data,
			@CookieParam("vitalAccessToken") Cookie cookie) {
		DBObject query = DMSUtils
				.encodeKeys((DBObject) JSON.parse(data.trim()));
		org.apache.http.cookie.Cookie c = null;
		if (cookie != null)
			c = new BasicClientCookie(cookie.getName(), cookie.getValue());
		try {
			if (DMSApp.isSecurityEnabled) {
				int code = DMSPermission.checkPermission(c);
				// System.out.println("Code: " + code);
				if (code == DMSPermission.successfulPermission) {
					DBObject perm = DMSUtils.encodeKeys(DMSPermission
							.getPermission());

					DBObject filteredQuery = DMSPermission.permissionFilter(
							perm, query);
					return Response.status(Response.Status.ACCEPTED)
							.entity(VitalSensor.querySensor(filteredQuery))
							.build();
				} else if (code == DMSPermission.accessTokenNotFound) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else if (code == DMSPermission.unsuccessful) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("{\"message\" : \"Internal Server error.\"}")
							.build();
				}
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity(VitalSensor.querySensor(query)).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception - Internal Server error.\"}")
					.build();
		}

	}

	@POST
	@Path("/queryObservation")
	@Produces(MediaType.APPLICATION_JSON)
	public Object queryObservation(String data,
			@CookieParam("vitalAccessToken") Cookie cookie) {
		DBObject query = DMSUtils
				.encodeKeys((DBObject) JSON.parse(data.trim()));
		org.apache.http.cookie.Cookie c = null;
		if (cookie != null)
			c = new BasicClientCookie(cookie.getName(), cookie.getValue());
		try {

			if (DMSApp.isSecurityEnabled) {
				int code = DMSPermission.checkPermission(c);
				// System.out.println("Code: " + code);
				if (code == DMSPermission.successfulPermission) {
					DBObject perm = DMSUtils.encodeKeys(DMSPermission
							.getPermission());

					DBObject filteredQuery = DMSPermission.permissionFilter(
							perm, query);
					return Response
							.status(Response.Status.ACCEPTED)
							.entity(VitalObservation
									.queryObservation(filteredQuery)).build();
				} else if (code == DMSPermission.accessTokenNotFound) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else if (code == DMSPermission.unsuccessful) {
					return Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("{\"message\" : \"Unauthorized. vitalAccessToken Not Found.\"}")
							.build();
				} else {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("{\"message\" : \"Internal Server error.\"}")
							.build();
				}
			} else {
				return Response.status(Response.Status.ACCEPTED)
						.entity(VitalObservation.queryObservation(query))
						.build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\" : \"Exception - Internal Server error.\"}")
					.build();
		}

	}

}
