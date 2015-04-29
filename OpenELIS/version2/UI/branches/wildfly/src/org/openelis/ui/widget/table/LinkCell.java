package org.openelis.ui.widget.table;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Link;
import org.openelis.ui.widget.cell.CellLink;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLTable;

@Deprecated
public class LinkCell extends CellLink implements CellRenderer {

	@Override
	public String display(Object value) {
		return asString((Link.Details)value);
	}

	@Override
	public SafeHtml bulkRender(Object value) {
		return asHtml((Link.Details)value);
	}

	@Override
	public void render(HTMLTable table, int row, int col, Object value) {
		render(table.getCellFormatter().getElement(row, col),(Link.Details)value);
	}

	@Override
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
	
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
