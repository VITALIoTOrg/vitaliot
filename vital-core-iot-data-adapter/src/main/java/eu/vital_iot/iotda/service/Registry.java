package eu.vital_iot.iotda.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;

import eu.vital_iot.iotda.common.IoTSystem;

/**
 * This class represents registries for IoT systems.
 * 
 * @author k4t3r1n4
 * 
 * @see IoTSystem
 *
 */
@Singleton
public class Registry {

	/**
	 * The keeper.
	 */
	@EJB
	private Keeper keeper;

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * The store.
	 */
	@EJB
	private Store store;

	/**
	 * Registers the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to register.
	 */
	public void register(IoTSystem iotsystem) {

		logger.log(Level.FINE, "Register system [ system: " + iotsystem + " ].");

		// Store that system.
		store.create(iotsystem);

		// Keep its metadata.
		keeper.keepMetadata(iotsystem);

		logger.log(Level.FINE, "Registered system [ system: " + iotsystem + " ].");
	}

	/**
	 * Deregisters the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 */
	public void deregister(String id) {

		logger.log(Level.FINE, "Deregister system [ id: " + id + " ].");

		// Delete the system with that ID.
		store.delete(id);

		logger.log(Level.FINE, "Deregistered system [ id: " + id + " ].");
	}

	/**
	 * Gets all IoT systems.
	 * 
	 * @return a list that contains all IoT systems.
	 */
	public List<IoTSystem> get() {

		logger.log(Level.FINE, "Get systems.");

		// Read all systems.
		final List<IoTSystem> iotsystems = store.read(null);

		logger.log(Level.FINE, "Got systems.");

		return iotsystems;
	}

	/**
	 * Gets the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 * @return the IoT system with the given ID.
	 */
	public IoTSystem get(String id) {

		logger.log(Level.FINE, "Get system [ id: " + id + " ].");

		// Read the system with that ID.
		final IoTSystem iotsystem = store.read("{\"_id\": ObjectId(\"" + id + "\") }").get(0);

		logger.log(Level.FINE, "Got system [ id: " + id + " ].");

		return iotsystem;
	}

	/**
	 * Edits the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to edit.
	 */
	public void edit(IoTSystem iotsystem) {

		logger.log(Level.FINE, "Edit system [ system: " + iotsystem + " ].");

		// Update that system.
		final IoTSystem existing = get(iotsystem.getId());
		iotsystem.setLastDataRefresh(existing.getLastDataRefresh());
		iotsystem.setLastMetadataRefresh(existing.getLastMetadataRefresh());
		store.update(iotsystem);

		logger.log(Level.FINE, "Edited system [ system: " + iotsystem + " ].");
	}

	/**
	 * Refreshes the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 */
	public void refresh(String id) {

		logger.log(Level.FINE, "Refresh system [ id: " + id + " ].");

		final IoTSystem iotsystem = get(id);

		// Keep the metadata of the system with that ID.
		keeper.keepMetadata(iotsystem);

		logger.log(Level.FINE, "Refreshed system [ id: " + id + " ].");
	}
}