package org.openelis.ui.widget.celltable;


public class CellData<T,V> implements FieldGetter<T,V>, FieldUpdater<T,V> {

	@Override
	public V getValue(int col, T row) {
		return null;
	}
	
	@Override
	public void update(int col, T row, V value) {
		
	}
}
