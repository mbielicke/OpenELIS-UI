package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeRowAddedHandler<T> extends EventHandler {
	
	public void onBeforeRowAdded(BeforeRowAddedEvent<T> event);
}
