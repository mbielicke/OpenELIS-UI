package org.openelis.ui.widget.table;

import org.openelis.ui.common.data.QueryData;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TableView extends View<Table> {
    
	public TableView(Table controller) {
		super(controller);
	}

	protected void bulkRenderCell(SafeHtmlBuilder builder, int row, int col) {
    	CellRenderer renderer;
    	
    	renderer = controller.getColumnAt(col).getCellRenderer();
    	//builder.appendHtmlConstant("<td>");
        builder.append(renderer.bulkRender(controller.getValueAt(row,col)));
        //builder.appendHtmlConstant("</td>");
    }
	
    protected void renderCell(int r, int c) {
        CellRenderer renderer;

        renderer = controller.getColumnAt(c).getCellRenderer();

        if (controller.getQueryMode())
            renderer.renderQuery(grid, r, c, (QueryData)controller.getValueAt(r, c));
        else
            renderer.render(grid,r, c, controller.getValueAt(r, c));

        if (controller.hasExceptions(r, c)) {
        	renderCellException(r,c);
        } else {
        	clearCellException(r,c);
        }
    }

}
