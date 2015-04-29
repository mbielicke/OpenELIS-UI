package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.CSSUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public abstract class EditableCell<V> extends Cell<V> implements CellEditor<V>, CellQuery {
	
	protected boolean editing;
	
	public boolean isEditing() {
		return editing;
	}
	
	protected void setEditor(Widget editor, Element container) {
		container.removeAllChildren();
		sizeEditor(editor,container);
		DOM.appendChild(container, editor.getElement());
		editor.sinkEvents(DOM.getEventsSunk(editor.getElement()));
	}
	
	@Override
	public void startEditing(V value) {
		startEditing(getRenderElement(),value);		
	}
	
	public void startEditing(QueryData qd) {
		startEditing(getRenderElement(),qd);
	}
	
	public void startEditing(Element container, V value, NativeEvent event) {
		startEditing(container,value);
	}
	
	public void startEditing(Element container, QueryData qd, NativeEvent event) {
		startEditing(container,qd);
	}
	
	protected void sizeEditor(final Widget editor, final Element element) {
		editor.setVisible(false);
		final double width = element.getClientWidth() - CSSUtils.getAddedPaddingWidth(element);
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
	
	public void addFinishedEditingHandler(FinishedEditingEvent.Handler handler) {
		addHandler(handler, FinishedEditingEvent.getType());
	}
	
	@Override
	public SafeHtml asHtml(QueryData qd) {
		return new SafeHtmlBuilder().appendEscaped(asString(qd)).toSafeHtml();
	}

	@Override
	public String asString(QueryData qd) {
		return qd != null ? DataBaseUtil.toString(qd.getQuery()) : "";
	}
	
	public <T> ArrayList<Exception> validate(T value) {
		return null;
	}
	
	public QueryData getQuery() {
		return null;
	}
}
