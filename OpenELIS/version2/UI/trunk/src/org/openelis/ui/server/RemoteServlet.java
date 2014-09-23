package org.openelis.ui.server;

import javax.servlet.http.HttpServletRequest;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ValidationErrorsList;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;

public class RemoteServlet extends XsrfProtectedServiceServlet {

    private static final long            serialVersionUID = 1L;
    protected SerializationPolicy sPolicy;

    /**
     * Overridden to manage session timeout
     */
    protected void onBeforeRequestDeserialized(String serializedRequest) {
        super.onBeforeRequestDeserialized(serializedRequest);

        getThreadLocalRequest().getSession().setAttribute("last_access",
                                             Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
    }

    /**
     * Overridden to expires the data request
     */
    protected void onAfterResponseSerialized(String serializedResponse) {
        super.onAfterResponseSerialized(serializedResponse);
        getThreadLocalResponse().setHeader("pragma", "no-cache");
        getThreadLocalResponse().setHeader("Cache-Control", "no-cache");
        getThreadLocalResponse().setHeader("Cache-Control", "no-store");
        getThreadLocalResponse().setDateHeader("Expires", 0);
    }

    /**
     * Throws or wraps the EJB exceptions so it can be forwarded to GWT
     */
    protected Exception serializeForGWT(Throwable t) {
        Throwable nt;
        
        if (t instanceof ValidationErrorsList)
            return (ValidationErrorsList)t;

        nt = t;
      
        do {
            try {
                sPolicy.validateSerialize(nt.getClass());
                return (Exception)nt;
            } catch (SerializationException se) {
                nt = nt.getCause();
            }
        } while (nt != null);
        
        return new Exception(t.toString());
    }        

    /*
     * @see
     * com.google.gwt.user.server.rpc.RemoteServiceServlet#doGetSerializationPolicy
     * (javax.servlet.http.HttpServletRequest, java.lang.String,
     * java.lang.String)
     * 
     * This method was overridden so that serialization policy could be cached for our exception
     * processing.
     */
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request,
                                                           String moduleBaseURL, String strongName) {
        sPolicy = super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
        return sPolicy;
    }
}
