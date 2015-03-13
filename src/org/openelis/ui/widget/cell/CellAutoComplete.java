package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class CellAutoComplete extends Cell<AutoCompleteValue> implements CellEditor<AutoCompleteValue> {
	
	AutoComplete editor;
	boolean editing;
	

	public CellAutoComplete() {
		initEditor(new AutoComplete());
	}
	
	public void initEditor(AutoComplete auto) {
		this.editor = auto;
		editor.setEnabled(true);
		editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				finishEditing();
			}
		});
	}
	
	@Override
	public void add(Widget w) {
		if(w instanceof AutoComplete)
			initEditor((AutoComplete)w);
	}
	
	@Override
	public boolean isEditing() {
		return editing;
	}
	
	@Override
	public void startEditing(AutoCompleteValue value) {
		GWT.log("in Start Editing");
		startEditing(getElement().getParentElement(),value);		
	}
	
	public void startEditing(Element container, AutoCompleteValue value) {
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
		AutoCompleteValue value = editor.getValue();
		remove(editor);
		render(value);
		editing = false;
		return value;
	}

	@Override
	public SafeHtml asHtml(AutoCompleteValue value) {
		if(editor == null)
			return new SafeHtmlBuilder().appendEscaped(DataBaseUtil.toString(value)).toSafeHtml();
        editor.setQueryMode(false);
        editor.setValue(value);
       	return new SafeHtmlBuilder().appendEscaped(editor.getDisplay()).toSafeHtml();
	}

}
