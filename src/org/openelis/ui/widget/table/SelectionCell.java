package org.openelis.ui.widget.table;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.cell.CellSelection;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLTable;

@Deprecated
public class SelectionCell extends CellSelection implements CellRenderer{

	@Override
	public String display(Object value) {
		return asString((String)value);
	}

	@Override
	public SafeHtml bulkRender(Object value) {
		return asHtml((String)value);
	}

	@Override
	public void render(HTMLTable table, int row, int col, Object value) {
		render(table.getCellFormatter().getElement(row,col),(String)value);
	}

	@Override
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
		render(table.getCellFormatter().getElement(row,col),qd);
	}

	@Override
	public ArrayList<Exception> validate(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setColumn(ColumnInt col) {
		// TODO Auto-generated method stub
		
	}

}
