package core;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.VitalObservation;
import api.VitalSensor;
import api.VitalService;
import api.VitalSystem;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import util.DMSUtils;
import util.DMSPermission;

public class DMS {

	private final static Logger logger = LoggerFactory.getLogger(DMS.class);

	final static int responseSuccess = 200;
	final static int responseUnauthorize = 401;
	final static int responseBadServer = 500;

	final static boolean isSecurityEnabled = true;

	static Timer timer;

	public static void main(String[] args) {

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				logger.info("Re-authenticating DMS...");
				DMSPermission.securityDMSAuth(); // Temporary blocked for
													// testing.
			}
		}, 20 * 60 * 1000, 20 * 60 * 1000);

		Spark.get(new Route("/") {

			@Override
			public Object handle(Request request, Response response) {
				logger.info("GET: /");
				DMSPermission.securityDMSAuth();// Temporary blocked for
												// testing.
				return "Welcome to DMS.";
			}
		});

		Spark.post(new Route("/insertSystem") {

			@Override
			public Object handle(Request request, Response response) {
				logger.info("POST: /insertSystem");

				DBObject objRet = new BasicDBObject();
				response.type("application/json");
				try {
					String inputData = request.body().trim();
					VitalSystem.insertSystem(inputData);
					objRet.put("status", "success");
					response.status(responseSuccess);
					logger.info("Successful data inserted. /insertSystem");
					return objRet;

				} catch (Exception e) {
					logger.error("Error in /insertSystem. " + e.getMessage());
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}
			}
		});

		Spark.post(new Route("/insertService") {

			@Override
			public Object handle(Request request, Response response) {
				logger.info("POST: /insertService");

				DBObject objRet = new BasicDBObject();
				response.type("application/json");
				try {
					String inputData = request.body().trim();
					VitalService.insertService(inputData);
					objRet.put("status", "success");
					response.status(responseSuccess);
					logger.info("Successful data inserted. /insertService");
					return objRet;

				} catch (Exception e) {
					logger.error("Error in /insertService. " + e.getMessage());
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}
			}
		});

		Spark.post(new Route("/insertSensor") {

			@Override
			public Object handle(Request request, Response response) {
				logger.info("POST: /insertSensor");

				DBObject objRet = new BasicDBObject();
				response.type("application/json");
				try {
					String inputData = request.body().trim();
					VitalSensor.insertSensor(inputData);
					objRet.put("status", "success");
					response.status(responseSuccess);
					logger.info("Successful data inserted. /insertSensor");
					return objRet;

				} catch (Exception e) {
					logger.error("Error in /insertSensor. " + e.getMessage());
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}
			}
		});

		Spark.post(new Route("/insertObservation") {

			@Override
			public Object handle(Request request, Response response) {
				logger.info("POST: /insertObservation");

				DBObject objRet = new BasicDBObject();
				response.type("application/json");
				try {
					String inputData = request.body().trim();
					VitalObservation.insertObservation(inputData);
					objRet.put("status", "success");
					response.status(responseSuccess);
					logger.info("Successful data inserted. /insertObservation");
					return objRet;

				} catch (Exception e) {
					logger.error("Error in /insertObservation. "
							+ e.getMessage());
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}
			}
		});

		Spark.post(new Route("/querySystem") {

			@Override
			public Object handle(Request request, Response response) {
				DBObject query = DMSUtils.encodeKeys((DBObject) JSON
						.parse(request.body().trim()));
				try {
					response.type("application/json");
					if (isSecurityEnabled) {

						int code = DMSPermission.checkPermission(request);

						if (code == DMSPermission.successfulPermission) {
							DBObject perm = DMSUtils.encodeKeys(DMSPermission
									.getPermission());

							DBObject filteredQuery = DMSPermission
									.permissionFilter(perm, query);
							response.status(responseSuccess);
							response.type("application/json+ld");
							return VitalSystem.querySystem(filteredQuery);

						} else if (code == DMSPermission.accessTokenNotFound) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. vitalAccessToken Not Found.",
											401);
						} else if (code == DMSPermission.unsuccessful) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
											401);

						} else {
							response.status(responseBadServer);
							return DMSUtils.sendError("Internal Server error.",
									500);
						}
					} else {
						response.status(responseSuccess);
						return VitalSystem.querySystem(query);
					}

				} catch (Exception e) {
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}

			}

		});

		Spark.post(new Route("/queryService") {

			@Override
			public Object handle(Request request, Response response) {
				DBObject query = DMSUtils.encodeKeys((DBObject) JSON
						.parse(request.body().trim()));
				try {
					response.type("application/json");
					if (isSecurityEnabled) {

						int code = DMSPermission.checkPermission(request);

						if (code == DMSPermission.successfulPermission) {
							DBObject perm = DMSUtils.encodeKeys(DMSPermission
									.getPermission());

							DBObject filteredQuery = DMSPermission
									.permissionFilter(perm, query);
							response.status(responseSuccess);
							response.type("application/json+ld");
							return VitalService.queryService(filteredQuery);

						} else if (code == DMSPermission.accessTokenNotFound) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. vitalAccessToken Not Found.",
											401);
						} else if (code == DMSPermission.unsuccessful) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
											401);

						} else {
							response.status(responseBadServer);
							return DMSUtils.sendError("Internal Server error.",
									500);
						}
					} else {
						response.status(responseSuccess);
						return VitalService.queryService(query);
					}

				} catch (Exception e) {
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}

			}

		});

		Spark.post(new Route("/querySensor") {

			@Override
			public Object handle(Request request, Response response) {
				DBObject query = DMSUtils.encodeKeys((DBObject) JSON
						.parse(request.body().trim()));
				try {
					response.type("application/json");
					if (isSecurityEnabled) {

						int code = DMSPermission.checkPermission(request);

						if (code == DMSPermission.successfulPermission) {
							DBObject perm = DMSUtils.encodeKeys(DMSPermission
									.getPermission());

							DBObject filteredQuery = DMSPermission
									.permissionFilter(perm, query);
							response.status(responseSuccess);
							response.type("application/json+ld");
							return VitalSensor.querySensor(filteredQuery);

						} else if (code == DMSPermission.accessTokenNotFound) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. vitalAccessToken Not Found.",
											401);
						} else if (code == DMSPermission.unsuccessful) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
											401);

						} else {
							response.status(responseBadServer);
							return DMSUtils.sendError("Internal Server error.",
									500);
						}
					} else {
						response.status(responseSuccess);
						return VitalSensor.querySensor(query);
					}

				} catch (Exception e) {
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}

			}

		});

		Spark.post(new Route("queryObservation") {

			@Override
			public Object handle(Request request, Response response) {
				DBObject query = DMSUtils.encodeKeys((DBObject) JSON
						.parse(request.body().trim()));
				try {
					response.type("application/json");
					if (isSecurityEnabled) {

						int code = DMSPermission.checkPermission(request);

						if (code == DMSPermission.successfulPermission) {
							DBObject perm = DMSUtils.encodeKeys(DMSPermission
									.getPermission());

							DBObject filteredQuery = DMSPermission
									.permissionFilter(perm, query);
							response.status(responseSuccess);
							response.type("application/json+ld");
							return VitalObservation
									.queryObservation(filteredQuery);

						} else if (code == DMSPermission.accessTokenNotFound) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. vitalAccessToken Not Found.",
											401);
						} else if (code == DMSPermission.unsuccessful) {
							response.status(responseUnauthorize);
							return DMSUtils
									.sendError(
											"Unauthorized. Permission Denied. Please re-authorize vitalAccessToken.",
											401);

						} else {
							response.status(responseBadServer);
							return DMSUtils.sendError("Internal Server error.",
									500);
						}
					} else {
						response.status(responseSuccess);
						return VitalObservation.queryObservation(query);
					}

				} catch (Exception e) {
					response.status(responseBadServer);
					return DMSUtils.sendException(response, e);
				}

			}

		});

	}
}
