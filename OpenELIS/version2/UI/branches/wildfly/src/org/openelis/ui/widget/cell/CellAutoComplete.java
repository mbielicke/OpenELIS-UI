package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellAutoComplete extends EditableCell<AutoCompleteValue> {
	
	protected AutoComplete editor;

	public CellAutoComplete() {
		initEditor(new AutoComplete());
	}
	
	public CellAutoComplete(AutoComplete auto) {
		initEditor(auto);
	}
	
	public void initEditor(AutoComplete auto) {
		this.editor = auto;
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
	


	public void startEditing(Element container, AutoCompleteValue value) {
		if (editing) {
			return;
		}
		editor.setQueryMode(false);
		editor.setValue(value);
		setEditor(editor,container);
		editing = true;
	}

	@Override
	public AutoCompleteValue finishEditing() throws ValidationErrorsList {
		AutoCompleteValue value = null;
		editor.finishEditing();
		editing = false;
		if (!editor.isQueryMode()) {
			value = editor.getValue();
		}
		render(editor.getElement().getParentElement(),value);
		return value;
	}

	@Override
	public SafeHtml asHtml(AutoCompleteValue value) {
		return new SafeHtmlBuilder().appendEscaped(asString(value)).toSafeHtml();
	}

	@Override
	public String asString(AutoCompleteValue value) {
		return DataBaseUtil.toString(value.getDisplay());
	}

	@Override
	public void startEditing(Element element, QueryData qd) {
		if (editing) {
			return;
		}
		editor.setQueryMode(true);
		editor.setQuery(qd);
		setEditor(editor,element);
		editing = true;
	}
	
    public ArrayList<Exception> validate(Object value) {
        editor.setValue((AutoCompleteValue)value);
        editor.hasExceptions();
        return editor.getValidateExceptions();
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
    
    public QueryData getQuery() {
    	return (QueryData)editor.getQuery();
    }
    
	@Override
	public void add(Widget w) {
		if(w instanceof AutoComplete)
			initEditor((AutoComplete)w);
	}

	@Override
	public Widget getWidget() {
		return editor;
	}
}
