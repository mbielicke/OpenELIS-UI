package org.openelis.ui.widget.cell;

public interface CellTipProvider<T> {
    
    public T getTip(int row, int col);
}
