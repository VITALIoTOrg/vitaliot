package eu.vital.discoverer.exception;

import java.io.Serializable;

public class ConnectionErrorException extends RuntimeException implements Serializable{
	private static final long serialVersionUID = 4835493509270817740L;

	public ConnectionErrorException(){
		super();
	}

	public ConnectionErrorException(String msg){
		super(msg);
	}

	public ConnectionErrorException(String msg, Throwable cause){
		super(msg, cause);
	}

}
