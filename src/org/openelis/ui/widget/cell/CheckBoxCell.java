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
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.table.ColumnInt;
import org.openelis.ui.widget.table.Container;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used by Table to edit and render columns that use CheckBox 
 * @author tschmidt
 *
 */
public class CheckBoxCell extends Cell implements CellEditor {

    /**
     * Widget used to edit the cell
     */
    private CheckBox  editor;
    private boolean   query;
    private ColumnInt column;
    private String    align = "center";
    
    protected CheckboxCSS css;
    
    public CheckBoxCell() {
    	setEditor(new CheckBox());
    }
    
    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public CheckBoxCell(CheckBox editor) {
    	setEditor(editor);
    }
    
    public void setEditor(CheckBox editor) {
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
        editor.setValue((String)value);
        
        if(Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
        	ClickEvent.fireNativeEvent(event, editor.getCheck());
   	        column.finishEditing();
        }

        container.setEditor(editor);
        container.getElement().getStyle().setProperty("align", align); 
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
            container.getElement().getStyle().setProperty("align", align); 
        }
    }
    
    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        
        query = false;
        editor.setQueryMode(false);
        
        if(editor.getMode() == Check.Mode.TWO_STATE && value == null)
        	value = "N";
        
        render((String)value,table,row,col);
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
        
        render(value,table,row,col);
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
	
	private void render(String value, HTMLTable table, int row, int col) {

		
		table.setWidget(row, col, getCheckDiv(value));
		
        if(align.equalsIgnoreCase("left"))
            table.getCellFormatter().setHorizontalAlignment(row, col,HasHorizontalAlignment.ALIGN_LEFT);
        else if(align.equalsIgnoreCase("right"))
            table.getCellFormatter().setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_RIGHT);
        else
            table.getCellFormatter().setHorizontalAlignment(row, col,HasHorizontalAlignment.ALIGN_CENTER);
            
	}
	
	public SafeHtml bulkRender(Object value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	    String algn;
	    
	    if(align.equalsIgnoreCase("left"))
            algn = HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString();
        else if(align.equalsIgnoreCase("right"))
            algn = HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString();
        else
            algn = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	    
	    builder.appendHtmlConstant("<td align='"+algn+"'>");
	    builder.appendHtmlConstant(getCheckDiv((String)value).getElement().getString());
	    builder.appendHtmlConstant("</td>");
	    
	    return builder.toSafeHtml();
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
	public void add(IsWidget w) {
		assert w instanceof CheckBox;
		
		setEditor((CheckBox)w);
	}

	@Override
	public Widget asWidget() {
		return editor;
	}

}
