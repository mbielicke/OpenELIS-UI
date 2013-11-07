package org.openelis.ui.widget.celltable;

public interface FieldGetter<R,V> {
	
	public V getValue(int col, R row);
	
}
