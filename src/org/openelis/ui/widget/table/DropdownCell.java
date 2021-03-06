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
import org.openelis.ui.resources.DropdownCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Dropdown;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table using an Dropdown<T> widget
 * 
 * @author tschmidt
 * 
 * @param <T>
 */
public class DropdownCell implements CellRenderer, CellEditor, IsWidget, HasWidgets {

    /**
     * Widget used to edit the cell
     */
    protected Dropdown      editor;

    protected boolean       query;
    
    private ColumnInt     column;

    public DropdownCell() {
    	//this(new Dropdown<String>());
    	
    }
    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public DropdownCell(Dropdown editor) {
        setEditor(editor);
    }
    
    public void setEditor(Dropdown editor) {
        DropdownCSS css = UIResources.INSTANCE.tableDropdown();
        this.editor = editor;
        editor.setEnabled(true);
        editor.setCSS(css);
        editor.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				column.finishEditing();
			}
		});
    }

    public Object finishEditing() {
    	editor.finishEditing();
        if (query) 
            return editor.getQuery();
        
        return editor.getValue();
    }

    public ArrayList<Exception> validate(Object value) {
    	ArrayList<Exception> exceptions = new ArrayList<Exception>();
    	
    	if(!query) {
    	    if(value != null && !editor.isValidKey(value))
    	        exceptions.add(new Exception("Invalid key set for dropdown"));
    	
    	    exceptions.addAll(editor.getHelper().validate(value));
    	}
    	
        return exceptions;
    }

    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        query = false;
        editor.setQueryMode(false);
       	table.setText(row, col, display(value));
    }

    public String display(Object value) {
        query = false;
        editor.setQueryMode(false);
    	if(value != null && editor.getHelper().isCorrectType(value) && editor.isValidKey(value)) {
   			editor.setValue(value);
        	return editor.getDisplay();
    	}else {
    		return DataBaseUtil.toString(value);
    	}
    }
    
    public SafeHtml bulkRender(Object value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        query = false;
        editor.setQueryMode(false);
        
        builder.appendEscaped(display(value));
        
        return builder.toSafeHtml();
    }

    /**
     * Sets the QueryData to the editor and sets the Query string into the cell
     * text
     */
    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
        query = true;
        editor.setQueryMode(true);
        editor.setQuery(qd);
        table.setText(row, col, editor.getDisplay());
    }

    /**
     * Returns the current widget set as this cells editor.
     */
    @SuppressWarnings("rawtypes")
	public void startEditing(Object value, final Container container, NativeEvent event) {
        query = false;
        editor.setQueryMode(false);
        editor.setValue(value);
        editor.setWidth(container.getWidth()+"px");
        editor.setHeight(container.getHeight()+"px");
        container.setEditor(editor);
    }

    @SuppressWarnings("rawtypes")
	public void startEditingQuery(QueryData qd, final Container container, NativeEvent event) {
        query = true;
        editor.setQueryMode(true);
        editor.setQuery(qd);
        container.setEditor(editor);
        editor.setWidth(container.getWidth()+"px");
        editor.setHeight(container.getHeight()+"px");
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
    
    public Widget getWidget() {
    	return editor;
    }
    
	@Override
	public void setColumn(ColumnInt col) {
		this.column = col;
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setWidth(int width) {
		editor.setWidth(width+"px");
	}
	@Override
	public void add(Widget w) {
		if(w instanceof Dropdown) {
		    setEditor((Dropdown)w);
		}
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
}
