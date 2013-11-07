package org.openelis.ui.widget.table.event;

import org.openelis.ui.widget.table.Row;

import com.google.gwt.event.shared.GwtEvent;

public class RowDeletedEvent<T> extends GwtEvent<RowDeletedHandler<T>> {
	
	private static Type<RowDeletedHandler<?>> TYPE;
	private int index;
	private T row;
	
	public static <T> void fire(HasRowDeletedHandlers<T> source, int index, T row) {
		if(TYPE != null) {
			RowDeletedEvent<T> event = new RowDeletedEvent<T>(index, row);
			source.fireEvent(event);
		}
	}
	
	protected RowDeletedEvent(int index, T row) {
		this.row = row;
		this.index = index;
	}

	@Override
	protected void dispatch(RowDeletedHandler<T> handler) {
		handler.onRowDeleted(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RowDeletedHandler<T>> getAssociatedType() {
		return (Type) TYPE;
	}
	
	public static Type<RowDeletedHandler<?>> getType() {
		if(TYPE == null) {
			TYPE = new Type<RowDeletedHandler<?>>();
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
