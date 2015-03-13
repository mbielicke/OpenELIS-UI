package org.openelis.ui.widget.cell;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class Cell<V> extends SimplePanel implements CellRenderer<V> {

	Element proxyElement;
	
	public Cell() {
		
	}
	
	protected Element getRenderElement() {
		if(proxyElement != null)
			return proxyElement;
		else
			return super.getElement();
	}
	
	public void render(V value) {
		render(getRenderElement(),value);
	}
	
	public void render(Element element, V value) {
		element.removeAllChildren();
		element.setInnerSafeHtml(asHtml(value));
	}
	
 	public void setProxyElement(Element element) {
		this.proxyElement = element;
	}
 
	
	public void setRenderInParent(boolean renderInParent) {
		if(renderInParent) {
			if(isOrWasAttached())
				takeOverParentElement();
			else
				addAttachHandler(new AttachEvent.Handler() {
					@Override
					public void onAttachOrDetach(AttachEvent event) {
						if(event.isAttached()) {
							takeOverParentElement();
					}
				}
			});
		}
	}
	
	private void takeOverParentElement() {
		setProxyElement(getElement().getParentElement());
		proxyElement.setInnerHTML(getElement().getInnerHTML());
		getElement().removeFromParent();
	}
}
