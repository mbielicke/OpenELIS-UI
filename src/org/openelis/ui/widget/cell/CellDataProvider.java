package org.openelis.ui.widget.cell;

public abstract class CellDataProvider<T,V> {
	
	public abstract V getValue(T data);

}
