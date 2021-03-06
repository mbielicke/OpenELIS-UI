package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.common.Exceptions;
import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.TextCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon.Options;
import org.openelis.ui.widget.Balloon.Placement;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public class TextArea extends Composite implements ScreenWidgetInt, Focusable, HasBlurHandlers,
                                       HasFocusHandlers, HasValueChangeHandlers<String>,
                                       HasValue<String>, HasExceptions, Queryable, HasBalloon {

    /**
     * Wrapped GWT TextBox
     */
    protected com.google.gwt.user.client.ui.TextArea textarea;

    /**
     * Textbox attributes
     */
    protected TextAlignment                          alignment = TextAlignment.LEFT;

    /**
     * Exceptions list
     */
    protected Exceptions                             exceptions;

    /**
     * Data moved from Field to the widget
     */
    protected boolean                                queryMode, required;
    protected String                                 value;

    /**
     * This class replaces the functionality that Field used to provide but now
     * in a static way.
     */
    protected WidgetHelper<String>                   helper    = new StringHelper();

    protected TextCSS                                css;

    protected Balloon.Options                        options;

    /**
     * The Constructor now sets the wrapped GWT TextBox as the element widget of
     * this composite and adds an anonymous ValueCahngeHandler to handle input
     * from the user.
     */
    public TextArea() {
        init();
    }

    public void init() {

        textarea = new com.google.gwt.user.client.ui.TextArea();

        addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                if (isEnabled()) {
                    // textarea.selectAll();
                    addStyleName(css.Focus());
                }
            }
        });

        addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                // textarea.setSelectionRange(0,0);
                removeStyleName(css.Focus());
                finishEditing();
            }

        });

        initWidget(textarea);

        setCSS(UIResources.INSTANCE.text());

        exceptions = new Exceptions();
    }

    // ************** Methods for TextBox attributes ***********************

    /**
     * Set the text alignment.
     */
    public void setTextAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        textarea.setAlignment(alignment);
    }

    public int getCursorPos() {
        return textarea.getCursorPos();
    }

    public void setSelectionRange(int pos, int length) {
        textarea.setSelectionRange(pos, length);
    }

    // ************** Implementation of ScreenWidgetInt ********************

    /**
     * Enables or disables the textbox for editing.
     */
    public void setEnabled(boolean enabled) {
        textarea.setReadOnly( !enabled);
    }

    /**
     * Returns whether the text is enabled for editing
     */
    public boolean isEnabled() {
        return !textarea.isReadOnly();
    }

    /**
     * This method will toggle textbox into and from query mode and suspend or
     * resume any format restrictions
     */
    public void setQueryMode(boolean query) {
    	if(queryMode != query) {
    		textarea.setText("");
    		textarea.setAlignment(TextAlignment.LEFT);
    	}
    	
    	queryMode = query;
    }

    /**
     * Returns a single QueryData object representing the query string entered
     * by the user. The Helper class is used here to create the correct
     * QueryData object for the passed type T.
     */
    public Object getQuery() {
        return helper.getQuery(textarea.getText());
    }

    public void setHelper(WidgetHelper<String> helper) {
        this.helper = helper;
    }

    public WidgetHelper<String> getHelper() {
        return null;
    }

    /**
     * This method will call the Helper to get the T value from the entered
     * string input. if invalid input is entered, Helper is expected to throw an
     * en exception and that exception will be added to the validate exceptions
     * list.
     * 
     * @param fireEvents
     */
    public void finishEditing() {
        exceptions.clearValidateExceptions();

        if (isEnabled()) {
            if (queryMode)
                validateQuery();
            else {

                try {
                    setValue(helper.getValue(textarea.getText()), true);
                    if (required && value == null)
                        addValidateException(new Exception(Messages.get().exc_fieldRequired()));
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
            exceptions.clearValidateExceptions();
            helper.validateQuery(textarea.getText());
        } catch (Exception e) {
            addValidateException(e);
        }
        Balloon.checkExceptionHandlers(this);
    }

    public void setRequired(boolean required) {
        this.required = required;
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
        if (exceptions.getValidateExceptions() != null)
            return true;

        if ( !queryMode && required && getValue() == null) {
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

    /**
     * Will add the style to the widget.
     */
    public void addExceptionStyle() {
        if (Balloon.isWarning(this))
            addStyleName(css.InputWarning());
        else
            addStyleName(css.InputError());
    }

    /**
     * will remove the style from the widget
     */
    public void removeExceptionStyle() {
        removeStyleName(css.InputError());
        removeStyleName(css.InputWarning());
    }

    // ************** Implementation of HasValue<T> interface ***************

    /**
     * Returns the current value for this widget.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the current value of this widget without firing the
     * ValueChangeEvent.
     */
    public void setValue(String value) {
        setValue(value, false);
    }

    /**
     * Sets the current value of this widget and will fire a ValueChangeEvent if
     * the value is different than what is currently stored.
     */
    public void setValue(String value, boolean fireEvents) {

        if ( !Util.isDifferent(this.value, value))
            return;

        this.value = value;
        if (value != null) {
            textarea.setText(helper.format(value));
        } else {
            textarea.setText("");
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
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
     * This is need for Focusable interface and to allow programmatic setting of
     * focus to this widget. We use the wrapped TextBox to make this work.
     */
    public void setFocus(boolean focused) {
        textarea.setFocus(true);
    }

    // ************ Handler Registration methods *********************

    /**
     * The Screen will add its screenHandler here to register for the
     * onValueChangeEvent
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
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

    public void setCSS(TextCSS css) {
        css.ensureInjected();
        this.css = css;
        textarea.setStyleName(css.ScreenTextArea());
    }

    public void setTip(String text) {
        if (text != null) {
            if (options == null)
                options = new Balloon.Options(this);
            options.setTip(text);
        } else if (text == null && options != null) {
            options.destroy();
            options = null;
        }
    }

    public void setTipPlacement(Placement placement) {
        if (options == null)
            options = new Balloon.Options(this);

        options.setPlacement(placement);
    }

    @UiChild(tagname = "balloonOptions", limit = 1)
    public void setBalloonOptions(Balloon.Options tip) {
        this.options = tip;
        options.setTarget(this);
    }

    public Balloon.Options getBalloonOptions() {
        return options;
    }

    @Override
    public void setQuery(QueryData qd) {
        if (qd != null) {
            textarea.setText(qd.getQuery());
        }
    }

    @Override
    public boolean isQueryMode() {
        return queryMode;
    }
}
