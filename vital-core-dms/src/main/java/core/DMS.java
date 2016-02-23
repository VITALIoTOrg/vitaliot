package core;

import java.util.Timer;
import java.util.TimerTask;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.VitalObservation;
import api.VitalSensor;
import api.VitalService;
import api.VitalSystem;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import util.DMSUtils;
import util.DMSPermission;

@Path("/")
public class DMS {

	private final static Logger logger = LoggerFactory.getLogger(DMS.class);

	final static int responseSuccess = 200;
	final static int responseUnauthorize = 401;
	final static int responseBadServer = 500;

	// final static boolean isSecurityEnabled = true;

	static Timer timer;

	/*
	 * public static void main(String[] args) {
	 * 
	 * timer = new Timer(); timer.scheduleAtFixedRate(new TimerTask() {
	 * 
	 * @Override public void run() { if (logger.isDebugEnabled()) {
	 * logger.info("Re-authenticating DMS..."); }
	 * DMSPermission.securityDMSAuth(); // Temporary blocked for // testing. }
	 * }, 5000, 15 * 60 * 1000);
	 */

	/*
	 * Spark.get(new Route("/") {
	 * 
	 * @Override public Object handle(Request request, Response response) { //
	 * logger.info("GET: /"); DMSPermission.securityDMSAuth();// Temporary
	 * blocked for // testing. return "Welcome to DMS."; } });
	 * 
	 * Spark.post(new Route("/insertSystem") {
	 * 
	 * @Override public Object handle(Request request, Response response) { //
	 * logger.info("POST: /insertSystem");
	 * 
	 * DBObject objRet = new BasicDBObject(); response.type("application/json");
	 * try { String inputData = request.body().trim();
	 * VitalSystem.insertSystem(inputData); objRet.put("status", "success");
	 * response.status(responseSuccess); //
	 * logger.info("Successful data inserted. /insertSystem"); return objRet;
	 * 
	 * } catch (Exception e) { // logger.error("Error in /insertSystem. " + //
	 * e.getMessage()); response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); } } });
	 * 
	 * Spark.post(new Route("/insertService") {
	 * 
	 * @Override public Object handle(Request request, Response response) { //
	 * logger.info("POST: /insertService");
	 * 
	 * DBObject objRet = new BasicDBObject(); response.type("application/json");
	 * try { String inputData = request.body().trim();
	 * VitalService.insertService(inputData); objRet.put("status", "success");
	 * response.status(responseSuccess); //
	 * logger.info("Successful data inserted. /insertService"); return objRet;
	 * 
	 * } catch (Exception e) { // logger.error("Error in /insertService. " + //
	 * e.getMessage()); response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); } } });
	 * 
	 * Spark.post(new Route("/insertSensor") {
	 * 
	 * @Override public Object handle(Request request, Response response) { //
	 * logger.info("POST: /insertSensor");
	 * 
	 * DBObject objRet = new BasicDBObject(); response.type("application/json");
	 * try { String inputData = request.body().trim();
	 * VitalSensor.insertSensor(inputData); objRet.put("status", "success");
	 * response.status(responseSuccess); //
	 * logger.info("Successful data inserted. /insertSensor"); return objRet;
	 * 
	 * } catch (Exception e) { // logger.error("Error in /insertSensor. " + //
	 * e.getMessage()); response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); } } });
	 * 
	 * Spark.post(new Route("/insertObservation") {
	 * 
	 * @Override public Object handle(Request request, Response response) { //
	 * logger.info("POST: /insertObservation");
	 * 
	 * DBObject objRet = new BasicDBObject(); response.type("application/json");
	 * try { String inputData = request.body().trim();
	 * VitalObservation.insertObservation(inputData); objRet.put("status",
	 * "success"); response.status(responseSuccess); //
	 * logger.info("Successful data inserted. /insertObservation"); return
	 * objRet;
	 * 
	 * } catch (Exception e) { // logger.error("Error in /insertObservation. "+
	 * // e.getMessage()); response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); } } });
	 * 
	 * Spark.post(new Route("/querySystem") {
	 * 
	 * @Override public Object handle(Request request, Response response) {
	 * DBObject query = DMSUtils.encodeKeys((DBObject) JSON
	 * .parse(request.body().trim())); try { response.type("application/json");
	 * if (isSecurityEnabled) {
	 * 
	 * int code = DMSPermission.checkPermission(request);
	 * System.out.println("Code: " + code); if (code ==
	 * DMSPermission.successfulPermission) { DBObject perm =
	 * DMSUtils.encodeKeys(DMSPermission .getPermission());
	 * 
	 * DBObject filteredQuery = DMSPermission .permissionFilter(perm, query);
	 * response.status(responseSuccess); response.type("application/json+ld");
	 * return VitalSystem.querySystem(filteredQuery);
	 * 
	 * } else if (code == DMSPermission.accessTokenNotFound) {
	 * response.status(responseUnauthorize); return DMSUtils .sendError(
	 * "Unauthorized. vitalAccessToken Not Found.", 401); } else if (code ==
	 * DMSPermission.unsuccessful) { response.status(responseUnauthorize);
	 * return DMSUtils .sendError(
	 * "Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
	 * 401);
	 * 
	 * } else { response.status(responseBadServer); return
	 * DMSUtils.sendError("Internal Server error.", 500); } } else {
	 * response.status(responseSuccess); return VitalSystem.querySystem(query);
	 * }
	 * 
	 * } catch (Exception e) { response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); }
	 * 
	 * }
	 * 
	 * });
	 * 
	 * Spark.post(new Route("/queryService") {
	 * 
	 * @Override public Object handle(Request request, Response response) {
	 * DBObject query = DMSUtils.encodeKeys((DBObject) JSON
	 * .parse(request.body().trim())); try { response.type("application/json");
	 * if (isSecurityEnabled) {
	 * 
	 * int code = DMSPermission.checkPermission(request);
	 * 
	 * if (code == DMSPermission.successfulPermission) { DBObject perm =
	 * DMSUtils.encodeKeys(DMSPermission .getPermission());
	 * 
	 * DBObject filteredQuery = DMSPermission .permissionFilter(perm, query);
	 * response.status(responseSuccess); response.type("application/json+ld");
	 * return VitalService.queryService(filteredQuery);
	 * 
	 * } else if (code == DMSPermission.accessTokenNotFound) {
	 * response.status(responseUnauthorize); return DMSUtils .sendError(
	 * "Unauthorized. vitalAccessToken Not Found.", 401); } else if (code ==
	 * DMSPermission.unsuccessful) { response.status(responseUnauthorize);
	 * return DMSUtils .sendError(
	 * "Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
	 * 401);
	 * 
	 * } else { response.status(responseBadServer); return
	 * DMSUtils.sendError("Internal Server error.", 500); } } else {
	 * response.status(responseSuccess); return
	 * VitalService.queryService(query); }
	 * 
	 * } catch (Exception e) { response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); }
	 * 
	 * }
	 * 
	 * });
	 * 
	 * Spark.post(new Route("/querySensor") {
	 * 
	 * @Override public Object handle(Request request, Response response) {
	 * DBObject query = DMSUtils.encodeKeys((DBObject) JSON
	 * .parse(request.body().trim())); try { response.type("application/json");
	 * if (isSecurityEnabled) {
	 * 
	 * int code = DMSPermission.checkPermission(request);
	 * 
	 * if (code == DMSPermission.successfulPermission) { DBObject perm =
	 * DMSUtils.encodeKeys(DMSPermission .getPermission());
	 * 
	 * DBObject filteredQuery = DMSPermission .permissionFilter(perm, query);
	 * response.status(responseSuccess); response.type("application/json+ld");
	 * return VitalSensor.querySensor(filteredQuery);
	 * 
	 * } else if (code == DMSPermission.accessTokenNotFound) {
	 * response.status(responseUnauthorize); return DMSUtils .sendError(
	 * "Unauthorized. vitalAccessToken Not Found.", 401); } else if (code ==
	 * DMSPermission.unsuccessful) { response.status(responseUnauthorize);
	 * return DMSUtils .sendError(
	 * "Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
	 * 401);
	 * 
	 * } else { response.status(responseBadServer); return
	 * DMSUtils.sendError("Internal Server error.", 500); } } else {
	 * response.status(responseSuccess); return VitalSensor.querySensor(query);
	 * }
	 * 
	 * } catch (Exception e) { response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); }
	 * 
	 * }
	 * 
	 * });
	 * 
	 * Spark.post(new Route("queryObservation") {
	 * 
	 * @Override public Object handle(Request request, Response response) {
	 * DBObject query = DMSUtils.encodeKeys((DBObject) JSON
	 * .parse(request.body().trim())); try { response.type("application/json");
	 * if (isSecurityEnabled) {
	 * 
	 * int code = DMSPermission.checkPermission(request);
	 * 
	 * if (code == DMSPermission.successfulPermission) { DBObject perm =
	 * DMSUtils.encodeKeys(DMSPermission .getPermission());
	 * 
	 * DBObject filteredQuery = DMSPermission .permissionFilter(perm, query);
	 * response.status(responseSuccess); response.type("application/json+ld");
	 * return VitalObservation .queryObservation(filteredQuery);
	 * 
	 * } else if (code == DMSPermission.accessTokenNotFound) {
	 * response.status(responseUnauthorize); return DMSUtils .sendError(
	 * "Unauthorized. vitalAccessToken Not Found.", 401); } else if (code ==
	 * DMSPermission.unsuccessful) { response.status(responseUnauthorize);
	 * return DMSUtils .sendError(
	 * "Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
	 * 401);
	 * 
	 * } else { response.status(responseBadServer); return
	 * DMSUtils.sendError("Internal Server error.", 500); } } else {
	 * response.status(responseSuccess); return
	 * VitalObservation.queryObservation(query); }
	 * 
	 * } catch (Exception e) { response.status(responseBadServer); return
	 * DMSUtils.sendException(response, e); }
	 * 
	 * }
	 * 
	 * });
	 * 
	 * }
	 */

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
			VitalSystem.insertSystem(data.trim());

			return Response.status(Response.Status.ACCEPTED)
					.entity("{\"message\" : \"Data pushed.\"}").build();

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
			VitalService.insertService(data.trim());

			return Response.status(Response.Status.ACCEPTED)
					.entity("{\"message\" : \"Data pushed.\"}").build();

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
			VitalSensor.insertSensor(data.trim());

			return Response.status(Response.Status.ACCEPTED)
					.entity("{\"message\" : \"Data pushed.\"}").build();

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
			VitalObservation.insertObservation(data.trim());

			return Response.status(Response.Status.ACCEPTED)
					.entity("{\"message\" : \"Data pushed.\"}").build();

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
		org.apache.http.cookie.Cookie c = new BasicClientCookie(
				cookie.getName(), cookie.getValue());

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
		org.apache.http.cookie.Cookie c = new BasicClientCookie(
				cookie.getName(), cookie.getValue());

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
		org.apache.http.cookie.Cookie c = new BasicClientCookie(
				cookie.getName(), cookie.getValue());

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
		org.apache.http.cookie.Cookie c = new BasicClientCookie(
				cookie.getName(), cookie.getValue());

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
