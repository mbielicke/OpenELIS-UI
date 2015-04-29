package org.openelis.ui.widget.table;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.cell.CellTime;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLTable;

@Deprecated
public class TimeCell extends CellTime implements CellRenderer, CellEditor {

	@Override
	public void startEditing(Object value, Container container,	NativeEvent event) {
		startEditing(container.getElement(),(Double)value,event);
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
		return asString((Double)value);
	}

	@Override
	public SafeHtml bulkRender(Object value) {
		return asHtml((Double)value);
	}

	@Override
	public void render(HTMLTable table, int row, int col, Object value) {
		render(table.getCellFormatter().getElement(row,col), (Double)value);
	}

	@Override
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
		render(table.getCellFormatter().getElement(row,col),qd);
	}

	@Override
	public void setColumn(ColumnInt col) {
		// TODO Auto-generated method stub
		
	}

}
