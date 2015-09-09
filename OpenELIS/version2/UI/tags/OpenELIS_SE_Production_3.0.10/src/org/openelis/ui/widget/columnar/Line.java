package org.openelis.ui.widget.columnar;

import java.util.Iterator;

import org.openelis.ui.widget.table.CellRenderer;

import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class Line implements IsWidget, HasWidgets.ForIsWidget {
    
    protected CellRenderer      renderer;
    
    protected String            label;
    
    protected Columnar          columnar;
    
    public Line() {
        
    }
    
    /**
     * Method will set the current renderer for this column
     * @param renderer
     */
    @UiChild(limit=1,tagname="renderer")
    public void setCellRenderer(CellRenderer renderer) {
        this.renderer = renderer;
    }
    
    public CellRenderer getCellRenderer() {
        return renderer;
    }

    public void setColumnar(Columnar clmnr) {
        this.columnar = clmnr;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getLable() {
        return label;
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
        assert w instanceof CellRenderer;

        setCellRenderer((CellRenderer)w);
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
