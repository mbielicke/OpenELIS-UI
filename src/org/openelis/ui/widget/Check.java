package org.openelis.ui.widget;

import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.dev.jjs.ast.HasName.Util;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;

public class Check extends FocusPanel implements HasValueChangeHandlers<Boolean> {
	
    /*
     * Enum to define checkbox modes.  This can be removed if it is decided that
     * a chekbox can only have three states in query mode only and can do normal 
     * input as a three state.
     */
    public enum Mode {
        TWO_STATE, THREE_STATE
    };
    
    protected CheckboxCSS css;

    /*
     * Fields for state and value
     */
    private Mode                            mode  = Mode.TWO_STATE;
    
    /*
     * Fields for query mode
     */
    protected boolean                       enabled;
    
    protected Boolean                       value;
    
    protected int                           eventsToSink;
    
    /**
     * Default no-arg constructor
     */
    public Check() {
        init();
    }

    /**
     * Constructor to set the mode of the Checkbox
     * 
     * @param mode
     */
    public Check(Mode mode) {
        init();
        setMode(mode);
    }

    /**
     * This method will set the Checkbox display and set Event handlers
     */
    public void init() {
        /**
         * If clicked call changeValue to rotate the state of the checkbox based
         * on its mode.
         */
        addHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                changeValue();
                event.stopPropagation();
            }
        }, ClickEvent.getType());

        /**
         * If Enter key hit while focused call changeValue to rotate the state
         * of the checkbox based on its mode.
         */
        addHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) 
                    changeValue();
            }
        }, KeyDownEvent.getType());
        
        addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				if(isEnabled())
					addStyleName(css.Focus());
			}
		});
        
        addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				removeStyleName(css.Focus());
			}
		});
        
        setCSS(UIResources.INSTANCE.checkbox());
        
    }

    /**
     * Changes the checkbox to be either TWO_STATE or THREE_STATE
     * 
     * @param type
     */
    public void setMode(Mode type) {
        this.mode = type;
        if (type == Mode.THREE_STATE)
            value = null;
        else
            value = false;
    }

    /**
     * Returns what mode the Checkbox is currently in
     * 
     * @return
     */
    public Mode getMode() {
        return mode;
    }
    
    /**
     * Method called form event handlers to switch the value of the checkbox
     */
    protected void changeValue() {
    	if(value == null)
    		value = true;
    	else if(!value) { 
    		if (mode == Mode.THREE_STATE)
    			value = null;
    		else
    			value = true;
    	}else
    		value = false;
    	
    	setStyle();
    	
    	ValueChangeEvent.fire(this, value);
    }
    
    public boolean isChecked() {
    	return value == null ? false : value == true;
    }
    
    public boolean isUnchecked() {
    	return value == null ? true : value == false;
    }
    
    public boolean isUnknown() {
    	return value == null;
    }
    
    public void check() {
    	value = true;
    	setStyle();
    }
    
    public void uncheck() {
    	value = false;
    	setStyle();
    }
    
    public void unkown() {
    	value = null;
    	setStyle();
    }
    
    protected void setStyle() {
        if(this.value == null && mode == Mode.THREE_STATE) 
       		setStylePrimaryName(css.Unknown());
        else if(value != null && value)
        	setStylePrimaryName(css.Checked());
        else
        	setStylePrimaryName(css.Unchecked());
    }
        

    // ******** Implementation of ScreenWidgetInt ***********************
    /**
     * Method to enable/disable the checkbox
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled)
            sinkEvents(Event.ONCLICK | Event.ONKEYDOWN);
        else
            unsinkEvents(Event.ONCLICK | Event.ONKEYDOWN);
    }

    /**
     * Method to determine if the checkbox is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return addHandler(handler,ValueChangeEvent.getType());
	}
	
	public void setCSS(CheckboxCSS css) {
		css.ensureInjected();
		this.css = css;
		setStyle();
	}
	
	@Override
	public void setWidth(String width) {
		getElement().getStyle().setWidth(org.openelis.ui.common.Util.stripUnits(width), Unit.PX);
	}
	
	@Override
	public void setHeight(String height) {
		getElement().getStyle().setHeight(org.openelis.ui.common.Util.stripUnits(height), Unit.PX);
	}


}
