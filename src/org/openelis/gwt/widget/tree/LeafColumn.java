package org.openelis.gwt.widget.tree;

import java.util.Iterator;

import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.table.CellEditor;
import org.openelis.gwt.widget.table.CellRenderer;
import org.openelis.gwt.widget.table.ColumnInt;
import org.openelis.gwt.widget.table.LabelCell;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class LeafColumn implements ColumnInt,IsWidget, HasWidgets.ForIsWidget {
    
    protected Tree tree;
    
    /**
     * Editor widget used for this column
     */
    protected CellEditor   editor;
    
    protected boolean      required;

    /**
     * Render widget used for this column
     */
    protected CellRenderer renderer = new LabelCell(new Label<String>());
    
    /**
     * Returns the Editor currently being used by this Column
     */
    public CellEditor getCellEditor() {
        return editor;
    }

    /**
     * Sets the Editor widget to be used by this Column. This method will also
     * set Cell Renderer if the passed editor also implements the CellRenderer
     * interface. If you need a different Cell Renderer make sure to call
     * setCellEditor first before calling setCellRenderer.
     * 
     * @param editor
     */
    public void setCellEditor(CellEditor editor) {
        this.editor = editor;
        this.editor.setColumn(this);
    }
    
    /**
     * Method will return the currently set Renderer for this column
     * @return
     */
    public CellRenderer getCellRenderer() {
        return renderer;
    }

    /**
     * Method will set the current renderer for this column
     * @param renderer
     */
    public void setCellRenderer(CellRenderer renderer) {
        this.renderer = renderer;
        if (renderer instanceof CellEditor) {
            editor = (CellEditor)renderer;
            editor.setColumn(this);
        }
    }

    /**
     * Returns the Table that this Column is used in.
     * 
     * @return
     */
    public Tree getTree() {
        return tree;
    }
    
    public void setRquierd(boolean required) {
        this.required = required;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    /**
     * Sets the Table that this Column is used in.
     * 
     * @param tree
     */
    public void setTree(Tree tree) {
        this.tree = tree;
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
    public void finishEditing() {
        if(editor != null)
            editor.finishEditing();
    }
    
    public boolean hasEditor() {
        return editor != null;
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
