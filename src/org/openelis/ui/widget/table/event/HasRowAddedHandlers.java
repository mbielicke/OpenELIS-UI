package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasRowAddedHandlers<T> extends HasHandlers {
	
	public HandlerRegistration addRowAddedHandler(RowAddedHandler<T> handler);
	

}
