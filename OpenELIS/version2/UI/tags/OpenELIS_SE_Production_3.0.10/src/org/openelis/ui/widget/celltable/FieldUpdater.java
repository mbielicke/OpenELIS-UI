package org.openelis.ui.widget.celltable;

public interface FieldUpdater<R,V> {
	
	public void update(int col, R row, V value);

}
