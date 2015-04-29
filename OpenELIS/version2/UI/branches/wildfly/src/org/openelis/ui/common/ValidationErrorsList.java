/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.common;

import java.util.ArrayList;
import java.util.List;

import org.openelis.ui.common.Warning;

public class ValidationErrorsList extends Exception {
    
    private static final long serialVersionUID = 1L;
    private ArrayList<Exception> errors;
    private boolean hasErrors, hasWarnings, hasCautions;
    
    public ValidationErrorsList() {
        super();
        errors = new ArrayList<Exception>();
        hasErrors = false;
        hasWarnings = false;
        hasCautions = false;
    }

    public ValidationErrorsList(String msg) {
        super(msg);
        hasErrors = false;
        hasWarnings = false;
        hasCautions = false;
    }
    
    public ValidationErrorsList(List<Exception> excs) {
    	this();
    	for (Exception exc : excs) {
    		add(exc);
    	}
    }
    
    public void add(Exception ex){
        errors.add(ex);

        if (ex instanceof Warning)
            hasWarnings = true;
        else if (ex instanceof Caution)
            hasCautions = true;
        else
            hasErrors = true;
    }
    
    public int size(){
        return errors.size();
    }
    
    public ArrayList<Exception> getErrorList(){
        return errors;
    }
    
    public boolean hasErrors(){
        return hasErrors;
    }
    
    public boolean hasWarnings(){
        return hasWarnings;
    }
    
    public boolean hasCautions(){
        return hasCautions;
    }
}