package org.openelis.ui.widget.tree.event;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeNodeCloseHandler extends EventHandler {

	public void onBeforeNodeClose(BeforeNodeCloseEvent event);
}
