package eu.vital.filtering.exception;

import java.io.Serializable;

public class WrongInputException extends RuntimeException implements Serializable{

	private static final long serialVersionUID = 3964220589966477519L;

	public WrongInputException() {
		super();
	}
	
	public WrongInputException(String msg) {
		super(msg);
	}
	
	public WrongInputException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	
	
}
