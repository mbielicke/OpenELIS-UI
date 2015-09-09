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
package org.openelis.ui.widget.table;

import org.openelis.ui.common.DataBaseUtil;

/**
 * @author tschmidt
 *
 */
public class CheckLabelValue {

    protected String checked, label;
    
    public CheckLabelValue(String checked, String label) {
        this.checked = checked;
        this.label = label;
    }
    
    public String getChecked() {
        return checked;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setChecked(String checked) {
        this.checked = checked;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean equals(Object value) {
        CheckLabelValue comp;
        if(value instanceof CheckLabelValue) {
            comp = ((CheckLabelValue)value);
            return DataBaseUtil.isSame(checked,comp.checked) && 
                   DataBaseUtil.isSame(label, comp.label);
        }
        return false; 
    }
    
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (checked == null ? 0 : checked.hashCode());
        hash = 17 * hash + (label == null ? 0 : label.hashCode());
        return hash;
    }
}
