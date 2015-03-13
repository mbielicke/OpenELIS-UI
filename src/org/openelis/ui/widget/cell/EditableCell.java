package org.openelis.ui.widget.cell;

import org.openelis.ui.widget.CSSUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public abstract class EditableCell<V> extends Cell<V> implements CellEditor<V> {
	
	protected boolean editing;
	
	public boolean isEditing() {
		return editing;
	}
	
	protected void setEditor(Widget editor, Element container) {
		sizeEditor(editor,container);
		getElement().removeAllChildren();
		setWidget(editor);
		container.removeAllChildren();
		container.appendChild(getElement());
		((Focusable)editor).setFocus(true);		
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
}
