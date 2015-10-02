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
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.ScreenWidgetInt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to bring together widgets into a logical unit of work that
 * is presented to the user.
 * 
 */
public abstract class View<T> extends ResizeComposite implements FocusHandler, Focusable, StateChangeEvent.Handler, DataChangeEvent.Handler<T>, ScreenWidgetInt {

    protected Focusable                         focused;
    protected HashMap<Shortcut, Focusable>      shortcuts;

    protected AbsolutePanel                     glass;

    public enum ShortKeys {
        CTRL, SHIFT, ALT
    };

    public View() {
        shortcuts = new HashMap<Shortcut, Focusable>();
        addDomHandler(new ViewKeyHandler(), KeyDownEvent.getType());
    }

    protected void clickButton(final Button button) {
        if (button.isEnabled() && !button.isLocked()) {
            button.setFocus(true);
            ClickEvent.fireNativeEvent(Document.get().createClickEvent(0,
                                                                       -1,
                                                                       -1,
                                                                       -1,
                                                                       -1,
                                                                       false,
                                                                       false,
                                                                       false,
                                                                       false),
                                       button);

        }
    }

    protected void focusNextWidget(Focusable focused, boolean forward) {
        assert focused != null;
        
        if(focused == null) 
            return;

        Focusable nextWidget = focused;

        do {
            nextWidget = getNextWidget(nextWidget,forward);
        } while (nextWidget != null && nextWidget != focused && ! ((ScreenWidgetInt)nextWidget).isEnabled());
                 

      	if (nextWidget != null)
       		nextWidget.setFocus(true);

    }

    protected Focusable getNextWidget(Focusable widget, boolean forward) {
    	return null;
    }
    
    public void onFocus(FocusEvent event) {
        focused = (Focusable)event.getSource();
        
        //TODO Remimplement this with EventBus event to browser
        //Focus window if not the focused window the browser
        /*if(window != null && window.asWidget() != null && window.asWidget().getStyleName().contains(css.unfocused())) {
         	FocusEvent.fireNativeEvent(event.getNativeEvent(),window);
         	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					focused.setFocus(true);	
				}
			});
        }
        */
    }

    public void finishEditing() {
        if (focused != null && focused instanceof ScreenWidgetInt)
            ((ScreenWidgetInt)focused).finishEditing();
    }

    public void validate(Validation validation) {
    	
    }

    
	protected void isValid(Widget widget, Validation validation) {
	    HasExceptions he;
	    
        if(widget instanceof HasExceptions) {
	        he = (HasExceptions)widget;
	        if(he.hasExceptions()) {
	            if(Balloon.isWarning(he))
	                validation.setStatus(Validation.Status.WARNINGS);
	            else
	                validation.setStatus(Validation.Status.ERRORS);
	        }
	    }
	}
	
    public void showErrors(ValidationErrorsList errors) {
        ArrayList<Exception> formErrors;
        FormErrorException formE;
        ValidationErrorsList tabErrors;
        
        if(errors == null || errors.getErrorList().isEmpty())
            return;

        formErrors = new ArrayList<Exception>();
        tabErrors = new ValidationErrorsList();
        for (Exception ex : errors.getErrorList()) {
            if (ex instanceof FormErrorException) {
                formE = (FormErrorException)ex;
                formErrors.add(formE);
            } else if (ex instanceof FieldErrorException) {
                String field = ((FieldErrorException)ex).getFieldName();
                //if (handlers.containsKey(field))
                 //   handlers.get(field).showError(ex);
                //else
                  //  tabErrors.add(ex);
            } else
                Window.alert(ex.getMessage());
        }

    }

    public void clearErrors() {
    	
    }
    
    public void setState(State state) {
    	
    }
    
    public void setData(T data) {
    	
    }

    /**
     * This method will ask all widgets for any Query values that were entered
     * by the user, and will return an ArrayList of QueryData objects to send
     * back to the server to execute the query.
     * 
     * @return
     */
    public void getQueryFields(ArrayList<QueryData> queries){
    	
    }
    
    
    
    protected void getQuery(ArrayList<QueryData> list, Object query, String key) {
    	if (query instanceof ArrayList<?>) {
    		ArrayList<QueryData> qds = (ArrayList<QueryData>)query;
    		list.addAll(qds);
    	} else if (query instanceof Object[]) {
    		QueryData[] qds = (QueryData[])query;
    		for (int i = 0; i < qds.length; i++ )
    			list.add(qds[i]);
    	}else if (query != null) {
    		if(((QueryData)query).getKey() == null)
    			((QueryData)query).setKey(key);
    		list.add((QueryData)query);
    	}
    }


    public void addShortcut(Focusable widget, char key, ShortKeys... shorts) {
        List<ShortKeys> shortList = Arrays.asList(shorts);
        shortcuts.put(new Shortcut(shortList.contains(ShortKeys.CTRL),
                                   shortList.contains(ShortKeys.SHIFT),
                                   shortList.contains(ShortKeys.ALT),
                                   key),
                      widget);
    }

    public EnumSet<State> isState(State... states) {
        return EnumSet.of(states[0], states);
    }
    
    public static class Validation {

        public enum Status {
            VALID(0), WARNINGS(1), FLAGGED(2), ERRORS(3);

            int value;

            private Status(int value) {
                this.value = value;
            }
        };

        public Validation() {
            status = Status.VALID;
        }

        private Status               status;

        private ArrayList<Exception> exceptions;

        public void setStatus(Status status) {
            if (status.value > this.status.value)
                this.status = status;
        }

        public Status getStatus() {
            return status;
        }

        public void addException(Exception exception) {
            if (exceptions == null)
                exceptions = new ArrayList<Exception>();

            exceptions.add(exception);
        }

        public ArrayList<Exception> getExceptions() {
            return exceptions;
        }
    }

    protected class ViewKeyHandler implements KeyDownHandler {

        @Override
        public void onKeyDown(KeyDownEvent event) {
            if (isTabEvent(event)) {
                handleTabEvent(event);
            } else if (isShortcutEvent(event)) {
                handleShortcutEvent(event);
            }
        }

        protected boolean isTabEvent(KeyDownEvent event) {
            return event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB;
        }

        protected boolean isShortcutEvent(KeyDownEvent event) {
            return event.isAnyModifierKeyDown();
        }

        protected void handleTabEvent(KeyDownEvent event) {
            if (focused == null)
                return;

            event.preventDefault();
            event.stopPropagation();

            focusNextWidget(focused, !event.isShiftKeyDown());
        }

        protected void handleShortcutEvent(KeyDownEvent event) {
            Focusable target;

            target = findShortcutTarget(event);

            if (target == null)
                return;

            event.preventDefault();
            event.stopPropagation();
            
            if (target instanceof ShortcutHandler) {
                ((ShortcutHandler)target).onShortcut();
            }else if (target instanceof Button) {
                clickButton((Button)target); 
            } else if ( ((ScreenWidgetInt)target).isEnabled()) {
                ((Focusable)target).setFocus(true);
            } 
        }

        protected Focusable findShortcutTarget(KeyDownEvent event) {
            return shortcuts.get(new Shortcut(event.isControlKeyDown(),
                                              event.isAltKeyDown(),
                                              event.isShiftKeyDown(),
                                              Character.toUpperCase((char)event.getNativeKeyCode())));
        }
    }

	@Override
	public int getTabIndex() {
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
		
	}

	@Override
	public void setFocus(boolean focused) {
		getElement().focus();
	}

	@Override
	public void setTabIndex(int index) {
		getElement().setTabIndex(index);
		
	}

	@Override
	public void setEnabled(boolean enabled) {
		
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * This is overriden in order to set tabindex so that
	 * setFocus() method will work
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		setTabIndex(0);
	}
	
	public abstract void setPresenter(Presenter<T> presenter);

	
	public void onDataChange(DataChangeEvent<T> event) {
		setData(event.getData());
		
	}

	@Override
	public void onStateChange(StateChangeEvent event) {	
		setState(event.getState());
	}
	

}
