package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasBeforeRowAddedHandlers<T> extends HasHandlers {
	
	public HandlerRegistration addBeforeRowAddedHandler(BeforeRowAddedHandler<T> handler);
	

}
