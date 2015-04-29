package org.openelis.ui.widget.table;

import java.util.ArrayList;

import org.openelis.ui.widget.cell.CellDataProvider;

public class TableDataProvider<T> {

	ArrayList<CellDataProvider> providers = new ArrayList<>();
	
	public <V> V getValue(int col, T data) {
		return (V)providers.get(col).getValue(data);
	}

	public <V> void setValue(int col, T data, V value) {
		providers.get(col).setValue(data, value);
	}

	public <V> void addColumnProvider(CellDataProvider<T,V> provider) {
		addColumnProvider(provider,providers.size());
	}
	
	public <V> void addColumnProvider(CellDataProvider<T,V> provider,int index) {		
		providers.add(index,provider);
	}

}
