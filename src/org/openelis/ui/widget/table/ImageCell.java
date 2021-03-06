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
package org.openelis.ui.widget.table;

import java.util.ArrayList;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table that is text only
 * 
 * @author tschmidt
 * 
 */
public class ImageCell implements CellRenderer,IsWidget {
    
    
	
	protected ColumnInt column;
	
    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public ImageCell() {
    }
    
    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        table.getCellFormatter().setStyleName(row,col,(String)value);
    }
    
    public String display(Object value) {
        return value.toString();
    }
    
    public SafeHtml bulkRender(Object value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendHtmlConstant("<span class='"+DataBaseUtil.toString(value)+"'/>");
        
        return builder.toSafeHtml();
                        
    }

    public void renderQuery(HTMLTable table,
                            int frow,
                            int col,
                            QueryData qd) {
        
    }

	@Override
	public ArrayList<Exception> validate(Object value) {
		return null;
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void setColumn(ColumnInt col) {
        this.column = col;
    }

    
}
