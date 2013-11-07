package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasRowDeletedHandlers<T> extends HasHandlers {
	
	public HandlerRegistration addRowDeletedHandler(RowDeletedHandler<T> handler);
	

}
