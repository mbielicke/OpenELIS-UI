package org.openelis.ui.widget.celltable.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeRowAddedHandler extends EventHandler {
	
	public void onBeforeRowAdded(BeforeRowAddedEvent event);
}
