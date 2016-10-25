/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.TrustManager.connectors.dms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.HttpMessage;
////import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//
//import org.json.simple.JSONArray;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;
//import java.util.Iterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.core.Cookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.ws.rs.ServerErrorException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;


import  javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.NotAuthorizedException;
import org.apache.http.conn.ConnectTimeoutException;



/**
 *
 * @author a601149
 */
public class DMSManager {
    
    public String dms_URL,cookie ;
    
    public DMSManager(String dms_url,String cookie){
        
        this.dms_URL = dms_url;
        this.cookie = cookie;
        
    }
    
    public JSONObject getLastSystemInstance(String systemId){
        
        JSONObject system = new JSONObject();
        return system;
        
    }
    
    public boolean pushObservations(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        String response = query("insertObservation",sbody,"POST");
        return response != null;
    }
    
    public boolean pushSensors(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query("insertSensor",sbody,"POST");
        
        return response != null;
    }
    
    public boolean pushServices(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query("insertService",sbody,"POST");
        
        return response != null;
    }
    
    public boolean pushSystem(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query("insertSystem",sbody,"POST");
        
        return response != null;
    }
        
    public JSONArray getObservations(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        //String response = queryDMSTest("queryObservation",sbody);
        
        String response = query("queryObservation",sbody,"POST");
        
        JSONArray resp;
        
        if (!(response=="")){        
            resp = new JSONArray(response);
        }else{
            resp = new JSONArray();
        }
        return resp;
}
    
public JSONArray getObservationsWithExceptions(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException, SocketTimeoutException
        , ConnectException, InterruptedException{
        
        String sbody = body;
        
        //String response = queryDMSTest("queryObservation",sbody);
        
        String response = queryWithExceptions("queryObservation",sbody,"POST");
        
        JSONArray resp;
        
        if (!(response=="")){        
            resp = new JSONArray(response);
        }else{
            resp = new JSONArray();
        }
        return resp;
}
  
    public JSONArray getSystems(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query("querySystem",sbody,"POST");
        

        return new JSONArray(response);
    }
    
    public JSONArray getServices(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query("queryService",sbody,"POST");
        
        return new JSONArray(response);
    }
    
    public JSONArray getSensors(String body) throws IOException,
            UnsupportedEncodingException, KeyManagementException, 
            NoSuchAlgorithmException, KeyStoreException{
        
        String sbody = body;
        
        String response = query("querySensor",sbody,"POST");
        

        return new JSONArray(response);
    }
    
    private String query(String dms_endpoint, String body, String method) 
            throws SocketTimeoutException, ConnectException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException{
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

        URI uri = null;
        try {
            // Prepare to forward the request to the proxy
            uri = new URI(dms_URL+"/"+dms_endpoint);
        } catch (URISyntaxException e1) {
            java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, e1);
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
        httpaction.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
        .setConnectTimeout(5000).setSocketTimeout(5000).build());
        httpaction.setHeader("Content-Type", 
                javax.ws.rs.core.MediaType.APPLICATION_JSON);
        StringEntity strEntity = new StringEntity(body, StandardCharsets.UTF_8);
        if (method.equals("POST")) {
                ((HttpPost) httpaction).setEntity(strEntity);
        }

        // Execute and get the response.
        CloseableHttpResponse response = null;
        try {
                response = httpclient.execute(httpaction);
        } catch (ClientProtocolException e) {
                java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, e);
        } catch (IOException e) {
                try {
                        // Try again with a higher timeout
                        try {
                                Thread.sleep(1000); // do not retry immediately
                        } catch (InterruptedException e1) {
                               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, e1);
                                return "";
                        }
                        httpaction.setConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(7000)
                        .setConnectTimeout(7000).setSocketTimeout(7000).build());
                        response = httpclient.execute(httpaction);
                } catch (ClientProtocolException ea) {
                        java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, ea);
                        return "";
                } catch (IOException ea) {
                    try {
                        // Try again with a higher timeout
                        try {
                                Thread.sleep(1000); // do not retry immediately
                        } catch (InterruptedException e1) {
                                java.util.logging.Logger.getLogger(DMSManager
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
                        java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, eaa);
                        return "";
                    } catch (SocketTimeoutException eaa) {
                        java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, eaa);
                        return "";
                    }catch (ConnectException eaa) {
                        java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, eaa);
                        return "";
                    }catch (ConnectTimeoutException eaa) {
                        java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, eaa);
                        return "";
                    }
                }
        }
        
        int statusCode = response.getStatusLine().getStatusCode();
         String respString = "";
        
        HttpEntity entity = null;
         
        if (statusCode == HttpStatus.SC_OK 
                || statusCode == HttpStatus.SC_ACCEPTED){
            
             entity = response.getEntity();

        }else{
            if (statusCode==503){
               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "httpStatusCode 503");
                return "";
           }else if (statusCode==502){
               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "httpStatusCode 502");
                return "";
           }else if (statusCode==401){
               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "httpStatusCode 401");      
                return "";
           }else{
               java.util.logging.Logger.getLogger(DMSManager
                    .class.getName()).log(Level.SEVERE, null, "PPI 500");
               return "";
                //throw new ServiceUnavailableException();
           }
            
        }

        if (entity != null) {
                try {
                        respString = EntityUtils.toString(entity);
                        response.close();
                } catch (ParseException | IOException e) {
                        java.util.logging.Logger.getLogger(DMSManager
                    .class.getName()).log(Level.SEVERE, null, "PPI 401");
                    return "";
                } 
        }
        return respString;
    
    }
    
    private String queryWithExceptions(String dms_endpoint, String body, String method) 
            throws SocketTimeoutException, ConnectException, IOException, InterruptedException{
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
            uri = new URI(dms_URL+"/"+dms_endpoint);
        } catch (URISyntaxException e1) {
            java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, e1);
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
        httpaction.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
        .setConnectTimeout(5000).setSocketTimeout(5000).build());
        httpaction.setHeader("Content-Type", 
                javax.ws.rs.core.MediaType.APPLICATION_JSON);
        
        StringEntity strEntity = new StringEntity(body, StandardCharsets.UTF_8);
        
        if (method.equals("POST")) {
                ((HttpPost) httpaction).setEntity(strEntity);
        }

        // Execute and get the response.
        CloseableHttpResponse response = null;
        try {
                response = httpclient.execute(httpaction);
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(); 
        } catch (IOException e) {
                try {
                        // Try again with a higher timeout
                        try {
                                Thread.sleep(1000); // do not retry immediately
                        } catch (InterruptedException e1) {
                            throw new InterruptedException();
                               // e1.printStackTrace();
                        }
                        httpaction.setConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(7000)
                        .setConnectTimeout(7000).setSocketTimeout(7000).build());
                        response = httpclient.execute(httpaction);
                } catch (ClientProtocolException ea) {
                    java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, ea);
                    throw new ClientProtocolException();
                } catch (IOException ea) {
                    try {
                        // Try again with a higher timeout
                        try {
                                Thread.sleep(1000); // do not retry immediately
                        } catch (InterruptedException e1) {
                            java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, e1);
                            throw new InterruptedException(); 
                        }
                    httpaction.setConfig(RequestConfig.custom()
                            .setConnectionRequestTimeout(12000)
                            .setConnectTimeout(12000)
                            .setSocketTimeout(12000).build());
                            response = httpclient.execute(httpaction);
                    } catch (ClientProtocolException eaa) {
                         java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, eaa);
                        throw new ClientProtocolException();
                    } catch (SocketTimeoutException eaa) {
                        java.util.logging.Logger.getLogger(DMSManager
                            .class.getName())
                            .log(Level.SEVERE, null, eaa);
                        throw new SocketTimeoutException();
                    }catch (ConnectException eaa) {
                            java.util.logging.Logger.getLogger(DMSManager
                            .class.getName())
                            .log(Level.SEVERE, null, eaa);
                        throw new ConnectException();
                    }catch (ConnectTimeoutException eaa) {
                        java.util.logging.Logger.getLogger(DMSManager
                            .class.getName())
                            .log(Level.SEVERE, null, eaa);
                        throw new ConnectTimeoutException();
                    }
                }
        }
        
        int statusCode = response.getStatusLine().getStatusCode();
        
        if (statusCode != HttpStatus.SC_OK 
                && statusCode != HttpStatus.SC_ACCEPTED){
           if (statusCode==503){
                java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "httpStatusCode 503");
               throw new ServiceUnavailableException();
           }else if (statusCode==502){
               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "httpStatusCode 502");
                throw new ServerErrorException(502);
           }else if (statusCode==401){
               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "could't Athorize the DMS");
               throw new NotAuthorizedException("could't Athorize the DMS");           
           }else{
               java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                "httpStatusCode 500");
               throw new ServiceUnavailableException();
           }
        }

        HttpEntity entity;
        entity = response.getEntity();
        String respString = "";

        if (entity != null) {
                try {
                        respString = EntityUtils.toString(entity);
                        response.close();
                } catch (ParseException | IOException e) {
                       java.util.logging.Logger.getLogger(DMSManager
                                    .class.getName())
                                        .log(Level.SEVERE, null, 
                                                e);
                } 
        }
        return respString;
    
    }
   
    private String queryDMSTest(String dms_endpoint, String body) throws UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{   
        
   
        HttpURLConnection connection = null;
        // Of course everything will go over HTTPS (here we trust anything, we do not check the certificate)
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch(NoSuchAlgorithmException e1) {
            
        }
        
        InputStream is;
        BufferedReader rd;
        char cbuf[] = new char[1000000];
        int len;

        String urlParameters = body; // test cookie is the user performing the evalaution
        // The array of resources to evaluate policies on must be included
        
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        URL url= new URL (dms_URL+"/"+dms_endpoint);
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            connection.setRequestProperty("Content-Type", 
                    "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", 
                    Integer.toString(postDataLength));
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Cookie", cookie ); // Include cookies (permissions evaluated for normal user, advanced user has the rights to evaluate)
            
            
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
            wr.close();
            
            // Get Response
            int err = connection.getResponseCode();
            if (err >= 200 && err<300){
                
                is = connection.getInputStream();
//             
                rd = new BufferedReader(new InputStreamReader(is));
//                //StringBuilder rd2 = new StringBuilder();
               
                len = rd.read(cbuf);
                String resp2 = String.valueOf(cbuf).substring(0, len-1);
                rd.close();
                return resp2;
                
                
//            char[] buffer = new char[1024*1024];
//            StringBuilder output = new StringBuilder();
//            int readLength = 0;
//            while (readLength != -1) {
//                readLength = rd.read(buffer, 0, buffer.length);
//                if (readLength != -1) {
//                    output.append(buffer, 0, readLength);
//                }
//            }
                
//                return output.toString();
            }

            
        } catch(Exception e) {
            throw new java.net.ConnectException();
            //log 
        } finally {
            if(connection != null) {
                connection.disconnect(); 
                
            }
        }
        return null;  
}

    private String queryDMS(String dms_endpoint, String body) throws UnsupportedEncodingException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{   
        
//        SSLContextBuilder builder = new SSLContextBuilder();
//        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        CloseableHttpClient httpclient = HttpClients.custom()
//                //.setSSLSocketFactory(sslsf)
//                .setHostnameVerifier(new AllowAllHostnameVerifier())
//                .setRedirectStrategy(new LaxRedirectStrategy()).build();
//        
         SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
            builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
            sslsf).setRedirectStrategy(new LaxRedirectStrategy()).build();
       
        String url =  this.dms_URL+"/"+dms_endpoint;
         
        HttpPost post = new HttpPost(url);

        post.addHeader("Content-Type", "application/json");
        
        post.addHeader("Content-Type",cookie.substring(17));
        RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(5000).setConnectTimeout(5000)
                .setSocketTimeout(5000).build();
        post.setConfig(requestConfig);
//            

        HttpEntity entity = new StringEntity(body,StandardCharsets.UTF_8);
        post.setEntity(entity);
        CloseableHttpResponse clientresponse = httpclient.execute(post);


        if (clientresponse.getStatusLine()
                .getStatusCode() != HttpStatus.SC_OK && clientresponse.getStatusLine()
                .getStatusCode() != HttpStatus.SC_ACCEPTED){
            return null;
        }
        String sdata;
    sdata = EntityUtils.toString(clientresponse
            .getEntity(), StandardCharsets.UTF_8);

    return sdata;
//       
    }
    
    private String query2(String DMS_endpoint, String postObject) throws NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException{
    
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) 
                    throws CertificateException {
                return true;
            }
        });

        SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
       
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setSSLSocketFactory(sslSF).build();
        
        HttpPost postRequest = new HttpPost(dms_URL+"/"+DMS_endpoint);
        postRequest.addHeader("Content-Type", "application/json");
        postRequest.addHeader("vitalAccessToken", cookie.substring(17));
        
        HttpEntity entityPost = new StringEntity(postObject,StandardCharsets.UTF_8);
        postRequest.setEntity(entityPost);
        
        CloseableHttpResponse response =  httpClient.execute(postRequest);
        
        try {
            //(CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(postRequest)) 
            //System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            if (entity == null)
                return null;
            return EntityUtils.toString(entity);
            //EntityUtils.consume(entity);
        } catch(IOException | ParseException e){
                    //logger.error(e.toString());
                    //throw new ConnectionErrorException("Error in connection with DMSManager");
        }
        return null;
    }
    
    private String queryDMS(String DMS_endpoint, String postObject, String cookie){
            HttpURLConnection connectionDMS = null;
            try{

                    String postObjectString=postObject;
                    byte[] postObjectByte=postObjectString.getBytes(StandardCharsets.UTF_8);
                    int postDataLength = postObjectByte.length;
                    URL DMS_Url=new URL(dms_URL+"/"+DMS_endpoint);

                    // prepare header
                    connectionDMS = (HttpURLConnection) DMS_Url.openConnection();
                    connectionDMS.setDoOutput(true);
                    connectionDMS.setDoInput(true);
                    connectionDMS.setConnectTimeout(5000);
                    connectionDMS.setReadTimeout(5000);
                    connectionDMS.setRequestProperty("Content-Type", "application/json");
                    connectionDMS.setRequestProperty("Accept", "application/json");
                    connectionDMS.setRequestProperty("charset", "utf-8");
                    connectionDMS.setRequestProperty("vitalAccessToken", cookie);
                    connectionDMS.setRequestMethod("POST");
                    connectionDMS.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                    DataOutputStream wr=new DataOutputStream(connectionDMS.getOutputStream());
                    wr.write(postObjectByte);
                    wr.flush();
                    int HttpResult = connectionDMS.getResponseCode(); 
                    if(HttpResult == HttpURLConnection.HTTP_OK){
                            Object obj=new InputStreamReader(connectionDMS.getInputStream(),"utf-8");




                            return obj.toString();

                    }else{
                            return null;
                    }  
            }
            catch(Exception e){
                    //logger.error(e.toString());
                    //throw new ConnectionErrorException("Error in connection with DMSManager");
            }finally{
                    if(connectionDMS != null) {
                            connectionDMS.disconnect(); 
                    }
            }
            return "";
    }
    
}
