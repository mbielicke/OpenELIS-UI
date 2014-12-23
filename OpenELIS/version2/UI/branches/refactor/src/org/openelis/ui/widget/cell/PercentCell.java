package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.PercentBar;
import org.openelis.ui.widget.table.ColumnInt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class will display a PercentBar for the value passed into the the Table cell. 
 *
 */
public class PercentCell extends Cell {
	
	/**
	 * This is the widget used to show Percent in the cell
	 */
	private PercentBar editor;
	
	public PercentCell() {
		
	}
	/**
	 * Constructor that takes the editor as an argument
	 * @param editor
	 */
	public PercentCell(PercentBar editor) {
		this.editor = editor;
	}

	/**
	 * Returns the value of the percentage formatted as a string
	 */

	public String display(Object value) {
		return NumberFormat.getFormat("###0.0").format((Double)value)+"%";
	}

	/**
	 * Gets the HTML to display the Percentage from the editor and sets it into the cell
	 */
	
	public void render(HTMLTable table, int row, int col, Object value) {
		editor.setPercent((Double)value);
		table.setHTML(row,col,editor.getElement().getInnerHTML());
	}
	
	public SafeHtml bulkRender(Object value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	    
	    editor.setPercent((Double)value);
	    builder.appendHtmlConstant("<td>"+editor.getElement().getString()+"</td>");
	    
	    return builder.toSafeHtml();
	}

	/**
	 * Blanks out the table cell since this is a display only cell and cannot accept Query input
	 */
	
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
		table.setText(row,col,"");
	}

	
	public ArrayList<Exception> validate(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Widget w) {
		// TODO Auto-generated method stub
	}


	@Override
	public Widget asWidget() {
		return this;
	}
	
    
    public void setColumn(ColumnInt col) {
        // TODO Auto-generated method stub
        
    }
}
