package org.openelis.ui.widget.cell;

import org.openelis.ui.widget.CSSUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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
 	
	protected void setEditor(Widget editor) {
		getElement().removeAllChildren();
		setWidget(editor);
	}
	
	protected void sizeEditor(final Widget editor, final Element element) {
		editor.setVisible(false);
		final double width = element.getClientWidth() - CSSUtils.getAddedPaddingWidth(element);;
		final double height = element.getClientHeight() - CSSUtils.getAddedPaddingHeight(element); 
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				editor.setWidth((width - CSSUtils.getAddedBorderWidth(editor.getElement())
						               - CSSUtils.getAddedPaddingWidth(editor.getElement()))+"px");
				editor.setHeight((height - CSSUtils.getAddedBorderHeight(editor.getElement())
						                 - CSSUtils.getAddedPaddingHeight(editor.getElement()))+"px");
				editor.setVisible(true);
				((Focusable)editor).setFocus(true);
			}
		});
	}
 		
 	public abstract SafeHtml asHtml(V value);
}
