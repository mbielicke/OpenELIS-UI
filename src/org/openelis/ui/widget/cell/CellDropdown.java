package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.widget.Dropdown;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class CellDropdown<V> extends Cell<V> implements CellEditor<V> {
	
	Dropdown<V> editor;
	boolean editing;
	

	public CellDropdown() {
		initEditor(new Dropdown<V>());
	}
	
	public void initEditor(Dropdown<V> dropdown) {
		this.editor = dropdown;
		editor.setEnabled(true);
		editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				finishEditing();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof Dropdown)
			initEditor((Dropdown<V>)w);
	}
	
	@Override
	public boolean isEditing() {
		return editing;
	}
	
	@Override
	public void startEditing(V value) {
		GWT.log("in Start Editing");
		startEditing(getElement().getParentElement(),value);		
	}
	
	public void startEditing(Element container, V value) {
		if(editing)
			return;
		editor.setValue(value);
		sizeEditor(editor,container);
		setEditor(editor);
		container.removeAllChildren();
		container.appendChild(getElement());
		editor.setFocus(true);		
		editing = true;
	}

	@Override
	public Object finishEditing() {
		V value = editor.getValue();
		remove(editor);
		render(value);
		editing = false;
		return value;
	}

	@Override
	public SafeHtml asHtml(V value) {
		if(editor == null)
			return new SafeHtmlBuilder().appendEscaped(DataBaseUtil.toString(value)).toSafeHtml();
        editor.setQueryMode(false);
        editor.setValue(value);
        return new SafeHtmlBuilder().appendEscaped(editor.getDisplay()).toSafeHtml();
	}

}
