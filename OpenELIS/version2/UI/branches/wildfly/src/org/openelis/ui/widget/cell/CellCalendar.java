package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellCalendar extends EditableCell<Datetime> {
	
	protected Calendar editor;
	
	public CellCalendar() {
		initEditor(new Calendar());
	}
	
	public CellCalendar(Calendar calendar) {
		initEditor(calendar);
	}
	
	public void initEditor(Calendar editor) {
		this.editor = editor;
		editor.setEnabled(true);
		editor.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				fireEvent(new FinishedEditingEvent());
			}
		});
		editor.setVisible(false);
		RootPanel.get().add(editor);
	}

	@Override
	public void startEditing(Element container, Datetime value) {
		if (isEditing()) {
			return;
		}
		boolean invalidInput = value == null && container.getInnerText() != null;
		if (invalidInput) {
			editor.setText(container.getInnerText());
		} else {
			editor.setValue(value);
		}
		setEditor(editor,container);
		editing = true;
	}

	@Override
	public Datetime finishEditing() throws ValidationErrorsList {
		Datetime value = null;
		editor.finishEditing();
		editing = false;
		if (!isValid()) {
			renderUserInput();
			throw new ValidationErrorsList(editor.getValidateExceptions());
		}
		if (!editor.isQueryMode()) {
			value = editor.getValue();
			render(getEditingElement(),value);
		} else {
			renderUserInput();
		}
		return value;
	}
	
	private Element getEditingElement() {
		return editor.getElement().getParentElement();
	}
	
	private void renderUserInput() {
		String text = editor.getText();
		Element element = getEditingElement();
		element.removeAllChildren();
		element.setInnerText(text);
	}

	@Override
	public SafeHtml asHtml(Datetime value) {
		return new SafeHtmlBuilder().appendEscaped(asString(value)).toSafeHtml();
	}

	@Override
	public String asString(Datetime value) {
		return editor.getHelper().format(value);
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
       	return editor.getHelper().validate(value);
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
    
    protected boolean isValid() {
    	return editor.getValidateExceptions() == null || editor.getValidateExceptions().isEmpty();
    }
    
    public QueryData getQuery() {
    	return (QueryData)editor.getQuery();
    }

	@Override
	public void add(Widget w) {
		if (w instanceof Calendar) {
			initEditor((Calendar)w);
		}
	}
	
	@Override
	public Widget getWidget() {
		return editor;
	}
}
