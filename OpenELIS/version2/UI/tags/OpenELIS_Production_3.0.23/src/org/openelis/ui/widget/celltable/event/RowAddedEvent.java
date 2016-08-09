package org.openelis.ui.widget.celltable.event;

import org.openelis.ui.widget.celltable.Row;

import com.google.gwt.event.shared.GwtEvent;

public class RowAddedEvent<T> extends GwtEvent<RowAddedHandler> {
	
	private static Type<RowAddedHandler> TYPE;
	private int index;
	private T row;
	
	public static void fire(HasRowAddedHandlers source, int index, Object row) {
		if(TYPE != null) {
			RowAddedEvent event = new RowAddedEvent(index, row);
			source.fireEvent(event);
		}
	}
	
	protected RowAddedEvent(int index, T row) {
		this.row = row;
		this.index = index;
	}

	@Override
	protected void dispatch(RowAddedHandler handler) {
		handler.onRowAdded(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RowAddedHandler> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public static Type<RowAddedHandler> getType() {
		if(TYPE == null) {
			TYPE = new Type<RowAddedHandler>();
		}
		return TYPE;
	}
	
	public T getRow() {
		return row;
	}
	
	public int getIndex() {
		return index;
	}
	

}
