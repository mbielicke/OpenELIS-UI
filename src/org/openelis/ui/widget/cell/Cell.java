package org.openelis.ui.widget.cell;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class Cell<V> extends SimplePanel implements CellRenderer<V> {
	
	public Cell() {
	}

	Element proxyElement;
	
	
	protected Element getRenderElement() {
		if(proxyElement != null)
			return proxyElement;
		else
			return super.getElement();
	}
	
	public void render(V value) {
		render(getElement(),value);
	}
	
	public void render(Element element, V value) {
		element.removeAllChildren();
		element.setInnerSafeHtml(asHtml(value));
	}
	
 	public void setProxyElement(Element element) {
		this.proxyElement = element;
	}
 		
 	public abstract SafeHtml asHtml(V value);
}
