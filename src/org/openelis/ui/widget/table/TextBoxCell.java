/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.widget.table;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TextCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table using a TextBox<T>
 * 
 * @author tschmidt
 * 
 * @param <T>
 */
public class TextBoxCell implements CellRenderer, CellEditor, IsWidget, HasWidgets.ForIsWidget {

    /**
     * Editor used by this cell
     */
    protected TextBox     editor;

    protected boolean     query;

    protected ColumnInt   column;
    
    protected TextCSS     textCss;
    
    public TextBoxCell() {
    	
    }
    /**
     * Constructor that takes the editor to be used as a param
     * 
     * @param editor
     */
    public TextBoxCell(final TextBox<?> editor) {
    	setEditor(editor);
    }
    
    public void setEditor(TextBox<?> editor) {
        this.editor = editor;
        
    	textCss = UIResources.INSTANCE.tableText();
    	textCss.ensureInjected();
       
        editor.setEnabled(true);
        editor.setCSS(textCss);
        editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				column.finishEditing();
			}
		});
    }

    public String display(Object value) {
        editor.setQueryMode(false);
        if (editor.getHelper().isCorrectType(value)) {
        	editor.setValue(value);
        	return editor.getText();
        } else {
        	return DataBaseUtil.toString(value);
        }
    }
    
    public SafeHtml bulkRender(Object value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        

        builder.appendEscaped(display(value));
        
        return builder.toSafeHtml();
    }

    /**
     * Returns the current widget set as this cells editor.
     */
    @SuppressWarnings("rawtypes")
	public void startEditing(Object value, Container container, NativeEvent event) {
    	editor.setQueryMode(false);
    	query = false;
    	if(!editor.getHelper().isCorrectType(value))
    		editor.setText(DataBaseUtil.toString(value));
    	else 
    		editor.setValue(value);
        editor.setWidth(container.getWidth()+"px");
        editor.setHeight(container.getHeight()+"px");
        container.setEditor(editor);
        editor.selectAll();
    }

    public void render(HTMLTable table, int row, int col, Object value) {
   		table.setText(row, col, display(value));
    }

    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
        editor.setQueryMode(true);
        editor.setQuery(qd);
        table.setText(row, col, editor.getText());
    }

    public ArrayList<Exception> validate(Object value) {
        ArrayList<Exception> exceptions;
        
        exceptions = new ArrayList<Exception>();
        if(query) {
            try {
                if(value != null)
                    editor.getHelper().validateQuery(((QueryData)value).getQuery());
            }catch(Exception e) {
                exceptions.add(e);
            }
        }else {
            exceptions.addAll(editor.getHelper().validate(value));
        }
        
        return exceptions;
    }

    public Object finishEditing() {
    	editor.finishEditing();
        if (query)
            return editor.getQuery();
        
        try {
        	return editor.getHelper().getValue(editor.getText());
        }catch(Exception e){
       		return editor.getText();
        }
        
    }

    @SuppressWarnings("rawtypes")
	public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {
        query = true;
        editor.setQueryMode(true);
        editor.setQuery(qd);
        editor.setWidth(container.getWidth()+"px");
        editor.setHeight(container.getHeight()+"px");
        container.setEditor(editor);
    }

    public boolean ignoreKey(int keyCode) {
        return false;
    }
    
    public Widget getWidget() {
    	return editor;
    }
    
	@Override
	public void setColumn(ColumnInt col) {
		this.column = col;
	}
	@Override
	public void add(Widget w) {
		assert w instanceof TextBox;
		
		setEditor((TextBox)w);
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void add(IsWidget w) {
		assert w instanceof TextBox;
		
		setEditor((TextBox)w);
	}
	@Override
	public boolean remove(IsWidget w) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return new Label("TextBox Cell");
	}

}
