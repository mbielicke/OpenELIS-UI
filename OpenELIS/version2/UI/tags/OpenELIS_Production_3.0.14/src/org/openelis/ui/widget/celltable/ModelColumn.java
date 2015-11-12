package org.openelis.ui.widget.celltable;

import com.google.gwt.cell.client.FieldUpdater;

public abstract class ModelColumn<R,C> {
	
	protected FieldUpdater<R,C> fieldUpdater;
	
	public abstract C getValue(R row);
	
	public FieldUpdater<R,C> getFieldUpdater() {
		return fieldUpdater;
	}
	
	public void setFieldUpdater(FieldUpdater<R,C> fieldUpdater) {
		this.fieldUpdater = fieldUpdater;
	}

}
