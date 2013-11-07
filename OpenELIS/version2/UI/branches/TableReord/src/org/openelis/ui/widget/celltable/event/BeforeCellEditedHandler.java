package org.openelis.ui.widget.celltable.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeCellEditedHandler extends EventHandler {

	public void onBeforeCellEdited(BeforeCellEditedEvent event);
}
