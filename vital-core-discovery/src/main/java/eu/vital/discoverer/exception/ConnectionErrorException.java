/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-02-26T09:52:37+01:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:25:06+02:00
*/



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
