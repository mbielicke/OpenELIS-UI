package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeRowDeletedHandler<T> extends EventHandler {
	
	public void onBeforeRowDeleted(BeforeRowDeletedEvent<T> event);
}
