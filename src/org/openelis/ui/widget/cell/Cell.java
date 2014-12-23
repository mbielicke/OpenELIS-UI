package org.openelis.ui.widget.cell;

import java.util.Iterator;

import org.openelis.ui.widget.HasHelper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class Cell<V> extends Widget implements CellRenderer<V>, HasWidgets {
	
	Element proxyElement;
	HasValue<V> editor;
	
	public Cell() {
		setElement(Document.get().createDivElement());
	}
	
	public Element getRenderElement() {
		if(proxyElement != null)
			return proxyElement;
		else
			return super.getElement();
	}
	
	public void render(V value) {
		getElement().setInnerText(value.toString());
	}
	
	public void render(Element element, V value) {
		element.setInnerSafeHtml(getHTML(value));
	}
	
	public SafeHtml getHTML(V value) {
		editor.setValue(value);
	    String text = ((HasHelper<V>)editor).getHelper().format(value);
		return new SafeHtmlBuilder().appendEscaped(text).toSafeHtml();
	}
	
 	public void setProxyElement(Element element) {
		this.proxyElement = element;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof HasValue)
			editor = (HasValue<V>)w;
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
