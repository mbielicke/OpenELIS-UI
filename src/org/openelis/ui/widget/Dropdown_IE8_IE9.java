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
package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.common.Util;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

/**
 * Implementation of Dropdown specifically created for IE8 and IE9 to be 
 * used in the web portal.
 */
public class Dropdown_IE8_IE9<T> extends Dropdown<T> {
	
	boolean shownOnce = false;
	int index;
	
	@Override
	protected void showPopup() {
		super.showPopup();
		if (!shownOnce) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					table.setModel(model);
					shownOnce = true;
				}
			});
		}
	}

	@Override
    public void setModel(ArrayList<Item<T>> model) {
		if(shownOnce) {
			super.setModel(model);
			return;
		}
		this.model = model;
		createKeyHash(model);
		searchText = null;
		setValue(null);
		setDisplay();
    }
	
    @Override
    public void setValue(T value, boolean fireEvents) {
    	if (shownOnce) {
    		super.setValue(value, fireEvents);
    		return;
    	}

    	if ( !Util.isDifferent(this.value == null ? null : this.value, value))
    		return;

    	index = -1;
    	if(keyHash.containsKey(value))
    		index = keyHash.get(value);

    	searchString = "";

    	setDisplay();

    	this.value = value;

    	if (fireEvents)
    		ValueChangeEvent.fire(this, value);
    };
    
    protected void setDisplay() {
    	if(shownOnce) {
    		super.setDisplay();
    		return;
    	}
    	textbox.setText(renderer.getDisplay(model.get(index)));
    }

}
