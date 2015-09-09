package org.openelis.ui.widget.tree.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeNodeAddedHandler extends EventHandler {
	
	public void onBeforeNodeAdded(BeforeNodeAddedEvent event);
}
