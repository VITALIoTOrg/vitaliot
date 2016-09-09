package eu.vital.vitalcep.connectors.iotda;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import javax.ws.rs.core.Cookie;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import eu.vital.vitalcep.connectors.ppi.PPIManager;

public class IOTDAManager {

	public String iotda_URL,cookie ;

	public IOTDAManager(String iotda_url,String cookie){
        
        this.iotda_URL = iotda_url;
        this.cookie = cookie;

	}


	public JSONArray getRegisteredIoTsystems(String body) throws IOException,
	UnsupportedEncodingException, KeyManagementException, 
	NoSuchAlgorithmException, KeyStoreException{

		String sbody = body;
		String ppi_url =iotda_URL+"/rest/systems";
		String response = query(ppi_url,sbody,"GET");
		if (response.isEmpty())
			response = "[]";


		return new JSONArray(response);
	}


	private String query(String ppi_endpoint, String body, String method) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException{
		Cookie ck;
		//String internalToken;
		CloseableHttpClient httpclient;
		HttpRequestBase httpaction;
		//boolean wasEmpty;
		//int code;
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				builder.build());

		httpclient = HttpClients.custom().setSSLSocketFactory(
				sslsf).build();
		//httpclient = HttpClients.createDefault();

		URI uri = null;
		try {
			// Prepare to forward the request to the proxy
			uri = new URI(ppi_endpoint);
		} catch (URISyntaxException e1) {
			//log
		}

		if (method.equals("GET")) {
			httpaction = new HttpGet(uri);
		}
		else {
			httpaction = new HttpPost(uri);
		}

		// Get token or authenticate if null or invalid
		//internalToken = client.getToken();
		ck = new Cookie("vitalAccessToken", cookie.substring(17));

		httpaction.setHeader("Cookie", ck.toString());
		httpaction.setConfig(RequestConfig.custom().setConnectionRequestTimeout(3000)
				.setConnectTimeout(3000).setSocketTimeout(3000).build());
		httpaction.setHeader("Content-Type", javax.ws.rs.core.MediaType.APPLICATION_JSON);
		StringEntity strEntity = new StringEntity(body, StandardCharsets.UTF_8);
		if (method.equals("POST")) {
			((HttpPost) httpaction).setEntity(strEntity);
		}

		// Execute and get the response.
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpaction);
		} catch (ClientProtocolException e) {
			java.util.logging.Logger.getLogger(PPIManager
					.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			try {
				// Try again with a higher timeout
				try {
					Thread.sleep(1000); // do not retry immediately
				} catch (InterruptedException e1) {
					// e1.printStackTrace();
				}
				httpaction.setConfig(RequestConfig.custom()
						.setConnectionRequestTimeout(7000)
						.setConnectTimeout(7000).setSocketTimeout(7000).build());
				response = httpclient.execute(httpaction);
			} catch (ClientProtocolException ea) {
				java.util.logging.Logger.getLogger(PPIManager
						.class.getName())
				.log(Level.SEVERE, null, ea);
				return "";
			} catch (IOException ea) {
				try {
					// Try again with a higher timeout
					try {
						Thread.sleep(1000); // do not retry immediately
					} catch (InterruptedException e1) {
						java.util.logging.Logger.getLogger(PPIManager
								.class.getName())
						.log(Level.SEVERE, null, e1);
						return "";
					}
					httpaction.setConfig(RequestConfig.custom()
							.setConnectionRequestTimeout(12000)
							.setConnectTimeout(12000)
							.setSocketTimeout(12000).build());
					response = httpclient.execute(httpaction);
				} catch (ClientProtocolException eaa) {
					java.util.logging.Logger.getLogger(PPIManager
							.class.getName()).log(Level.SEVERE, null, eaa);
					return "";
				} catch (Exception eaa) {
					java.util.logging.Logger.getLogger(PPIManager
							.class.getName()).log(Level.SEVERE, null, eaa);
					return "";
					//return eaa.getMessage();
				}
			}
		}

		int statusCode;
		try{
			statusCode = response.getStatusLine().getStatusCode();
		}catch(Exception eaa){
			java.util.logging.Logger.getLogger(PPIManager
					.class.getName()).log(Level.SEVERE, null, eaa);
			return "";
		}

		if (statusCode != HttpStatus.SC_OK 
				&& statusCode != HttpStatus.SC_ACCEPTED){
			if (statusCode==503){
				java.util.logging.Logger.getLogger(PPIManager
						.class.getName()).log(Level.SEVERE, null, "PPI 503");
				return "";
			}else if (statusCode==502){
				java.util.logging.Logger.getLogger(PPIManager
						.class.getName()).log(Level.SEVERE, null, "PPI 502");
				return "";
			}else if (statusCode==401){
				java.util.logging.Logger.getLogger(PPIManager
						.class.getName()).log(Level.SEVERE, null, "PPI 401");
				return "";               
			}else{
				java.util.logging.Logger.getLogger(PPIManager
						.class.getName()).log(Level.SEVERE, null, "PPI 500");
				return "";
				//throw new ServiceUnavailableException();
			}
		}

		HttpEntity entity;
		entity = response.getEntity();
		String respString = "";

		if (entity != null) {
			try {
				respString = EntityUtils.toString(entity);
				response.close();
				return respString;
			} catch (ParseException | IOException e) {
				java.util.logging.Logger.getLogger(PPIManager
						.class.getName()).log(Level.SEVERE, null, "PPI 401");
				return "";
			} 
		}
		return respString;

	}
}
