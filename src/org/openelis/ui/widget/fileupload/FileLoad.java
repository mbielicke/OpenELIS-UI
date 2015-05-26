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
package org.openelis.ui.widget.fileupload;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class will create a HTML form that will allow upload of a single file.  A widget is used
 * as the diplay to open the browser file dialog.  
 *
 */
public class FileLoad extends Composite implements HasWidgets {
	
	protected FileLoadButton upload;
	protected FormPanel form;
	protected AbsolutePanel panel;
    //protected Hidden service,method;
	
	public FileLoad() {
		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		
		upload = new FileLoadButton();
		
		// Submit form to server once user has chosen a file
		upload.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				form.submit();
			}
		});
		upload.setName("file");
		
		// Hidden fields in form to define service and method to call on upload
		//service = new Hidden("service");
		//method = new Hidden("method");
		
		panel = new AbsolutePanel();
		panel.add(upload);
		//panel.add(service);
		//panel.add(method);
		form.add(panel);
		
		initWidget(form);
	}
	
	/**
	 * Adds a handler that will be called back om form submission 
	 */
	public HandlerRegistration addSubmitHandler(SubmitHandler handler) {
		return form.addSubmitHandler(handler);
	}
	
	/**
	 * Adds a handler that will be called back when file upload is completed
	 */
	public HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler) {
		return form.addSubmitCompleteHandler(handler);
	}
	
	/**
	 * Sets the action of the form that represents the path of the servlet that should 
	 * be called on submission
	 */
	public void setAction(String action) {
		form.setAction(action);
	}
	
	/**
	 * Sets a class name that will be called using reflection from the FileUploadServlet when
	 * the file is received on the server.   
	 */
	public void setService(String service) {
		//this.service.setValue(service);
	}
	
	/**
	 * Method called in the reflected service class to process the uploaded file.
	 */
	public void setMethod(String method) {
		//this.method.setValue(method);
	}
	
	/**
	 * Sets the widget to be used for display and to open the file dialog in the browser.
	 */
	public void setWidget(Widget widget) {
		upload.setButton(widget);
	}

	/**
	 * Sets the text of the default upload button
	 * @param text
	 */
	public void setText(String text) {
	    upload.setText(text);
	}

    @Override
    public void add(Widget w) {
       setWidget(w);
        
    }

    @Override
    public void clear() {
        
    }

    @Override
    public Iterator<Widget> iterator() {
        return null;
    }

    @Override
    public boolean remove(Widget w) {
        return false;
    }
}
