package org.openelis.ui.widget.table;

public abstract class DataProvider<T,V> {
	
	public abstract V getValue(T data);
	
	public void setValue(T data, V value) {
		
	}
	
	@SuppressWarnings("unchecked")
	protected <W> void setCellValue(T data, W value) {
		setValue(data, (V)value);
	}
}
