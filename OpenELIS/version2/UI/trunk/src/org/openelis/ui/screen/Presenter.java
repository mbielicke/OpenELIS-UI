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

import java.util.EnumSet;

import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.resources.WindowCSS;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * This class is used to bring together widgets into a logical unit of work that
 * is presented to the user.
 * 
 */
public class Presenter<T> {

    protected int                               busy;

    protected EventBus                          bus;

    public enum ShortKeys {
        CTRL, SHIFT, ALT
    };

    protected WindowCSS css;
    protected WindowInt window;
    protected State     state;
    
    protected ModulePermission permissions;

    public Presenter() {
        css = UIResources.INSTANCE.window();
        css.ensureInjected();

        bus = new SimpleEventBus();
    }

    
    /**
     * Registers a DataChangeHandler to the Screen.
     */
    public HandlerRegistration addDataChangeHandler(DataChangeEvent.Handler<T> handler) {
        return bus.addHandlerToSource(DataChangeEvent.getType(), this, handler);
    }

    /**
     * Registers a StateChangeHandler to the Screen.
     */
    public HandlerRegistration addStateChangeHandler(StateChangeEvent.Handler handler) {
        return bus.addHandlerToSource(StateChangeEvent.getType(), this, handler);
    }

    public void fireDataChange(T data) {
        bus.fireEventFromSource(new DataChangeEvent<T>(data), this);
    }


    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            bus.fireEventFromSource(new StateChangeEvent(state), this);
        }
    }

    public boolean isState(State... states) {
        return states.length > 1 ? EnumSet.of(states[0], states).contains(state)
                                : state == states[0];
    }

    public void setBusy() {
        setBusy("");
    }

    public void setBusy(String message) {

        busy++ ;
        window.setStatus(message, css.spinnerIcon());

    }

    public void removeBusy() {
        if (busy > 0)
            busy-- ;
    }

    public void resetBusy() {
        busy = 0;
    }

    public boolean isBusy() {
        return busy > 0;
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
        removeBusy();
        window.setStatus(message, css.ErrorPanel());
    }

    public void setWindow(WindowInt window) {
        this.window = window;
    }

    public WindowInt getWindow() {
        return window;
    }

    public void setEventBus(EventBus bus) {
        this.bus = bus;
    }

    public EventBus getEventBus() {
        return bus;
    }
	
	public ModulePermission permissions() {
		return permissions;
	}
	
	public void setModulePermissions(ModulePermission permissions) {
		this.permissions = permissions;
	}

}
