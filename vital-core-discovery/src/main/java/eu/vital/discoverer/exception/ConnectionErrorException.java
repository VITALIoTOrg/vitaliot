/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
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
