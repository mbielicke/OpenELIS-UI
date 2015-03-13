package org.openelis.ui.widget.cell;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCellClickedHandlers extends HasHandlers {
	
	public HandlerRegistration addCellClickedHandler(CellClickedHandler handler);

}
