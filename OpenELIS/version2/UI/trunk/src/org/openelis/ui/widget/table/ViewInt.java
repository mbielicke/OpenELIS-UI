package org.openelis.ui.widget.table;

import org.openelis.ui.resources.TableCSS;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class ViewInt extends ResizeComposite {
    
    abstract void addColumn(int index);
    
    abstract void removeColumn(int index);
    
    abstract void addRow(int index);
    
    abstract void removeRow(int index);
    
    abstract void removeAllRows();
    
    abstract void applySelectionStyle(int index);
    
    abstract void applyUnselectionStyle(int index);
    
    abstract void startEditing(int r, int c, Object val, NativeEvent event);
    
    abstract Object finishEditing(int r, int c);
    
    abstract boolean scrollToVisible(int index);
    
    abstract void scrollBy(int r);
    
    abstract void layout();
    
    abstract Header getHeader();
    
    abstract void resize();
    
    abstract void adjustScrollBarHeight();
    
    abstract void renderView(int start, int end);
    
    abstract void renderCell(int r, int c);
    
    abstract void renderExceptions(int start, int end);
    
    abstract int rowHeight();
    
    abstract FlexTable table();
    
    abstract ScrollPanel scrollView();
    
    abstract TableCSS css();
    
    abstract void setCSS(TableCSS css);
    
    abstract void createRow(int row);

}
