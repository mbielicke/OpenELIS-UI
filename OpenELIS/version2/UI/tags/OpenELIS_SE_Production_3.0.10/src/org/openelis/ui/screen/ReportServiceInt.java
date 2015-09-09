package org.openelis.ui.screen;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;

public interface ReportServiceInt extends RemoteService {
	
	public ArrayList<Prompt> getPrompts() throws Exception;
	
	public ReportStatus runReport(Query query) throws Exception;
	

}
