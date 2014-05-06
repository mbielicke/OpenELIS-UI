package org.openelis.ui.widget.touch;

import org.openelis.ui.widget.touch.event.SwipeEvent;

import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;

public class TouchResizeComposite extends ResizeComposite implements HasTouchHandlers {
	
	int start;
	
	public TouchResizeComposite() {
		addTouchStartHandler(new TouchStartHandler() {	
			@Override
			public void onTouchStart(TouchStartEvent event) {
				start = event.getTouches().get(0).getPageX();
			}
		});
		
		addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				int move = event.getTouches().get(0).getPageX();
				if(Math.abs(move - start) > 10) {
					if(move-start > 0)
						fireEvent(new SwipeEvent(SwipeEvent.Direction.LEFT_TO_RIGHT));
					else
						fireEvent(new SwipeEvent(SwipeEvent.Direction.RIGHT_TO_LEFT));
				}
			}
		});
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return addDomHandler(handler, TouchMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
		return addDomHandler(handler, TouchCancelEvent.getType());
	}

	public HandlerRegistration addSwipeHandler(SwipeEvent.Handler handler) {
		return addHandler(handler,SwipeEvent.getType());
	}
}
