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
package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TableCalendarCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.ColumnInt;
import org.openelis.ui.widget.table.Container;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table using a Calendar
 * 
 * @author tschmidt
 * 
 * @param <T>
 */
public class CalendarCell extends Cell implements CellEditor {
    /**
     * Editor used by this cell
     */
    private Calendar  editor;
    private boolean   query;
    private ColumnInt column;

    
    public CalendarCell() {
    	
    }
    
    /**
     * Constructor that takes the editor to be used as a param
     * 
     * @param editor
     */
    public CalendarCell(Calendar editor) {
    	setEditor(editor);
    }
    
    public void setEditor(Calendar editor) {
        TableCalendarCSS css = UIResources.INSTANCE.tableCalendar();
        this.editor = editor;
        editor.setEnabled(true);
        editor.setCSS(css);
        editor.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				column.finishEditing();
			}
		});
    }

    /**
     * Method to return the editor set for this cell
     */
	public void startEditing(Object value, Container container, NativeEvent event) {
        if(value instanceof Datetime)
        	editor.setValue((Datetime)value);
        else
        	editor.setText(DataBaseUtil.toString(value));
        editor.setWidth(container.getWidth()+"px");
        editor.setHeight(container.getHeight()+"px");
        container.setEditor(editor);
        editor.selectAll();
    }

	public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {
        query = true;
        editor.setQueryMode(true);
        editor.setQuery(qd);
        editor.setWidth(container.getWidth()+"px");
        editor.setHeight(container.getHeight()+"px");
        container.setEditor(editor);
    }

    public Object finishEditing() {
    	editor.finishEditing();
        if (query) 
            return editor.getQuery();
        
        if(!editor.hasExceptions())
        	return editor.getValue();
        else
        	return editor.getText();
    }

    public ArrayList<Exception> validate(Object value) {
        if (!query) 
        	return editor.getHelper().validate(value);
        else {
        	editor.setQuery((QueryData)value);
        	return editor.getValidateExceptions();
        }
    }

    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        table.setText(row, col, display(value));
    }

    public String display(Object value) {
        query = false;
        editor.setQueryMode(false);
        if(value instanceof Datetime)
        	return editor.getHelper().format((Datetime)value);
        else
        	return DataBaseUtil.toString(value);
    }
    
    public SafeHtml bulkRender(Object value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(display(value));
        builder.appendHtmlConstant("</td>");
        
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
        table.setText(row, col, editor.getText());
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
	public void add(Widget w) {
		assert w instanceof Calendar;
		
		setEditor((Calendar)w);
		
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	public void startEditing(Object data) {
		// TODO Auto-generated method stub
		
	}

}
