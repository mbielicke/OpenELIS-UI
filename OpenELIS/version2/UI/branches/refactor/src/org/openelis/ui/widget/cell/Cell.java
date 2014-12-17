package org.openelis.ui.widget.cell;

import java.util.Iterator;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public abstract class Cell implements CellRenderer, IsWidget, HasWidgets.ForIsWidget {
	
	@Override
	public void add(Widget w) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean remove(IsWidget w) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
