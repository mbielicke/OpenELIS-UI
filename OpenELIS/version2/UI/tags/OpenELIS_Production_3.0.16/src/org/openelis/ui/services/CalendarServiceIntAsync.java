package org.openelis.ui.services;

import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CalendarServiceIntAsync {
	
	public void getCurrentDatetime(byte begin, byte end, AsyncCallback<Datetime> callback);

}
