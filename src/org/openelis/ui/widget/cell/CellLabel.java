package org.openelis.ui.widget.cell;

import org.openelis.ui.widget.Label;

public class CellLabel<V> extends Cell<V> {
	
	public CellLabel() {
		super();
		editor = new Label<V>();
	}

}
