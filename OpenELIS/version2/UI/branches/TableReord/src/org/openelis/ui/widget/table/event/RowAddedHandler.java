package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;

public interface RowAddedHandler<T> extends EventHandler {
	
	public void onRowAdded(RowAddedEvent<T> event);
}
