package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasBeforeRowDeletedHandlers<T> extends HasHandlers {
	
	public HandlerRegistration addBeforeRowDeletedHandler(BeforeRowDeletedHandler<T> handler);
	

}
