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

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.cell.CellTextbox;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLTable;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table using a TextBox<T>
 * 
 * @author tschmidt
 * 
 * @param <T>
 */
@Deprecated
public class TextBoxCell<T> extends CellTextbox<T> implements CellRenderer, CellEditor {

	@Override
	public void startEditing(Object value, Container container,	NativeEvent event) {
		startEditing(container.getElement(),(T)value,event);
	}

	@Override
	public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {
		startEditing(container.getElement(),qd,event);
	}

	@Override
	public boolean ignoreKey(int keyCode) {
		return false;
	}

	@Override
	public String display(Object value) {
		return asString((T)value);
	}

	@Override
	public SafeHtml bulkRender(Object value) {
		return asHtml((T)value);
	}

	@Override
	public void render(HTMLTable table, int row, int col, Object value) {
		render(table.getCellFormatter().getElement(row, col),(T)value);
	}

	@Override
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
		render(table.getCellFormatter().getElement(row, col), qd);
	}

	@Override
	public void setColumn(ColumnInt col) {
		// TODO Auto-generated method stub
		
	}

}
