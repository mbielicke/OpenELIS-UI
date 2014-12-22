/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Check;
import org.openelis.ui.widget.CheckLabel;
import org.openelis.ui.widget.table.ColumnInt;
import org.openelis.ui.widget.table.Container;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used by Table to edit and render columns that use CheckBox 
 * @author tschmidt
 *
 */
public class CheckLabelCell extends Cell implements CellEditor {

    /**
     * Widget used to edit the cell
     */
    private CheckLabel editor;
    private boolean    query;
    private ColumnInt  column;
    
    
    protected CheckboxCSS css;
    
    public CheckLabelCell() {
    	setEditor(new CheckLabel());
    }
    
    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public CheckLabelCell(CheckLabel editor) {
    	setEditor(editor);
    }
    
    public void setEditor(CheckLabel editor) {
        this.editor = editor;
        
        css = UIResources.INSTANCE.checkbox();
        css.ensureInjected();
        
        editor.setEnabled(true);
        //editor.addBlurHandler(new BlurHandler() {
		//	public void onBlur(BlurEvent event) {
		//		column.finishEditing();
	//		}
	//	});
    }
    
    public Object finishEditing() {
        if(query){
            return editor.getQuery();
        }
        
        return editor.getValue();
    }

    public ArrayList<Exception> validate(Object value) {
        if (query){
            return editor.getValidateExceptions();
        }        
        return editor.getValidateExceptions();
    }
    
    /**
     * Returns the current widget set as this cells editor.
     */
	public void startEditing(Object value, Container container, NativeEvent event) {
        query = false;
        editor.setQueryMode(false);
        if(value != null)
            editor.setValue((CheckLabelValue)value);
        else
            editor.setValue(null);
        
        if(Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
        	ClickEvent.fireNativeEvent(event, editor.getCheck());
   	        column.finishEditing();
        }
        container.setEditor(editor);
        editor.setFocus(true);
    }
    
	public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {        
        query = true;
        editor.setQueryMode(true);
        editor.setQuery(qd);
       
        if(Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
            ClickEvent.fireNativeEvent(event, editor.getCheck());
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
                @Override
                public void execute() {
                    column.finishEditing();
                }
            });
        } else {
            container.setEditor(editor);
            container.getElement().getStyle().setProperty("align", "center"); 
        }
    }
    
    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        
        query = false;
        editor.setQueryMode(false);
        String checkValue = null;
        
        if(value != null)
            checkValue = ((CheckLabelValue)value).checked;
                
        if(editor.getMode() == Check.Mode.TWO_STATE && checkValue == null)
        	checkValue  = "N";
        
        render(checkValue,(value != null ? ((CheckLabelValue)value).label : ""),table,row,col);
    }
    
    public String display(Object value) {
        return null;
    }

    /**
     * Sets the QueryData to the editor and sets the Query string into the cell
     * text
     */
    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
        String value;
        
        query = true;
        editor.setQueryMode(true);
        
        if(qd == null)
        	value = null;
        else 
        	value = qd.getQuery();
        
        render(value,"",table,row,col);
    }

    public boolean ignoreKey(int keyCode) {
        switch(keyCode) {
            case KeyCodes.KEY_ENTER :
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
	
	private void render(String value, String label, HTMLTable table, int row, int col) {

		table.setWidget(row, col, getCheckDiv(value,label));
		   
	}
	
	public SafeHtml bulkRender(Object value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	  
	    CheckLabelValue val = (CheckLabelValue)value;
	    
	    builder.appendHtmlConstant("<td>");
	    builder.appendHtmlConstant(getCheckDiv(((CheckLabelValue)value).getChecked(),((CheckLabelValue)value).label).toString());
	    builder.appendHtmlConstant("</td>");
	    
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
	public void add(Widget w) {
	    assert w instanceof CheckLabel;	        
	    setEditor((CheckLabel)w);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	
	public void startEditing(Object data) {
		// TODO Auto-generated method stub
		
	}
  

}
