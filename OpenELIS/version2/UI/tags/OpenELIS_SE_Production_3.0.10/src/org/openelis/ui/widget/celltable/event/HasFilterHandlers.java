package org.openelis.ui.widget.celltable.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasFilterHandlers extends HasHandlers {
	
	public HandlerRegistration addFilterHandler(FilterHandler handler);
	

}
