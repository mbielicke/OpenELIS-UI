package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class CellTextbox<V> extends Cell<V> implements CellEditor<V> {
	
	TextBox<V> editor;
	boolean editing;

	public CellTextbox() {
		initEditor(new TextBox<V>());
	}
	
	public void initEditor(TextBox<V> editor) {
		this.editor = editor;
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
		if(w instanceof TextBox)
			initEditor((TextBox<V>)w);
	}
	
	@Override
	public boolean isEditing() {
		return editing;
	}
	
	@Override
	public void startEditing(V value) {
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
        if(editor.getHelper().isCorrectType(value))
        	return new SafeHtmlBuilder().appendEscaped(editor.getHelper().format(value)).toSafeHtml();
        else
        	return new SafeHtmlBuilder().appendEscaped(DataBaseUtil.toString(value)).toSafeHtml();
	}

}
