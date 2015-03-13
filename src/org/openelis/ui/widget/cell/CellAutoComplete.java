package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class CellAutoComplete extends EditableCell<AutoCompleteValue> {
	
	AutoComplete editor;

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
	public void startEditing(AutoCompleteValue value) {
		startEditing(getElement().getParentElement(),value);		
	}
	
	public void startEditing(Element container, AutoCompleteValue value) {
		if(editing)
			return;
		editor.setValue(value);
		setEditor(editor,container);
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
