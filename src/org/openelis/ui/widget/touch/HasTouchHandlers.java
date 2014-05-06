package org.openelis.ui.widget.touch;

import com.google.gwt.event.dom.client.HasTouchCancelHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchMoveHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;

public interface HasTouchHandlers extends HasTouchStartHandlers, 
										  HasTouchEndHandlers, 
										  HasTouchMoveHandlers,
										  HasTouchCancelHandlers {

}
