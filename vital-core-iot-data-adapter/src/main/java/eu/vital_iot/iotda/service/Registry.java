package eu.vital_iot.iotda.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

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

		// Store that IoT system.
		store.create(iotsystem);

		// Keep its metadata.
		keeper.keepMetadata(iotsystem);
	}

	/**
	 * Deregisters the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to deregister.
	 */
	public void deregister(IoTSystem iotsystem) {

		// Delete that IoT system.
		store.delete(iotsystem.getId());
	}

	/**
	 * Deregisters the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 */
	public void deregister(String id) {

		// Delete the IoT system with that ID.
		store.delete(id);
	}

	/**
	 * Gets all IoT systems.
	 * 
	 * @return a list that contains all IoT systems.
	 */
	public List<IoTSystem> get() {

		// Read all IoT systems.
		return store.read();
	}

	/**
	 * Gets the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 * @return the IoT system with the given ID.
	 */
	public IoTSystem get(String id) {

		// Read the IoT system with that ID.
		return store.read("{\"_id\": ObjectId(\"" + id + "\") }").get(0);
	}

	/**
	 * Edits the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to update.
	 */
	public void edit(IoTSystem iotsystem) {

		// Update that IoT system.
		final IoTSystem existing = get(iotsystem.getId());
		iotsystem.setLastDataRefresh(existing.getLastDataRefresh());
		iotsystem.setLastMetadataRefresh(existing.getLastMetadataRefresh());
		store.update(iotsystem);
	}

	/**
	 * Refreshes the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 */
	public void refresh(String id) {

		final IoTSystem iotsystem = get(id);

		// Keep its metadata.
		keeper.keepMetadata(iotsystem);
	}
}