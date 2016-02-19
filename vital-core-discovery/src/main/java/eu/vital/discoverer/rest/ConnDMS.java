/*******************************************************************************
 * Copyright (c) 2013, 2016 Vital Consortium. All rights reserved. 
 * http://vital-iot.eu
 *
 * Contributors:
 *     riccardo.petrolo@inria.fr --- Inria Lille - Nord Europe
 *******************************************************************************/
package eu.vital.discoverer.rest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class ConnDMS {
	
	public boolean canConnect(String url, String port) {
		return true;
	}
		
	public boolean canConnect(InetAddress url, int port) {
		Socket socket=new Socket();
		
		SocketAddress socketAddress = new InetSocketAddress(url, port);
	    boolean connected=false;
	    try {
	        // 2 seconds and then give up
	        socket.connect(socketAddress, 2000);
	        connected=true;
	        socket.close();
	    }
	    catch (IOException e) {
	        // error during the connection 
	        e.printStackTrace();
	    }
	    return connected;

	}
	
}