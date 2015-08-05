package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.resources.TextCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon.Placement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.Composite;
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
public class Label<T> extends Composite implements HasValue<T>,
                                                   HasHelper<T>,
                                                   HasExceptions,
                                                   HasBalloon,
                                                   HasClickHandlers {

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
    
    protected Balloon.Options   options;
    
    protected TextCSS css;
    
    protected com.google.gwt.user.client.ui.Label label;

    /**
     * Default no-arg constructor
     */
    public Label() {
        label = GWT.create(com.google.gwt.user.client.ui.Label.class);
        initWidget(label);
        setCSS(UIResources.INSTANCE.text());
    }

    /**
     * Constructor that accepts a default value
     * 
     * @param value
     */
    public Label(T value) {
        this();
        setValue(value);
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
        label.setText(helper.format(value));
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
        Balloon.checkExceptionHandlers(this);
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
        Balloon.checkExceptionHandlers(this);
    }
    
    public void clearEndUserExceptions() {
        endUserExceptions = null;
        Balloon.checkExceptionHandlers(this);
    }
    
    public void clearValidateExceptions() {
        validateExceptions = null;
        Balloon.checkExceptionHandlers(this);
    }

    /**
     * Will add the style to the widget.
     */
    public void addExceptionStyle() {
    	if(!Balloon.isWarning(this))
    		label.addStyleName(css.InputError());
    	else
    		label.addStyleName(css.InputWarning());
    }

    /**
     * will remove the style from the widget
     */
    public void removeExceptionStyle() {
        label.removeStyleName(css.InputError());
        label.removeStyleName(css.InputWarning());
    }
    
    public void setCSS(TextCSS css) {
    	css.ensureInjected();
    	
    	if(getStyleName().contains(css.InputError())) {
    		label.removeStyleName(this.css.InputError());
    		label.addStyleName(css.InputError());
    	}
    	if(getStyleName().contains(css.InputWarning())) {
    		label.removeStyleName(this.css.InputWarning());
    		label.addStyleName(css.InputWarning());
    	}
    	
    	this.css = css;
    	
    	setStylePrimaryName(css.ScreenLabel());	
    }

    public void setField(String field) {
    	if(field.equals("Date")) {
    		DateHelper helper = new DateHelper();
    		setHelper((WidgetHelper)helper);
    	}else if(field.equals("Integer")) {
    		IntegerHelper helper = new IntegerHelper();
    		setHelper((WidgetHelper)helper);
    	}else if(field.equals("Double")) {
    		DoubleHelper helper = new DoubleHelper();
    		setHelper((WidgetHelper)helper);
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

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return label.addMouseOverHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return label.addMouseOutHandler(handler);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return label.addClickHandler(handler);
    }
    
    public void setText(String text) {
        label.setText(text);
    }
    
    public void setWordWrap(boolean wrap) {
        label.setWordWrap(wrap);
    }
}
