/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.utils;

import java.io.IOException;
import java.net.InetAddress;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
  
public class Metrics {
  
    public static String getMetrics(final String[] args) throws Exception {
        ModelControllerClient client = ModelControllerClient.Factory.create(
                InetAddress.getByName("localhost"), 9993,
                new CallbackHandler() {
  
                    @Override
                    public void handle(Callback[] callbacks)
                            throws IOException, UnsupportedCallbackException {
                        for (Callback current : callbacks) {
                            if (current instanceof NameCallback) {
                                NameCallback ncb = (NameCallback) current;
                                ncb.setName(args[0]);
                            } else if (current instanceof PasswordCallback) {
                                PasswordCallback pcb = (PasswordCallback) current;
                                pcb.setPassword(args[1].toCharArray());
                            } else if (current instanceof RealmCallback) {
                                RealmCallback rcb = (RealmCallback) current;
                                rcb.setText(rcb.getDefaultText());
                            } else {
                                throw new UnsupportedCallbackException(current);
                            }
                        }
                    }
                });
  
        ModelNode operation = new ModelNode();
        operation.get("operation").set("read-attribute");
        operation.get("name").set("servlet-container");
  
        ModelNode address = operation.get("address");
        address.add("subsystem", "undertow");
        address.add("server", "default-server");
        
        ModelNode returnVal;
  
        try {
            returnVal = client.execute(operation);
    
        } finally {
            client.close();
        }
  
        return returnVal.toJSONString(false);
    }
}