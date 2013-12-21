package org.openelis.ui.widget.table;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Link;

import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class LinkCell implements CellRenderer, IsWidget {
    
    
    public LinkCell() {
    }

    @Override
    public String display(Object value) {
        assert value instanceof Link.Details;
        
        return ((Link.Details)value).text;
    }

    @Override
    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        assert value instanceof Link.Details;
        
        table.setWidget(row,col,new Link((Link.Details)value));
    }

    @Override
    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ArrayList<Exception> validate(Object value) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public void setColumn(ColumnInt col) {
        // TODO Auto-generated method stub
        
    }
    

}
