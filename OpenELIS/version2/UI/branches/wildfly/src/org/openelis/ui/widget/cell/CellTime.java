package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.TextCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellTime extends EditableCell<Double> {
	
	protected TextBox<String> editor;
	
	public CellTime() {
		initEditor(new TextBox<String>());
	}
	
	public CellTime(TextBox<String> editor) {
		initEditor(editor);
	}
	
	public void initEditor(TextBox<String> editor) {
		this.editor = editor;
		editor.setEnabled(true);
		editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				//fireEvent(new FinishedEditingEvent());
			}
		});
		editor.setVisible(false);
		RootPanel.get().add(editor);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof TextBox)
			initEditor((TextBox<String>)w);
	}

	@Override
	public void startEditing(Element element, Double value) {
		if (isEditing()) {
			return;
		}
		editor.setValue(getTime(value));
		setEditor(editor,element);
		editing = true;
	}

	@Override
	public Double finishEditing() {
		editor.finishEditing();
		Double value = getHours(editor.getValue());
		editing = false;
		render(editor.getElement().getParentElement(),value);
		return value;
		
	}

	@Override
	public SafeHtml asHtml(Double value) {
       	return new SafeHtmlBuilder().appendEscaped(getTime(value)).toSafeHtml();
	}
	
    protected Double getHours(String time) {
    	double hours = 0.0 ,mins = 0.0;
    	String tm[];
    	
    	if(time == null || "".equals(time))
    		return null;
    	
        tm = time.split(":");
        try {
        	hours = Double.parseDouble(tm[0]);
        }catch(Exception e) {
        	hours = 0.0;
        }
        
        if(tm.length > 1) {
        	try {
        		mins = Double.parseDouble(tm[1]) / 60.0;
        	}catch(Exception e) {
        		mins = 0.0;
        	}
        }
        
        return new Double(hours += mins);
    }
    
    protected String getTime(Double hours) {
    	int h,m;
    	
        if (hours != null && hours.doubleValue() > 0.0) {
            h = (int)Math.floor(hours);
            m = 0;
            if(h == 0)
                m = (int)Math.round(hours  * 60);
            else
                m = (int)Math.round((hours % h) * 60);
            return h + ":" + m;
        } 
  
        return "";      
    }
    
    protected boolean isValid(String input) {
    	String[] time = null;
    	
    	if(input == null || "".equals(input))
    		return true;
    	
    	if(input.contains(":")) {	
    		time = input.split(":");
    		if(time.length > 2)
    			return false;
    	}
    	
    	if(time == null) {
    		try {
    			Integer.parseInt(input);
    		}catch(Exception e) {
    			return false;
    		}
    	}else {
    		try {
    			Integer.parseInt(time[0]);
    			Integer.parseInt(time[1]);
    		}catch(Exception e) {
    			return false;
    		}
    	}
    	
    	return true;
    }

	@Override
	public String asString(Double value) {
		return getTime(value);
	}

	@Override
	public void startEditing(Element element, QueryData qd) {
		
	}
	
	public ArrayList<Exception> validate(Object validate) {
		ArrayList<Exception> exceptions = new ArrayList<Exception>();
		
		if(validate != null && !(validate instanceof Double))
			exceptions.add(new Exception(Messages.get().exc_invalidNumeric()));
		
		return exceptions;		
	}
	
	@Override
	public Widget getWidget() {
		return editor;
	}

}
