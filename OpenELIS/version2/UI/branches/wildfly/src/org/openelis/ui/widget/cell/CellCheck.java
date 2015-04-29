package org.openelis.ui.widget.cell;

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Check;
import org.openelis.ui.widget.CheckBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CellCheck extends EditableCell<Boolean> {
	
	protected Check editor;
	protected CheckboxCSS css;
	protected String align;
	
	public CellCheck() {
		initEditor(new Check());
	}
	
	public CellCheck(Check checkbox) {
		initEditor(checkbox);
	}
	
	public void initEditor(Check editor) {
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
	public void startEditing(Element element, Boolean value) {
		if (isEditing()) {
			return;
		}
		if (value) {
			editor.check();
		} else {
			editor.uncheck();
		}
		setEditor(editor,element);
		editing = true;
	}
	
	public void startEditing(Element element, Boolean value, NativeEvent event) {
		if (isEditing()) {
			return;
		}
		if (value) {
			editor.check();
		} else {
			editor.uncheck();
		}
        if (Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
        	ClickEvent.fireNativeEvent(event, editor);
   	        fireEvent(new FinishedEditingEvent());
        } else {
        	startEditing(element,value);
        }
	}
	
	@Override
	public Boolean finishEditing() throws ValidationErrorsList {
		editing = false;
		Boolean value =  editor.isUnknown() ? null : new Boolean(editor.isChecked());
		render(editor.getElement().getParentElement(),value);
		return value;
	}



	@Override
	public SafeHtml asHtml(Boolean value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	    String algn;
	    
	    if(align.equalsIgnoreCase("left"))
            algn = HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString();
        else if(align.equalsIgnoreCase("right"))
            algn = HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString();
        else
            algn = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	    
	    builder.appendHtmlConstant("<span align='"+algn+"'>");
	    builder.appendHtmlConstant(getCheckDiv(value).getElement().getString());
	    builder.appendHtmlConstant("</span>");
	    
	    return builder.toSafeHtml();
	}
	
	@Override
	public SafeHtml asHtml(QueryData qd) {
		return asHtml("Y".equals(qd.getQuery()));
	}

	private AbsolutePanel getCheckDiv(Boolean value) {
	    String style;
	    AbsolutePanel div;
	        
	    if(value == null)
            style = css.Unknown();
        else if (value)
            style = css.Checked();
        else
            style = css.Unchecked();
            
        div = new AbsolutePanel();
        div.setStyleName(style);
        
        return div;
	}
	
	public void setCss(CheckboxCSS css) {
		this.css = css;
		css.ensureInjected();
	}
	
	public void setAlign(String align) {
	    this.align = align;
	}

	@Override
	public String asString(Boolean value) {
		if (value == null) {
			return "";
		} else if (value) {
			return "Y";
		} else { 
			return "N";
		}
	}

	@Override
	public void startEditing(Element element, QueryData qd) {
		if (isEditing()) {
			return;
		}
		if ("Y".equals(qd.getQuery())) {
			editor.check();
		} else {
			editor.uncheck();
		}
		setEditor(editor,element);
		editing = true;
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
    public Widget getWidget() {
    	return editor;
    }
}
