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
