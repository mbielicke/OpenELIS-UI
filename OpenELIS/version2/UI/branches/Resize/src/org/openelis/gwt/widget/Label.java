package org.openelis.gwt.widget;

import java.util.ArrayList;

import org.openelis.gwt.resources.UIResources;
import org.openelis.gwt.resources.TextCSS;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * This class extends the GWT Label class and adds HasValue, and HasHelper
 * interfaces so that a Label can show formatted data. For instance a label that
 * shows a timestamp would implement HasValue<Datetieme> and HasHelper<Datetime>
 * so the screen in onDataChange can assign a Datetime value to the Label and
 * the Label will use the helper to show a correctly localized date string.
 * 
 * @author tschmidt
 * 
 * @param <T>
 */
public class Label<T> extends com.google.gwt.user.client.ui.Label implements HasValue<T>,
                                                                             HasHelper<T>,
                                                                             HasExceptions {

    /*
     * value and helper fields
     */
    protected T               value;
    
    @SuppressWarnings("unchecked")
	protected WidgetHelper<T> helper = (WidgetHelper<T>)new StringHelper();

    /*
     * Exceptions list
     */
    protected ArrayList<Exception> endUserExceptions, validateExceptions;
    
    protected TextCSS css;

    /**
     * Default no-arg constructor
     */
    public Label() {
        super();
        setCSS(UIResources.INSTANCE.text());
    }

    /**
     * Constructor that accepts a default value
     * 
     * @param value
     */
    public Label(T value) {
        super();
        setValue(value);
        setCSS(UIResources.INSTANCE.text());
    }

    // *********** Implementaton of HasValue<T> ************

    /**
     * Returns the currently set value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the current value of the label.
     */
    public void setValue(T value) {
        this.value = value;
        setText(helper.format(value));
    }

    /**
     * Sets the current value of the label. Label does not fire
     * ValueChangeEvents so fireEvents param is ignored and method is only here
     * to satisfy HasValue<T> interface.
     */
    public void setValue(T value, boolean fireEvents) {
        setValue(value);
    }

    /**
     * Stub method to satisfy HasValue interface.
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return null;
    }

    // ********* Implementation of HasHelper *****************
    /**
     * Sets the Helper to be used by this widget
     */
    public void setHelper(WidgetHelper<T> helper) {
        this.helper = helper;
    }

    /**
     * Returns the Helper being used by this widget.
     */
    public WidgetHelper<T> getHelper() {
        return helper;
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
        return endUserExceptions != null || validateExceptions != null;
    }

    /**
     * Adds a manual Exception to the widgets exception list.
     */
    public void addException(Exception error) {
        if (endUserExceptions == null)
            endUserExceptions = new ArrayList<Exception>();
        endUserExceptions.add(error);
        ExceptionHelper.checkExceptionHandlers(this);
    }

    protected void addValidateException(Exception error) {
        if (validateExceptions == null)
            validateExceptions = new ArrayList<Exception>();
        validateExceptions.add(error);
    }

    /**
     * Combines both exceptions list into a single list to be displayed on the
     * screen.
     */
    public ArrayList<Exception> getValidateExceptions() {
        return validateExceptions;
    }

    public ArrayList<Exception> getEndUserExceptions() {
        return endUserExceptions;
    }

    /**
     * Clears all manual and validate exceptions from the widget.
     */
    public void clearExceptions() {
        endUserExceptions = null;
        validateExceptions = null;
        ExceptionHelper.checkExceptionHandlers(this);
    }
    
    public void clearEndUserExceptions() {
        endUserExceptions = null;
        ExceptionHelper.checkExceptionHandlers(this);
    }
    
    public void clearValidateExceptions() {
        validateExceptions = null;
        ExceptionHelper.checkExceptionHandlers(this);
    }

    /**
     * Will add the style to the widget.
     */
    public void addExceptionStyle() {
    	if(ExceptionHelper.isWarning(this))
    		addStyleName(css.InputError());
    	else
    		addStyleName(css.InputWarning());
    }

    /**
     * will remove the style from the widget
     */
    public void removeExceptionStyle() {
        removeStyleName(css.InputError());
        removeStyleName(css.InputWarning());
    }
    
    public void setCSS(TextCSS css) {
    	css.ensureInjected();
    	
    	if(getStyleName().contains(css.InputError())) {
    		removeStyleName(this.css.InputError());
    		addStyleName(css.InputError());
    	}
    	if(getStyleName().contains(css.InputWarning())) {
    		removeStyleName(this.css.InputWarning());
    		addStyleName(css.InputWarning());
    	}
    	
    	this.css = css;
    	
    	setStylePrimaryName(css.ScreenLabel());	
    }

    public void setField(String field) {
    	if(field.equals("Date")) {
    		DateHelper helper = new DateHelper();
    		helper.setBegin((byte)0);
    		helper.setEnd((byte)2);
    		setHelper((WidgetHelper)helper);
    	}
    }
}