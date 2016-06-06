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
package org.openelis.ui.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This class will fire events to registered objects for a DataChange action. If a target is passed
 * to the fire() method than only the handler for that widget will be called and all other handlers
 * will be ignored. 
 *
 */
public class DataChangeEvent<T> extends GwtEvent<DataChangeEvent.Handler>{
	private static Type<DataChangeEvent.Handler> TYPE;
	
	T data;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Type<DataChangeEvent.Handler> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public DataChangeEvent() {
		
	}
	
	public DataChangeEvent(T data) {
		this.data = data;
	}
	
    @Override
    protected void dispatch(Handler handler) {
        handler.onDataChange(this);
    }

	public static Type<DataChangeEvent.Handler> getType() {
	   if (TYPE == null) {
	      TYPE = new Type<DataChangeEvent.Handler>();
	    }
	    return TYPE;
	 }
	
	public static interface Handler<T> extends EventHandler {
	    public void onDataChange(DataChangeEvent<T> event);
	}
	
	public T getData() {
		return data;
	}

	
	
}
