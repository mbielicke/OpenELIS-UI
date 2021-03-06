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
package org.openelis.ui.widget;

import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;

/**
 * This class is used to draw a FilterMenItem to be used in the Popup menu of a table
 * header cell to apply filters to columns of a table. 
 *
 */
public class CheckMenuItem extends MenuItem implements HasValueChangeHandlers<Boolean> {
    
    /**
     * Check box to apply/remove filter  
     */
    protected boolean checked;
   
    protected static CheckboxCSS css  = UIResources.INSTANCE.checkbox(); 
    {
    	css.ensureInjected();
    }
    
    
    public CheckMenuItem() {
        super();
        init();
    }
    
    /**
     * Constructor that accepts a String for the display of the item
     * @param text
     */
    public CheckMenuItem(String display, String description, boolean autoClose) {
        super();
        
        setDisplay(display);
        setAutoClose(autoClose);
        init();
    }
        
    public void init() {
        final CheckMenuItem source = this;
        
        setIcon(css.Unchecked());
        
        /*
         * Setting this click handler lets the user click anywhere in the menu
         * to toggle the checkbox
         */
        addCommand(new Command() {
            public void execute() {
                setCheck(!checked);
                ValueChangeEvent.fire(source, checked);           
            }
        });            
    }
    
    /**
     * Method used to set the initial state of the menu to Checked if filters are in place.
     */
    public void setCheck(boolean checked) {
        this.checked = checked;
        if(checked){
            setIcon(css.Checked());
        }else{
            setIcon(css.Unchecked());
        }
    }
    
    public boolean isChecked() {
    	return checked;
    }

    /**
     * Method to register ValueChangeHandler
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
