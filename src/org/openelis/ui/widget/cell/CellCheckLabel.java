package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.CheckLabel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.CheckLabelValue;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellCheckLabel extends EditableCell<CheckLabelValue> {
	
	protected CheckLabel editor;
	protected CheckboxCSS css;
	
	public CellCheckLabel() {
		initEditor(new CheckLabel());
	}
	
	public CellCheckLabel(CheckLabel checkLabel) {
		initEditor(checkLabel);
	}
	
	public void initEditor(CheckLabel editor) {
		this.editor = editor;
        css = UIResources.INSTANCE.checkbox();
        css.ensureInjected();
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
	public void startEditing(Element element, CheckLabelValue value) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(false);
		editor.setValue(value);
		setEditor(editor,element);
		editing = true;
	}
	
	public void startEditing(Element element, CheckLabelValue value, NativeEvent event) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(false);
        editor.setValue(value);
        if(Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
        	ClickEvent.fireNativeEvent(event, editor.getCheck());
   	        fireEvent(new FinishedEditingEvent());
        } else {
        	setEditor(editor,element);
    		editing = true;
        }
	}

	@Override
	public CheckLabelValue finishEditing() throws ValidationErrorsList {
		editor.finishEditing();
		CheckLabelValue value = editor.getValue();
		editing = false;
		render(editor.getElement().getParentElement(),value);
		return value;
	}

	@Override
	public SafeHtml asHtml(CheckLabelValue value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
		  	    
	    builder.appendHtmlConstant(getCheckDiv(value.getChecked(),value.getLabel()).toString());
	   
	    return builder.toSafeHtml();
	}

	private Grid getCheckDiv(String value, String label) {
	    Grid grid = new Grid(1,2);
	    grid.setCellSpacing(0);
	    grid.setBorderWidth(0);
	    grid.setCellPadding(0);
	    grid.setHeight("16px");
	   
	    String style;
	    
	    if(value == null)
	        style = css.Unknown();
	    else if("Y".equals(value))
	        style = css.Checked();
	    else
	        style = css.Unchecked();
	    
	    if(editor.getLabelPosition() == CheckLabel.LabelPosition.LEFT) {
	        grid.getCellFormatter().setStyleName(0, 1, style);
	        grid.setText(0, 0, label);
	    }else{
	        grid.getCellFormatter().setStyleName(0, 0, style);
	        grid.setText(0, 1, label);
	    }
        
        return grid;
	}

	@Override
	public String asString(CheckLabelValue value) {
		StringBuilder sb = new StringBuilder();
		if (value != null) {
			sb.append(value.getChecked());
			sb.append(" : ");
			sb.append(value.getLabel());
		}
		return sb.toString();
	}

	@Override
	public void startEditing(Element element, QueryData qd) {

	}	
    
    public boolean ignoreKey(int keyCode) {
        switch(keyCode) {
            case KeyCodes.KEY_ENTER :
                return true;
            default :
                return false;
        }
    }
    
	@Override
	public void add(Widget w) {
		if (w instanceof CheckLabel) {
			initEditor((CheckLabel)w);
		}
	}

}
