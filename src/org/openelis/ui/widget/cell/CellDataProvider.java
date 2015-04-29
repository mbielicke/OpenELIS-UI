package org.openelis.ui.widget.cell;


public abstract class CellDataProvider<T,V> {
	
	Cell<V> cell;
	
	public CellDataProvider(Cell<V> cell) {
		this.cell = cell;
	}
	
	public abstract V getValue(T data);
	
	public abstract void setValue(T data, V value);
	
	protected void render(T data) {
		cell.render(getValue(data));
	}

}
