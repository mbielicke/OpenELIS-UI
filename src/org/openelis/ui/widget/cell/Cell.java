package org.openelis.ui.widget.cell;

import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public abstract class Cell<T,V> extends Widget implements CellRenderer<V>, HasWidgets {
	
	Element proxyElement;
	
	public void render(T data) {
		
	}
	
	public Element getRenderElement() {
		if(proxyElement != null)
			return proxyElement;
		else
			return super.getElement();
	}
	
	public void setProxyElement(Element element) {
		this.proxyElement = element;
	}
	
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
	
}
