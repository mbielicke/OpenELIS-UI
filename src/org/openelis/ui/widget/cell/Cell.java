package org.openelis.ui.widget.cell;

import org.openelis.ui.common.data.QueryData;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class Cell<V> extends SimplePanel implements CellRenderer<V> {

	Element proxyElement;
	
	public Cell() {
		addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if(event.isAttached()) {
					takeOverParentElement();
				}
			}
		});
	}
	
	protected Element getRenderElement() {
		if(proxyElement != null)
			return proxyElement;
		else
			return getElement();
	}
	
	public void render(V value) {
		render(getRenderElement(),value);
	}
	
	public void render(QueryData qd) {
		render(getRenderElement(),qd);
	}
	
	public void render(Element element, V value) {
		element.removeAllChildren();
		element.setInnerSafeHtml(asHtml(value));
	}
	
	public void render(Element element, QueryData qd) {
		element.removeAllChildren();
		element.setInnerSafeHtml(asHtml(qd));
	}
	
 	public void setProxyElement(Element element) {
		this.proxyElement = element;
	}
	
	private void takeOverParentElement() {
		setProxyElement(getElement().getParentElement());
		proxyElement.setInnerHTML(getElement().getInnerHTML());
		getElement().removeFromParent();
	}
	
	public SafeHtml asHtml(QueryData qd) {
		return null;
	}

	public String asString(QueryData qd) {
		return null;
	}
}
