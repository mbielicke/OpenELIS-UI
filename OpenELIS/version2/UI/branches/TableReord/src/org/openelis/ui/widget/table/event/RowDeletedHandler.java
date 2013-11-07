package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;

public interface RowDeletedHandler<T> extends EventHandler {
	
	public void onRowDeleted(RowDeletedEvent<T> event);
}
