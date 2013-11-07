package org.openelis.ui.services;

import org.openelis.ui.common.Datetime;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("calendar")
public interface CalendarServiceInt extends RemoteService{
	
	public Datetime getCurrentDatetime(byte begin, byte end) throws Exception;

}
