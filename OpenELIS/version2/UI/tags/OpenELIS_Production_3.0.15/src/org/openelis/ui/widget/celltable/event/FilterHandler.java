package org.openelis.ui.widget.celltable.event;

import com.google.gwt.event.shared.EventHandler;

public interface FilterHandler extends EventHandler {
	
	public void onFilter(FilterEvent event);
}
