package org.openelis.ui.widget;


import org.openelis.ui.widget.Balloon.Placement;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;

public class IconContainer extends FocusPanel implements HasAllMouseHandlers, HasClickHandlers, HasBalloon {
	
	protected Balloon.Options options;
    
    public IconContainer() {

    }
    
    public IconContainer(String style) {
        this();
        setStyleName(style);
    }
    
    public void enable(boolean enabled) {
        if(!enabled) {
        	unsinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONKEYPRESS);
        	getElement().getStyle().setCursor(Cursor.AUTO);
        }else {
        	sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONKEYPRESS);
        	getElement().getStyle().setCursor(Cursor.POINTER);
        }
        	
    }
    
    public void setTip(String text) {
        if(text != null) {
            if(options == null) 
                options = new Balloon.Options(this);
            options.setTip(text);
         }else if(text == null && options != null) {
            options.destroy();
            options = null;
        }
    }
    
    public void setTipPlacement(Placement placement) {
        if(options == null)
            options = new Balloon.Options(this);
        
        options.setPlacement(placement);
    }
            
    @UiChild(tagname="balloonOptions",limit=1)
    public void setBalloonOptions(Balloon.Options tip) {
        this.options = tip;
        options.setTarget(this);
    }
    
    public Balloon.Options getBalloonOptions() {
        return options;
    }

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler,MouseDownEvent.getType());
	}

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler,MouseUpEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler,MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler,MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler,MouseMoveEvent.getType());
	}

	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler,MouseWheelEvent.getType());
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler,ClickEvent.getType());
	}
}
