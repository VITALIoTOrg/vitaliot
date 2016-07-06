package eu.vital.filtering.exception;

import java.io.Serializable;

public class ConnectionErrorException extends RuntimeException implements Serializable{
	private static final long serialVersionUID = 2440010426017536307L;


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
