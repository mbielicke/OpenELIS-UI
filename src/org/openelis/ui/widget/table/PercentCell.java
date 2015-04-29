package org.openelis.ui.widget.table;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.PercentBar;
import org.openelis.ui.widget.cell.CellPercent;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class will display a PercentBar for the value passed into the the Table cell. 
 *
 */
public class PercentCell extends CellPercent implements CellRenderer {

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
		render(table.getElement(), (Double)value);
	}

	@Override
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
		render(table.getElement(),qd);
	}

	@Override
	public ArrayList<Exception> validate(Object value) {
		return validate((Double)value);
	}

	@Override
	public void setColumn(ColumnInt col) {
		
	}

}
