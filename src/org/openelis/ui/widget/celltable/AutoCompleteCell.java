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
package org.openelis.ui.widget.celltable;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TableAutoCompleteCSS;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table using an AutoComplete
 * 
 * @author tschmidt
 * 
 */
public class AutoCompleteCell implements CellRenderer,
                             		     CellEditor,IsWidget,HasWidgets.ForIsWidget {

    /**
     * Widget used to edit the cell
     */
    private AutoComplete editor;

    private boolean      query;
    
    private ColumnInt    column;
    
    protected TableCSS   css;
    
    public AutoCompleteCell() {

    }

    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public AutoCompleteCell(AutoComplete editor) {
    	setEditor(editor);
    }
    
    public void setEditor(AutoComplete editor) {
        this.editor = editor;
        TableAutoCompleteCSS css = UIResources.INSTANCE.tableAutoComplete();
        css.ensureInjected();
        editor.setEnabled(true);
        //editor.setCSS(css);
        editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				column.finishEditing();
			}
		});
    }

    public Object finishEditing() {
    	editor.finishEditing();
        if (query) {
            editor.validateQuery();
            return editor.getQuery();
        }

        return editor.getValue();
    }

    public ArrayList<Exception> validate(Object value) {
        if (!query) {
        	editor.setValue((AutoCompleteValue)value);
        	editor.hasExceptions();
            return editor.getValidateExceptions();
        }
        return null;
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
        editor.setQueryMode(false);
        if(value instanceof AutoCompleteValue) {
        	editor.setValue((AutoCompleteValue)value);
        	return editor.getDisplay();
        }else
        	return value.toString(); 
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
	public void startEditing(Object value, Container container, NativeEvent event) {
        query = false;
        editor.setQueryMode(false);
        if(value instanceof AutoCompleteValue)
        	editor.setValue((AutoCompleteValue)value);
        else
        	editor.setValue(null,value.toString());
        editor.setWidth(container.getWidth()+"px");
        container.setEditor(editor);
        editor.selectAll();
    }

    @SuppressWarnings("rawtypes")
	public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {
        query = true;
        editor.setQueryMode(true);
        editor.setQuery(qd);
        editor.setWidth(container.getWidth()+"px");
        container.setEditor(editor);
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
	public void add(Widget w) {
		// TODO Auto-generated method stub
		
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
		assert w instanceof AutoComplete;
		
		setEditor((AutoComplete)w);
	}

	@Override
	public boolean remove(IsWidget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}
}
