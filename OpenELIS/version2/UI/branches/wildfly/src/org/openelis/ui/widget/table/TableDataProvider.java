package org.openelis.ui.widget.table;

import java.util.ArrayList;

public class TableDataProvider<T> {

	ArrayList<DataProvider<T,?>> providers = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public <V> V getValue(int col, T data) {
		return (V)providers.get(col).getValue(data);
	}

	public <V> void setValue(int col, T data, V value) {
		providers.get(col).setCellValue(data, value);
	}

	public void addColumnProvider(DataProvider<T,?> provider) {
		addColumnProvider(provider,providers.size());
	}
	
	public void addColumnProvider(DataProvider<T,?> provider,int index) {		
		providers.add(index,provider);
	}

}
