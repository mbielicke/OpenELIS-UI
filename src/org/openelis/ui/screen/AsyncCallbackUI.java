/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.screen;

import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class expands on the GWT AsyncCallback to add some specific screen functionality
 * for OpenELIS Screens and a finish method that is called for both success and failuire 
 * paths
 */
public abstract class AsyncCallbackUI<T> implements AsyncCallback<T> {

    /**
     * Implementation of onSuccess that will call finish() after calling the 
     * user defined success method
     */
    @Override
    public void onSuccess(T result) {
        success(result);
        finish();
    }
   
    /**
     * Implementation of onFailure that will inspect the exception and call one
     * of the defined handle methods if the appropriate type.  Otherwise the default
     * failure method is called.
     */
    @Override
    public void onFailure(Throwable caught) {
        if(caught instanceof ValidationErrorsList)
            validationErrors((ValidationErrorsList)caught);
        else if(caught instanceof LastPageException)
            lastPage();
        else if(caught instanceof NotFoundException)
            notFound();
        else
            failure(caught);
       
        finish();
    }

    /**
     * Abstract method that must be supplied by the extending class for the success
     * return of the call
     */
    public abstract void success(T result);
    
    /**
     * Stub method that can be overridden by extending class to handle failed calls
     */
    public void failure(Throwable caught) {}
    
    /**
     * Stub method that can be overridden by extending class to handle validation errors
     */
    public void validationErrors(ValidationErrorsList e) {}
    
    /**
     * Stub method that can be overridden by extending class to habdle LastPageException
     */
    public void lastPage() {}
    
    /**
     * Stub method that can be overridden by extending class to handle NotFoundException
     */
    public void notFound() {}
    
    /**
     * Stub method that can be overridden by extending class to add code that will be 
     * executed regardless of success or failure of the call
     */
    public void finish() {}

}
