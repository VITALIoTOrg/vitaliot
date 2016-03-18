/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.connectors.ppi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.ws.rs.core.Cookie;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class PPIManager {
    
     public String ppi_URL,cookie ;
    
    public PPIManager(String ppi_url, String cookie){
        
        this.ppi_URL = ppi_url;
        this.cookie = cookie;
        
        
    }
    
    public JSONArray getObservations(String ppi_endpoint,
            String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query(ppi_endpoint,sbody,"POST");
        JSONArray resp = new JSONArray(response);

        return resp;
}
  
    public JSONArray getSystems(String ppi_endpoint,String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query(ppi_endpoint+"/metadata",sbody,"POST");
        

        return new JSONArray(response);
    }
    
    public JSONArray getServices(String ppi_endpoint,String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query(ppi_endpoint+"/service/metadata",sbody,"POST");
        
        return new JSONArray(response);
    }
    
    public String getObservationServices(String ppi_endpoint) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody ="[\"vital:ObservationService\"]" ;
        
        String response = query(ppi_endpoint+"/service/metadata",sbody,"POST");
        
        return response;
    }
    
    public JSONArray getSensors(String ppi_endpoint,String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query(ppi_endpoint+"/sensor/metadata",sbody,"POST");
        

        return new JSONArray(response);
    }
    
    private String query(String ppi_endpoint, String body, String method){
        Cookie ck;
        //String internalToken;
        CloseableHttpClient httpclient;
        HttpRequestBase httpaction;
        //boolean wasEmpty;
        //int code;

        httpclient = HttpClients.createDefault();

        URI uri = null;
        try {
                // Prepare to forward the request to the proxy
                uri = new URI(ppi_URL+"/"+ppi_endpoint);
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
                //e.printStackTrace();
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
                        ea.printStackTrace();
                } catch (IOException ea) {
                        try {
                                // Try again with a higher timeout
                                try {
                                        Thread.sleep(1000); // do not retry immediately
                                } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                }
                        httpaction.setConfig(RequestConfig.custom()
                                .setConnectionRequestTimeout(12000)
                                .setConnectTimeout(12000)
                                .setSocketTimeout(12000).build());
                                response = httpclient.execute(httpaction);
                        } catch (ClientProtocolException eaa) {
                                ea.printStackTrace();
                        } catch (IOException eaa) {
                                ea.printStackTrace();
                                return eaa.getMessage();
                        }
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
                        //e.printStackTrace();
                } 
        }
        return respString;
    
    }
    private JSONArray getPPIObservations(String ppi_url
            ,String body) throws ParseException {
        JSONArray data = new JSONArray();
        HttpClientBuilder builder = HttpClientBuilder.create();
        try {
            
            final CloseableHttpClient client = builder.build();

            HttpPost post = new HttpPost(ppi_url);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");

            HttpEntity entity = new StringEntity(body);
            post.setEntity(entity);
            
            HttpResponse clientresponse = client.execute(post);
            if (clientresponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return  null;
            final String sdata = EntityUtils.toString(clientresponse.getEntity(), StandardCharsets.UTF_8);
            data = new JSONArray( sdata);
        } catch (IOException ioe) {
            //logger.error(ioe);
        }
        
        return data;
    }
    
}
