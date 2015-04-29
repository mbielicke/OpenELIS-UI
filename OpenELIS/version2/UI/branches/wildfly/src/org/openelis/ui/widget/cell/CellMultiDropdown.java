package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.MultiDropdown;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellMultiDropdown<V> extends EditableCell<ArrayList<V>> {
	
	public MultiDropdown<V> editor;
	
	public CellMultiDropdown() {
		initEditor(editor);
	}
	
	public CellMultiDropdown(MultiDropdown<V> multiDropdown) {
		initEditor(editor);
	}
	
	public void initEditor(MultiDropdown<V> editor) {
		this.editor = editor;
		editor.setEnabled(true);
		editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				fireEvent(new FinishedEditingEvent());
			}
		});
		editor.setVisible(false);
		RootPanel.get().add(editor);
	}

	@Override
	public void startEditing(Element element, ArrayList<V> value) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(false);
		editor.setValue(value);
		setEditor(editor,element);
		editing = true;
	}

	@Override
	public ArrayList<V> finishEditing() throws ValidationErrorsList {
		ArrayList<V> value = null;
		editor.finishEditing();
		editing = false;
		if (editor.hasExceptions()) {
			throw new ValidationErrorsList(editor.getValidateExceptions());
		}
		if (!editor.isQueryMode()) {
			value = editor.getValue();
			render(editor.getElement().getParentElement(),value);
		}
		return value;
	}

	@Override
	public SafeHtml asHtml(ArrayList<V> value) {
		return new SafeHtmlBuilder().appendEscaped(asString(value)).toSafeHtml();
	}

	@Override
	public String asString(ArrayList<V> value) {
		editor.setValue(value);
		return editor.getDisplay();
	}

	@Override
	public void startEditing(Element element, QueryData qd) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(true);
		editor.setQuery(qd);
		setEditor(editor,element);
		editing = true;
	}

    public boolean ignoreKey(int keyCode) {
        switch(keyCode) {
            case KeyCodes.KEY_ENTER :
            case KeyCodes.KEY_DOWN :
            case KeyCodes.KEY_UP :
                return true;
            default :
                return false;
        }
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof MultiDropdown)
			initEditor((MultiDropdown<V>)w);
	}
	
	@Override
	public Widget getWidget() {
		return editor;
	}
}
