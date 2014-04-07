/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.HasDataChangeHandlers;
import org.openelis.ui.event.HasStateChangeHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.event.StateChangeHandler;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.resources.WindowCSS;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to bring together widgets into a logical unit of work that
 * is presented to the user.
 * 
 */
public class Screen extends ResizeComposite implements FocusHandler, HasDataChangeHandlers,
                                           HasStateChangeHandlers {

    private Focusable                           focused;

    protected HashMap<String, ScreenHandler<?>> handlers;
    protected HashMap<Widget, ScreenHandler<?>> widgets;
    protected HashMap<Shortcut, Focusable>      shortcuts;

    protected AbsolutePanel                     glass;
    protected int                               busy;

    protected EventBus                          bus;

    public enum ShortKeys {
        CTRL, SHIFT, ALT
    };

    protected WindowCSS css;
    protected WindowInt window;
    protected State     state;

    @SuppressWarnings("rawtypes")
    public Screen() {
        css = UIResources.INSTANCE.window();
        css.ensureInjected();

        handlers = new HashMap<String, ScreenHandler<?>>();
        widgets = new HashMap<Widget, ScreenHandler<?>>();
        shortcuts = new HashMap<Shortcut, Focusable>();

        bus = new SimpleEventBus();

        addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB && focused != null) {

                    if ( ! ((ScreenWidgetInt)focused).isEnabled()) {
                        event.preventDefault();
                        event.stopPropagation();
                        return;
                    }

                    event.preventDefault();
                    event.stopPropagation();
                    Focusable next = focused;
                    do {
                        next = (Focusable)widgets.get(next).onTab( !event.isShiftKeyDown());
                    } while (next != null && ! ((ScreenWidgetInt)next).isEnabled());
                    if (next != null)
                        next.setFocus(true);

                    return;
                }

                if (event.isAnyModifierKeyDown()) {

                    boolean ctrl, alt, shift;
                    char key;

                    /*
                     * If no modifier is pressed then return out
                     */

                    ctrl = event.isControlKeyDown();
                    alt = event.isAltKeyDown();
                    shift = event.isShiftKeyDown();
                    key = (char)event.getNativeKeyCode();
                    final Focusable target = shortcuts.get(new Shortcut(ctrl,
                                                                        alt,
                                                                        shift,
                                                                        Character.toUpperCase(key)));

                    if (target != null) {
                        if (target instanceof Button) {
                            if ( ((Button)target).isEnabled() && ! ((Button)target).isLocked()) {
                                ((Focusable)target).setFocus(true);
                                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                    public void execute() {
                                        NativeEvent clickEvent = Document.get()
                                                                         .createClickEvent(0,
                                                                                           -1,
                                                                                           -1,
                                                                                           -1,
                                                                                           -1,
                                                                                           false,
                                                                                           false,
                                                                                           false,
                                                                                           false);

                                        ClickEvent.fireNativeEvent(clickEvent, (Button)target);
                                    }
                                });

                                event.stopPropagation();
                            }
                            event.preventDefault();
                            event.stopPropagation();
                        } else if ( ((ScreenWidgetInt)target).isEnabled()) {
                            ((Focusable)target).setFocus(true);
                            event.preventDefault();
                            event.stopPropagation();
                        }
                    }
                }
            }
        },
                      KeyDownEvent.getType());

    }

    public void onFocus(FocusEvent event) {
        focused = (Focusable)event.getSource();
    }

    public void finishEditing() {
        if (focused != null && focused instanceof ScreenWidgetInt)
            ((ScreenWidgetInt)focused).finishEditing();
    }

    public boolean validate() {
        boolean valid = true;

        for (ScreenHandler<?> wid : handlers.values())
            valid = wid.isValid();

        return valid;
    }

    public void showErrors(ValidationErrorsList errors) {
        ArrayList<Exception> formErrors;
        FormErrorException formE;

        formErrors = new ArrayList<Exception>();
        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof FormErrorException) {
                formE = (FormErrorException)ex;
                formErrors.add(formE);
            } else {
                handlers.get( ((FieldErrorException)ex).getFieldName()).showError(ex);
            }
        }

        if (formErrors.size() == 0)
            setError("Please correct the errors indicated, then press Commit");
        else if (formErrors.size() == 1)
            setError(formErrors.get(0).getMessage());
        else {
            setError("(Error 1 of " + formErrors.size() + ") " + formErrors.get(0).getMessage());
            window.setMessagePopup(formErrors, "ErrorPanel");
        }
    }

    public void clearErrors() {
        for (ScreenHandler<?> wid : handlers.values())
            wid.clearError();

        window.clearStatus();
        window.clearMessagePopup("");
    }

    /**
     * This method will ask all widgets for any Query values that were entered
     * by the user, and will return an ArrayList of QueryData objects to send
     * back to the server to execute the query.
     * 
     * @return
     */
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        for (String key : handlers.keySet()) {
            Object query = handlers.get(key).getQuery();
            if (query instanceof ArrayList<?>) {
                ArrayList<QueryData> qds = (ArrayList<QueryData>)query;
                list.addAll(qds);
            } else if (query instanceof Object[]) {
                QueryData[] qds = (QueryData[])query;
                for (int i = 0; i < qds.length; i++ )
                    list.add(qds[i]);
            } else if (query != null) {
                ((QueryData)query).setKey(key);
                list.add((QueryData)query);
            }
        }

        return list;
    }

    public <T> void addScreenHandler(Widget widget, String meta, ScreenHandler<T> screenHandler) {
        assert widget != null : "addScreenHandler received a null widget";

        if (widget instanceof HasFocusHandlers)
            ((HasFocusHandlers)widget).addFocusHandler(this);

        screenHandler.widget = widget;

        if (widget instanceof HasValueChangeHandlers)
            ((HasValueChangeHandlers<T>)widget).addValueChangeHandler(screenHandler);

        addDataChangeHandler(screenHandler);
        addStateChangeHandler(screenHandler);

        handlers.put(meta, screenHandler);
        widgets.put(widget, screenHandler);
    }

    /**
     * Registers a DataChangeHandler to the Screen.
     */
    public HandlerRegistration addDataChangeHandler(DataChangeEvent.Handler handler) {
        return bus.addHandlerToSource(DataChangeEvent.getType(), this, handler);
    }

    /**
     * Registers a StateChangeHandler to the Screen.
     */
    public HandlerRegistration addStateChangeHandler(StateChangeEvent.Handler handler) {
        return bus.addHandlerToSource(StateChangeEvent.getType(), this, handler);
    }

    protected void fireDataChange() {
        bus.fireEventFromSource(new DataChangeEvent(), this);
    }

    public void addShortcut(Focusable widget, char key, ShortKeys... shorts) {
        List<ShortKeys> shortList = Arrays.asList(shorts);
        shortcuts.put(new Shortcut(shortList.contains(ShortKeys.CTRL),
                                   shortList.contains(ShortKeys.SHIFT),
                                   shortList.contains(ShortKeys.ALT),
                                   key),
                      widget);
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            bus.fireEventFromSource(new StateChangeEvent(state), this);
        }
    }

    protected boolean isState(State... states) {
        return states.length > 1 ? EnumSet.of(states[0],states).contains(state) : state == states[0];
    }

    public void setBusy() {
        setBusy("");
    }

    public void setBusy(String message) {

        busy++ ;

        lockWindow();

        window.setStatus(message, css.spinnerIcon());

    }

    public void removeBusy() {
        if (busy > 0)
            busy-- ;

        if (busy == 0)
            unlockWindow();
    }

    public void resetBusy() {
        busy = 0;
        unlockWindow();
    }

    public void unlockWindow() {
        if (glass != null) {
            glass.removeFromParent();
            glass = null;
        }
    }

    public void lockWindow() {
        if (glass == null) {
            glass = new AbsolutePanel();
            glass.setStyleName(css.GlassPanel());
            glass.setHeight(getOffsetHeight() + "px");
            glass.setWidth(getOffsetWidth() + "px");
            RootPanel.get().add(glass, getAbsoluteLeft(), getAbsoluteTop());
        }
    }

    public void clearStatus() {
        removeBusy();
        window.setStatus("", "");
    }

    public void setDone(String message) {
        removeBusy();
        window.setStatus(message, "");

    }

    public void setError(String message) {
        window.setStatus(message, css.ErrorPanel());
    }

    public void setWindow(WindowInt window) {
        this.window = window;
    }

    public void setEventBus(EventBus bus) {
        this.bus = bus;
    }

    public EventBus getEventBus() {
        return bus;
    }

}