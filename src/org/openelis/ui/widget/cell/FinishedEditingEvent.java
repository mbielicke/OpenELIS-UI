package org.openelis.ui.widget.cell;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FinishedEditingEvent extends GwtEvent<FinishedEditingEvent.Handler> {
	
	private static Type<FinishedEditingEvent.Handler> TYPE;
	
	public FinishedEditingEvent() {
		
	}
	
	public static Type<FinishedEditingEvent.Handler> getType() {
		if (TYPE == null) {
			TYPE = new Type<FinishedEditingEvent.Handler>();
		}
		return TYPE;		
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return (Type) TYPE;

	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onFinishEditing(this);
	}
	
	public static interface Handler extends EventHandler {
		void onFinishEditing(FinishedEditingEvent event);
	}

}
