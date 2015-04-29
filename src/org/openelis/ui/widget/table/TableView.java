package org.openelis.ui.widget.table;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.cell.Cell;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TableView<T> extends View<Controller> {
    
	public TableView(Controller controller) {
		super(controller);
	}

	protected void bulkRenderCell(SafeHtmlBuilder builder, int row, int col) {
    	org.openelis.ui.widget.cell.CellRenderer<?> renderer;
    	
    	renderer = controller.getColumnAt(col).getCellRenderer();
    	SafeHtml html = renderer.asHtml(controller.getValueAt(row, col)); 

   		builder.appendHtmlConstant("<td>");
        builder.append(html);
       	builder.appendHtmlConstant("</td>");
    }
	
    protected void renderCell(int r, int c) {
        Cell<?> renderer;

        renderer = controller.getColumnAt(c).getCellRenderer();

        if (controller.getQueryMode()) {
            renderer.render(grid.getCellFormatter().getElement(r, c),(QueryData)controller.getValueAt(r, c));
        } else
            renderer.render(grid.getCellFormatter().getElement(r, c), controller.getValueAt(r, c));

        if (controller.hasExceptions(r, c)) {
        	renderCellException(r,c);
        } else {
        	clearCellException(r,c);
        }
    }

}
