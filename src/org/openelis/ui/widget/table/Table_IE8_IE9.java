package org.openelis.ui.widget.table;

import java.util.ArrayList;

public class Table_IE8_IE9 extends Table {
	
	@Override
    @SuppressWarnings("unchecked")
    public void setModel(ArrayList<? extends Row> model) {
        finishEditing();
        unselectAll();
        
        this.model = (ArrayList<Row>)model;
        modelView = this.model;
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
