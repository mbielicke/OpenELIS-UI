package org.openelis.ui.widget.touch.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SwipeEvent extends GwtEvent<SwipeEvent.Handler>{
	
	private static final Type<SwipeEvent.Handler> TYPE = new Type<SwipeEvent.Handler>();


	public static enum Direction {LEFT_TO_RIGHT,RIGHT_TO_LEFT}
	protected Direction direction;
	
	public SwipeEvent(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public static Type<Handler> getType() {
		return TYPE;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSwipe(this);
	}

	public static interface Handler extends EventHandler {
		public void onSwipe(SwipeEvent event);
	}
}
