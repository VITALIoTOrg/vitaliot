package eu.vital_iot.iotda.exception;

/**
 * Thrown when something related to a store goes wrong.
 * 
 * @author k4t3r1n4
 *
 * @see Store
 */
@SuppressWarnings("serial")
public class StoreException extends RuntimeException {

	/**
	 * Constructs a new store exception.
	 */
	public StoreException() {
		super();
	}

	/**
	 * Constructs a new store exception with the given message.
	 * 
	 * @param message
	 *            a message.
	 */
	public StoreException(String message) {
		super(message);
	}

	/**
	 * Constructs a new store exception with the given message and cause.
	 * 
	 * @param message
	 *            a message.
	 * @param cause
	 *            a cause.
	 */
	public StoreException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new store exception with the given cause.
	 * 
	 * @param cause
	 *            a cause.
	 */
	public StoreException(Throwable cause) {
		super(cause);
	}
}