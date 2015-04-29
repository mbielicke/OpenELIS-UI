package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Dropdown;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellDropdown<V> extends EditableCell<V> {
	
	protected Dropdown<V> editor;

	public CellDropdown() {
		initEditor(new Dropdown<V>());
	}
	
	public CellDropdown(Dropdown<V> editor) {
		initEditor(editor);
	}
	
	public void initEditor(Dropdown<V> dropdown) {
		this.editor = dropdown;
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
	
	public void startEditing(Element container, V value) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(false);
		editor.setValue(value);
		setEditor(editor,container);
		editing = true;
	}

	@Override
	public V finishEditing() throws ValidationErrorsList {
		V value = null;
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
	public SafeHtml asHtml(V value) {
		return new SafeHtmlBuilder().appendEscaped(asString(value)).toSafeHtml();
	}

	@Override
	public String asString(V value) {
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
	
    public ArrayList<Exception> validate(Object value) {
    	ArrayList<Exception> exceptions = new ArrayList<Exception>();
    	
    	if(value != null && !editor.isValidKey((V)value))
    	    exceptions.add(new Exception("Invalid key set for dropdown"));
    	
    	exceptions.addAll(editor.getHelper().validate(value));
    	
        return exceptions;
    }
    
    public QueryData getQuery() {
    	return (QueryData)editor.getQuery();
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
		if(w instanceof Dropdown)
			initEditor((Dropdown<V>)w);
	}
	
	@Override
	public Widget getWidget() {
		return editor;
	}

}
