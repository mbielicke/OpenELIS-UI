package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;

public interface RowDeletedHandler extends EventHandler {
	
	public void onRowDeleted(RowDeletedEvent event);
}
