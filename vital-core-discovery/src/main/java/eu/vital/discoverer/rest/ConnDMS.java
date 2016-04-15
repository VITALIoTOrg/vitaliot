/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/

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
