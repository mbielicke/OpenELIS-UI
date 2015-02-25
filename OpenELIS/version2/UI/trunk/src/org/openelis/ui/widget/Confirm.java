package org.openelis.ui.widget;

import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.ConfirmCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Window.Caption;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class will display a modal window with a message and buttons for 
 * the user to confirm
 * @author tschmidt
 *
 */
public class Confirm extends PopupPanel implements HasSelectionHandlers<Integer>, ClickHandler, NativePreviewHandler {
    
    @UiTemplate("Confirm.ui.xml")
    interface ConfirmUiBinder extends UiBinder<Widget, Confirm>{};
    private static final ConfirmUiBinder uiBinder = GWT.create(ConfirmUiBinder.class);
   
    int active = -1;
    
    @UiField
    HorizontalPanel buttonPanel;
    
    @UiField 
    AbsolutePanel   icon;
    
    @UiField 
    HTML           text;
    
    @UiField 
    Caption        cap;
    
    @UiField 
    FocusPanel     close;
    
    HandlerRegistration keyHandler;
    
    public enum Type {WARN,ERROR,QUESTION,BUSY};
    
    ConfirmCSS css; 
    
    public Confirm() {
        
    }
        
    public Confirm(Type type, String caption, String message, String... buttons) {    	
    	
        setWidget(uiBinder.createAndBindUi(this));
    	
    	setModal(true);
    	
    	css = UIResources.INSTANCE.confirm();
    	css.ensureInjected();
    	    	
    	switch(type) {
    		case WARN : {
    			icon.setStyleName(css.largeWarnIcon());
    			if(caption == null || caption.equals(""))
    				cap.add(new HTML(Messages.get().confirm_warning()));
    			break;
    		}
    		case ERROR : {
        	    icon.setStyleName(css.largeErrorIcon());
        		if(caption == null || caption.equals(""))
        			cap.add(new HTML(Messages.get().confirm_error()));
        		break;
        	}
    		case QUESTION : {
        		icon.setStyleName(css.largeQuestionIcon());
        		if(caption == null || caption.equals(""))
        			cap.add(new HTML(Messages.get().confirm_question()));
        		break;
        	}
    		case BUSY : {
    			icon.setStyleName(css.spinnerIcon());
    			if(caption == null || caption.equals(""))
    				cap.add(new HTML(Messages.get().confirm_busy()));
    			break;
    		}
    	}
   	    
    	text.setText(message);
    	text.setWordWrap(true);
    	text.setStyleName(css.ScreenWindowLabel());
    

    	HTML label = new HTML(caption);
    	label.setStyleName(css.ScreenWindowLabel());
    	cap.add(label);
    	
    	if(buttons != null && buttons[0] != null ){
    		createButtons(buttons);
    	}else
    	    buttonPanel.setVisible(false);
    	    
        close.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        
        setSize("250px","125px");
    	
    }
    
    @Override
    public void show() {
        super.show();
        center();
        if (buttonPanel.getWidgetCount() > 0) {
        	((Focusable)buttonPanel.getWidget(0)).setFocus(true);
        }
    }
    
    public void hide() {
        if(keyHandler != null) 
            keyHandler.removeHandler();
    	hide(false);
    }
    
    private void createButtons(String[] buttons) {
    	for(int i = 0; i < buttons.length; i++) {
    		Button ab = new Button("",buttons[i]);
    		ab.setAction(String.valueOf(i));
    		ab.setEnabled(true);
    		buttonPanel.add(ab);
    		ab.addClickHandler(this);
    	}    
    }

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
	
	public void onClick(ClickEvent event) {
		int clicked;
		
		clicked = new Integer(((Button)event.getSource()).getAction()).intValue();
		SelectionEvent.fire(this,new Integer(((Button)event.getSource()).getAction()));
		hide();
        if(active > -1)
           	((Button)buttonPanel.getWidget(active)).setFocus(true);
        active = -1;
        ((Button)buttonPanel.getWidget(clicked)).setFocus(false);
           
		
	}

	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if(event.getTypeInt() == Event.ONKEYDOWN){
			if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB){
				((Button)buttonPanel.getWidget(active)).setFocus(false);
				active++;
				if(active == buttonPanel.getWidgetCount())
					active = 0;
				((Button)buttonPanel.getWidget(active)).setFocus(true);
			}
			if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
				SelectionEvent.fire(this, active);
				hide();
			}
		}
	}
    
}
