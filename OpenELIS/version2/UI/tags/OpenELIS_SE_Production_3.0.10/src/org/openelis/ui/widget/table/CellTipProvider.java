package org.openelis.ui.widget.table;

public interface CellTipProvider<T> {
    
    public T getTip(int row, int col);
}
