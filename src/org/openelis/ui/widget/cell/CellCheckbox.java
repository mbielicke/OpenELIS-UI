package org.openelis.ui.widget.cell;

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;
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

public class CellCheckbox extends EditableCell<String> {
	
	protected CheckBox editor;
	protected CheckboxCSS css;
	protected String align;
	
	public CellCheckbox() {
		initEditor(new CheckBox());
	}
	
	public CellCheckbox(CheckBox checkbox) {
		initEditor(checkbox);
	}
	
	public void initEditor(CheckBox editor) {
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
	public void startEditing(Element element, String value) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(false);
		editor.setValue(value);
		setEditor(editor,element);
		editing = true;
	}
	
	public void startEditing(Element element, String value, NativeEvent event) {
		if (isEditing()) {
			return;
		}
		editor.setQueryMode(false);
        editor.setValue(value);
        if (Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
        	ClickEvent.fireNativeEvent(event, editor.getCheck());
   	        fireEvent(new FinishedEditingEvent());
        } else {
        	setEditor(editor,element);
    		editing = true;
        }
	}

	@Override
	public String finishEditing() throws ValidationErrorsList {
		editor.finishEditing();
		String value = editor.getValue();
		editing = false;
		render(editor.getElement().getParentElement(),value);
		return value;
	}

	@Override
	public SafeHtml asHtml(String value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	    String algn;
	    
	    if(align.equalsIgnoreCase("left"))
            algn = HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString();
        else if(align.equalsIgnoreCase("right"))
            algn = HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString();
        else
            algn = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	    
	    builder.appendHtmlConstant("<span align='"+algn+"'>");
	    builder.appendHtmlConstant(getCheckDiv((String)value).getElement().getString());
	    builder.appendHtmlConstant("</span>");
	    
	    return builder.toSafeHtml();
	}
	
	@Override
	public SafeHtml asHtml(QueryData qd) {
		return asHtml(qd.getQuery());
	}

	private AbsolutePanel getCheckDiv(String value) {
	    String style;
	    AbsolutePanel div;
	        
	    if(value == null)
            style = css.Unknown();
        else if("Y".equals(value))
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
	public String asString(String value) {
		return value;
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
                return true;
            default :
                return false;
        }
    }
    
	@Override
	public void add(Widget w) {
		if (w instanceof CheckBox) {
			initEditor((CheckBox)w);
		}
	}
	
	@Override
	public Widget getWidget() {
		return editor;
	}
	
}
