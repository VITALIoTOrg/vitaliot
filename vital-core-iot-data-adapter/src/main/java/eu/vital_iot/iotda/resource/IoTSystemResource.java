package eu.vital_iot.iotda.resource;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.vital_iot.iotda.common.IoTSystem;
import eu.vital_iot.iotda.service.Registry;

/**
 * The IoT system resource.
 * 
 * @author k4t3r1n4
 *
 */
@Path("/systems")
public class IoTSystemResource {

	/**
	 * The registry.
	 */
	@EJB
	private Registry registry;

	/**
	 * Gets all registered IoT systems.
	 * 
	 * @return a list that contains all registered IoT systems.
	 * @throws Exception
	 *             in case getting fails.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<IoTSystem> get() throws Exception {
		return registry.get();
	}

	/**
	 * Gets the registered IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 * @return the registered IoT system with the given ID.
	 * @throws Exception
	 *             in case getting fails.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public IoTSystem get(@PathParam("id") String id) throws Exception {
		return registry.get(id);
	}

	/**
	 * Updates the registered IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of the IoT system to update.
	 * @param iotsystem
	 *            the IoT system to update.
	 * @throws Exception
	 *             in case updating fails.
	 */
	@POST
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void edit(@PathParam("id") String id, IoTSystem iotsystem) throws Exception {
		registry.edit(iotsystem);
	}

	/**
	 * Registers the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to register.
	 * @throws Exception
	 *             in case registering fails.
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(IoTSystem iotsystem) throws Exception {
		registry.register(iotsystem);
	}

	/**
	 * Deregisters the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of the IoT system to deregister.
	 * @throws Exception
	 *             in case deregistering fails.
	 */
	@DELETE
	@Path("/{id}")
	public void deregister(@PathParam("id") String id) throws Exception {
		registry.deregister(id);
	}

	/**
	 * Refreshes the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to refresh.
	 * @throws Exception
	 *             in case refreshing fails.
	 */
	@GET
	@Path("/{id}/refresh")
	public void refresh(@PathParam("id") String id) throws Exception {
		registry.refresh(id);
	}
}