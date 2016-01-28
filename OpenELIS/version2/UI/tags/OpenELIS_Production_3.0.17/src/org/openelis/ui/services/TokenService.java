package org.openelis.ui.services;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;

/**
 * This Singleton is used to retrieve the Session ID hash token form the
 * server.  The token only needs to be retrieved once and set to each service
 * during the current user sesssion of an app.
 * @author tschmidt
 *
 */
public class TokenService {

    private static XsrfToken token;
    
    public static XsrfToken getToken() {
        if(token == null) {
            XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync)GWT.create(XsrfTokenService.class);
            
            ((ServiceDefTarget)xsrf).setServiceEntryPoint(com.google.gwt.core.client.GWT.getModuleBaseURL()+"xsrf");
            
            xsrf.getNewXsrfToken(new SyncCallback<XsrfToken>() {
                public void onSuccess(XsrfToken result) {
                    token = result;
                };
            
                @Override
                /** 
                 * This error should already be logged in the server no need to log again.
                 */
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                }
            });
        }
        
        return token;
    }
    
    private TokenService(){}
}
