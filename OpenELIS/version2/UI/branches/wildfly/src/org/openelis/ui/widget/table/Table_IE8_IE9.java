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

import java.util.ArrayList;

/**
 *  Version of Table to be used for IE8 and IE9 Builds since those
 *  browsers can not support the new bulk rendering 
 *
 */
public class Table_IE8_IE9<T> extends Table<T> {
	
	@Override
    @SuppressWarnings("unchecked")
    public void setModel(ArrayList<T> model) {
        finishEditing();
        unselectAll();
        
        this.model = model;
        modelView = null;
		rowIndex = null;
        
        checkExceptions();

        // Clear any filter choices that may have been in force before model
        // changed
        for (Column col : columns) {
            if (col.getFilter() != null)
                col.getFilter().unselectAll();
        }
        
        renderView(-1,-1);
    }

}
