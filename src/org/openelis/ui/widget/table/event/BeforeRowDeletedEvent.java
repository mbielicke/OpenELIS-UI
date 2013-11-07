package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.GwtEvent;

public class BeforeRowDeletedEvent<T> extends GwtEvent<BeforeRowDeletedHandler<T>> {
	
	private static Type<BeforeRowDeletedHandler<?>> TYPE;
	private int index;
	private T row;
	private boolean cancelled;
	
	public static <T> BeforeRowDeletedEvent<T> fire(HasBeforeRowDeletedHandlers<T> source, int index, T row) {
		if(TYPE != null) {
			BeforeRowDeletedEvent<T> event = new BeforeRowDeletedEvent<T>(index, row);
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
	protected void dispatch(BeforeRowDeletedHandler<T> handler) {
		handler.onBeforeRowDeleted(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BeforeRowDeletedHandler<T>> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public static Type<BeforeRowDeletedHandler<?>> getType() {
		if(TYPE == null) {
			TYPE = new Type<BeforeRowDeletedHandler<?>>();
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
