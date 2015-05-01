package org.openelis.ui.widget.table.event;

import com.google.gwt.event.shared.GwtEvent;

public class RowAddedEvent<T> extends GwtEvent<RowAddedHandler<T>> {
	
	private static Type<RowAddedHandler<?>> TYPE;
	private int index;
	private T row;
	
	public static <T> void fire(HasRowAddedHandlers source, int index, T row) {
		if(TYPE != null) {
			RowAddedEvent<T> event = new RowAddedEvent<T>(index, row);
			source.fireEvent(event);
		}
	}
	
	protected RowAddedEvent(int index, T row) {
		this.row = row;
		this.index = index;
	}

	@Override
	protected void dispatch(RowAddedHandler<T> handler) {
		handler.onRowAdded(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RowAddedHandler<T>> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public static Type<RowAddedHandler<?>> getType() {
		if(TYPE == null) {
			TYPE = new Type<RowAddedHandler<?>>();
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
