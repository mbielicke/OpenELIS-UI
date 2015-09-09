package org.openelis.ui.widget.celltable.event;

import org.openelis.ui.widget.celltable.Row;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeRowDeletedEvent<T> extends GwtEvent<BeforeRowDeletedHandler> {
	
	private static Type<BeforeRowDeletedHandler> TYPE;
	private int index;
	private T row;
	private boolean cancelled;
	
	public static BeforeRowDeletedEvent fire(HasBeforeRowDeletedHandlers source, int index, Object row) {
		if(TYPE != null) {
			BeforeRowDeletedEvent event = new BeforeRowDeletedEvent(index, row);
			source.fireEvent(event);
			return event;
		}
		return null;
	}
	
	protected BeforeRowDeletedEvent(int index, T row) {
		this.row = row;
		this.index = index;
	}

	@Override
	protected void dispatch(BeforeRowDeletedHandler handler) {
		handler.onBeforeRowDeleted(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BeforeRowDeletedHandler> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public static Type<BeforeRowDeletedHandler> getType() {
		if(TYPE == null) {
			TYPE = new Type<BeforeRowDeletedHandler>();
		}
		return TYPE;
	}
	
	public T getRow() {
		return row;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	

}
