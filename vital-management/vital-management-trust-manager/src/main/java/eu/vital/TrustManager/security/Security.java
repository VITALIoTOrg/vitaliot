/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.TrustManager.security;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import eu.vital.TrustManager.conf.ConfigReader;
import eu.vital.TrustManager.connectors.dms.trust.TrustAllX509TrustManager;

/**
 *
 * @author a601149
 */
public class Security {
    
    
    public  Boolean login(String user, String password, boolean testCookie, StringBuilder cookie) {
        
        ConfigReader configReader = ConfigReader.getInstance();
        
        String securityURL = configReader.get(ConfigReader.AUTH_URL);
        
        // Initializing connection variable (will be used throughout the code)
        HttpURLConnection connection = null;

        // Of course everything will go over HTTPS (here we trust anything, we do not check the certificate)
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch(NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        try {
            sc.init(null, new TrustManager[] { new TrustAllX509TrustManager() }, new java.security.SecureRandom());
        } catch(KeyManagementException e1) {
            e1.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        });
        
        
          
        URL url;
        InputStream is;
        BufferedReader rd;
        char cbuf[] = new char[10000];
        String resp = null;
        int len;
        String ck;

        // Create connection
        String urlParameters;
        if(testCookie) {
            urlParameters = "name=" + user + "&password=" + password + "&testCookie=true";
        }
        else {
            urlParameters = "name=" + user + "&password=" + password + "&testCookie=false";
        }
        // The parameters are user name, password and a flag saying if you want the test cookie or not...
        // ... the test cookie is not the SSO cookie
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            url = new URL(securityURL+"/authenticate");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
            wr.close();

            int err = connection.getResponseCode();
            if (err >= 200 && err<300){
            // Get Response  
                is = connection.getInputStream();
                rd = new BufferedReader(new InputStreamReader(is));
                len = rd.read(cbuf);
                resp = String.valueOf(cbuf).substring(0, len);
                rd.close();
                String headerName = null;
                // Look for the session cookie
                for(int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                     if(headerName.equalsIgnoreCase("Set-Cookie")) {                  
                         ck = connection.getHeaderField(i);
                         cookie.append(ck.substring(0, ck.indexOf(";")));
                     }
                }
                return true;
            }
            return false;
        } catch(Exception e) {
            //log
        } finally {
            if(connection != null) {
                connection.disconnect(); 
            }
           
        }
        return false;
    }
    
}
