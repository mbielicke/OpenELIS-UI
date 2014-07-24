/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.widget.calendar;

import java.util.ArrayList;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.Exceptions;
import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;
import org.openelis.ui.resources.CalendarCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.HasHelper;
import org.openelis.ui.widget.HasBalloon;
import org.openelis.ui.widget.IconContainer;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.TextBase;
import org.openelis.ui.widget.WidgetHelper;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.widget.datetimepicker.DatetimePicker;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class extends the TextBox<Datetime> and adds a button for using the
 * CalendarWidget to pick Dates.
 * 
 */
public class Calendar extends Composite implements ScreenWidgetInt,
												   Queryable,
												   Focusable,
												   HasBlurHandlers,
												   HasFocusHandlers,
												   HasValue<Datetime>,
												   HasHelper<Datetime>,
												   HasExceptions,
												   HasBalloon {
												  
	@UiTemplate("Select.ui.xml")
	interface CalendarUiBinder extends UiBinder<Widget, Calendar>{};
	public static final CalendarUiBinder uiBinder = GWT.create(CalendarUiBinder.class);
	
    /**
     * Used for Calendar display
     */
	@UiField
    protected LayoutPanel                           display;
	@UiField
    protected FocusPanel                            button;
    protected PopupPanel                            popup;
    protected DatetimePicker                        picker;
    protected int                                   width;
    protected boolean                               showingCalendar,queryMode,required;

    @UiField
    protected TextBase                              textbox;
    
    protected Datetime                              value;
    
    protected WidgetHelper<Datetime>                helper = new DateHelper();
    
    final Calendar                                  source;
    
    /**
     * Exceptions list
     */
    protected Exceptions                            exceptions;
    protected Balloon.Options                       options;
    
    protected CalendarCSS                           css;
    
    /**
     * Default no-arg constructor
     */
    public Calendar() {
    	source = this;

        final KeyboardHandler keyHandler = new KeyboardHandler();
        
        initWidget(uiBinder.createAndBindUi(this));
        
        /*
         * Set the focus style when the Focus event is fired Externally
         */
        addFocusHandler(new FocusHandler() {
        	public void onFocus(FocusEvent event) {
        		if(isEnabled())
        			display.addStyleName(css.Focus());
        	}
        });

        /*
         * Removes the focus style when the Blue event is fires externally
         */
        addBlurHandler(new BlurHandler() {
        	public void onBlur(BlurEvent event) {
        	    display.removeStyleName(css.Focus());
        		finishEditing(true);
        	}
        });
        
        
        exceptions = new Exceptions();

        addHandler(keyHandler, KeyDownEvent.getType());
        
        setCSS(UIResources.INSTANCE.calendar());
        
        setWidth("90px");
        
    }
    
    @UiHandler("textbox")
    public void onFocus(FocusEvent event) {
        FocusEvent.fireNativeEvent(event.getNativeEvent(), this);
    }
    
    @UiHandler("textbox")
    public void onBlur(BlurEvent event) {
        display.removeStyleName(css.Focus());
    	if(!showingCalendar && isEnabled())
    		BlurEvent.fireNativeEvent(event.getNativeEvent(), this);
    }
    
    @UiHandler("button")
    public void onClick(ClickEvent event) {
        showPopup();
    }
    
    @UiHandler("button")
	public void onMouseDown(MouseDownEvent event) {
		showingCalendar = true;
	}
    
    public String getText() {
        return textbox.getText();
    }
    
    public void setText(String text) {
        textbox.setText(text);
    }

    /**
     * This method will initialize and show the popup panel for this widget.
     */
    protected void showPopup() {
        Datetime time = null;
        
    	showingCalendar = true;
       
    	if (popup == null) {
            popup = new PopupPanel(true);
            popup.setStyleName(css.Popup());
            popup.setPreviewingAllNativeEvents(false);
            popup.addCloseHandler(new CloseHandler<PopupPanel>() {
                public void onClose(CloseEvent<PopupPanel> event) {
                	showingCalendar = false;
                	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                	    public void execute() {
                	      	setText(helper.format(picker.getDatetime()));
                	      	setFocus(true);
                	    }
                	});
                }
            });
        }
    	
    	
        try {            
            time = helper.getValue(textbox.getText());
        } catch (Exception e) {
            time = null;
        }
        
        if(picker == null) {
            picker = new DatetimePicker(((DateHelper)helper).getBegin(),
                                        ((DateHelper)helper).getEnd(),
                                        time);
            popup.setWidget(picker);
        }else
            picker.setDatetime(time);
        
        
        popup.showRelativeTo(source);

    }
    
    @Override
    public void setWidth(String w) {
        width = Util.stripUnits(w);
 
        if (display != null){
            display.setWidth(w);
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    @Override
    public void setHeight(String height) {
        display.setHeight(height);
        button.setHeight(height);
    }

    /**
     * This private class will handle key events for this widget
     * 
     * @author tschmidt
     * 
     */
    private class KeyboardHandler implements KeyDownHandler {

        public void onKeyDown(KeyDownEvent event) {
            switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_ENTER:
                    showPopup();
            }
        }
    }

    /**
     * Overridden method from TextBox for enabling and disabling the widget
     */
    @Override
    public void setEnabled(boolean enabled) {
        textbox.enforceMask(enabled && !queryMode);
        textbox.setReadOnly(!enabled);
        if (enabled) {
            sinkEvents(Event.ONKEYDOWN | Event.ONKEYUP);
            button.sinkEvents(Event.ONCLICK);
            button.getElement().getStyle().setCursor(Cursor.POINTER);
        }else{
            unsinkEvents(Event.ONKEYDOWN | Event.ONKEYUP);
            button.unsinkEvents(Event.ONCLICK);
            button.getElement().getStyle().setCursor(Cursor.AUTO);
        }
    }

    /**
     * Overridden method from TextBox for setting the Exception style.
     */
    @Override
    public void addExceptionStyle() {
    	if(Balloon.isWarning(this))
    	    addStyleName(css.InputWarning());
    	else
    		addStyleName(css.InputError());
    }

    /**
     * Overridden method from TextBox for removing the Exception style.
     */
    @Override
    public void removeExceptionStyle() {
        removeStyleName(css.InputError());
        removeStyleName(css.InputWarning());
    }
    
    @Override
    public void setHelper(WidgetHelper<Datetime> helper) {
    	DateHelper dh;
    	
    	this.helper = helper;
    	
    	setDefaultMask();
    }
    
    public void setPrecision(byte begin, byte end) {
    	assert(begin < end) : "Precsion in wrong order";
    	
    	((DateHelper)getHelper()).setBegin(begin);
    	((DateHelper)getHelper()).setEnd(end);
    	
    	picker = null;
    	
    	setDefaultMask();
    	
    }
    
    public void setBegin(int begin) {
        ((DateHelper)getHelper()).setBegin((byte)begin);
        
        picker = null;
        
        setDefaultMask();
    }
    
    public void setEnd(int end) {
        ((DateHelper)getHelper()).setEnd((byte)end);
        
        picker = null;
        
        setDefaultMask();
    }
    
    private void setDefaultMask() {
    	DateHelper dh;
    	
    	dh = (DateHelper)getHelper();
    	/*
    	 * Setting default mask based on precision of helper
    	 * internationalized mask pictures should be set from 
    	 * xsl, but defaults are provided if none set.
    	 */
    	if(dh.getBegin() > Datetime.DAY) {
    		textbox.setMask(getMessages().gen_timeMask());
    		setWidth("60px");
    	} else if (dh.getEnd() < Datetime.HOUR){
    		textbox.setMask(getMessages().gen_dateMask());
    		setWidth("90px");
    	} else {
    		textbox.setMask(getMessages().gen_dateTimeMask());
    		setWidth("125px");
    	}
    }

    /**
     * Returns the current value for this widget.
     */
    public Datetime getValue() {
        return value;
    }

    /**
     * Sets the current value of this widget without firing the
     * ValueChangeEvent.
     */
    public void setValue(Datetime value) {
        setValue(value, false);
    }

    /**
     * Sets the current value of this widget and will fire a ValueChangeEvent if
     * the value is different than what is currently stored.
     */
    public void setValue(Datetime value, boolean fireEvents) {
    	
        if(!Util.isDifferent(this.value, value)) {
        	if(value != null)
        		textbox.setText(helper.format(value));
        	else { 
        	    // Here to make sure text and exceptions are cleared
        	    // when null is set to calendar with invalid data
        	    textbox.setText("");
        	    clearExceptions();
        	}
            return;
        }
        
        this.value = value;
        if (value != null) {
            textbox.setText(helper.format(value));
        } else {
            textbox.setText("");
        }

        if (fireEvents) 
            ValueChangeEvent.fire(this, value);
        
    }

    /**
     * This method is made available so the Screen can on commit make sure all
     * required fields are entered without having the user visit each widget on
     * the screen.
     */
    public void finishEditing() {
        finishEditing(false);
    }

    /**
     * This method will call the Helper to get the T value from the entered
     * string input. if invalid input is entered, Helper is expected to throw an
     * en exception and that exception will be added to the validate exceptions
     * list.
     * 
     * @param fireEvents
     */
    protected void finishEditing(boolean fireEvents) {
    	String text;
    	
    	if(isEnabled()) {
    		if(queryMode) {
    			validateQuery();
    		}else {
    			
    			text = textbox.getText();
    		
    			clearValidateExceptions();
        
    			try {
    				setValue(helper.getValue(text), fireEvents);
    				if (required && value == null) 
    					addValidateException(new Exception(getMessages().exc_fieldRequired()));
    			} catch (Exception e) {
    				addValidateException(e);
    			}
    			Balloon.checkExceptionHandlers(this);
    		}
    	}
    }

    /**
     * Method used to validate the inputed query string by the user.
     */
    public void validateQuery() {
        try {
            clearValidateExceptions();
            helper.validateQuery(textbox.getText());
        } catch (Exception e) {
            addValidateException(e);
        }
        Balloon.checkExceptionHandlers(this);
    }
    
	// ********** Implementation of HasException interface ***************
	/**
	 * Convenience method to check if a widget has exceptions so we do not need
	 * to go through the cost of merging the logical and validation exceptions
	 * in the getExceptions method.
	 * 
	 * @return
	 */
	public boolean hasExceptions() {
		if (getValidateExceptions() != null)
			return true;

		if (!queryMode && required && getValue() == null) {
			addValidateException(new Exception(Messages.get().exc_fieldRequired()));
			Balloon.checkExceptionHandlers(this);
		}

		return getEndUserExceptions() != null || getValidateExceptions() != null;
	}

	/**
	 * Adds a manual Exception to the widgets exception list.
	 */
	public void addException(Exception error) {
		exceptions.addException(error);
		Balloon.checkExceptionHandlers(this);
	}

	protected void addValidateException(Exception error) {
		exceptions.addValidateException(error);

	}

	/**
	 * Combines both exceptions list into a single list to be displayed on the
	 * screen.
	 */
	public ArrayList<Exception> getValidateExceptions() {
		return exceptions.getValidateExceptions();
	}

	public ArrayList<Exception> getEndUserExceptions() {
		return exceptions.getEndUserExceptions();
	}

	/**
	 * Clears all manual and validate exceptions from the widget.
	 */
	public void clearExceptions() {
		exceptions.clearExceptions();
		removeExceptionStyle();
		Balloon.clearExceptionHandlers(this);
	}

	public void clearEndUserExceptions() {
		exceptions.clearEndUserExceptions();
		Balloon.checkExceptionHandlers(this);
	}

	public void clearValidateExceptions() {
		exceptions.clearValidateExceptions();
		Balloon.checkExceptionHandlers(this);
	}
    // ************* Implementation of Focusable ******************

    /**
     * Method only implemented to satisfy Focusable interface.
     */
    public int getTabIndex() {
        return -1;
    }

    /**
     * Method only implemented to satisfy Focusable interface.
     */
    public void setTabIndex(int index) {

    }

    /**
     * Method only implemented to satisfy Focusable interface.
     */
    public void setAccessKey(char key) {

    }
    
    /**
     * Exposing this method on the wrapped widget
     */
    public void selectAll() {
    	textbox.selectAll();
    }
    
    /**
     * Exposing this method on the wrapped widget
     */
    public void setSelectionRange(int pos, int length) {
    	textbox.setSelectionRange(pos, length);
    }
    
    /**
     * Exposing this method on the wrapped widget
     */
    public void unselectAll() {
    	textbox.setSelectionRange(0, 0);
    }
    
    public void setMask(String mask) {
    	textbox.setMask(mask);
    }

    /**
     * This is need for Focusable interface and to allow programmatic setting of
     * focus to this widget. We use the wrapped TextBox to make this work.
     */
    public void setFocus(boolean focused) {
        textbox.setFocus(true);
    }

    // ************ Handler Registration methods *********************

    /**
     * The Screen will add its screenHandler here to register for the
     * onValueChangeEvent
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Datetime> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * This Method is here so the Focus logic of ScreenPanel can be notified
     */
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    /**
     * This method is here so the Focus logic of ScreenPanel can be notified
     */
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }

    /**
     * Adds a mouseover handler to the textbox for displaying Exceptions
     */
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    /**
     * Adds a MouseOut handler for hiding exceptions display
     */
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler, MouseOutEvent.getType());
    }
    
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

	@Override
	public WidgetHelper<Datetime> getHelper() {
		return helper;
	}

    // ******** Implementation of Queryable *****************
    /**
     * This method will toggle TextBox into and from query mode and suspend or
     * resume any format restrictions
     */
    public void setQueryMode(boolean query) {
        if (queryMode == query) 
            return;
        

        queryMode = query;
        textbox.enforceMask(!query);
        textbox.setText("");
        value = null;
    }

    /**
     * Returns a single QueryData object representing the query string entered
     * by the user. The Helper class is used here to create the correct
     * QueryData object for the passed type T.
     */
    public Object getQuery() {
    	Object query;
    	
    	query = helper.getQuery(textbox.getText());
                
        return query;
    }
    
    /**
     * Sets a query string to this widget when loaded from a table model
     */
    public void setQuery(QueryData qd) {
        if(qd != null)
            textbox.setText(qd.getQuery());
        else
            textbox.setText("");
    }
    
    /**
     * Method used to determine if widget is currently in Query mode
     */
    public boolean isQueryMode() {
    	return queryMode;
    }

	@Override
	public boolean isEnabled() {
		return !textbox.isReadOnly();
	}
	
    /**
     * Set the text alignment.
     */
    public void setTextAlignment(TextAlignment alignment) {
        textbox.setTextAlignment(alignment);
    }


    /**
     * Method used to set if this widget is required to have a value inputed.
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public void setCSS(CalendarCSS css) {
    	css.ensureInjected();
    	this.css = css;
    	
        button.setStyleName(css.CalendarButton());
        display.setStyleName(css.SelectBox());
        textbox.setStyleName(css.SelectText());
    }

    public void setAsText(boolean asTextBox) {
        button.setVisible(false);
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
    
    protected UIMessages getMessages() {
        return Messages.get();
    }
}
