package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;

public interface CellEditedHandler extends EventHandler {

	public void onCellUpdated(CellEditedEvent event);
}
