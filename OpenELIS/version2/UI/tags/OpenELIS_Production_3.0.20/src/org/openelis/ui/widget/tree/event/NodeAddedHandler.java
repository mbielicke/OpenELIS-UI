package org.openelis.ui.widget.tree.event;

import com.google.gwt.event.shared.EventHandler;

public interface NodeAddedHandler extends EventHandler {
	
	public void onNodeAdded(NodeAddedEvent event);
}
