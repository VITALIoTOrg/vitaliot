/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.security;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author a601149
 */
public class Security {
    
    
    public  Boolean login(String user, String password, boolean testCookie, StringBuilder cookie) {
        
        URL url;
        InputStream is;
        BufferedReader rd;
        char cbuf[] = new char[10000];
        String resp = null;
        int len;
        HttpURLConnection connection = null;
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
            url = new URL("https://vitalgateway.cloud.reply.eu/securitywrapper/rest/authenticate");
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
