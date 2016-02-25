package eu.vital_iot.iotda.exception;

/**
 * Thrown when an attempt is made to register an already registered IoT system.
 * 
 * @author k4t3r1n4
 *
 */
@SuppressWarnings("serial")
public class AlreadyRegisteredException extends Exception {

	/**
	 * Constructs a new exception.
	 */
	public AlreadyRegisteredException() {
		super();
	}

	/**
	 * Constructs a new exception with the given message.
	 * 
	 * @param message
	 *            a message.
	 */
	public AlreadyRegisteredException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the given message and cause.
	 * 
	 * @param message
	 *            a message.
	 * @param cause
	 *            a cause.
	 */
	public AlreadyRegisteredException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the given cause.
	 * 
	 * @param cause
	 *            a cause.
	 */
	public AlreadyRegisteredException(Throwable cause) {
		super(cause);
	}
}