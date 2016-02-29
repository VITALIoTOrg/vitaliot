/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 *
 * @author a601149
 */
public class RestJSONClient {
    
    /** The id. */
	private String url;
	
	/** The server name. */
	private String port;
	
	/** The CE pip address. */
	private String resource;
	
	/** The CE pport. */
	private String body;
	
	/** The UDP listen port. */
	private String method;
	
        /**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url            the url to set
	 */
	public void setURL(String url) {
		this.url = url;
	}
        
        /**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
        
        /**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the resource.
	 *
	 * @param resource            the resource to set
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
        
        /**
	 * Gets the body.
	 *
	 * @return body the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 *
	 * @param body            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
        
        /**
	 * Gets the method.
	 *
	 * @return method  the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the method.
	 *
	 * @param method            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
    
    public String send() {
        
        HttpClient client = HttpClientBuilder.create().build();
    
        try {
            
            if ("GET".equals((this.method).toUpperCase())){
                // Create new getRequest with below mentioned URL
                HttpGet getRequest = new HttpGet("http://"+this.url+":"
                    +this.port+"/"+this.resource);
                // Add additional header to getRequest which accepts application/xml data
                getRequest.addHeader("accept", "application/json");

                // Execute your request and catch response
                HttpResponse response = client.execute(getRequest);
                
                // Check for HTTP response code: 200 = success
                if (response.getStatusLine().getStatusCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : " 
                                + response.getStatusLine().getStatusCode());
                }

                // Get-Capture Complete application/json body response
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (response.getEntity().getContent())));
                String output;

                while ((output = br.readLine()) != null) {
                }
                br.close();
                              
                return output;
            }
            
             if ("POST".equals((this.method).toUpperCase())){
                // Create new getRequest with below mentioned URL
                HttpPost postRequest = new HttpPost("http://"+this.url+":"
                    +this.port+"/"+this.resource);
                // Add additional header to getRequest which accepts application/xml data
                postRequest.addHeader("accept", "application/json");
                
                StringEntity bodyEntity = new StringEntity(this.body);
                postRequest.setEntity(bodyEntity);
                
                // Execute your request and catch response
                HttpResponse response = client.execute(postRequest);
                
                // Check for HTTP response code: 200 = success
                if (response.getStatusLine().getStatusCode() != 200 ||
                       response.getStatusLine().getStatusCode() != 201) {
                        throw new RuntimeException("Failed : HTTP error code : " 
                                + response.getStatusLine().getStatusCode());
                }

                // Get-Capture Complete application/json body response
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (response.getEntity().getContent())));
                String output;

                while ((output = br.readLine()) != null) {
                }
                br.close();
                
                return output;
            }
    
        } catch (ClientProtocolException e) {
                e.printStackTrace();

        } catch (IOException e) {
                e.printStackTrace();
        }
        return "";
    }    
}
