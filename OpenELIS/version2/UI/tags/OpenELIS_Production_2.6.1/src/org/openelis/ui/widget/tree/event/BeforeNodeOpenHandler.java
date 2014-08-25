package org.openelis.ui.widget.tree.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeNodeOpenHandler extends EventHandler {

	public void onBeforeNodeOpen(BeforeNodeOpenEvent event);
}
