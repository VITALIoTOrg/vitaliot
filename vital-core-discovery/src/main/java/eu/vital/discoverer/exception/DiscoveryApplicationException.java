/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-02-26T09:52:37+01:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:12+02:00
*/



package eu.vital.discoverer.exception;

import java.io.Serializable;

public class DiscoveryApplicationException extends RuntimeException implements Serializable{

	private static final long serialVersionUID = 1L;
    public DiscoveryApplicationException() {
        super();
    }
    public DiscoveryApplicationException(String msg)   {
        super(msg);
    }
    public DiscoveryApplicationException(String msg, Exception e)  {
        super(msg, e);
    }

}
