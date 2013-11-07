package org.openelis.ui.widget.tree;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class Leaf implements IsWidget, HasWidgets.ForIsWidget {
    
    ArrayList<LeafColumn> columns = new ArrayList<LeafColumn>();
    String key;

    
    public ArrayList<LeafColumn> getColumns() {
        return columns;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    
    
    @Override
    public void add(Widget w) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Iterator<Widget> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Widget w) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void add(IsWidget w) {
        assert w instanceof LeafColumn;
        
        columns.add((LeafColumn)w);
    }

    @Override
    public boolean remove(IsWidget w) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Widget asWidget() {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
