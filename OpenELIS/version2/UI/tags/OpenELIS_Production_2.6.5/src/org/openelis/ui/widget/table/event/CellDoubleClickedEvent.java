package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CellDoubleClickedEvent extends GwtEvent<CellDoubleClickedEvent.Handler> {
	
	private static Type<CellDoubleClickedEvent.Handler> TYPE;
	private int row;
	private int col;
	private boolean cancelled;
	
	public static CellDoubleClickedEvent fire(HasCellEditedHandlers source, int row, int col) {
		if(TYPE != null) {
			CellDoubleClickedEvent event = new CellDoubleClickedEvent(row, col);
			source.fireEvent(event);
			return event;
		}
		return null;
	}
	
	protected CellDoubleClickedEvent(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	protected void dispatch(CellDoubleClickedEvent.Handler handler) {
		handler.onCellDoubleClicked(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CellDoubleClickedEvent.Handler> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public static Type<CellDoubleClickedEvent.Handler> getType() {
		if(TYPE == null) {
			TYPE = new Type<CellDoubleClickedEvent.Handler>();
		}
		return TYPE;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public interface Handler extends EventHandler {
	    void onCellDoubleClicked(CellDoubleClickedEvent event);
	}

}
